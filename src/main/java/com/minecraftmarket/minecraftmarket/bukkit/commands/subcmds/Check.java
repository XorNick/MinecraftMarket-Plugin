package com.minecraftmarket.minecraftmarket.bukkit.commands.subcmds;

import com.minecraftmarket.minecraftmarket.bukkit.MCMarket;
import com.r4g3baby.pluginutils.bukkit.Utils;
import com.r4g3baby.pluginutils.i18n.I18n;
import org.bukkit.command.CommandSender;

public class Check extends Cmd {
    private final MCMarket plugin;

    public Check(MCMarket plugin) {
        super("check", "Manually check for new purchases");
        this.plugin = plugin;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (plugin.isAuthenticated()) {
            sender.sendMessage(Utils.color(I18n.tl("prefix") + " " + I18n.tl("cmd_check_purchases")));
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> plugin.getPurchasesTask().updatePurchases());
        } else {
            sender.sendMessage(Utils.color(I18n.tl("prefix") + " " + I18n.tl("cmd_auth_key")));
        }
    }
}