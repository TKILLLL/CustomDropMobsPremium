package me.phantam.customdropmobs.listener;

import me.phantam.customdropmobs.CustomDropMobs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class GlowListener {
    private final CustomDropMobs plugin;

    public GlowListener(CustomDropMobs plugin) {
        this.plugin = plugin;
    }


    public void applyGlowAndSound(ItemStack itemStack, int chance, Player player) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return;

        // Lưu tỉ lệ glow vào PersistentData của item (dùng để xác định màu glow sau khi spawn item)
        meta.getPersistentDataContainer().set(plugin.getGlowChanceKey(), PersistentDataType.INTEGER, chance);

        if (chance >= 1 && chance <= 20 && player != null) {
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        }

        itemStack.setItemMeta(meta);
    }

    /**
     * Áp dụng hiệu ứng glow cho Entity bằng cách thêm nó vào một team trên Scoreboard.
     */
    public void applyGlow(Entity entity, ChatColor color) {
        if (!color.isColor()) return;

        // Lấy Scoreboard chính
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        // Kiểm tra nếu team glow đã tồn tại; nếu chưa, tạo mới với tên duy nhất dựa trên màu
        Team team = scoreboard.getTeam("glow_" + color.name());
        if (team == null) {
            team = scoreboard.registerNewTeam("glow_" + color.name());
            team.setColor(color);
        }

        // Thêm entity vào team để kích hoạt glow
        team.addEntry(entity.getUniqueId().toString());
        entity.setGlowing(true);

        // (Tùy chọn) Lưu thông tin glow vào PersistentDataContainer của entity, nếu cần dùng cho mục đích khác
        entity.getPersistentDataContainer().set(plugin.getGlowChanceKey(), PersistentDataType.STRING, color.name());
    }
}
