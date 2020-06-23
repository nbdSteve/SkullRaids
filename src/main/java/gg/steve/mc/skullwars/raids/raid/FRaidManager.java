package gg.steve.mc.skullwars.raids.raid;

import com.massivecraft.factions.Faction;
import gg.steve.mc.skullwars.raids.SkullRaids;
import gg.steve.mc.skullwars.raids.framework.utils.LogUtil;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FRaidManager {
    private static Map<UUID, FRaid> activeRaids;

    public static void loadRaids() {
        activeRaids = new HashMap<>();
        File dataFolder = new File(SkullRaids.getInstance().getDataFolder() + File.separator + "raid-data");
        if (dataFolder.listFiles() == null) return;
        for (File file : dataFolder.listFiles()) {
            UUID raidId = UUID.fromString(file.getName().split(".yml")[0]);
            activeRaids.put(raidId, new FRaid(raidId));
        }
        Bukkit.getScheduler().runTaskTimerAsynchronously(SkullRaids.getInstance(), () -> {
            if (activeRaids.isEmpty()) return;
            for (FRaid raid : activeRaids.values()) {
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

    public static boolean addFRaid(Faction defending, Faction attacking, Chunk origin) {
        UUID raidId = UUID.randomUUID();
        return activeRaids.put(raidId, new FRaid(raidId, defending, attacking, origin)) != null;
    }

    public static boolean removeFRaid(UUID raidId) {
        if (!activeRaids.containsKey(raidId)) return false;
        return activeRaids.remove(raidId) != null;
    }

    public static boolean isRaidActive(Faction defending) {
        for (UUID raidId : activeRaids.keySet()) {
            FRaid fRaid = activeRaids.get(raidId);
            if (fRaid.getDefending().equals(defending)) return true;
        }
        return false;
    }

    public static boolean isAttacking(Faction attacking, Faction defending, Chunk origin) {
        for (UUID raidId : activeRaids.keySet()) {
            FRaid fRaid = activeRaids.get(raidId);
            if (fRaid.getAttacking().equals(attacking) && fRaid.getDefending().equals(defending)) {
                if (fRaid.isMainFBase() && fRaid.getFBase().isChunkRegistered(origin)) return true;
                return fRaid.getClaim().isClaimChunk(origin);
            }
        }
        return false;
    }

    public static FRaid getFRaid(Faction defending, Faction attacking, Chunk origin) {
        for (UUID raidId : activeRaids.keySet()) {
            FRaid fRaid = activeRaids.get(raidId);
            if (fRaid.getAttacking().equals(attacking) && fRaid.getDefending().equals(defending)) {
                if (fRaid.isMainFBase() && fRaid.getFBase().isChunkRegistered(origin)) return fRaid;
                if (fRaid.getClaim().isClaimChunk(origin)) return fRaid;
            }
        }
        return null;
    }

    public static FRaid getFRaid(Faction defending, Chunk origin) {
        for (UUID raidId : activeRaids.keySet()) {
            FRaid fRaid = activeRaids.get(raidId);
            if (fRaid.getDefending().equals(defending)) {
                if (fRaid.isMainFBase() && fRaid.getFBase().isChunkRegistered(origin)) return fRaid;
                if (fRaid.getClaim().isClaimChunk(origin)) return fRaid;
            }
        }
        return null;
    }

    public static boolean isRaiding(Faction faction) {
        for (UUID raidId : activeRaids.keySet()) {
            FRaid fRaid = activeRaids.get(raidId);
            if (fRaid.getAttacking().equals(faction) || fRaid.getDefending().equals(faction)) return true;
        }
        return false;
    }

    public static FRaid getFRaid(Faction faction) {
        for (UUID raidId : activeRaids.keySet()) {
            FRaid fRaid = activeRaids.get(raidId);
            if (fRaid.getAttacking().equals(faction) || fRaid.getDefending().equals(faction)) return fRaid;
        }
        return null;
    }
}
