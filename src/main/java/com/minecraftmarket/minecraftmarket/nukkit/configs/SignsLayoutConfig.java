package com.minecraftmarket.minecraftmarket.nukkit.configs;

import cn.nukkit.plugin.PluginBase;
import com.minecraftmarket.minecraftmarket.nukkit.utils.chat.Colors;
import com.minecraftmarket.minecraftmarket.nukkit.utils.config.ConfigFile;

import java.util.List;

public class SignsLayoutConfig extends ConfigFile {
    private final List<String> activeSignsLayout;
    private final List<String> waitingSignsLayout;

    public SignsLayoutConfig(PluginBase plugin) {
        super(plugin, "signsLayout");

        activeSignsLayout = Colors.colorList(config.getStringList("SignsLayout.Active"));
        waitingSignsLayout = Colors.colorList(config.getStringList("SignsLayout.Waiting"));
    }

    public List<String> getActiveSignsLayout() {
        return activeSignsLayout;
    }

    public List<String> getWaitingSignsLayout() {
        return waitingSignsLayout;
    }
}