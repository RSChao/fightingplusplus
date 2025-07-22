package com.rschao.plugins.fightingpp.items;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.inventory.RecipeChoice.ExactChoice;

import com.rschao.plugins.fightingpp.api.DevilFruit;

import net.md_5.bungee.api.ChatColor;

public class fruits {
    //public static ItemStack codeFruit;
    public static ItemStack fireFruit;
    public static ItemStack airFruit;
    public static ItemStack iceFruit;
    public static ItemStack lightFruit;
    public static ItemStack darkFruit;
    public static ItemStack fabriFruit;
    public static ItemStack deltaFrutita;
    public static ItemStack darioFruit;
    public static ItemStack chaoFruit;
    public static ItemStack luffyFruit;
    public static ItemStack peruFruit;
    public static ItemStack paperFruit;
    public static ItemStack chocoFruit;
    public static ItemStack awaken;
    public static ItemStack tickleFruit;
    public static ItemStack flyFruit;
    public static ItemStack dndFruit;
    public static ItemStack ganonFruit;
    public static ItemStack jevilFruit;
    public static ItemStack flowerFruit;
    public static ItemStack toñoFruit;
    public static ItemStack thegenothing;
    public static ItemStack fruitStealer;
    public static void Init(){
        fruitStealer = stealerItem();
        fireFruit = DevilFruit.makeFruit("Mera Mera", new NamespacedKey("fruit", "fire"), 1);
        fabriFruit = DevilFruit.makeFruit("Fabri Fabri", new NamespacedKey("fruit", "fabri"), 3);
        deltaFrutita = DevilFruit.makeFruit("Deruta Deruta", new NamespacedKey("fruit", "delta"), 60);
        darioFruit = DevilFruit.makeFruit("Supeedo Supeedo", new NamespacedKey("fruit", "dario"), 3);
        chaoFruit = DevilFruit.makeFruit("Katate Kami", new NamespacedKey("fruit", "chao"), 60);
        luffyFruit = DevilFruit.makeFruit("Gomu Gomu", new NamespacedKey("fruit", "gomu"), 0);
        airFruit = DevilFruit.makeFruit("Kaze Kaze", new NamespacedKey("fruit", "air"), 1);
        iceFruit = DevilFruit.makeFruit("Hie hie", new NamespacedKey("fruit", "ice"), 1);
        lightFruit = DevilFruit.makeFruit("Hikari Hikari", new NamespacedKey("fruit", "light"), 1);
        darkFruit = DevilFruit.makeFruit("Yami Yami", new NamespacedKey("fruit", "dark"), 1);
        awaken = awakenItem();
        peruFruit = DevilFruit.makeFruit("Perú Perú", new NamespacedKey("fruit", "peru"), 3);
        paperFruit = DevilFruit.makeFruit("Kami Kami", new NamespacedKey("fruit", "paper"), 2);
        chocoFruit = DevilFruit.makeFruit("Choko Choko", new NamespacedKey("fruit", "choco"), 3);
        tickleFruit = DevilFruit.makeFruit("Tikoru Tikoru", new NamespacedKey("fruit", "tickle"), 0);
        flyFruit = DevilFruit.makeFruit("Sora Sora", new NamespacedKey("fruit", "fly"), 60);
        dndFruit = DevilFruit.makeFruit("D&D", "Dice", new NamespacedKey("fruit", "dnd_dice"), 60);
        ganonFruit = DevilFruit.makeFruit("Sora Sora", new NamespacedKey("fruit", "ganon"), 2);
        jevilFruit = DevilFruit.makeFruit("Sora Sora", new NamespacedKey("fruit", "jevil"), 2);
        flowerFruit = DevilFruit.makeFruit("Sora Sora", new NamespacedKey("fruit", "flower"), 60);
        toñoFruit = DevilFruit.makeFruit("Sora Sora", new NamespacedKey("fruit", "tono"), 69);
        thegenothing = geno();


        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey("fruit", "stealer"), fruitStealer);
        recipe.shape("BAB", "ACA", "BAB");
        recipe.setIngredient('A', new ExactChoice(getAllExistingFruits()));
        recipe.setIngredient('B', Material.END_PORTAL_FRAME);
        recipe.setIngredient('C', Material.COMMAND_BLOCK);
        Bukkit.addRecipe(recipe);
    }
    public static List<ItemStack> getAllFruits() {
        List<ItemStack> list = new ArrayList<>();
        list.add(fabriFruit);
        list.add(deltaFrutita);
        list.add(darioFruit);
        list.add(chaoFruit);
        list.add(luffyFruit);
        list.add(airFruit);
        list.add(iceFruit);
        list.add(lightFruit);
        list.add(darkFruit);
        list.add(fireFruit);
        list.add(peruFruit);
        list.add(paperFruit);
        list.add(chocoFruit);
        list.add(flyFruit);
        list.add(toñoFruit);
        //list.add(tickleFruit);
        //list.add(codeFruit);
        //list.add(awaken);
        return list;
    }
    public static List<ItemStack> getAllExistingFruits() {
        List<ItemStack> list = getAllFruits();
        list.add(tickleFruit);
    
        return list;
    }
    
    
    public static ItemStack techItem(NamespacedKey id, String fruitName){
        ItemStack item = new ItemStack(Material.HEART_OF_THE_SEA);
        ItemMeta meta = item.getItemMeta();
        meta.setRarity(ItemRarity.EPIC);
        meta.setItemName("Devil Fruit Catalyst");
        List<String> lore = new ArrayList<>();
        lore.add(fruitName);
        meta.setLore(lore);
        meta.setEnchantmentGlintOverride(true);
        meta.getPersistentDataContainer().set(new NamespacedKey("fruit", "action"), PersistentDataType.BOOLEAN, true);
        meta.getPersistentDataContainer().set(id, PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);
        return item;
    }
    
    static ItemStack awakenItem(){
        ItemStack item = new ItemStack(Material.ENDER_EYE);
        ItemMeta meta = item.getItemMeta();
        meta.setRarity(ItemRarity.EPIC);
        meta.setItemName("Devil Fruit Awakener");
        meta.setEnchantmentGlintOverride(true);
        meta.getPersistentDataContainer().set(new NamespacedKey("fruit", "awaken"), PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);
        return item;
    }

    static ItemStack geno(){
        ItemStack item = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setRarity(ItemRarity.EPIC);
        meta.setItemName(ChatColor.DARK_RED + "Genocidal knife");
        meta.setEnchantmentGlintOverride(true);
        meta.setItemModel(NamespacedKey.minecraft("geno_knife"));
        meta.getPersistentDataContainer().set(new NamespacedKey("event", "geno"), PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);
        return item;
    }
    static ItemStack stealerItem(){
        ItemStack item = new ItemStack(Material.ENDER_EYE);
        ItemMeta meta = item.getItemMeta();
        meta.setRarity(ItemRarity.EPIC);
        meta.setItemName("Devil Fruit Stealing Amulet");
        meta.setEnchantmentGlintOverride(true);
        meta.getPersistentDataContainer().set(new NamespacedKey("fruit", "thief"), PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);
        return item;
    }

}
