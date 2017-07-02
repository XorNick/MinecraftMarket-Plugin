package com.minecraftmarket.minecraftmarket.nukkit.tasks;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.scheduler.NukkitRunnable;
import com.minecraftmarket.minecraftmarket.nukkit.MCMarket;
import com.minecraftmarket.minecraftmarket.nukkit.api.MCMApi;

public class PurchasesTask extends AsyncTask {
    private final MCMarket plugin;

    public PurchasesTask(MCMarket plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onRun() {
        updatePurchases();
    }

    public void updatePurchases() {
        if (plugin.isAuthenticated()) {
            for (MCMApi.ExpiredPurchase expiredPurchase : plugin.getApi().getExpiredPurchases()) {
                for (MCMApi.Command command : expiredPurchase.getCommands()) {
                    runCommand(expiredPurchase.getUser(), command);
                }
            }

            for (MCMApi.PendingPurchase pendingPurchase : plugin.getApi().getPendingPurchases()) {
                for (MCMApi.Command command : pendingPurchase.getCommands()) {
                    runCommand(pendingPurchase.getUser(), command);
                }
            }
        }
    }

    private void runCommand(String user, MCMApi.Command command) {
        Player player = Server.getInstance().getPlayerExact(user);
        boolean shouldExecute = true;
        if (command.isOnline() && (player == null || !player.isOnline())) {
            shouldExecute = false;
        }
        if (shouldExecute) {
            plugin.getServer().getScheduler().scheduleDelayedTask(plugin, new AsyncTask() {
                @Override
                public void onRun() {
                    boolean canContinue = true;
                    if (command.getSlots() > 0 && player != null) {
                        if (getEmptySlots(player.getInventory()) < command.getSlots()) {
                            canContinue = false;
                        }
                    }
                    if (canContinue) {
                        plugin.getServer().getScheduler().scheduleTask(plugin, () -> plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command.getCommand()));
                        if (command.isRepeat()) {
                            long period = command.getPeriod() > 0 ? 20 * 60 * 60 * command.getPeriod() : 1;
                            new NukkitRunnable() {
                                int executed = 0;

                                @Override
                                public void run() {
                                    plugin.getServer().getScheduler().scheduleTask(plugin, () -> plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command.getCommand()));
                                    executed++;

                                    if (executed >= command.getCycles()) {
                                        cancel();
                                    }
                                }
                            }.runTaskTimerAsynchronously(plugin, (int) period, (int) period);
                        }
                        plugin.getApi().setExecuted(command.getId(), true);
                    }
                }
            }, command.getDelay() > 0 ? (int) (20 * command.getDelay()) : 1, true);
        }
    }

    private int getEmptySlots(PlayerInventory inventory) {
        int amount = 0;
        for (Item item : inventory.getContents().values()) {
            if (item == null || item.getId() < 1) {
                amount++;
            }
        }
        return amount;
    }
}