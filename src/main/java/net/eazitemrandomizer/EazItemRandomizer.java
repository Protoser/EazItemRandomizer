package net.eazitemrandomizer;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class EazItemRandomizer extends JavaPlugin implements Listener {
    private Set<UUID> giftReceived;
    private List<Material> availableItems;
    public FileConfiguration config;
    File configFile = new File(getDataFolder(), "config.yml");

    @Override
    public void onEnable() {
        // Register events and load configuration
        getServer().getPluginManager().registerEvents(this, this);
        loadConfig();

        availableItems = new ArrayList<>();
        for (Material material : Material.values()) {
            // Include only items, not blocks
            if (material.isItem()) {
                availableItems.add(material);
            }
        }

        // Print loaded players for demonstration purposes
        getLogger().info("Players that received the gift: " + giftReceived);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (!giftReceived.contains(playerUUID)) {
            // Player is not in the config, add them
            giftReceived.add(playerUUID);
            savConfig();
            getLogger().info("Added player " + player.getName() + " to the config.");

            // Choose a random item from the list
            Material randomMaterial = availableItems.get(new Random().nextInt(availableItems.size()));

            // Give the player the random item
            ItemStack randomItem = new ItemStack(randomMaterial);
            player.getInventory().addItem(randomItem);
            player.sendMessage("Sie haben ein zufälliges Weihnachtsgeschenk erhalten");

        }
    }

    private void loadConfig() {
        if (!configFile.exists()) {
            saveDefaultConfig();
        }

        config = getConfig(); // Use the class-level config field here
        giftReceived = config.getStringList("giftReceived")
                .stream()
                .map(UUID::fromString)
                .collect(Collectors.toSet());
    }

    public void savConfig() {
        FileConfiguration config = getConfig();
        config.set("giftReceived", giftReceived.stream().map(UUID::toString).collect(Collectors.toList()));
        saveConfig();
    }
}