package com.rschao.plugins.fightingpp.techs;

import com.rschao.events.soulEvents;
import com.rschao.plugins.fightingpp.Plugin;
import com.rschao.plugins.fightingpp.events.awakening;
import com.rschao.plugins.fightingpp.events.events;
import com.rschao.plugins.techniqueAPI.tech.Technique;
import com.rschao.plugins.techniqueAPI.tech.TechniqueMeta;
import com.rschao.plugins.techniqueAPI.tech.cooldown.cooldownHelper;
import com.rschao.plugins.techniqueAPI.tech.feedback.hotbarMessage;
import com.rschao.plugins.techniqueAPI.tech.register.TechRegistry;
import com.rschao.plugins.techniqueAPI.tech.selectors.TargetSelectors;
import de.slikey.effectlib.effect.BleedEffect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

public class YoruKami {
    static final String fruitId = "shadow";
    public static void register(){
        Plugin.registerFruitID(fruitId);
        TechRegistry.registerTechnique(fruitId, shadowDash);
        TechRegistry.registerTechnique(fruitId, soulless);
        TechRegistry.registerTechnique(fruitId, shadowAbyss);
        TechRegistry.registerTechnique(fruitId, shadowWalk);
        TechRegistry.registerTechnique(fruitId, shadowWorld);
        TechRegistry.registerTechnique(fruitId, shadowStrikes);
        TechRegistry.registerTechnique(fruitId, shadowBleed);
        TechRegistry.registerTechnique(fruitId, shadowKick);
        TechRegistry.registerTechnique(fruitId, shadowPunch);
        TechRegistry.registerTechnique(fruitId, omegaBlast);
        TechRegistry.registerTechnique(fruitId, shadowMassacre);
    }

    static Technique shadowDash = new Technique("shadow_dash","Shadow Dash",new TechniqueMeta(false, cooldownHelper.minutesToMiliseconds(7), List.of("Proporciona un pequeño salto", "Daña a un jugador cercano")), TargetSelectors.self(),(ctx, token)->{
        Vector direction = ctx.caster().getLocation().getDirection().normalize().multiply(2);
        Player player = ctx.caster();
        player.setVelocity(direction.normalize());

        Player p = chao.getClosestPlayer(player.getLocation());
        if(p.getLocation().distance(player.getLocation()) < 10){
            p.damage(400, player);
        }
    });

    static Technique soulless = new Technique("soulless","Soulless",new TechniqueMeta(false, cooldownHelper.minutesToMiliseconds(10), List.of("Elimina las almas del jugador durante un tiempo")), TargetSelectors.self(),(ctx, token)->{
        Player player = chao.getClosestPlayer(ctx.caster().getLocation());
        Bukkit.getScheduler().runTaskLater(Plugin.getPlugin(Plugin.class), () -> {
            int soul1 = soulEvents.GetSoulN(player);
            int soul2 = soulEvents.GetSecondSoulN(player);
            int[] souls = new int[]{soul1, soul2};
            boolean hasBannedSoul = false;
            int[] newSouls = new int[]{-1, -1};
            for(int x : souls){
                if(x == -5 || x == 30 || x == 66){
                    hasBannedSoul = true;
                }
                if(hasBannedSoul && newSouls[0] == -1){
                    newSouls[0] = x;
                    newSouls[1] = -1;
                } else if(hasBannedSoul && newSouls[1] == -1){
                    newSouls[1] = x;
                }
                if(x == soul1){
                    newSouls[0] = -1;
                } else if(x == soul2){
                    newSouls[1] = -1;
                }
                soulEvents.setSouls(player, newSouls[0], newSouls[1]);
                soulEvents.setSoullessTimed(player, true, 180*20);

                Bukkit.getScheduler().runTaskLater(Plugin.getPlugin(Plugin.class), () -> {
                    soulEvents.setSouls(player, soul1, soul2);
                }, 20 * 180);
            }
        }, 2);
    });

    static Technique shadowAbyss = new Technique("shadow_abyss","Shadow Abyss",new TechniqueMeta(false, cooldownHelper.minutesToMiliseconds(15), List.of("Crea un abismo de sombras que daña a los enemigos cercanos")), TargetSelectors.self(),(ctx, token)->{
        Player player = ctx.caster();
        Bukkit.getScheduler().runTaskLater(Plugin.getPlugin(Plugin.class), () -> {
            Player player2 = chao.getClosestPlayer(player.getLocation());
            Location loc = player.getLocation().clone();
            loc.setY(player.getLocation().getY() + 60);
            player2.teleport(loc);
            player2.setFallDistance(200);
            player2.addPotionEffect(PotionEffectType.BLINDNESS.createEffect(20*20,1));
        }, 2);
    });

