package com.rschao.plugins.fightingpp.techs;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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

public class fabri {
    static final String fruitId = "fabri";
    static final Plugin plugin = Plugin.getPlugin(Plugin.class);

    public static void Register() {
        TechRegistry.registerTechnique(fruitId, freePearl);
        TechRegistry.registerTechnique(fruitId, infiniteCrystals);
        TechRegistry.registerTechnique(fruitId, obsidianFarm);
        TechRegistry.registerTechnique(fruitId, armorHaki);
        TechRegistry.registerTechnique(fruitId, immolation);
        Plugin.registerFruitID(fruitId);
    }

    static Technique freePearl = new Technique(
            "free_pearl",
            "Free pearl",
            new TechniqueMeta(false, cooldownHelper.secondsToMiliseconds(60), List.of("Free pearl")),
            TargetSelectors.self(),
            (ctx, token) -> {
                ctx.caster().getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 64));
                hotbarMessage.sendHotbarMessage(ctx.caster(), ChatColor.DARK_GREEN + "You have used the Free Pearl technique");
            }
    );

    static Technique infiniteCrystals = new Technique(
            "infinite_crystals",
            "Infinite Crystals",
            new TechniqueMeta(false, cooldownHelper.secondsToMiliseconds(180), List.of("Infinite crystals")),
            TargetSelectors.self(),
            (ctx, token) -> {
                ctx.caster().getInventory().addItem(new ItemStack(Material.END_CRYSTAL, 64));
                hotbarMessage.sendHotbarMessage(ctx.caster(), ChatColor.DARK_PURPLE + "You have used the Infinite Crystal technique");
            }
    );

    static Technique obsidianFarm = new Technique(
            "obsidian_farm",
            "Obsidian Farm",
            new TechniqueMeta(false, cooldownHelper.secondsToMiliseconds(180), List.of("Obsidian farm")),
            TargetSelectors.self(),
            (ctx, token) -> {
                ctx.caster().getInventory().addItem(new ItemStack(Material.OBSIDIAN, 64));
                hotbarMessage.sendHotbarMessage(ctx.caster(), ChatColor.DARK_GRAY + "You have used the Obsidian Farm technique");
            }
    );

    static Technique armorHaki = new Technique(
            "armor_haki",
            "Armor Haki",
            new TechniqueMeta(false, cooldownHelper.secondsToMiliseconds(300), List.of("Armor Haki")),
            TargetSelectors.self(),
            (ctx, token) -> {
                String playerName = ctx.caster().getName();
                ItemStack[] armorContents = ctx.caster().getInventory().getArmorContents();
                if (awakening.isFruitAwakened(playerName, fruitId)) {
                    for (ItemStack item : armorContents) {
                        if (item != null && item.getItemMeta() instanceof Damageable) {
                            Damageable meta = (Damageable) item.getItemMeta();
                            meta.setDamage(0);
                            item.setItemMeta(meta);
                        }
                    }
                    armorContents = ctx.caster().getInventory().getContents();
                }
                for (ItemStack item : armorContents) {
                    if (item != null && item.getItemMeta() instanceof Damageable) {
                        Damageable meta = (Damageable) item.getItemMeta();
                        meta.setDamage(0);
                        item.setItemMeta(meta);
                    }
                }
                hotbarMessage.sendHotbarMessage(ctx.caster(), ChatColor.DARK_PURPLE + "You have used the Armor Haki technique");
            }
    );

    static Technique immolation = new Technique(
            "immolation",
            "Immolation",
            new TechniqueMeta(true, cooldownHelper.secondsToMiliseconds(3600), List.of("Ultimate: Immolation")),
            TargetSelectors.self(),
            (ctx, token) -> {
                Player player = ctx.caster();
                player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 5, 255));
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    player.getWorld().createExplosion(player.getLocation(), 50.0F, true, true);
                }, 2);
                hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "You have used the Immolation ultimate technique!");
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
