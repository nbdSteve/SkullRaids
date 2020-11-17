package gg.steve.mc.skullwars.raids.framework.message;

import gg.steve.mc.skullwars.raids.framework.yml.Files;
import gg.steve.mc.skullwars.raids.framework.utils.ColorUtil;
import gg.steve.mc.skullwars.raids.framework.utils.actionbarapi.ActionBarAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public enum DebugMessage {
    // plugin
    UNCLAIM_RAID_ACTIVE("unclaim-raid-active"),
    DISBAND_RAID_ACTIVE("disband-raid"),
    UNCLAIM_SPAWNER_CHUNK("unclaim-spawner-chunk"),
    NO_FACTION("no-faction"),
    NOT_LEADER("not-leader"),
    NOT_IN_TERRITORY("not-in-territory"),
    ALREADY_SPAWNER_CHUNK("already-spawner-chunk"),
    FBASE_REGISTER("fbase-register"),
    MAX_SPAWNER_CHUNKS("max-spawner-chunks"),
    NOT_CONNECTED("not-connected"),
    ADD_SPAWNER_CHUNK("add-spawner-chunk"),
    NOT_IN_CLAIM("not-in-claim"),
    NO_FBASE_SET("no-fbase-set"),
    REMOVED_FBASE("removed-fbase"),
    // misc
    INVALID_COMMAND("invalid-command"),
    INCORRECT_ARGS("incorrect-args"),
    INSUFFICIENT_PERMISSION("insufficient-permission", "{node}");

    private String path;
    private boolean actionBar;
    private List<String> placeholders;

    DebugMessage(String path, String... placeholders) {
        this.path = path;
        this.placeholders = Arrays.asList(placeholders);
        this.actionBar = Files.MESSAGES.get().getBoolean(this.path + ".action-bar");
    }

    public void message(Player receiver, String... replacements) {
        List<String> data = Arrays.asList(replacements);
        if (this.actionBar) {
            for (String line : Files.DEBUG.get().getStringList(this.path + ".text")) {
                for (int i = 0; i < this.placeholders.size(); i++) {
                    line = line.replace(this.placeholders.get(i), data.get(i));
                }
                ActionBarAPI.sendActionBar(receiver, ColorUtil.colorize(line));
            }
        } else {
            for (String line : Files.DEBUG.get().getStringList(this.path + ".text")) {
                for (int i = 0; i < this.placeholders.size(); i++) {
                    line = line.replace(this.placeholders.get(i), data.get(i));
                }
                receiver.sendRawMessage(ColorUtil.colorize(line));
            }
        }
    }

    public void message(CommandSender receiver, String... replacements) {
        List<String> data = Arrays.asList(replacements);
        if (this.actionBar && receiver instanceof Player) {
            for (String line : Files.DEBUG.get().getStringList(this.path + ".text")) {
                for (int i = 0; i < this.placeholders.size(); i++) {
                    line = line.replaceAll(this.placeholders.get(i), data.get(i));
                }
                ActionBarAPI.sendActionBar((Player) receiver, ColorUtil.colorize(line));
            }
        } else {
            for (String line : Files.DEBUG.get().getStringList(this.path + ".text")) {
                for (int i = 0; i < this.placeholders.size(); i++) {
                    line = line.replace(this.placeholders.get(i), data.get(i));
                }
//                receiver.sendRawMessage(ColorUtil.colorize(line));
            }
        }
    }
}
