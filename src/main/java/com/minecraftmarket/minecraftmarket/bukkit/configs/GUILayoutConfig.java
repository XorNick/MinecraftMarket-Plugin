package com.minecraftmarket.minecraftmarket.bukkit.configs;

import com.minecraftmarket.minecraftmarket.bukkit.utils.chat.Colors;
import com.minecraftmarket.minecraftmarket.bukkit.utils.config.ConfigFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class GUILayoutConfig extends ConfigFile {
    private final String categoryListTile;
    private final String categoryName;
    private final List<String> categoryLore;
    private final String itemListTile;
    private final String itemName;
    private final List<String> itemLore;
    private final String bottomFillItem;
    private final String bottomCloseItem;
    private final String bottomBackItem;
    private final String bottomPreviousPageItem;
    private final String bottomNextPageItem;

    public GUILayoutConfig(JavaPlugin plugin) {
        super(plugin, "guiLayout");

        categoryListTile = Colors.color(config.getString("CategoryListTitle"));
        categoryName = Colors.color(config.getString("CategoryName"));
        categoryLore = Colors.colorList(config.getStringList("CategoryLore"));
        itemListTile = Colors.color(config.getString("ItemListTitle"));
        itemName = Colors.color(config.getString("ItemName"));
        itemLore = Colors.colorList(config.getStringList("ItemLore"));
        bottomFillItem = config.getString("Bottom.FillItem");
        bottomCloseItem = config.getString("Bottom.CloseItem");
        bottomBackItem = config.getString("Bottom.BackItem");
        bottomPreviousPageItem = config.getString("Bottom.PreviousPageItem");
        bottomNextPageItem = config.getString("Bottom.NextPageItem");
    }

    public String getCategoryListTile() {
        return categoryListTile;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public List<String> getCategoryLore() {
        return categoryLore;
    }

    public String getItemListTile() {
        return itemListTile;
    }

    public String getItemName() {
        return itemName;
    }

    public List<String> getItemLore() {
        return itemLore;
    }

    public String getBottomFillItem() {
        return bottomFillItem;
    }

    public String getBottomCloseItem() {
        return bottomCloseItem;
    }

    public String getBottomBackItem() {
        return bottomBackItem;
    }

    public String getBottomPreviousPageItem() {
        return bottomPreviousPageItem;
    }

    public String getBottomNextPageItem() {
        return bottomNextPageItem;
    }
}