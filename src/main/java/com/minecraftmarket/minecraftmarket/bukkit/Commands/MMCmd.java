package com.minecraftmarket.minecraftmarket.bukkit.Commands;

import com.minecraftmarket.minecraftmarket.bukkit.MCMarket;
import com.r4g3baby.pluginutils.Bukkit.Utils;
import com.r4g3baby.pluginutils.I18n.I18n;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

public class MMCmd implements CommandExecutor, TabCompleter {
    private final MCMarket plugin;

    public MMCmd(MCMarket plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("apiKey")) {
                if (args.length > 1) {
                    plugin.setKey(args[1], true, authenticated -> {
                        if (authenticated) {
                            sender.sendMessage(Utils.color(I18n.tl("prefix") + " " + I18n.tl("cmd.keyChanged")));
                        } else {
                            sender.sendMessage(Utils.color(I18n.tl("prefix") + " " + I18n.tl("cmd.invalidKey", "/MM apiKey <key>")));
                        }
                    });
                } else {
                    sender.sendMessage(Utils.color(I18n.tl("prefix") + " " + I18n.tl("cmd.invalidUsage", "/MM apiKey <key>")));
                }
            } else if (args[0].equalsIgnoreCase("signs")) {
                if (plugin.getMainConfig().isUseSigns()) {
                    if (plugin.isAuthenticated()) {
                        if (sender instanceof Player) {
                            if (args.length > 1) {
                                Player player = (Player) sender;
                                if (args[1].equalsIgnoreCase("add")) {
                                    if (args.length > 2) {
                                        if (Utils.isInt(args[2])) {
                                            if (Utils.getInt(args[2]) > 0) {
                                                Set<Material> blocks = new HashSet<>();
                                                blocks.add(Material.AIR);
                                                Block block = player.getTargetBlock(blocks, 7);
                                                if (block != null) {
                                                    if (block.getState() instanceof Sign) {
                                                        if (plugin.getSignsConfig().addDonorSign(Utils.getInt(args[2]), block)) {
                                                            player.sendMessage(Utils.color(I18n.tl("prefix") + " " + I18n.tl("cmd.signAdd")));
                                                            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> plugin.getSignsTask().updateSigns());
                                                        } else {
                                                            player.sendMessage(Utils.color(I18n.tl("prefix") + " " + I18n.tl("cmd.signAddFail")));
                                                        }
                                                    } else {
                                                        player.sendMessage(Utils.color(I18n.tl("prefix") + " " + I18n.tl("cmd.invalidBlock")));
                                                    }
                                                } else {
                                                    player.sendMessage(Utils.color(I18n.tl("prefix") + " " + I18n.tl("cmd.invalidBlock")));
                                                }
                                            } else {
                                                player.sendMessage(Utils.color(I18n.tl("prefix") + " " + I18n.tl("cmd.greaterThan", "<order>", 0)));
                                            }
                                        } else {
                                            player.sendMessage(Utils.color(I18n.tl("prefix") + " " + I18n.tl("cmd.invalidNumber", args[2])));
                                        }
                                    } else {
                                        player.sendMessage(Utils.color(I18n.tl("prefix") + " " + I18n.tl("cmd.invalidUsage", "/MM signs add <order>")));
                                    }
                                } else if (args[1].equalsIgnoreCase("remove")) {
                                    Set<Material> blocks = new HashSet<>();
                                    blocks.add(Material.AIR);
                                    Block block = player.getTargetBlock(blocks, 7);
                                    if (block != null) {
                                        if (block.getState() instanceof Sign) {
                                            if (plugin.getSignsConfig().removeDonorSign(block)) {
                                                player.sendMessage(Utils.color(I18n.tl("prefix") + " " + I18n.tl("cmd.signRem")));
                                                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> plugin.getSignsTask().updateSigns());
                                                block.breakNaturally();
                                            } else {
                                                player.sendMessage(Utils.color(I18n.tl("prefix") + " " + I18n.tl("cmd.signRemFail")));
                                            }
                                        } else {
                                            player.sendMessage(Utils.color(I18n.tl("prefix") + " " + I18n.tl("cmd.invalidBlock")));
                                        }
                                    } else {
                                        player.sendMessage(Utils.color(I18n.tl("prefix") + " " + I18n.tl("cmd.invalidBlock")));
                                    }
                                } else if (args[1].equalsIgnoreCase("update")) {
                                    player.sendMessage(Utils.color(I18n.tl("prefix") + " " + I18n.tl("cmd.signUpdate")));
                                    plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> plugin.getSignsTask().updateSigns());
                                } else {
                                    player.sendMessage(Utils.color(I18n.tl("prefix") + " " + I18n.tl("cmd.invalidUsage", "/MM signs <add|remove|update>")));
                                }
                            } else {
                                sender.sendMessage(Utils.color(I18n.tl("prefix") + " " + I18n.tl("cmd.invalidUsage", "/MM signs <add|remove|update>")));
                            }
                        } else {
                            sender.sendMessage(Utils.color(I18n.tl("prefix") + " " + I18n.tl("cmd.invalidSender")));
                        }
                    } else {
                        sender.sendMessage(Utils.color(I18n.tl("prefix") + " " + I18n.tl("cmd.invalidKey", "/MM apiKey <key>")));
                    }
                } else {
                    sender.sendMessage(Utils.color(I18n.tl("prefix") + " " + I18n.tl("cmd.signDisabled")));
                }
            } else if (args[0].equalsIgnoreCase("check")) {
                if (plugin.isAuthenticated()) {
                    sender.sendMessage(Utils.color(I18n.tl("prefix") + " " + I18n.tl("cmd.checkPurchases")));
                    plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> plugin.getPurchasesTask().updatePurchases());
                } else {
                    sender.sendMessage(Utils.color(I18n.tl("prefix") + " " + I18n.tl("cmd.invalidKey", "/MM apiKey <key>")));
                }
            } else if (args[0].equalsIgnoreCase("version")) {
                sender.sendMessage(Utils.color(I18n.tl("prefix") + " " + I18n.tl("cmd.currentVersion", plugin.getDescription().getVersion())));
            } else {
                sendHelp(sender);
            }
        } else {
            sendHelp(sender);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> matches = new ArrayList<>();
        if (args.length != 0) {
            String arg0 = args[0].toLowerCase();
            if (args.length == 1) {
                List<String> subCmds = Arrays.asList("apikey", "signs", "check", "version");
                for (String subCmd : subCmds) {
                    if (subCmd.startsWith(arg0)) {
                        matches.add(subCmd);
                    }
                }
            } else if (arg0.equals("signs") && args.length == 2) {
                String arg1 = args[1].toLowerCase();
                List<String> subCmds = Arrays.asList("add", "remove", "update");
                for (String subCmd : subCmds) {
                    if (subCmd.startsWith(arg1)) {
                        matches.add(subCmd);
                    }
                }
            }
        }
        return matches;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§7§m================ §eMinecraftMarket Help §7§m================");
        sender.sendMessage("§6/MM apiKey <key> §8- §7Change APIKey");
        sender.sendMessage("§6/MM signs <args> §8- §7Manage recent donor signs");
        sender.sendMessage("§6/MM check §8- §7Manually check for new purchases");
        sender.sendMessage("§6/MM version §8- §7Shows plugin version");
        sender.sendMessage("§7§m===================================================");
    }
}