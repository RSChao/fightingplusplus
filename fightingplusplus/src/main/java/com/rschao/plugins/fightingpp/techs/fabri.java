package com.rschao.plugins.fightingpp.techs;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.rschao.plugins.fightingpp.Plugin;
import com.rschao.plugins.fightingpp.events.awakening;
import com.rschao.plugins.fightingpp.events.events;
import com.rschao.plugins.techapi.tech.Technique;
import com.rschao.plugins.techapi.tech.cooldown.cooldownHelper;
import com.rschao.plugins.techapi.tech.feedback.hotbarMessage;
import com.rschao.plugins.techapi.tech.register.TechRegistry;

import net.md_5.bungee.api.ChatColor;

public class fabri {
    static final String fruitId = "fabri";
    static final Plugin plugin = Plugin.getPlugin(Plugin.class);

    public static void Register() {
        TechRegistry.registerTechnique(fruitId, freePearl);
        TechRegistry.registerTechnique(fruitId, infiniteCrystals);
        TechRegistry.registerTechnique(fruitId, obsidianFarm);
        TechRegistry.registerTechnique(fruitId, armorHaki);
        TechRegistry.registerTechnique(fruitId, immolation);
        Plugin.registerFruitID(fruitId);
    }

    static Technique freePearl = new Technique("free_pearl", "Free pearl", false, cooldownHelper.secondsToMiliseconds(60), (player, fruit, code) -> {
        player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 64));
        hotbarMessage.sendHotbarMessage(player, ChatColor.DARK_GREEN + "You have used the Free Pearl technique");
    });

    static Technique infiniteCrystals = new Technique("infinite_crystals", "Infinite Crystals", false, cooldownHelper.secondsToMiliseconds(180), (player, fruit, code) -> {
        player.getInventory().addItem(new ItemStack(Material.END_CRYSTAL, 64));
        hotbarMessage.sendHotbarMessage(player, ChatColor.DARK_PURPLE + "You have used the Infinite Crystal technique");
    });

    static Technique obsidianFarm = new Technique("obsidian_farm", "Obsidian Farm", false, cooldownHelper.secondsToMiliseconds(180), (player, fruit, code) -> {
        player.getInventory().addItem(new ItemStack(Material.OBSIDIAN, 64));
        hotbarMessage.sendHotbarMessage(player, ChatColor.DARK_GRAY + "You have used the Obsidian Farm technique");
    });
    static Technique armorHaki = new Technique("armor_haki", "Armor Haki", false, cooldownHelper.secondsToMiliseconds(300), (player, fruit, code) -> {
        String playerName = player.getName();
        ItemStack[] armorContents = player.getInventory().getArmorContents();
        if (awakening.isFruitAwakened(playerName, fruitId)) {
            for (ItemStack item : armorContents) {
                if (item != null && item.getItemMeta() instanceof Damageable) {
                    Damageable meta = (Damageable) item.getItemMeta();
                    meta.setDamage(0);
                    item.setItemMeta(meta);
                }
            }
            armorContents = player.getInventory().getContents();
        }
        for (ItemStack item : armorContents) {
            if (item != null && item.getItemMeta() instanceof Damageable) {
                Damageable meta = (Damageable) item.getItemMeta();
                meta.setDamage(0);
                item.setItemMeta(meta);
            }
        }
        hotbarMessage.sendHotbarMessage(player, ChatColor.DARK_PURPLE + "You have used the Armor Haki technique");
    });

    static Technique immolation = new Technique("immolation", "Immolation", true, cooldownHelper.secondsToMiliseconds(3600), (player, fruit, code) -> {
        if (!awakening.isFruitAwakened(player.getName(), fruitId)) {
            hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "You need to awaken your fruit to use this technique!");
            return;
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 5, 255));
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            player.getWorld().createExplosion(player.getLocation(), 50.0F, true, true);
        }, 2);
        hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "You have used the Immolation ultimate technique!");
        events.DeawakenFruit(player, fruitId);
    });
}
