package com.rschao.plugins.fightingpp.techs;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.rschao.plugins.fightingpp.Plugin;
import com.rschao.plugins.techapi.tech.Technique;
import com.rschao.plugins.techapi.tech.cooldown.cooldownHelper;
import com.rschao.plugins.techapi.tech.feedback.hotbarMessage;
import com.rschao.plugins.techapi.tech.register.TechRegistry;

public class ganon {
    static final String id = "ganon";
    public static Map<UUID, Boolean> hasFSmash = new HashMap<>();

    public static void register(){
        TechRegistry.registerTechnique(id, test);
    }

    static Technique test = new Technique("fsmash", "Claymore Smash", false, cooldownHelper.minutesToMiliseconds(5), (player, item, args) -> {
        hasFSmash.put(player.getUniqueId(), true);
        hotbarMessage.sendHotbarMessage(player, "You have used the Claymore Smash technique!");
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            void onPlayerHit(EntityDamageByEntityEvent e){
                Player damager = (Player) e.getDamager();
                if(hasFSmash.get(damager.getUniqueId())) {
                    e.setDamage(200);
                    hasFSmash.put(player.getUniqueId(), false);
                }
            }
        }, Plugin.getPlugin(Plugin.class));
        Bukkit.getScheduler().runTaskLater(Plugin.getPlugin(Plugin.class), () -> {
            hasFSmash.remove(player.getUniqueId());
            hotbarMessage.sendHotbarMessage(player, "Claymore Smash technique is no longer active.");
        }, 20);
    });
}