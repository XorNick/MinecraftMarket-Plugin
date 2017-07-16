package com.minecraftmarket.minecraftmarket.bungee.tasks;

import com.minecraftmarket.minecraftmarket.bungee.MCMarket;
import com.minecraftmarket.minecraftmarket.bungee.utils.BungeeRunnable;
import com.minecraftmarket.minecraftmarket.common.api.MCMarketApi;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.concurrent.TimeUnit;

public class PurchasesTask implements Runnable {
    private final MCMarket plugin;

    public PurchasesTask(MCMarket plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        plugin.getProxy().getScheduler().runAsync(plugin, this::updatePurchases);
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
        ProxiedPlayer player = plugin.getProxy().getPlayer(user);
        boolean shouldExecute = true;
        if (command.isOnline() && (player == null || !player.isConnected())) {
            shouldExecute = false;
        }
        if (shouldExecute) {
            plugin.getProxy().getScheduler().schedule(plugin, () -> {
                plugin.getProxy().getPluginManager().dispatchCommand(plugin.getProxy().getConsole(), command.getCommand());
                if (command.isRepeat()) {
                    long period = command.getPeriod() > 0 ? 60 * 60 * command.getPeriod() : 1;
                    new BungeeRunnable() {
                        int executed = 0;

                        @Override
                        public void run() {
                            plugin.getProxy().getPluginManager().dispatchCommand(plugin.getProxy().getConsole(), command.getCommand());
                            executed++;

                            if (executed >= command.getCycles()) {
                                cancel();
                            }
                        }
                    }.schedule(plugin, period, period, TimeUnit.SECONDS);
                }
                plugin.getApi().setExecuted(command.getId(), true);
            }, command.getDelay() > 0 ? command.getDelay() : 1, TimeUnit.SECONDS);
        }
    }
}