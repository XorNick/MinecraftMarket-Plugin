package com.minecraftmarket.minecraftmarket.Task;

import com.minecraftmarket.minecraftmarket.Api.MCMApi;
import com.minecraftmarket.minecraftmarket.MCMarket;
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
        Player player = Bukkit.getPlayerExact(user);
        boolean shouldExecute = true;
        if (command.isOnline() && player == null) {
            shouldExecute = false;
        }
        if (shouldExecute) {
            plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> {
                boolean canContinue = true;
                if (command.getSlots() > 0 && player != null) {
                    if (getEmptySlots(player.getInventory()) < command.getSlots()) {
                        canContinue = false;
                    }
                }
                if (canContinue) {
                    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command.getCommand());
                    if (command.isRepeat()) {
                        long period = command.getPeriod() > 0 ? 20 * 60 * 60 * command.getPeriod() : 1;
                        new BukkitRunnable() {
                            int executed = 0;
                            @Override
                            public void run() {
                                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command.getCommand());
                                executed++;

                                if (executed >= command.getCycles()) {
                                    cancel();
                                }
                            }
                        }.runTaskTimerAsynchronously(plugin, period, period);
                    }
                    plugin.getApi().setExecuted(command.getId(), true);
                }
            }, command.getDelay() > 0 ? 20 * command.getDelay() : 1);
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