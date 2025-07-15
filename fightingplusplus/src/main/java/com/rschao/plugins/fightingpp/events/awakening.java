package com.rschao.plugins.fightingpp.events;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.rschao.plugins.fightingpp.Plugin;
import com.rschao.plugins.techapi.tech.awakening.Awakening;

public class awakening {
    public static final String path = Plugin.getPlugin(Plugin.class).getDataFolder().getAbsolutePath();

    // Use TechAPI's Awakening for runtime state
    public static void setFruitAwakened(String playerName, String fruitId, boolean awakened) {
        Awakening.setAwakened(playerName, fruitId, awakened);
        // Optionally persist to config for saving
        saveAwakenedToConfig(playerName, fruitId, awakened);
    }

    public static boolean isFruitAwakened(String playerName, String fruitId) {
        return Awakening.isAwakened(playerName, fruitId);
    }

    // Save awakened state to config for persistence
    public static void saveAwakenedToConfig(String playerName, String fruitId, boolean awakened) {
        File configFile = new File(path, "fruits.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        config.set("fruits." + playerName + ".awakened." + fruitId, awakened);
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load all awakened fruits for a player from config and update TechAPI's Awakening
    public static void loadAwakenedFromConfig(String playerName) {
        File configFile = new File(path, "fruits.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        if (config.contains("fruits." + playerName + ".awakened")) {
            for (String fruitId : config.getConfigurationSection("fruits." + playerName + ".awakened").getKeys(false)) {
                boolean awakened = config.getBoolean("fruits." + playerName + ".awakened." + fruitId, false);
                Awakening.setAwakened(playerName, fruitId, awakened);
            }
        }
    }

    // Utility: returns a list of all fruits
    public static String[] getFruits() {
        List<String> fruits = new ArrayList<>();
        fruits.add("fire");
        fruits.add("air");
        fruits.add("ice");
        fruits.add("light");
        fruits.add("dark");
        fruits.add("paper");
        fruits.add("choco");
        fruits.add("peru");
        fruits.add("fabri");
        fruits.add("delta");
        fruits.add("dario");
        fruits.add("chao");
        fruits.add("gomu");
        return fruits.toArray(new String[0]);
    }
}
