package com.rschao.plugins.fightingpp.commands;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.rschao.plugins.fightingpp.Plugin;
import com.rschao.plugins.fightingpp.events.events;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import net.md_5.bungee.api.ChatColor;

public class removeFruit {
    static final String path = Plugin.getPlugin(Plugin.class).getDataFolder().getAbsolutePath();
    public static CommandAPICommand deleteFruit() {
        @SuppressWarnings("null")
        CommandAPICommand cmd = new CommandAPICommand("removefruit")
            .withPermission("fightingpp.removefruit")
            .withArguments(new PlayerArgument("target"))
            .executes((player, args) -> {
                Player target = (Player) args.get("target");
                String playerName = target.getName();
                if (playerName.startsWith(".")) {
                    playerName = playerName.substring(1);
                }
                if (events.checkEaten(playerName)) {
                    File configFile = new File(path, "fruits.yml");
                    FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
                    // remove tech usage count
                    try{
                        config.set("fruits." + playerName + ".fruits", null);
                        config.getConfigurationSection("fruits." + playerName + ".fruits").set("fruits", null);
                        config.set("fruits." + playerName + ".awakened", null); // Remove awakened state
                        for(String key : config.getConfigurationSection("fruits." + playerName + ".tech").getKeys(false)){
                            config.set(key, null);
                            config.getConfigurationSection("fruits." + playerName + ".tech").set(key, null);
                        }
                        config.set("fruits." + playerName + ".tech", null);
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }
                    config.set("fruits." + playerName + ".tech", null);
                    

                    try {
                        config.save(configFile);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    target.sendMessage(ChatColor.DARK_RED + "Your Devil Fruit power has been lost.");
                    player.sendMessage(ChatColor.DARK_RED + "You have removed " + playerName + "'s Devil Fruit power.");
                    events.playerTechniques.remove(playerName); // Remove technique index
                }
            });
        return cmd;
        
    }
}
