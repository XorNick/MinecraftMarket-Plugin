package com.minecraftmarket.minecraftmarket.bukkit.tasks;

import com.minecraftmarket.minecraftmarket.bukkit.MCMarket;
import com.minecraftmarket.minecraftmarket.common.api.MCMarketApi;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;

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
        Player player = Bukkit.getPlayerExact(command.getPlayer().getName());
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
                plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> {
                    plugin.getServer().getScheduler().runTask(plugin, () -> plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command.getCommand()));
                    if (command.isRepeat()) {
                        long period = command.getRepeatPeriod() > 0 ? 20 * 60 * 60 * command.getRepeatPeriod() : 1;
                        new BukkitRunnable() {
                            int executed = 0;
                            @Override
                            public void run() {
                                plugin.getServer().getScheduler().runTask(plugin, () -> plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command.getCommand()));
                                executed++;

                                if (executed >= command.getRepeatCycles()) {
                                    cancel();
                                }
                            }
                        }.runTaskTimerAsynchronously(plugin, period, period);
                    }
                }, command.getDelay() > 0 ? 20 * command.getDelay() : 1);
                plugin.getApi().setExecuted(command.getId());
            }
        }
    }

    private int getEmptySlots(PlayerInventory inventory) {
        int amount = 0;
        for (ItemStack stack : inventory.getContents()) {
            if (stack == null || stack.getType() == Material.AIR) {
                amount++;
            }
        }
        return amount;
    }
}