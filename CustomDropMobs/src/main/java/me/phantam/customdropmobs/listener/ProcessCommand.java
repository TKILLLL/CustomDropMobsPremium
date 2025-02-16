package me.phantam.customdropmobs.listener;

import me.phantam.customdropmobs.CustomDropMobs;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ProcessCommand {
    private final CustomDropMobs plugin;

    public ProcessCommand(CustomDropMobs plugin) {
        this.plugin = plugin;
    }

    public void handleCommandDrop(ConfigurationSection section, String key, Player killer) {
        String commandString = section.getString(key);
        if (commandString != null && killer != null) {
            if (commandString.toLowerCase().startsWith("give ")) {
                processGiveCommand(commandString, killer);
            } else {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandString.replace("%player%", killer.getName()));
            }
        }
    }

    private void processGiveCommand(String commandString, Player killer) {
        String[] parts = commandString.split(" ");
        if (parts.length >= 3) {
            Material mat = Material.getMaterial(parts[2].toUpperCase());
            if (mat != null) {
                int amount = parts.length >= 4 ? Integer.parseInt(parts[3]) : 1;
                ItemStack item = new ItemStack(mat, amount);
                new GlowListener(plugin).applyGlowAndSound(item, 0, killer);
                killer.getWorld().dropItemNaturally(killer.getLocation(), item);
            }
        }
    }
}
