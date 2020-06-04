package gg.steve.mc.skullwars.raids.core;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import gg.steve.mc.skullwars.raids.SkullRaids;
import org.bukkit.Chunk;
import org.bukkit.Location;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FBaseManager {
    private static Map<Faction, FBase> fBases;

    public static void loadFactionBases() {
        fBases = new HashMap<>();
        File dataFolder = new File(SkullRaids.getInstance().getDataFolder() + File.separator + "base-data");
        if (dataFolder.listFiles() == null) return;
        for (File file : dataFolder.listFiles()) {
            Faction faction = Factions.getInstance().getFactionById(file.getName().split(".yml")[0]);
            fBases.put(faction, new FBase(faction));
        }
    }

    public static void shutdown() {
        if (fBases != null && !fBases.isEmpty()) fBases.clear();
    }

    public static boolean isFBaseSet(Faction faction) {
        return fBases.containsKey(faction);
    }

    public static boolean isSpawnerChunk(Faction faction, Chunk chunk) {
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
}
