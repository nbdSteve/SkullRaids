package gg.steve.mc.skullwars.raids.framework.permission;

import gg.steve.mc.skullwars.raids.framework.yml.Files;
import org.bukkit.command.CommandSender;

public enum PermissionNode {
    // fcmd
    SET("f-command.set"),
    UN_SET("f-command.un-set"),
    // cmd
    RELOAD("command.reload"),
    HELP("command.help"),
    END("command.end"),
    PROTECT("command.protect");

    private String path;

    PermissionNode(String path) {
        this.path = path;
    }

    public String get() {
        return Files.PERMISSIONS.get().getString(this.path);
    }

    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission(get());
    }

    public static boolean isPurchasePerms() {
        return Files.PERMISSIONS.get().getBoolean("purchase.enabled");
    }
}
