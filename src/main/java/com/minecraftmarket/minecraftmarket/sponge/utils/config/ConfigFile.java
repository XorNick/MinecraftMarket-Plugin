package com.minecraftmarket.minecraftmarket.sponge.utils.config;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public abstract class ConfigFile extends File {
    private final YAMLConfigurationLoader loader;
    protected ConfigurationNode config;

    public ConfigFile(File baseDir, String name) {
        super(baseDir, name + ".yml");

        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }

        if (!exists()) {
            try {
                if (getClass().getClassLoader().getResource(name + ".yml") != null) {
                    Files.copy(getClass().getClassLoader().getResourceAsStream(name + ".yml"), toPath());
                } else {
                    createNewFile();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        loader = YAMLConfigurationLoader.builder().setFile(this).build();
        try {
            config = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveConfig() {
        try {
            loader.save(config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}