package com.wizm.bansystem.data;

import java.util.UUID;

public class Punishment {
    public PunishmentType type;
    public UUID targetUuid;
    public String targetName;
    public String targetIp;
    public String reason;
    public long start;
    public long duration;
    public String staffName;
    public UUID staffUuid;
    public boolean active;

    public Punishment() {}

    public boolean isExpired() {
        if (duration < 0) return false;
        return System.currentTimeMillis() > start + duration;
    }

    public long getRemaining() {
        if (duration < 0) return -1;
        long r = (start + duration) - System.currentTimeMillis();
        return Math.max(0, r);
    }
}