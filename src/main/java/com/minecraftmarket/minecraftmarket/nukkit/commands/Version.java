package com.minecraftmarket.minecraftmarket.nukkit.commands;

import cn.nukkit.command.CommandSender;
import com.minecraftmarket.minecraftmarket.nukkit.MCMarket;
import com.r4g3baby.pluginutils.i18n.I18n;
import com.r4g3baby.pluginutils.nukkit.Utils;

public class Version extends Cmd {
    private final MCMarket plugin;

    public Version(MCMarket plugin) {
        super("version", "Shows plugin version");
        this.plugin = plugin;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        sender.sendMessage(Utils.color(I18n.tl("prefix") + " " + I18n.tl("cmd_current_version", plugin.getDescription().getVersion())));
    }
}