package me.phantam.customdropmobs.utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import me.phantam.customdropmobs.CustomDropMobs;
import java.io.File;

public class MessageUtils {
    private static YamlConfiguration messages;
    private static CustomDropMobs plugin;

    public MessageUtils(CustomDropMobs plugin) {
        MessageUtils.plugin = plugin;
    }

    public static void loadMessages() {
        File langFile = new File(plugin.getDataFolder(), "lang/en_messages.yml");
        if (!langFile.exists()) {
            plugin.saveResource("lang/en_messages.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(langFile);
    }

    public static String getMessage(String key) {
        return ChatColor.translateAlternateColorCodes('&',
                messages.getString(key, "&cMissing message: " + key));
    }

    public static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}