package com.rschao.plugins.fightingpp.techs;

import com.rschao.plugins.fightingpp.Plugin;
import com.rschao.plugins.fightingpp.events.awakening;
import com.rschao.plugins.techapi.tech.Technique;
import com.rschao.plugins.techapi.tech.cooldown.cooldownHelper;
import com.rschao.plugins.techapi.tech.feedback.hotbarMessage;
import com.rschao.plugins.techapi.tech.register.TechRegistry;
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

import java.util.Set;

public class jevil {
    static final String ID = "jevil";

    public static void register() {
        TechRegistry.registerTechnique(ID, spade);
        Plugin.registerFruitID(ID);
        TechRegistry.registerTechnique(ID, heartbarrier);
        TechRegistry.registerTechnique(ID, diamondchain);
        TechRegistry.registerTechnique(ID, shuffle);
        TechRegistry.registerTechnique(ID, laugh);
    }

    static Technique spade = new Technique("spade", "Spade Bomb", false, cooldownHelper.minutesToMiliseconds(5), (player, item, args) -> {
        //shoot a bunch of arrows in a circle around the player
        for (int i = 0; i < 360; i += 5) {
            double angle = Math.toRadians(i);
            double x = Math.cos(angle) * 5; // Radius of the circle
            double z = Math.sin(angle) * 5; // Radius of the circle
            Vector direction = player.getLocation().getDirection().normalize();
            direction.setX(direction.getX() + x);
            direction.setZ(direction.getZ() + z);
            // Spawn the arrow at the player's location, slightly above ground
            Arrow arrow = player.getWorld().spawnArrow(player.getLocation().add(x, 1, z),
                    direction, 1.0f, 12.0f);
            arrow.setDamage(20);
            arrow.setVelocity(direction.multiply(5));

        }
        hotbarMessage.sendHotbarMessage(player, "You have used the Spade Bomb technique!");
    });

    static Technique heartbarrier = new Technique("heartbarrier", "Heart Barrier", false, cooldownHelper.minutesToMiliseconds(1), (player, item, args) -> {
        // Create a barrier around the player
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
    });
    static Technique diamondchain = new Technique("diamondchain", "Diamond Chain", false, cooldownHelper.minutesToMiliseconds(5), (player, item, args) -> {
        // Create a chain of diamond blocks around the player
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
    });
    static Technique shuffle = new Technique("shuffle", "Houdini's Shuffle", false, cooldownHelper.secondsToMiliseconds(180), (player, fruit, code) -> {

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
    });
    static Technique laugh = new Technique("laugh", "Jevil's Laugh", true, cooldownHelper.hour / 2, (player, fruit, code) -> {
        if (!awakening.isFruitAwakened(player.getName(), ID)) {
            hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "You need to awaken your fruit to use this technique!");
            return;
        }
        for (org.bukkit.entity.Entity entity : player.getNearbyEntities(20, 20, 20)) {
            if (entity instanceof Player) {
                Player target = (Player) entity;
                target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5 * 20, 255));
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 5 * 20, 255));
                target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 5 * 20, 255));
                target.getAttribute(org.bukkit.attribute.Attribute.JUMP_STRENGTH).setBaseValue(0);
                double jumpstrength = 0.41999998697815;
                Bukkit.getScheduler().runTaskLater(Plugin.getPlugin(Plugin.class), () -> {
                    target.getAttribute(org.bukkit.attribute.Attribute.JUMP_STRENGTH).setBaseValue(jumpstrength);
                    target.damage(300);
                }, 5 * 20);
            }
        }
        hotbarMessage.sendHotbarMessage(player, ChatColor.DARK_GRAY + "You have used the Jevil's Laugh technique");
    });
}
