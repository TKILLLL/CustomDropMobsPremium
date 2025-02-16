package me.phantam.customdropmobs.command;

import me.phantam.customdropmobs.CustomDropMobs;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import me.phantam.customdropmobs.utils.MessageUtils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RemoveCommand {

    private final CustomDropMobs plugin;

    public RemoveCommand(CustomDropMobs plugin) {
        this.plugin = plugin;
    }

    public void handleRemoveCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("customdropmobs.remove")) {
            sender.sendMessage(MessageUtils.getMessage("messages.no_permission"));
        } else {
            if (args.length < 3) {
                sender.sendMessage(MessageUtils.getMessage("command.remove_usage"));
                return;
            }
            String mobToRemove = args[1].toUpperCase();
            try {
                int index = Integer.parseInt(args[2]);
                removeDrop(sender, mobToRemove, index);
            } catch (NumberFormatException e) {
                sender.sendMessage(MessageUtils.getMessage("command.provide_index"));
            }
        }
    }

    private void removeDrop(CommandSender sender, String mob, int index) {
        FileConfiguration config = plugin.getConfig();
        String dropsPath = "customdropmobs." + mob + ".drops";
        ConfigurationSection dropsSection = config.getConfigurationSection(dropsPath);

        if (dropsSection == null) {
            sender.sendMessage(MessageUtils.getMessage("error_messages.invalid_mob").replace("%mob%", mob));
            return;
        }

        List<String> keys = dropsSection.getKeys(false).stream()
                .filter(key -> key.matches("\\d+"))
                .sorted(Comparator.comparingInt(Integer::parseInt))
                .map(String::valueOf)
                .collect(Collectors.toList());
        String noDrop = MessageUtils.getMessage("command.mob_no_custom_drops").replace("%mob%", mob);
        String countDrop = MessageUtils.getMessage("command.current_drops").replace("%mob%", mob);

        if (keys.isEmpty()) {
            sender.sendMessage(noDrop);
            return;
        }

        if (index < 1 || index > keys.size()) {
            sender.sendMessage(countDrop.replace("%number%", String.valueOf(keys.size())));
            return;
        }

        String keyToRemove = keys.get(index - 1);
        dropsSection.set(keyToRemove, null);
        plugin.saveConfig();
        sender.sendMessage(MessageUtils.getMessage("messages.command_removed").replace("%mob%", mob));
    }
}
