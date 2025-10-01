package com.rschao.plugins.fightingpp.events;

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
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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

import java.io.File;
import java.io.IOException;
import java.util.*;

public class events implements Listener {
    public static Map<String, Integer> playerTechniques = new HashMap<>();
    public static Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();
    public static final String path = Plugin.getPlugin(Plugin.class).getDataFolder().getAbsolutePath();
    public static final Map<UUID, Integer> playerGroupIdIndex = new HashMap<>();

    // Nueva bandera para permitir múltiples fruitIDs por catalizador
    public static boolean allowMultipleFruitIDsPerCatalyst = false;

    // Mapa para guardar el índice de fruitID activo por jugador y por catalyst (hash del catalyst)
    private static final Map<UUID, Map<Integer, Integer>> playerActiveFruitIndex = new HashMap<>();

    public static boolean checkEaten(String playerName) {
        File configFile = new File(path, "fruits.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        return config.contains("fruits." + playerName + ".fruits");
    }

    private boolean isFruitAlreadyEaten(String fruitId) {
        File configFile = new File(path, "fruits.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        if (!config.contains("fruits")) {
            return false;
        }
        for (String playerName : config.getConfigurationSection("fruits").getKeys(false)) {
            List<String> playerFruits = config.getStringList("fruits." + playerName + ".fruits");
            if (playerFruits.contains(fruitId)) {
                // Si no se permite múltiples fruitIDs por catalizador, retorna true como antes
                if (!allowMultipleFruitIDsPerCatalyst) {
                    return true;
                }
                // Si se permite, ignora este chequeo (permite múltiples fruitIDs)
            }
        }
        return false;
    }

    public List<String> getPlayerFruits(String playerName) {
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
        if(ev.getItem() == null) return;
        if(!ev.getItem().getType().equals(Material.GOLDEN_APPLE)) return;
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
            // Solo bloquea si la bandera está desactivada (comportamiento original)
            if (isFruitAlreadyEaten(fruitId)) {
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
                playerTechniques.put(playerName + "_" + fruitId, 0);
                playerTechniques.put(playerName, 0); // Initialize technique index
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
        if (com.rschao.smp.Plugin.getPauseLives()) return;
        if (checkEaten(playerName)) {
            File configFile = new File(path, "fruits.yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
            // remove tech usage count
            try {
                config.set("fruits." + playerName + ".fruits", null);
                config.getConfigurationSection("fruits." + playerName + ".fruits").set("fruits", null);
                config.set("fruits." + playerName + ".awakened", null); // Remove awakened state
                for (String key : config.getConfigurationSection("fruits." + playerName + ".tech").getKeys(false)) {
                    config.set(key, null);
                    config.getConfigurationSection("fruits." + playerName + ".tech").set(key, null);
                }
                config.set("fruits." + playerName + ".tech", null);
            } catch (Exception ex) {
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
        // ...existing code hasta Player player = e.getPlayer();
        Player player = e.getPlayer();
        if (e.getItem() == null) return;
        if (e.getItem().getItemMeta() == null) return;

        ItemStack item = e.getItem();
        ItemMeta meta = item.getItemMeta();
        Set<NamespacedKey> keys = meta.getPersistentDataContainer().getKeys();
        if(item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey("fruit", "awaken"), PersistentDataType.BOOLEAN)){
            if(!checkEaten(player.getName())){
                player.sendMessage(ChatColor.RED + "You need to eat a fruit first!");
                return;
            }
            for(String fruitId : getPlayerFruits(player.getName())){
                if(awakening.isFruitAwakened(player.getName(), fruitId)){
                    player.sendMessage(ChatColor.RED + "Your fruit is already awakened!");
                    return;
                }
                awakening.setFruitAwakened(player.getName(), fruitId, true);
                awakening.saveAwakenedToConfig(player.getName(), fruitId, true);
                player.sendMessage(ChatColor.GOLD + "Your " + fruitId + " fruit has awakened!");
            }
            item.setAmount(item.getAmount() - 1);
            return;
        }
        if(!item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey("fruit", "action"), PersistentDataType.BOOLEAN)) return;
        // Detectar si el catalyst tiene 3 NamespacedKeys (las 2 últimas son fruitIDs)
        boolean hasMultipleFruitIDs = keys.size() == 3;
        List<NamespacedKey> keyList = new ArrayList<>(keys);

        // Si tiene 3 keys, las dos últimas son fruitIDs
        List<NamespacedKey> fruitKeys = new ArrayList<>();
        if (hasMultipleFruitIDs) {
            // Ordenar para que las dos últimas sean las fruitIDs (por consistencia)
            keyList.sort(Comparator.comparing(NamespacedKey::getKey));
            fruitKeys.add(keyList.get(1));
            fruitKeys.add(keyList.get(2));
        }

        // Alternar fruitID activo con click izquierdo + agachado
        if (hasMultipleFruitIDs && e.getAction().toString().contains("LEFT") && player.isSneaking()) {
            int catalystHash = item.hashCode();
            Map<Integer, Integer> catalystMap = playerActiveFruitIndex.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>());
            int currentIndex = catalystMap.getOrDefault(catalystHash, 0);
            int newIndex = (currentIndex + 1) % fruitKeys.size();
            catalystMap.put(catalystHash, newIndex);
            player.sendMessage(ChatColor.YELLOW + "Switched to fruit: " + fruitKeys.get(newIndex).getKey());
            e.setCancelled(true);
            return;
        }

        // Si tiene solo 2 keys, impedir cambio de fruitID
        if (keys.size() == 2 && e.getAction().toString().contains("LEFT") && player.isSneaking()) {
            player.sendMessage(ChatColor.RED + "This catalyst only supports one fruit.");
            e.setCancelled(true);
            return;
        }

        // ...existing code hasta NamespacedKey fruitKey = null;

        NamespacedKey fruitKey = null;
        if (hasMultipleFruitIDs) {
            int catalystHash = item.hashCode();
            int idx = playerActiveFruitIndex
                    .getOrDefault(player.getUniqueId(), Collections.emptyMap())
                    .getOrDefault(catalystHash, 0);
            fruitKey = fruitKeys.get(idx);
        } else {
            // Comportamiento original: busca el primer key que no sea "action"
            for (NamespacedKey key : keys) {
                if (!key.getKey().equals("action")) {
                    fruitKey = key;
                    break;
                }
            }
        }


        if (fruitKey == null) {
            player.sendMessage(ChatColor.RED + "Could not detect a valid fruit ID.");
            return;
        }

        String fruitId = fruitKey.getKey();
        //check if the player has the fruit
        if (!getPlayerFruits(player.getName()).contains(fruitId) && !player.hasPermission("fruits.all")) {
            return;
        }
        int techIndex = PlayerTechniqueManager.getCurrentTechnique(player.getUniqueId(), fruitId);
        if (e.getAction().toString().contains("LEFT")) {
            Technique technique = null;
            technique = TechRegistry.getAllTechniques(fruitId).get(techIndex);

            if (technique == null) return;
            if (technique.isUltimate() && awakening.isFruitAwakened(player.getName(), fruitId)) {
                if (CooldownManager.isOnCooldown(player, technique.getId())) {
                    long remaining = CooldownManager.getRemaining(player, technique.getId());
                    hotbarMessage.sendHotbarMessage(player, "On cooldown! Wait " + (double) remaining / 1000.0 + " seconds.");
                    return;
                }
                Bukkit.getScheduler().runTaskLater(Plugin.getPlugin(Plugin.class), () -> {
                    DeawakenFruit(player, fruitId);
                }, 1L);
                PlayerUseUltimate ultimateEvent = new PlayerUseUltimate(technique.getName(), player);
                Bukkit.getPluginManager().callEvent(ultimateEvent);
            }
            if(technique.isUltimate() && !awakening.isFruitAwakened(player.getName(), fruitId)){
                hotbarMessage.sendHotbarMessage(player, ChatColor.RED + "You need to awaken your fruit to use this technique!");
                return;
            }
            technique.use(player, e.getItem(), Technique.nullValue());

        } else if (e.getAction().toString().contains("RIGHT")) {
            if (player.isSneaking()) {
                //switch to the previous technique
                if (techIndex == 0) {
                    PlayerTechniqueManager.setCurrentTechnique(player.getUniqueId(), fruitId, TechRegistry.getAllTechniques(fruitId).size() - 1);
                } else {
                    PlayerTechniqueManager.setCurrentTechnique(player.getUniqueId(), fruitId, techIndex - 1);
                }
            } else {
                PlayerTechniqueManager.setCurrentTechnique(player.getUniqueId(), fruitId, (techIndex + 1) % TechRegistry.getAllTechniques(fruitId).size());
            }
            techIndex = PlayerTechniqueManager.getCurrentTechnique(player.getUniqueId(), fruitId);
            player.sendMessage(ChatColor.GREEN + "Switched to technique: " + TechRegistry.getAllTechniques(fruitId).get(techIndex).getName());
        }

    }

    @EventHandler
    void onHotbarSwitch(PlayerItemHeldEvent ev) {
        // ...existing code hasta ItemStack newItem = player.getInventory().getItem(ev.getNewSlot());
        Player player = ev.getPlayer();
        ItemStack newItem = player.getInventory().getItem(ev.getNewSlot());
        if (newItem == null) return;

        if (newItem != null && newItem.getItemMeta().getPersistentDataContainer().has(new NamespacedKey("fruit", "action"), PersistentDataType.BOOLEAN)) {
            // Display current technique using la misma lógica de fruitID activo
            if (checkEaten(player.getName())) {
                ItemMeta meta = newItem.getItemMeta();
                Set<NamespacedKey> keys = meta.getPersistentDataContainer().getKeys();
                boolean hasMultipleFruitIDs = keys.size() == 3;
                List<NamespacedKey> keyList = new ArrayList<>(keys);
                List<NamespacedKey> fruitKeys = new ArrayList<>();
                if (hasMultipleFruitIDs) {
                    keyList.sort(Comparator.comparing(NamespacedKey::getKey));
                    fruitKeys.add(keyList.get(1));
                    fruitKeys.add(keyList.get(2));
                }
                NamespacedKey fruitKey = null;
                if (hasMultipleFruitIDs) {
                    int catalystHash = newItem.hashCode();
                    int idx = playerActiveFruitIndex
                            .getOrDefault(player.getUniqueId(), Collections.emptyMap())
                            .getOrDefault(catalystHash, 0);
                    fruitKey = fruitKeys.get(idx);
                } else {
                    for (NamespacedKey key : keys) {
                        if (!key.getKey().equals("action")) {
                            fruitKey = key;
                            break;
                        }
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
        if (e.getEntity().getKiller() == null) return;
        if (e.getEntity().getKiller().getInventory().getItem(9) == null)
            return; // Check if the killer has an item in their offhand
        Player killer = e.getEntity().getKiller();
        if (!killer.getInventory().getItem(9).isSimilar(fruits.fruitStealer))
            return; // Check if the killer has a fruit in their offhand
        if (checkEaten(playerName)) {
            File configFile = new File(path, "fruits.yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
            // Check if the player has eaten any fruits
            if (config.contains("fruits." + playerName + ".fruits")) {
                List<String> fruits = config.getStringList("fruits." + playerName + ".fruits");
                for (String fruit : fruits) {
                    for (ItemStack fruta : com.rschao.plugins.fightingpp.items.fruits.getAllExistingFruits()) {
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
            if(player.getWorld() != location.getWorld()) continue; // Skip players in different worlds
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
            if (player.getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer().has(new NamespacedKey("event", "geno"), PersistentDataType.BOOLEAN)) {
                e.setDamage(1000);
                damaged.sendMessage(ChatColor.DARK_RED + "You feel like you're going to have a bad time");
                player.sendMessage(ChatColor.DARK_RED + "=)");
            }
        }
    }

    ItemStack result = null;

    @EventHandler
    void AnvilRecipes(PrepareAnvilEvent ev) {
        ItemStack[] items = ev.getInventory().getContents();
        if (items[0] == null || items[1] == null) return;
        if (items[0].getItemMeta().getPersistentDataContainer().has(new NamespacedKey("fruit", "action")) && items[1].getItemMeta().getPersistentDataContainer().has(new NamespacedKey("fruit", "action"))) {
            List<String> lore1 = items[0].getItemMeta().getLore();
            List<String> lore2 = items[1].getItemMeta().getLore();
            lore1.add(lore2.get(0));
            ItemStack newItem = items[0].clone();
            ItemMeta meta = newItem.getItemMeta();
            meta.setLore(lore1);
            for(NamespacedKey key : items[1].getItemMeta().getPersistentDataContainer().getKeys()){
                if(key != new NamespacedKey("fruit", "action")){
                    meta.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, true);
                }
            }
            newItem.setItemMeta(meta);
            ev.setResult(newItem);
            result = newItem;
        }

    }

    @EventHandler
    void onInventoryClick(InventoryClickEvent ev) {
        if (ev.getInventory() instanceof AnvilInventory) {
            AnvilInventory anvilInventory = (AnvilInventory) ev.getInventory();
            if (ev.getSlotType() == InventoryType.SlotType.RESULT) {
                ItemStack result = anvilInventory.getItem(2);
                if (result != null && result.isSimilar(result)) {
                    Player player = (Player) ev.getWhoClicked();
                    player.getInventory().addItem(result);
                    anvilInventory.clear();
                    ev.setCancelled(true);
                }
            }
        }
    }

}