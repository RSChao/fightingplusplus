package com.rschao.plugins.fightingpp.techs;

import com.rschao.items.Items;
import com.rschao.plugins.fightingpp.Plugin;
import com.rschao.plugins.fightingpp.events.awakening;
import com.rschao.plugins.fightingpp.events.events;
import com.rschao.plugins.techniqueAPI.tech.Technique;
import com.rschao.plugins.techniqueAPI.tech.TechniqueMeta;
import com.rschao.plugins.techniqueAPI.tech.cooldown.CooldownManager;
import com.rschao.plugins.techniqueAPI.tech.cooldown.cooldownHelper;
import com.rschao.plugins.techniqueAPI.tech.feedback.hotbarMessage;
import com.rschao.plugins.techniqueAPI.tech.register.TechRegistry;
import com.rschao.plugins.techniqueAPI.tech.selectors.TargetSelectors;
import de.slikey.effectlib.Effect;
import de.slikey.effectlib.effect.HelixEffect;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffectTypeCategory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class chao {
    static final String fruitId = "chao";
    static final Plugin plugin = Plugin.getPlugin(Plugin.class);

    public static void Register() {
        TechRegistry.registerTechnique(fruitId, food);
        TechRegistry.registerTechnique(fruitId, pearl);
        TechRegistry.registerTechnique(fruitId, armor);
        TechRegistry.registerTechnique(fruitId, tp);
        TechRegistry.registerTechnique(fruitId, feast);
        TechRegistry.registerTechnique(fruitId, purify);
        TechRegistry.registerTechnique(fruitId, spiral);
        TechRegistry.registerTechnique(fruitId, slashes);
        TechRegistry.registerTechnique(fruitId, soulstorm);
        Plugin.registerFruitID(fruitId);
    }


    static Technique food = new Technique("food", "Abundance of Food", new TechniqueMeta(false, cooldownHelper.minutesToMiliseconds(1), List.of("Genera comida gratis")), TargetSelectors.self(), (ctx, token) ->{
        Player player = ctx.caster();
        String playerName = player.getName();
        int rng = com.rschao.events.events.getRNG(0, 100);
        if (player.isSprinting()) {
            if (awakening.isFruitAwakened(playerName, fruitId)) {
                player.getInventory().addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 32));
                hotbarMessage.sendHotbarMessage(player, ChatColor.DARK_PURPLE + "You have used the Free Notch technique");
                Bukkit.getScheduler().runTaskLater(plugin, () -> CooldownManager.setCooldown(player, "food", 180000), 2);
            } else {
                if (rng < 30) {
                    player.getInventory().addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 32));
                    hotbarMessage.sendHotbarMessage(player, ChatColor.DARK_PURPLE + "You have used the Free Notch technique");
                    CooldownManager.setCooldown(player, "food", 180000);
                    Bukkit.getScheduler().runTaskLater(plugin, () -> CooldownManager.setCooldown(player, "food", 180000), 2);
                } else {
                    player.getInventory().addItem(new ItemStack(Material.GOLDEN_CARROT, 64));
                    hotbarMessage.sendHotbarMessage(player, ChatColor.YELLOW + "You have used the Abundance of food technique");
                }
            }
        } else {
            player.getInventory().addItem(new ItemStack(Material.GOLDEN_CARROT, 64));
            hotbarMessage.sendHotbarMessage(player, ChatColor.YELLOW + "You have used the Abundance of food technique");
        }
    });
    static Technique pearl = new Technique("pearl", "Free Pearl", new TechniqueMeta(false, cooldownHelper.minutesToMiliseconds(1), List.of("Genera perlas gratis")), TargetSelectors.self(), (ctx, token) ->{
        Player player = ctx.caster();
        ItemStack[] inventoryContents = player.getInventory().getContents();
        int freeSlot = -1;
        for (int i = 0; i < inventoryContents.length; i++) {
            if (inventoryContents[i] == null || inventoryContents[i].getType() == Material.AIR) {
                freeSlot = i;
                break;
            }
        }
        if (freeSlot == -1) {
            hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "You have no free inventory slots for a Free Pearl!");
            return;
        }
        ItemStack pearl = new ItemStack(Material.ENDER_PEARL, 64);
        player.getInventory().setItem(freeSlot, pearl);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(ChatColor.DARK_GREEN + "You have used the Free Pearl technique"));
    });
    @SuppressWarnings("deprecation")
    static Technique armor = new Technique("armor", "Armor Haki", new TechniqueMeta(false, cooldownHelper.minutesToMiliseconds(5), List.of("Repara armor")), TargetSelectors.self(), (ctx, token) ->{
        Player player = ctx.caster();
        String playerName = player.getName();
        ItemStack[] armorContents = player.getInventory().getArmorContents();
        if (awakening.isFruitAwakened(playerName, fruitId)) {
            for (ItemStack item : armorContents) {
                if (item != null && item.getItemMeta() instanceof Damageable meta && item.getDurability() < item.getType().getMaxDurability()) {
                    meta.setDamage(0); // Set durability to maximum
                    item.setItemMeta(meta);
                }
            }
            armorContents = player.getInventory().getContents();
        }

        for (ItemStack item : armorContents) {
            if (item != null && item.getItemMeta() instanceof Damageable && item.getDurability() < item.getType().getMaxDurability()) {
                Damageable meta = (Damageable) item.getItemMeta();
                meta.setDamage(0); // Set durability to maximum
                item.setItemMeta(meta);
            }
        }
        hotbarMessage.sendHotbarMessage(player, ChatColor.DARK_PURPLE + "You have used the Armor Haki technique");
    });
    static Technique tp = new Technique("tp", "Dont get Lost", new TechniqueMeta(false, cooldownHelper.minutesToMiliseconds(3), List.of("Tp a jugadores cercanos")), TargetSelectors.self(), (ctx, token) ->{
        Player player = ctx.caster();
        Location someLocation = player.getLocation();
        Player closestPlayer = getClosestPlayer(someLocation);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("gaster.boss")) {
                player.teleport(p.getLocation());
                hotbarMessage.sendHotbarMessage(player, ChatColor.DARK_PURPLE + "You have used the Dont get Lost technique!");
                return;
            }
        }
        if (closestPlayer != null) {
            if (someLocation.distance(closestPlayer.getLocation()) < 400) {
                player.teleport(closestPlayer.getLocation());
                hotbarMessage.sendHotbarMessage(player, ChatColor.DARK_PURPLE + "You have used the Dont get Lost technique!");
            } else {
                hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "The closest player is too far away to teleport to!");
            }
        } else {
            hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "No players found nearby to teleport to!");
        }
    });
    static Technique feast = new Technique("feast", "Golden Feast", new TechniqueMeta(false, cooldownHelper.minutesToMiliseconds(1), List.of("Cura hambre y saturación")), TargetSelectors.self(), (ctx, token) ->{
        Player player = ctx.caster();
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 60 * 20, 2));
        hotbarMessage.sendHotbarMessage(player, ChatColor.DARK_PURPLE + "You have used the Golden Feast technique");
    });
    static Technique purify = new Technique("purify", "Heavenly Purification", new TechniqueMeta(false, cooldownHelper.minutesToMiliseconds(6), List.of("Elimina efectos negativos")), TargetSelectors.self(), (ctx, token) ->{
        Player player = ctx.caster();
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 10 * 20, 2));
        for (PotionEffect effect : player.getActivePotionEffects()) {
            if (effect.getType().getCategory() == PotionEffectTypeCategory.HARMFUL) {
                player.removePotionEffect(effect.getType());
            }
        }
        hotbarMessage.sendHotbarMessage(player, ChatColor.LIGHT_PURPLE + "You have used the Heavenly Purification technique");
    });
    static Technique shields = new Technique("shields", "Magic Shields", new TechniqueMeta(false, cooldownHelper.minutesToMiliseconds(4), List.of("Te defiende de flechas y proyectiles :)")), TargetSelectors.self(), (ctx, token) ->{
        Player player = ctx.caster();
        // Passive ability: lasts 1 minute (1200 ticks), checks every tick
        new BukkitRunnable() {
            int ticks = 0;
            final Material shieldBlock = Material.CYAN_STAINED_GLASS;
            final int radius = 4;
            final int shieldDuration = 5; // ticks
            final Set<Block> spawnedBlocks = new HashSet<>();

            @Override
            public void run() {
                if (ticks >= 1200 || !player.isOnline()) { // 1 minute = 1200 ticks
                    this.cancel();
                    return;
                }

                Location playerLoc = player.getLocation();
                List<Entity> projectiles = new ArrayList<>();
                for (Entity entity : player.getNearbyEntities(11, 11, 11)) {
                    if (entity instanceof Projectile) {
                        projectiles.add(entity);
                    }
                }
                if (!projectiles.isEmpty()) {
                    for (Entity proj : projectiles) {
                        // 75% chance to activate shield per projectile
                        if (com.rschao.events.events.getRNG(1, 100) <= 75) {
                            Vector dir = proj.getLocation().toVector().subtract(playerLoc.toVector()).normalize();
                            // Place blocks in a partial circle (arc) between player and projectile
                            for (int angle = -30; angle <= 30; angle += 10) {
                                Vector rotated = dir.clone().rotateAroundY(Math.toRadians(angle)).multiply(radius);
                                Location blockLoc = playerLoc.clone().add(rotated).getBlock().getLocation();
                                Block block = blockLoc.getBlock();
                                if (block.getType() == Material.AIR) {
                                    block.setType(shieldBlock);
                                    spawnedBlocks.add(block);
                                }
                            }
                        }
                    }
                    // Remove spawned blocks after 5 ticks
                    if (!spawnedBlocks.isEmpty()) {
                        Set<Block> toRemove = new HashSet<>(spawnedBlocks);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            for (Block b : toRemove) {
                                if (b.getType() == shieldBlock) {
                                    b.setType(Material.AIR);
                                }
                            }
                            spawnedBlocks.removeAll(toRemove);
                        }, shieldDuration);
                    }
                }
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
        hotbarMessage.sendHotbarMessage(player, ChatColor.DARK_PURPLE + "Magic Shields activated for 1 minute!");
    });
    static Technique spiral = new Technique("spiral", "Spiral of the End", new TechniqueMeta(true, cooldownHelper.hour, List.of("Genocidio")), TargetSelectors.self(), (ctx, token) ->{
        Player player = ctx.caster();
        Player p = player;
        Location center = p.getLocation();
        World world = p.getWorld();

        // 1. Find all entities within 100 blocks (excluding p) and filter out frames/paintings/minecarts/end crystals
        List<Entity> nearbyEntities = new ArrayList<>();
        for (Entity ent : world.getEntities()) {
            if (ent.equals(p)) continue;
            if (!ent.getWorld().equals(world)) continue;
            if (ent.getLocation().distance(center) <= 100) {
                if(!(ent instanceof Player)) continue;
                if (ent instanceof ItemFrame) continue;
                if (ent instanceof Painting) continue;
                if (ent instanceof Minecart) continue;
                if (ent instanceof EnderCrystal) continue;
                nearbyEntities.add(ent);
            }
        }
        if (nearbyEntities.isEmpty()) return;

        int n = nearbyEntities.size();
        double radius = 5.0;

        // 2. Teleport entities in a circle around p
        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n;
            double x = center.getX() + radius * Math.cos(angle);
            double z = center.getZ() + radius * Math.sin(angle);
            Location loc = new Location(world, x, center.getY(), z);
            nearbyEntities.get(i).teleport(loc);
        }

        // 3. Spiral movement task
        new BukkitRunnable() {
            double spiralRadius = radius;
            double yOffset = 0;
            int tick = 0;
            final double spiralStep = 0.2; // how much to move up per tick
            final double radiusStep = 0.1; // how much to shrink radius per tick

            @Override
            public void run() {
                tick++;
                spiralRadius = Math.max(0, spiralRadius - radiusStep);
                yOffset += spiralStep;

                // Move entities in spiral
                for (int i = 0; i < n; i++) {
                    double angle = 2 * Math.PI * i / n + tick * 0.2;
                    double x = center.getX() + spiralRadius * Math.cos(angle);
                    double z = center.getZ() + spiralRadius * Math.sin(angle);
                    double y = center.getY() + yOffset;
                    Location loc = new Location(world, x, y, z);
                    nearbyEntities.get(i).teleport(loc);
                }

                // Stop when radius is almost zero
                if (spiralRadius <= 0.2) {
                    this.cancel();
                    // 4. Explosions at each entity's location (excluding p)
                    for (Entity ent : nearbyEntities) {
                        Location loc = ent.getLocation();
                        p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 20 * 5, 255)); // resistance for 5 seconds
                        if(ent instanceof Player p){

                            p.damage(1000.0, player);
                        }
                        else{
                            if(ent instanceof LivingEntity){
                                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                    ((LivingEntity) ent).damage(700.0, p);
                                }, 2);
                            }
                        }
                        world.createExplosion(loc, 100.0F, false, true, p); // very powerful explosion
                    }
                }
            }
        }.runTaskTimer(plugin, 10L, 2L); // start after 10 ticks, repeat every 2 ticks
        hotbarMessage.sendHotbarMessage(player, ChatColor.DARK_PURPLE + "You have used the Spiral of the End technique");
    }, (ctx, token) -> {
        Player player = ctx.caster();

        if (!awakening.isFruitAwakened(player.getName(), fruitId)) {
            hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "You must awaken your fruit to use this technique!");
            return false;
        }
        events.DeawakenFruit(player, fruitId);
        return true;
    });
    static Technique slashes = new Technique("slashes", "Flying Slashes of Hatred", new TechniqueMeta(true, cooldownHelper.hour, List.of("Flechitas matan cosas si tienes xp")), TargetSelectors.self(), (ctx, token) ->{
        Player player = ctx.caster();
        Player p = player;
        Location center = p.getLocation();
        World world = p.getWorld();


        // 1. Teleport all players within 50 blocks (excluding p) to 5 blocks in front of p
        Vector direction = p.getLocation().getDirection().normalize();
        Location frontLoc = center.clone().add(direction.clone().multiply(5));
        for (Player pl : world.getPlayers()) {
            if (!pl.equals(p) && pl.getLocation().distance(center) <= 50) {
                pl.teleport(frontLoc);
            }
        }

        // 2. Schedule arrow spreads
        // Horizontal spread after 2 ticks
        new BukkitRunnable() {
            @Override
            public void run() {
                for (int i = -2; i <= 2; i++) {
                    Vector spread = direction.clone().rotateAroundY(i * Math.PI / 16);
                    Arrow arrow = world.spawnArrow(center.clone().add(0, 1.5, 0), spread, 2.0f, 0.1f);
                    arrow.setPierceLevel(7);
                }
            }
        }.runTaskLater(plugin, 2L);

        // Diagonal spread after 2+15=17 ticks
        new BukkitRunnable() {
            @Override
            public void run() {
                Vector up = new Vector(0, 0.2, 0);
                for (int i = -2; i <= 2; i++) {
                    Vector diag = direction.clone().rotateAroundY(i * Math.PI / 16).add(up.clone().multiply(i * 0.2));
                    Arrow arrow = world.spawnArrow(center.clone().add(0, 1.5, 0), diag, 2.0f, 0.1f);
                    arrow.setPierceLevel(7);
                }
            }
        }.runTaskLater(plugin, 17L);

        // Combined spread after 2+15+15=32 ticks
        new BukkitRunnable() {
            @Override
            public void run() {
                int xp = p.getLevel();
                //if xp == 0, set it to 1
                if (xp == 0) xp = 1;
                double baseDamage = 3.0 * xp;
                Vector up = new Vector(0, 0.2, 0);
                // Horizontal
                for (int i = -2; i <= 2; i++) {
                    Vector spread = direction.clone().rotateAroundY(i * Math.PI / 16);
                    Arrow arrow = world.spawnArrow(center.clone().add(0, 1.5, 0), spread, 2.0f, 0.1f);
                    arrow.setPierceLevel(7);
                    arrow.setDamage(baseDamage);
                }
                // Diagonal (up left/down right)
                for (int i = -2; i <= 2; i++) {
                    Vector diag = direction.clone().rotateAroundY(i * Math.PI / 16).add(up.clone().multiply(i * 0.2));
                    Arrow arrow = world.spawnArrow(center.clone().add(0, 1.5, 0), diag, 2.0f, 0.1f);
                    arrow.setPierceLevel(7);
                    arrow.setDamage(baseDamage);
                }
                // Vertical spread
                for (int i = -2; i <= 2; i++) {
                    Vector vert = direction.clone().add(new Vector(0, i * 0.2, 0));
                    Arrow arrow = world.spawnArrow(center.clone().add(0, 1.5, 0), vert, 2.0f, 0.1f);
                    arrow.setPierceLevel(7);
                    arrow.setDamage(baseDamage);
                }
            }
        }.runTaskLater(plugin, 35L);
        hotbarMessage.sendHotbarMessage(player, ChatColor.DARK_PURPLE + "You have used the Chao Slashes technique");
    }, (ctx, token) -> {
        Player player = ctx.caster();

        if (!awakening.isFruitAwakened(player.getName(), fruitId)) {
            hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "You must awaken your fruit to use this technique!");
            return false;
        }
        events.DeawakenFruit(player, fruitId);
        return true;
    });

    static Technique soulstorm = new Technique("soulstorm", "Soulstorm", new TechniqueMeta(true, cooldownHelper.hour, List.of("Revienta a jugadores cercanos (2 o 3 pops)")), TargetSelectors.self(), (ctx, token) ->{
        Player player = ctx.caster();

        // Paso 1: Flotar y quitar gravedad
        Location startLoc = player.getLocation().clone();
        startLoc.setY(startLoc.getY() + 1);
        player.teleport(startLoc);
        player.setGravity(false);
        player.setVelocity(new Vector(0, 0.1, 0));

        hotbarMessage.sendHotbarMessage(player, ChatColor.LIGHT_PURPLE + "¡Soulstorm iniciado!");
        List<Player> enemies = new ArrayList<>();
        for (Player p : player.getWorld().getPlayers()) {
            if (!p.equals(player) && p.getLocation().distance(startLoc) <= 80) {
                enemies.add(p);
            }
        }

        if(enemies.isEmpty()) {
            hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "No hay enemigos en el radio para afectar con Soulstorm.");
            player.setGravity(true);
            awakening.setFruitAwakened(player.getName(), "chao", true);
            awakening.setFruitAwakened(player.getName(), "fly", true);
            return;
        }
        // Paso 2: Fijar posición tras 2 segundos
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Location animLoc = player.getLocation().clone();
            // Teletransporte constante
            BukkitRunnable tpTask = new BukkitRunnable() {
                @Override
                public void run() {
                    if (!player.isOnline()) {
                        this.cancel();
                        return;
                    }
                    player.teleport(animLoc);
                }
            };
            tpTask.runTaskTimer(plugin, 0L, 1L);
            // Paso 3: Invocar 7 ítems en círculo vertical detrás del jugador
            ItemStack[] items = {
                    Items.soul_Determination,
                    Items.soul_Kind,
                    Items.soul_Patience,
                    Items.soul_Bravery,
                    Items.soul_Justice,
                    Items.soul_Integrity,
                    Items.soul_Persevearence
            };
            List<Item> spawnedItems = new ArrayList<>();
            World world = animLoc.getWorld();
            Vector backDir = animLoc.getDirection().normalize().multiply(-1);
            Location circleCenter = animLoc.clone().add(backDir);
            HelixEffect e = new HelixEffect(Plugin.getEffectManager());
            e.setLocation(animLoc);
            e.start();
            for (int i = 0; i < 7; i++) {
                final int idx = i;
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    double angle = 2 * Math.PI * idx / 7;
                    double radius = 1.2;
                    double x = circleCenter.getX() + radius * Math.cos(angle);
                    double y = circleCenter.getY();
                    double z = circleCenter.getZ() + radius * Math.sin(angle);
                    Location itemLoc = new Location(world, x, y, z);

                    ItemStack stack = items[idx].clone();
                    Item itemEntity = world.dropItem(itemLoc, stack);
                    itemEntity.setPickupDelay(Integer.MAX_VALUE);
                    itemEntity.setGravity(false);
                    itemEntity.setVelocity(new Vector(0, 0, 0));
                    spawnedItems.add(itemEntity);
                }, 20L * idx); // 1 segundo entre cada ítem
            }

            // Paso 4: Explosiones y daño tras aparecer los 7 ítems
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                // Guardar enemigos en radio de 20 bloques al inicio
                // Contador de 60 ticks, cada 20 ticks explosión y daño
                BukkitRunnable explosionTask = new BukkitRunnable() {
                    int tick = 0;

                    @Override
                    public void run() {
                        for (Player p : enemies) {
                            //get resistance potion effect
                            PotionEffect[] effects = p.getActivePotionEffects().toArray(new PotionEffect[0]);
                            if (effects.length > 0) {
                                for (PotionEffect effect : effects) {
                                    if (effect.getType().equals(PotionEffectType.RESISTANCE)) {
                                        if (effect.getAmplifier() < 5) {
                                            p.getActivePotionEffects().remove(effect);
                                        }
                                    }
                                }
                            }
                        }
                        if (tick % 20 == 0) {
                            for (Player enemy : enemies) {
                                player.sendMessage(enemy.getName());
                                Location feet = enemy.getLocation().clone().add(0, 1, 0);
                                //world.createExplosion(feet, 7.0F, false, true, player);
                                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                    enemy.damage(1000.0, player);
                                }, 2);
                                world.createExplosion(feet.add(0, 1, 0), 7.0F, false, false, player);
                            }
                        }
                        tick += 1;
                        if (tick > 40) {
                            // Eliminar ítems y restaurar gravedad
                            for (Item item : spawnedItems) {
                                if (!item.isDead()) item.remove();
                            }
                            player.setGravity(true);
                            tpTask.cancel();
                            e.cancel();
                            hotbarMessage.sendHotbarMessage(player, ChatColor.LIGHT_PURPLE + "¡Soulstorm finalizado!");
                            this.cancel();
                        }
                    }
                };
                explosionTask.runTaskTimer(plugin, 0L, 1L);
            }, 20L * 7); // Espera a que aparezcan los 7 ítems
        }, 40L); // 2 segundos después de iniciar
    }, (ctx, token) -> {
        Player player = ctx.caster();
        if (!awakening.isFruitAwakened(player.getName(), "fly") || !awakening.isFruitAwakened(player.getName(), "chao")) {
            hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "¡Debes tener ambos frutos despertados para usar esta técnica!");
            return false;
        }
        events.DeawakenFruit(player, "fly");
        events.DeawakenFruit(player, "chao");
        return true;
    });


    public static Set<Block> sphereAround(Location location, int radius) {
        Set<Block> sphere = new HashSet<Block>();
        Block center = location.getBlock();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Block b = center.getRelative(x, y, z);
                    if (center.getLocation().distance(b.getLocation()) <= radius && center.getLocation().distance(b.getLocation()) > (radius - 2)) {
                        sphere.add(b);
                    }
                }

            }
        }
        return sphere;
    }

    public static Player getClosestPlayer(Location location) {
        Player closestPlayer = null;
        double closestDistance = Double.MAX_VALUE;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld() != location.getWorld()) continue; // Skip players in different worlds
            double distance = player.getLocation().distance(location);
            if (distance > 1 && distance < closestDistance) {
                closestDistance = distance;
                closestPlayer = player;
            }
        }

        return closestPlayer;
    }


}