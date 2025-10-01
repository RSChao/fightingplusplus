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
import org.bukkit.attribute.Attribute;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class flower {
    public static final String ID = "flower";

    public static void register() {
        TechRegistry.registerTechnique(ID, blizzard);
        TechRegistry.registerTechnique(ID, florafade);
        TechRegistry.registerTechnique(ID, berryburst);
        TechRegistry.registerTechnique(ID, mandragorasbreath);
        TechRegistry.registerTechnique(ID, pentaflare);
        TechRegistry.registerTechnique(ID, buff);
        TechRegistry.registerTechnique(ID, judge);
        Plugin.registerFruitID(ID);
    }

    static Technique blizzard = new Technique("blizzard", "Petal Blizzard", false, cooldownHelper.minutesToMiliseconds(3), (player, item, args) -> {
        for (int i = 0; i < 15; i++) {
            Bukkit.getScheduler().runTaskLater(Plugin.getPlugin(Plugin.class), () -> {
                Vector direction = player.getEyeLocation().getDirection().normalize();
                Vector randomOffset = new Vector(
                        (Math.random() - 0.5) * 0.2,
                        (Math.random() - 0.5) * 0.2,
                        (Math.random() - 0.5) * 0.2
                );
                Vector finalDirection = direction.add(randomOffset).multiply(5); // Adjust speed multiplier as needed
                org.bukkit.entity.Arrow arrow = player.launchProjectile(org.bukkit.entity.Arrow.class, finalDirection);
                arrow.setDamage(20.0); // Set base damage to 50
                arrow.setGravity(false);
                arrow.setVelocity(finalDirection);
                arrow.setPickupStatus(org.bukkit.entity.AbstractArrow.PickupStatus.DISALLOWED); // Prevent pickup
            }, i * 2L); // Slight delay between each arrow
        }
    });

    static Technique florafade = new Technique("florafade", "Flora Fade", false, cooldownHelper.minutesToMiliseconds(5), (player, item, args) -> {

        Location loc = player.getLocation().clone();
        Location tp = loc.add(com.rschao.events.events.getRNG(-4, 4), 0, com.rschao.events.events.getRNG(-4, 4));
        player.teleport(tp);
        player.getWorld().createExplosion(loc, 2.0F, false, false);
        player.sendMessage("You have used Flora Fade!");
    });

    static Technique berryburst = new Technique("berryburst", "Berry Burst", false, cooldownHelper.minutesToMiliseconds(4), (player, item, args) -> {
        Location playerLoc = player.getLocation();
        int radius = 5;
        int y = playerLoc.getBlockY();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                Location checkLoc = playerLoc.clone().add(x, 0, z);
                // Find the closest air block to player's Y level in this column
                int closestY = y;
                boolean found = false;
                for (int dy = -2; dy <= 2; dy++) { // Check a small vertical range around player
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
                        org.bukkit.block.data.Ageable ageable = (org.bukkit.block.data.Ageable) bushLoc.getBlock().getBlockData();
                        ageable.setAge(ageable.getMaximumAge());
                        bushLoc.getBlock().setBlockData(ageable);
                    }
                }
            }
        }
        player.sendMessage("Berry bushes have grown around you!");
    });

    static Technique mandragorasbreath = new Technique("mandragorasbreath", "Mandragora's Breath", false, cooldownHelper.minutesToMiliseconds(5), (player, item, args) -> {
        Location origin = player.getLocation();
        Vector direction = player.getEyeLocation().getDirection().normalize();
        // Calculate triangle vertices
        Location tip = origin.clone().add(direction.clone().multiply(5));
        Vector side = direction.clone().crossProduct(new Vector(0, 1, 0)).normalize();
        Location left = tip.clone().add(side.clone().multiply(3));
        Location right = tip.clone().add(side.clone().multiply(-3));

        // Get all players in the world
        for (org.bukkit.entity.Player target : player.getWorld().getPlayers()) {
            if (target.equals(player)) continue;
            Location tLoc = target.getLocation();
            // Check if target is within triangle
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
    });
    static Technique pentaflare = new Technique("pentaflare", "Wood Dragon's Fury", false, cooldownHelper.minutesToMiliseconds(6), (player, item, args) -> {
        Location origin = player.getLocation().add(0, 1, 0); // Fireballs spawn slightly above ground
        double radius = 1.5;
        double angleOffset = Math.atan2(player.getLocation().getDirection().getZ(), player.getLocation().getDirection().getX());
        //player.setGameMode(GameMode.SPECTATOR);
        for (int i = 0; i < 5; i++) {
            double angle = angleOffset + (2 * Math.PI * i / 5);
            double dx = Math.cos(angle) * radius;
            double dz = Math.sin(angle) * radius;
            Vector direction = new Vector(dx, 0, dz).normalize();
            Location launch = origin.clone().add(direction.multiply(1.5)); // Launch position 1.5 blocks in front of player
            Bukkit.getScheduler().runTaskLater(com.rschao.plugins.fightingpp.Plugin.getPlugin(com.rschao.plugins.fightingpp.Plugin.class), () -> {
                org.bukkit.entity.Fireball fireball = player.getWorld().spawn(launch, org.bukkit.entity.Fireball.class);
                fireball.setDirection(direction);
                fireball.setYield(2F);
                fireball.setIsIncendiary(true);
                fireball.setShooter(player);
                fireball.setCustomName("blasterBall");
            }, 1);
        }
        player.sendMessage("Wood Dragon's Fury unleashed!");

    });
    static Technique buff = new Technique("buff", "Funny Plants", false, cooldownHelper.minutesToMiliseconds(4), (player, item, args) -> {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 600, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 600, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 600, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 600, 1));
        player.sendMessage("Funny plants unleashed!");
    });

    static Technique judge = new Technique("judge", "Ygdrasil's Judgment", true, cooldownHelper.hour, (player, item, args) -> {
        if (!awakening.isFruitAwakened(player.getName(), ID)) {
            hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "You need to awaken your fruit to use this technique!");
            return;
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            double distance = p.getLocation().distance(player.getLocation());
            if (distance > 7) continue; // Only affect players within 7 blocks
                if (p != player) {
                    Bukkit.getScheduler().runTaskLater(com.rschao.plugins.fightingpp.Plugin.getPlugin(com.rschao.plugins.fightingpp.Plugin.class), () -> {
                        double hearts = p.getAttribute(Attribute.MAX_HEALTH).getBaseValue();
                        p.getAttribute(Attribute.MAX_HEALTH).setBaseValue(hearts - 10);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 5 * 20, 5));
                        Bukkit.getScheduler().runTaskLater(com.rschao.plugins.fightingpp.Plugin.getPlugin(com.rschao.plugins.fightingpp.Plugin.class), () -> p.getAttribute(Attribute.MAX_HEALTH).setBaseValue(hearts), 60 * 20);

                    }, 5);

                } else {
                    double hearts = p.getAttribute(Attribute.MAX_HEALTH).getBaseValue();
                    p.getAttribute(Attribute.MAX_HEALTH).setBaseValue(hearts - 10);
                    Bukkit.getScheduler().runTaskLater(com.rschao.plugins.fightingpp.Plugin.getPlugin(com.rschao.plugins.fightingpp.Plugin.class), () -> p.getAttribute(Attribute.MAX_HEALTH).setBaseValue(hearts), 60 * 20);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 5 * 20, 5));
                    player.damage(50);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 60 * 20, 4));
                }
        }
    });

}
