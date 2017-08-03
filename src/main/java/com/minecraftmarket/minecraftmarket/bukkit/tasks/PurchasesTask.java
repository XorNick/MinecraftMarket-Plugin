package com.minecraftmarket.minecraftmarket.bukkit.tasks;

import com.minecraftmarket.minecraftmarket.bukkit.MCMarket;
import com.minecraftmarket.minecraftmarket.common.api.MCMarketApi;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

public class PurchasesTask implements Runnable {
    private final MCMarket plugin;

    public PurchasesTask(MCMarket plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
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
        Player player = Bukkit.getPlayerExact(user);
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
                plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> {
                    plugin.getServer().getScheduler().runTask(plugin, () -> plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command.getCommand()));
                    if (command.isRepeat()) {
                        long period = command.getPeriod() > 0 ? 20 * 60 * 60 * command.getPeriod() : 1;
                        new BukkitRunnable() {
                            int executed = 0;
                            @Override
                            public void run() {
                                plugin.getServer().getScheduler().runTask(plugin, () -> plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command.getCommand()));
                                executed++;

                                if (executed >= command.getCycles()) {
                                    cancel();
                                }
                            }
                        }.runTaskTimerAsynchronously(plugin, period, period);
                    }
                }, command.getDelay() > 0 ? 20 * command.getDelay() : 1);
                plugin.getApi().setExecuted(command.getId(), true);
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