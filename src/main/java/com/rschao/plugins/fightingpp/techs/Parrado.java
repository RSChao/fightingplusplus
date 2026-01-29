package com.rschao.plugins.fightingpp.techs;

import com.rschao.plugins.fightingpp.Plugin;
import com.rschao.plugins.fightingpp.events.events;
import com.rschao.plugins.fightingpp.events.awakening;
import com.rschao.plugins.techniqueAPI.tech.register.TechRegistry;
import com.rschao.plugins.techniqueAPI.tech.Technique;
import com.rschao.plugins.techniqueAPI.tech.TechniqueMeta;
import com.rschao.plugins.techniqueAPI.tech.cooldown.cooldownHelper;
import com.rschao.plugins.techniqueAPI.tech.feedback.hotbarMessage;
import com.rschao.plugins.techniqueAPI.tech.selectors.TargetSelectors;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Parrado {

    static final String fruitId = "parrado";
    static final Plugin plugin = Plugin.getPlugin(Plugin.class);

    public static void Register() {

        Plugin.registerFruitID(fruitId);
        TechRegistry.registerTechnique(fruitId, parradoSpiral);
    }

    static Technique parradoSpiral = new Technique(
        "parrado_spiral",
        "Parrado Spiral",
        new TechniqueMeta(true, cooldownHelper.secondsToMiliseconds(3600), List.of("Ultimate: Parrado Spiral")),
        TargetSelectors.self(),
        (ctx, token) -> {
            Player caster = ctx.caster();
            World world = caster.getWorld();
            Location center = caster.getLocation().clone();

            // 1) Recolectar jugadores en radio 40, excluir al lanzador
            List<Player> targets = new ArrayList<>();
            for (Player p : world.getPlayers()) {
                if (!p.getWorld().equals(world)) continue;
                if (p.equals(caster)) continue;
                if (p.getLocation().distance(center) <= 40) targets.add(p);
            }
            if (targets.isEmpty()) {
                hotbarMessage.sendHotbarMessage(caster, ChatColor.RED + "No players in range for Parrado Spiral.");
                return;
            }

            // 2) Posicionar en círculo inicial alrededor del centro (radio 40)
            final int n = targets.size();
            final double startRadius = 40.0;
            for (int i = 0; i < n; i++) {
                double angle = 2 * Math.PI * i / Math.max(1, n);
                double x = center.getX() + startRadius * Math.cos(angle);
                double z = center.getZ() + startRadius * Math.sin(angle);
                Location loc = new Location(world, x, center.getY(), z);
                Player p = targets.get(i);
                if (p.isOnline()) p.teleport(loc);
            }

            // 3) Ejecutar espiral: reducir radio y elevar altura hasta el centro
            new BukkitRunnable() {
                double spiralRadius = startRadius;
                double yOffset = 0;
                int tick = 0;
                final double radiusStep = 0.4; // control de velocidad de contracción
                final double yStep = 0.08; // control de elevación por tick

                @Override
                public void run() {
                    if (targets.isEmpty()) { this.cancel(); return; }
                    tick++;
                    spiralRadius = Math.max(0, spiralRadius - radiusStep);
                    yOffset += yStep;

                    for (int i = 0; i < targets.size(); i++) {
                        Player p = targets.get(i);
                        if (p == null || !p.isOnline()) continue;
                        double angle = 2 * Math.PI * i / Math.max(1, n) + tick * 0.15;
                        double x = center.getX() + spiralRadius * Math.cos(angle);
                        double z = center.getZ() + spiralRadius * Math.sin(angle);
                        double y = center.getY() + yOffset;
                        Location dest = new Location(world, x, y, z);
                        p.teleport(dest);
                    }

                    // Cuando la espiral llegue al centro, ejecutar lanzamiento hacia afuera y hacia arriba
                    if (spiralRadius <= 1.0) {
                        // Lanzar fuera y hacia arriba con mucha fuerza
                        for (Player p : targets) {
                            if (p == null || !p.isOnline()) continue;
                            Vector away = p.getLocation().toVector().subtract(center.toVector()).normalize();
                            if (away.lengthSquared() < 0.0001) away = new Vector(0, 0, 0); // fallback
                            Vector vel = away.multiply(2.5); // componente horizontal
                            vel.setY(10.0); // componente vertical grande (mucho arriba)
                            p.setVelocity(vel);
                        }

                        // 3 segundos después: teleport 3 bloques arriba del lanzador, set fallDistance y luego poner y velocity = -20
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                for (Player p : targets) {
                                    if (p == null || !p.isOnline()) continue;
                                    Location aboveCaster = caster.getLocation().clone().add(0, 3, 0);
                                    // Teleportarlos 3 bloques sobre el lanzador
                                    p.teleport(aboveCaster);
                                    // Fijar fallDistance grande para asegurar daño de caída
                                    try { p.setFallDistance(1000f); } catch (Throwable ignored) {}
                                    // Finalmente, aplicar velocidad vertical negativa severa
                                    Vector down = p.getVelocity();
                                    down.setX(0); down.setZ(0); down.setY(-20.0);
                                    p.setVelocity(down);
                                }
                            }
                        }.runTaskLater(plugin, 60L); // 60 ticks = 3 segundos

                        this.cancel();
                    }

                    // Safety stop after a reasonable number of ticks
                    if (tick > 1000) this.cancel();
                }
            }.runTaskTimer(plugin, 0L, 1L);

            hotbarMessage.sendHotbarMessage(caster, ChatColor.GREEN + "You have used Parrado Spiral");
        },
        (ctx, token) -> {
            Player caster = ctx.caster();
            if (!awakening.isFruitAwakened(caster.getName(), fruitId)) {
                hotbarMessage.sendHotbarMessage(caster, ChatColor.RED + "You must awaken your fruit to use this technique!");
                return false;
            }
            events.DeawakenFruit(caster, fruitId);
            return true;
        }
    );

}
