package com.minecraftmarket.minecraftmarket.bukkit.utils.inventories;

import com.minecraftmarket.minecraftmarket.bukkit.utils.chat.Colors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class InventoryGUI {
    private static final Map<Inventory, InventoryGUI> inventories = new HashMap<>();
    private final Map<Integer, ItemClick> items = new HashMap<>();
    private Inventory inventory;
    private boolean cancelClick;
    private boolean listed;

    public InventoryGUI(String name, int size) {
        this(name, size, false);
    }

    public InventoryGUI(String name, int size, boolean cancelClick) {
        if (size < 9) {
            size = 9;
        }
        this.inventory = Bukkit.createInventory(null, size, Colors.color(name));
        this.cancelClick = cancelClick;
        this.listed = false;
    }

    public void open(Player... players) {
        addToList();
        for (Player player : players) {
            player.openInventory(inventory);
        }
    }

    public void addItem(ItemStack item) {
        if (inventory.firstEmpty() >= 0) {
            setItem(inventory.firstEmpty(), item);
        }
    }

    public void addItem(ItemStack item, ItemClick itemClick) {
        if (inventory.firstEmpty() >= 0) {
            setItem(inventory.firstEmpty(), item, itemClick);
        }
    }

    public void setItem(int slot, ItemStack item) {
        setItem(slot, item, null);
    }

    public void setItem(int slot, ItemStack item, ItemClick itemClick) {
        if (items.containsKey(slot)) {
            items.remove(slot);
        }
        inventory.setItem(slot, item);
        if (itemClick != null) {
            items.put(slot, itemClick);
        }
    }

    public void removeItem(int slot) {
        inventory.setItem(slot, new ItemStack(Material.AIR));
        if (items.containsKey(slot)) {
            items.remove(slot);
        }
    }

    public ItemStack getItem(int slot) {
        return inventory.getItem(slot);
    }

    public void clear() {
        inventory.clear();
    }

    public static Listener getListener() {
        return new Listener() {
            @EventHandler
            public void onInventoryClickEvent(InventoryClickEvent e) {
                if (inventories.containsKey(e.getClickedInventory())) {
                    InventoryGUI current = inventories.get(e.getClickedInventory());
                    if (current.cancelClick) {
                        e.setCancelled(true);
                    }
                    if (current.items.containsKey(e.getSlot())) {
                        e.setCancelled(current.items.get(e.getSlot()).onClick((Player) e.getWhoClicked(), e.getSlot(), e.getCurrentItem(), e.getClick()));
                    }
                }
            }

            @EventHandler
            public void onInventoryCloseEvent(InventoryCloseEvent e) {
                if (inventories.containsKey(e.getInventory())) {
                    if ((e.getInventory().getViewers().size() - 1) <= 0) {
                        InventoryGUI current = inventories.get(e.getInventory());
                        current.removeFromList();
                    }
                }
            }
        };
    }

    private void addToList() {
        if (!listed) {
            inventories.put(inventory, this);
            listed = true;
        }
    }

    private void removeFromList() {
        if (listed) {
            inventories.remove(inventory);
            listed = false;
        }
    }

    public interface ItemClick {
        boolean onClick(Player player, int slot, ItemStack item, ClickType clickType);
    }
}