package com.wizm.bansystem.commands;

import com.wizm.bansystem.BanSystem;
import com.wizm.bansystem.data.PunishmentType;
import com.wizm.bansystem.utils.ColorUtil;
import com.wizm.bansystem.utils.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BanCommand implements CommandExecutor {
    protected final BanSystem plugin;
    public BanCommand(BanSystem plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String perm = plugin.getConfigManager().getPermission("ban");
        if (!sender.hasPermission(perm)) return true;

        if (args.length < 2) {
            sender.sendMessage(ColorUtil.color("&cUso: /ban <utente> <motivo> [durata]"));
            return true;
        }

        String name = args[0];
        OfflinePlayer target = resolve(name);
        if (target == null) {
            sender.sendMessage(ColorUtil.color(plugin.getConfigManager().getPrefix()
                    + plugin.getConfigManager().getMessage("user-not-exist")));
            return true;
        }

        if (plugin.getPunishmentManager().getActive(target.getUniqueId(), PunishmentType.BAN) != null) {
            sender.sendMessage(ColorUtil.color(plugin.getConfigManager().getPrefix()
                    + plugin.getConfigManager().getMessage("user-already-punished")));
            return true;
        }

        String reason = args[1];
        long duration = -1;
        if (args.length >= 3) duration = TimeUtil.parseDuration(args[2]);

        plugin.getPunishmentManager().apply(sender, target, target.getName(), PunishmentType.BAN, reason, duration);
        return true;
    }

    protected OfflinePlayer resolve(String name) {
        if (name == null || name.isEmpty()) return null;

        org.bukkit.entity.Player online = Bukkit.getPlayerExact(name);
        if (online != null) return online;

        OfflinePlayer op = Bukkit.getOfflinePlayer(name);
        if (op != null && op.hasPlayedBefore()) return op;

        return null;
    }
}