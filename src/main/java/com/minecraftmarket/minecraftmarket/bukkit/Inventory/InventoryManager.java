package com.minecraftmarket.minecraftmarket.bukkit.Inventory;

import com.minecraftmarket.minecraftmarket.bukkit.Configs.LayoutsConfig;
import com.minecraftmarket.minecraftmarket.bukkit.MCMarket;
import com.minecraftmarket.minecraftmarket.core.I18n;
import com.minecraftmarket.minecraftmarket.core.MCMApi;
import com.r4g3baby.pluginutils.Bukkit.Utils;
import com.r4g3baby.pluginutils.Inventory.InventoryGUI;
import com.r4g3baby.pluginutils.Items.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryManager {
    private final Map<Long, InventoryGUI> inventories = new HashMap<>();
    private final MCMarket plugin;
    private final LayoutsConfig layoutsConfig;
    private InventoryGUI mainMenu;

    public InventoryManager(MCMarket plugin) {
        this.plugin = plugin;
        this.layoutsConfig = plugin.getLayoutsConfig();
        load();
    }

    public void load() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                if (plugin.isAuthenticated()) {
                    List<MCMApi.Category> categories = plugin.getApi().getCategories();
                    mainMenu = new InventoryGUI(layoutsConfig.getGuiCategoryTile(), Utils.roundUp(categories.size(), 9), true);
                    for (MCMApi.Category category : categories) {
                        InventoryGUI invCat = new InventoryGUI(replaceVars(layoutsConfig.getGuiItemTile(), category, null), Utils.roundUp(category.getItems().size(), 9), true);
                        for (MCMApi.Item item : category.getItems()) {
                            ItemStackBuilder iconItem;
                            if (item.getIcon().contains(":")) {
                                String[] splitedIcon = item.getIcon().split(":");
                                if (Utils.isInt(splitedIcon[0]) && Utils.isInt(splitedIcon[1])) {
                                    iconItem = new ItemStackBuilder(Material.matchMaterial(splitedIcon[0])).withData(Utils.getInt(splitedIcon[1]));
                                } else {
                                    iconItem = new ItemStackBuilder(Material.POTATO_ITEM);
                                }
                            } else {
                                iconItem = new ItemStackBuilder(Material.matchMaterial(item.getIcon()));
                            }
                            iconItem.withName(replaceVars(layoutsConfig.getGuiItemName(), category, item));
                            for (String lines : layoutsConfig.getGuiItemLore()) {
                                for (String line : replaceVars(lines, category, item).split("\r\n")) {
                                    iconItem.withLore(line);
                                }
                            }
                            invCat.addItem(iconItem.build(), (player, slot, itemStack, clickType) -> {
                                player.sendMessage(Utils.color(replaceVars(I18n.tl("prefix") + " " + I18n.tl("gui.itemUrl"), category, item)));
                                return true;
                            });
                        }
                        inventories.put(category.getId(), invCat);

                        ItemStackBuilder catItem;
                        if (category.getIcon().contains(":")) {
                            String[] splitedIcon = category.getIcon().split(":");
                            if (Utils.isInt(splitedIcon[0]) && Utils.isInt(splitedIcon[1])) {
                                catItem = new ItemStackBuilder(Material.matchMaterial(splitedIcon[0])).withData(Utils.getInt(splitedIcon[1]));
                            } else {
                                catItem = new ItemStackBuilder(Material.POTATO_ITEM);
                            }
                        } else {
                            catItem = new ItemStackBuilder(Material.matchMaterial(category.getIcon()));
                        }
                        catItem.withName(replaceVars(layoutsConfig.getGuiCategoryName(), category, null));
                        for (String lines : layoutsConfig.getGuiCategoryLore()) {
                            for (String line : replaceVars(lines, category, null).split("\r\n")) {
                                catItem.withLore(line);
                            }
                        }
                        mainMenu.addItem(catItem.build(), (player, slot, item, clickType) -> {
                            inventories.get(category.getId()).open((Player) player);
                            return true;
                        });
                    }
                } else {
                    mainMenu = new InventoryGUI(layoutsConfig.getGuiCategoryTile(), 9, true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void open(Player player) {
        if (plugin.isAuthenticated()) {
            mainMenu.open(player);
        } else {
            player.sendMessage(Utils.color(I18n.tl("prefix") + " " + I18n.tl("cmd.authKey")));
        }
    }

    private String replaceVars(String msg, MCMApi.Category category, MCMApi.Item item) {
        if (category != null) {
            msg = msg.replace("{category_id}", "" + category.getId());
            msg = msg.replace("{category_name}", category.getName());
        }
        if (item != null) {
            msg = msg.replace("{item_id}", "" + item.getId());
            msg = msg.replace("{item_name}", item.getName());
            msg = msg.replace("{item_desc}", item.getDescription());
            msg = msg.replace("{item_url}", item.getUrl());
            msg = msg.replace("{item_price}", item.getPrice());
            msg = msg.replace("{item_currency}", item.getCurrency());
            msg = msg.replace("{item_category}", item.getCategory());
            msg = msg.replace("{item_categoryid}", "" + item.getCategoryID());
        }
        return msg;
    }
}