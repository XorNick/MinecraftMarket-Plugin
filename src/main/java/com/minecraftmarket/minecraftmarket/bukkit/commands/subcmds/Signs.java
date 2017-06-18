package com.minecraftmarket.minecraftmarket.bukkit.commands.subcmds;

import com.minecraftmarket.minecraftmarket.bukkit.MCMarket;
import com.r4g3baby.pluginutils.bukkit.Utils;
import com.r4g3baby.pluginutils.i18n.I18n;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Signs extends Cmd {
    private final MCMarket plugin;

    public Signs(MCMarket plugin) {
        super("signs", "Manage recent donor signs", "<add|remove|update>", Arrays.asList("add", "remove", "update"));
        this.plugin = plugin;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (plugin.getMainConfig().isUseSigns()) {
            if (plugin.isAuthenticated()) {
                if (sender instanceof Player) {
                    if (args.length > 0) {
                        Player player = (Player) sender;
                        if (args[0].equalsIgnoreCase("add")) {
                            if (args.length > 1) {
                                if (Utils.isInt(args[1])) {
                                    if (Utils.getInt(args[1]) > 0) {
                                        Block block = getTargetBlock(player);
                                        if (block != null) {
                                            if (block.getState() instanceof Sign) {
                                                if (plugin.getSignsConfig().addDonorSign(Utils.getInt(args[1]), block)) {
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
                                    player.sendMessage(Utils.color(I18n.tl("prefix") + " " + I18n.tl("cmd.invalidNumber", args[1])));
                                }
                            } else {
                                player.sendMessage(Utils.color(I18n.tl("prefix") + " " + I18n.tl("cmd.invalidUsage", "/MM signs add <order>")));
                            }
                        } else if (args[0].equalsIgnoreCase("remove")) {
                            Block block = getTargetBlock(player);
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
                        } else if (args[0].equalsIgnoreCase("update")) {
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
    }

    private Block getTargetBlock(Player player) {
        Set<Material> blocks = new HashSet<>();
        blocks.add(Material.AIR);
        return player.getTargetBlock(blocks, 7);
    }
}