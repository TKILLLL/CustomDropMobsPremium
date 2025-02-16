package me.phantam.customdropmobs.command;

import org.bukkit.command.CommandSender;
import me.phantam.customdropmobs.CustomDropMobs;
import me.phantam.customdropmobs.utils.MessageUtils;

public class ReloadCommand {
    private final CustomDropMobs plugin;

    public ReloadCommand(CustomDropMobs plugin) {
        this.plugin = plugin;
    }

    public void execute(CommandSender sender) {
        if (!sender.hasPermission("customdropmobs.reload")) {
            sender.sendMessage(MessageUtils.getMessage("messages.no_permission"));
        } else {
            try {
                plugin.reloadConfig();
                plugin.saveDefaultConfig();
                plugin.loadLanguageFile();
                plugin.getListener().updateConfig();
                sender.sendMessage(MessageUtils.getMessage("messages.reload_success"));
            } catch (Exception e) {
                sender.sendMessage(MessageUtils.getMessage("messages.reload_failure"));
            }
        }
    }

}