package com.rschao.plugins.fightingpp.techs;

import com.rschao.plugins.fightingpp.Plugin;
import com.rschao.plugins.techapi.tech.Technique;
import com.rschao.plugins.techapi.tech.cooldown.cooldownHelper;
import com.rschao.plugins.techapi.tech.register.TechRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class toño {
    static final String FRUIT_ID = "tono";

    static final Plugin plugin = Plugin.getPlugin(Plugin.class);
    public static void register() {
        TechRegistry.registerTechnique(FRUIT_ID, lewis);
    }

    static Technique lewis = new Technique("lewis", "Lewis de cinemática formulada", true, cooldownHelper.hour*2, (player, item, args) -> {
        Player p = player;
        Location center = p.getLocation();
        World world = p.getWorld();

        // 1. Find all players within 100 blocks (excluding p)
        List<Player> nearbyPlayers = new ArrayList<>();
        for (Player pl : world.getPlayers()) {
            if (!pl.equals(p) && pl.getLocation().distance(center) <= 100) {
                nearbyPlayers.add(pl);
            }
        }
        if (nearbyPlayers.isEmpty()) return;
        BukkitRunnable spiralTask;
        BukkitRunnable danceTask;
        BukkitRunnable noroiTask;
        int n = nearbyPlayers.size();
        double radius = 5.0;
        noroiTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : nearbyPlayers) {
                    if (p != player) {
                        Bukkit.getScheduler().runTaskLater(com.rschao.plugins.fightingpp.Plugin.getPlugin(com.rschao.plugins.fightingpp.Plugin.class), () -> {
                            double hearts = p.getAttribute(Attribute.MAX_HEALTH).getBaseValue();
                            p.getAttribute(Attribute.MAX_HEALTH).setBaseValue(hearts - 20);
                            player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 5 * 20, 5));
                            Bukkit.getScheduler().runTaskLater(com.rschao.plugins.fightingpp.Plugin.getPlugin(com.rschao.plugins.fightingpp.Plugin.class), () -> p.getAttribute(Attribute.MAX_HEALTH).setBaseValue(hearts), 60 * 20);

                        }, 5);

                    } else {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 5 * 20, 5));
                        player.damage(300);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 60 * 20, 4));
                    }
                }
            }
        };
        danceTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : nearbyPlayers) {
                    p.setVelocity(new Vector(0, 500, 0));
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        p.getWorld().createExplosion(p.getLocation(), 50, false, false);
                        p.setVelocity(new Vector(0, 0, 0));
                    }, 20);
                }
                noroiTask.runTaskLater(plugin, 40L);
            }
        };
        // 2. Teleport players in a circle around p
        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n;
            double x = center.getX() + radius * Math.cos(angle);
            double z = center.getZ() + radius * Math.sin(angle);
            Location loc = new Location(world, x, center.getY(), z);
            nearbyPlayers.get(i).teleport(loc);
        }

        // 3. Spiral movement task
        spiralTask = new BukkitRunnable() {
            double spiralRadius = radius;
            double yOffset = 0;
            int tick = 0;
            final double spiralStep = 0.2; // how much to move up per tick
            final double radiusStep = 0.1; // how much to shrink radius per tick

            @Override
            public void run() {
                tick++;
                spiralRadius = Math.max(0, spiralRadius - radiusStep);
                yOffset += spiralStep;

                // Move players in spiral
                for (int i = 0; i < n; i++) {
                    double angle = 2 * Math.PI * i / n + tick * 0.2;
                    double x = center.getX() + spiralRadius * Math.cos(angle);
                    double z = center.getZ() + spiralRadius * Math.sin(angle);
                    double y = center.getY() + yOffset;
                    Location loc = new Location(world, x, y, z);
                    nearbyPlayers.get(i).teleport(loc);
                }

                // Stop when all players are at the same block (radius ~ 0)
                if (spiralRadius <= 0.2) {
                    this.cancel();
                    danceTask.runTaskLater(plugin, 2);
                }
            }
        }; // start after 10 ticks, repeat every 2 ticks
        spiralTask.runTaskTimer(plugin, 10L, 2L);

    });
}
