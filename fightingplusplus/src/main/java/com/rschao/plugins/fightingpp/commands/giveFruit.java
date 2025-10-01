package com.rschao.plugins.fightingpp.commands;

import com.rschao.plugins.fightingpp.items.fruits;
import dev.jorel.commandapi.CommandAPICommand;

public class giveFruit {
    public static CommandAPICommand givefruit(){
        return new CommandAPICommand("givefruit")
        .withPermission("fruits.give")
        .withSubcommands(fire(), fabri(), toño(), delta(), dario(), chao(), luffy(), wakeup(), air(), ice(), light(), dark(), peru(), paper(), choco(), fly(), ticles(), jevil(), flowah(), ganon())
        .executes((exec, args) ->{

        });

    }
    public static CommandAPICommand fire(){
        CommandAPICommand cmd = new CommandAPICommand("MeraMera")
                .withPermission("fruits.give")
                .executesPlayer((exec, args) ->{
                    exec.getInventory().addItem(fruits.fireFruit);
                });
        return cmd;
    }
    public static CommandAPICommand toño(){
        CommandAPICommand cmd = new CommandAPICommand("ToñoToño")
                .withPermission("fruits.give")
                .executesPlayer((exec, args) ->{
                    exec.getInventory().addItem(fruits.toñoFruit);
                });
        return cmd;
    }
    public static CommandAPICommand flowah(){
        CommandAPICommand cmd = new CommandAPICommand("HanaHana")
                .withPermission("fruits.give")
                .executesPlayer((exec, args) ->{
                    exec.getInventory().addItem(fruits.flowerFruit);
                });
        return cmd;
    }
    public static CommandAPICommand ganon(){
        CommandAPICommand cmd = new CommandAPICommand("GanonGanon")
                .withPermission("fruits.give")
                .executesPlayer((exec, args) ->{
                    exec.getInventory().addItem(fruits.ganonFruit);
                });
        return cmd;
    }
    public static CommandAPICommand jevil(){
        CommandAPICommand cmd = new CommandAPICommand("JevilJevil")
                .withPermission("fruits.give")
                .executesPlayer((exec, args) ->{
                    exec.getInventory().addItem(fruits.jevilFruit);
                });
        return cmd;
    }
    public static CommandAPICommand fly(){
        CommandAPICommand cmd = new CommandAPICommand("SoraSora")
        .withPermission("fruits.give")
        .executesPlayer((exec, args) ->{
            exec.getInventory().addItem(fruits.flyFruit);
        });
        return cmd;
    }
    public static CommandAPICommand dice(){
        CommandAPICommand cmd = new CommandAPICommand("D&D_dice")
        .withPermission("fruits.give")
        .executesPlayer((exec, args) ->{
            exec.getInventory().addItem(fruits.dndFruit);
        });
        return cmd;
    }
    public static CommandAPICommand air(){
        CommandAPICommand cmd = new CommandAPICommand("KazeKaze")
        .withPermission("fruits.give")
        .executesPlayer((exec, args) ->{
            exec.getInventory().addItem(fruits.airFruit);
        });
        return cmd;
    }
    public static CommandAPICommand ice(){
        CommandAPICommand cmd = new CommandAPICommand("HieHie")
        .withPermission("fruits.give")
        .executesPlayer((exec, args) ->{
            exec.getInventory().addItem(fruits.iceFruit);
        });
        return cmd;
    }
    public static CommandAPICommand light(){
        CommandAPICommand cmd = new CommandAPICommand("hikarihikari")
        .withPermission("fruits.give")
        .executesPlayer((exec, args) ->{
            exec.getInventory().addItem(fruits.lightFruit);
        });
        return cmd;
    }
    public static CommandAPICommand dark(){
        CommandAPICommand cmd = new CommandAPICommand("yamiyami")
        .withPermission("fruits.give")
        .executesPlayer((exec, args) ->{
            exec.getInventory().addItem(fruits.darkFruit);
        });
        return cmd;
    }
    public static CommandAPICommand fabri(){
        CommandAPICommand cmd = new CommandAPICommand("FabriFabri")
        .withPermission("fruits.give")
        .executesPlayer((exec, args) ->{
            exec.getInventory().addItem(fruits.fabriFruit);
        });
        return cmd;
    }
    public static CommandAPICommand wakeup(){
        CommandAPICommand cmd = new CommandAPICommand("awakener")
        .withPermission("fruits.give")
        .executesPlayer((exec, args) ->{
            exec.getInventory().addItem(fruits.awaken);
        });
        return cmd;
    }
    public static CommandAPICommand delta(){
        CommandAPICommand cmd = new CommandAPICommand("DerutaDeruta")
        .withPermission("fruits.give")
        .executesPlayer((exec, args) ->{
            exec.getInventory().addItem(fruits.deltaFrutita);
        });
        return cmd;
    }
    public static CommandAPICommand dario(){
        CommandAPICommand cmd = new CommandAPICommand("SupeedoSupeedo")
        .withPermission("fruits.give")
        .executesPlayer((exec, args) ->{
            exec.getInventory().addItem(fruits.darioFruit);
        });
        return cmd;
    }
    public static CommandAPICommand chao(){
        CommandAPICommand cmd = new CommandAPICommand("KatateKami")
        .withPermission("fruits.give")
        .executesPlayer((exec, args) ->{
            exec.getInventory().addItem(fruits.chaoFruit);
        });
        return cmd;
    }
    public static CommandAPICommand luffy(){
        CommandAPICommand cmd = new CommandAPICommand("GomuGomu")
        .withPermission("fruits.give")
        .executesPlayer((exec, args) ->{
            exec.getInventory().addItem(fruits.luffyFruit);
        });
        return cmd;
    }
    public static CommandAPICommand peru(){
        CommandAPICommand cmd = new CommandAPICommand("PerúPerú")
        .withPermission("fruits.give")
        .executesPlayer((exec, args) ->{
            exec.getInventory().addItem(fruits.peruFruit);
        });
        return cmd;
    }
    public static CommandAPICommand paper(){
        CommandAPICommand cmd = new CommandAPICommand("KamiKami")
        .withPermission("fruits.give")
        .executesPlayer((exec, args) ->{
            exec.getInventory().addItem(fruits.paperFruit);
        });
        return cmd;
    }
    public static CommandAPICommand choco(){
        CommandAPICommand cmd = new CommandAPICommand("ChokoChoko")
        .withPermission("fruits.give")
        .executesPlayer((exec, args) ->{
            exec.getInventory().addItem(fruits.chocoFruit);
        });
        return cmd;
    }
    public static CommandAPICommand ticles(){
        CommandAPICommand cmd = new CommandAPICommand("TikoruTikoru")
        .withPermission("fruits.give")
        .executesPlayer((exec, args) ->{
            exec.getInventory().addItem(fruits.tickleFruit);
        });
        return cmd;
    }
    /*public static CommandAPICommand techItem(){
        CommandAPICommand cmd = new CommandAPICommand("catalyst")
        .withPermission("fruits.give")
        .executesPlayer((exec, args) ->{
            exec.getInventory().addItem(fruits.friutTechItem);
        });
        return cmd;

    } */
}
