package me.phantam.customdropmobs.command;

import me.phantam.customdropmobs.CustomDropMobs;
import me.phantam.customdropmobs.utils.MessageUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Set;

public class ListCommand {
    private final CustomDropMobs plugin;

    public ListCommand(CustomDropMobs plugin) {
        this.plugin = plugin;
    }

    public void handleListCommand(CommandSender sender) {
        if (!sender.hasPermission("customdropmobs.reload")) {
            sender.sendMessage(MessageUtils.getMessage("messages.no_permission"));
        } else {
            FileConfiguration config = plugin.getConfig();
            if (config.contains("customdropmobs")) {
                ConfigurationSection mobsSection = config.getConfigurationSection("customdropmobs");
                if (mobsSection != null) {
                    Set<String> mobKeys = mobsSection.getKeys(false);
                    String mobCount = MessageUtils.getMessage("command.mob_count").replace("%mob_count%", String.valueOf(mobKeys.size()));
                    sender.sendMessage(mobCount);
                    mobKeys.forEach(mob -> sender.sendMessage(ChatColor.GRAY + "- " + mob));
                }
            } else {
                sender.sendMessage(MessageUtils.getMessage("command.no_custom_drops"));
            }
        }
    }
}
