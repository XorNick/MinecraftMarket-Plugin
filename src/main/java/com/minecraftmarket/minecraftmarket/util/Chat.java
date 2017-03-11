package com.minecraftmarket.minecraftmarket.util;

import java.io.File;
import java.util.logging.Level;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class Chat {

	private Chat() {
	}

	static Chat instance;


	File langFile;
	FileConfiguration lang;

	public String prefix = "";

	public void SetupDefaultLanguage() {
		reloadLanguage();
		prefix = ChatColor.translateAlternateColorCodes('&', getLanguage().getString("messages.prefix"));
		if (!prefix.endsWith(" ")) prefix += " ";

	}

	public FileConfiguration getLanguage() {
		return Settings.get().getLanguageFile();
	}

	public void reloadLanguage() {
		Settings.get().reloadLanguageConfig();
	}

	public String translate(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}

	public String getMsg(String string) {
		if(getLanguage().isSet(string)) {
			return translate(getLanguage().getString(string));
		} else {
			Log.log(Level.SEVERE, "Could not find '" + string + "' in 'language.yml'.");
			return "";
		}
	}

	public static String center(String str) {
		return StringUtils.stripEnd(StringUtils.center(str, 80), " ");
	}


	public static Chat get() {
		if (instance == null) instance = new Chat();
		return instance;
	}

}
