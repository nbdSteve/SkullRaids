package gg.steve.mc.skullwars.raids.listener;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import gg.steve.mc.skullwars.raids.core.FBaseManager;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.SpawnerSpawnEvent;

public class SpawnerListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void spawn(SpawnerSpawnEvent event) {
        Chunk chunk = event.getSpawner().getChunk();
        Faction faction = Board.getInstance().getFactionAt(new FLocation(event.getSpawner().getLocation()));
        if (!FBaseManager.isFBaseSet(faction) || !FBaseManager.isSpawnerChunk(faction, chunk))
            event.setCancelled(true);
    }
}
