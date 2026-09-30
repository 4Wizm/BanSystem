package com.wizm.bansystem.data;

public enum PunishmentType {
    BAN("ban", "bansystem.staff.use.ban"),
    KICK("kick", "bansystem.staff.use.kick"),
    MUTE("mute", "bansystem.staff.use.mute"),
    BLACKLIST("blacklist", "bansystem.staff.use.blacklist");

    private final String id;
    private final String permission;

    PunishmentType(String id, String permission) {
        this.id = id;
        this.permission = permission;
    }

    public String getId() { return id; }
    public String getPermission() { return permission; }
}