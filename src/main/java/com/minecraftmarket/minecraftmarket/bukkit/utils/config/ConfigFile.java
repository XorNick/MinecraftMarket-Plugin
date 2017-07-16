package com.minecraftmarket.minecraftmarket.bukkit.utils.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public abstract class ConfigFile extends File {
    protected final FileConfiguration config;

    public ConfigFile(JavaPlugin plugin, String name) {
        super(plugin.getDataFolder(), name + ".yml");

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        if (!exists()) {
            try {
                if (plugin.getResource(name + ".yml") != null) {
                    plugin.saveResource(name + ".yml", true);
                } else {
                    createNewFile();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(this);
    }

    public void saveConfig() {
        try {
            config.save(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}