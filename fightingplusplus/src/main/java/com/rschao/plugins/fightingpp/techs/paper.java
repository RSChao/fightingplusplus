package com.rschao.plugins.fightingpp.techs;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
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

public class paper {
    static final String fruitId = "paper";
    static final Plugin plugin = Plugin.getPlugin(Plugin.class);

    public static void Register() {
        TechRegistry.registerTechnique(fruitId, paperSpawn);
        TechRegistry.registerTechnique(fruitId, healWithPaper);
        TechRegistry.registerTechnique(fruitId, paperArmor);
        TechRegistry.registerTechnique(fruitId, paperBlade);
        TechRegistry.registerTechnique(fruitId, mayhemOfPaper);
        Plugin.registerFruitID(fruitId);
    }

    static Technique paperSpawn = new Technique("paper_spawn", "Paper Spawn", false, cooldownHelper.secondsToMiliseconds(60), (player, fruit, code) -> {
        if (awakening.isFruitAwakened(player.getName(), fruitId)) {
            player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(Material.PAPER, 64));
            int rng = com.rschao.events.events.getRNG(0, 100);
            if (rng < 30) {
                player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(Material.PAPER, 64));
            }
        } else {
            player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(Material.PAPER, 32));
            int rng = com.rschao.events.events.getRNG(0, 100);
            if (rng < 30) {
                player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(Material.PAPER, 32));
            }
        }
        hotbarMessage.sendHotbarMessage(player, ChatColor.YELLOW + "You have used the Paper Spawn technique!");
    });

    static Technique healWithPaper = new Technique("heal_with_paper", "Heal with Paper", false, cooldownHelper.secondsToMiliseconds(180), (player, fruit, code) -> {
        int paperCount = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.PAPER) {
                paperCount += item.getAmount();
            }
        }
        if (paperCount >= 64) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.INSTANT_HEALTH, 1, 3));
            player.getInventory().removeItem(new ItemStack(Material.PAPER, 64));
        } else if (paperCount >= 32) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5 * 20, 4));
            player.getInventory().removeItem(new ItemStack(Material.PAPER, 32));
        }
        hotbarMessage.sendHotbarMessage(player, ChatColor.YELLOW + "You have used the Heal with Paper technique!");
    });

    static Technique paperArmor = new Technique("paper_armor", "Paper Armor", false, cooldownHelper.secondsToMiliseconds(300), (player, fruit, code) -> {
        int paperCount = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.PAPER) {
                paperCount += item.getAmount();
            }
        }
        if (paperCount >= 96) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 5 * 20, 4));
            player.getInventory().removeItem(new ItemStack(Material.PAPER, 96));
        }
        hotbarMessage.sendHotbarMessage(player, ChatColor.YELLOW + "You have used the Paper Armor technique!");
    });

    static Technique paperBlade = new Technique("paper_blade", "Paper Blade", false, cooldownHelper.secondsToMiliseconds(360), (player, fruit, code) -> {
        int paperCount = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.PAPER) {
                paperCount += item.getAmount();
            }
        }
        if (paperCount >= 128) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 30 * 20, 2));
            player.getInventory().removeItem(new ItemStack(Material.PAPER, 128));
        }
        hotbarMessage.sendHotbarMessage(player, ChatColor.YELLOW + "You have used the Paper Blade technique!");
    });

    static Technique mayhemOfPaper = new Technique("mayhem_of_paper", "Mayhem of Paper", true, cooldownHelper.secondsToMiliseconds(3600), (player, fruit, code) -> {
        if (!awakening.isFruitAwakened(player.getName(), fruitId)) {
            hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "You need to awaken your fruit to use this technique!");
            return;
        }
        int paperCount = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.PAPER) {
                paperCount += item.getAmount();
            }
        }
        final int dmg = paperCount * 2;
        if (paperCount >= 256) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 60, 255));
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.getWorld().createExplosion(player.getLocation(), dmg, true, true);
            }, 2);
            player.getInventory().removeItem(new ItemStack(Material.PAPER, paperCount));
        }
        hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "You have used the Mayhem of Paper ultimate technique!");
        events.DeawakenFruit(player, fruitId);
    });
}
