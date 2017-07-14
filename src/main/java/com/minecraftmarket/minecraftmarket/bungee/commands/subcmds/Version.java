package com.minecraftmarket.minecraftmarket.bungee.commands.subcmds;

import com.minecraftmarket.minecraftmarket.bungee.MCMarket;
import com.minecraftmarket.minecraftmarket.bungee.utils.chat.Chat;
import com.minecraftmarket.minecraftmarket.bungee.utils.chat.Colors;
import com.minecraftmarket.minecraftmarket.common.i18n.I18n;
import net.md_5.bungee.api.CommandSender;

public class Version extends Cmd {
    private final MCMarket plugin;

    public Version(MCMarket plugin) {
        super("version", "Shows plugin version");
        this.plugin = plugin;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        sender.sendMessage(Chat.toComponent(Colors.color(I18n.tl("prefix") + " " + I18n.tl("cmd_current_version", plugin.getDescription().getVersion()))));
    }
}