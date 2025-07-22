package com.rschao.plugins.fightingpp.techs;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.rschao.plugins.fightingpp.Plugin;
import com.rschao.plugins.techapi.tech.Technique;
import com.rschao.plugins.techapi.tech.cooldown.cooldownHelper;
import com.rschao.plugins.techapi.tech.feedback.hotbarMessage;
import com.rschao.plugins.techapi.tech.register.TechRegistry;

import net.md_5.bungee.api.ChatColor;

public class dnd_dice {
    static final String fruitId = "dnd_dice";
    static final Plugin plugin = Plugin.getPlugin(Plugin.class);

    public static void Register() {
        TechRegistry.registerTechnique(fruitId, rollTheDice);
    }

    static Technique rollTheDice = new Technique("roll_the_dice", "Roll the dice", false, cooldownHelper.secondsToMiliseconds(300), (player, fruit, code) -> {
        int dice = com.rschao.events.events.getRNG(0, 20);
        if (dice == 1) {
            // Remove 10 hearts from the player's max health
            player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(
                player.getAttribute(Attribute.MAX_HEALTH).getBaseValue() - 10
            );
            // Add glitch effect - no jumping, slowness, weakness, blindness 255 - for a minute
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5 * 20, 255));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 5 * 20, 255));
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 5 * 20, 255));
            double jumpstrength = 0.41999998697815;
            player.getAttribute(Attribute.JUMP_STRENGTH).setBaseValue(0);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.getAttribute(Attribute.JUMP_STRENGTH).setBaseValue(jumpstrength);
            }, 60 * 20);
            // Add glowing effect for 10 minutes
            player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 10 * 60 * 20, 255));
            hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "You have rolled a 1! You are now glitched!");
        } else if (dice == 20) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60 * 20, 2));
            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 60 * 20, 2));
            hotbarMessage.sendHotbarMessage(player, ChatColor.GOLD + "You have rolled a 20! Critical success!");
        } else {
            hotbarMessage.sendHotbarMessage(player, ChatColor.GRAY + "You rolled a " + dice + ".");
        }
    });
}
