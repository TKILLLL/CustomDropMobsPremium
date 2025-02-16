package me.phantam.customdropmobs.command;

import me.phantam.customdropmobs.utils.MessageUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import me.phantam.customdropmobs.CustomDropMobs;

public class CommandManager {
    private final CustomDropMobs plugin;

    public CommandManager(CustomDropMobs plugin) {
        this.plugin = plugin;
        registerCommands();
    }

    private void registerCommands() {
        plugin.getCommand("cdm").setExecutor((sender, command, label, args) -> {
            if (!checkPermission(sender, "customdropmobs.admin")) return true;

            switch (args[0].toLowerCase()) {
                case "reload":
                    new ReloadCommand(plugin).execute(sender);
                    break;
                case "add":
                    new AddCommand(plugin).handleAddCommand(sender, args);
                    break;
                case "list":
                    new ListCommand(plugin).handleListCommand(sender);
                    break;
                case "remove":
                    new RemoveCommand(plugin).handleRemoveCommand(sender, args);
                    break;
                case "toggle":
                    new ToggleCommand(plugin).toggleDropMessages(sender);
                    break;
                case "menu":
                    new MenuCommand(plugin).openMenu(sender);
                    break;
                default:
                    showHelp(sender);
                    break;
            }
            return true;
        });
    }

    private boolean checkPermission(CommandSender sender, String permission) {
        if (sender.hasPermission(permission)) return true;
        sender.sendMessage(MessageUtils.getMessage("messages.no_permission"));
        return false;
    }

    private void showHelp(CommandSender sender) {
        String[] helpLines = plugin.getMessage(MessageUtils.getMessage("messages.help_message")).split("\n");
        for (String line : helpLines) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', line));
        }
    }
}