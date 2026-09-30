package com.wizm.bansystem;

import com.wizm.bansystem.commands.*;
import com.wizm.bansystem.config.ConfigManager;
import com.wizm.bansystem.listeners.ChatListener;
import com.wizm.bansystem.listeners.PlayerListener;
import com.wizm.bansystem.managers.LogManager;
import com.wizm.bansystem.managers.PunishmentManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class BanSystem extends JavaPlugin {

    private static BanSystem instance;
    private ConfigManager configManager;
    private LogManager logManager;
    private PunishmentManager punishmentManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        this.configManager = new ConfigManager(this);
        this.logManager = new LogManager(this);
        this.punishmentManager = new PunishmentManager(this);

        logManager.load();

        getCommand("ban").setExecutor(new BanCommand(this));
        getCommand("kick").setExecutor(new KickCommand(this));
        getCommand("mute").setExecutor(new MuteCommand(this));
        getCommand("blacklist").setExecutor(new BlacklistCommand(this));

        UnpunishCommand unpunish = new UnpunishCommand(this);
        getCommand("unban").setExecutor(unpunish);
        getCommand("unmute").setExecutor(unpunish);
        getCommand("unkick").setExecutor(unpunish);
        getCommand("unblacklist").setExecutor(unpunish);

        InfoCommand info = new InfoCommand(this);
        getCommand("baninfo").setExecutor(info);
        getCommand("muteinfo").setExecutor(info);
        getCommand("kickinfo").setExecutor(info);
        getCommand("blacklistinfo").setExecutor(info);

        IpCheckCommand ipCheck = new IpCheckCommand(this);
        getCommand("dueip").setExecutor(ipCheck);
        getCommand("muteip").setExecutor(ipCheck);
        getCommand("kickip").setExecutor(ipCheck);

        getCommand("bansystem").setExecutor(new BanSystemCommand(this));

        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ChatListener(this), this);

        getLogger().info("BanSystem abilitato! Proxy mode: " + configManager.isProxyEnabled());
    }

    @Override
    public void onDisable() {
        if (logManager != null) logManager.save();
        getLogger().info("BanSystem disabilitato.");
    }

    public static BanSystem getInstance() { return instance; }
    public ConfigManager getConfigManager() { return configManager; }
    public LogManager getLogManager() { return logManager; }
    public PunishmentManager getPunishmentManager() { return punishmentManager; }
}