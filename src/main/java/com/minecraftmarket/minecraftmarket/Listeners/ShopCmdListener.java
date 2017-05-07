package com.minecraftmarket.minecraftmarket.Listeners;

import com.minecraftmarket.minecraftmarket.MCMarket;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ShopCmdListener implements Listener {
    private final MCMarket plugin;

    public ShopCmdListener(MCMarket plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
        if (plugin.getMainConfig().isUseGUI()) {
            for (String cmd : plugin.getMainConfig().getShopCommands()) {
                if (e.getMessage().split(" ")[0].equalsIgnoreCase("/" + cmd)) {
                    e.setCancelled(true);
                    plugin.getInventoryManager().open(e.getPlayer());
                    break;
                }
            }
        }
    }
}