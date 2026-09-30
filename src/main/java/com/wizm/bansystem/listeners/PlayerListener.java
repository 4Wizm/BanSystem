package com.wizm.bansystem.listeners;

import com.wizm.bansystem.BanSystem;
import com.wizm.bansystem.data.Punishment;
import com.wizm.bansystem.data.PunishmentType;
import com.wizm.bansystem.utils.ColorUtil;
import com.wizm.bansystem.utils.TimeUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.List;
import java.util.UUID;

public class PlayerListener implements Listener {
    private final BanSystem plugin;
    public PlayerListener(BanSystem plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPreLogin(AsyncPlayerPreLoginEvent e) {
        UUID uuid = e.getUniqueId();
        String playerName = e.getName();
        String ip = e.getAddress().getHostAddress();

        Punishment bl = plugin.getPunishmentManager().getActive(uuid, PunishmentType.BLACKLIST);
        if (bl == null) bl = plugin.getLogManager().getActiveByName(playerName, PunishmentType.BLACKLIST);
        if (bl == null) {
            for (UUID linked : plugin.getLogManager().findByIp(ip)) {
                Punishment b2 = plugin.getPunishmentManager().getActive(linked, PunishmentType.BLACKLIST);
                if (b2 != null) { bl = b2; break; }
            }
        }
        if (bl != null) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED,
                    ColorUtil.color(buildScreen("blacklist", bl)));
            return;
        }

        Punishment ban = plugin.getPunishmentManager().getActive(uuid, PunishmentType.BAN);
        if (ban == null) ban = plugin.getLogManager().getActiveByName(playerName, PunishmentType.BAN);
        if (ban != null) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED,
                    ColorUtil.color(buildScreen("ban", ban)));
        }
    }

    private String buildScreen(String type, Punishment p) {
        List<String> lines = plugin.getConfigManager().getScreen(type);
        if (lines == null || lines.isEmpty()) return "&cSei stato punito!";
        String dur = p.duration < 0 ? "Permanente" : TimeUtil.formatDuration(p.duration);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            if (i > 0) sb.append("\n");
            sb.append(lines.get(i)
                    .replace("%reason%", p.reason)
                    .replace("%duration%", dur)
                    .replace("%staff%", p.staffName)
                    .replace("%target%", p.targetName)
                    .replace("%date%", TimeUtil.formatDate(p.start)));
        }
        return sb.toString();
    }
}