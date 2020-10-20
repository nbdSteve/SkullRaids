package gg.steve.mc.skullwars.raids.framework.message;

import com.massivecraft.factions.Faction;
import gg.steve.mc.skullwars.raids.framework.utils.ColorUtil;
import gg.steve.mc.skullwars.raids.framework.utils.actionbarapi.ActionBarAPI;
import gg.steve.mc.skullwars.raids.framework.yml.Files;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public enum GeneralMessage {
    // plugin
    ANTI_LEACH("anti-leach"),
    ANTI_LEACH_WALL("anti-leach-wall", "{defending}"),
    GEN_BUCKET_BLOCKED("gen-bucket-blocked"),
    PHASE_1_RESET("phase-1-reset"),
    DISPENSERS_IN_CLAIMS("dispensers-in-claims"),
    ALREADY_BEING_RAIDED("already-being-raided", "{defending}", "{attacking}"),
    FBASE_REMINDER("fbase-reminder"),
    BLOCK_MESSAGE("max-placed"),
    // phases
    PHASE_1_ATTACKING("phase-1-attacking", "{defending}"),
    PHASE_1_DEFENDING("phase-1-defending", "{attacking}"),
    PHASE_2_ATTACKING("phase-2-attacking", "{defending}"),
    PHASE_2_DEFENDING("phase-2-defending", "{attacking}"),
    PHASE_3_ATTACKING("phase-3-attacking", "{defending}"),
    PHASE_3_DEFENDING("phase-3-defending", "{attacking}"),
    // misc
    RELOAD("reload"),
    HELP("help");

    private String path;
    private boolean actionBar;
    private List<String> placeholders;

    GeneralMessage(String path, String... placeholders) {
        this.path = path;
        this.placeholders = Arrays.asList(placeholders);
        this.actionBar = Files.MESSAGES.get().getBoolean(this.path + ".action-bar");
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void message(Player receiver, String... replacements) {
        List<String> data = Arrays.asList(replacements);
        if (this.actionBar) {
            for (String line : Files.MESSAGES.get().getStringList(this.path + ".text")) {
                for (int i = 0; i < this.placeholders.size(); i++) {
                    line = line.replace(this.placeholders.get(i), data.get(i));
                }
                ActionBarAPI.sendActionBar(receiver, ColorUtil.colorize(line));
            }
        } else {
            for (String line : Files.MESSAGES.get().getStringList(this.path + ".text")) {
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
            for (String line : Files.MESSAGES.get().getStringList(this.path + ".text")) {
                for (int i = 0; i < this.placeholders.size(); i++) {
                    line = line.replace(this.placeholders.get(i), data.get(i));
                }
                ActionBarAPI.sendActionBar((Player) receiver, ColorUtil.colorize(line));
            }
        } else {
            for (String line : Files.MESSAGES.get().getStringList(this.path + ".text")) {
                for (int i = 0; i < this.placeholders.size(); i++) {
                    line = line.replace(this.placeholders.get(i), data.get(i));
                }
                try {
                    ((Player) receiver).sendRawMessage(ColorUtil.colorize(line));
                } catch (Exception e) {
                }
            }
        }
    }

    public static void doMessage(Player receiver, List<String> lines) {
        for (String line : lines) {
            receiver.sendRawMessage(ColorUtil.colorize(line));
        }
    }

    public void doFactionMessage(Faction faction, String... replacements) {
        List<String> data = Arrays.asList(replacements);
        for (String line : Files.MESSAGES.get().getStringList(this.path + ".text")) {
            for (int i = 0; i < this.placeholders.size(); i++) {
                line = line.replace(this.placeholders.get(i), data.get(i));
            }
            faction.sendMessage(ColorUtil.colorize(line));
        }
    }
}