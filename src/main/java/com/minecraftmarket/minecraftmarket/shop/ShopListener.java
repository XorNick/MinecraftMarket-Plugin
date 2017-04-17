package com.minecraftmarket.minecraftmarket.shop;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.minecraftmarket.minecraftmarket.signs.SignListener;
import com.minecraftmarket.minecraftmarket.util.Chat;

public class ShopListener implements Listener {

	Shop gui = Shop.getInstance();

	Chat chat = Chat.get();

	public String getMsg(String string) {
		return Chat.get().getMsg(string);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		try {
			if (event.getInventory().getTitle().contains("Category: ") || event.getInventory().getTitle().contains("Categories")) {
				event.setCancelled(true);
				if (event.getCurrentItem() == null) {
					event.setCancelled(true);
					return;
				}
				if (event.getCurrentItem().getItemMeta() == null) {
					event.setCancelled(true);
					return;
				}
			}
			if (event.getInventory().getTitle().startsWith("Category: ")) {
				Player player = (Player) event.getWhoClicked();
				String name = event.getCurrentItem().getItemMeta().getDisplayName();
				
				if (name.contains(Chat.get().translate(getMsg("shop.back-to-category")))) {
					event.getWhoClicked().closeInventory();
					gui.showCategories((Player) event.getWhoClicked());
					event.setCancelled(true);
					return;
				}
				
				String[] id = name.split(": ");
				int id1 = Integer.parseInt(id[1]);
				String url = ShopPackage.getById(id1).getUrl();
				if(SignListener.purchases.containsKey(event.getWhoClicked().getName())) {
					event.setCancelled(true);
					Sign sign = SignListener.purchases.get(event.getWhoClicked().getName());
					String price = ShopPackage.getById(id1).getPrice();
					String currency = ShopPackage.getById(id1).getCurrency();
					sign.setLine(0, Chat.get().translate(replace(Chat.get().getMsg("signPurchases.first_line"), id1, price, currency)));
					sign.setLine(1, Chat.get().translate(replace(Chat.get().getMsg("signPurchases.second_line"), id1, price, currency)));
					sign.setLine(2, Chat.get().translate(replace(Chat.get().getMsg("signPurchases.third_line"), id1, price, currency)));
					sign.setLine(3, id[1]);
					sign.update();
					SignListener.purchases.remove(event.getWhoClicked().getName());
					event.getWhoClicked().closeInventory();
					return;
				}
				player.sendMessage(chat.prefix + getMsg("shop.item-url") + url);
				event.getWhoClicked().closeInventory();
				event.setCancelled(true);
				return;
			}
			
			
			if (event.getInventory().getTitle().contains("Categories")) {
				int num = ShopCategory.getCategoryBySlot(event.getSlot()).getID();
				event.getWhoClicked().closeInventory();
				gui.showGui((Player) event.getWhoClicked(), num);
				event.setCancelled(true);
			}
			
		} catch (Exception e1) {
		}
	}

	private String replace(String replace, int id, String price, String currency) {
		return replace.replace("%name%", ShopPackage.getById(id).getName())
				.replace("%price%", price)
				.replace("%currency%", currency);
	}

}