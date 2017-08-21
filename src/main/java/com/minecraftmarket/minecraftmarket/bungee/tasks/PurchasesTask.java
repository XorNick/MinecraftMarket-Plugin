package com.minecraftmarket.minecraftmarket.bungee.tasks;

import com.minecraftmarket.minecraftmarket.bungee.MCMarket;
import com.minecraftmarket.minecraftmarket.bungee.utils.BungeeRunnable;
import com.minecraftmarket.minecraftmarket.common.api.MCMarketApi;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PurchasesTask implements Runnable {
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
    public void run() {
        plugin.getProxy().getScheduler().runAsync(plugin, this::updatePurchases);
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
        ProxiedPlayer player = plugin.getProxy().getPlayer(command.getPlayer().getName());
        boolean shouldExecute = true;
        if (command.isRequiredOnline() && (player == null || !player.isConnected())) {
            shouldExecute = false;
        }
        if (shouldExecute) {
            plugin.getProxy().getScheduler().schedule(plugin, () -> {
                plugin.getProxy().getPluginManager().dispatchCommand(plugin.getProxy().getConsole(), command.getCommand());
                if (command.isRepeat()) {
                    long period = command.getRepeatPeriod() > 0 ? 60 * 60 * command.getRepeatPeriod() : 1;
                    new BungeeRunnable() {
                        int executed = 0;

                        @Override
                        public void run() {
                            plugin.getProxy().getPluginManager().dispatchCommand(plugin.getProxy().getConsole(), command.getCommand());
                            executed++;

                            if (executed >= command.getRepeatCycles()) {
                                cancel();
                            }
                        }
                    }.schedule(plugin, period, period, TimeUnit.SECONDS);
                }
            }, command.getDelay() > 0 ? command.getDelay() : 1, TimeUnit.SECONDS);
            plugin.getApi().setExecuted(command.getId());
        }
    }
}