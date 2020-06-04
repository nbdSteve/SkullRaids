package gg.steve.mc.skullwars.raids.core;

import com.massivecraft.factions.Faction;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class FBase {
    private Faction faction;
    private List<Chunk> spawnerChunks, baseArea;
    private Chunk center;
    private World world;
    private FBaseFile fBaseData;

    public FBase(Faction faction) {
        this.faction = faction;
        this.fBaseData = new FBaseFile(faction);
        this.world = Bukkit.getWorld(fBaseData.get().getString("base.world"));
        this.center = this.world.getChunkAt(fBaseData.get().getInt("base.chunk-x"),
                fBaseData.get().getInt("base.chunk-z"));
        this.spawnerChunks = convertStringToChunks();
    }

    public FBase(Faction faction, Location location) {
        this.faction = faction;
        this.fBaseData = new FBaseFile(faction, location);
    }

    private List<Chunk> convertStringToChunks() {
        List<Chunk> chunks = new ArrayList<>();
        for (String entry : this.fBaseData.get().getStringList("spawner-chunks")) {
            String[] parts = entry.split(":");
            int x = Integer.parseInt(parts[0]), z = Integer.parseInt(parts[1]);
            chunks.add(this.world.getChunkAt(x, z));
        }
        return chunks;
    }

    private List<Chunk> loadBaseClaim() {
        List<Chunk> chunks = new ArrayList<>();
//        FLocation.
        return chunks;
    }

    public boolean isSpawnerChunk(Chunk chunk) {
        return spawnerChunks.contains(chunk);
    }

    public FBaseFile getfBaseData() {
        return fBaseData;
    }
}
