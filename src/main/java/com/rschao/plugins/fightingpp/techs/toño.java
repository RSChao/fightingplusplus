package com.rschao.plugins.fightingpp.techs;

import com.rschao.plugins.fightingpp.Plugin;
import com.rschao.plugins.fightingpp.events.awakening;
import com.rschao.plugins.fightingpp.events.events;
import com.rschao.plugins.techniqueAPI.tech.Technique;
import com.rschao.plugins.techniqueAPI.tech.TechniqueMeta;
import com.rschao.plugins.techniqueAPI.tech.cooldown.cooldownHelper;
import com.rschao.plugins.techniqueAPI.tech.register.TechRegistry;
import com.rschao.plugins.techniqueAPI.tech.selectors.TargetSelectors;
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
        Plugin.registerFruitID(FRUIT_ID);
    }

    static Technique lewis = new Technique(
            "lewis",
            "Lewis de cinemática formulada",
            new TechniqueMeta(true, cooldownHelper.hour * 2, List.of("Ultimate: Lewis")),
            TargetSelectors.self(),
            (ctx, token) -> {
                Player p = ctx.caster();
                Location center = p.getLocation();
                World world = p.getWorld();

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
                        for (Player other : nearbyPlayers) {
                            if (other != p) {
                                Bukkit.getScheduler().runTaskLater(Plugin.getPlugin(Plugin.class), () -> {
                                    double hearts = other.getAttribute(Attribute.MAX_HEALTH).getBaseValue();
                                    other.getAttribute(Attribute.MAX_HEALTH).setBaseValue(hearts - 20);
                                    p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 5 * 20, 5));
                                    Bukkit.getScheduler().runTaskLater(Plugin.getPlugin(Plugin.class), () -> other.getAttribute(Attribute.MAX_HEALTH).setBaseValue(hearts), 60 * 20);
                                }, 5);
                            } else {
                                p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 5 * 20, 5));
                                p.damage(300);
                                p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 60 * 20, 4));
                            }
                        }
                    }
                };
                danceTask = new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (Player pl : nearbyPlayers) {
                            pl.setVelocity(new Vector(0, 500, 0));
                            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                pl.getWorld().createExplosion(pl.getLocation(), 50, false, false);
                                pl.setVelocity(new Vector(0, 0, 0));
                            }, 20);
                        }
                        noroiTask.runTaskLater(plugin, 40L);
                    }
                };
                for (int i = 0; i < n; i++) {
                    double angle = 2 * Math.PI * i / n;
                    double x = center.getX() + radius * Math.cos(angle);
                    double z = center.getZ() + radius * Math.sin(angle);
                    Location loc = new Location(world, x, center.getY(), z);
                    nearbyPlayers.get(i).teleport(loc);
                }

                spiralTask = new BukkitRunnable() {
                    double spiralRadius = radius;
                    double yOffset = 0;
                    int tick = 0;
                    final double spiralStep = 0.2;
                    final double radiusStep = 0.1;

                    @Override
                    public void run() {
                        tick++;
                        spiralRadius = Math.max(0, spiralRadius - radiusStep);
                        yOffset += spiralStep;

                        for (int i = 0; i < n; i++) {
                            double angle = 2 * Math.PI * i / n + tick * 0.2;
                            double x = center.getX() + spiralRadius * Math.cos(angle);
                            double z = center.getZ() + spiralRadius * Math.sin(angle);
                            double y = center.getY() + yOffset;
                            Location loc = new Location(world, x, y, z);
                            nearbyPlayers.get(i).teleport(loc);
                        }

                        if (spiralRadius <= 0.2) {
                            this.cancel();
                            danceTask.runTaskLater(plugin, 2);
                        }
                    }
                };
                spiralTask.runTaskTimer(plugin, 10L, 2L);
            },
            (ctx, token) -> {
                Player p = ctx.caster();
                if (!awakening.isFruitAwakened(p.getName(), FRUIT_ID)) {
                    // mensaje coherente con hotbarMessage
                    // Puede usar hotbarMessage.sendHotbarMessage
                    com.rschao.plugins.techniqueAPI.tech.feedback.hotbarMessage.sendHotbarMessage(p, "You must awaken your fruit to use this technique!");
                    return false;
                }
                com.rschao.plugins.fightingpp.events.events.DeawakenFruit(p, FRUIT_ID);
                return true;
            }
    );
}
