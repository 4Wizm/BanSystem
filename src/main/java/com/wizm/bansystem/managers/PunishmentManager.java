package com.wizm.bansystem.managers;

import com.wizm.bansystem.BanSystem;
import com.wizm.bansystem.data.Punishment;
import com.wizm.bansystem.data.PunishmentType;
import com.wizm.bansystem.utils.ColorUtil;
import com.wizm.bansystem.utils.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class PunishmentManager {

    private final BanSystem plugin;
    public PunishmentManager(BanSystem plugin) { this.plugin = plugin; }

    public Punishment getActive(UUID uuid, PunishmentType type) {
        return plugin.getLogManager().getActive(uuid, type);
    }

    public boolean isBlacklisted(UUID uuid) {
        return getActive(uuid, PunishmentType.BLACKLIST) != null;
    }

    public void apply(CommandSender staff, OfflinePlayer target, String targetName,
                      PunishmentType type, String reason, long duration) {

        UUID targetUuid = target.getUniqueId();
        if (targetUuid == null) {
            staff.sendMessage(ColorUtil.color(plugin.getConfigManager().getPrefix()
                    + "&cImpossibile determinare l'utente."));
            return;
        }

        if (staff instanceof Player) {
            Player p = (Player) staff;
            if (p.getUniqueId().equals(targetUuid)) {
                staff.sendMessage(ColorUtil.color(plugin.getConfigManager().getPrefix()
                        + "&cNon puoi punire te stesso!"));
                return;
            }
        }

        if (staff instanceof Player) {
            Player onlineTarget = Bukkit.getPlayer(targetUuid);
            if (onlineTarget != null && onlineTarget.hasPermission("bansystem.staff.highstaff")) {
                staff.sendMessage(ColorUtil.color(plugin.getConfigManager().getPrefix()
                        + "&cNon puoi punire un membro dello staff superiore."));
                return;
            }
        }

        Punishment p = new Punishment();
        p.type = type;
        p.targetUuid = targetUuid;
        p.targetName = targetName;
        p.targetIp = resolveIp(targetUuid);
        p.reason = reason;
        p.start = System.currentTimeMillis();
        p.duration = duration;
        p.staffName = staff.getName();
        p.staffUuid = staff instanceof Player ? ((Player) staff).getUniqueId() : null;
        p.active = true;

        plugin.getLogManager().log(p);
        broadcast(staff, p);

        Player online = Bukkit.getPlayer(targetUuid);
        if (online != null) {
            switch (type) {
                case BAN:
                    kickWithScreen(online, buildScreen("ban", p));
                    break;
                case BLACKLIST:
                    kickWithScreen(online, buildScreen("blacklist", p));
                    break;
                case KICK:
                    kickWithScreen(online, buildScreen("kick", p));
                    break;
                case MUTE:
                    for (String line : buildScreenLines("mute", p)) {
                        online.sendMessage(ColorUtil.color(line));
                    }
                    break;
            }
        }
    }

    public String buildScreen(String type, Punishment p) {
        List<String> lines = plugin.getConfigManager().getScreen(type);
        if (lines == null || lines.isEmpty()) return "&cSei stato punito!";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            if (i > 0) sb.append("\n");
            sb.append(applyPlaceholders(lines.get(i), p));
        }
        return sb.toString();
    }

    public List<String> buildScreenLines(String type, Punishment p) {
        List<String> lines = plugin.getConfigManager().getScreen(type);
        if (lines == null) return new java.util.ArrayList<>();
        java.util.List<String> out = new java.util.ArrayList<>();
        for (String line : lines) out.add(applyPlaceholders(line, p));
        return out;
    }

    private String applyPlaceholders(String line, Punishment p) {
        String dur = p.duration < 0 ? "Permanente" : TimeUtil.formatDuration(p.duration);
        return line
                .replace("%reason%", p.reason)
                .replace("%duration%", dur)
                .replace("%staff%", p.staffName)
                .replace("%target%", p.targetName)
                .replace("%date%", TimeUtil.formatDate(p.start));
    }

    private void kickWithScreen(Player player, String rawScreen) {
        String msg = ColorUtil.color(rawScreen);
        try {
            player.kickPlayer(msg);
        } catch (Throwable t) {
            try {
                Object component = Class.forName("net.kyori.adventure.text.Component")
                        .getMethod("text", String.class)
                        .invoke(null, msg);
                Player.class.getMethod("kick", component.getClass()).invoke(player, component);
            } catch (Throwable t2) {
                player.kickPlayer(msg);
            }
        }
    }

    public void broadcast(CommandSender staff, Punishment p) {
        List<String> lines = plugin.getConfigManager().getBroadcast(p.type.getId());
        if (lines == null) return;

        String perm = plugin.getConfigManager().getBroadcastPerm(p.type.getId());
        String date = TimeUtil.formatDate(p.start);
        String dur = TimeUtil.formatDuration(p.duration);

        for (String line : lines) {
            String out = ColorUtil.color(line
                    .replace("%staff%", p.staffName)
                    .replace("%target%", p.targetName)
                    .replace("%reason%", p.reason)
                    .replace("%duration%", dur)
                    .replace("%date%", date));

            for (Player online : Bukkit.getOnlinePlayers()) {
                if (online.hasPermission(perm)) online.sendMessage(out);
            }
            Bukkit.getConsoleSender().sendMessage(out);
        }
    }

    private String resolveIp(UUID uuid) {
        try {
            OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
            if (op.isOnline() && op.getPlayer() != null) {
                return op.getPlayer().getAddress().getAddress().getHostAddress();
            }
        } catch (Throwable ignored) {}
        return null;
    }

    public void removeAll(UUID uuid, PunishmentType type) {
        plugin.getLogManager().deactivateAll(uuid, type);
    }
}