package com.minecraftmarket.minecraftmarket.bukkit.commands.subcmds;

import com.minecraftmarket.minecraftmarket.bukkit.MCMarket;
import com.minecraftmarket.minecraftmarket.bukkit.utils.chat.Colors;
import com.minecraftmarket.minecraftmarket.common.i18n.I18n;
import org.bukkit.command.CommandSender;

public class Version extends Cmd {
    private final MCMarket plugin;

    public Version(MCMarket plugin) {
        super("version", "Shows plugin version");
        this.plugin = plugin;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        sender.sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("cmd_current_version", plugin.getDescription().getVersion())));
    }
}