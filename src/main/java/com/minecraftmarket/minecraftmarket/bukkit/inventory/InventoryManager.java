package com.minecraftmarket.minecraftmarket.bukkit.inventory;

import com.minecraftmarket.minecraftmarket.bukkit.MCMarket;
import com.minecraftmarket.minecraftmarket.bukkit.configs.LayoutsConfig;
import com.minecraftmarket.minecraftmarket.bukkit.utils.chat.Colors;
import com.minecraftmarket.minecraftmarket.bukkit.utils.inventories.InventoryGUI;
import com.minecraftmarket.minecraftmarket.bukkit.utils.items.ItemStackBuilder;
import com.minecraftmarket.minecraftmarket.common.api.MCMarketApi;
import com.minecraftmarket.minecraftmarket.common.i18n.I18n;
import com.minecraftmarket.minecraftmarket.common.utils.Utils;
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
            if (plugin.isAuthenticated()) {
                List<MCMarketApi.Category> categories = plugin.getApi().getCategories();
                mainMenu = new InventoryGUI(layoutsConfig.getGuiCategoryTile(), Utils.roundUp(categories.size(), 9), true);
                for (MCMarketApi.Category category : categories) {
                    InventoryGUI invCat = new InventoryGUI(replaceVars(layoutsConfig.getGuiItemTile(), category, null), Utils.roundUp(category.getItems().size(), 9), true);
                    for (MCMarketApi.Item item : category.getItems()) {
                        ItemStackBuilder iconItem = null;
                        if (item.getIcon().contains(":")) {
                            String[] splitedIcon = item.getIcon().split(":");
                            if (Utils.isInt(splitedIcon[0]) && Utils.isInt(splitedIcon[1])) {
                                Material icon = Material.matchMaterial(splitedIcon[0]);
                                if (icon != null) {
                                    iconItem = new ItemStackBuilder(icon).withData(Utils.getInt(splitedIcon[1]));
                                }
                            }
                        } else {
                            Material icon = Material.matchMaterial(category.getIcon());
                            if (icon != null) {
                                iconItem = new ItemStackBuilder(icon);
                            }
                        }
                        if (iconItem == null) iconItem = new ItemStackBuilder(Material.CHEST);
                        iconItem.withName(replaceVars(layoutsConfig.getGuiItemName(), category, item));
                        for (String lines : layoutsConfig.getGuiItemLore()) {
                            for (String line : replaceVars(lines, category, item).split("\r\n")) {
                                iconItem.withLore(line);
                            }
                        }
                        invCat.addItem(iconItem.build(), (player, slot, itemStack, clickType) -> {
                            player.sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("gui_item_url", item.getUrl())));
                            return true;
                        });
                    }
                    inventories.put(category.getId(), invCat);

                    ItemStackBuilder catItem = null;
                    if (category.getIcon().contains(":")) {
                        String[] splitedIcon = category.getIcon().split(":");
                        if (Utils.isInt(splitedIcon[0]) && Utils.isInt(splitedIcon[1])) {
                            Material icon = Material.matchMaterial(splitedIcon[0]);
                            if (icon != null) {
                                catItem = new ItemStackBuilder(icon).withData(Utils.getInt(splitedIcon[1]));
                            }
                        }
                    } else {
                        Material icon = Material.matchMaterial(category.getIcon());
                        if (icon != null) {
                            catItem = new ItemStackBuilder(icon);
                        }
                    }
                    if (catItem == null) catItem = new ItemStackBuilder(Material.ENDER_CHEST);
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
        });
    }

    public void open(Player player) {
        if (plugin.isAuthenticated()) {
            mainMenu.open(player);
        } else {
            player.sendMessage(Colors.color(I18n.tl("prefix") + " " + I18n.tl("cmd_auth_key")));
        }
    }

    private String replaceVars(String msg, MCMarketApi.Category category, MCMarketApi.Item item) {
        if (category != null) {
            msg = msg.replace("{category_id}", "" + category.getId())
                    .replace("{category_name}", category.getName());
        }
        if (item != null) {
            msg = msg.replace("{item_id}", "" + item.getId())
                    .replace("{item_name}", item.getName())
                    .replace("{item_desc}", item.getDescription())
                    .replace("{item_url}", item.getUrl())
                    .replace("{item_price}", item.getPrice())
                    .replace("{item_currency}", item.getCurrency())
                    .replace("{item_category}", item.getCategory())
                    .replace("{item_categoryid}", "" + item.getCategoryID());
        }
        return msg;
    }
}