package gg.steve.mc.skullwars.raids.player;

import gg.steve.mc.skullwars.raids.SkullRaids;
import gg.steve.mc.skullwars.raids.framework.yml.Files;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerBlockManager {
    private static Map<UUID, Integer> players;
    private static int capacity;

    public static void init() {
        players = new HashMap<>();
        capacity = Files.CONFIG.get().getInt("raid-blocks-per-minute");
        Bukkit.getScheduler().scheduleSyncRepeatingTask(SkullRaids.getInstance(), () -> {
            players.clear();
        }, 0L, 1800L);
    }

    public static void shutdown() {
        if (players!=null && !players.isEmpty())players.clear();
    }

    public static boolean isAdded(UUID playerId) {
        return players.containsKey(playerId);
    }

    public static void addPlayer(UUID playerId) {
        players.put(playerId, 0);
    }

    public static void incrementPlayer(UUID playerId) {
        if (!players.containsKey(playerId)) addPlayer(playerId);
        players.put(playerId, players.get(playerId) + 1);
    }

    public static boolean isAtMax(UUID playerId) {
        if (!players.containsKey(playerId)) return false;
        return players.get(playerId) >= capacity;
    }
}
