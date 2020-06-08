package gg.steve.mc.skullwars.raids.papi;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import gg.steve.mc.skullwars.raids.framework.utils.TimeUtil;
import gg.steve.mc.skullwars.raids.framework.yml.Files;
import gg.steve.mc.skullwars.raids.raid.FRaid;
import gg.steve.mc.skullwars.raids.raid.FRaidManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SkullRaidsExpansion extends PlaceholderExpansion {
    private JavaPlugin instance;

    public SkullRaidsExpansion(JavaPlugin instance) {
        this.instance = instance;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getAuthor() {
        return instance.getDescription().getAuthors().toString();
    }

    @Override
    public String getIdentifier() {
        return "raid";
    }

    @Override
    public String getVersion() {
        return instance.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        if (!FRaidManager.isRaiding(fPlayer.getFaction()))
            return Files.CONFIG.get().getString("no-raid-active-placeholder");
        FRaid fRaid = FRaidManager.getFRaid(fPlayer.getFaction());
        if (identifier.contains("last_shot")) {
            TimeUtil time = new TimeUtil(fRaid.getTimeSinceLastShot());
            if (identifier.equalsIgnoreCase("last_shot_hours")) {
                return time.getHours();
            }
            if (identifier.equalsIgnoreCase("last_shot_minutes")) {
                return time.getMinutes();
            }
            if (identifier.equalsIgnoreCase("last_shot_seconds")) {
                return time.getSeconds();
            }
        }
        if (identifier.equalsIgnoreCase("current_phase")) {
            return String.valueOf(fRaid.getPhase().getWeight());
        }
        return "Wrong placeholder";
    }
}
