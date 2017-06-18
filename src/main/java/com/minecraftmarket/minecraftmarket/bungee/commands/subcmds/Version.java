package com.minecraftmarket.minecraftmarket.bungee.commands.subcmds;

import com.minecraftmarket.minecraftmarket.bungee.MCMarket;
import com.r4g3baby.pluginutils.bungee.Utils;
import com.r4g3baby.pluginutils.i18n.I18n;
import net.md_5.bungee.api.CommandSender;

public class Version extends Cmd {
    private final MCMarket plugin;

    public Version(MCMarket plugin) {
        super("version", "Shows plugin version");
        this.plugin = plugin;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        sender.sendMessage(Utils.toComponent(Utils.color(I18n.tl("prefix") + " " + I18n.tl("cmd_current_version", plugin.getDescription().getVersion()))));
    }
}