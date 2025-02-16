package me.phantam.customdropmobs.menu;

import me.phantam.customdropmobs.CustomDropMobs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class MainMenu implements Listener {

    private final CustomDropMobs plugin;
    private Inventory menu;
    private final Map<Integer, List<String>> itemActions = new HashMap<>();
    private final Map<UUID, String> selectedMobs = new HashMap<>();
    private int menuSize;
    private String menuTitle;

    public MainMenu(CustomDropMobs plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        loadConfig();
    }

    public void loadConfig() {
        FileConfiguration config = plugin.getConfig();
        menuTitle = ChatColor.translateAlternateColorCodes('&', config.getString("title", "&a&lCustom Drop Mobs"));
        menuSize = config.getInt("size", 27);

        menu = Bukkit.createInventory(null, menuSize, menuTitle);
        itemActions.clear();

        ConfigurationSection itemsSection = config.getConfigurationSection("items");
        if (itemsSection == null) return;

        for (String key : itemsSection.getKeys(false)) {
            ConfigurationSection itemSection = itemsSection.getConfigurationSection(key);
            if (itemSection == null) continue;

            Material material = Material.matchMaterial(itemSection.getString("material", "BARRIER"));
            if (material == null) continue;

            int slot = itemSection.getInt("slot", 0);
            String name = ChatColor.translateAlternateColorCodes('&', itemSection.getString("name", "&cUnknown Item"));
            List<String> lore = itemSection.getStringList("lore");

            List<String> formattedLore = new ArrayList<>();
            for (String line : lore) {
                formattedLore.add(ChatColor.translateAlternateColorCodes('&', line));
            }

            List<String> actions = itemSection.getStringList("actions");
            itemActions.put(slot, actions);

            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(name);
                meta.setLore(formattedLore);
                item.setItemMeta(meta);
            }
            menu.setItem(slot, item);
        }
    }

    public void open(Player player) {
        player.openInventory(menu);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        if (!event.getView().getTitle().equals(menuTitle)) return;
        event.setCancelled(true);

        int slot = event.getSlot();
        if (!itemActions.containsKey(slot)) return;

        List<String> actions = itemActions.get(slot);
        for (String action : actions) {
            if (action.equalsIgnoreCase("select_mob")) {
                player.closeInventory();
                player.sendMessage(ChatColor.YELLOW + "Nhập tên mob vào chat:");
                selectedMobs.put(player.getUniqueId(), "PENDING");
            } else if (action.startsWith("open_menu:")) {
                String menuName = action.split(":")[1];
                if ("EditDrop".equalsIgnoreCase(menuName)) {
                    if (selectedMobs.getOrDefault(player.getUniqueId(), "PENDING").equals("PENDING")) {
                        player.sendMessage(ChatColor.RED + "Bạn cần chọn một mob trước!");
                    } else {
                        player.sendMessage(ChatColor.GREEN + "Mở menu chỉnh sửa Drop!");
                        // Thêm logic mở GUI EditDrop ở đây
                    }
                }
            }
        }
    }

    public void setSelectedMob(Player player, String mobType) {
        selectedMobs.put(player.getUniqueId(), mobType);
        updateSelectMobItem(player, mobType);
    }

    private void updateSelectMobItem(Player player, String mobType) {
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection itemSection = config.getConfigurationSection("items.select_mob");
        if (itemSection == null) return;

        int slot = itemSection.getInt("slot", 11);
        Material mobEgg = Material.matchMaterial(mobType.toUpperCase() + "_SPAWN_EGG");
        if (mobEgg == null) mobEgg = Material.PLAYER_HEAD;

        ItemStack item = new ItemStack(mobEgg);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GREEN + "Đã chọn: " + mobType);
            meta.setLore(Arrays.asList(ChatColor.GRAY + "Nhấp để chọn lại."));
            item.setItemMeta(meta);
        }
        menu.setItem(slot, item);
        player.updateInventory();
    }
}
