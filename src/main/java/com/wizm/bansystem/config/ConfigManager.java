package com.wizm.bansystem.config;

import com.wizm.bansystem.BanSystem;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ConfigManager {

    private final BanSystem plugin;

    public ConfigManager(BanSystem plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
    }

    public void reload() {
        plugin.reloadConfig();
    }

    public FileConfiguration cfg() {
        return plugin.getConfig();
    }

    public boolean isProxyEnabled() {
        return cfg().getBoolean("proxy.enabled", false);
    }

    public String getProxyType() {
        return cfg().getString("proxy.type", "BUNGEECORD").toUpperCase();
    }

    public String getMessage(String path) {
        return cfg().getString("messages." + path, "&cMissing message: " + path);
    }

    public String getPrefix() {
        return cfg().getString("messages.prefix", "&8[&cBanSystem&8] &r");
    }

    public String getPermission(String key) {
        return cfg().getString("permissions." + key, "bansystem.staff.use." + key);
    }

    public List<String> getBroadcast(String type) {
        return cfg().getStringList("broadcast." + type);
    }

    public List<String> getScreen(String type) {
        return cfg().getStringList("screen." + type);
    }

    public List<String> getMuteNotify() {
        return cfg().getStringList("mute-notify");
    }

    public String getBroadcastPerm(String type) {
        return cfg().getString("messages." + type + "-broadcast-perm",
                "bansystem.staff.use." + type);
    }
}