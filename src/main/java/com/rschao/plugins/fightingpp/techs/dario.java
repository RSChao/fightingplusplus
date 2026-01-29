package com.rschao.plugins.fightingpp.techs;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.rschao.plugins.fightingpp.Plugin;
import com.rschao.plugins.fightingpp.events.awakening;
import com.rschao.plugins.fightingpp.events.events;
import com.rschao.plugins.techniqueAPI.tech.Technique;
import com.rschao.plugins.techniqueAPI.tech.TechniqueMeta;
import com.rschao.plugins.techniqueAPI.tech.cooldown.cooldownHelper;
import com.rschao.plugins.techniqueAPI.tech.feedback.hotbarMessage;
import com.rschao.plugins.techniqueAPI.tech.register.TechRegistry;
import com.rschao.plugins.techniqueAPI.tech.selectors.TargetSelectors;

import net.md_5.bungee.api.ChatColor;

import java.util.List;

public class dario {
    static final String fruitId = "dario";
    static final Plugin plugin = Plugin.getPlugin(Plugin.class);

    public static void Register() {
        TechRegistry.registerTechnique(fruitId, swiftAttack);
        TechRegistry.registerTechnique(fruitId, luckySpeed);
        TechRegistry.registerTechnique(fruitId, fastHealingBlessing);
        TechRegistry.registerTechnique(fruitId, blazingDash); // Register new technique
        Plugin.registerFruitID(fruitId);
    }

    static Technique swiftAttack = new Technique(
        "swift_attack",
        "Swift Attack",
        new TechniqueMeta(false, cooldownHelper.secondsToMiliseconds(300), List.of("Swift Attack")),
        TargetSelectors.self(),
        (ctx, token) -> {
            Player player = ctx.caster();
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.getAttribute(Attribute.ATTACK_SPEED).setBaseValue(999);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    player.getAttribute(Attribute.ATTACK_SPEED).setBaseValue(4);
                }, 60 * 20);
            }, 2);
            hotbarMessage.sendHotbarMessage(player, ChatColor.AQUA + "You have used the Swift Attack technique");
        }
    );

    static Technique luckySpeed = new Technique(
        "lucky_speed",
        "Lucky speed!",
        new TechniqueMeta(false, cooldownHelper.secondsToMiliseconds(180), List.of("Lucky speed!")),
        TargetSelectors.self(),
        (ctx, token) -> {
            Player player = ctx.caster();
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
        }
    );

    static Technique fastHealingBlessing = new Technique(
        "no-geno-haha",
        "Nou moar Jénos!",
        new TechniqueMeta(true, cooldownHelper.secondsToMiliseconds(3600), List.of("Ultimate: Nou moar Jénos!")),
        TargetSelectors.self(),
        (ctx, token) -> {
            Player player = ctx.caster();
            events.hasAntiGeno.put(player.getName(), true);
            hotbarMessage.sendHotbarMessage(player, ChatColor.GOLD + "You have used the No moar Jenós! ultimate technique!");
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                events.hasAntiGeno.put(player.getName(), false);
                hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "Your No moar Jenós! ultimate technique has worn off!");
            }, 60 * 20);
        },
        (ctx, token) -> {
            Player player = ctx.caster();
            if (!awakening.isFruitAwakened(player.getName(), fruitId)) {
                hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "You need to awaken your fruit to use this technique!");
                return false;
            }
            events.DeawakenFruit(player, fruitId);
            return true;
        }
    );

    static Technique blazingDash = new Technique(
        "blazing_dash",
        "Blazing Dash",
        new TechniqueMeta(true, cooldownHelper.secondsToMiliseconds(1800), List.of("Ultimate: Blazing Dash")),
        TargetSelectors.self(),
        (ctx, token) -> {
            Player player = ctx.caster();
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 3 * 20, 3));
            hotbarMessage.sendHotbarMessage(player, ChatColor.GOLD + "You have used the Blazing Dash ultimate technique!");

            final Vector[] lastLoc = {player.getLocation().toVector()};
            final int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                Vector currentLoc = player.getLocation().toVector();
                if (!currentLoc.equals(lastLoc[0]) && player.getVelocity().length() > 0) {
                    for (Entity entity : player.getNearbyEntities(2.5, 2.5, 2.5)) {
                        if (entity instanceof LivingEntity && entity != player) {
                            int damage = com.rschao.events.events.getRNG(16, 24);
                            ((LivingEntity) entity).damage(damage, player);
                        }
                    }
                }
                lastLoc[0] = currentLoc;
            }, 0L, 2L);

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Bukkit.getScheduler().cancelTask(taskId);
            }, 3 * 20L);
        },
        (ctx, token) -> {
            Player player = ctx.caster();
            if (!awakening.isFruitAwakened(player.getName(), fruitId)) {
                hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "You need to awaken your fruit to use this technique!");
                return false;
            }
            events.DeawakenFruit(player, fruitId);
            return true;
        }
    );
}
