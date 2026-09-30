package com.wizm.bansystem.commands;

import com.wizm.bansystem.BanSystem;
import com.wizm.bansystem.utils.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BanSystemCommand implements CommandExecutor {
    private final BanSystem plugin;
    public BanSystemCommand(BanSystem plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("reload")) {
            String perm = plugin.getConfigManager().getPermission("reload");
            if (!sender.hasPermission(perm)) return true;

            plugin.getConfigManager().reload();
            plugin.getLogManager().reload();
            sender.sendMessage(ColorUtil.color("&aConfig ricaricata!"));
            return true;
        }
        if (!sender.hasPermission(plugin.getConfigManager().getPermission("reload"))) return true;

        sender.sendMessage(ColorUtil.color("&8&m-------------------"));
        sender.sendMessage(ColorUtil.color("&cBanSystem &7by &f4Wizm"));
        sender.sendMessage(ColorUtil.color("&7/bansystem reload"));
        sender.sendMessage(ColorUtil.color("&8&m-------------------"));
        return true;
    }
}