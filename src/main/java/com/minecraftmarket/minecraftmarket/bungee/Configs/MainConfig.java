package com.minecraftmarket.minecraftmarket.bungee.Configs;

import com.r4g3baby.pluginutils.Configs.BungeeConfigFile;
import net.md_5.bungee.api.plugin.Plugin;

public class MainConfig extends BungeeConfigFile {
    private final String apiKey;
    private final int checkInterval;
    private final String lang;
    private final boolean debug;

    public MainConfig(Plugin plugin) {
        super(plugin, "bungeeConfig");

        apiKey = config.getString("APIKey");
        checkInterval = config.getInt("CheckInterval");
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

    public String getLang() {
        return lang;
    }

    public boolean isDebug() {
        return debug;
    }
}