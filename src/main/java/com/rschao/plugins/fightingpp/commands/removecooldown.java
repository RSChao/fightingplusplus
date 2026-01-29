package com.rschao.plugins.fightingpp.commands;

import com.rschao.plugins.techniqueAPI.tech.cooldown.CooldownManager;
import org.bukkit.entity.Player;


import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.EntitySelectorArgument.OnePlayer;

public class removecooldown {
    //a method to remove all cooldowns from a player's fruit, even the ultimate
    public static CommandAPICommand removeCooldowns() {
        CommandAPICommand cmd = new CommandAPICommand("resetcooldown")
        .withPermission("fruits.cooldowns")
        .withArguments(new OnePlayer("player"))
        .executesPlayer((player, args) -> {
            Player target = (Player) args.get("player");
            if(target == null) {
                player.sendMessage("Player not found");
                return;
            }
            // Use TechAPI's CooldownManager to clear all cooldowns for the player
            CooldownManager.removeAllCooldowns(target);
            player.sendMessage("All cooldowns have been removed from " + target.getName());
        });
        return cmd;
    }
}
