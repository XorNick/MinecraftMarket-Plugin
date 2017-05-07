package com.minecraftmarket.minecraftmarket.Configs;

import com.r4g3baby.pluginutils.Configs.ConfigFile;
import com.r4g3baby.pluginutils.Utils;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class MessagesConfig extends ConfigFile {
    private final String prefix;
    private final String guiCategoryTile;
    private final String guiCategoryName;
    private final List<String> guiCategoryLore;
    private final String guiItemTile;
    private final String guiItemName;
    private final List<String> guiItemLore;
    private final String guiItemUrl;
    private final List<String> signsLayout;

    public MessagesConfig(JavaPlugin plugin) {
        super(plugin, "messages");

        prefix = Utils.color(config.getString("prefix"));
        guiCategoryTile = Utils.color(config.getString("GUI.CategoryTitle"));
        guiCategoryName = Utils.color(config.getString("GUI.CategoryName"));
        guiCategoryLore = Utils.colorList(config.getStringList("GUI.CategoryLore"));
        guiItemTile = Utils.color(config.getString("GUI.ItemTitle"));
        guiItemName = Utils.color(config.getString("GUI.ItemName"));
        guiItemLore = Utils.colorList(config.getStringList("GUI.ItemLore"));
        guiItemUrl = Utils.color(config.getString("GUI.ItemUrl"));
        signsLayout = Utils.colorList(config.getStringList("SignsLayout"));
    }

    public String getPrefix() {
        return prefix;
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

    public String getGuiItemUrl() {
        return guiItemUrl;
    }

    public List<String> getSignsLayout() {
        return signsLayout;
    }
}