package com.minecraftmarket.minecraftmarket.nukkit.utils.config;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;

import java.io.File;
import java.io.IOException;

public abstract class ConfigFile extends File {
    protected final Config config;

    public ConfigFile(PluginBase plugin, String name) {
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

        config = new Config(this, Config.YAML);
    }

    public void saveConfig() {
        config.save();
    }
}