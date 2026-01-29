package com.rschao.plugins.fightingpp.techs;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.Location;
import com.rschao.plugins.fightingpp.Plugin;
import com.rschao.plugins.fightingpp.events.awakening;
import com.rschao.plugins.fightingpp.events.events;
import com.rschao.plugins.techniqueAPI.tech.Technique;
import com.rschao.plugins.techniqueAPI.tech.TechniqueMeta;
import com.rschao.plugins.techniqueAPI.tech.cooldown.cooldownHelper;
import com.rschao.plugins.techniqueAPI.tech.feedback.hotbarMessage;
import com.rschao.plugins.techniqueAPI.tech.register.TechRegistry;
import com.rschao.plugins.techniqueAPI.tech.selectors.TargetSelectors;

import net.md_5.bungee.api.ChatColor;

import java.util.List;

public class choco {
    static final String fruitId = "choco";
    static final Plugin plugin = Plugin.getPlugin(Plugin.class);

    public static void Register() {
        TechRegistry.registerTechnique(fruitId, makeMeSomeChoco);
        TechRegistry.registerTechnique(fruitId, chocoChocoNotMe);
        TechRegistry.registerTechnique(fruitId, sweetArmor);
        TechRegistry.registerTechnique(fruitId, aceOfChocolate);
        TechRegistry.registerTechnique(fruitId, chocolateCircus);
        Plugin.registerFruitID(fruitId);
    }

    static Technique makeMeSomeChoco = new Technique(
        "make_me_some_choco",
        "Make me some Choco",
        new TechniqueMeta(false, cooldownHelper.secondsToMiliseconds(60), List.of("Make me some Choco")),
        TargetSelectors.self(),
        (ctx, token) -> {
            Player player = ctx.caster();
            if (awakening.isFruitAwakened(player.getName(), fruitId)) {
                player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(Material.COCOA_BEANS, 64));
                int rng = com.rschao.events.events.getRNG(0, 100);
                if (rng < 30) {
                    player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(Material.COCOA_BEANS, 64));
                }
            } else {
                player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(Material.COCOA_BEANS, 32));
                int rng = com.rschao.events.events.getRNG(0, 100);
                if (rng < 30) {
                    player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(Material.COCOA_BEANS, 32));
                }
            }
            hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "You have used the Make me some Choco technique!");
        }
    );

    static Technique chocoChocoNotMe = new Technique(
        "choco_choco_not_me",
        "Choco Choco not Me!",
        new TechniqueMeta(false, cooldownHelper.secondsToMiliseconds(180), List.of("Choco Choco not Me")),
        TargetSelectors.self(),
        (ctx, token) -> {
            Player player = ctx.caster();
            int cocoaCount = 0;
            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null && item.getType() == Material.COCOA_BEANS) {
                    cocoaCount += item.getAmount();
                }
            }
            Location loc = player.getLocation().clone();
            loc.add(com.rschao.events.events.getRNG(-4, 4), 0, com.rschao.events.events.getRNG(-4, 4));
            if (cocoaCount >= 64) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INSTANT_HEALTH, 1, 3));
                player.teleport(loc);
                player.getInventory().removeItem(new ItemStack(Material.COCOA_BEANS, 64));
            } else if (cocoaCount >= 32) {
                player.teleport(loc);
                player.getInventory().removeItem(new ItemStack(Material.COCOA_BEANS, 32));
            }
            hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "You have used the Choco Choco not Me! technique!");
        }
    );

    static Technique sweetArmor = new Technique(
        "sweet_armor",
        "Sweet armor",
        new TechniqueMeta(false, cooldownHelper.secondsToMiliseconds(300), List.of("Sweet armor")),
        TargetSelectors.self(),
        (ctx, token) -> {
            Player player = ctx.caster();
            int cocoaCount = 0;
            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null && item.getType() == Material.COCOA_BEANS) {
                    cocoaCount += item.getAmount();
                }
            }
            if (cocoaCount >= 96) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 5 * 20, 4));
                player.getInventory().removeItem(new ItemStack(Material.COCOA_BEANS, 96));
            }
            hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "You have used the Sweet armor technique!");
        }
    );

    static Technique aceOfChocolate = new Technique(
        "ace_of_chocolate",
        "Ace of chocolate",
        new TechniqueMeta(false, cooldownHelper.secondsToMiliseconds(60), List.of("Ace of chocolate")),
        TargetSelectors.self(),
        (ctx, token) -> {
            Player player = ctx.caster();
            int cocoaCount = 0;
            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null && item.getType() == Material.COCOA_BEANS) {
                    cocoaCount += item.getAmount();
                }
            }
            if (cocoaCount >= 128) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 30 * 20, 2));
                player.getInventory().removeItem(new ItemStack(Material.COCOA_BEANS, 128));
            }
            hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "You have used the Ace of chocolate technique!");
        }
    );

    static Technique chocolateCircus = new Technique(
        "chocolate_circus",
        "Chocolate Circus",
        new TechniqueMeta(true, cooldownHelper.secondsToMiliseconds(3600), List.of("Ultimate: Chocolate Circus")),
        TargetSelectors.self(),
        (ctx, token) -> {
            Player player = ctx.caster();
            int cocoaCount = 0;
            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null && item.getType() == Material.COCOA_BEANS) {
                    cocoaCount += item.getAmount();
                }
            }
            final int dmg = cocoaCount * 3;
            if (cocoaCount >= 256) {
                for (org.bukkit.entity.Entity entity : player.getNearbyEntities(20, 20, 20)) {
                    if (entity instanceof Player) {
                        Player target = (Player) entity;
                        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5 * 20, 255));
                        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 5 * 20, 255));
                        target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 5 * 20, 255));
                        try {
                            target.getAttribute(org.bukkit.attribute.Attribute.JUMP_STRENGTH).setBaseValue(0);
                        } catch (Throwable ignored) {}
                        double jumpstrength = 0.41999998697815;
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            try { target.getAttribute(org.bukkit.attribute.Attribute.JUMP_STRENGTH).setBaseValue(jumpstrength); } catch (Throwable ignored) {}
                            target.damage(dmg);
                        }, 5 * 20);
                    }
                }
                player.getInventory().removeItem(new ItemStack(Material.COCOA_BEANS, cocoaCount));
            }
            hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "You have used the Chocolate Circus ultimate technique!");
        },
        (ctx, token) -> {
            Player player = ctx.caster();
            if (!awakening.isFruitAwakened(player.getName(), fruitId)) {
                hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "You need to awaken your fruit to use this technique!");
                return false;
            }
            events.DeawakenFruit(player, fruitId);
            return true;
        }
    );
}
