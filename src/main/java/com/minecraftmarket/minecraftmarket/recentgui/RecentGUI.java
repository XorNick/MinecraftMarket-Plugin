package com.minecraftmarket.minecraftmarket.recentgui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.minecraftmarket.minecraftmarket.Market;
import com.minecraftmarket.minecraftmarket.util.Chat;

public class RecentGUI {

	private Player player;
	private Inventory inv;

	public RecentGUI(Player player) {
		this.setPlayer(player);
		inv = Bukkit.createInventory(null, 18, Chat.get().getMsg("recent.inv-title"));
		RecentTask task = new RecentTask(inv);
		task.runTaskAsynchronously(Market.getPlugin());
		player.openInventory(inv);
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
}
