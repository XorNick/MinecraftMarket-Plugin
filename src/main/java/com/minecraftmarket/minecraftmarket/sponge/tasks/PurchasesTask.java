package com.minecraftmarket.minecraftmarket.sponge.tasks;

import com.minecraftmarket.minecraftmarket.common.api.MCMarketApi;
import com.minecraftmarket.minecraftmarket.sponge.MCMarket;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.type.CarriedInventory;
import org.spongepowered.api.scheduler.Task;

import java.util.Optional;
import java.util.function.Consumer;

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
        if (Sponge.isServerAvailable()) {
            Optional<Player> player = Sponge.getServer().getPlayer(user);
            boolean shouldExecute = true;
            if (command.isOnline() && player.isPresent()) {
                shouldExecute = false;
            }
            if (shouldExecute) {
                Sponge.getScheduler().createTaskBuilder().async().delayTicks(command.getDelay() > 0 ? 20 * command.getDelay() : 1).execute(() -> {
                    boolean canContinue = true;
                    if (command.getSlots() > 0 && player.isPresent()) {
                        if (getEmptySlots(player.get().getInventory()) < command.getSlots()) {
                            canContinue = false;
                        }
                    }
                    if (canContinue) {
                        Sponge.getScheduler().createTaskBuilder().execute(() -> {
                            Optional<CommandSource> commandSource = Sponge.getServer().getConsole().getCommandSource();
                            commandSource.ifPresent(source -> Sponge.getGame().getCommandManager().process(source, command.getCommand()));
                        }).submit(plugin);

                        if (command.isRepeat()) {
                            long period = command.getPeriod() > 0 ? 20 * 60 * 60 * command.getPeriod() : 1;
                            Sponge.getScheduler().createTaskBuilder().intervalTicks(period).execute(new Consumer<Task>() {
                                int executed = 0;
                                @Override
                                public void accept(Task task) {
                                    Optional<CommandSource> commandSource = Sponge.getServer().getConsole().getCommandSource();
                                    commandSource.ifPresent(source -> Sponge.getGame().getCommandManager().process(source, command.getCommand()));
                                    executed++;

                                    if (executed >= command.getCycles()) {
                                        task.cancel();
                                    }
                                }
                            }).submit(plugin);
                        }
                        plugin.getApi().setExecuted(command.getId(), true);
                    }
                }).submit(plugin);
            }
        }
    }

    private int getEmptySlots(CarriedInventory inventory) {
        return Math.max(0, 36 - inventory.size());
    }
}