package com.minecraftmarket.minecraftmarket.bukkit.commands.subcmds;

import com.minecraftmarket.minecraftmarket.bukkit.MCMarket;
import com.minecraftmarket.minecraftmarket.bukkit.utils.chat.Colors;
import com.minecraftmarket.minecraftmarket.common.i18n.I18n;
import org.bukkit.command.CommandSender;

public class UpdateSigns extends Cmd {
    private final MCMarket plugin;

    public UpdateSigns(MCMarket plugin) {
        super("updateSigns", "Updates plugin signs");
        this.plugin = plugin;
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (plugin.getMainConfig().isUseSigns()) {
            if (plugin.isAuthenticated()) {
                sender.sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("cmd_sign_update")));
                plugin.getSignsTask().updateSigns();
            } else {
                sender.sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("cmd_auth_key")));
            }
        } else {
            sender.sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("cmd_sign_disabled")));
        }
    }
}