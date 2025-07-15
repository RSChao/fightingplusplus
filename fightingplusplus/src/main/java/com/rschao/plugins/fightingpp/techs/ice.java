package com.rschao.plugins.fightingpp.techs;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.Particle;

import com.rschao.plugins.fightingpp.Plugin;
import com.rschao.plugins.fightingpp.events.awakening;
import com.rschao.plugins.fightingpp.events.events;
import com.rschao.plugins.techapi.tech.Technique;
import com.rschao.plugins.techapi.tech.cooldown.cooldownHelper;
import com.rschao.plugins.techapi.tech.feedback.hotbarMessage;
import com.rschao.plugins.techapi.tech.register.TechRegistry;

import net.md_5.bungee.api.ChatColor;

public class ice {
    static final String fruitId = "ice";
    static final Plugin plugin = Plugin.getPlugin(Plugin.class);

    public static void Register() {
        TechRegistry.registerTechnique(fruitId, iceball);
        TechRegistry.registerTechnique(fruitId, airBurst);
        TechRegistry.registerTechnique(fruitId, blizzard);
    }

    static Technique iceball = new Technique("iceball", "Iceball", false, cooldownHelper.secondsToMiliseconds(5), (player, fruit, code) -> {
        player.launchProjectile(Snowball.class);
        hotbarMessage.sendHotbarMessage(player, ChatColor.AQUA + "You have used the Iceball technique!");
    });

    static Technique airBurst = new Technique("ice_burst", "Ice Burst", false, cooldownHelper.secondsToMiliseconds(60), (player, fruit, code) -> {
        Location location = player.getLocation();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getLocation().distance(location) <= 20) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 5 * 20, 255));
                p.getLocation().getWorld().spawnParticle(Particle.SNOWFLAKE, p.getLocation(), 30);
            }
        }
        hotbarMessage.sendHotbarMessage(player, ChatColor.AQUA + "You have used the Air Burst technique!");
    });

    static Technique blizzard = new Technique("blizzard", "Blizzard", true, cooldownHelper.secondsToMiliseconds(3600), (player, fruit, code) -> {
        if (!awakening.isFruitAwakened(player.getName(), fruitId)) {
            hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "You need to awaken your fruit to use this technique!");
            return;
        }
        player.getWorld().createExplosion(player.getLocation(), 50.0F, true, false);
        hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "You have used the Blizzard ultimate technique!");
        events.DeawakenFruit(player, fruitId);
    });
}
