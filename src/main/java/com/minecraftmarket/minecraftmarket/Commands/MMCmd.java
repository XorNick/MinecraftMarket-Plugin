package com.minecraftmarket.minecraftmarket.Commands;

import com.minecraftmarket.minecraftmarket.Configs.MessagesConfig;
import com.minecraftmarket.minecraftmarket.MCMarket;
import com.r4g3baby.pluginutils.Utils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;

public class MMCmd implements CommandExecutor, TabCompleter {
    private final MCMarket plugin;
    private final MessagesConfig messages;

    public MMCmd(MCMarket plugin) {
        this.plugin = plugin;
        this.messages = plugin.getMessagesConfig();
    }

    @Override // TODO Add translation from messages.yml config
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("apiKey")) {
                if (args.length > 1) {
                    plugin.setKey(args[1], true, authenticated -> {
                        if (authenticated) {
                            sender.sendMessage(messages.getPrefix() + " §aKey changed and activated successfully!");
                        } else {
                            sender.sendMessage(messages.getPrefix() + " §cInvalid APIKey! Get your APIKey from MinecraftMarket panel.");
                            sender.sendMessage(messages.getPrefix() + " §cAnd use /MM apiKey <key> to change your key.");
                        }
                    });
                } else {
                    sender.sendMessage(messages.getPrefix() + " §cInvalid usage use§8: §7/MM apiKey <key>");
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
                                                            player.sendMessage(messages.getPrefix() + " §aSign added. Updating signs..");
                                                            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> plugin.getSignsTask().updateSigns());
                                                        } else {
                                                            player.sendMessage(messages.getPrefix() + " §cFailed to add sign make sure it's not already added.");
                                                        }
                                                    } else {
                                                        player.sendMessage(messages.getPrefix() + " §cThe block you're looking at isn't valid or is too far away.");
                                                    }
                                                } else {
                                                    player.sendMessage(messages.getPrefix() + " §cThe block you're looking at isn't valid or is too far away.");
                                                }
                                            } else {
                                                player.sendMessage(messages.getPrefix() + " §c<order> needs to be greater than 0.");
                                            }
                                        } else {
                                            player.sendMessage(messages.getPrefix() + " §c" + args[2] + " isn't a valid number.");
                                        }
                                    } else {
                                        player.sendMessage(messages.getPrefix() + " §cInvalid usage use§8: §7/MM signs add <order>");
                                    }
                                } else if (args[1].equalsIgnoreCase("remove")) {
                                    Set<Material> blocks = new HashSet<>();
                                    blocks.add(Material.AIR);
                                    Block block = player.getTargetBlock(blocks, 7);
                                    if (block != null) {
                                        if (block.getState() instanceof Sign) {
                                            if (plugin.getSignsConfig().removeDonorSign(block)) {
                                                player.sendMessage(messages.getPrefix() + " §aSign removed. Updating signs..");
                                                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> plugin.getSignsTask().updateSigns());
                                                block.breakNaturally();
                                            } else {
                                                player.sendMessage(messages.getPrefix() + " §cFailed to remove sign make sure it has been added.");
                                            }
                                        } else {
                                            player.sendMessage(messages.getPrefix() + " §cThe block you're looking at isn't valid or is too far away.");
                                        }
                                    } else {
                                        player.sendMessage(messages.getPrefix() + " §cThe block you're looking at isn't valid or is too far away.");
                                    }
                                } else if (args[1].equalsIgnoreCase("update")) {
                                    player.sendMessage(messages.getPrefix() + " §aUpdating signs..");
                                    plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> plugin.getSignsTask().updateSigns());
                                } else {
                                    player.sendMessage(messages.getPrefix() + " §cInvalid usage use§8: §7/MM signs <add|remove|update>");
                                }
                            } else {
                                sender.sendMessage(messages.getPrefix() + " §cInvalid usage use§8: §7/MM signs <add|remove|update>");
                            }
                        } else {
                            sender.sendMessage(messages.getPrefix() + " §cSender must be a player!");
                        }
                    } else {
                        sender.sendMessage(messages.getPrefix() + " §cInvalid APIKey! Get your APIKey from MinecraftMarket panel.");
                        sender.sendMessage(messages.getPrefix() + " §cAnd use /MM apiKey <key> to change your key.");
                    }
                } else {
                    sender.sendMessage(messages.getPrefix() + " §cEnable the use of signs in the config.");
                }
            } else if (args[0].equalsIgnoreCase("check")) {
                if (plugin.isAuthenticated()) {
                    sender.sendMessage(messages.getPrefix() + " §aRunning purchases check..");
                    plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> plugin.getPurchasesTask().updatePurchases());
                } else {
                    sender.sendMessage(messages.getPrefix() + " §cInvalid APIKey! Get your APIKey from MinecraftMarket panel.");
                    sender.sendMessage(messages.getPrefix() + " §cAnd use /MM apiKey <key> to change your key.");
                }
            } else if (args[0].equalsIgnoreCase("version")) {
                sender.sendMessage(messages.getPrefix() + " §7Current plugin version is §b" + plugin.getDescription().getVersion() + "§7.");
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