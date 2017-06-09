package com.minecraftmarket.minecraftmarket.bukkit.Configs;

import com.r4g3baby.pluginutils.Bukkit.Utils;
import com.r4g3baby.pluginutils.Configs.BukkitConfigFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class LayoutsConfig extends BukkitConfigFile {
    private final String guiCategoryTile;
    private final String guiCategoryName;
    private final List<String> guiCategoryLore;
    private final String guiItemTile;
    private final String guiItemName;
    private final List<String> guiItemLore;
    private final List<String> signsLayout;

    public LayoutsConfig(JavaPlugin plugin) {
        super(plugin, "layouts");

        guiCategoryTile = Utils.color(config.getString("GUI.CategoryTitle"));
        guiCategoryName = Utils.color(config.getString("GUI.CategoryName"));
        guiCategoryLore = Utils.colorList(config.getStringList("GUI.CategoryLore"));
        guiItemTile = Utils.color(config.getString("GUI.ItemTitle"));
        guiItemName = Utils.color(config.getString("GUI.ItemName"));
        guiItemLore = Utils.colorList(config.getStringList("GUI.ItemLore"));
        signsLayout = Utils.colorList(config.getStringList("SignsLayout"));
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

    public List<String> getSignsLayout() {
        return signsLayout;
    }
}