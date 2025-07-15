package com.rschao.plugins.fightingpp.events;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.loot.LootTables;
import org.bukkit.persistence.PersistentDataType;

import com.rschao.items.weapons;
import com.rschao.plugins.fightingpp.Plugin;
import com.rschao.plugins.fightingpp.api.DevilFruit;
import com.rschao.plugins.fightingpp.events.definitions.PlayerUseUltimate;
import com.rschao.plugins.fightingpp.items.fruits;
import com.rschao.plugins.techapi.tech.PlayerTechniqueManager;
import com.rschao.plugins.techapi.tech.Technique;
import com.rschao.plugins.techapi.tech.cooldown.CooldownManager;
import com.rschao.plugins.techapi.tech.feedback.hotbarMessage;
import com.rschao.plugins.techapi.tech.register.TechRegistry;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class events implements Listener {
    public static Map<String, Integer> playerTechniques = new HashMap<>();
    public static Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();
    public static final String path = Plugin.getPlugin(Plugin.class).getDataFolder().getAbsolutePath();

    public static boolean checkEaten(String playerName) {
        File configFile = new File(path, "fruits.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        return config.contains("fruits." + playerName + ".fruits");
    }

    private boolean isFruitAlreadyEaten(String fruitId) {
        File configFile = new File(path, "fruits.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        if(!config.contains("fruits")) {
            return false;
        }
        for (String playerName : config.getConfigurationSection("fruits").getKeys(false)) {
            List<String> playerFruits = config.getStringList("fruits." + playerName + ".fruits");
            if (playerFruits.contains(fruitId)) {
                return true;
            }
        }
        return false;
    }
    private List<String> getPlayerFruits(String playerName) {
        File configFile = new File(path, "fruits.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
    
        return config.getStringList("fruits." + playerName + ".fruits");
    }
    public static void saveFruitToConfig(String playerName, String fruitId) {
        File configFile = new File(path, "fruits.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
    
        List<String> fruits = config.getStringList("fruits." + playerName + ".fruits");
        if (!fruits.contains(fruitId)) {
            fruits.add(fruitId);
        }
        config.set("fruits." + playerName + ".fruits", fruits);
    
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    void onPlayerEat(PlayerItemConsumeEvent ev) {
        String playerName = ev.getPlayer().getName();
        if (playerName.startsWith(".")) {
            playerName = playerName.substring(1);
        }
        ItemStack item = ev.getItem();
        try {
            DevilFruit.getFruitKey(item);
        } catch (Exception e) {
            return;
        }
        if (item.getItemMeta().getPersistentDataContainer().has(DevilFruit.getFruitKey(item))) {
            NamespacedKey key = DevilFruit.getFruitKey(item);
            String fruitId = key.getKey();
            String fruitName = item.getItemMeta().getItemName();
            if(isFruitAlreadyEaten(fruitId)) {
                ev.getPlayer().sendMessage("But it turned out to be a fake...");
                return;
            }
            if (!checkEaten(playerName)) {
                // First fruit
                ev.getPlayer().sendMessage("You have eaten the " + fruitName);
                ev.getPlayer().getWorld().dropItemNaturally(ev.getPlayer().getLocation(), fruits.techItem(key, fruitName));
                saveFruitToConfig(playerName, fruitId);
            } else {
                List<String> playerFruits = getPlayerFruits(playerName);
                if (playerFruits.contains(fruitId)) {
                    ev.getPlayer().sendMessage("You have already eaten this fruit.");
                    return;
                }
                if (playerFruits.size() >= 2) {
                    ev.getPlayer().sendMessage(ChatColor.RED + "You cannot eat more than two fruits!");
                    return;
                }
                if (!ev.getPlayer().hasPermission("fruits.second")) {
                    ev.getPlayer().sendMessage(ChatColor.RED + "You cannot eat more fruits!");
                    return;
                }
                // Second fruit
                ev.getPlayer().sendMessage("You have eaten the " + fruitName);
                playerTechniques.put(playerName + "_" + fruitId, 0); playerTechniques.put(playerName, 0); // Initialize technique index
                ev.getPlayer().getWorld().dropItemNaturally(ev.getPlayer().getLocation(), fruits.techItem(key, fruitName));
                saveFruitToConfig(playerName, fruitId);
            }
        }
    }
        

    @EventHandler
    void onPlayerDed(PlayerDeathEvent e) {
        String playerName = e.getEntity().getName();
        if (playerName.startsWith(".")) {
            playerName = playerName.substring(1);
        }
        if (checkEaten(playerName)) {
            File configFile = new File(path, "fruits.yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
            // remove tech usage count
            try{
                config.set("fruits." + playerName + ".fruits", null);
                config.getConfigurationSection("fruits." + playerName + ".fruits").set("fruits", null);
                config.set("fruits." + playerName + ".awakened", null); // Remove awakened state
                for(String key : config.getConfigurationSection("fruits." + playerName + ".tech").getKeys(false)){
                    config.set(key, null);
                    config.getConfigurationSection("fruits." + playerName + ".tech").set(key, null);
                }
                config.set("fruits." + playerName + ".tech", null);
            }catch(Exception ex){
                ex.printStackTrace();
            }
            config.set("fruits." + playerName + ".tech", null);
            

            try {
                config.save(configFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            e.getEntity().sendMessage(ChatColor.DARK_RED + "Your Devil Fruit power has been lost.");
            playerTechniques.remove(playerName); // Remove technique index
        }
    }

    @EventHandler
    void onPlayerInteract(PlayerInteractEvent e) {
        String playerName = e.getPlayer().getName();
        if (playerName.startsWith(".")) {
            playerName = playerName.substring(1);
        }
        awakening.loadAwakenedFromConfig(playerName);
        Player player = e.getPlayer();
        if (e.getItem() == null) return;
        if(e.getItem().getItemMeta() == null) return;
        if (e.getItem().getItemMeta().getPersistentDataContainer().has(new NamespacedKey("fruit", "awaken"),
        PersistentDataType.BOOLEAN)) {
            // Check if the player has eaten any fruits
            if (checkEaten(playerName)) {
                List<String> playerFruits = getPlayerFruits(playerName);

                // Find the first fruit that hasn't been awakened
                for (String fruitId : playerFruits) {
                    if (!awakening.isFruitAwakened(playerName, fruitId)) {
                        // Awaken the fruit (TechAPI + persist)
                        awakening.setFruitAwakened(playerName, fruitId, true);
                        player.sendMessage(ChatColor.GOLD + "Your " + fruitId + " fruit has awakened!");

                        // Save the updated configuration
                        awakening.saveAwakenedToConfig(playerName, fruitId, true);
                    }
                    else {
                        player.sendMessage(ChatColor.RED + "Your " + fruitId + " fruit is already awakened!");
                    }
                }

                e.setCancelled(true);
                e.getItem().setAmount(e.getItem().getAmount() - 1);
            } else {
                player.sendMessage(ChatColor.RED + "You have not eaten any fruits to awaken!");
            }
            return;
        }
        if (!e.getItem().getItemMeta().getPersistentDataContainer().has(new NamespacedKey("fruit", "action"),
                PersistentDataType.BOOLEAN)) {
            return;
        }
        if (checkEaten(playerName) || e.getPlayer().hasPermission("fruits.all")) {
            NamespacedKey fruitKey = null;

            // Iterate through all keys to find the correct fruitId key
            for (NamespacedKey key : e.getItem().getItemMeta().getPersistentDataContainer().getKeys()) {
                if (!key.getKey().equals("action")) { // Exclude "action" key
                    fruitKey = key;
                    break;
                }
            }

            if (fruitKey == null) {
                player.sendMessage(ChatColor.RED + "Could not detect a valid fruit ID.");
                return;
            }

            String fruitId = fruitKey.getKey();
            //check if the player has the fruit
            if(!getPlayerFruits(playerName).contains(fruitId) && !player.hasPermission("fruits.all")) {
                return;
            }
            int techIndex = PlayerTechniqueManager.getCurrentTechnique(player.getUniqueId(), fruitId);
            if(e.getAction().toString().contains("LEFT")){
                Technique technique = null;
                technique = TechRegistry.getAllTechniques(fruitId).get(techIndex);
            
                if(technique == null) return;
                if(technique.isUltimate() && awakening.isFruitAwakened(playerName, fruitId)){
                    if(CooldownManager.isOnCooldown(player, technique.getId())){
                        long remaining = CooldownManager.getRemaining(player, technique.getId());
                        hotbarMessage.sendHotbarMessage(player, "On cooldown! Wait " + (double)remaining / 1000.0 + " seconds.");
                        return;
                    }
                    Bukkit.getScheduler().runTaskLater(Plugin.getPlugin(Plugin.class), () -> {
                        DeawakenFruit(player, fruitId);
                    }, 1L);
                    PlayerUseUltimate ultimateEvent = new PlayerUseUltimate(technique.getName(), player);
                    Bukkit.getPluginManager().callEvent(ultimateEvent);
                }
                technique.use(player, e.getItem(), Technique.nullValue());
            
            }
            else if(e.getAction().toString().contains("RIGHT")){
                if(player.isSneaking()){
                    //switch to the previous technique
                    if (techIndex == 0) {
                        PlayerTechniqueManager.setCurrentTechnique(player.getUniqueId(), fruitId, TechRegistry.getAllTechniques(fruitId).size() - 1);
                    } else {
                        PlayerTechniqueManager.setCurrentTechnique(player.getUniqueId(), fruitId, techIndex - 1);
                    }
                }
                else {
                    PlayerTechniqueManager.setCurrentTechnique(player.getUniqueId(), fruitId, (techIndex + 1) % TechRegistry.getAllTechniques(fruitId).size());
                }
                techIndex = PlayerTechniqueManager.getCurrentTechnique(player.getUniqueId(), fruitId);
                player.sendMessage(ChatColor.GREEN + "Switched to technique: " + TechRegistry.getAllTechniques(fruitId).get(techIndex).getName());
            }
            
        }
    }

    @EventHandler
    void onHotbarSwitch(PlayerItemHeldEvent ev) {
        String playerName = ev.getPlayer().getName();
        if (playerName.startsWith(".")) {
            playerName = playerName.substring(1);
        }
        
        Player player = ev.getPlayer();
        ItemStack newItem = player.getInventory().getItem(ev.getNewSlot());
        if (newItem == null) return;

        if (newItem != null && newItem.getItemMeta().getPersistentDataContainer().has(new NamespacedKey("fruit", "action"), PersistentDataType.BOOLEAN)) {
            // Display current technique using the same API as onPlayerInteract
            if (checkEaten(playerName)) {
                NamespacedKey fruitKey = null;
                // Iterate through all keys to find the correct fruitId key
                for (NamespacedKey key : newItem.getItemMeta().getPersistentDataContainer().getKeys()) {
                    if (!key.getKey().equals("action")) { // Exclude "action" key
                        fruitKey = key;
                        break;
                    }
                }
                if (fruitKey == null) return;
                String fruitId = fruitKey.getKey();

                int techIndex = PlayerTechniqueManager.getCurrentTechnique(player.getUniqueId(), fruitId);
                List<Technique> techniques = TechRegistry.getAllTechniques(fruitId);
                if (techniques == null || techniques.isEmpty() || techIndex >= techniques.size()) return;
                String currentTechnique = techniques.get(techIndex).getName();
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                        TextComponent.fromLegacy("Current technique: " + currentTechnique));
            }
        }
    }

    
    @EventHandler
    void fruitStealCheck(PlayerDeathEvent e) {
        String playerName = e.getEntity().getName();
        if (playerName.startsWith(".")) {
            playerName = playerName.substring(1);
        }
        if(e.getEntity().getKiller() == null) return;
        Player killer = e.getEntity().getKiller();
        if(!killer.getInventory().getItem(9).isSimilar(fruits.fruitStealer)) return; // Check if the killer has a fruit in their offhand
        if (checkEaten(playerName)) {
            File configFile = new File(path, "fruits.yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
            // Check if the player has eaten any fruits
            if (config.contains("fruits." + playerName + ".fruits")) {
                List<String> fruits = config.getStringList("fruits." + playerName + ".fruits");
                for (String fruit : fruits) {
                    for(ItemStack fruta : com.rschao.plugins.fightingpp.items.fruits.getAllExistingFruits()){
                        if (fruta.getItemMeta().getPersistentDataContainer().has(new NamespacedKey("fruit", fruit), PersistentDataType.BOOLEAN)) {                    
                            // Add the fruit to the killer's inventory
                            killer.getInventory().addItem(fruta);
                            killer.sendMessage(ChatColor.GREEN + "You have stolen " + fruit + " from " + playerName + "!");
                            e.getEntity().sendMessage(ChatColor.RED + "Your " + fruit + " has been stolen by " + killer.getName() + "!");
                            
                            // Remove the fruit from the dead player's config
                            fruits.remove(fruit);
                            config.set("fruits." + playerName + ".fruits", fruits);
                            
                            try {
                                config.save(configFile);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                            
                        }
                    }
                }
            }
        }
        
    }
    

    @EventHandler
    public void onLootGenerate(LootGenerateEvent event) {
        // Check if the loot table is a shipwreck chest loot table
        if (event.getLootTable().equals(LootTables.SHIPWRECK_TREASURE.getLootTable())) {
            // Get a random fruit from the fruits class
            ItemStack randomFruit = getRandomFruit();
            if (randomFruit != null) {
                // Check if the fruit has already been eaten
                int rng = com.rschao.events.events.getRNG(0, 100);
                if (rng < 5) {
                    // Add the fruit item to the loot table
                    int rn = com.rschao.events.events.getRNG(0, 26);
                    //set the item in slot rn to the fruit item
                    event.getInventoryHolder().getInventory().setItem(rn, randomFruit);
                    Bukkit.getOnlinePlayers().forEach((p) -> {
                        p.sendMessage("A Devil Fruit has appeared!");
                    });
                }
            }
        }
    }

    private ItemStack getRandomFruit() {
        List<ItemStack> fruitList = fruits.getAllFruits();
        if (fruitList.isEmpty()) {
            return null;
        }
        Random random = new Random();
        return fruitList.get(random.nextInt(fruitList.size()));
    }
    public void awakenTest(Player player, String fruitId) {
        awakening.setFruitAwakened(player.getName(), fruitId, true);
        player.sendMessage(ChatColor.GOLD + "Your " + fruitId + " fruit has awakened!");
    }

    

    public Player getClosestPlayer(Location location) {
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

    public static void DeawakenFruit(Player player, String fruitId) {
        // Use TechAPI's Awakening system
        awakening.setFruitAwakened(player.getName(), fruitId, false);
        // Persist to config
        awakening.saveAwakenedToConfig(player.getName(), fruitId, false);
        File configFile = new File(path, "fruits.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacy(ChatColor.RED + "Your fruit has lost its awakened state!"));
    }
    @EventHandler
    void onPlayerHurt(EntityDamageByEntityEvent e) {
        Entity damager = e.getDamager();
        Entity victim = e.getEntity();
        if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            Player player = (Player) damager;
            Player damaged = (Player) victim;
            if(player.getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().has(new NamespacedKey("event", "geno"), PersistentDataType.BOOLEAN)){
                e.setDamage(1000);
                damaged.sendMessage(ChatColor.DARK_RED + "You feel like you're going to have a bad time");
                player.sendMessage(ChatColor.DARK_RED + "=)");
            }
        }
    }
    ItemStack buffsword = null;
    @EventHandler
    void AnvilRecipes(PrepareAnvilEvent ev){
        ItemStack[] items = ev.getInventory().getContents();
        if(items[0] == null) return;
        if(items[0].getItemMeta().getPersistentDataContainer().has(weapons.CSKey) && items[0].getItemMeta().getPersistentDataContainer().has(weapons.DSKey) && items[1] == null) {
            ItemStack item = new ItemStack(items[0]);
            ItemMeta meta = item.getItemMeta();
            meta.setItemName(ChatColor.DARK_RED + "Sword of Chaos");
            meta.setCustomModelData(17);
            item.setItemMeta(meta);
            ev.setResult(item);
            buffsword = item;
            
        }
        
    }
    @EventHandler
    void onInventoryClick(InventoryClickEvent ev) {
        if (ev.getInventory() instanceof AnvilInventory) {
            AnvilInventory anvilInventory = (AnvilInventory) ev.getInventory();
            if (ev.getSlotType() == InventoryType.SlotType.RESULT) {
                ItemStack result = anvilInventory.getItem(2);
                if (result != null && result.isSimilar(buffsword)) {
                    Player player = (Player) ev.getWhoClicked();
                    player.getInventory().addItem(result);
                    anvilInventory.clear();
                    ev.setCancelled(true);
                }
            }
        }
    }

}