package com.rschao.plugins.fightingpp.events.bossEvents;

import com.rschao.Plugin;
import com.rschao.boss_battle.BossAPI;
import com.rschao.events.definitions.BossChangeEvent;
import com.rschao.events.definitions.BossEndEvent;
import com.rschao.events.definitions.BossStartEvent;
import com.rschao.events.definitions.PlayerPopHeartEvent;
import com.rschao.plugins.fightingpp.events.awakening;
import com.rschao.plugins.fightingpp.events.definitions.PlayerUseUltimate;
import com.rschao.plugins.fightingpp.events.events;
import com.rschao.plugins.techniqueAPI.tech.cooldown.CooldownManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.File;
import java.util.List;

public class bossEventsListener implements Listener {
    static boolean isBossActive = false;

    @EventHandler
    void onBossPhaseChangeEvent(BossChangeEvent ev) {
        FileConfiguration config = ev.config;
        isBossActive = true;

        List<String> fruits = BossAPI.getAddon(config, ev.getPhase(), "fruits");
        if (fruits.isEmpty()) return;
        File configFile = new File(events.path, "fruits.yml");
        FileConfiguration conf = YamlConfiguration.loadConfiguration(configFile);
        String playerName = ev.getBossPlayer().getName();
        if (playerName.startsWith(".")) {
            playerName = playerName.substring(1);
        }
        if(fruits.size() == 1 && fruits.get(0).equalsIgnoreCase(playerName)) return;
        conf.set("fruits." + playerName + ".fruits", null);
        conf.set("fruits." + playerName + ".awakened", null); // Remove awakened state
        try {
            conf.save(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        CooldownManager.removeAllCooldowns(ev.getBossPlayer());
        for(String fruitId : fruits){
            com.rschao.plugins.fightingpp.events.events.saveFruitToConfig(playerName, fruitId);
            com.rschao.plugins.fightingpp.events.awakening.setFruitAwakened(playerName, fruitId, true);
        }
    }

    @EventHandler
    void onBossEndEvent(BossEndEvent event) {
        isBossActive = false;
        File configFile = new File(events.path, "fruits.yml");
        FileConfiguration conf = YamlConfiguration.loadConfiguration(configFile);
        String playerName = event.getBossPlayer().getName();
        conf.set("fruits." + playerName + ".fruits", null);
        conf.set("fruits." + playerName + ".awakened", null); // Remove awakened state
        try {
            conf.save(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    void onPlayerUseUltimate(PlayerUseUltimate event) {
        if (isBossActive) {
            Player user = event.getUser();
            Bukkit.getScheduler().runTaskLater(Plugin.getPlugin(Plugin.class), () -> {
                awakening.setFruitAwakened(user.getName(), event.getFruitId(), true);
            }, 20);
        }
    }

    @EventHandler
    void onPlayerPopHeart(PlayerPopHeartEvent ev) {
        //get the player's fruits
        if (ev.getTimesUsed() % 3 != 0) return; // Only process if the player has used the heart at least 3 times
        String playerName = ev.getPlayer().getName();
        File configFile = new File(events.path, "fruits.yml");
        FileConfiguration conf = YamlConfiguration.loadConfiguration(configFile);
        List<String> fruits = conf.getStringList("fruits." + playerName + ".fruits");
        if (fruits.size() == 0) return;
        for (String fruitId : fruits) {
            awakening.setFruitAwakened(playerName, fruitId, true);
        }
        ev.getPlayer().sendMessage(ChatColor.GOLD + "You have awakened your fruit!");
    }
}
