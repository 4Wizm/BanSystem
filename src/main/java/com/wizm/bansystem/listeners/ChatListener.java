package com.wizm.bansystem.listeners;

import com.wizm.bansystem.BanSystem;
import com.wizm.bansystem.data.Punishment;
import com.wizm.bansystem.data.PunishmentType;
import com.wizm.bansystem.utils.ColorUtil;
import com.wizm.bansystem.utils.TimeUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;

public class ChatListener implements Listener {
    private final BanSystem plugin;
    public ChatListener(BanSystem plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent e) {
        Punishment mute = plugin.getPunishmentManager().getActive(
                e.getPlayer().getUniqueId(), PunishmentType.MUTE);
        if (mute == null) mute = plugin.getLogManager().getActiveByName(
                e.getPlayer().getName(), PunishmentType.MUTE);
        if (mute == null) return;

        e.setCancelled(true);

        List<String> lines = plugin.getConfigManager().getMuteNotify();
        String dur = TimeUtil.formatDuration(mute.getRemaining());

        if (lines == null || lines.isEmpty()) {
            e.getPlayer().sendMessage(ColorUtil.color("&cSei mutato! Motivo: &f" + mute.reason
                    + " &7| Tempo rimanente: &f" + dur));
            return;
        }

        for (String line : lines) {
            e.getPlayer().sendMessage(ColorUtil.color(line
                    .replace("%reason%", mute.reason)
                    .replace("%duration%", dur)
                    .replace("%staff%", mute.staffName)));
        }
    }
}