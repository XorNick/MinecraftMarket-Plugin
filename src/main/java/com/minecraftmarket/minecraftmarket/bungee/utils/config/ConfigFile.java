package com.minecraftmarket.minecraftmarket.bungee.utils.config;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public abstract class ConfigFile extends File {
    protected Configuration config;

    public ConfigFile(Plugin plugin, String name) {
        super(plugin.getDataFolder(), name + ".yml");

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        if (!exists()) {
            try {
                if (plugin.getResourceAsStream(name + ".yml") != null) {
                    Files.copy(plugin.getResourceAsStream(name + ".yml"), toPath());
                } else {
                    createNewFile();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}