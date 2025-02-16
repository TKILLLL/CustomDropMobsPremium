package me.phantam.customdropmobs.command;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import me.phantam.customdropmobs.CustomDropMobs;
import me.phantam.customdropmobs.utils.MessageUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class AddCommand {
    private final CustomDropMobs plugin;

    public AddCommand(CustomDropMobs plugin) {
        this.plugin = plugin;
    }

    public void handleAddCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("customdropmobs.add")) {
            sender.sendMessage(MessageUtils.getMessage("messages.no_permission"));
        } else {
            if (args.length < 4) {
                sender.sendMessage(MessageUtils.getMessage("command.add_usage"));
                return;
            }

            String mob = args[1].toUpperCase();
            String chanceKey = args[2];
            String itemId = args[3].toUpperCase();

            if (itemId.equals("COMMAND")) {
                handleCommandAdd(sender, args, mob, chanceKey);
            } else {
                handleItemAdd(sender, args, mob, chanceKey, itemId);
            }
        }
    }

    private void handleCommandAdd(CommandSender sender, String[] args, String mob, String chanceKey) {
        if (args.length < 5) {
            sender.sendMessage(MessageUtils.getMessage("command.provide_command"));
            return;
        }
        StringBuilder commandBuilder = new StringBuilder();
        for (int i = 4; i < args.length; i++) {
            commandBuilder.append(args[i]).append(" ");
        }
        String commandString = commandBuilder.toString().trim();
        addDrop(sender, mob, chanceKey, "COMMAND", commandString);
    }

    private void handleItemAdd(CommandSender sender, String[] args, String mob, String chanceKey, String itemId) {
        if (args.length < 5) {
            sender.sendMessage(MessageUtils.getMessage("command.provide_quantity"));
            return;
        }
        try {
            int amount = Integer.parseInt(args[4]);
            addDrop(sender, mob, chanceKey, itemId, amount);
        } catch (NumberFormatException e) {
            sender.sendMessage(MessageUtils.getMessage("command.provide_index"));
        }
    }

    private void addDrop(CommandSender sender, String mob, String chanceKey, String itemId, Object value) {
        if (!chanceKey.matches("\\d+")) {
            sender.sendMessage(MessageUtils.getMessage("error_messages.invalid_drop_format"));
            return;
        }

        int chance = Integer.parseInt(chanceKey);
        if (chance < 1 || chance > 100) {
            sender.sendMessage(MessageUtils.getMessage("error_messages.invalid_drop_format"));
            return;
        }

        if (Material.getMaterial(itemId) == null && !itemId.equals("COMMAND")) {
            String errorMsg = MessageUtils.getMessage("error_messages.invalid_drop_format").replace("%item%", itemId);
            sender.sendMessage(errorMsg);
            return;
        }

        FileConfiguration config = plugin.getConfig();
        String basePath = "customdropmobs." + mob;
        ConfigurationSection mobSection = config.getConfigurationSection(basePath);
        if (mobSection == null) {
            mobSection = config.createSection(basePath);
        }

        ConfigurationSection dropsSection = mobSection.getConfigurationSection("drops");
        if (dropsSection == null) {
            dropsSection = mobSection.createSection("drops");
        }

        ConfigurationSection chanceSection = dropsSection.getConfigurationSection(chanceKey);
        if (chanceSection == null) {
            chanceSection = dropsSection.createSection(chanceKey);
        }

        chanceSection.set(itemId, value);
        plugin.saveConfig();
        sender.sendMessage(MessageUtils.getMessage("messages.command_added").replace("%mob%", mob));
    }
}