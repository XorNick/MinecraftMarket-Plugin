package com.minecraftmarket.minecraftmarket.bukkit.commands.subcmds;

import com.minecraftmarket.minecraftmarket.bukkit.MCMarket;
import com.minecraftmarket.minecraftmarket.bukkit.utils.chat.Colors;
import com.minecraftmarket.minecraftmarket.common.i18n.I18n;
import org.bukkit.command.CommandSender;

public class ApiKey extends Cmd {
    private final MCMarket plugin;

    public ApiKey(MCMarket plugin) {
        super("apikey", "Change APIKey", "<key>");
        this.plugin = plugin;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (args.length > 0) {
            plugin.setKey(args[0], true, authenticated -> {
                if (authenticated) {
                    sender.sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("cmd_key_changed")));
                } else {
                    sender.sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("api_auth_failed")));
                }
            });
        } else {
            sender.sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("cmd_invalid_usage", "/MM apiKey <key>")));
        }
    }
}