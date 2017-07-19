package com.minecraftmarket.minecraftmarket.sponge.commands.subcmds;


import com.minecraftmarket.minecraftmarket.common.i18n.I18n;
import com.minecraftmarket.minecraftmarket.sponge.MCMarket;
import com.minecraftmarket.minecraftmarket.sponge.utils.chat.Colors;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;

public class Check extends Cmd {
    private final MCMarket plugin;

    public Check(MCMarket plugin) {
        super("check", "Manually check for new purchases");
        this.plugin = plugin;
    }

    @Override
    public void run(CommandSource sender, String[] args) {
        if (plugin.isAuthenticated()) {
            sender.sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("cmd_check_purchases")));
            Sponge.getScheduler().createTaskBuilder().async().execute(() -> plugin.getPurchasesTask().updatePurchases()).submit(plugin);
        } else {
            sender.sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("cmd_auth_key")));
        }
    }
}