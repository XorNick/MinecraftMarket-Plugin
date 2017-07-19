package com.minecraftmarket.minecraftmarket.sponge.commands.subcmds;

import com.minecraftmarket.minecraftmarket.common.i18n.I18n;
import com.minecraftmarket.minecraftmarket.sponge.MCMarket;
import com.minecraftmarket.minecraftmarket.sponge.utils.chat.Colors;
import org.spongepowered.api.command.CommandSource;

public class Reload extends Cmd {
    private final MCMarket plugin;

    public Reload(MCMarket plugin) {
        super("reload", "Reloads the plugin");
        this.plugin = plugin;
    }

    @Override
    public void run(CommandSource sender, String[] args) {
        sender.sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("cmd_reloading")));
        plugin.reloadConfigs(authenticated -> {
            if (!authenticated) {
                sender.sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("api_auth_failed")));
            }
            sender.sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("cmd_reload_done")));
        });
    }
}