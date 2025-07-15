package com.rschao.plugins.fightingpp.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.rschao.plugins.fightingpp.Plugin;
import com.rschao.smp.lives.saveData;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.event.Listener;

public class extraEvents implements Listener {
    private final JavaPlugin plugin;

    public extraEvents(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    public void spiralAttack(PlayerInteractEvent event) {
        Player p = event.getPlayer();
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

        int n = nearbyPlayers.size();
        double radius = 5.0;

        // 2. Teleport players in a circle around p
        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n;
            double x = center.getX() + radius * Math.cos(angle);
            double z = center.getZ() + radius * Math.sin(angle);
            Location loc = new Location(world, x, center.getY(), z);
            nearbyPlayers.get(i).teleport(loc);
        }

        // 3. Spiral movement task
        new BukkitRunnable() {
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
                    // 4. Explosions at each player's location (not p)
                    for (Player pl : nearbyPlayers) {
                        Location loc = pl.getLocation();
                        p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 20 * 5, 255)); // resistance for 5 seconds
                        world.createExplosion(loc, 100.0F, true, true, p); // very powerful explosion
                    }
                }
            }
        }.runTaskTimer(plugin, 10L, 2L); // start after 10 ticks, repeat every 2 ticks
    }

    public void arrowSpreadAttack(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        Location center = p.getLocation();
        World world = p.getWorld();

        // 1. Teleport all players within 50 blocks (excluding p) to 5 blocks in front of p
        Vector direction = p.getLocation().getDirection().normalize();
        Location frontLoc = center.clone().add(direction.clone().multiply(5));
        for (Player pl : world.getPlayers()) {
            if (!pl.equals(p) && pl.getLocation().distance(center) <= 50) {
                pl.teleport(frontLoc);
            }
        }

        // 2. Schedule arrow spreads
        // Horizontal spread after 2 ticks
        new BukkitRunnable() {
            @Override
            public void run() {
                for (int i = -2; i <= 2; i++) {
                    Vector spread = direction.clone().rotateAroundY(i * Math.PI / 16);
                    Arrow arrow = world.spawnArrow(center.clone().add(0, 1.5, 0), spread, 2.0f, 0.1f);
                    arrow.setPierceLevel(7);
                }
            }
        }.runTaskLater(plugin, 2L);

        // Diagonal spread after 2+15=17 ticks
        new BukkitRunnable() {
            @Override
            public void run() {
                Vector up = new Vector(0, 0.2, 0);
                for (int i = -2; i <= 2; i++) {
                    Vector diag = direction.clone().rotateAroundY(i * Math.PI / 16).add(up.clone().multiply(i * 0.2));
                    Arrow arrow = world.spawnArrow(center.clone().add(0, 1.5, 0), diag, 2.0f, 0.1f);
                    arrow.setPierceLevel(7);
                }
            }
        }.runTaskLater(plugin, 17L);

        // Combined spread after 2+15+15=32 ticks
        new BukkitRunnable() {
            @Override
            public void run() {
                int xp = p.getLevel();
                //if xp == 0, set it to 1
                if (xp == 0) xp = 1;
                double baseDamage = 3.0 * xp;
                Vector up = new Vector(0, 0.2, 0);
                // Horizontal
                for (int i = -2; i <= 2; i++) {
                    Vector spread = direction.clone().rotateAroundY(i * Math.PI / 16);
                    Arrow arrow = world.spawnArrow(center.clone().add(0, 1.5, 0), spread, 2.0f, 0.1f);
                    arrow.setPierceLevel(7);
                    arrow.setDamage(baseDamage);
                }
                // Diagonal (up left/down right)
                for (int i = -2; i <= 2; i++) {
                    Vector diag = direction.clone().rotateAroundY(i * Math.PI / 16).add(up.clone().multiply(i * 0.2));
                    Arrow arrow = world.spawnArrow(center.clone().add(0, 1.5, 0), diag, 2.0f, 0.1f);
                    arrow.setPierceLevel(7);
                    arrow.setDamage(baseDamage);
                }
                // Vertical spread
                for (int i = -2; i <= 2; i++) {
                    Vector vert = direction.clone().add(new Vector(0, i * 0.2, 0));
                    Arrow arrow = world.spawnArrow(center.clone().add(0, 1.5, 0), vert, 2.0f, 0.1f);
                    arrow.setPierceLevel(7);
                    arrow.setDamage(baseDamage);
                }
            }
        }.runTaskLater(plugin, 35L);
    }
    void katakenoroi(PlayerInteractEvent event){
        Player player = event.getPlayer();
        for (Player p : Bukkit.getOnlinePlayers()) {
                                double distance = p.getLocation().distance(player.getLocation());
                                if (p != player) {
                                    Bukkit.getScheduler().runTaskLater(Plugin.getPlugin(Plugin.class), () -> {
                                        double jumpstrength = player.getAttribute(Attribute.GENERIC_JUMP_STRENGTH)
                                                .getBaseValue();
                                        double hearts = p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
                                        if (distance <= 5) {

                                            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5 * 20, 255));
                                            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 5 * 20, 255));
                                            Bukkit.getScheduler().runTaskLater(Plugin.getPlugin(Plugin.class), () -> {
                                                player.getAttribute(Attribute.GENERIC_JUMP_STRENGTH)
                                                        .setBaseValue(jumpstrength);
                                            }, 5 * 20);
                                        }
                                        if (distance <= 7) {

                                            p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(hearts - 20);
                                            Bukkit.getScheduler().runTaskLater(Plugin.getPlugin(Plugin.class), () -> {
                                                p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(hearts);
                                            }, 60 * 20);
                                        }

                                    }, 5);

                                } else {
                                    player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 5 * 20, 5));
                                    player.damage(50);
                                    com.rschao.smp.lives.lifeAPI.SubtractLife(player);
                                    double hearts = p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
                                    p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(hearts - 10);
                                    Bukkit.getScheduler().runTaskLater(Plugin.getPlugin(Plugin.class), () -> {
                                        p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(hearts);
                                    }, 60 * 20);
                                }
                            }
    }

    void godDestruction(PlayerInteractEvent ev){
        Player p = ev.getPlayer();
        
        
        new BukkitRunnable() {
            int i = 0;
            @Override
            public void run() {
                
            int lives = com.rschao.smp.lives.saveData.getLives(p);
            if(lives>10) lives = 10;
                for(Player pl: Bukkit.getOnlinePlayers()){
                    if(i > lives) {
                        saveData.SaveLives(p, 1);
                        p.sendMessage("You have used all your lives!");
                        p.setHealth(0);
                        this.cancel();
                        return;
                    }
                    Location loc = pl.getLocation();
                    p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 20 * 5, 255)); // resistance for 5 seconds
                    for(int j = 0; j < lives; j++){
                        pl.getWorld().createExplosion(loc, 100.0F, true, true, p); // very powerful explosion
                    }
                    i++;
                }
            }
        }.runTaskLater(plugin, 10);
    }
}
