package com.minecraftmarket.minecraftmarket.nukkit.tasks;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.scheduler.NukkitRunnable;
import com.minecraftmarket.minecraftmarket.common.api.MCMarketApi;
import com.minecraftmarket.minecraftmarket.nukkit.MCMarket;

import java.util.Arrays;
import java.util.List;

public class PurchasesTask extends AsyncTask {
    private final MCMarket plugin;
    private final List<MCMarketApi.CommandType> commandTypes = Arrays.asList(
            MCMarketApi.CommandType.EXPIRY,
            MCMarketApi.CommandType.CHARGEBACK,
            MCMarketApi.CommandType.REFUND,
            MCMarketApi.CommandType.INITIAL,
            MCMarketApi.CommandType.RENEWAL
    );

    public PurchasesTask(MCMarket plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onRun() {
        updatePurchases();
    }

    public void updatePurchases() {
        if (plugin.isAuthenticated()) {
            for (MCMarketApi.CommandType commandType : commandTypes) {
                for (MCMarketApi.Command command : plugin.getApi().getCommands(MCMarketApi.CommandStatus.NOT_EXECUTED, commandType)) {
                    runCommand(command);
                }
            }
        }
    }

    private void runCommand(MCMarketApi.Command command) {
        Player player = Server.getInstance().getPlayerExact(command.getPlayer().getName());
        boolean shouldExecute = true;
        if (command.isRequiredOnline() && (player == null || !player.isOnline())) {
            shouldExecute = false;
        }
        if (shouldExecute) {
            if (command.getRequiredSlots() > 0 && player != null) {
                if (getEmptySlots(player.getInventory()) < command.getRequiredSlots()) {
                    shouldExecute = false;
                }
            }
            if (shouldExecute) {
                plugin.getServer().getScheduler().scheduleDelayedTask(plugin, new AsyncTask() {
                    @Override
                    public void onRun() {
                        plugin.getServer().getScheduler().scheduleTask(plugin, () -> plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command.getCommand()));
                        if (command.isRepeat()) {
                            long period = command.getRepeatPeriod() > 0 ? 20 * 60 * 60 * command.getRepeatPeriod() : 1;
                            new NukkitRunnable() {
                                int executed = 0;

                                @Override
                                public void run() {
                                    plugin.getServer().getScheduler().scheduleTask(plugin, () -> plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command.getCommand()));
                                    executed++;

                                    if (executed >= command.getRepeatCycles()) {
                                        cancel();
                                    }
                                }
                            }.runTaskTimerAsynchronously(plugin, (int) period, (int) period);
                        }
                    }
                }, command.getDelay() > 0 ? (int) (20 * command.getDelay()) : 1, true);
                plugin.getApi().setExecuted(command.getId());
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