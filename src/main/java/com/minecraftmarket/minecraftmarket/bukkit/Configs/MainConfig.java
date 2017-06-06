package com.minecraftmarket.minecraftmarket.bukkit.Configs;

import com.r4g3baby.pluginutils.Configs.BukkitConfigFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class MainConfig extends BukkitConfigFile {
    private final String apiKey;
    private final int checkInterval;
    private final List<String> shopCommands;
    private final boolean useGUI;
    private final boolean useSigns;
    private final String defaultHeadSkin;
    private final boolean debug;

    public MainConfig(JavaPlugin plugin) {
        super(plugin, "bukkitConfig");

        apiKey = config.getString("APIKey");
        checkInterval = config.getInt("CheckInterval");
        shopCommands = config.getStringList("ShopCommands");
        useGUI = config.getBoolean("UseGUI");
        useSigns = config.getBoolean("UseSigns");
        defaultHeadSkin = config.getString("DefaultHeadSkin");
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

    public String getDefaultHeadSkin() {
        return defaultHeadSkin;
    }

    public boolean isDebug() {
        return debug;
    }
}