package gg.steve.mc.skullwars.raids.raid;

import com.massivecraft.factions.Faction;
import gg.steve.mc.skullwars.raids.SkullRaids;
import gg.steve.mc.skullwars.raids.core.FBase;
import gg.steve.mc.skullwars.raids.core.FBaseManager;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;

import java.io.File;
import java.util.*;

public class FRaidManager {
    private static Map<UUID, FRaid> activeRaids;
    private static List<Faction> protectedFactions;

    public static void loadRaids() {
        activeRaids = new HashMap<>();
        protectedFactions = new ArrayList<>();
        File dataFolder = new File(SkullRaids.getInstance().getDataFolder() + File.separator + "raid-data");
        if (dataFolder.listFiles() == null) return;
        for (File file : dataFolder.listFiles()) {
            UUID raidId = UUID.fromString(file.getName().split(".yml")[0]);
            activeRaids.put(raidId, new FRaid(raidId));
        }
        protectedFactions.addAll(FBaseManager.getProtectedFactions());
        Bukkit.getScheduler().runTaskTimerAsynchronously(SkullRaids.getInstance(), () -> {
            if (activeRaids.isEmpty() && protectedFactions.isEmpty()) return;
            List<Faction> removed = new ArrayList<>();
            for (Faction faction : protectedFactions) {
                FBase base = FBaseManager.getFBase(faction);
                if (!base.decrementProtection()) removed.add(faction);
            }
            for (Faction faction : removed) {
                protectedFactions.remove(faction);
            }
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
        if (attacking.isWilderness()) return false;
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

    public static boolean endFRaid(Faction faction) {
        if (!isRaidActive(faction)) return false;
        for (UUID raidId : activeRaids.keySet()) {
            FRaid fRaid = activeRaids.get(raidId);
            if (fRaid.getDefending().equals(faction)) fRaid.forceEnd();
        }
        return true;
    }

    public static boolean setProtected(Faction faction, int duration) {
        if (!FBaseManager.isFBaseSet(faction)) return false;
        endFRaid(faction);
        protectedFactions.add(faction);
        return FBaseManager.setProtected(faction, duration);
    }
}
