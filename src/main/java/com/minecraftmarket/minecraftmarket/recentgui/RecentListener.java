package com.minecraftmarket.minecraftmarket.recentgui;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.minecraftmarket.minecraftmarket.util.Chat;

public class RecentListener implements Listener {
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getInventory().getName().equals(Chat.get().getMsg("recent.inv-title"))) {
			event.setCancelled(true);
			return;
		}
	}
}
