package gg.steve.mc.skullwars.raids.raid;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.shade.com.google.gson.internal.$Gson$Preconditions;
import gg.steve.mc.skullwars.raids.SkullRaids;
import gg.steve.mc.skullwars.raids.framework.utils.LogUtil;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FRaidManager {
    private static Map<Faction, FRaid> activeRaids;

    public static void loadRaids() {
        activeRaids = new HashMap<>();
        File dataFolder = new File(SkullRaids.getInstance().getDataFolder() + File.separator + "raid-data");
        if (dataFolder.listFiles() == null) return;
        for (File file : dataFolder.listFiles()) {
            Faction defending = Factions.getInstance().getFactionById(file.getName().split(".yml")[0]);
            activeRaids.put(defending, new FRaid(defending));
        }
        Bukkit.getScheduler().scheduleSyncRepeatingTask(SkullRaids.getInstance(), () -> {
//            if (activeRaids == null || activeRaids.isEmpty());
            for (FRaid raid : activeRaids.values()) {
                LogUtil.info("decrementing");
                raid.decrementRemaining();
            }
        }, 0L, 20L);
    }

    public static void shutdownRaidCache() {
        if (activeRaids == null || activeRaids.isEmpty()) return;
        for (FRaid raid : activeRaids.values()) {
            raid.saveToFile();
        }
        activeRaids.clear();
    }

    public static boolean addFRaid(Faction defending, Faction attacking) {
        if (activeRaids.containsKey(defending)) return false;
        return activeRaids.put(defending, new FRaid(defending, attacking)) != null;
    }

    public static boolean removeFRaid(Faction defending) {
        if (!activeRaids.containsKey(defending)) return false;
        return activeRaids.remove(defending) != null;
    }

    public static boolean isDefending(FPlayer player) {
        return activeRaids.containsKey(player.getFaction());
    }

    public static boolean isAttacking(FPlayer player) {
        for (FRaid raid : activeRaids.values()) {

        }
        return false;
    }

    public static boolean isRaidActive(Faction defending) {
        return activeRaids.containsKey(defending);
    }

    public static FRaid getFRaid(Faction defending) {
        return activeRaids.get(defending);
    }

    public static boolean isAttackingFaction(Faction attacking, Faction defending) {
        if (!activeRaids.containsKey(defending)) return true;
        return activeRaids.get(defending).getAttacking().equals(attacking);
    }
}
