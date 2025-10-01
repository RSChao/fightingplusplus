package com.rschao.plugins.fightingpp;

import com.rschao.plugins.fightingpp.commands.*;
import com.rschao.plugins.fightingpp.events.bossEvents.bossEventsListener;
import com.rschao.plugins.fightingpp.events.debuffEvent;
import com.rschao.plugins.fightingpp.events.events;
import com.rschao.plugins.fightingpp.items.fruits;
import com.rschao.plugins.fightingpp.techs.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


/*
 * fightingplusplus java plugin
 */
public class Plugin extends JavaPlugin {
    private static final Logger LOGGER = Logger.getLogger("fightingplusplus");
    private static final List<String> abyssIds = new ArrayList<>();

    public void onEnable() {
        LOGGER.info("fightingplusplus enabled");
        fruits.Init();
        registerTechs();
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            void onPlayerHit(EntityDamageByEntityEvent e) {
                if ((!(e.getDamager() instanceof Player))) return;
                Player damager = (Player) e.getDamager();
                if (ganon.hasFSmash.get(damager.getUniqueId()) == null) return;
                if (ganon.hasFSmash.get(damager.getUniqueId())) {
                    e.setDamage(200);
                    ganon.hasFSmash.put(damager.getUniqueId(), false);
                }
            }
        }, Plugin.getPlugin(Plugin.class));
        getServer().getPluginManager().registerEvents(new events(), this);
        getServer().getPluginManager().registerEvents(new bossEventsListener(), this);
        if (getConfig().getBoolean("fruitdebuff")) {
            getServer().getPluginManager().registerEvents(new debuffEvent(), this);
        }

        giveFruit.givefruit().register();
        setAwaken.givefruit().register();
        removecooldown.removeCooldowns().register();
        removeFruit.deleteFruit().register();
        catalyst.Load().register();
        eventitems.Load().register();
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "whitelist off");
            com.rschao.Plugin.EnableCHeart();
        });
    }


    public void onDisable() {
        LOGGER.info("fightingplusplus disabled");
    }

    void registerTechs() {
        // Register all techniques here
        tickle.Register();
        gomu.Register();
        air.Register();
        fire.Register();
        ice.Register();
        light.Register();
        dark.Register();
        dario.Register();
        delta.Register();
        chao.Register();
        fabri.Register();
        peru.Register();
        fly.Register();
        paper.Register();
        choco.Register();
        ganon.register();
        jevil.register();
        flower.register();
        toño.register();
        // Add more tech registrations as needed
    }
    public static List<String> getAllFruitIDs(){
        return abyssIds;
    }
    public static void registerFruitID(String id){
        if(!abyssIds.contains(id)){
            abyssIds.add(id);
        }
    }
}
