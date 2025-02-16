package me.phantam.customdropmobs;

import me.phantam.customdropmobs.command.CommandManager;
import me.phantam.customdropmobs.command.MenuCommand;
import me.phantam.customdropmobs.menu.MainMenu;
import me.phantam.customdropmobs.listener.EntityListener;
import me.phantam.customdropmobs.utils.MessageUtils;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

public final class CustomDropMobs extends JavaPlugin {

    private NamespacedKey glowChanceKey;
    private FileConfiguration langConfig;
    private Map<String, String> messages = new HashMap<>();
    private boolean showDropMessages = false;
    private CommandManager commandManager;
    private EntityListener entityListener;
    public MainMenu mainMenu;
    private static CustomDropMobs instance;

    @Override
    public void onEnable() {
        instance = this;
        glowChanceKey = new NamespacedKey(this, "glow_chance");
        loadLanguageFile();

        saveConfig();
        saveDefaultConfig();

        saveResourceIfNotExists("gui/MainMenu.yml");
        saveResourceIfNotExists("lang/en_messages.yml");

        new MessageUtils(this).loadMessages();
        this.commandManager = new CommandManager(this);
        this.mainMenu = new MainMenu(this);

        getServer().getPluginManager().registerEvents(new EntityListener(this), this);

        getCommand("menu").setExecutor(new MenuCommand(this));
    }

    private void saveResourceIfNotExists(String fileName) {
        File file = new File(getDataFolder(), fileName);
        if (!file.exists()) {
            saveResource(fileName, false);
        }
    }

    @Override
    public void onDisable() {
        saveConfig();
    }

    public void loadLanguageFile() {
        String langFileName = getConfig().getString("lang", "en_messages.yml");
        File langFile = new File(getDataFolder(), "lang/" + langFileName);

        if (!langFile.exists()) {
            saveResource("lang/" + langFileName, false);
        }

        langConfig = YamlConfiguration.loadConfiguration(langFile);
        messages.clear();
        loadNestedMessages("", langConfig);
    }

    private void loadNestedMessages(String parentPath, ConfigurationSection section) {
        for (String key : section.getKeys(false)) {
            String fullPath = parentPath.isEmpty() ? key : parentPath + "." + key;
            if (section.isConfigurationSection(key)) {
                loadNestedMessages(fullPath, section.getConfigurationSection(key));
            } else {
                String message = ChatColor.translateAlternateColorCodes('&', section.getString(key, ""));
                messages.put(fullPath, message);
            }
        }
    }

    public String getMessage(String key) {
        return messages.getOrDefault(key, "§cMissing message: " + key);
    }

    public boolean isShowDropMessages() {
        return showDropMessages;
    }

    public NamespacedKey getGlowChanceKey() {
        return glowChanceKey;
    }

    public EntityListener getListener() {
        return entityListener;
    }

    public void setShowDropMessages(boolean showDropMessages) {
        this.showDropMessages = showDropMessages;
    }

    public static CustomDropMobs getInstance() {
        return instance;
    }
}