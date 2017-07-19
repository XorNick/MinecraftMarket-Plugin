package com.minecraftmarket.minecraftmarket.sponge.commands.subcmds;

import com.minecraftmarket.minecraftmarket.common.i18n.I18n;
import com.minecraftmarket.minecraftmarket.sponge.MCMarket;
import com.minecraftmarket.minecraftmarket.sponge.utils.chat.Colors;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.plugin.Plugin;

public class Version extends Cmd {
    private final MCMarket plugin;

    public Version(MCMarket plugin) {
        super("version", "Shows plugin version");
        this.plugin = plugin;
    }

    @Override
    public void run(CommandSource sender, String[] args) {
        sender.sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("cmd_current_version", plugin.getClass().getAnnotation(Plugin.class).version())));
    }
}