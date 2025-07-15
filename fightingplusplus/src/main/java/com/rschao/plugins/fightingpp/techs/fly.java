package com.rschao.plugins.fightingpp.techs;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vex;
import org.bukkit.entity.AbstractArrow.PickupStatus;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.rschao.plugins.fightingpp.Plugin;
import com.rschao.plugins.fightingpp.events.awakening;
import com.rschao.plugins.fightingpp.events.events;
import com.rschao.plugins.techapi.tech.Technique;
import com.rschao.plugins.techapi.tech.cooldown.cooldownHelper;
import com.rschao.plugins.techapi.tech.feedback.hotbarMessage;
import com.rschao.plugins.techapi.tech.register.TechRegistry;

import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class fly {
    static final String fruitId = "fly";
    static final Plugin plugin = Plugin.getPlugin(Plugin.class);

    public static void Register() {
        TechRegistry.registerTechnique(fruitId, flyMeToTheMoon);
        TechRegistry.registerTechnique(fruitId, tailwind);
        TechRegistry.registerTechnique(fruitId, flyingLittleBugs);
        TechRegistry.registerTechnique(fruitId, featherFall);
        TechRegistry.registerTechnique(fruitId, ambush);
        TechRegistry.registerTechnique(fruitId, danceInTheStars);
    }

    static Technique flyMeToTheMoon = new Technique("fly_me_to_the_moon", "Fly me to the moon", false, 10000, (player, fruit, code) -> {
        Location location = player.getLocation();
        player.getWorld().spawnParticle(Particle.EXPLOSION, location, 30);
        int multiply = 4;
        Vector direction = player.getLocation().getDirection();
        player.setVelocity(direction.multiply(multiply));
        hotbarMessage.sendHotbarMessage(player, ChatColor.LIGHT_PURPLE + "You have used the Fly me to the moon technique!");
    });

    static Technique tailwind = new Technique("tailwind", "Tailwind", false, 50000, (player, fruit, code) -> {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 50 * 20, 2));
        hotbarMessage.sendHotbarMessage(player, ChatColor.AQUA + "You have used the Tailwind technique!");
    });

    static Technique flyingLittleBugs = new Technique("flying_little_bugs", "Flying little bugs", false, cooldownHelper.secondsToMiliseconds(60), (player, fruit, code) -> {
        int amnt = awakening.isFruitAwakened(player.getName(), fruitId) ? 15 : 5;
        for (int i = 0; i < amnt; i++) {
            Vex tickles = (Vex) player.getWorld().spawnEntity(player.getLocation(), org.bukkit.entity.EntityType.VEX);
            tickles.setCustomName("Flying Tickles");
            tickles.setCustomNameVisible(true);
            tickles.setGlowing(true);
            tickles.setMemory(MemoryKey.LIKED_PLAYER, player.getUniqueId());
            if (awakening.isFruitAwakened(player.getName(), fruitId)) {
                tickles.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, 4));
                tickles.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 3));
            }
            List<Player> players = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p != player && p.getLocation().distance(player.getLocation()) < 100) {
                    players.add(p);
                    break;
                }
            }
            if (players.size() > 0) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    Player target = players.get(new Random().nextInt(players.size()));
                    tickles.teleport(target.getLocation());
                }, 2);
            }
        }
        hotbarMessage.sendHotbarMessage(player, ChatColor.GREEN + "You have used the Flying little bugs technique!");
    });

    static Technique featherFall = new Technique("feather_fall", "Feather fall", false, 60000, (player, fruit, code) -> {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 5 * 20, 255));
        hotbarMessage.sendHotbarMessage(player, ChatColor.GRAY + "You have used the Feather fall technique!");
    });
    static Technique ambush = new Technique("arrows", "Astral Barrage", false, cooldownHelper.minutesToMiliseconds(3), (player, fruit, code) -> {
        Player target = chao.getClosestPlayer(player.getLocation());
        if (target == null) target = player;
        final Player finalTarget = target;
        final Location playerLoc = player.getLocation();
        final World world = player.getWorld();
        final int minArrows = 30;
        final int maxArrows = 50;
        final int durationTicks = 100;
        final int totalArrows = minArrows + (int)(Math.random() * (maxArrows - minArrows + 1));
        final double arrowsPerTick = (double)totalArrows / durationTicks;

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            new BukkitRunnable() {
                int ticks = 0;
                double arrowAccumulator = 0.0;
                @Override
                public void run() {
                    if (ticks++ >= durationTicks || !player.isOnline()) {
                        this.cancel();
                        return;
                    }
                    arrowAccumulator += arrowsPerTick;
                    int arrowsThisTick = (int)arrowAccumulator;
                    arrowAccumulator -= arrowsThisTick;
                    float yaw = playerLoc.getYaw();
                    for (int i = 0; i < arrowsThisTick; i++) {
                        // Spawn arrows in a vertical circle (ring) above the player
                        double phi = Math.random() * 2 * Math.PI; // 0 to 2PI (circle)
                        double radius = 12 + Math.random() * 10;
                        double heightAbove = 8 + Math.random() * 4; // height above player

                        // Spherical to Cartesian (with fixed theta)
                        double x = radius * Math.cos(phi);
                        double y = heightAbove;
                        double z = radius * Math.sin(phi);

                        // Rotate around Y axis by player's yaw
                        double yawRad = Math.toRadians(-yaw);
                        double rotatedX = x * Math.cos(yawRad) - z * Math.sin(yawRad);
                        double rotatedZ = x * Math.sin(yawRad) + z * Math.cos(yawRad);

                        Location spawnLoc = playerLoc.clone().add(rotatedX, y, rotatedZ);

                        Vector direction = finalTarget.getLocation().add(0, 1, 0).toVector().subtract(spawnLoc.toVector()).normalize();
                        Arrow arrow = world.spawnArrow(spawnLoc, direction, 2.5f, 0.1f);
                        arrow.setShooter(player);
                        arrow.setCritical(true);
                        arrow.setDamage(15);
                        arrow.setPickupStatus(PickupStatus.DISALLOWED);
                        arrow.setGravity(false);
                    }
                }
            }.runTaskTimer(plugin, 0L, 1L);
            hotbarMessage.sendHotbarMessage(player, ChatColor.GOLD + "Ambush Barrage unleashed!");
        }, 100L); // 5 seconds (100 ticks)
        hotbarMessage.sendHotbarMessage(player, ChatColor.YELLOW + "Ambush Barrage charging...");
    });
    static Technique danceInTheStars = new Technique("dance_in_the_stars", "Dance in the stars", true, cooldownHelper.secondsToMiliseconds(3600), (player, fruit, code) -> {
        if (!awakening.isFruitAwakened(player.getName(), fruitId)) {
            hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "You need to awaken your fruit to use this technique!");
            return;
        }
        List<Player> players = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p != player && p.getLocation().distance(player.getLocation()) < 100) {
                players.add(p);
            }
        }
        int days = (int) (Bukkit.getWorlds().get(0).getTime() / 24000);
        Bukkit.getWorlds().get(0).setTime(24000 * (days + 1) + 14000);
        for (Player p : players) {
            p.setVelocity(new Vector(0, 500, 0));
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                p.getWorld().createExplosion(p.getLocation(), 20, false, false);
                p.setVelocity(new Vector(0, 0, 0));
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    Bukkit.getWorlds().get(0).setTime(24000 * (days + 1));
                }, 5 * 20);
            }, 20);
        }
        hotbarMessage.sendHotbarMessage(player, ChatColor.BLUE + "You have used the Dance in the stars ultimate technique!");
        events.DeawakenFruit(player, fruitId);
    });
}
