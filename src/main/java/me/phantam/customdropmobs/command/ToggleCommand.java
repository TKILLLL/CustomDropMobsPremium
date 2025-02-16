package me.phantam.customdropmobs.command;

import me.phantam.customdropmobs.CustomDropMobs;
import me.phantam.customdropmobs.utils.MessageUtils;
import org.bukkit.command.CommandSender;

public class ToggleCommand {
    private final CustomDropMobs plugin;
    public ToggleCommand(CustomDropMobs plugin) {
        this.plugin = plugin;
    }

    public void toggleDropMessages(CommandSender sender) {
        if (!sender.hasPermission("customdropmobs.toggle")) {
            sender.sendMessage(MessageUtils.getMessage("messages.no_permission"));
        } else {
            boolean currentState = plugin.isShowDropMessages();
            plugin.setShowDropMessages(!currentState);
            String toggleMessage = plugin.isShowDropMessages() ? MessageUtils.getMessage("command.toogle_enabled") : MessageUtils.getMessage("command.toogle_disabled");
            sender.sendMessage(toggleMessage);
        }
    }
}
