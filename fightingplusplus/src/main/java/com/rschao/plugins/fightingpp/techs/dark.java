package com.rschao.plugins.fightingpp.techs;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.rschao.plugins.fightingpp.Plugin;
import com.rschao.plugins.fightingpp.events.awakening;
import com.rschao.plugins.fightingpp.events.events;
import com.rschao.plugins.fightingpp.projectiles.darkball;
import com.rschao.plugins.techapi.tech.Technique;
import com.rschao.plugins.techapi.tech.cooldown.cooldownHelper;
import com.rschao.plugins.techapi.tech.feedback.hotbarMessage;
import com.rschao.plugins.techapi.tech.register.TechRegistry;

import net.md_5.bungee.api.ChatColor;

public class dark {
    static final String fruitId = "dark";
    static final Plugin plugin = Plugin.getPlugin(Plugin.class);

    public static void Register() {
        TechRegistry.registerTechnique(fruitId, darkballTech);
        TechRegistry.registerTechnique(fruitId, darkBurst);
        TechRegistry.registerTechnique(fruitId, blackHole);
    }

    static Technique darkballTech = new Technique("darkball", "Darkball", false, cooldownHelper.secondsToMiliseconds(5), (player, fruit, code) -> {
        darkball lb = new darkball(player.getLocation(), player);
        lb.launch();
        hotbarMessage.sendHotbarMessage(player, ChatColor.DARK_PURPLE + "You have used the Darkball technique!");
    });

    static Technique darkBurst = new Technique("dark_burst", "Dark Burst", false, cooldownHelper.secondsToMiliseconds(60), (player, fruit, code) -> {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getLocation().distance(player.getLocation()) <= 20 && p.getLocation().distance(player.getLocation()) > 3) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5 * 20, 255));
                p.damage(10);
                p.getLocation().getWorld().spawnParticle(Particle.END_ROD, p.getLocation(), 30);
            }
        }
        hotbarMessage.sendHotbarMessage(player, ChatColor.DARK_PURPLE + "You have used the Dark Burst technique!");
    });

    static Technique blackHole = new Technique("black_hole", "Black Hole", true, cooldownHelper.secondsToMiliseconds(3600), (player, fruit, code) -> {
        if (!awakening.isFruitAwakened(player.getName(), fruitId)) {
            hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "You need to awaken your fruit to use this technique!");
            return;
        }
        player.getWorld().createExplosion(player.getLocation(), 30.0F, true, false);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getLocation().distance(player.getLocation()) <= 40 && p.getLocation().distance(player.getLocation()) > 5) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5 * 20, 255));
                p.getLocation().getWorld().spawnParticle(Particle.END_ROD, p.getLocation(), 30);
            }
        }
        hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "You have used the Black Hole ultimate technique!");
        events.DeawakenFruit(player, fruitId);
    });
}
