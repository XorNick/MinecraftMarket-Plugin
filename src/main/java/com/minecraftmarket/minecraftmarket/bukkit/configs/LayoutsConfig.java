package com.minecraftmarket.minecraftmarket.bukkit.configs;

import com.r4g3baby.pluginutils.bukkit.Utils;
import com.r4g3baby.pluginutils.configs.BukkitConfigFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class LayoutsConfig extends BukkitConfigFile {
    private final String guiCategoryTile;
    private final String guiCategoryName;
    private final List<String> guiCategoryLore;
    private final String guiItemTile;
    private final String guiItemName;
    private final List<String> guiItemLore;
    private final List<String> activeSignsLayout;
    private final List<String> waitingSignsLayout;

    public LayoutsConfig(JavaPlugin plugin) {
        super(plugin, "layouts");

        guiCategoryTile = Utils.color(config.getString("GUI.CategoryTitle"));
        guiCategoryName = Utils.color(config.getString("GUI.CategoryName"));
        guiCategoryLore = Utils.colorList(config.getStringList("GUI.CategoryLore"));
        guiItemTile = Utils.color(config.getString("GUI.ItemTitle"));
        guiItemName = Utils.color(config.getString("GUI.ItemName"));
        guiItemLore = Utils.colorList(config.getStringList("GUI.ItemLore"));
        activeSignsLayout = Utils.colorList(config.getStringList("SignsLayout.Active"));
        waitingSignsLayout = Utils.colorList(config.getStringList("SignsLayout.Waiting"));
    }

    public String getGuiCategoryTile() {
        return guiCategoryTile;
    }

    public String getGuiCategoryName() {
        return guiCategoryName;
    }

    public List<String> getGuiCategoryLore() {
        return guiCategoryLore;
    }

    public String getGuiItemTile() {
        return guiItemTile;
    }

    public String getGuiItemName() {
        return guiItemName;
    }

    public List<String> getGuiItemLore() {
        return guiItemLore;
    }

    public List<String> getActiveSignsLayout() {
        return activeSignsLayout;
    }

    public List<String> getWaitingSignsLayout() {
        return waitingSignsLayout;
    }
}