package com.minecraftmarket.minecraftmarket.recentgui;

import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.minecraftmarket.minecraftmarket.Api;
import com.minecraftmarket.minecraftmarket.json.JSONArray;
import com.minecraftmarket.minecraftmarket.json.JSONObject;
import com.minecraftmarket.minecraftmarket.util.Chat;
import com.minecraftmarket.minecraftmarket.util.Json;
import com.minecraftmarket.minecraftmarket.util.Log;

public class RecentUtil {

	private JSONArray json;

	public RecentUtil() {
		try {
			String recent = Json.getJSON(Api.getUrl() + "/recentdonor");
			Log.response("Recent payment", recent);
			if (!Json.isJson(recent)) {
				json = null;
			} else {
				JSONObject jsono = new JSONObject(recent);
				this.json = jsono.optJSONArray("result");
			}
		} catch (Exception e) {
			Log.log(e);
		}
	}

	@SuppressWarnings("deprecation")
	public ItemStack get(int num) {
		try {
			if (json == null) {
				return null;
			}
			String user = json.getJSONObject(num).getString("username");
			String packageName = json.getJSONObject(num).getString("item");
			String date = json.getJSONObject(num).getString("date");
			int amount = json.getJSONObject(num).getInt("price");
			String currency = json.getJSONObject(num).getString("currency");
			ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 0, (byte) 3);
			SkullMeta meta = (SkullMeta) item.getItemMeta();
			
			meta.setOwner(user);
			meta.setDisplayName(Chat.get().translate(Chat.get().getMsg("recent.item.displayName").replace("%name%", user)));
			ArrayList<String> newLore = new ArrayList<>();
			for(String s: Chat.get().getLanguage().getStringList("recent.item.lore")) {
				newLore.add(Chat.get().translate(s)
						.replace("%package%", packageName)
						.replace("%name%", user)
						.replace("%date%", date)
						.replace("%price%", String.valueOf(amount))
						.replace("%currency%", currency)
						);
			}
			meta.setLore(newLore);
			item.setItemMeta(meta);
			return item;
		} catch (Exception e) {
			if (!e.getMessage().contains("Not found")) {
				Log.log(e);
			}
			return null;
		}

	}
	
	public String tgetMsg(String string) {
		return Chat.get().getLanguage().getString(string);
	}
}
