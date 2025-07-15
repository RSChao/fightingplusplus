package com.rschao.plugins.fightingpp.techs;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.entity.WindCharge;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
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

public class delta {
    static final String fruitId = "delta";
    static final Plugin plugin = Plugin.getPlugin(Plugin.class);

    public static void Register() {
        TechRegistry.registerTechnique(fruitId, windSword);
        TechRegistry.registerTechnique(fruitId, negate);
        TechRegistry.registerTechnique(fruitId, dragonOnslaught);
        TechRegistry.registerTechnique(fruitId, armorHaki);
        TechRegistry.registerTechnique(fruitId, conquerorsHaki);
        TechRegistry.registerTechnique(fruitId, zoltraakBarrage);
        TechRegistry.registerTechnique(fruitId, wrathOfElementalGod);
        TechRegistry.registerTechnique(fruitId, tornado);
    }

    static Technique windSword = new Technique("wind_sword", "Wind sword", false, cooldownHelper.secondsToMiliseconds(30), (player, fruit, code) -> {
        for (int i = 0; i < 4; i++) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.getWorld().spawn(player.getEyeLocation(), WindCharge.class);
            }, 5L * i);
        }
        hotbarMessage.sendHotbarMessage(player, ChatColor.DARK_GREEN + "You have used the Wind sword technique");
    });

    static Technique negate = new Technique("negate", "NEGATE", false, cooldownHelper.secondsToMiliseconds(300), (player, fruit, code) -> {
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 3 * 20, 255));
        hotbarMessage.sendHotbarMessage(player, ChatColor.DARK_PURPLE + "You have used the NEGATE technique");
    });

    static Technique dragonOnslaught = new Technique("dragon_onslaught", "Dragon onslaught", false, cooldownHelper.secondsToMiliseconds(60), (player, fruit, code) -> {
        Location location = player.getLocation();
        player.getWorld().spawnParticle(org.bukkit.Particle.SWEEP_ATTACK, location, 30);
        player.getWorld().playSound(location, org.bukkit.Sound.ENTITY_ENDER_DRAGON_FLAP, 1, 1);

        for (org.bukkit.entity.Entity entity : location.getWorld().getEntities()) {
            if (entity.getLocation().distance(location) <= 20 && entity != player) {
                if((entity instanceof Player)){
                    Player target = (Player) entity;
                    target.damage(30);
                }
                Vector direction = entity.getLocation().toVector().subtract(location.toVector()).normalize();
                entity.setVelocity(direction.multiply(3));
            }
        }
        Vector direction = player.getLocation().getDirection();
        player.setVelocity(direction.multiply(4));
        hotbarMessage.sendHotbarMessage(player, ChatColor.DARK_GRAY + "You have used the Dragon onslaught technique");
    });

    static Technique armorHaki = new Technique("armor_haki", "Armor Haki", false, cooldownHelper.secondsToMiliseconds(300), (player, fruit, code) -> {
        String playerName = player.getName();
        ItemStack[] armorContents = player.getInventory().getArmorContents();
        if (awakening.isFruitAwakened(playerName, fruitId)) {
            for (ItemStack item : armorContents) {
                if (item != null && item.getItemMeta() instanceof Damageable) {
                    Damageable meta = (Damageable) item.getItemMeta();
                    meta.setDamage(0);
                    item.setItemMeta(meta);
                }
            }
            armorContents = player.getInventory().getContents();
        }
        for (ItemStack item : armorContents) {
            if (item != null && item.getItemMeta() instanceof Damageable) {
                Damageable meta = (Damageable) item.getItemMeta();
                meta.setDamage(0);
                item.setItemMeta(meta);
            }
        }
        hotbarMessage.sendHotbarMessage(player, ChatColor.DARK_PURPLE + "You have used the Armor Haki technique");
    });

    static Technique conquerorsHaki = new Technique("conquerors_haki", "Conqueror's Haki", false, cooldownHelper.secondsToMiliseconds(300), (player, fruit, code) -> {
        for (org.bukkit.entity.Entity entity : player.getNearbyEntities(20, 20, 20)) {
            if (entity instanceof Player) {
                Player target = (Player) entity;
                double jumpstrength = 0.41999998697815;
                target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5 * 20, 255));
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 5 * 20, 255));
                target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 5 * 20, 255));
                target.getAttribute(Attribute.GENERIC_JUMP_STRENGTH).setBaseValue(0);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    target.getAttribute(Attribute.GENERIC_JUMP_STRENGTH).setBaseValue(jumpstrength);
                }, 5 * 20);
            }
        }
        hotbarMessage.sendHotbarMessage(player, ChatColor.DARK_PURPLE + "You have used the Conqueror's Haki technique");
    });
    static Technique zoltraakBarrage = new Technique("zoltraak", "Zoltraak", false, cooldownHelper.minutesToMiliseconds(2), (player, fruit, code) -> {
        final int beams = 12; // Number of beams in the circle
        final double radius = 10.0; // Distance from player to spawn beams
        final double beamRange = 40.0;
        final double beamStep = 0.5;
        final double beamDamage = 20.0;
        final World world = player.getWorld();
        final Location playerLoc = player.getLocation().clone();
        final Player target = chao.getClosestPlayer(playerLoc);
        if (target == null) {
            hotbarMessage.sendHotbarMessage(player, ChatColor.GRAY + "No target found for Zoltraak.");
            return;
        }

        for (int i = 0; i < beams; i++) {
            double angle = 2 * Math.PI * i / beams;
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);
            Location spawnLoc = playerLoc.clone().add(x, 1.5, z);

            // Calculate initial direction to target
            Vector toTarget = target.getLocation().add(0, 1, 0).toVector().subtract(spawnLoc.toVector()).normalize();

            // Each beam runs in its own task for smooth "turning"
            new BukkitRunnable() {
                boolean turned = false;
                Vector direction = toTarget.clone();
                Location current = spawnLoc.clone();
                int steps = 0;
                @Override
                public void run() {
                    if (steps++ > beamRange / beamStep) {
                        this.cancel();
                        return;
                    }
                    // Draw the beam
                    world.spawnParticle(Particle.END_ROD, current, 1, 0, 0, 0, 0);

                    // Damage entities in path (players only, except shooter)
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (p == player) continue;
                        if (p.getWorld() != world) continue;
                        if (p.getLocation().add(0, 1, 0).distance(current) < 1.2) {
                            p.damage(beamDamage, player);
                        }
                    }

                    if (current.getBlock().getType().isSolid()) {
                        // Small explosion (no fire, no block damage)
                        world.createExplosion(current, 2.0f, false, false, player);
                        this.cancel();
                        return;
                    }

                    // If not turned yet, check if target has dodged enough to trigger a turn
                    if (!turned) {
                        Vector newToTarget = target.getLocation().add(0, 1, 0).toVector().subtract(current.toVector()).normalize();
                        double angleBetween = direction.angle(newToTarget);
                        if (angleBetween > Math.toRadians(10)) { // If target moved enough
                            // 50% chance to turn (otherwise, beam misses)
                            if (Math.random() < 0.5) {
                                // Turn smoothly up to 90º
                                double maxTurn = Math.toRadians(90);
                                double turnAngle = Math.min(angleBetween, maxTurn);
                                Vector axis = direction.clone().crossProduct(newToTarget).normalize();
                                direction = direction.clone().rotateAroundAxis(axis, turnAngle).normalize();
                            }
                            turned = true;
                        }
                    }

                    // Move beam forward
                    current.add(direction.clone().multiply(beamStep));
                }
            }.runTaskTimer(plugin, i * 2, 1L); // Stagger beams for effect
        }

        hotbarMessage.sendHotbarMessage(player, ChatColor.AQUA + "Zoltraak Barrage unleashed!");
    });
    static Technique wrathOfElementalGod = new Technique("wrath_of_elemental_god", "Wrath of the Elemental God", true, cooldownHelper.secondsToMiliseconds(3600), (player, fruit, code) -> {
        if (!awakening.isFruitAwakened(player.getName(), fruitId)) {
            hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "You need to awaken your fruit to use this technique!");
            return;
        }
        Vector direction = new Vector(0, 10, 0);
        player.setVelocity(direction.multiply(5));
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 5 * 20, 5));
            for (int i = 0; i < 10; i++) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    player.getWorld().createExplosion(
                        player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(39)),
                        12.0F, false, true);
                }, 5L * i);
            }
        }, 10L);
        hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "You have used the Wrath of the Elemental God ultimate technique!");
        events.DeawakenFruit(player, fruitId);
    });
    static Technique tornado = new Technique("tornado", "Tornado", true, cooldownHelper.hour, (player, fruit, code) -> {
        if (!awakening.isFruitAwakened(player.getName(), fruitId)) {
            hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "You need to awaken your fruit to use this technique!");
            return;
        }
        Player user = player;

        // 2. Gather players in 60-block radius
        Location center = user.getLocation().add(0, 3, 0);
        List<Player> targets = user.getWorld().getPlayers().stream()
            .filter(p -> !p.equals(user) && p.getLocation().distance(user.getLocation()) <= 60)
            .collect(Collectors.toList());

        // 3. Teleport all to center (+3 up)
        for (Player p : targets) {
            p.teleport(center);
        }

        // 4. Spiral push: schedule repeating task for 5 seconds (100 ticks)
        int spiralDuration = 100;
        int numPlayers = targets.size();
        double spiralRadius = 8; // distance from center
        double spiralHeight = 10; // total height to push up
        double spiralTurns = 3; // number of spiral turns

        new BukkitRunnable() {
            int tick = 0;
            @Override
            public void run() {
                if (tick >= spiralDuration || targets.isEmpty()) {
                    this.cancel();
                    return;
                }
                double angleStep = 2 * Math.PI / numPlayers;
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

        // 5. After 5 seconds, damage and launch players
        new BukkitRunnable() {
            @Override
            public void run() {
                int xp = user.getLevel();
                for (Player p : targets) {
                    p.damage(5 * xp, user);
                }
                // Next tick: set velocity away from user, multiplied by xp
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

        // 6. 3 ticks after velocity set, generate 2-block radius effect at tornado center
        new BukkitRunnable() {
            @Override
            public void run() {
                Location effectLoc = center.clone();
                user.getWorld().createExplosion(effectLoc, 1.0F, false, false);
                // Add any other effects you want here
            }
        }.runTaskLater(plugin, spiralDuration + 4);
        hotbarMessage.sendHotbarMessage(player, ChatColor.GREEN + "You have used the Tornado technique");
    });
}
