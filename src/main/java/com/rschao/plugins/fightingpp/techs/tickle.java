package com.rschao.plugins.fightingpp.techs;

import org.bukkit.Bukkit;
import org.bukkit.entity.Endermite;
import org.bukkit.entity.Player;
import org.bukkit.entity.EntityType;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class tickle {
    static final String fruitId = "tickle";
    static final Plugin plugin = Plugin.getPlugin(Plugin.class);

    public static void Register() {
        TechRegistry.registerTechnique(fruitId, tickleSpawner);
        Plugin.registerFruitID(fruitId);
    }

    static Technique tickleSpawner = new Technique(
            "tickle_spawner",
            "Tickle Spawner",
            new TechniqueMeta(false, cooldownHelper.secondsToMiliseconds(60), List.of("Tickle Spawner")),
            TargetSelectors.self(),
            (ctx, token) -> {
                Player player = ctx.caster();
                int amnt = awakening.isFruitAwakened(player.getName(), fruitId) ? 15 : 5;
                for (int i = 0; i < amnt; i++) {
                    Endermite tickles = (Endermite) player.getWorld().spawnEntity(player.getLocation(), EntityType.ENDERMITE);
                    tickles.setCustomName("Tickles");
                    tickles.setCustomNameVisible(true);
                    tickles.setGlowing(true);
                    tickles.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
                    if (awakening.isFruitAwakened(player.getName(), fruitId)) {
                        tickles.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, Integer.MAX_VALUE, 4));
                        tickles.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 3));
                    }
                    List<Player> players = new ArrayList<>();
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (p != player && p.getLocation().distance(player.getLocation()) < 100) {
                            players.add(p);
                            break;
                        }
                    }
                    if (!players.isEmpty()) {
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            Player target = players.get(new Random().nextInt(players.size()));
                            tickles.setTarget(target);
                            tickles.teleport(target.getLocation());
                        }, 2);
                    }
                }
                hotbarMessage.sendHotbarMessage(player, "You have used the Tickle Spawner technique");
            }
    );
}