    static Technique shadowWalk = new Technique("shadow_walk","Shadow Walk",new TechniqueMeta(false, cooldownHelper.minutesToMiliseconds(3), List.of("Te teletransporta a un jugador cercano")), TargetSelectors.self(),(ctx, token)->{
        Player player = ctx.caster();
        Bukkit.getScheduler().runTaskLater(Plugin.getPlugin(Plugin.class), () -> {
            player.addPotionEffect(PotionEffectType.SPEED.createEffect(60*3*20,2));
            player.addPotionEffect(PotionEffectType.STRENGTH.createEffect(60*2*20,4));
            player.addPotionEffect(PotionEffectType.INVISIBILITY.createEffect(60*3*20,1));
        }, 2);
    });

    static Technique shadowWorld = new Technique("shadow_world","Shadow World",new TechniqueMeta(true, cooldownHelper.minutesToMiliseconds(4), List.of("Crea un mundo de sombras que daña a los enemigos cercanos")), TargetSelectors.self(),(ctx, token)->{
        Player player = ctx.caster();
        Bukkit.getScheduler().runTaskLater(Plugin.getPlugin(Plugin.class), () -> {

            int days = (int) (Bukkit.getWorlds().get(0).getTime() / 24000);
            player.getWorld().setTime(24000 * (days + 1) + 14000);
            List<LivingEntity> listiya = new ArrayList<>();
            for(Entity e : player.getNearbyEntities(50, 50, 50)){
                if(!(e instanceof LivingEntity)) continue;
                if(e instanceof Player) continue;
                LivingEntity le = (LivingEntity) e;
                if(listiya.size() < 5 && (new Random()).nextInt(100) < 25){
                    listiya.add(le);
                    le.addPotionEffect(PotionEffectType.STRENGTH.createEffect(5*20*3600,4));
                    le.getAttribute(Attribute.MAX_HEALTH).setBaseValue(100);
                }


            }

            for(Entity e : player.getNearbyEntities(50, 50, 50)){
                if(!(e instanceof LivingEntity)) continue;
                LivingEntity le = (LivingEntity) e;
                if(!listiya.contains(le)){
                    le.damage(1000, player);
                }
            }
        }, 2);
    });

    static Technique shadowStrikes = new Technique("shadow_strikes","Shadow Strikes",new TechniqueMeta(false, cooldownHelper.minutesToMiliseconds(10), List.of("Lanza una serie de ataques rapidos")), TargetSelectors.self(),(ctx, token)->{
        Player player = ctx.caster();
        Bukkit.getScheduler().runTaskLater(Plugin.getPlugin(Plugin.class), () -> {
            for (int i = 0; i < 10; i++) {
                Bukkit.getScheduler().runTaskLater(Plugin.getPlugin(Plugin.class), () -> {
                    Player p = chao.getClosestPlayer(player.getLocation());
                    if(p.getLocation().distance(player.getLocation()) < 10){
                        p.setNoDamageTicks(0);
                        p.damage(20, player);
                    }
                }, i * 2);
            }
        }, 2);
    });

    static Technique shadowBleed = new Technique("shadow_bleed","Shadow Bleed",new TechniqueMeta(false, cooldownHelper.minutesToMiliseconds(8), List.of("Causa sangrado a un jugador cercano")), TargetSelectors.self(),(ctx, token)->{
        Player player = ctx.caster();
        Bukkit.getScheduler().runTaskLater(Plugin.getPlugin(Plugin.class), () -> {
            Player p = chao.getClosestPlayer(player.getLocation());
            if(p.getLocation().distance(player.getLocation()) < 30){
                BleedEffect effect = new BleedEffect(Plugin.getEffectManager());
                effect.setEntity(p);
                effect.start();
                double h = p.getHealth()/2;
                player.setHealth(Math.max(1, h));
                Bukkit.getScheduler().runTaskLater(Plugin.getPlugin(Plugin.class), () -> {
                    effect.cancel();
                }, 2);
            }
        }, 2);
    });

    static Technique shadowKick = new Technique("shadow_kick","Shadow Kick",new TechniqueMeta(false, cooldownHelper.minutesToMiliseconds(5), List.of("Lanza por los aires a un jugador cercano")), TargetSelectors.self(),(ctx, token)->{
        Player player = ctx.caster();
        Bukkit.getScheduler().runTaskLater(Plugin.getPlugin(Plugin.class), () -> {
            Player p = chao.getClosestPlayer(player.getLocation());
            if(p.getLocation().distance(player.getLocation()) < 20){
                p.damage(600, player);
            }
        }, 2);
    });

