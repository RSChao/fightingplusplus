package com.rschao.plugins.fightingpp;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.rschao.plugins.fightingpp.commands.catalyst;
import com.rschao.plugins.fightingpp.commands.eventitems;
import com.rschao.plugins.fightingpp.commands.giveFruit;
import com.rschao.plugins.fightingpp.commands.removeFruit;
import com.rschao.plugins.fightingpp.commands.removecooldown;
import com.rschao.plugins.fightingpp.commands.setAwaken;
import com.rschao.plugins.fightingpp.events.debuffEvent;
import com.rschao.plugins.fightingpp.events.events;
import com.rschao.plugins.fightingpp.events.bossEvents.bossEventsListener;
import com.rschao.plugins.fightingpp.items.fruits;
import com.rschao.plugins.fightingpp.techs.air;
import com.rschao.plugins.fightingpp.techs.chao;
import com.rschao.plugins.fightingpp.techs.choco;
import com.rschao.plugins.fightingpp.techs.dario;
import com.rschao.plugins.fightingpp.techs.dark;
import com.rschao.plugins.fightingpp.techs.delta;
import com.rschao.plugins.fightingpp.techs.fabri;
import com.rschao.plugins.fightingpp.techs.fire;
import com.rschao.plugins.fightingpp.techs.fly;
import com.rschao.plugins.fightingpp.techs.ganon;
import com.rschao.plugins.fightingpp.techs.gomu;
import com.rschao.plugins.fightingpp.techs.ice;
import com.rschao.plugins.fightingpp.techs.light;
import com.rschao.plugins.fightingpp.techs.paper;
import com.rschao.plugins.fightingpp.techs.peru;
import com.rschao.plugins.fightingpp.techs.tickle;


/*
 * fightingplusplus java plugin
 */
public class Plugin extends JavaPlugin
{
  private static final Logger LOGGER=Logger.getLogger("fightingplusplus");

  public void onEnable()
  {
    LOGGER.info("fightingplusplus enabled");
    fruits.Init();

    getServer().getPluginManager().registerEvents(new events(), this);
    getServer().getPluginManager().registerEvents(new bossEventsListener(), this);
    if(getConfig().getBoolean("fruitdebuff")){
      getServer().getPluginManager().registerEvents(new debuffEvent(), this);
    }
    registerTechs();
    giveFruit.givefruit().register();
    setAwaken.givefruit().register();
    removecooldown.removeCooldowns().register();
    removeFruit.deleteFruit().register();
    catalyst.Load().register();
    eventitems.Load().register();
    Bukkit.getScheduler().scheduleSyncDelayedTask(this, ()->{
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "whitelist off");
    });
  }


  public void onDisable(){
    LOGGER.info("fightingplusplus disabled");
  }
  void registerTechs()
  {
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
    // Add more tech registrations as needed
  }
}
