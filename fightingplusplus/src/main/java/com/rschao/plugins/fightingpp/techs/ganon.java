package com.rschao.plugins.fightingpp.techs;

import com.rschao.plugins.techapi.tech.Technique;
import com.rschao.plugins.techapi.tech.register.TechRegistry;

public class ganon {
    static final String ID = "ganon";

    public static void register(){
        TechRegistry.registerTechnique(ID, test);
    }

    static Technique test = new Technique("fsmash", "Claymore Smash", false, 10000, (player, item, args) -> {
        player.sendMessage("Pato si");
    });
}
