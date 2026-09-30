package com.wizm.bansystem.managers;

import com.wizm.bansystem.BanSystem;
import com.wizm.bansystem.data.Punishment;
import com.wizm.bansystem.data.PunishmentType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class LogManager {

    private final BanSystem plugin;
    private final File logFile;
    private FileConfiguration logConfig;

    public LogManager(BanSystem plugin) {
        this.plugin = plugin;
        this.logFile = new File(plugin.getDataFolder(), "logs.yml");
    }

    public void load() {
        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
        if (!logFile.exists()) {
            try { logFile.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
        logConfig = YamlConfiguration.loadConfiguration(logFile);
    }

    public void save() {
        if (logConfig == null) return;
        try { logConfig.save(logFile); } catch (IOException e) { e.printStackTrace(); }
    }

    public void reload() { load(); }

    @SuppressWarnings("unchecked")
    public void log(Punishment p) {
        String base = "punishments." + p.targetUuid + "." + p.type.getId();
        List<Map<String, Object>> entries = (List<Map<String, Object>>) logConfig.getList(base);
        if (entries == null) entries = new ArrayList<>();
        Map<String, Object> entry = new LinkedHashMap<>();
        entry.put("reason", p.reason);
        entry.put("start", p.start);
        entry.put("duration", p.duration);
        entry.put("staff", p.staffName);
        entry.put("staff-uuid", p.staffUuid == null ? null : p.staffUuid.toString());
        entry.put("target-name", p.targetName);
        entry.put("target-ip", p.targetIp);
        entry.put("active", p.active);
        entries.add(entry);
        logConfig.set(base, entries);
        save();
    }

    public Punishment getActive(UUID uuid, PunishmentType type) {
        String base = "punishments." + uuid + "." + type.getId();
        List<Map<?, ?>> entries = logConfig.getMapList(base);
        if (entries == null) return null;
        for (int i = entries.size() - 1; i >= 0; i--) {
            Map<?, ?> m = entries.get(i);
            Boolean active = (Boolean) m.get("active");
            if (active == null || !active) continue;
            Punishment p = fromMap(uuid, type, m);
            if (p.isExpired()) { markInactive(uuid, type, i); continue; }
            return p;
        }
        return null;
    }

    public Punishment getActiveByName(String name, PunishmentType type) {
        if (name == null || name.isEmpty()) return null;
        ConfigurationSection sec = logConfig.getConfigurationSection("punishments");
        if (sec == null) return null;
        for (String key : sec.getKeys(false)) {
            UUID u;
            try { u = UUID.fromString(key); } catch (Exception e) { continue; }
            String stored = getStoredName(u);
            if (stored != null && stored.equalsIgnoreCase(name)) {
                Punishment p = getActive(u, type);
                if (p != null) return p;
            }
        }
        return null;
    }

    public UUID findUuidByName(String name) {
        if (name == null || name.isEmpty()) return null;
        ConfigurationSection sec = logConfig.getConfigurationSection("punishments");
        if (sec == null) return null;
        for (String key : sec.getKeys(false)) {
            UUID u;
            try { u = UUID.fromString(key); } catch (Exception e) { continue; }
            String stored = getStoredName(u);
            if (stored != null && stored.equalsIgnoreCase(name)) return u;
        }
        return null;
    }

    private void markInactive(UUID uuid, PunishmentType type, int index) {
        String base = "punishments." + uuid + "." + type.getId();
        List<Map<?, ?>> entries = logConfig.getMapList(base);
        if (entries == null || index >= entries.size()) return;
        List<Map<String, Object>> newList = new ArrayList<>();
        for (int i = 0; i < entries.size(); i++) {
            Map<String, Object> mm = new LinkedHashMap<>();
            for (Map.Entry<?, ?> e : entries.get(i).entrySet())
                mm.put(e.getKey().toString(), e.getValue());
            if (i == index) mm.put("active", false);
            newList.add(mm);
        }
        logConfig.set(base, newList);
        save();
    }

    public void deactivateAll(UUID uuid, PunishmentType type) {
        String base = "punishments." + uuid + "." + type.getId();
        List<Map<?, ?>> entries = logConfig.getMapList(base);
        if (entries == null) return;
        List<Map<String, Object>> newList = new ArrayList<>();
        for (Map<?, ?> e : entries) {
            Map<String, Object> m = new LinkedHashMap<>();
            for (Map.Entry<?, ?> en : e.entrySet())
                m.put(en.getKey().toString(), en.getValue());
            m.put("active", false);
            newList.add(m);
        }
        logConfig.set(base, newList);
        save();
    }

    public List<Punishment> getAll(UUID uuid, PunishmentType type) {
        List<Punishment> out = new ArrayList<>();
        String base = "punishments." + uuid + "." + type.getId();
        List<Map<?, ?>> entries = logConfig.getMapList(base);
        if (entries == null) return out;
        for (Map<?, ?> m : entries) out.add(fromMap(uuid, type, m));
        return out;
    }

    public Set<UUID> getAllUUIDs() {
        ConfigurationSection sec = logConfig.getConfigurationSection("punishments");
        Set<UUID> set = new HashSet<>();
        if (sec == null) return set;
        for (String k : sec.getKeys(false)) {
            try { set.add(UUID.fromString(k)); } catch (IllegalArgumentException ignored) {}
        }
        return set;
    }

    public Set<UUID> findByIp(String ip) {
        Set<UUID> result = new HashSet<>();
        if (ip == null) return result;
        for (UUID u : getAllUUIDs()) {
            ConfigurationSection sec = logConfig.getConfigurationSection("punishments." + u);
            if (sec == null) continue;
            for (String type : sec.getKeys(false)) {
                List<Map<?, ?>> entries = logConfig.getMapList("punishments." + u + "." + type);
                for (Map<?, ?> m : entries) {
                    Object tip = m.get("target-ip");
                    if (tip != null && tip.toString().equalsIgnoreCase(ip)) result.add(u);
                }
            }
        }
        return result;
    }

    public String getStoredName(UUID uuid) {
        ConfigurationSection sec = logConfig.getConfigurationSection("punishments." + uuid);
        if (sec == null) return null;
        for (String type : sec.getKeys(false)) {
            List<Map<?, ?>> entries = logConfig.getMapList("punishments." + uuid + "." + type);
            for (Map<?, ?> m : entries) {
                Object n = m.get("target-name");
                if (n != null) return n.toString();
            }
        }
        return null;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Punishment fromMap(UUID uuid, PunishmentType type, Map<?, ?> m) {
        Punishment p = new Punishment();
        p.type = type;
        p.targetUuid = uuid;

        Object reason = m.get("reason");
        p.reason = reason == null ? "N/A" : reason.toString();

        p.start = toLong(m.get("start"));
        p.duration = toLong(m.get("duration"));

        Object staff = m.get("staff");
        p.staffName = staff == null ? "Console" : staff.toString();

        Object su = m.get("staff-uuid");
        if (su != null) {
            try { p.staffUuid = UUID.fromString(su.toString()); } catch (Exception ignored) {}
        }

        Object tn = m.get("target-name");
        p.targetName = tn == null ? uuid.toString() : tn.toString();

        Object tip = m.get("target-ip");
        p.targetIp = tip == null ? null : tip.toString();

        Object act = m.get("active");
        p.active = act == null || (Boolean) act;

        return p;
    }

    private long toLong(Object o) {
        if (o == null) return 0L;
        if (o instanceof Number) return ((Number) o).longValue();
        try { return Long.parseLong(o.toString()); } catch (Exception e) { return 0L; }
    }
}