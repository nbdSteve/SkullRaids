package gg.steve.mc.skullwars.raids.core;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import gg.steve.mc.skullwars.raids.SkullRaids;
import gg.steve.mc.skullwars.raids.framework.message.GeneralMessage;
import gg.steve.mc.skullwars.raids.framework.utils.TimeUtil;
import gg.steve.mc.skullwars.raids.framework.yml.Files;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FBaseManager {
    private static Map<Faction, FBase> fBases;
    private static int counter;

    public static void loadFactionBases() {
        fBases = new HashMap<>();
        counter = 0;
        File dataFolder = new File(SkullRaids.getInstance().getDataFolder() + File.separator + "base-data");
        if (dataFolder.listFiles() == null) return;
        for (File file : dataFolder.listFiles()) {
            Faction faction = Factions.getInstance().getFactionById(file.getName().split(".yml")[0]);
            fBases.put(faction, new FBase(faction));
        }
        Bukkit.getScheduler().scheduleSyncRepeatingTask(SkullRaids.getInstance(), () -> {
            counter++;
            if (counter >= Files.CONFIG.get().getInt("reminder-delay")) {
                counter = 0;
            } else return;
            for (Faction faction : Factions.getInstance().getAllFactions()) {
                if (faction.isWilderness() || faction.isSafeZone() || faction.isWarZone()) continue;
                if (!fBases.containsKey(faction)) GeneralMessage.FBASE_REMINDER.doFactionMessage(faction);
            }
        }, 0L, 20L);
    }

    public static void shutdown() {
        if (fBases != null && !fBases.isEmpty()) {
            for (FBase fBase : fBases.values()) {
                fBase.saveToFile();
            }
            fBases.clear();
        }
    }

    public static boolean isFBaseSet(Faction faction) {
        return fBases.containsKey(faction);
    }

    public static boolean isSpawnerChunk(Faction faction, Chunk chunk) {
        if (!fBases.containsKey(faction)) return false;
        return fBases.get(faction).isSpawnerChunk(chunk);
    }

    public static FBase getFBase(Faction faction) {
        return fBases.get(faction);
    }

    public static boolean createFBase(Faction faction, Location location) {
        if (fBases.containsKey(faction)) return false;
        return fBases.put(faction, new FBase(faction, location)) != null;
    }

    public static boolean unsetFBase(Faction faction) {
        if (!fBases.containsKey(faction)) return false;
        fBases.get(faction).getfBaseData().delete();
        return fBases.remove(faction) != null;
    }

    public static boolean removeBaseChunk(Faction faction, Chunk chunk) {
        if (!fBases.containsKey(faction)) return false;
        return fBases.get(faction).removeBaseChunk(chunk);
    }

    public static List<Faction> getProtectedFactions() {
        List<Faction> protectedFactions = new ArrayList<>();
        for (FBase base : fBases.values()) {
            if (base.isProtected()) protectedFactions.add(base.getFaction());
        }
        return protectedFactions;
    }

    public static boolean isProtected(Faction faction) {
        if (!fBases.containsKey(faction)) return false;
        return fBases.get(faction).isProtected();
    }

    public static boolean setProtected(Faction faction, int duration) {
        if (!fBases.containsKey(faction)) return false;
        GeneralMessage.FACTION_PROTECT.doFactionMessage(faction, new TimeUtil(duration).getTimeAsString());
        return fBases.get(faction).setProtected(duration);
    }
}
