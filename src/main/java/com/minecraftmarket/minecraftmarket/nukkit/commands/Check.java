package com.minecraftmarket.minecraftmarket.nukkit.commands;

import cn.nukkit.command.CommandSender;
import cn.nukkit.scheduler.AsyncTask;
import com.minecraftmarket.minecraftmarket.nukkit.MCMarket;
import com.r4g3baby.pluginutils.i18n.I18n;
import com.r4g3baby.pluginutils.nukkit.Utils;

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
            plugin.getServer().getScheduler().scheduleAsyncTask(plugin, new AsyncTask() {
                @Override
                public void onRun() {
                    plugin.getPurchasesTask().updatePurchases();
                }
            });
        } else {
            sender.sendMessage(Utils.color(I18n.tl("prefix") + " " + I18n.tl("cmd_auth_key")));
        }
    }
}