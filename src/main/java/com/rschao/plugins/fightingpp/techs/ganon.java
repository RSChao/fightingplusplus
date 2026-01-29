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
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class ganon {
    static final String id = "ganon";
    public static Map<UUID, Boolean> hasFSmash = new HashMap<>();
    static final Plugin plugin = Plugin.getPlugin(Plugin.class);

    public static void register() {
        TechRegistry.registerTechnique(id, test);
        TechRegistry.registerTechnique(id, kick);
        TechRegistry.registerTechnique(id, grab);
        TechRegistry.registerTechnique(id, punch);
        TechRegistry.registerTechnique(id, rush);
        TechRegistry.registerTechnique(id, ultimateCombo);
        Plugin.registerFruitID(id);
    }

    static Technique test = new Technique(
            "fsmash",
            "Claymore Smash",
            new TechniqueMeta(false, cooldownHelper.minutesToMiliseconds(5), List.of("Claymore Smash")),
            TargetSelectors.self(),
            (ctx, token) -> {
                Player player = ctx.caster();
                hasFSmash.put(player.getUniqueId(), true);
                hotbarMessage.sendHotbarMessage(player, "You have used the Claymore Smash technique!");
                Bukkit.getScheduler().runTaskLater(Plugin.getPlugin(Plugin.class), () -> {
                    hasFSmash.remove(player.getUniqueId());
                    hotbarMessage.sendHotbarMessage(player, "Claymore Smash technique is no longer active.");
                }, 100);
            }
    );

    static Technique kick = new Technique(
            "kick",
            "Ganon kick",
            new TechniqueMeta(false, cooldownHelper.secondsToMiliseconds(60), List.of("Ganon kick")),
            TargetSelectors.self(),
            (ctx, token) -> {
                Player player = ctx.caster();
                Location location = player.getLocation();
                player.getWorld().spawnParticle(org.bukkit.Particle.SWEEP_ATTACK, location, 30);
                player.getWorld().playSound(location, org.bukkit.Sound.ENTITY_ENDER_DRAGON_FLAP, 1, 1);

                for (org.bukkit.entity.Entity entity : location.getWorld().getEntities()) {
                    if (entity.getLocation().distance(location) <= 20 && entity != player) {
                        if ((entity instanceof Player)) {
                            Player target = (Player) entity;
                            target.damage(30);
                        }
                        Vector direction = entity.getLocation().toVector().subtract(location.toVector()).normalize();
                        double y = direction.getY();
                        if (direction.getY() > 0) {
                            direction.setY(-3);
                        }
                        direction.setY(y);
                        entity.setVelocity(direction.multiply(3));
                    }
                }
                Vector direction = player.getLocation().getDirection();
                player.setVelocity(direction.multiply(4));
                hotbarMessage.sendHotbarMessage(player, ChatColor.DARK_GRAY + "You have used the Ganon Kick technique");
            }
    );

    static Technique grab = new Technique(
            "grab",
            "Ganon grab",
            new TechniqueMeta(false, cooldownHelper.secondsToMiliseconds(90), List.of("Ganon grab")),
            TargetSelectors.self(),
            (ctx, token) -> {
                Player player = ctx.caster();
                Location location = player.getLocation();
                player.getWorld().spawnParticle(org.bukkit.Particle.SWEEP_ATTACK, location, 30);
                player.getWorld().playSound(location, org.bukkit.Sound.ENTITY_ENDER_DRAGON_FLAP, 1, 1);

                for (org.bukkit.entity.Entity entity : location.getWorld().getEntities()) {
                    if (entity.getLocation().distance(location) <= 20 && entity != player) {
                        if ((entity instanceof Player)) {
                            Player target = (Player) entity;
                            target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 20, 255));
                            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 20, 255));
                        }
                        Vector direction = entity.getLocation().toVector().subtract(location.toVector()).normalize();
                        entity.setVelocity(direction.multiply(3));
                    }
                }
                Vector direction = player.getLocation().getDirection();
                player.setVelocity(direction.multiply(4));
                hotbarMessage.sendHotbarMessage(player, ChatColor.DARK_GRAY + "You have used the Ganon Grab technique");
            }
    );

    static Technique punch = new Technique(
            "punch",
            "Warlock punch",
            new TechniqueMeta(false, cooldownHelper.minutesToMiliseconds(2), List.of("Warlock punch")),
            TargetSelectors.self(),
            (ctx, token) -> {
                Player player = ctx.caster();
                Location location = player.getLocation();
                Vector direction = player.getLocation().getDirection().normalize();
                Location launch = location.add(direction.multiply(2));
                for (int i = 0; i < 3; i++) {
                    Bukkit.getScheduler().runTaskLater(Plugin.getPlugin(Plugin.class), () -> {
                        org.bukkit.entity.Fireball fireball = player.getWorld().spawn(launch, org.bukkit.entity.Fireball.class);
                        fireball.setDirection(direction);
                        fireball.setYield(2F);
                        fireball.setIsIncendiary(true);
                        fireball.setShooter(player);
                        fireball.setCustomName("determinationBall");
                    }, i * 3);
                }
                hotbarMessage.sendHotbarMessage(player, ChatColor.DARK_GRAY + "You have used the Warlock Punch technique");
            }
    );

    static Technique rush = new Technique(
            "rush",
            "Calamity Beast Rush",
            new TechniqueMeta(true, cooldownHelper.hour, List.of("Calamity Beast Rush")),
            TargetSelectors.self(),
            (ctx, token) -> {
                Player player = ctx.caster();
                Location location = player.getLocation();
                player.getWorld().spawnParticle(org.bukkit.Particle.SWEEP_ATTACK, location, 30);
                player.getWorld().playSound(location, org.bukkit.Sound.ENTITY_ENDER_DRAGON_FLAP, 1, 1);

                for (org.bukkit.entity.Entity entity : location.getWorld().getEntities()) {
                    if (entity.getLocation().distance(location) <= 20 && entity != player) {
                        if ((entity instanceof Player)) {
                            Player target = (Player) entity;
                            target.damage(500);
                            target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 5 * 20, 4));
                        }
                        Vector direction = entity.getLocation().toVector().subtract(location.toVector()).normalize();
                        entity.setVelocity(direction.multiply(3));
                    }
                }
                Vector direction = player.getLocation().getDirection();
                player.setVelocity(direction.multiply(4));
                hotbarMessage.sendHotbarMessage(player, ChatColor.DARK_GRAY + "You have used the Calamity Beast Rush technique");
            },
            (ctx, token) -> {
                Player player = ctx.caster();
                if (!awakening.isFruitAwakened(player.getName(), id)) {
                    hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "You need to awaken your fruit to use this technique!");
                    return false;
                }
                events.DeawakenFruit(player, id);
                return true;
            }
    );

    static Technique ultimateCombo = new Technique(
            "tempest_of_ruin",
            "Tempestad de la Ruina",
            new TechniqueMeta(true, cooldownHelper.hour * 10, List.of("Tempestad de la Ruina")),
            TargetSelectors.self(),
            (ctx, token) -> {
                Player player = ctx.caster();
                World world = player.getWorld();
                Location center = player.getLocation();
                List<Player> enemigos = new ArrayList<>();
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if(!p.getWorld().equals(world)) continue;
                    if (p != player && p.getLocation().distance(center) < 40) {
                        enemigos.add(p);
                    }
                }
                if(enemigos.isEmpty()){
                    hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "¡No hay enemigos cercanos para desatar la Tempestad de la Ruina!");
                    awakening.setFruitAwakened(player.getName(), "delta", true);
                    awakening.setFruitAwakened(player.getName(), "ganon", true);
                    return;
                }
                //world.spawnParticle(Particle.DRAGON_BREATH, center, 200, 5, 2, 5);
                world.playSound(center, Sound.ENTITY_ENDER_DRAGON_GROWL, 2, 0.5f);
                new BukkitRunnable() {
                    int tick = 0;
                    final int duration = 20;
                    @Override
                    public void run() {
                        if (tick++ > duration || enemigos.isEmpty()) {
                            this.cancel();
                            return;
                        }
                        double angleStep = 2 * Math.PI / Math.max(1, enemigos.size());
                        for (int i = 0; i < enemigos.size(); i++) {
                            Player p = enemigos.get(i);
                            double angle = angleStep * i + 2 * Math.PI * tick / duration;
                            double radius = 10 + 5 * Math.sin(tick / 5.0);
                            double y = 2 + 0.5 * tick;
                            Location spiralLoc = center.clone().add(radius * Math.cos(angle), y, radius * Math.sin(angle));
                            Vector vel = spiralLoc.toVector().subtract(p.getLocation().toVector()).multiply(0.2).add(new Vector(0, 0.5, 0));
                            p.setVelocity(vel);
                            p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40, 3, true, false));
                            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 2, true, false));
                            if (tick % 10 == 0) {
                                p.damage(100, player);
                                world.createExplosion(p.getLocation(), 30.0F, false, false, player);
                                world.spawnParticle(Particle.LARGE_SMOKE, p.getLocation(), 30, 1, 1, 1, 0.2);
                            }
                        }
                    }
                }.runTaskTimer(plugin, 0L, 2L);
                hotbarMessage.sendHotbarMessage(player, ChatColor.DARK_PURPLE + "¡Has desatado la Tempestad de la Ruina!");
            },
            (ctx, token) -> {
                Player player = ctx.caster();
                if (!awakening.isFruitAwakened(player.getName(), "delta") || !awakening.isFruitAwakened(player.getName(), "ganon")) {
                    hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "¡Debes tener ambos frutos despertados para usar esta técnica!");
                    return false;
                }
                events.DeawakenFruit(player, "delta");
                events.DeawakenFruit(player, "ganon");
                return true;
            }
    );
}