package com.rschao.plugins.fightingpp.techs;

import com.rschao.plugins.fightingpp.Plugin;
import com.rschao.plugins.fightingpp.events.events;
import com.rschao.plugins.techniqueAPI.tech.Technique;
import com.rschao.plugins.techniqueAPI.tech.TechniqueMeta;
import com.rschao.plugins.techniqueAPI.tech.cooldown.cooldownHelper;
import com.rschao.plugins.techniqueAPI.tech.register.TechRegistry;
import com.rschao.plugins.techniqueAPI.tech.selectors.TargetSelectors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Set;

import static com.rschao.plugins.fightingpp.techs.chao.sphereAround;

public class freeze {
    static final String fruitId = "freeze";
    static final Plugin plugin = Plugin.getPlugin(Plugin.class);
    public static void registerTechs() {
        TechRegistry.registerTechnique(fruitId, blizzard);
        TechRegistry.registerTechnique(fruitId, ice_wall);
        TechRegistry.registerTechnique(fruitId, ice_blade);
        TechRegistry.registerTechnique(fruitId, frost_grip);
        TechRegistry.registerTechnique(fruitId, ice_glide);
        TechRegistry.registerTechnique(fruitId, world_of_ice);
    }

    static Technique blizzard = new Technique(
            "blizzard_ice",
            "Ice Barrage",
            new TechniqueMeta(false, cooldownHelper.minutesToMiliseconds(3), List.of("Launches ice arrows that slow enemies on hit.")),
            TargetSelectors.self(),
            (ctx, token) -> {
                Player player = ctx.caster();
                for (int i = 0; i < 15; i++) {
                    int delay = i * 2;
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        Vector direction = player.getEyeLocation().getDirection().normalize();
                        Vector randomOffset = new Vector(
                                (Math.random() - 0.5) * 0.2,
                                (Math.random() - 0.5) * 0.2,
                                (Math.random() - 0.5) * 0.2
                        );
                        Vector finalDirection = direction.add(randomOffset).multiply(5);
                        org.bukkit.entity.Arrow arrow = player.launchProjectile(org.bukkit.entity.Arrow.class, finalDirection);
                        arrow.setDamage(20.0);
                        arrow.addCustomEffect(PotionEffectType.SLOWNESS.createEffect(200, 7), true);
                        arrow.setGravity(false);
                        arrow.setVelocity(finalDirection);
                        arrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
                    }, delay);
                }
            }
    );

    static Technique ice_wall = new Technique(
            "ice_wall",
            "Ice Wall",
            new TechniqueMeta(false, cooldownHelper.minutesToMiliseconds(5), List.of("Creates a wall of ice that blocks projectiles and slows enemies.")),
            TargetSelectors.self(),
            (ctx, token) -> {
                Set<Block> sphere = sphereAround(ctx.caster().getLocation(), 5);
                for (Block b : sphere) {
                    if (b.getType().equals(Material.AIR)) {
                        b.setType(Material.BLUE_ICE);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            b.setType(Material.AIR);
                        }, 60L);
                    }
                }
            }
    );

    static Technique ice_blade = new Technique(
            "ice_blade",
            "Ice Blade",
            new TechniqueMeta(false, cooldownHelper.minutesToMiliseconds(5), List.of("Your blade becomes covered in ice, dealing extra damage and slowing enemies on hit.")),
            TargetSelectors.self(),
            (ctx, token) -> {
                Player player = ctx.caster();
                events.hasIce.put(player.getName(), true);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    events.hasIce.put(player.getName(), false);
                }, 30*20L);
            }
    );

    static Technique frost_grip = new Technique(
            "frost_grip",
            "Frost Grip",
            new TechniqueMeta(false, cooldownHelper.minutesToMiliseconds(4), List.of("Grabs an enemy and encases them in ice, immobilizing them for a short duration.", "Knockback is saved until the effect ends.")),
            TargetSelectors.self(),
            (ctx, token) -> {
                Player target = chao.getClosestPlayer(ctx.caster().getLocation());
                if (target != null && target.getLocation().distance(ctx.caster().getLocation()) < 10) {
                    events.isStasis.put(target.getName(), true);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        events.isStasis.put(target.getName(), false);
                        Vector v = events.StasisVector.get(ctx.caster().getName());
                        if (v != null) {
                            ctx.caster().sendMessage("Releasing " + target.getName() + " with velocity " + v);
                            target.setVelocity(v.multiply(3));
                            events.StasisVector.remove(target.getName());
                        }
                    }, 100L);
                }

            }
    );

    static Technique ice_glide = new Technique(
            "ice_glide",
            "Ice Glide",
            new TechniqueMeta(false, cooldownHelper.minutesToMiliseconds(3), List.of("Glide across the ground, increasing movement speed and leaving a trail of ice.")),
            TargetSelectors.self(),
            (ctx, token) -> {
                Player player = ctx.caster();
                player.addPotionEffect(PotionEffectType.SPEED.createEffect(200, 2), true);
                Bukkit.getScheduler().runTaskTimer(plugin, new BukkitRunnable() {
                    int ticks = 0;
                    @Override
                    public void run() {
                        if(ticks >= 200) {
                            this.cancel();
                            return;
                        }
                        ticks++;
                        Block b = player.getLocation().subtract(0, 1, 0).getBlock();
                        if(b.getType().equals(Material.AIR) || b.getType().equals(Material.DIRT) || b.getType().equals(Material.STONE) || b.getType().equals(Material.GRASS_BLOCK)) {
                            b.setType(Material.BLUE_ICE);
                            BlockPlaceEvent ev = new BlockPlaceEvent(b, b.getState(), player.getLocation().subtract(new Vector(0, -1, 0)).getBlock(), player.getInventory().getItemInMainHand(), player, true);
                            Bukkit.getPluginManager().callEvent(ev);
                        }
                    }
                }, 0, 1);
            }
    );

    static Technique world_of_ice = new Technique(
            "world_of_ice",
            "World of Ice",
            new TechniqueMeta(true, cooldownHelper.hour/2, List.of("Creates a large area of ice around you, slowing and damaging enemies that enter it.")),
            TargetSelectors.self(),
            (ctx, token) -> {
                Player player = ctx.caster();
                Vector center = player.getLocation().toVector();
                new BukkitRunnable() {
                    int ticks = 0;
                    @Override
                    public void run() {
                        if(ticks >= 40) {
                            this.cancel();
                            return;
                        }
                        ticks++;
                        for (Player p : player.getWorld().getPlayers()) {
                            if (p.getLocation().toVector().distance(center) < 100 && !p.equals(player)) {
                                p.setVelocity(new Vector(0, 0, 0));
                                p.damage(30.0);
                                p.addPotionEffect(PotionEffectType.SLOWNESS.createEffect(40, 4), true);
                                if(ticks ==39){
                                    p.damage(1000.0);
                                }
                            }
                        }
                    }
                }.runTaskTimer(plugin, 0, 2);
            }
    );


}
