package com.minecraftmarket.minecraftmarket.bungee.commands.subcmds;

import com.minecraftmarket.minecraftmarket.bungee.MCMarket;
import com.minecraftmarket.minecraftmarket.bungee.utils.chat.Chat;
import com.minecraftmarket.minecraftmarket.bungee.utils.chat.Colors;
import com.minecraftmarket.minecraftmarket.common.i18n.I18n;
import net.md_5.bungee.api.CommandSender;

public class ApiKey extends Cmd {
    private final MCMarket plugin;

    public ApiKey(MCMarket plugin) {
        super("apiKey", "Change APIKey", "<key>");
        this.plugin = plugin;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (args.length > 0) {
            plugin.setKey(args[0], true, authenticated -> {
                if (authenticated) {
                    sender.sendMessage(Chat.toComponent(Colors.color(I18n.tl("prefix") + " " + I18n.tl("cmd_key_changed"))));
                } else {
                    sender.sendMessage(Chat.toComponent(Colors.color(I18n.tl("prefix") + " " + I18n.tl("cmd_invalid_key", "/MM apiKey <key>"))));
                }
            });
        } else {
            sender.sendMessage(Chat.toComponent(Colors.color(I18n.tl("prefix") + " " + I18n.tl("cmd_invalid_usage", "/MM apiKey <key>"))));
        }
    }
}