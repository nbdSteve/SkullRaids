package gg.steve.mc.skullwars.raids.core.utils;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import gg.steve.mc.skullwars.raids.core.BaseClaim;
import gg.steve.mc.skullwars.raids.core.FBase;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;

public class BaseLoaderUtil {
    private Faction faction;
    private World world;
    private FBase fBase;
    private BaseClaim claim;

    public BaseLoaderUtil(World world, Faction faction, Chunk start, FBase fBase) {
        this.world = world;
        this.faction = faction;
        this.fBase = fBase;
        loadConnectedClaims(start);
    }

    public BaseLoaderUtil(World world, Faction faction, Chunk start, BaseClaim claim) {
        this.world = world;
        this.faction = faction;
        this.claim = claim;
        loadConnectedClaimsRegular(start);
    }

    public void loadConnectedClaims(Chunk start) {
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

    public void loadConnectedClaimsRegular(Chunk start) {
        this.claim.addClaim(start);
        checkNeighborsRegular(new PointUtil(start.getX(), start.getZ()));
    }

    public boolean checkNeighborsRegular(PointUtil start) {
        boolean neighborFound = false;
        for (PointUtil point : start.getNeighbors()) {
            Chunk chunk = this.world.getChunkAt(point.getX(), point.getZ());
            if (this.claim.isClaimChunk(chunk)) continue;
            Block block = this.world.getBlockAt(chunk.getX() * 16, 100, chunk.getZ() * 16);
            if (Board.getInstance().getFactionAt(new FLocation(block)).getId().equalsIgnoreCase(faction.getId())) {
                this.claim.addClaim(chunk);
                checkNeighbors(new PointUtil(chunk.getX(), chunk.getZ()));
                neighborFound = true;
            }
        }
        return neighborFound;
    }
}
