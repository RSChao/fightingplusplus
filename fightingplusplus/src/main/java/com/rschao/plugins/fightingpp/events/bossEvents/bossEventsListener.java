package com.rschao.plugins.fightingpp.events.bossEvents;

import java.io.File;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import com.rschao.Plugin;
import com.rschao.events.definitions.BossChangeEvent;
import com.rschao.events.definitions.BossEndEvent;
import com.rschao.events.definitions.BossStartEvent;
import com.rschao.events.definitions.PlayerPopHeartEvent;
import com.rschao.plugins.fightingpp.events.awakening;
import com.rschao.plugins.fightingpp.events.events;
import com.rschao.plugins.fightingpp.events.definitions.PlayerUseUltimate;
import com.rschao.plugins.techapi.tech.cooldown.CooldownManager;

import net.md_5.bungee.api.ChatColor;
public class bossEventsListener implements Listener {
    static boolean isBossActive = false;
    @EventHandler
    void onBossPhaseChangeEvent(BossChangeEvent ev) {
        //get the boss name as the name of the config file, then load it and look for the value of key boss.world.x.soul (x being the phase number)
        String bossName = ev.getBossName();
        FileConfiguration config = YamlConfiguration.loadConfiguration(new File(Bukkit.getPluginManager().getPlugin("bossfight").getDataFolder() + "/bosses/", bossName + ".yml"));
        
        List<String> fruits = config.getStringList("boss.world." + ev.getPhase() + ".fruits");
        if(fruits.size() == 0) return;
        else{
            File configFile = new File(events.path, "fruits.yml");
            FileConfiguration conf = YamlConfiguration.loadConfiguration(configFile);
            String playerName = ev.getBossPlayer().getName();
            if (playerName.startsWith(".")) {
                playerName = playerName.substring(1);
            }
            conf.set("fruits." + playerName + ".fruits", null);
            conf.set("fruits." + playerName + ".awakened", null); // Remove awakened state
            try {
                conf.save(configFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
            CooldownManager.removeAllCooldowns(ev.getBossPlayer());
            //get the player who used the ultimate
            //get the fruit id from the player
            String fruitId = fruits.get(0);
            //set the fruit to the player and awake it
            events.saveFruitToConfig(playerName, fruitId);
            awakening.setFruitAwakened(playerName, fruitId, true);
            if(fruits.size() <2) return;
            fruitId = fruits.get(1);
            events.saveFruitToConfig(playerName, fruitId);
            awakening.setFruitAwakened(playerName, fruitId, true);
        }
    }
    @EventHandler
    void onBossStartEvent(BossStartEvent event) {
        //Boss Start Event
        //Can change fruits here
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
        if(isBossActive) {
            Player user = event.getUser();
            Bukkit.getScheduler().runTaskLater(Plugin.getPlugin(Plugin.class), () ->{
                awakening.setFruitAwakened(user.getName(), event.getFruitId(), true);
            }, 20);
        }
    }
    @EventHandler
    void onPlayerPopHeart(PlayerPopHeartEvent ev){
        //get the player's fruits
        String playerName = ev.getPlayer().getName();
        File configFile = new File(events.path, "fruits.yml");
        FileConfiguration conf = YamlConfiguration.loadConfiguration(configFile);
        List<String> fruits = conf.getStringList("fruits." + playerName + ".fruits");
        if(fruits.size() == 0) return;
        for(String fruitId : fruits){
            awakening.setFruitAwakened(playerName, fruitId, true);
        }
        ev.getPlayer().sendMessage(ChatColor.GOLD + "You have awakened your fruit!");
    }
}
