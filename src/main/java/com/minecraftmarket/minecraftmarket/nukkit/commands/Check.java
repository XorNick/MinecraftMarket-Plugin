package com.minecraftmarket.minecraftmarket.nukkit.commands;

import cn.nukkit.command.CommandSender;
import cn.nukkit.scheduler.AsyncTask;
import com.minecraftmarket.minecraftmarket.common.i18n.I18n;
import com.minecraftmarket.minecraftmarket.nukkit.MCMarket;
import com.minecraftmarket.minecraftmarket.nukkit.utils.chat.Colors;

public class Check extends Cmd {
    private final MCMarket plugin;

    public Check(MCMarket plugin) {
        super("check", "Manually check for new purchases");
        this.plugin = plugin;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (plugin.isAuthenticated()) {
            sender.sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("cmd_check_purchases")));
            plugin.getServer().getScheduler().scheduleAsyncTask(plugin, new AsyncTask() {
                @Override
                public void onRun() {
                    plugin.getPurchasesTask().updatePurchases();
                }
            });
        } else {
            sender.sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("cmd_auth_key")));
        }
    }
}