package com.rschao.plugins.fightingpp.techs;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import com.rschao.plugins.fightingpp.Plugin;
import com.rschao.plugins.fightingpp.events.awakening;
import com.rschao.plugins.techapi.tech.Technique;
import com.rschao.plugins.techapi.tech.feedback.hotbarMessage;
import com.rschao.plugins.techapi.tech.register.TechRegistry;

public class gomu {
    static final String fruitId = "gomu";
    static final Plugin plugin = Plugin.getPlugin(Plugin.class);

    public static void Register() {
        TechRegistry.registerTechnique(fruitId, gomuNoMi);
    }

    static Technique gomuNoMi = new Technique("gomu_no_mi", "Gomu no mi", false, 33000, (player, fruit, code) -> {
        String playerName = player.getName();
        if (awakening.isFruitAwakened(playerName, fruitId)) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.getAttribute(Attribute.PLAYER_BLOCK_INTERACTION_RANGE).setBaseValue(20);
                player.getAttribute(Attribute.PLAYER_ENTITY_INTERACTION_RANGE).setBaseValue(20);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    player.getAttribute(Attribute.PLAYER_BLOCK_INTERACTION_RANGE).setBaseValue(4.5);
                    player.getAttribute(Attribute.PLAYER_ENTITY_INTERACTION_RANGE).setBaseValue(3);
                }, 60 * 20);
            }, 2);
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.getAttribute(Attribute.PLAYER_BLOCK_INTERACTION_RANGE).setBaseValue(9);
                player.getAttribute(Attribute.PLAYER_ENTITY_INTERACTION_RANGE).setBaseValue(9);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    player.getAttribute(Attribute.PLAYER_BLOCK_INTERACTION_RANGE).setBaseValue(4.5);
                    player.getAttribute(Attribute.PLAYER_ENTITY_INTERACTION_RANGE).setBaseValue(3);
                }, 60 * 20);
            }, 2);
        }
        hotbarMessage.sendHotbarMessage(player, "You have used the Gomu no mi technique");
    });
}
