package com.minecraftmarket.minecraftmarket.nukkit.tasks;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.scheduler.NukkitRunnable;
import com.minecraftmarket.minecraftmarket.common.api.MCMarketApi;
import com.minecraftmarket.minecraftmarket.nukkit.MCMarket;

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
            for (MCMarketApi.ExpiredPurchase expiredPurchase : plugin.getApi().getExpiredPurchases()) {
                for (MCMarketApi.Command command : expiredPurchase.getCommands()) {
                    runCommand(expiredPurchase.getUser(), command);
                }
            }

            for (MCMarketApi.PendingPurchase pendingPurchase : plugin.getApi().getPendingPurchases()) {
                for (MCMarketApi.Command command : pendingPurchase.getCommands()) {
                    runCommand(pendingPurchase.getUser(), command);
                }
            }
        }
    }

    private void runCommand(String user, MCMarketApi.Command command) {
        Player player = Server.getInstance().getPlayerExact(user);
        boolean shouldExecute = true;
        if (command.isOnline() && (player == null || !player.isOnline())) {
            shouldExecute = false;
        }
        if (shouldExecute) {
            if (command.getSlots() > 0 && player != null) {
                if (getEmptySlots(player.getInventory()) < command.getSlots()) {
                    shouldExecute = false;
                }
            }
            if (shouldExecute) {
                plugin.getServer().getScheduler().scheduleDelayedTask(plugin, new AsyncTask() {
                    @Override
                    public void onRun() {
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
                    }
                }, command.getDelay() > 0 ? (int) (20 * command.getDelay()) : 1, true);
                plugin.getApi().setExecuted(command.getId(), true);
            }
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