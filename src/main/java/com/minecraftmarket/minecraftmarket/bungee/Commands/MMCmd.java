package com.minecraftmarket.minecraftmarket.bungee.Commands;

import com.minecraftmarket.minecraftmarket.bungee.MCMarket;
import com.r4g3baby.pluginutils.Bungee.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MMCmd extends Command implements TabExecutor {
    private final MCMarket plugin;

    public MMCmd(MCMarket plugin) {
        super("MinecraftMarket", "minecraftmarket.use", "MM");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("apiKey")) {
                if (args.length > 1) {
                    plugin.setKey(args[1], true, authenticated -> {
                        if (authenticated) {
                            sender.sendMessage(Utils.toComponent("§7[§eMinecraftMarket§7] §aKey changed and activated successfully!"));
                        } else {
                            sender.sendMessage(Utils.toComponent("§7[§eMinecraftMarket§7] §cInvalid APIKey! Get your APIKey from MinecraftMarket panel."));
                            sender.sendMessage(Utils.toComponent("§7[§eMinecraftMarket§7] §cAnd use /MM apiKey <key> to change your key."));
                        }
                    });
                } else {
                    sender.sendMessage(Utils.toComponent("§7[§eMinecraftMarket§7] §cInvalid usage use§8: §7/MM apiKey <key>"));
                }
            } else if (args[0].equalsIgnoreCase("check")) {
                if (plugin.isAuthenticated()) {
                    sender.sendMessage(Utils.toComponent("§7[§eMinecraftMarket§7] §aRunning purchases check.."));
                    plugin.getProxy().getScheduler().runAsync(plugin, () -> plugin.getPurchasesTask().updatePurchases());
                } else {
                    sender.sendMessage(Utils.toComponent("§7[§eMinecraftMarket§7] §cInvalid APIKey! Get your APIKey from MinecraftMarket panel."));
                    sender.sendMessage(Utils.toComponent("§7[§eMinecraftMarket§7] §cAnd use /MM apiKey <key> to change your key."));
                }
            } else if (args[0].equalsIgnoreCase("version")) {
                sender.sendMessage(Utils.toComponent("§7[§eMinecraftMarket§7] §7Current plugin version is §b" + plugin.getDescription().getVersion() + "§7."));
            } else {
                sendHelp(sender);
            }
        } else {
            sendHelp(sender);
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        Set<String> matches = new HashSet<>();
        if (args.length != 0) {
            String arg0 = args[0].toLowerCase();
            if (args.length == 1) {
                for (String subCmd : Arrays.asList("apikey", "check", "version")) {
                    if (subCmd.startsWith(arg0)) {
                        matches.add(subCmd);
                    }
                }
            }
        }
        return matches;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(Utils.toComponent("§7§m================ §eMinecraftMarket Help §7§m================"));
        sender.sendMessage(Utils.toComponent("§6/MM apiKey <key> §8- §7Change APIKey"));
        sender.sendMessage(Utils.toComponent("§6/MM check §8- §7Manually check for new purchases"));
        sender.sendMessage(Utils.toComponent("§6/MM version §8- §7Shows plugin version"));
        sender.sendMessage(Utils.toComponent("§7§m==================================================="));
    }
}