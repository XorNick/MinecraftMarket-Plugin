package com.minecraftmarket.minecraftmarket.sponge.config;

import com.minecraftmarket.minecraftmarket.sponge.utils.config.ConfigFile;

import java.io.File;

public class MainConfig extends ConfigFile {
    private final String apiKey;
    private final int checkInterval;
    private final String lang;
    private final boolean debug;

    public MainConfig(File baseDir) {
        super(baseDir, "spongeConfig");

        apiKey = config.getNode("APIKey").getString();
        checkInterval = config.getNode("CheckInterval").getInt();
        lang = config.getNode("Lang").getString();
        debug = config.getNode("Debug").getBoolean();
    }

    public void setApiKey(String apiKey) {
        config.getNode("APIKey").setValue(apiKey);
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