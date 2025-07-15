package com.rschao.plugins.fightingpp.techs;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.WindCharge;
import org.bukkit.util.Vector;

import com.rschao.plugins.fightingpp.Plugin;
import com.rschao.plugins.fightingpp.events.awakening;
import com.rschao.plugins.fightingpp.events.events;
import com.rschao.plugins.techapi.tech.Technique;
import com.rschao.plugins.techapi.tech.cooldown.cooldownHelper;
import com.rschao.plugins.techapi.tech.feedback.hotbarMessage;
import com.rschao.plugins.techapi.tech.register.TechRegistry;

import net.md_5.bungee.api.ChatColor;

public class air {
    static final String fruitId = "air";
    static final Plugin plugin = Plugin.getPlugin(Plugin.class);

    public static void Register() {
        TechRegistry.registerTechnique(fruitId, airball);
        TechRegistry.registerTechnique(fruitId, airBurst);
        TechRegistry.registerTechnique(fruitId, tornado);
    }

    static Technique airball = new Technique("airball", "Airball", false, cooldownHelper.secondsToMiliseconds(5), (player, fruit, code) -> {
        player.launchProjectile(WindCharge.class);
        hotbarMessage.sendHotbarMessage(player, ChatColor.AQUA + "You have used the Airball technique!");
    });

    static Technique airBurst = new Technique("air_burst", "Air Burst", false, cooldownHelper.secondsToMiliseconds(60), (player, fruit, code) -> {
        Location location = player.getLocation();
        player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, location, 30);
        player.getWorld().playSound(location, Sound.ENTITY_ENDER_DRAGON_FLAP, 1, 1);

        for (org.bukkit.entity.Entity entity : location.getWorld().getEntities()) {
            if (entity.getLocation().distance(location) <= 20 && entity != player) {
                Vector direction = entity.getLocation().toVector().subtract(location.toVector()).normalize();
                entity.setVelocity(direction.multiply(8));
            }
        }
        hotbarMessage.sendHotbarMessage(player, ChatColor.AQUA + "You have used the Air Burst technique!");
    });

    static Technique tornado = new Technique("tornado", "Tornado", true, cooldownHelper.secondsToMiliseconds(3600), (player, fruit, code) -> {
        if (!awakening.isFruitAwakened(player.getName(), fruitId)) {
            hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "You need to awaken your fruit to use this technique!");
            return;
        }
        Location location = player.getLocation();
        player.getWorld().spawnParticle(Particle.EXPLOSION, location, 30);
        player.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);

        for (org.bukkit.entity.Entity entity : location.getWorld().getEntities()) {
            if (entity instanceof Player && entity.getLocation().distance(location) <= 50 && entity != player) {
                Vector direction = entity.getLocation().toVector().subtract(location.toVector()).normalize();
                entity.setVelocity(direction.multiply(40));
            }
        }
        hotbarMessage.sendHotbarMessage(player, ChatColor.GREEN + "You have used the Tornado ultimate technique!");
        events.DeawakenFruit(player, fruitId);
    });
}
