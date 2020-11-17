package gg.steve.mc.skullwars.raids.fevent;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.event.FactionDisbandEvent;
import com.massivecraft.factions.event.LandUnclaimAllEvent;
import com.massivecraft.factions.event.LandUnclaimEvent;
import gg.steve.mc.skullwars.raids.core.FBaseManager;
import gg.steve.mc.skullwars.raids.framework.message.DebugMessage;
import gg.steve.mc.skullwars.raids.raid.FRaid;
import gg.steve.mc.skullwars.raids.raid.FRaidManager;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ClaimListener implements Listener {

    @EventHandler
    public void unclaim(LandUnclaimEvent event) {
        Faction faction = event.getFaction();
        Chunk chunk = event.getLocation().getChunk();
        if (FRaidManager.isRaidActive(faction) || FRaidManager.isRaiding(faction)) {
            event.setCancelled(true);
            DebugMessage.UNCLAIM_RAID_ACTIVE.message(event.getfPlayer().getPlayer());
            return;
        }
        if (!FBaseManager.isFBaseSet(faction)) return;
        if (FBaseManager.isSpawnerChunk(faction, chunk)) {
            event.setCancelled(true);
            DebugMessage.UNCLAIM_SPAWNER_CHUNK.message(event.getfPlayer().getPlayer());
        } else {
            FBaseManager.removeBaseChunk(faction, chunk);
        }
    }

    @EventHandler
    public void unclaimAll(LandUnclaimAllEvent event) {
        Faction faction = event.getFaction();
        if (FRaidManager.isRaidActive(faction) || FRaidManager.isRaiding(faction)) {
            event.setCancelled(true);
            DebugMessage.UNCLAIM_RAID_ACTIVE.message(event.getfPlayer().getPlayer());
            return;
        }
        if (!FBaseManager.isFBaseSet(faction)) return;
        FBaseManager.unsetFBase(faction);
    }

    @EventHandler
    public void disband(FactionDisbandEvent event) {
        Faction faction = event.getFaction();
        if (FRaidManager.isRaidActive(faction) || FRaidManager.isRaiding(faction)) {
            event.setCancelled(true);
            DebugMessage.DISBAND_RAID_ACTIVE.message(event.getPlayer());
            return;
        }
        if (!FBaseManager.isFBaseSet(faction)) return;
        FBaseManager.unsetFBase(faction);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void command(PlayerCommandPreprocessEvent event) {
        Faction faction = FPlayers.getInstance().getByPlayer(event.getPlayer()).getFaction();
        if (event.getMessage().equalsIgnoreCase("/f unclaim")) {
            if (FRaidManager.isRaidActive(faction) || FRaidManager.isRaiding(faction)) {
                event.setCancelled(true);
                DebugMessage.UNCLAIM_RAID_ACTIVE.message(event.getPlayer());
                return;
            }
        } else if (event.getMessage().equalsIgnoreCase("/f unclaimall") ) {
            if (FRaidManager.isRaidActive(faction) || FRaidManager.isRaiding(faction)) {
                event.setCancelled(true);
                DebugMessage.UNCLAIM_RAID_ACTIVE.message(event.getPlayer());
                return;
            }
        } else if (event.getMessage().equalsIgnoreCase("/f disband")) {
            if (FRaidManager.isRaidActive(faction) || FRaidManager.isRaiding(faction)) {
                event.setCancelled(true);
                DebugMessage.DISBAND_RAID_ACTIVE.message(event.getPlayer());
                return;
            }
        }
    }
}
