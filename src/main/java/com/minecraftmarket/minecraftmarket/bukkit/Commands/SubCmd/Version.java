package com.minecraftmarket.minecraftmarket.bukkit.Commands.SubCmd;

import com.minecraftmarket.minecraftmarket.bukkit.MCMarket;
import com.r4g3baby.pluginutils.Bukkit.Utils;
import com.r4g3baby.pluginutils.I18n.I18n;
import org.bukkit.command.CommandSender;

public class Version extends Cmd {
    private final MCMarket plugin;

    public Version(MCMarket plugin) {
        super("version", "Shows plugin version");
        this.plugin = plugin;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        sender.sendMessage(Utils.color(I18n.tl("prefix") + " " + I18n.tl("cmd.currentVersion", plugin.getDescription().getVersion())));
    }
}