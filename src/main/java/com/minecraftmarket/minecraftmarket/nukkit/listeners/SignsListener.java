package com.minecraftmarket.minecraftmarket.nukkit.listeners;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.SignChangeEvent;
import com.minecraftmarket.minecraftmarket.common.i18n.I18n;
import com.minecraftmarket.minecraftmarket.common.utils.Utils;
import com.minecraftmarket.minecraftmarket.nukkit.MCMarket;
import com.minecraftmarket.minecraftmarket.nukkit.utils.chat.Colors;

import java.util.Arrays;
import java.util.List;

public class SignsListener implements Listener {
    private final MCMarket plugin;

    public SignsListener(MCMarket plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSignChangeEvent(SignChangeEvent e) {
        if (plugin.getMainConfig().isUseSigns()) {
            if (e.getPlayer().hasPermission("minecraftmarket.signs")) {
                List<String> lines = Arrays.asList(e.getLines());
                if (lines.size() > 1 && lines.get(0).equals("[RecentDonor]") && Utils.isInt(lines.get(1))) {
                    int order = Utils.getInt(lines.get(1));
                    if (order > 0) {
                        if (plugin.getSignsConfig().addDonorSign(order, e.getBlock())) {
                            e.getPlayer().sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("sign_added")));
                            plugin.getSignsTask().updateSigns();
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent e) {
        if (plugin.getMainConfig().isUseSigns()) {
            if (e.getPlayer().hasPermission("minecraftmarket.signs")) {
                if (plugin.getSignsConfig().getDonorSignFor(e.getBlock()) != null) {
                    if (plugin.getSignsConfig().removeDonorSign(e.getBlock())) {
                        e.getPlayer().sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("sign_removed")));
                        plugin.getSignsTask().updateSigns();
                    }
                }
            }
        }
    }
}