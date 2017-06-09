package com.minecraftmarket.minecraftmarket.bungee.Task;

import com.minecraftmarket.minecraftmarket.bungee.MCMarket;
import com.minecraftmarket.minecraftmarket.core.MCMApi;
import com.r4g3baby.pluginutils.Bungee.BungeeRunnable;
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