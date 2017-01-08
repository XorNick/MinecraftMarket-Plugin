package com.minecraftmarket.minecraftmarket.signs;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.minecraftmarket.minecraftmarket.Market;
import com.minecraftmarket.minecraftmarket.json.JSONException;
import com.minecraftmarket.minecraftmarket.shop.Shop;
import com.minecraftmarket.minecraftmarket.shop.ShopPackage;
import com.minecraftmarket.minecraftmarket.util.Chat;

public class SignListener implements Listener {

	public Market plugin = Market.getPlugin();
	public static Map<String, Sign> purchases = new HashMap<>();
	private Chat chat = Chat.get();

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event){
		if(event.getBlock().getType() == Material.SKULL){
			SignData.updateAllSigns();
		}
	}
	
	@EventHandler
	public void onSignChange1(SignChangeEvent event) {
		if (!event.getLine(0).equalsIgnoreCase("[mm_purchase]")) {
			return;
		}
		if (!event.getPlayer().hasPermission("minecraftmarket.admin")) {
			event.getPlayer().sendMessage(Chat.get().getLanguage().getString(("signs.no-permissions")));
			return;
		}

		if(event.getLine(0).equalsIgnoreCase("[mm_purchase]")) {
			if(purchases.containsKey(event.getPlayer().getName())) {
				purchases.remove(event.getPlayer().getName());
			}
			purchases.put(event.getPlayer().getName(), (Sign) event.getBlock().getState());
			Shop.getInstance().showCategories(event.getPlayer());
			return;
		}
	}

	@EventHandler
	public void onSignChange(final SignChangeEvent event) {
		if (!event.getLine(0).equalsIgnoreCase("[Recent]")) {
			return;
		}
		if (!event.getPlayer().hasPermission("minecraftmarket.admin")) {
			event.getPlayer().sendMessage(Chat.get().getLanguage().getString(("signs.no-permissions")));
			return;
		}

		int id = 0;

		try {
			id = Integer.parseInt(event.getLine(1));
		} catch (NumberFormatException ex) {
			event.getBlock().breakNaturally();
			event.getPlayer().sendMessage(chat.prefix + ChatColor.DARK_RED + "Wrong sign format");
		}

		Location loc = event.getBlock().getLocation();
		SignData sign = SignData.getSignByLocation(loc);

		if (sign != null) {
			sign.remove();
		}

		SignData signData = null;


		signData = new SignData(loc, id - 1);

		try {
			signData.update();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		event.getPlayer().sendMessage(chat.prefix + ChatColor.GREEN + Chat.get().getLanguage().getString("signs.created"));
	}
	
	@EventHandler
	public void onSignInteract(PlayerInteractEvent event) {
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		if(event.getClickedBlock().getType() != Material.SIGN 
				&& event.getClickedBlock().getType() != Material.SIGN_POST 
				&& event.getClickedBlock().getType() != Material.WALL_SIGN) {
			return;
		}
		Sign sign = (Sign) event.getClickedBlock().getState();
		if(sign == null) {
			return;
		}
		if(sign.getLine(0).equalsIgnoreCase(Chat.get().translate(Chat.get().getMsg("signPurchases.first_line")))) {
			event.setCancelled(true);
			int id1 = Integer.parseInt(sign.getLine(3));
			String url = ShopPackage.getById(id1).getUrl();
			event.getPlayer().sendMessage(Chat.get().translate(chat.prefix + Chat.get().getMsg("shop.item-url") + url));
		}
	}

	@EventHandler
	public void onSignBreak(BlockBreakEvent event) {
		SignData sign = SignData.getSignByLocation(event.getBlock().getLocation());
		if (sign != null) {
			if (event.getPlayer().hasPermission("signs.remove")) {
				sign.remove();
				event.getPlayer().sendMessage(chat.prefix + ChatColor.RED + "Sign removed");
			} else {
				event.getPlayer().sendMessage(ChatColor.DARK_RED + Chat.get().getLanguage().getString(("signs.no-permissions")));
			}
		}
	}



}
