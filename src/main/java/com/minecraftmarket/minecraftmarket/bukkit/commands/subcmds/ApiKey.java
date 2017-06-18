package com.minecraftmarket.minecraftmarket.bukkit.commands.subcmds;

import com.minecraftmarket.minecraftmarket.bukkit.MCMarket;
import com.r4g3baby.pluginutils.bukkit.Utils;
import com.r4g3baby.pluginutils.i18n.I18n;
import org.bukkit.command.CommandSender;

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
                    sender.sendMessage(Utils.color(I18n.tl("prefix") + " " + I18n.tl("cmd_key_changed")));
                } else {
                    sender.sendMessage(Utils.color(I18n.tl("prefix") + " " + I18n.tl("cmd_invalid_key", "/MM apiKey <key>")));
                }
            });
        } else {
            sender.sendMessage(Utils.color(I18n.tl("prefix") + " " + I18n.tl("cmd_invalid_usage", "/MM apiKey <key>")));
        }
    }
}