package com.rschao.plugins.fightingpp.techs;

import com.rschao.plugins.fightingpp.Plugin;
import com.rschao.plugins.fightingpp.events.awakening;
import com.rschao.plugins.fightingpp.events.events;
import com.rschao.plugins.techniqueAPI.tech.Technique;
import com.rschao.plugins.techniqueAPI.tech.TechniqueMeta;
import com.rschao.plugins.techniqueAPI.tech.cooldown.cooldownHelper;
import com.rschao.plugins.techniqueAPI.tech.feedback.hotbarMessage;
import com.rschao.plugins.techniqueAPI.tech.register.TechRegistry;
import com.rschao.plugins.techniqueAPI.tech.selectors.TargetSelectors;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class Aitor {
    static final String fruitId = "aitor";
    static final Plugin plugin = Plugin.getPlugin(Plugin.class);

    public static void Register() {
        Plugin.registerFruitID(fruitId);
        TechRegistry.registerTechnique(fruitId, aitorTech);
    }

    static Technique aitorTech = new Technique(
            "aitor_tech",
            "Aitor's Technique",
            new TechniqueMeta(true, cooldownHelper.hour, List.of("Aitor ultimate")),
            TargetSelectors.self(),
            (ctx, token) -> {
                Player caster = ctx.caster();

                // 1) Encontrar jugadores a 50 bloques (excluye al lanzador para inmovilizar)
                List<Player> immobilized = new ArrayList<>();
                Map<UUID, Float> originalSpeeds = new HashMap<>();

                for (Entity e : caster.getWorld().getNearbyEntities(caster.getLocation(), 50, 50, 50)) {
                    if (e instanceof Player) {
                        Player p = (Player) e;
                        if (p.getUniqueId().equals(caster.getUniqueId())) continue; // no inmovilizar al lanzador ahora
                        // Guardar velocidad original
                        try {
                            originalSpeeds.put(p.getUniqueId(), p.getWalkSpeed());
                        } catch (Throwable ignored) {}
                        // Inmovilizar: velocidad a 0, efecto de lentitud fuerte y cancelar velocidad actual
                        try {
                            p.setWalkSpeed(0f);
                        } catch (Throwable ignored) {}
                        p.setVelocity(new Vector(0, 0, 0));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 60 * 5, 10, true, false)); // larga duración, se quitará manualmente
                        immobilized.add(p);
                    }
                }

                // 2) Tras ~2s (40 ticks) comenzar las explosiones; targets serán los inmovilizados + el lanzador
                List<Player> explosionTargets = new ArrayList<>(immobilized);
                explosionTargets.add(caster);

                // Si no hay objetivos, salir y restaurar (no se inmovilizó nadie)
                if (explosionTargets.isEmpty()) return;

                final World world = caster.getWorld();
                final Random rnd = new Random();

                // Plan: esperar 40 ticks, luego correr 42 iteraciones cada 4 ticks
                new BukkitRunnable() {
                    int count = 0;
                    final int max = 42;

                    @Override
                    public void run() {
                        if (count >= max) {
                            // Liberar a los inmovilizados: restaurar velocidades y quitar efectos
                            for (Player p : immobilized) {
                                if (p == null || !p.isOnline()) continue;
                                Float orig = originalSpeeds.get(p.getUniqueId());
                                if (orig != null) {
                                    try {
                                        p.setWalkSpeed(orig);
                                    } catch (Throwable ignored) {}
                                } else {
                                    try { p.setWalkSpeed(0.2f); } catch (Throwable ignored) {}
                                }
                                p.removePotionEffect(PotionEffectType.SLOWNESS);
                            }
                            this.cancel();
                            return;
                        }

                        // Elegir un objetivo aleatorio válido
                        List<Player> valid = new ArrayList<>();
                        for (Player t : explosionTargets) if (t != null && t.isOnline()) valid.add(t);
                        if (valid.isEmpty()) { count++; return; }

                        Player chosen = valid.get(rnd.nextInt(valid.size()));

                        // Generar posición aleatoria 3-10 bloques alrededor del jugador
                        double distance = 3 + rnd.nextDouble() * 7; // [3,10)
                        double angle = rnd.nextDouble() * Math.PI * 2;
                        double dx = Math.cos(angle) * distance;
                        double dz = Math.sin(angle) * distance;
                        double dy = -1 + rnd.nextDouble() * 3; // pequeño offset vertical -1..2
                        Location base = chosen.getLocation();
                        Location explLoc = base.clone().add(dx, dy, dz);
                        // Asegurarse que Y está en rango razonable
                        explLoc.setY(Math.max(1, Math.min(explLoc.getY(), world.getMaxHeight() - 2)));

                        // Crear explosión visual sin incendiar ni romper bloques
                        world.createExplosion(explLoc, 2.0f, false, false);

                        // El siguiente tick, infligir 70 de daño al jugador seleccionado (daño fuerte)
                        final Player toDamage = chosen;
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (toDamage != null && toDamage.isOnline()) {
                                    try {
                                        toDamage.damage(70.0);
                                    } catch (Throwable ignored) {}
                                }
                            }
                        }.runTask(plugin);

                        count++;
                    }
                }.runTaskTimer(plugin, 40L, 4L); // delay 40 ticks (~2s), period 4 ticks
            },
            (ctx, token) -> {
                Player caster = ctx.caster();
                if (!awakening.isFruitAwakened(caster.getName(), fruitId)) {
                    hotbarMessage.sendHotbarMessage(caster, "You must awaken your fruit to use this technique!");
                    return false;
                }
                events.DeawakenFruit(caster, fruitId);
                return true;
            }
    );
}
