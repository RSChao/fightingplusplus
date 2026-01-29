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
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vex;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffectTypeCategory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

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
        TechRegistry.registerTechnique(fruitId, ultimateCombo);
        Plugin.registerFruitID(fruitId);
    }

    static Technique flyMeToTheMoon = new Technique(
            "fly_me_to_the_moon",
            "Fly me to the moon",
            new TechniqueMeta(false, 10000, List.of("Propulsa al jugador")),
            TargetSelectors.self(),
            (ctx, token) -> {
                Player player = ctx.caster();
                if (player.isGliding()) {
                    EnableProFlight(player);
                } else {
                    Location location = player.getLocation();
                    player.getWorld().spawnParticle(Particle.EXPLOSION, location, 30);
                    int multiply = 4;
                    Vector direction = player.getLocation().getDirection();
                    player.setVelocity(direction.multiply(multiply));
                }
                hotbarMessage.sendHotbarMessage(player, ChatColor.LIGHT_PURPLE + "You have used the Fly me to the moon technique!");
            }
    );

    public static void EnableProFlight(Player player) {
        net.kryspin.Plugin pl = net.kryspin.Plugin.getPlugin(net.kryspin.Plugin.class);
        pl.enableProFlight(player);
    }

    static Technique tailwind = new Technique(
            "tailwind",
            "Tailwind",
            new TechniqueMeta(false, 50000, List.of("Velocidad temporal")),
            TargetSelectors.self(),
            (ctx, token) -> {
                Player player = ctx.caster();
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 50 * 20, 2));
                hotbarMessage.sendHotbarMessage(player, ChatColor.AQUA + "You have used the Tailwind technique!");
            }
    );

    static Technique flyingLittleBugs = new Technique(
            "flying_little_bugs",
            "Flying little bugs",
            new TechniqueMeta(false, cooldownHelper.secondsToMiliseconds(60), List.of("Invoca vex")),
            TargetSelectors.self(),
            (ctx, token) -> {
                Player player = ctx.caster();
                int amnt = awakening.isFruitAwakened(player.getName(), fruitId) ? 15 : 5;
                for (int i = 0; i < amnt; i++) {
                    Vex tickles = (Vex) player.getWorld().spawnEntity(player.getLocation(), org.bukkit.entity.EntityType.VEX);
                    tickles.setCustomName("Flying Tickles");
                    tickles.setCustomNameVisible(true);
                    tickles.setGlowing(true);
                    if (awakening.isFruitAwakened(player.getName(), fruitId)) {
                        tickles.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, 4));
                        tickles.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 3));
                    }
                }
                hotbarMessage.sendHotbarMessage(player, ChatColor.GREEN + "You have used the Flying little bugs technique!");
            }
    );

    static Technique featherFall = new Technique(
            "feather_fall",
            "Feather fall",
            new TechniqueMeta(false, 60000, List.of("Caída lenta")),
            TargetSelectors.self(),
            (ctx, token) -> {
                Player player = ctx.caster();
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 5 * 20, 255));
                hotbarMessage.sendHotbarMessage(player, ChatColor.GRAY + "You have used the Feather fall technique!");
            }
    );

    static Technique ambush = new Technique(
            "arrows",
            "Astral Barrage",
            new TechniqueMeta(false, cooldownHelper.minutesToMiliseconds(3), List.of("Lluvia de flechas")),
            TargetSelectors.self(),
            (ctx, token) -> {
                Player player = ctx.caster();
                Player target = chao.getClosestPlayer(player.getLocation());
                if (target == null) target = player;
                final Player finalTarget = target;
                final Location playerLoc = player.getLocation();
                final World world = player.getWorld();
                final int minArrows = 30;
                final int maxArrows = 50;
                final int durationTicks = 100;
                final int totalArrows = minArrows + (int) (Math.random() * (maxArrows - minArrows + 1));
                final double arrowsPerTick = (double) totalArrows / durationTicks;

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
                            int arrowsThisTick = (int) arrowAccumulator;
                            arrowAccumulator -= arrowsThisTick;
                            float yaw = playerLoc.getYaw();
                            for (int i = 0; i < arrowsThisTick; i++) {
                                double phi = Math.random() * 2 * Math.PI;
                                double radius = 12 + Math.random() * 10;
                                double heightAbove = 8 + Math.random() * 4;
                                double x = radius * Math.cos(phi);
                                double y = heightAbove;
                                double z = radius * Math.sin(phi);
                                double yawRad = Math.toRadians(-yaw);
                                double rotatedX = x * Math.cos(yawRad) - z * Math.sin(yawRad);
                                double rotatedZ = x * Math.sin(yawRad) + z * Math.cos(yawRad);
                                Location spawnLoc = playerLoc.clone().add(rotatedX, y, rotatedZ);
                                Vector direction = finalTarget.getLocation().add(0, 1, 0).toVector().subtract(spawnLoc.toVector()).normalize();
                                Arrow arrow = world.spawnArrow(spawnLoc, direction, 2.5f, 0.1f);
                                arrow.setShooter(player);
                                arrow.setCritical(true);
                                arrow.setDamage(15);
                                arrow.setPickupStatus(org.bukkit.entity.AbstractArrow.PickupStatus.DISALLOWED);
                                arrow.setGravity(false);
                            }
                        }
                    }.runTaskTimer(plugin, 0L, 1L);
                    hotbarMessage.sendHotbarMessage(player, ChatColor.GOLD + "Astral Barrage unleashed!");
                }, 100L);
                hotbarMessage.sendHotbarMessage(player, ChatColor.YELLOW + "Astral Barrage charging...");
            }
    );

    static Technique danceInTheStars = new Technique(
            "dance_in_the_stars",
            "Dance in the stars",
            new TechniqueMeta(true, cooldownHelper.secondsToMiliseconds(3600), List.of("Ultimate: Dance in the stars")),
            TargetSelectors.self(),
            (ctx, token) -> {
                Player player = ctx.caster();
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

    static Technique ultimateCombo = new Technique(
            "ascension_of_abundance",
            "Ascensión de la Abundancia",
            new TechniqueMeta(true, cooldownHelper.secondsToMiliseconds(3600) * 10, List.of("Ultimate: Ascensión de la Abundancia")),
            TargetSelectors.self(),
            (ctx, token) -> {
                Player player = ctx.caster();
                World world = player.getWorld();
                Location center = player.getLocation();
                List<Player> enemigos = new ArrayList<>();
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p != player && p.getLocation().distance(center) < 30) {
                        enemigos.add(p);
                        p.setVelocity(new Vector(0, 2.5, 0));
                    }
                }
                player.setVelocity(new Vector(0, 4, 0));
                for (int i = 0; i < 10; i++) {
                    Vex vex = (Vex) world.spawnEntity(center, org.bukkit.entity.EntityType.VEX);
                    vex.setCustomName("Guardián Celestial");
                    vex.setCustomNameVisible(true);
                    vex.setGlowing(true);
                    vex.setTarget(enemigos.isEmpty() ? null : enemigos.get(i % Math.max(1, enemigos.size())));
                    vex.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 60, 2));
                    vex.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 20 * 60, 2));
                }
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 20, 2));
                player.setFoodLevel(20);
                player.setSaturation(20);
                new BukkitRunnable() {
                    int ticks = 0;
                    @Override
                    public void run() {
                        if (ticks++ > 20) { this.cancel(); return; }
                        for (Player enemigo : enemigos) {
                            Location above = enemigo.getLocation().add(0, 15, 0);
                            Vector dir = enemigo.getLocation().toVector().subtract(above.toVector()).normalize();
                            Arrow arrow = world.spawnArrow(above, dir, 3.0f, 0.1f);
                            arrow.setShooter(player);
                            arrow.setCritical(true);
                            arrow.setVelocity(dir.multiply(5));
                            arrow.setDamage(20);
                        }
                    }
                }.runTaskTimer(plugin, 20L, 2L);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    for (PotionEffect effect : player.getActivePotionEffects()) {
                        if (effect.getType().getCategory().equals(PotionEffectTypeCategory.HARMFUL)) {
                            player.removePotionEffect(effect.getType());
                        }
                    }
                    hotbarMessage.sendHotbarMessage(player, ChatColor.LIGHT_PURPLE + "¡Ascensión de la Abundancia completada!");
                }, 120L);
                hotbarMessage.sendHotbarMessage(player, ChatColor.GOLD + "¡Has desatado la Ascensión de la Abundancia!");
            },
            (ctx, token) -> {
                Player player = ctx.caster();
                if (!awakening.isFruitAwakened(player.getName(), "fly") || !awakening.isFruitAwakened(player.getName(), "chao")) {
                    hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "¡Debes tener ambos frutos despertados para usar esta técnica!");
                    return false;
                }
                events.DeawakenFruit(player, "fly");
                events.DeawakenFruit(player, "chao");
                return true;
            }
    );
}
