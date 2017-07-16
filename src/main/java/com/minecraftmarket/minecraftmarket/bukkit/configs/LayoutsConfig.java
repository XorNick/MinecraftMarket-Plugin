package com.minecraftmarket.minecraftmarket.bukkit.configs;

import com.minecraftmarket.minecraftmarket.bukkit.utils.chat.Colors;
import com.minecraftmarket.minecraftmarket.bukkit.utils.config.ConfigFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class LayoutsConfig extends ConfigFile {
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

        guiCategoryTile = Colors.color(config.getString("GUI.CategoryTitle"));
        guiCategoryName = Colors.color(config.getString("GUI.CategoryName"));
        guiCategoryLore = Colors.colorList(config.getStringList("GUI.CategoryLore"));
        guiItemTile = Colors.color(config.getString("GUI.ItemTitle"));
        guiItemName = Colors.color(config.getString("GUI.ItemName"));
        guiItemLore = Colors.colorList(config.getStringList("GUI.ItemLore"));
        activeSignsLayout = Colors.colorList(config.getStringList("SignsLayout.Active"));
        waitingSignsLayout = Colors.colorList(config.getStringList("SignsLayout.Waiting"));
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