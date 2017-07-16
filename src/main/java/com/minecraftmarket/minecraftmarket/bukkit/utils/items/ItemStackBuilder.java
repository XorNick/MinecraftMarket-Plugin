package com.minecraftmarket.minecraftmarket.bukkit.utils.items;

import com.minecraftmarket.minecraftmarket.bukkit.utils.chat.Colors;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemStackBuilder {
    private final ItemStack itemStack;

    public ItemStackBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemStackBuilder(Material mat) {
        this.itemStack = new ItemStack(mat);
    }

    public ItemStackBuilder withName(String name) {
        final ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(Colors.color(name));
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder withLore(String name) {
        final ItemMeta meta = itemStack.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }
        lore.add(Colors.color(name));
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return this;
    }

    public List<String> getLore() {
        final ItemMeta meta = itemStack.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }
        return lore;
    }

    public ItemStackBuilder withItemFlag(ItemFlag flag) {
        final ItemMeta meta = itemStack.getItemMeta();
        meta.addItemFlags(flag);
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder withAmount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    public ItemStackBuilder withDurability(int durability) {
        itemStack.setDurability((short) durability);
        return this;
    }

    public ItemStackBuilder withData(int data) {
        itemStack.setDurability((short) data);
        return this;
    }

    public ItemStackBuilder withEnchantment(Enchantment enchantment, final int level) {
        itemStack.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemStackBuilder withEnchantment(Enchantment enchantment) {
        itemStack.addUnsafeEnchantment(enchantment, 1);
        return this;
    }

    public ItemStackBuilder withType(Material material) {
        if (material != null) {
            itemStack.setType(material);
        }
        return this;
    }

    public ItemStackBuilder clearLore() {
        final ItemMeta meta = itemStack.getItemMeta();
        meta.setLore(new ArrayList<>());
        itemStack.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder clearEnchantments() {
        for (Enchantment enchantment : itemStack.getEnchantments().keySet()) {
            itemStack.removeEnchantment(enchantment);
        }
        return this;
    }

    public ItemStackBuilder withOwner(String owner) {
        if (itemStack.getItemMeta() instanceof SkullMeta) {
            SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
            meta.setOwner(owner);
            itemStack.setItemMeta(meta);
        } else {
            throw new IllegalArgumentException("withOwner is only applicable for player skulls!");
        }
        return this;
    }

    public ItemStackBuilder withColor(Color color) {
        if (itemStack.getItemMeta() instanceof LeatherArmorMeta) {
            LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
            meta.setColor(color);
            itemStack.setItemMeta(meta);
        } else {
            throw new IllegalArgumentException("withColor is only applicable for leather armor!");
        }
        return this;
    }

    public ItemStack build() {
        return itemStack;
    }
}