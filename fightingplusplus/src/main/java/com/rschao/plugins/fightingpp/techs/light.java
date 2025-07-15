package com.rschao.plugins.fightingpp.techs;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.rschao.plugins.fightingpp.Plugin;
import com.rschao.plugins.fightingpp.events.awakening;
import com.rschao.plugins.fightingpp.events.events;
import com.rschao.plugins.fightingpp.projectiles.lightball;
import com.rschao.plugins.techapi.tech.Technique;
import com.rschao.plugins.techapi.tech.cooldown.cooldownHelper;
import com.rschao.plugins.techapi.tech.feedback.hotbarMessage;
import com.rschao.plugins.techapi.tech.register.TechRegistry;

import net.md_5.bungee.api.ChatColor;

public class light {
    static final String fruitId = "light";
    static final Plugin plugin = Plugin.getPlugin(Plugin.class);

    public static void Register() {
        TechRegistry.registerTechnique(fruitId, lightballTech);
        TechRegistry.registerTechnique(fruitId, lightBurst);
        TechRegistry.registerTechnique(fruitId, solarFlare);
    }

    static Technique lightballTech = new Technique("lightball", "Lightball", false, cooldownHelper.secondsToMiliseconds(5), (player, fruit, code) -> {
        lightball lb = new lightball(player.getLocation(), player);
        lb.launch();
        hotbarMessage.sendHotbarMessage(player, ChatColor.YELLOW + "You have used the Lightball technique!");
    });

    static Technique lightBurst = new Technique("light_burst", "Light Burst", false, cooldownHelper.secondsToMiliseconds(60), (player, fruit, code) -> {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getLocation().distance(player.getLocation()) <= 20 && p.getLocation().distance(player.getLocation()) > 3) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 5 * 20, 255));
                p.getLocation().getWorld().spawnParticle(Particle.END_ROD, p.getLocation(), 30);
            }
        }
        hotbarMessage.sendHotbarMessage(player, ChatColor.YELLOW + "You have used the Light Burst technique!");
    });

    static Technique solarFlare = new Technique("solar_flare", "Solar Flare", true, cooldownHelper.secondsToMiliseconds(3600), (player, fruit, code) -> {
        if (!awakening.isFruitAwakened(player.getName(), fruitId)) {
            hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "You need to awaken your fruit to use this technique!");
            return;
        }
        player.getWorld().createExplosion(player.getLocation(), 30.0F, true, false);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getLocation().distance(player.getLocation()) <= 40 && p.getLocation().distance(player.getLocation()) > 5) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 5 * 20, 255));
                p.getLocation().getWorld().spawnParticle(Particle.END_ROD, p.getLocation(), 30);
            }
        }
        hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "You have used the Solar Flare ultimate technique!");
        events.DeawakenFruit(player, fruitId);
    });
}
