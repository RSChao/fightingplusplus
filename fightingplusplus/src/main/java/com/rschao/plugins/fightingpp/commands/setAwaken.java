package com.rschao.plugins.fightingpp.commands;

import java.io.File;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.rschao.plugins.fightingpp.events.awakening;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;

public class setAwaken {
    public static CommandAPICommand givefruit() {
        CommandAPICommand cmd = new CommandAPICommand("awaken")
            .withPermission("fruits.give")
            .withArguments(new PlayerArgument("player"), new BooleanArgument("state"))
            .executes((exec, args) -> {
                Player targetPlayer = (Player) args.get(0);
                @SuppressWarnings("null")
                boolean state = (boolean) args.get(1);

                @SuppressWarnings("null")
                String playerName = targetPlayer.getName();
                //get player's fruits from fruits.yml
                FileConfiguration config = YamlConfiguration.loadConfiguration(new File(awakening.path, "fruits.yml"));
                List<String> list = config.getStringList("fruits." + playerName + ".fruits");


                for (String fruitId : list) {
                    // This now uses TechAPI's Awakening system and persists to config
                    awakening.setFruitAwakened(playerName, fruitId, state);
                }

                exec.sendMessage("Player " + playerName + "'s fruits are now " + (state ? "awakened" : "de-awakened") + ".");
            });
        return cmd;
    }
}
