package com.wizm.bansystem.commands;

import com.wizm.bansystem.BanSystem;
import com.wizm.bansystem.data.Punishment;
import com.wizm.bansystem.data.PunishmentType;
import com.wizm.bansystem.utils.ColorUtil;
import com.wizm.bansystem.utils.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.UUID;

public class InfoCommand implements CommandExecutor {
    private final BanSystem plugin;
    public InfoCommand(BanSystem plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String name = cmd.getName().toLowerCase();
        PunishmentType type;
        String permKey;
        switch (name) {
            case "baninfo": type = PunishmentType.BAN; permKey = "baninfo"; break;
            case "muteinfo": type = PunishmentType.MUTE; permKey = "muteinfo"; break;
            case "kickinfo": type = PunishmentType.KICK; permKey = "kickinfo"; break;
            case "blacklistinfo": type = PunishmentType.BLACKLIST; permKey = "blacklistinfo"; break;
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

        List<Punishment> list = plugin.getLogManager().getAll(targetUuid, type);
        if (list.isEmpty()) {
            sender.sendMessage(ColorUtil.color("&7Nessun record per &f" + targetName));
            return true;
        }
        sender.sendMessage(ColorUtil.color("&8&m-------------------"));
        sender.sendMessage(ColorUtil.color("&c" + type.getId().toUpperCase() + " Info &7- &f" + targetName));
        for (Punishment p : list) {
            String status = p.isExpired() ? "&7(Scaduto)" : (p.active ? "&a(Attivo)" : "&7(Inattivo)");
            sender.sendMessage(ColorUtil.color("&7» &f" + TimeUtil.formatDate(p.start)
                    + " &8| &7Staff: &f" + p.staffName
                    + " &8| &7Motivo: &f" + p.reason
                    + " &8| &7Durata: &f" + TimeUtil.formatDuration(p.duration)
                    + " " + status));
        }
        sender.sendMessage(ColorUtil.color("&8&m-------------------"));
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