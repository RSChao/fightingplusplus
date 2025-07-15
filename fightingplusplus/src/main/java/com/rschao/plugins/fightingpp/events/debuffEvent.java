package com.rschao.plugins.fightingpp.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.rschao.plugins.fightingpp.Plugin;

public class debuffEvent implements Listener {
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        String playerName = event.getPlayer().getName();
        if (playerName.startsWith(".")) {
            playerName = playerName.substring(1);
        }
        // Check if the player is in water
        if (!events.checkEaten(playerName)) {
            return;
        }
        Location from = event.getFrom();
        Location to = event.getTo();
        if(to.getWorld().getBlockAt(to).equals(from.getWorld().getBlockAt(from))) return;
        //ensure the player isnt riding a vehicle
        if (event.getPlayer().isInsideVehicle()) {
            return;
        }
        if (event.getPlayer().getLocation().add(new Vector(0, 1, 0)).getBlock().getType() == Material.WATER) {
            // Apply poison effect to the player
            Bukkit.getScheduler().runTaskLater(Plugin.getPlugin(Plugin.class), () -> {
                if (event.getPlayer().getLocation().getBlock().getType() == Material.WATER)
                    event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, 5));
            }, 20);
        }
    }
    
}
