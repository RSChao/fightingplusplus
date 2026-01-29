package com.rschao.plugins.fightingpp.techs;

import com.rschao.boss_battle.bossEvents;
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
import org.bukkit.attribute.Attribute;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.stream.Collectors;

public class flower {
    public static final String ID = "flower";
    static final Plugin plugin = Plugin.getPlugin(Plugin.class);

    public static void register() {
        TechRegistry.registerTechnique(ID, blizzard);
        TechRegistry.registerTechnique(ID, florafade);
        TechRegistry.registerTechnique(ID, berryburst);
        TechRegistry.registerTechnique(ID, mandragorasbreath);
        TechRegistry.registerTechnique(ID, pentaflare);
        TechRegistry.registerTechnique(ID, buff);
        TechRegistry.registerTechnique(ID, judge);
        TechRegistry.registerTechnique(ID, combo);
        Plugin.registerFruitID(ID);
    }

    static Technique blizzard = new Technique(
            "blizzard",
            "Petal Blizzard",
            new TechniqueMeta(false, cooldownHelper.minutesToMiliseconds(3), List.of("Petal Blizzard")),
            TargetSelectors.self(),
            (ctx, token) -> {
                Player player = ctx.caster();
                for (int i = 0; i < 15; i++) {
                    int delay = i * 2;
                    Bukkit.getScheduler().runTaskLater(Plugin.getPlugin(Plugin.class), () -> {
                        Vector direction = player.getEyeLocation().getDirection().normalize();
                        Vector randomOffset = new Vector(
                                (Math.random() - 0.5) * 0.2,
                                (Math.random() - 0.5) * 0.2,
                                (Math.random() - 0.5) * 0.2
                        );
                        Vector finalDirection = direction.add(randomOffset).multiply(5);
                        org.bukkit.entity.Arrow arrow = player.launchProjectile(org.bukkit.entity.Arrow.class, finalDirection);
                        arrow.setDamage(20.0);
                        arrow.setGravity(false);
                        arrow.setVelocity(finalDirection);
                        arrow.setPickupStatus(org.bukkit.entity.AbstractArrow.PickupStatus.DISALLOWED);
                    }, delay);
                }
            }
    );

    static Technique florafade = new Technique(
            "florafade",
            "Flora Fade",
            new TechniqueMeta(false, cooldownHelper.minutesToMiliseconds(5), List.of("Flora Fade")),
            TargetSelectors.self(),
            (ctx, token) -> {
                Player player = ctx.caster();
                Location loc = player.getLocation().clone();
                Location tp = loc.add(com.rschao.events.events.getRNG(-4, 4), 0, com.rschao.events.events.getRNG(-4, 4));
                player.teleport(tp);
                player.getWorld().createExplosion(loc, 2.0F, false, false);
                player.sendMessage("You have used Flora Fade!");
            }
    );

    static Technique berryburst = new Technique(
            "berryburst",
            "Berry Burst",
            new TechniqueMeta(false, cooldownHelper.minutesToMiliseconds(4), List.of("Berry Burst")),
            TargetSelectors.self(),
            (ctx, token) -> {
                Player player = ctx.caster();
                Location playerLoc = player.getLocation();
                int radius = 5;
                int y = playerLoc.getBlockY();
                for (int x = -radius; x <= radius; x++) {
                    for (int z = -radius; z <= radius; z++) {
                        Location checkLoc = playerLoc.clone().add(x, 0, z);
                        int closestY = y;
                        boolean found = false;
                        for (int dy = -2; dy <= 2; dy++) {
                            Location airLoc = checkLoc.clone().add(0, dy, 0);
                            if (airLoc.getBlock().getType() == org.bukkit.Material.AIR) {
                                closestY = y + dy;
                                found = true;
                                break;
                            }
                        }
                        if (found) {
                            Location bushLoc = checkLoc.clone();
                            bushLoc.setY(closestY);
                            bushLoc.getBlock().setType(org.bukkit.Material.SWEET_BERRY_BUSH);
                            if (bushLoc.getBlock().getBlockData() instanceof Ageable) {
                                Ageable ageable = (Ageable) bushLoc.getBlock().getBlockData();
                                ageable.setAge(ageable.getMaximumAge());
                                bushLoc.getBlock().setBlockData(ageable);
                            }
                        }
                    }
                }
                player.sendMessage("Berry bushes have grown around you!");
            }
    );

