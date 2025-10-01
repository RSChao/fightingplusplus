package com.rschao.plugins.fightingpp.api;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class DevilFruit {
    public static List<String> fruits = new ArrayList<>();
    public static ItemStack makeFruit(String fruitName, NamespacedKey id, int tier) {
        ItemStack item = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta meta = item.getItemMeta();
        meta.setItemName(fruitName + " no Mi");
        meta.setRarity(ItemRarity.EPIC);
        meta.getPersistentDataContainer().set(id, PersistentDataType.BOOLEAN, true);
        fruits.add(id.getKey());
        List<String> lore = new ArrayList<>();
        switch (tier) {
            case 1:
                lore.add("Regular tier");
                break;
            case 2:
                lore.add("Rare tier");
                break;
            case 3:
                lore.add("Epic tier");
                break;
            case 60:
                lore.add("Sacred tier");
                break;
            case 69:
                lore.add("Mean tier");
                break;
            default:
                lore.add("Unknown tier");
                break;
        }
        meta.setLore(lore);
        if(tier == 4){
            meta.setEnchantmentGlintOverride(true);
        }
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack makeFruit(String fruitName, String modelName, NamespacedKey id, int tier) {
        ItemStack item = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta meta = item.getItemMeta();
        meta.setItemName(fruitName + " no Mi model " + modelName);
        meta.setRarity(ItemRarity.EPIC);
        meta.getPersistentDataContainer().set(id, PersistentDataType.BOOLEAN, true);
        fruits.add(id.getKey());
        List<String> lore = new ArrayList<>();
        switch (tier) {
            case 1:
                lore.add("Regular tier");
                break;
            case 2:
                lore.add("Rare tier");
                break;
            case 3:
                lore.add("Epic tier");
                break;
            case 60:
                lore.add("Sacred tier");
                break;
            case 69:
                lore.add("Mean tier");
                break;
            default:
                lore.add("Unknown tier");
                break;
        }
        meta.setLore(lore);
        if(tier >= 60){
            meta.setEnchantmentGlintOverride(true);
        }
        item.setItemMeta(meta);
        return item;
    }
    //a method that returns the fruit's namespaced key
    public static NamespacedKey getFruitKey(ItemStack item) {
        return item.getItemMeta().getPersistentDataContainer().getKeys().iterator().next();
    }
    
}
