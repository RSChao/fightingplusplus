package com.rschao.plugins.fightingpp.techs;

import com.rschao.plugins.fightingpp.Plugin;
import com.rschao.plugins.fightingpp.events.awakening;
import com.rschao.plugins.fightingpp.events.events;
import com.rschao.plugins.techapi.tech.Technique;
import com.rschao.plugins.techapi.tech.cooldown.cooldownHelper;
import com.rschao.plugins.techapi.tech.feedback.hotbarMessage;
import com.rschao.plugins.techapi.tech.register.TechRegistry;

import net.md_5.bungee.api.ChatColor;

public class fire {
    static final String fruitId = "fire";
    static final Plugin plugin = Plugin.getPlugin(Plugin.class);

    public static void Register() {
        TechRegistry.registerTechnique(fruitId, fireball);
        TechRegistry.registerTechnique(fruitId, flameBurst);
        TechRegistry.registerTechnique(fruitId, infernoBlast);
    }

    static Technique fireball = new Technique("fireball", "Fireball", false, cooldownHelper.secondsToMiliseconds(5), (player, fruit, code) -> {
        player.launchProjectile(org.bukkit.entity.Fireball.class);
        hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "You have used the Fireball technique!");
    });

    static Technique flameBurst = new Technique("flame_burst", "Flame Burst", false, cooldownHelper.secondsToMiliseconds(60), (player, fruit, code) -> {
        player.getWorld().createExplosion(player.getLocation(), 15.0F, true, false);
        hotbarMessage.sendHotbarMessage(player, ChatColor.GOLD + "You have used the Flame Burst technique!");
    });

    static Technique infernoBlast = new Technique("inferno_blast", "Inferno Blast", true, cooldownHelper.secondsToMiliseconds(3600), (player, fruit, code) -> {
        if (!awakening.isFruitAwakened(player.getName(), fruitId)) {
            hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "You need to awaken your fruit to use this technique!");
            return;
        }
        player.getWorld().createExplosion(player.getLocation(), 50.0F, true, false);
        hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "You have used the Inferno Blast ultimate technique!");
        events.DeawakenFruit(player, fruitId);
    });
}
