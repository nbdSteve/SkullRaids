package gg.steve.mc.skullwars.raids.fevent;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.event.FactionDisbandEvent;
import com.massivecraft.factions.event.LandUnclaimAllEvent;
import com.massivecraft.factions.event.LandUnclaimEvent;
import gg.steve.mc.skullwars.raids.core.FBaseManager;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ClaimListener implements Listener {

    @EventHandler
    public void unclaim(LandUnclaimEvent event) {
        Faction faction = event.getFaction();
        Chunk chunk = event.getLocation().getChunk();
        if (!FBaseManager.isFBaseSet(faction)) return;
        FBaseManager.removeBaseChunk(faction, chunk);
    }

    @EventHandler
    public void unclaimAll(LandUnclaimAllEvent event) {
        Faction faction = event.getFaction();
        if (!FBaseManager.isFBaseSet(faction)) return;
        FBaseManager.unsetFBase(faction);
    }

    @EventHandler
    public void disband(FactionDisbandEvent event) {
        Faction faction = event.getFaction();
        if (!FBaseManager.isFBaseSet(faction)) return;
        FBaseManager.unsetFBase(faction);
    }
}
