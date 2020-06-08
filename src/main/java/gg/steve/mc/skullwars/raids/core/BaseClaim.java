package gg.steve.mc.skullwars.raids.core;

import com.massivecraft.factions.Faction;
import gg.steve.mc.skullwars.raids.core.utils.BaseLoaderUtil;
import org.bukkit.Chunk;

import java.util.ArrayList;
import java.util.List;

public class BaseClaim {
    private List<Chunk> claims;

    public BaseClaim(Chunk origin, Faction defending) {
        this.claims = new ArrayList<>();
        new BaseLoaderUtil(origin.getWorld(), defending, origin, this);
    }

    public void addClaim(Chunk chunk) {
        if (this.claims.contains(chunk)) return;
        this.claims.add(chunk);
    }

    public boolean isClaimChunk(Chunk chunk) {
        return claims.contains(chunk);
    }
}
