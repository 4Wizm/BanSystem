package com.wizm.bansystem.commands;

import com.wizm.bansystem.BanSystem;
import com.wizm.bansystem.data.PunishmentType;
import com.wizm.bansystem.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BlacklistCommand implements CommandExecutor {
    private final BanSystem plugin;
    public BlacklistCommand(BanSystem plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String perm = plugin.getConfigManager().getPermission("blacklist");
        if (!sender.hasPermission(perm)) return true;

        if (args.length < 2) {
            sender.sendMessage(ColorUtil.color("&cUso: /blacklist <utente> <motivo>"));
            return true;
        }

        String name = args[0];
        OfflinePlayer target = resolve(name);
        if (target == null) {
            sender.sendMessage(ColorUtil.color(plugin.getConfigManager().getPrefix()
                    + plugin.getConfigManager().getMessage("user-not-exist")));
            return true;
        }

        if (plugin.getPunishmentManager().getActive(target.getUniqueId(), PunishmentType.BLACKLIST) != null) {
            sender.sendMessage(ColorUtil.color(plugin.getConfigManager().getPrefix()
                    + plugin.getConfigManager().getMessage("user-already-punished")));
            return true;
        }

        plugin.getPunishmentManager().apply(sender, target, target.getName(), PunishmentType.BLACKLIST, args[1], -1);
        return true;
    }

    private OfflinePlayer resolve(String name) {
        if (name == null || name.isEmpty()) return null;

        org.bukkit.entity.Player online = Bukkit.getPlayerExact(name);
        if (online != null) return online;

        OfflinePlayer op = Bukkit.getOfflinePlayer(name);
        if (op != null && op.hasPlayedBefore()) return op;

        return null;
    }
}