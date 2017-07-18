package com.minecraftmarket.minecraftmarket.bungee.commands;

import com.minecraftmarket.minecraftmarket.bungee.MCMarket;
import com.minecraftmarket.minecraftmarket.bungee.commands.subcmds.*;
import com.minecraftmarket.minecraftmarket.bungee.utils.chat.Chat;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MMCmd extends Command implements TabExecutor {
    private final List<Cmd> subCmds = new ArrayList<>();

    public MMCmd(MCMarket plugin) {
        super("MinecraftMarket", "minecraftmarket.use", "MM");
        subCmds.add(new ApiKey(plugin));
        subCmds.add(new Check(plugin));
        subCmds.add(new Reload(plugin));
        subCmds.add(new Version(plugin));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length > 0) {
            List<String> subCmdArgs = new ArrayList<>(Arrays.asList(args));
            subCmdArgs.remove(0);
            for (Cmd subCmd : subCmds) {
                if (subCmd.getCommand().equalsIgnoreCase(args[0])) {
                    subCmd.run(sender, subCmdArgs.toArray(new String[subCmdArgs.size()]));
                    return;
                }
            }
            sendHelp(sender);
        } else {
            sendHelp(sender);
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> matches = new ArrayList<>();
        if (args.length != 0) {
            String arg0 = args[0].toLowerCase();
            if (args.length == 1) {
                for (Cmd subCmd : subCmds) {
                    if (subCmd.getCommand().startsWith(arg0)) {
                        matches.add(subCmd.getCommand());
                    }
                }
            } else if (args.length == 2) {
                String arg1 = args[1].toLowerCase();
                for (Cmd subCmd : subCmds) {
                    if (subCmd.getTabComplete().size() > 0) {
                        if (subCmd.getCommand().equalsIgnoreCase(arg0)) {
                            for (String tabComplete : subCmd.getTabComplete()) {
                                if (tabComplete.startsWith(arg1)) {
                                    matches.add(tabComplete);
                                }
                            }
                        }
                    }
                }
            }
        }
        return matches;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(Chat.toComponent(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "================ " + ChatColor.YELLOW + "MinecraftMarket Help " + ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "================"));
        for (Cmd subCmd : subCmds) {
            if (subCmd.getArgs().isEmpty()) {
                sender.sendMessage(Chat.toComponent(ChatColor.GOLD + "/MM " + subCmd.getCommand() + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + subCmd.getDescription()));
            } else {
                sender.sendMessage(Chat.toComponent(ChatColor.GOLD + "/MM " + subCmd.getCommand() + " " + subCmd.getArgs() + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + subCmd.getDescription()));
            }
        }
        sender.sendMessage(Chat.toComponent(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "==================================================="));
    }
}