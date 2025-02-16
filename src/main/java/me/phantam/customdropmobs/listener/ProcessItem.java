package me.phantam.customdropmobs.listener;

import me.phantam.customdropmobs.CustomDropMobs;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ProcessItem {
    private final CustomDropMobs plugin;

    public ProcessItem(CustomDropMobs plugin) {
        this.plugin = plugin;
    }

    public void handleItemDrop(EntityDeathEvent event, ConfigurationSection section, String key, int chance, Player killer) {
        Material material = Material.getMaterial(key.toUpperCase());
        if (material != null) {
            int quantity = section.getInt(key, 1);
            ItemStack itemStack = new ItemStack(material, quantity);
            new GlowListener(plugin).applyGlowAndSound(itemStack, chance, killer);
            addItemToDrops(event.getDrops(), itemStack);
        }
    }

    private void addItemToDrops(List<ItemStack> drops, ItemStack itemStack) {
        for (ItemStack existing : drops) {
            if (existing.isSimilar(itemStack) && existing.getAmount() < existing.getMaxStackSize()) {
                int canAdd = existing.getMaxStackSize() - existing.getAmount();
                int add = Math.min(canAdd, itemStack.getAmount());
                existing.setAmount(existing.getAmount() + add);
                itemStack.setAmount(itemStack.getAmount() - add);
                if (itemStack.getAmount() <= 0) return;
            }
        }
        drops.add(itemStack);
    }
}
