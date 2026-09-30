package com.wizm.bansystem.commands;

import com.wizm.bansystem.BanSystem;
import com.wizm.bansystem.data.PunishmentType;
import com.wizm.bansystem.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class UnpunishCommand implements CommandExecutor {
    private final BanSystem plugin;
    public UnpunishCommand(BanSystem plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String name = cmd.getName().toLowerCase();
        PunishmentType type;
        String permKey;
        switch (name) {
            case "unban": type = PunishmentType.BAN; permKey = "unban"; break;
            case "unmute": type = PunishmentType.MUTE; permKey = "unmute"; break;
            case "unkick": type = PunishmentType.KICK; permKey = "unkick"; break;
            case "unblacklist": type = PunishmentType.BLACKLIST; permKey = "unblacklist"; break;
            default: return true;
        }
        String perm = plugin.getConfigManager().getPermission(permKey);
        if (!sender.hasPermission(perm)) return true;

        if (args.length < 1) {
            sender.sendMessage(ColorUtil.color("&cUso: /" + name + " <utente>"));
            return true;
        }

        String targetName = args[0];
        UUID targetUuid = findUuid(targetName);

        if (targetUuid == null) {
            sender.sendMessage(ColorUtil.color(plugin.getConfigManager().getPrefix()
                    + plugin.getConfigManager().getMessage("user-not-exist")));
            return true;
        }

        if (plugin.getLogManager().getActive(targetUuid, type) == null) {
            sender.sendMessage(ColorUtil.color(plugin.getConfigManager().getPrefix()
                    + plugin.getConfigManager().getMessage("user-not-punished")));
            return true;
        }

        plugin.getPunishmentManager().removeAll(targetUuid, type);
        sender.sendMessage(ColorUtil.color(plugin.getConfigManager().getPrefix()
                + "&aRimosso " + type.getId() + " per &f" + targetName));
        return true;
    }

    private UUID findUuid(String name) {
        org.bukkit.entity.Player online = Bukkit.getPlayerExact(name);
        if (online != null) return online.getUniqueId();

        OfflinePlayer op = Bukkit.getOfflinePlayer(name);
        if (op != null && op.hasPlayedBefore()) return op.getUniqueId();

        UUID fromLogs = plugin.getLogManager().findUuidByName(name);
        if (fromLogs != null) return fromLogs;

        return null;
    }
}