package com.rschao.plugins.fightingpp.techs;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.PufferFish;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.rschao.plugins.fightingpp.Plugin;
import com.rschao.plugins.fightingpp.events.awakening;
import com.rschao.plugins.fightingpp.events.events;
import com.rschao.plugins.techapi.tech.Technique;
import com.rschao.plugins.techapi.tech.cooldown.cooldownHelper;
import com.rschao.plugins.techapi.tech.feedback.hotbarMessage;
import com.rschao.plugins.techapi.tech.register.TechRegistry;

import net.md_5.bungee.api.ChatColor;

public class peru {
    static final String fruitId = "peru";
    static final Plugin plugin = Plugin.getPlugin(Plugin.class);

    public static void Register() {
        TechRegistry.registerTechnique(fruitId, lilPoisonDarts);
        TechRegistry.registerTechnique(fruitId, annoyingGlitch);
        TechRegistry.registerTechnique(fruitId, jumpGoBig);
        TechRegistry.registerTechnique(fruitId, dontBlowMeUp);
        TechRegistry.registerTechnique(fruitId, ultimateGlitch);
    }

    static Technique lilPoisonDarts = new Technique("lil_poison_darts", "Lil' poison darts", false, 180000, (player, fruit, code) -> {
        int count = awakening.isFruitAwakened(player.getName(), fruitId) ? 6 : 4;
        for (int i = 0; i < count; i++) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.getWorld().spawn(player.getEyeLocation(), PufferFish.class);
            }, 4L * i);
        }
        hotbarMessage.sendHotbarMessage(player, "You have used the Lil' poison darts technique!");
    });

    static Technique annoyingGlitch = new Technique("annoying_glitch", "Annoying glitch", false, 240000, (player, fruit, code) -> {
        for (org.bukkit.entity.Entity entity : player.getNearbyEntities(20, 20, 20)) {
            if (entity instanceof Player) {
                Player target = (Player) entity;
                target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5 * 20, 255));
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 5 * 20, 255));
                target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 5 * 20, 255));
            }
        }
        hotbarMessage.sendHotbarMessage(player, "You have used the Annoying glitch technique!");
    });

    static Technique jumpGoBig = new Technique("jump_go_big", "Jump go big", false, 360000, (player, fruit, code) -> {
        Vector direction = new Vector(0, 20, 0);
        player.setVelocity(direction.multiply(5));
        if (awakening.isFruitAwakened(player.getName(), fruitId)) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 5 * 20, 4));
            }, 2);
        }
        hotbarMessage.sendHotbarMessage(player, "You have used the Jump go big technique!");
    });

    static Technique dontBlowMeUp = new Technique("dont_blow_me_up", "Dont blow me up", false, 300000, (player, fruit, code) -> {
        for (ItemStack item : player.getInventory().getArmorContents()) {
            if (item != null) {
                ItemMeta meta = item.getItemMeta();
                int level = awakening.isFruitAwakened(player.getName(), fruitId) ? 4 : 3;
                meta.addEnchant(Enchantment.BLAST_PROTECTION, level, true);
                item.setItemMeta(meta);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    meta.removeEnchant(Enchantment.BLAST_PROTECTION);
                    item.setItemMeta(meta);
                }, 60 * 20);
            }
        }
        hotbarMessage.sendHotbarMessage(player, "You have used the Dont blow me up technique!");
    });

    static Technique ultimateGlitch = new Technique("ultimate_glitch", "Ultimate glitch", true, cooldownHelper.secondsToMiliseconds(3600), (player, fruit, code) -> {
        if (!awakening.isFruitAwakened(player.getName(), fruitId)) {
            hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "You need to awaken your fruit to use this technique!");
            return;
        }
        for (ItemStack item : player.getInventory().getArmorContents()) {
            if (item != null) {
                ItemMeta meta = item.getItemMeta();
                meta.addEnchant(Enchantment.BLAST_PROTECTION, 4, true);
                item.setItemMeta(meta);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    meta.removeEnchant(Enchantment.BLAST_PROTECTION);
                    item.setItemMeta(meta);
                }, 60 * 20);
            }
        }
        for (org.bukkit.entity.Entity entity : player.getNearbyEntities(20, 20, 20)) {
            if (entity instanceof Player) {
                Player target = (Player) entity;
                target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5 * 20, 255));
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 5 * 20, 255));
                target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 5 * 20, 255));
                target.getAttribute(org.bukkit.attribute.Attribute.GENERIC_JUMP_STRENGTH).setBaseValue(0);
                double jumpstrength = 0.41999998697815;
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    target.getAttribute(org.bukkit.attribute.Attribute.GENERIC_JUMP_STRENGTH).setBaseValue(jumpstrength);
                }, 5 * 20);
            }
        }
        hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "You have used the Ultimate glitch ultimate technique!");
        events.DeawakenFruit(player, fruitId);
    });
}
