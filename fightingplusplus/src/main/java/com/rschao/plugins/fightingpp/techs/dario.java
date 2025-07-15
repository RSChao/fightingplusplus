package com.rschao.plugins.fightingpp.techs;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
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

public class dario {
    static final String fruitId = "dario";
    static final Plugin plugin = Plugin.getPlugin(Plugin.class);

    public static void Register() {
        TechRegistry.registerTechnique(fruitId, swiftAttack);
        TechRegistry.registerTechnique(fruitId, luckySpeed);
        TechRegistry.registerTechnique(fruitId, fastHealingBlessing);
        TechRegistry.registerTechnique(fruitId, blazingDash); // Register new technique
    }

    static Technique swiftAttack = new Technique("swift_attack", "Swift Attack", false, cooldownHelper.secondsToMiliseconds(300), (player, fruit, code) -> {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(999);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(4);
            }, 60 * 20);
        }, 2);
        hotbarMessage.sendHotbarMessage(player, ChatColor.AQUA + "You have used the Swift Attack technique");
    });

    static Technique luckySpeed = new Technique("lucky_speed", "Lucky speed!", false, cooldownHelper.secondsToMiliseconds(180), (player, fruit, code) -> {
        int rng = com.rschao.events.events.getRNG(0, 100);
        if (awakening.isFruitAwakened(player.getName(), fruitId)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60 * 20, 2));
            hotbarMessage.sendHotbarMessage(player, ChatColor.DARK_PURPLE + "You have used the Lucky speed! technique");
        } else {
            if (rng < 20) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 50 * 20, 4));
                hotbarMessage.sendHotbarMessage(player, ChatColor.DARK_PURPLE + "You failed the Lucky speed! technique");
            } else {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60 * 20, 2));
                hotbarMessage.sendHotbarMessage(player, ChatColor.DARK_PURPLE + "You have used the Lucky speed! technique");
            }
        }
    });

    static Technique fastHealingBlessing = new Technique("fast_healing_blessing", "Fast Healing Blessing", true, cooldownHelper.secondsToMiliseconds(3600), (player, fruit, code) -> {
        if (!awakening.isFruitAwakened(player.getName(), fruitId)) {
            hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "You need to awaken your fruit to use this technique!");
            return;
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 30 * 20, 9));
        hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "You have used the Fast Healing Blessing ultimate technique!");
        events.DeawakenFruit(player, fruitId);
    });

    static Technique blazingDash = new Technique("blazing_dash", "Blazing Dash", true, cooldownHelper.secondsToMiliseconds(1800), (player, fruit, code) -> {
        if (!awakening.isFruitAwakened(player.getName(), fruitId)) {
            hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "You need to awaken your fruit to use this technique!");
            return;
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 3 * 20, 3)); // Speed 4 (amplifier 3) for 3 seconds
        hotbarMessage.sendHotbarMessage(player, ChatColor.GOLD + "You have used the Blazing Dash ultimate technique!");

        final Vector[] lastLoc = {player.getLocation().toVector()};
        final int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            Vector currentLoc = player.getLocation().toVector();
            if (!currentLoc.equals(lastLoc[0]) && player.getVelocity().length() > 0) {
                // Player is moving
                
                for (Entity entity : player.getNearbyEntities(2.5, 2.5, 2.5)) {
                    if (entity instanceof LivingEntity && entity != player) {
                        int damage = com.rschao.events.events.getRNG(16, 24); // 8-12 hearts
                        ((LivingEntity) entity).damage(damage, player);
                    }
                }
            }
            lastLoc[0] = currentLoc;
        }, 0L, 2L); // Check every 2 ticks (0.1s)

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Bukkit.getScheduler().cancelTask(taskId);
        }, 3 * 20L); // Stop after 3 seconds
    });
}
