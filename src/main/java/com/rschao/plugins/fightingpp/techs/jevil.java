package com.rschao.plugins.fightingpp.techs;

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
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Set;

public class jevil {
    static final String ID = "jevil";
    static final Plugin plugin = Plugin.getPlugin(Plugin.class);

    public static void register() {
        TechRegistry.registerTechnique(ID, spade);
        TechRegistry.registerTechnique(ID, heartbarrier);
        TechRegistry.registerTechnique(ID, diamondchain);
        TechRegistry.registerTechnique(ID, shuffle);
        TechRegistry.registerTechnique(ID, laugh);
        Plugin.registerFruitID(ID);
    }

    static Technique spade = new Technique(
            "spade",
            "Spade Bomb",
            new TechniqueMeta(false, cooldownHelper.minutesToMiliseconds(5), List.of("Spade Bomb")),
            TargetSelectors.self(),
            (ctx, token) -> {
                Player player = ctx.caster();
                for (int i = 0; i < 360; i += 5) {
                    double angle = Math.toRadians(i);
                    double x = Math.cos(angle) * 5;
                    double z = Math.sin(angle) * 5;
                    Vector direction = player.getLocation().getDirection().normalize();
                    direction.setX(direction.getX() + x);
                    direction.setZ(direction.getZ() + z);
                    Arrow arrow = player.getWorld().spawnArrow(player.getLocation().add(x, 1, z), direction, 1.0f, 12.0f);
                    arrow.setDamage(20);
                    arrow.setVelocity(direction.multiply(5));
                }
                hotbarMessage.sendHotbarMessage(player, "You have used the Spade Bomb technique!");
            }
    );

    static Technique heartbarrier = new Technique(
            "heartbarrier",
            "Heart Barrier",
            new TechniqueMeta(false, cooldownHelper.minutesToMiliseconds(1), List.of("Heart Barrier")),
            TargetSelectors.self(),
            (ctx, token) -> {
                Player player = ctx.caster();
                Set<Block> sphere = chao.sphereAround(player.getLocation(), 5);
                for (Block b : sphere) {
                    if (b.getType().equals(Material.AIR)) {
                        b.setType(Material.RED_STAINED_GLASS);
                        Bukkit.getScheduler().runTaskLater(Plugin.getPlugin(Plugin.class), () -> {
                            b.setType(Material.AIR);
                        }, 60L);
                    }
                }
                hotbarMessage.sendHotbarMessage(player, "You have used the Heart Barrier technique!");
            }
    );

    static Technique diamondchain = new Technique(
            "diamondchain",
            "Diamond Chain",
            new TechniqueMeta(false, cooldownHelper.minutesToMiliseconds(5), List.of("Diamond Chain")),
            TargetSelectors.self(),
            (ctx, token) -> {
                Player player = ctx.caster();
                Player closestPlayer = chao.getClosestPlayer(player.getLocation());
                if (closestPlayer != null) {
                    closestPlayer.damage(20);
                    for (int i = 0; i < 100; i++) {
                        int t = i % 5;
                        Bukkit.getScheduler().runTaskLater(Plugin.getPlugin(Plugin.class), () -> {
                            Vector direction = player.getEyeLocation().getDirection().normalize().multiply(20);
                            Location targetLocation = player.getEyeLocation().add(direction);
                            closestPlayer.teleport(targetLocation);
                            if (t == 0) {
                                closestPlayer.damage(10);
                            }
                        }, i);
                    }
                    hotbarMessage.sendHotbarMessage(player, "You have used the Diamond Chain technique!");
                } else {
                    player.sendMessage("No players nearby to launch.");
                }
            }
    );

    static Technique shuffle = new Technique(
            "shuffle",
            "Houdini's Shuffle",
            new TechniqueMeta(false, cooldownHelper.secondsToMiliseconds(180), List.of("Houdini's Shuffle")),
            TargetSelectors.self(),
            (ctx, token) -> {
                Player player = ctx.caster();
                Location loc = player.getLocation().clone();
                loc.add(com.rschao.events.events.getRNG(-4, 4), 0, com.rschao.events.events.getRNG(-4, 4));
                player.addPotionEffect(new PotionEffect(PotionEffectType.INSTANT_HEALTH, 1, 3));
                player.teleport(loc);
                for (org.bukkit.entity.Entity entity : player.getNearbyEntities(10, 10, 10)) {
                    if (entity instanceof Player) {
                        Player target = (Player) entity;
                        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 2 * 20, 255));
                    }
                }
                hotbarMessage.sendHotbarMessage(player, ChatColor.DARK_GRAY + "You have used the Houdini's Shuffle technique");
            }
    );

    static Technique laugh = new Technique(
            "laugh",
            "Jevil's Laugh",
            new TechniqueMeta(true, cooldownHelper.hour / 2, List.of("Ultimate: Jevil's Laugh")),
            TargetSelectors.self(),
            (ctx, token) -> {
                Player player = ctx.caster();
                for (org.bukkit.entity.Entity entity : player.getNearbyEntities(20, 20, 20)) {
                    if (entity instanceof Player) {
                        Player target = (Player) entity;
                        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5 * 20, 255));
                        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 5 * 20, 255));
                        target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 5 * 20, 255));
                        try {
                            target.getAttribute(org.bukkit.attribute.Attribute.JUMP_STRENGTH).setBaseValue(0);
                        } catch (Throwable ignored) {}
                        double jumpstrength = 0.41999998697815;
                        Bukkit.getScheduler().runTaskLater(Plugin.getPlugin(Plugin.class), () -> {
                            try {
                                target.getAttribute(org.bukkit.attribute.Attribute.JUMP_STRENGTH).setBaseValue(jumpstrength);
                            } catch (Throwable ignored) {}
                            target.damage(300);
                        }, 5 * 20);
                    }
                }
                hotbarMessage.sendHotbarMessage(player, ChatColor.DARK_GRAY + "You have used the Jevil's Laugh technique");
            },
            (ctx, token) -> {
                Player player = ctx.caster();
                if (!awakening.isFruitAwakened(player.getName(), ID)) {
                    hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "You need to awaken your fruit to use this technique!");
                    return false;
                }
                events.DeawakenFruit(player, ID);
                return true;
            }
    );
}
