package com.minecraftmarket.minecraftmarket.bukkit.configs;

import com.minecraftmarket.minecraftmarket.bukkit.utils.chat.Colors;
import com.minecraftmarket.minecraftmarket.bukkit.utils.config.ConfigFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class SignsLayoutConfig extends ConfigFile {
    private final List<String> activeLayout;
    private final List<String> waitingLayout;

    public SignsLayoutConfig(JavaPlugin plugin) {
        super(plugin, "signsLayout");

        activeLayout = Colors.colorList(config.getStringList("Active"));
        waitingLayout = Colors.colorList(config.getStringList("Waiting"));
    }

    public List<String> getActiveLayout() {
        return activeLayout;
    }

    public List<String> getWaitingLayout() {
        return waitingLayout;
    }
}