    //ulti
    static Technique shadowPunch = new Technique("shadow_punch","Shadow Punch",new TechniqueMeta(true, cooldownHelper.minutesToMiliseconds(45), List.of("Lanza un poderoso puñetazo de sombras que daña a un enemigo")), TargetSelectors.self(),(ctx, token)->{
        Player player = ctx.caster();
        Bukkit.getScheduler().runTaskLater(Plugin.getPlugin(Plugin.class), () -> {
            Player p = chao.getClosestPlayer(player.getLocation());
            if(p.getLocation().distance(player.getLocation()) < 50){
                p.damage(800, player);
                p.setNoDamageTicks(0);

                Bukkit.getScheduler().runTaskLater(Plugin.getPlugin(Plugin.class), () -> {
                    p.getWorld().createExplosion(p.getLocation(), 10, false, false);

                    Bukkit.getScheduler().runTaskLater(Plugin.getPlugin(Plugin.class), () -> {
                        p.setNoDamageTicks(0);
                        p.damage(800, player);
                    }, 2);
                }, 2);
            }
        }, 2);
    }, (ctx, token) -> {
        Player player = ctx.caster();
        // Check if the player's fruit is awakened
        if (!awakening.isFruitAwakened(player.getName(), fruitId)) {
            hotbarMessage.sendHotbarMessage(player, "You need to awaken your fruit to use this technique!");
            return false;
        }
        // Deawaken the fruit after using the ultimate
        events.DeawakenFruit(player, fruitId);
        return true;
    });

    //ulti
    static Technique omegaBlast = new Technique("omega_blast","Omega Blast",new TechniqueMeta(true, cooldownHelper.minutesToMiliseconds(60), List.of("Lanza un poderoso ataque de sombras que daña a todos los enemigos cercanos")), TargetSelectors.self(),(ctx, token)->{
        Player player = ctx.caster();
        Bukkit.getScheduler().runTaskLater(Plugin.getPlugin(Plugin.class), () -> {
            player.getWorld().createExplosion(player.getLocation(), 70, true, true);

            Bukkit.getScheduler().runTaskLater(Plugin.getPlugin(Plugin.class), () -> {
                for(Entity e : player.getNearbyEntities(50, 50, 50)){
                    if(!(e instanceof LivingEntity)) continue;
                    LivingEntity le = (LivingEntity) e;
                    le.damage(1000, player);
                }
            }, 2);
        }, 2);
    }, (ctx, token) -> {
        Player player = ctx.caster();
        // Check if the player's fruit is awakened
        if (!awakening.isFruitAwakened(player.getName(), fruitId)) {
            hotbarMessage.sendHotbarMessage(player, "You need to awaken your fruit to use this technique!");
            return false;
        }
        // Deawaken the fruit after using the ultimate
        events.DeawakenFruit(player, fruitId);
        return true;
    });

    //ulti (check if this and "dario" are awakened at the same time)
    static Technique shadowMassacre = new Technique("shadow_massacre","Shadow Massacre",new TechniqueMeta(true, cooldownHelper.hour*10, List.of(
            "Crea una esfera de destrucción",
            "3 pops"
    )), TargetSelectors.self(),(ctx, token)->{
        Player player = ctx.caster();
        final int radius = 50;
        final Location centerLoc = player.getLocation().getBlock().getLocation(); // center block-aligned
        final double centerY = centerLoc.getY();

        // Save and break (replace with AIR), sending BlockBreakEvent for each
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Location loc = centerLoc.clone().add(x, y, z);
                    double dist = centerLoc.distance(loc);
                    if (dist <= radius) {
                        Block b = loc.getBlock();
                        BlockBreakEvent evt = new BlockBreakEvent(b, player);
                        Bukkit.getPluginManager().callEvent(evt);
                        try {
                            b.setType(Material.AIR);
                        } catch (Exception ignored) {}
                    }
                }
            }
        }
        for(Player pl : player.getWorld().getPlayers()){
            Location plLoc = pl.getLocation();
            if(plLoc.distance(centerLoc) <= radius && plLoc.getY() < centerY){
                for(int i = 0; i < 3; i++){
                    Bukkit.getScheduler().runTaskLater(Plugin.getPlugin(Plugin.class), () -> {
                        Bukkit.getScheduler().runTaskLater(Plugin.getPlugin(Plugin.class), () -> {
                            pl.damage(1000);
                        }, 2);
                    }, i * 10);
                }
            }


        }
    }, (ctx, token) -> {
        Player player = ctx.caster();
        // Check if the player's fruit is awakened
        if (!awakening.isFruitAwakened(player.getName(), fruitId) || !awakening.isFruitAwakened(player.getName(), "dario")) {
            hotbarMessage.sendHotbarMessage(player, "You need to awaken your fruits to use this technique!");
            return false;
        }
        // Deawaken the fruit after using the ultimate
        events.DeawakenFruit(player, fruitId);
        events.DeawakenFruit(player, "dario");
        return true;
    });

    public static Set<Block> sphereAround(Location location, int radius) {
        Set<Block> sphere = new HashSet<Block>();
        Block center = location.getBlock();
        for(int x = -radius; x <= radius; x++) {
            for(int y = -radius; y <= radius; y++) {
                for(int z = -radius; z <= radius; z++) {
                    Block b = center.getRelative(x, y, z);
                    double dist = center.getLocation().distance(b.getLocation());
                    if(dist <= radius && dist > (radius - 2)) {
                        sphere.add(b);
                    }
                }
            }
        }
        return sphere;
    }
}
