package com.rschao.plugins.fightingpp.commands;

import com.rschao.plugins.fightingpp.items.fruits;

import dev.jorel.commandapi.CommandAPICommand;

public class eventitems {
    public static CommandAPICommand Load(){
        CommandAPICommand cmd = new CommandAPICommand("giveevent")
        .withPermission("fruits.give")
        .withSubcommand(geno())
        .executesPlayer((exec, args) ->{
        });
        return cmd;
    }
    static CommandAPICommand geno(){
        CommandAPICommand cmd = new CommandAPICommand("thegenothing")
        .withPermission("fruits.give")
        .executesPlayer((exec, args) ->{
            exec.getInventory().addItem(fruits.thegenothing);
        });
        return cmd;
    }
}
