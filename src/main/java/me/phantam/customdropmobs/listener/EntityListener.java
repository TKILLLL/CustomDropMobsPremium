package me.phantam.customdropmobs.listener;

import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import me.phantam.customdropmobs.CustomDropMobs;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.Random;

public class EntityListener implements Listener {
    private final CustomDropMobs plugin;
    private FileConfiguration config;
    private final Random random;

    public EntityListener(CustomDropMobs plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.random = new Random();
    }

    public void updateConfig() {
        this.config = plugin.getConfig();
    }


    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        String entityType = entity.getType().name().toUpperCase();
        ConfigurationSection mobConfig = config.getConfigurationSection("customdropmobs." + entityType + ".drops");

        if (mobConfig != null) {
            mobConfig.getKeys(false).stream()
                    .filter(key -> key.matches("\\d+"))
                    .mapToInt(Integer::parseInt)
                    .sorted()
                    .forEachOrdered(chance -> {
                        if (random.nextInt(100) < chance) {
                            ConfigurationSection chanceSection = mobConfig.getConfigurationSection(String.valueOf(chance));
                            if (chanceSection != null) {
                                processDrops(event, chanceSection, chance);
                            }
                        }
                    });
        }
    }

    private void processDrops(EntityDeathEvent event, ConfigurationSection section, int chance) {
        Player killer = event.getEntity().getKiller();
        for (String key : section.getKeys(false)) {
            if (key.equalsIgnoreCase("COMMAND")) {
                ProcessCommand processCommand = new ProcessCommand(plugin);
                processCommand.handleCommandDrop(section, key, killer);
            } else {
                ProcessItem processItem = new ProcessItem(plugin);
                processItem.handleItemDrop(event, section, key, chance, killer);
            }
        }
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (!(event.getEntity() instanceof Item)) return;
        final Item itemEntity = (Item) event.getEntity();
        ItemStack itemStack = itemEntity.getItemStack();
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        final Integer chance = pdc.get(plugin.getGlowChanceKey(), PersistentDataType.INTEGER);
        if (chance == null) return;

        // Áp dụng hiệu ứng glow cho item, bất kể trạng thái vật lý lúc spawn
        if (chance >= 1 && chance <= 20) {
            new GlowListener(plugin).applyGlow(itemEntity, ChatColor.RED);
        } else if (chance >= 21 && chance <= 40) {
            new GlowListener(plugin).applyGlow(itemEntity, ChatColor.LIGHT_PURPLE);
        }

        // Xóa dữ liệu tạm lưu glow chance khỏi item meta
        pdc.remove(plugin.getGlowChanceKey());
        itemStack.setItemMeta(meta);
        itemEntity.setItemStack(itemStack);

        // Lặp lại nhiệm vụ spawn Particle khi item đã chạm đất
        new BukkitRunnable() {
            @Override
            public void run() {
                // Nếu item đã bị lấy đi hoặc không hợp lệ, hủy nhiệm vụ
                if (!itemEntity.isValid() || itemEntity.isDead()) {
                    cancel();
                    return;
                }
                // Nếu item đã chạm đất, hiển thị Particle
                if (itemEntity.isOnGround()) {
                    Location location = itemEntity.getLocation();
                    World world = location.getWorld();
                    if (world != null) {
                        if (chance >= 1 && chance <= 20) {
                            world.spawnParticle(Particle.REDSTONE, location, 10, 0.5, 0.5, 0.5, 0.1,
                                    new Particle.DustOptions(Color.RED, 1));
                        } else if (chance >= 21 && chance <= 40) {
                            world.spawnParticle(Particle.REDSTONE, location, 10, 0.5, 0.5, 0.5, 0.1,
                                    new Particle.DustOptions(Color.PURPLE, 1));
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L); // chạy mỗi 20 ticks (~1 giây)
    }
}