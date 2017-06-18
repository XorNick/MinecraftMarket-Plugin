package com.minecraftmarket.minecraftmarket.bungee.commands.subcmds;

import com.minecraftmarket.minecraftmarket.bungee.MCMarket;
import com.r4g3baby.pluginutils.bungee.Utils;
import com.r4g3baby.pluginutils.i18n.I18n;
import net.md_5.bungee.api.CommandSender;

public class Check extends Cmd {
    private final MCMarket plugin;

    public Check(MCMarket plugin) {
        super("check", "Manually check for new purchases");
        this.plugin = plugin;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (plugin.isAuthenticated()) {
            sender.sendMessage(Utils.toComponent(Utils.color(I18n.tl("prefix") + " " + I18n.tl("cmd_check_purchases"))));
            plugin.getProxy().getScheduler().runAsync(plugin, () -> plugin.getPurchasesTask().updatePurchases());
        } else {
            sender.sendMessage(Utils.toComponent(Utils.color(I18n.tl("prefix") + " " + I18n.tl("cmd_auth_key"))));
        }
    }
}