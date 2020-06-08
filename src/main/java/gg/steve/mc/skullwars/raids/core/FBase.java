package gg.steve.mc.skullwars.raids.core;

import com.massivecraft.factions.Faction;
import gg.steve.mc.skullwars.raids.core.utils.BaseLoaderUtil;
import gg.steve.mc.skullwars.raids.core.utils.PointUtil;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FBase {
    private Faction faction;
    private List<Chunk> spawnerChunks;
    private Map<Chunk, Boolean> base;
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
        this.base = new HashMap<>();
        loadBaseClaim();
    }

    public FBase(Faction faction, Location location) {
        this.faction = faction;
        this.fBaseData = new FBaseFile(faction, location);
        this.world = location.getWorld();
        this.center = location.getChunk();
        this.spawnerChunks = convertStringToChunks();
        this.base = new HashMap<>();
        loadBaseClaim();
    }

    public void saveToFile() {
        YamlConfiguration conf = fBaseData.get();
        conf.set("base.world", this.world.getName());
        conf.set("base.chunk-x", this.center.getX());
        conf.set("base.chunk-z", this.center.getZ());
        List<String> spawner = new ArrayList<>();
        for (Chunk chunk : spawnerChunks) {
            spawner.add(chunk.getX() + ":" + chunk.getZ());
        }
        conf.set("spawner-chunks", spawner);
        fBaseData.save();
    }

    public void addBaseChunk(Chunk chunk) {
        if (this.spawnerChunks.contains(chunk)) {
            this.base.put(chunk, true);
        } else {
            this.base.put(chunk, false);
        }
    }

    public void addSpawnerChunk(Chunk chunk) {
        if (this.spawnerChunks.contains(chunk)) return;
        this.spawnerChunks.add(chunk);
        this.base.put(chunk, true);
    }

    public boolean isSpawnerChunk(Chunk chunk) {
        return this.spawnerChunks.contains(chunk);
    }

    public FBaseFile getfBaseData() {
        return fBaseData;
    }

    public boolean canBeSpawnerChunk(Chunk chunk) {
        if (isSpawnerChunk(chunk)) return true;
        PointUtil point = new PointUtil(chunk.getX(), chunk.getZ());
        for (PointUtil neighbor : point.getNeighbors()) {
            if (isSpawnerChunk(this.world.getChunkAt(neighbor.getX(), neighbor.getZ()))) return true;
        }
        return false;
    }

    public boolean isChunkRegistered(Chunk chunk) {
        return this.base.containsKey(chunk) || this.spawnerChunks.contains(chunk);
    }

    public boolean removeBaseChunk(Chunk chunk) {
        if (!this.base.containsKey(chunk)) return false;
        if (this.base.get(chunk)) this.spawnerChunks.remove(chunk);
        return this.base.remove(chunk) != null;
    }

    public int getSpawnerChunkCount() {
        return this.spawnerChunks.size();
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

    private void loadBaseClaim() {
        new BaseLoaderUtil(this.world, this.faction, this.center, this);
    }
}