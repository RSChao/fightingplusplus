package com.rschao.plugins.fightingpp.techs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffectTypeCategory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.rschao.plugins.fightingpp.Plugin;
import com.rschao.plugins.fightingpp.events.awakening;
import com.rschao.plugins.fightingpp.events.events;
import com.rschao.plugins.techapi.tech.Technique;
import com.rschao.plugins.techapi.tech.cooldown.cooldownHelper;
import com.rschao.plugins.techapi.tech.feedback.hotbarMessage;
import com.rschao.plugins.techapi.tech.register.TechRegistry;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class chao {
    static final String fruitId = "chao";
    static final Plugin plugin = Plugin.getPlugin(Plugin.class);
    public static void Register(){
        TechRegistry.registerTechnique(fruitId, food);
        TechRegistry.registerTechnique(fruitId, pearl);
        TechRegistry.registerTechnique(fruitId, armor);
        TechRegistry.registerTechnique(fruitId, tp);
        TechRegistry.registerTechnique(fruitId, feast);
        TechRegistry.registerTechnique(fruitId, purify);
        TechRegistry.registerTechnique(fruitId, shields);
        TechRegistry.registerTechnique(fruitId, spiral);
        TechRegistry.registerTechnique(fruitId, slashes);
        //TechRegistry.registerTechnique("god", soulstorm); // Register the new technique
    }



    static Technique food = new Technique("food", "Abundance of Food", false, 120000, (player, fruit, code) -> {
        String playerName = player.getName();
        int rng = com.rschao.events.events.getRNG(0, 100);
        if (player.isSneaking()) {
            if (awakening.isFruitAwakened(playerName, fruitId)) {
                player.getInventory().addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 32));
                hotbarMessage.sendHotbarMessage(player, ChatColor.DARK_PURPLE + "You have used the Free Notch technique");
            } else {
                if (rng < 30) {
                    player.getInventory().addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 32));
                    hotbarMessage.sendHotbarMessage(player, ChatColor.DARK_PURPLE + "You have used the Free Notch technique");
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
    static Technique pearl = new Technique("pearl", "Free Pearl", false, 60000, (player, fruit, code) -> {
        //check the player's first free inventory slot
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
    static Technique armor = new Technique("armor", "Armor Haki", false, cooldownHelper.secondsToMiliseconds(300), (player, fruit, code) -> {
        String playerName = player.getName();
        ItemStack[] armorContents = player.getInventory().getArmorContents();
                        if(awakening.isFruitAwakened(playerName, fruitId)){
                            for (ItemStack item : armorContents) {
                                if (item != null && item.getItemMeta() instanceof Damageable) {
                                    Damageable meta = (Damageable) item.getItemMeta();
                                    meta.setDamage(0); // Set durability to maximum
                                    item.setItemMeta(meta);
                                }
                            }
                            armorContents = player.getInventory().getContents();
                        }

                        for (ItemStack item : armorContents) {
                            if (item != null && item.getItemMeta() instanceof Damageable) {
                                Damageable meta = (Damageable) item.getItemMeta();
                                meta.setDamage(0); // Set durability to maximum
                                item.setItemMeta(meta);
                            }
                        }
                        hotbarMessage.sendHotbarMessage(player, ChatColor.DARK_PURPLE + "You have used the Armor Haki technique");
    });
    static Technique tp = new Technique("tp", "Dont get Lost", false, cooldownHelper.secondsToMiliseconds(180), (player, fruit, code) -> {
        Location someLocation = player.getLocation();
                        Player closestPlayer = getClosestPlayer(someLocation);

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
    static Technique feast = new Technique("feast", "Golden Feast", false, cooldownHelper.secondsToMiliseconds(300), (player, fruit, code) -> {
        player.setFoodLevel(20);
                        player.setSaturation(20);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 60 * 20, 2));
                        hotbarMessage.sendHotbarMessage(player, ChatColor.DARK_PURPLE + "You have used the Golden Feast technique");
    });
    static Technique purify = new Technique("purify", "Heavenly Purification", false, cooldownHelper.secondsToMiliseconds(420), (player, fruit, code) -> {
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 10 * 20, 2));
                        for(PotionEffect effect: player.getActivePotionEffects()){
                            if(effect.getType().getCategory() == PotionEffectTypeCategory.HARMFUL){
                                player.removePotionEffect(effect.getType());
                            }
                        }
        hotbarMessage.sendHotbarMessage(player, ChatColor.LIGHT_PURPLE + "You have used the Heavenly Purification technique");
    });
    static Technique shields = new Technique("shields", "Magic Shields", false, cooldownHelper.secondsToMiliseconds(20), (player, fruit, code) -> {
        // For 100 ticks, check for projectiles and spawn shield blocks between player and projectile
        new BukkitRunnable() {
            int ticks = 0;
            final Material shieldBlock = Material.CYAN_STAINED_GLASS;
            final int radius = 4;
            final int shieldDuration = 5; // ticks
            final Set<Block> spawnedBlocks = new HashSet<>();

            @Override
            public void run() {
                if (ticks >= 100 || !player.isOnline()) {
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
        hotbarMessage.sendHotbarMessage(player, ChatColor.DARK_PURPLE + "You have used the Magic Shields technique");
    });
    static Technique spiral = new Technique("spiral", "Spiral of the End", true, cooldownHelper.secondsToMiliseconds(3600), (player, fruit, code) -> {
        if (!awakening.isFruitAwakened(player.getName(), fruitId)) {
            hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "You must awaken your fruit to use this technique!");
            return;
        }
        events.DeawakenFruit(player, fruitId);
        Player p = player;
        Location center = p.getLocation();
        World world = p.getWorld();

        // 1. Find all players within 100 blocks (excluding p)
        List<Player> nearbyPlayers = new ArrayList<>();
        for (Player pl : world.getPlayers()) {
            if (!pl.equals(p) && pl.getLocation().distance(center) <= 100) {
                nearbyPlayers.add(pl);
            }
        }
        if (nearbyPlayers.isEmpty()) return;

        int n = nearbyPlayers.size();
        double radius = 5.0;

        // 2. Teleport players in a circle around p
        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n;
            double x = center.getX() + radius * Math.cos(angle);
            double z = center.getZ() + radius * Math.sin(angle);
            Location loc = new Location(world, x, center.getY(), z);
            nearbyPlayers.get(i).teleport(loc);
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

                // Move players in spiral
                for (int i = 0; i < n; i++) {
                    double angle = 2 * Math.PI * i / n + tick * 0.2;
                    double x = center.getX() + spiralRadius * Math.cos(angle);
                    double z = center.getZ() + spiralRadius * Math.sin(angle);
                    double y = center.getY() + yOffset;
                    Location loc = new Location(world, x, y, z);
                    nearbyPlayers.get(i).teleport(loc);
                }

                // Stop when all players are at the same block (radius ~ 0)
                if (spiralRadius <= 0.2) {
                    this.cancel();
                    // 4. Explosions at each player's location (not p)
                    for (Player pl : nearbyPlayers) {
                        Location loc = pl.getLocation();
                        p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 20 * 5, 255)); // resistance for 5 seconds
                        world.createExplosion(loc, 100.0F, true, true, p); // very powerful explosion
                    }
                }
            }
        }.runTaskTimer(plugin, 10L, 2L); // start after 10 ticks, repeat every 2 ticks
        hotbarMessage.sendHotbarMessage(player, ChatColor.DARK_PURPLE + "You have used the Spiral of the End technique");
    });
    static Technique slashes = new Technique("slashes", "Flying Slashes of Hatred", true, cooldownHelper.secondsToMiliseconds(3600), (player, fruit, code) -> {
        if (!awakening.isFruitAwakened(player.getName(), fruitId)) {
            hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "You must awaken your fruit to use this technique!");
            return;
        }
        Player p = player;
        Location center = p.getLocation();
        World world = p.getWorld();
        events.DeawakenFruit(player, fruitId);

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
    });
    static Technique soulstorm = new Technique("soulstorm", "Soulstorm", true, cooldownHelper.secondsToMiliseconds(3600), (player, fruit, code) -> {
        // 1. Lift player 5 blocks up slowly and freeze them
        final int liftTicks = 40; // 2 seconds (20 ticks per second)
        final double liftAmount = 5.0;
        final double liftPerTick = liftAmount / liftTicks;
        final Location startLoc = player.getLocation().clone();
        final float yaw = startLoc.getYaw();
        final float pitch = startLoc.getPitch();

        // 2. Prepare glass colors (Undertale Soul colors)
        final Material[] soulColors = new Material[] {
            Material.RED_STAINED_GLASS,      // Red
            Material.ORANGE_STAINED_GLASS,   // Orange
            Material.YELLOW_STAINED_GLASS,   // Yellow
            Material.LIME_STAINED_GLASS,     // Green
            Material.LIGHT_BLUE_STAINED_GLASS, // Light Blue
            Material.BLUE_STAINED_GLASS,     // Blue
            Material.PURPLE_STAINED_GLASS    // Purple (Violet)
        };

        final List<Block> spawnedGlass = new ArrayList<>();
        final World world = player.getWorld();

        // Freeze player by disabling movement
        player.setGravity(false);
        player.setVelocity(new Vector(0, 0, 0));

        // Lifting task
        new BukkitRunnable() {
            int tick = 0;
            @Override
            public void run() {
                if (!player.isOnline()) {
                    player.setGravity(true);
                    this.cancel();
                    return;
                }
                if (tick < liftTicks) {
                    Location loc = startLoc.clone().add(0, tick * liftPerTick, 0);
                    loc.setYaw(yaw);
                    loc.setPitch(pitch);
                    player.teleport(loc);
                    player.setVelocity(new Vector(0, 0, 0));
                    tick++;
                } else {
                    // Keep player frozen at top position
                    Location loc = startLoc.clone().add(0, liftAmount, 0);
                    loc.setYaw(yaw);
                    loc.setPitch(pitch);
                    player.teleport(loc);
                    player.setVelocity(new Vector(0, 0, 0));
                }
                if(tick >= 100){
                    // After 100 ticks, unfreeze player
                    player.setGravity(true);
                    player.setVelocity(new Vector(0, 0, 0));
                    this.cancel();
                    return;
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);

        // 3. Spawn glass blocks in a circle, 10 ticks apart
        new BukkitRunnable() {
            int colorIdx = 0;
            final double radius = 2.5;
            @Override
            public void run() {
                if (colorIdx >= soulColors.length) {
                    this.cancel();
                    // After all glass blocks are spawned, proceed to TNT phase
                    spawnTNTs();
                    return;
                }
                // Calculate position in a vertical circle (halo) around the player, relative to yaw
                double angle = Math.toRadians((360.0 / soulColors.length) * colorIdx);
                // Get player's facing direction as axis
                Vector facing = player.getLocation().getDirection().setY(0).normalize();
                Vector up = new Vector(0, 1, 0);
                Vector right = up.clone().crossProduct(facing).normalize(); // FIXED: swapped order

                // Vertical circle: x = radius * cos(angle) * right + radius * sin(angle) * up
                Vector offset = right.clone().multiply(Math.cos(angle)).add(up.clone().multiply(Math.sin(angle))).multiply(radius);
                double x = startLoc.getX() + offset.getX();
                double y = startLoc.getY() + liftAmount + offset.getY();
                double z = startLoc.getZ() + offset.getZ();
                Location glassLoc = new Location(world, x, y, z);
                Block block = glassLoc.getBlock();
                if (block.getType() == Material.AIR) {
                    block.setType(soulColors[colorIdx]);
                    spawnedGlass.add(block);
                }
                colorIdx++;
            }
            // After all glass, call TNT phase
            void spawnTNTs() {
                // 4. Shoot TNT from each glass block in all directions
                final int tntPerBlock = 100;
                final double tntSpeed = 2.0;
                final int tntFuse = 80; // 4 seconds (20 ticks per second)
                for (Block glass : spawnedGlass) {
                    Location loc = glass.getLocation().add(0.5, 0.5, 0.5);
                    for (int i = 0; i < tntPerBlock; i++) {
                        double theta = 2 * Math.PI * i / tntPerBlock;
                        Vector dir = new Vector(Math.cos(theta), 0.1, Math.sin(theta)).normalize().multiply(tntSpeed);
                        org.bukkit.entity.TNTPrimed tnt = (org.bukkit.entity.TNTPrimed) world.spawn(loc, org.bukkit.entity.TNTPrimed.class);
                        tnt.setFuseTicks(tntFuse);
                        tnt.setVelocity(dir);
                    }
                }
                // Remove glass blocks after TNTs are fired
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    for (Block b : spawnedGlass) {
                        if (b.getType().toString().endsWith("_STAINED_GLASS")) {
                            b.setType(Material.AIR);
                        }
                    }
                }, 20L);

                // 5. Teleport all players to their bed 5 ticks before the final blast
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    for (Player pl : Bukkit.getOnlinePlayers()) {
                        Location bed = pl.getBedSpawnLocation();
                        if (bed != null) {
                            pl.teleport(bed);
                        }
                    }
                }, 80L + tntFuse - 5); // 5 ticks before explosion

                // 6. Massive explosions at (0,80,0) and at each player's location
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    // (0,80,0) explosion
                    World w = Bukkit.getWorlds().get(0);
                    Location epicenter = new Location(w, 0, 80, 0);
                    w.createExplosion(epicenter, 200.0F, true, true);
                    // At each player's location
                    for (Player pl : Bukkit.getOnlinePlayers()) {
                        pl.getWorld().createExplosion(pl.getLocation(), 200.0F, true, true);
                    }
                    // Unfreeze the original player
                    player.setGravity(true);
                }, 80L + tntFuse); // After TNTs explode
            }
        }.runTaskTimer(plugin, liftTicks, 10L);

        hotbarMessage.sendHotbarMessage(player, ChatColor.LIGHT_PURPLE + "You have used the Soulstorm technique!");
    });



    public static Set<Block> sphereAround(Location location, int radius) {
        Set<Block> sphere = new HashSet<Block>();
        Block center = location.getBlock();
        for(int x = -radius; x <= radius; x++) {
            for(int y = -radius; y <= radius; y++) {
                for(int z = -radius; z <= radius; z++) {
                    Block b = center.getRelative(x, y, z);
                    if(center.getLocation().distance(b.getLocation()) <= radius && center.getLocation().distance(b.getLocation()) > (radius - 2)) {
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
            double distance = player.getLocation().distance(location);
            if (distance > 1 && distance < closestDistance) {
                closestDistance = distance;
                closestPlayer = player;
            }
        }

        return closestPlayer;
    }



    
}