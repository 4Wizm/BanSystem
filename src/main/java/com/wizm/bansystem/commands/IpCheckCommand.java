package com.wizm.bansystem.commands;

import com.wizm.bansystem.BanSystem;
import com.wizm.bansystem.data.PunishmentType;
import com.wizm.bansystem.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class IpCheckCommand implements CommandExecutor {
    private final BanSystem plugin;
    public IpCheckCommand(BanSystem plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String name = cmd.getName().toLowerCase();
        PunishmentType type;
        String permKey;
        switch (name) {
            case "dueip": type = PunishmentType.BAN; permKey = "dueip"; break;
            case "muteip": type = PunishmentType.MUTE; permKey = "muteip"; break;
            case "kickip": type = PunishmentType.KICK; permKey = "kickip"; break;
            default: return true;
        }
        String perm = plugin.getConfigManager().getPermission(permKey);
        if (!sender.hasPermission(perm)) return true;

        if (args.length < 1) {
            sender.sendMessage(ColorUtil.color("&cUso: /" + name + " <utente>"));
            return true;
        }

        OfflinePlayer target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(args[0]);
            if (op != null && op.hasPlayedBefore()) target = op;
        }

        if (target == null) {
            sender.sendMessage(ColorUtil.color(plugin.getConfigManager().getPrefix()
                    + plugin.getConfigManager().getMessage("user-not-exist")));
            return true;
        }

        String ip = null;
        Player online = target.getPlayer();
        if (online != null) ip = online.getAddress().getAddress().getHostAddress();
        else if (plugin.getPunishmentManager().getActive(target.getUniqueId(), type) != null)
            ip = plugin.getPunishmentManager().getActive(target.getUniqueId(), type).targetIp;

        sender.sendMessage(ColorUtil.color("&8&m-------------------"));
        sender.sendMessage(ColorUtil.color("&cIP-Check &7(" + type.getId() + ") &f- " + target.getName()));

        boolean punished = plugin.getPunishmentManager().getActive(target.getUniqueId(), type) != null;
        sender.sendMessage(ColorUtil.color((punished ? "&c" : "&a") + target.getName()));

        if (ip != null) {
            Set<UUID> linked = plugin.getLogManager().findByIp(ip);
            for (UUID u : linked) {
                if (u.equals(target.getUniqueId())) continue;
                String name2 = plugin.getLogManager().getStoredName(u);
                if (name2 == null) name2 = u.toString();
                boolean p2 = plugin.getPunishmentManager().getActive(u, type) != null;
                sender.sendMessage(ColorUtil.color((p2 ? "&c" : "&a") + name2));
            }
        }
        sender.sendMessage(ColorUtil.color("&8&m-------------------"));
        return true;
    }
}