    static Technique mandragorasbreath = new Technique(
            "mandragorasbreath",
            "Mandragora's Breath",
            new TechniqueMeta(false, cooldownHelper.minutesToMiliseconds(5), List.of("Mandragora's Breath")),
            TargetSelectors.self(),
            (ctx, token) -> {
                Player player = ctx.caster();
                Location origin = player.getLocation();
                Vector direction = player.getEyeLocation().getDirection().normalize();
                Location tip = origin.clone().add(direction.clone().multiply(5));
                Vector side = direction.clone().crossProduct(new Vector(0, 1, 0)).normalize();
                Location left = tip.clone().add(side.clone().multiply(3));
                Location right = tip.clone().add(side.clone().multiply(-3));
                for (Player target : player.getWorld().getPlayers()) {
                    if (target.equals(player)) continue;
                    Location tLoc = target.getLocation();
                    Vector v0 = left.toVector().subtract(origin.toVector());
                    Vector v1 = right.toVector().subtract(origin.toVector());
                    Vector v2 = tLoc.toVector().subtract(origin.toVector());
                    double dot00 = v0.dot(v0);
                    double dot01 = v0.dot(v1);
                    double dot02 = v0.dot(v2);
                    double dot11 = v1.dot(v1);
                    double dot12 = v1.dot(v2);
                    double invDenom = 1 / (dot00 * dot11 - dot01 * dot01);
                    double u = (dot11 * dot02 - dot01 * dot12) * invDenom;
                    double v = (dot00 * dot12 - dot01 * dot02) * invDenom;
                    if (u >= 0 && v >= 0 && (u + v) <= 1) {
                        target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 600, 1));
                        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 600, 1));
                        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 600, 1));
                        target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 600, 1));
                    }
                }
                player.sendMessage("Mandragora's Breath unleashed!");
            }
    );

    static Technique pentaflare = new Technique(
            "pentaflare",
            "Wood Dragon's Fury",
            new TechniqueMeta(false, cooldownHelper.minutesToMiliseconds(6), List.of("Wood Dragon's Fury")),
            TargetSelectors.self(),
            (ctx, token) -> {
                Player player = ctx.caster();
                Location origin = player.getLocation().add(0, 1, 0);
                double radius = 1.5;
                double angleOffset = Math.atan2(player.getLocation().getDirection().getZ(), player.getLocation().getDirection().getX());
                for (int i = 0; i < 5; i++) {
                    double angle = angleOffset + (2 * Math.PI * i / 5);
                    double dx = Math.cos(angle) * radius;
                    double dz = Math.sin(angle) * radius;
                    Vector direction = new Vector(dx, 0, dz).normalize();
                    Location launch = origin.clone().add(direction.multiply(1.5));
                    Bukkit.getScheduler().runTaskLater(Plugin.getPlugin(Plugin.class), () -> {
                        org.bukkit.entity.Fireball fireball = player.getWorld().spawn(launch, org.bukkit.entity.Fireball.class);
                        fireball.setDirection(direction);
                        fireball.setYield(2F);
                        fireball.setIsIncendiary(true);
                        fireball.setShooter(player);
                        fireball.setCustomName("blasterBall");
                    }, 1);
                }
                player.sendMessage("Wood Dragon's Fury unleashed!");
            }
    );

    static Technique buff = new Technique(
            "buff",
            "Funny Plants",
            new TechniqueMeta(false, cooldownHelper.minutesToMiliseconds(4), List.of("Buff temporal")),
            TargetSelectors.self(),
            (ctx, token) -> {
                Player player = ctx.caster();
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 600, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 600, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 600, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 600, 1));
                player.sendMessage("Funny plants unleashed!");
            }
    );

    static Technique judge = new Technique(
            "judge",
            "Ygdrasil's Judgment",
            new TechniqueMeta(true, cooldownHelper.hour, List.of("Ultimate: Ygdrasil's Judgment")),
            TargetSelectors.self(),
            (ctx, token) -> {
                Player player = ctx.caster();
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if(!p.getWorld().equals(player.getWorld())) continue;
                    double distance = p.getLocation().distance(player.getLocation());
                    if (distance > 7) continue;
                    if (p != player) {
                        Bukkit.getScheduler().runTaskLater(Plugin.getPlugin(Plugin.class), () -> {
                            double hearts = p.getAttribute(Attribute.MAX_HEALTH).getBaseValue();
                            p.getAttribute(Attribute.MAX_HEALTH).setBaseValue(hearts - 10);
                            player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 5 * 20, 5));
                            Bukkit.getScheduler().runTaskLater(Plugin.getPlugin(Plugin.class), () -> p.getAttribute(Attribute.MAX_HEALTH).setBaseValue(hearts), 60 * 20);
                        }, 5);
                    } else {
                        double hearts = p.getAttribute(Attribute.MAX_HEALTH).getBaseValue();
                        p.getAttribute(Attribute.MAX_HEALTH).setBaseValue(hearts - 10);
                        Bukkit.getScheduler().runTaskLater(Plugin.getPlugin(Plugin.class), () -> p.getAttribute(Attribute.MAX_HEALTH).setBaseValue(hearts), 60 * 20);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 5 * 20, 5));
                        player.damage(50);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 60 * 20, 4));
                    }
                }
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

    static Technique combo = new Technique(
            "true_blizzard",
            "Danza de los mil pétalos",
            new TechniqueMeta(true, cooldownHelper.hour, List.of("Ultimate: Danza de los mil pétalos")),
            TargetSelectors.self(),
            (ctx, token) -> {
                Player player = ctx.caster();
                Player user = player;
                Location center = user.getLocation().add(0, 3, 0);
                List<Player> targets;
                if (bossEvents.bossActive) {
                    targets = user.getWorld().getPlayers().stream()
                            .filter(p -> !p.equals(user) && p.getLocation().distance(user.getLocation()) <= 60).filter(p -> p.getWorld().equals(user.getWorld()))
                            .filter(p -> p.hasPermission("gaster.boss"))
                            .collect(Collectors.toList());
                } else {
                    targets = user.getWorld().getPlayers().stream().filter(p -> p.getWorld().equals(user.getWorld()))
                            .filter(p -> !p.equals(user) && p.getLocation().distance(user.getLocation()) <= 60)
                            .collect(Collectors.toList());
                }
                for (Player p : targets) {
                    p.teleport(center);
                }
                int spiralDuration = 100;
                int numPlayers = targets.size();
                double spiralRadius = 8;
                double spiralHeight = 10;
                double spiralTurns = 3;
                new BukkitRunnable() {
                    int tick = 0;
                    @Override
                    public void run() {
                        if (tick >= spiralDuration || targets.isEmpty()) {
                            this.cancel();
                            return;
                        }
                        double angleStep = 2 * Math.PI / Math.max(1, numPlayers);
                        for (int i = 0; i < targets.size(); i++) {
                            Player p = targets.get(i);
                            double angle = angleStep * i + spiralTurns * 2 * Math.PI * tick / spiralDuration;
                            double y = spiralHeight * tick / spiralDuration;
                            double x = spiralRadius * Math.cos(angle);
                            double z = spiralRadius * Math.sin(angle);
                            Location spiralLoc = center.clone().add(x, y, z);
                            Vector vel = spiralLoc.toVector().subtract(p.getLocation().toVector()).multiply(0.2);
                            p.setVelocity(vel);
                        }
                        tick++;
                    }
                }.runTaskTimer(plugin, 0, 1);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        int xp = user.getLevel();
                        for (Player p : targets) {
                            p.damage(700, user);
                        }
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                for (Player p : targets) {
                                    Vector away = p.getLocation().toVector().subtract(user.getLocation().toVector()).normalize().multiply(xp);
                                    p.setVelocity(away);
                                }
                            }
                        }.runTaskLater(plugin, 1);
                    }
                }.runTaskLater(plugin, spiralDuration);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Location effectLoc = center.clone();
                        user.getWorld().createExplosion(effectLoc, 1.0F, false, false);
                        int xp = user.getLevel();
                        for (Player p : targets) {
                            p.setVelocity(new Vector(0, 500, 0));
                            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                p.damage(700, user);
                                p.setVelocity(new Vector(0, 0, 0));
                            }, 20);
                        }
                    }
                }.runTaskLater(plugin, spiralDuration + 4);
            },
            (ctx, token) -> {
                Player player = ctx.caster();
                if (!awakening.isFruitAwakened(player.getName(), ID) || !awakening.isFruitAwakened(player.getName(), "dario")) {
                    hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "You need to awaken your fruits to use this technique!");
                    return false;
                }
                events.DeawakenFruit(player, ID);
                events.DeawakenFruit(player, "dario");
                return true;
            }
    );

}
