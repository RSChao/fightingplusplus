package com.rschao.plugins.fightingpp.commands;

import org.bukkit.NamespacedKey;

import com.rschao.plugins.fightingpp.items.fruits;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;

public class catalyst {
    public static CommandAPICommand Load(){
        CommandAPICommand cmd = new CommandAPICommand("catalyst")
        .withPermission("fruits.give")
        .withArguments(new StringArgument("id"), new StringArgument("name"))
        .executesPlayer((exec, args) ->{
            String fruit = (String) args.get(0);
            String name = (String) args.get(1);
            exec.getInventory().addItem(fruits.techItem(new NamespacedKey("fruit", fruit), name));
        });
        return cmd;
    }
}
