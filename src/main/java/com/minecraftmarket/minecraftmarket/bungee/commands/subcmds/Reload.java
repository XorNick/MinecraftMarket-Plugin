package com.minecraftmarket.minecraftmarket.bungee.commands.subcmds;

import com.minecraftmarket.minecraftmarket.bungee.MCMarket;
import com.minecraftmarket.minecraftmarket.bungee.utils.chat.Chat;
import com.minecraftmarket.minecraftmarket.bungee.utils.chat.Colors;
import com.minecraftmarket.minecraftmarket.common.i18n.I18n;
import net.md_5.bungee.api.CommandSender;

public class Reload extends Cmd {
    private final MCMarket plugin;

    public Reload(MCMarket plugin) {
        super("reload", "Reloads the plugin");
        this.plugin = plugin;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        sender.sendMessage(Chat.toComponent(Colors.color(I18n.tl("prefix") + " " + I18n.tl("cmd_reloading"))));
        plugin.reloadConfigs(authenticated -> {
            if (!authenticated) {
                sender.sendMessage(Chat.toComponent(Colors.color(I18n.tl("prefix") + " " + I18n.tl("api_auth_failed"))));
            }
            sender.sendMessage(Chat.toComponent(Colors.color(I18n.tl("prefix") + " " + I18n.tl("cmd_reload_done"))));
        });
    }
}