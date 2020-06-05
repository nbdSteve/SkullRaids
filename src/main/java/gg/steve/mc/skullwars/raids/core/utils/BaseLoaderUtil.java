package gg.steve.mc.skullwars.raids.core.utils;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import gg.steve.mc.skullwars.raids.core.FBase;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class BaseLoaderUtil {
    private List<Chunk> baseChunks;
    private Faction faction;
    private World world;
    private FBase fBase;

    public BaseLoaderUtil(World world, Faction faction, Chunk start, FBase fBase) {
        this.world = world;
        this.faction = faction;
        this.baseChunks = new ArrayList<>();
        this.baseChunks.add(start);
        this.fBase = fBase;
        loadConnectedClaims(start);
    }

    public void loadConnectedClaims(Chunk start) {
        this.baseChunks.add(start);
        this.fBase.addSpawnerChunk(start);
        checkNeighbors(new PointUtil(start.getX(), start.getZ()));
    }

    public boolean checkNeighbors(PointUtil start) {
        boolean neighborFound = false;
        for (PointUtil point : start.getNeighbors()) {
            Chunk chunk = this.world.getChunkAt(point.getX(), point.getZ());
            if (this.fBase.isChunkRegistered(chunk)) continue;
            Block block = this.world.getBlockAt(chunk.getX() * 16, 100, chunk.getZ() * 16);
            if (Board.getInstance().getFactionAt(new FLocation(block)).getId().equalsIgnoreCase(faction.getId())) {
                this.fBase.addBaseChunk(chunk);
                checkNeighbors(new PointUtil(chunk.getX(), chunk.getZ()));
                neighborFound = true;
            }
        }
        return neighborFound;
    }
}
