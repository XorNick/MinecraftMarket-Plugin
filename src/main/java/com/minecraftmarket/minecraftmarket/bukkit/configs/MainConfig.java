package com.minecraftmarket.minecraftmarket.bukkit.configs;

import com.minecraftmarket.minecraftmarket.bukkit.utils.config.ConfigFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class MainConfig extends ConfigFile {
    private final String apiKey;
    private final int checkInterval;
    private final List<String> shopCommands;
    private final boolean useGUI;
    private final boolean useSigns;
    private final String dateFormat;
    private final String defaultHeadSkin;
    private final String lang;
    private final boolean debug;

    public MainConfig(JavaPlugin plugin) {
        super(plugin, "bukkitConfig");

        apiKey = config.getString("APIKey");
        checkInterval = config.getInt("CheckInterval");
        shopCommands = config.getStringList("ShopCommands");
        useGUI = config.getBoolean("UseGUI");
        useSigns = config.getBoolean("UseSigns");
        dateFormat = config.getString("DateFormat");
        defaultHeadSkin = config.getString("DefaultHeadSkin");
        lang = config.getString("Lang");
        debug = config.getBoolean("Debug");
    }

    public void setApiKey(String apiKey) {
        config.set("APIKey", apiKey);
        saveConfig();
    }

    public String getApiKey() {
        return apiKey;
    }

    public int getCheckInterval() {
        return checkInterval;
    }

    public List<String> getShopCommands() {
        return shopCommands;
    }

    public boolean isUseGUI() {
        return useGUI;
    }

    public boolean isUseSigns() {
        return useSigns;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public String getDefaultHeadSkin() {
        return defaultHeadSkin;
    }

    public String getLang() {
        return lang;
    }

    public boolean isDebug() {
        return debug;
    }
}