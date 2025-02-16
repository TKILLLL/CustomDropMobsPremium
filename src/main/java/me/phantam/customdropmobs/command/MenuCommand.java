package me.phantam.customdropmobs.command;

import me.phantam.customdropmobs.CustomDropMobs;
import me.phantam.customdropmobs.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MenuCommand implements CommandExecutor {
    private final CustomDropMobs plugin;

    public MenuCommand(CustomDropMobs plugin) {
        this.plugin = plugin;
    }

    public void openMenu(CommandSender sender) {
        if (!sender.hasPermission("customdropmobs.menu")) {
            sender.sendMessage(MessageUtils.getMessage("messages.no_permission"));
        } else {
            if (sender instanceof Player) {
                plugin.mainMenu.open((Player) sender);
            } else {
                sender.sendMessage("Lệnh này chỉ dành cho người chơi!");
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        openMenu(sender);
        return true;
    }
}