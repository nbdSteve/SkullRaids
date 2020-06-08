package gg.steve.mc.skullwars.raids.fevent;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.event.FactionDisbandEvent;
import com.massivecraft.factions.event.LandUnclaimAllEvent;
import com.massivecraft.factions.event.LandUnclaimEvent;
import gg.steve.mc.skullwars.raids.core.FBaseManager;
import gg.steve.mc.skullwars.raids.raid.FRaidManager;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ClaimListener implements Listener {

    @EventHandler
    public void unclaim(LandUnclaimEvent event) {
        Faction faction = event.getFaction();
        Chunk chunk = event.getLocation().getChunk();
        if (FRaidManager.isRaidActive(faction)) {
            event.setCancelled(true);
            faction.sendMessage("You are unable to unclaim land while you are being raided");
            return;
        }
        if (!FBaseManager.isFBaseSet(faction)) return;
        if (FBaseManager.isSpawnerChunk(faction, chunk)) {
            event.getfPlayer().sendMessage("You can not unclaim a spawner chunk.");
            event.setCancelled(true);
        } else {
            FBaseManager.removeBaseChunk(faction, chunk);
        }
    }

    @EventHandler
    public void unclaimAll(LandUnclaimAllEvent event) {
        Faction faction = event.getFaction();
        if (FRaidManager.isRaidActive(faction)) {
            event.setCancelled(true);
            faction.sendMessage("You are unable to unclaim land while you are being raided");
            return;
        }
        if (!FBaseManager.isFBaseSet(faction)) return;
        FBaseManager.unsetFBase(faction);
    }

    @EventHandler
    public void disband(FactionDisbandEvent event) {
        Faction faction = event.getFaction();
        if (FRaidManager.isRaidActive(faction)) {
            event.setCancelled(true);
            faction.sendMessage("You are unable to disband while you are being raided");
            return;
        }
        if (!FBaseManager.isFBaseSet(faction)) return;
        FBaseManager.unsetFBase(faction);
    }

    @EventHandler
    public void command(PlayerCommandPreprocessEvent event) {
        Faction faction = FPlayers.getInstance().getByPlayer(event.getPlayer()).getFaction();
        if (event.getMessage().equalsIgnoreCase("/f unclaim")) {
            if (FRaidManager.isRaidActive(faction)) {
                event.setCancelled(true);
                faction.sendMessage("You are unable to unclaim land while you are being raided.");
                return;
            }
        } else if (event.getMessage().equalsIgnoreCase("/f unclaimall")) {
            if (FRaidManager.isRaidActive(faction)) {
                event.setCancelled(true);
                faction.sendMessage("You are unable to unclaim land while you are being raided.");
                return;
            }
        } else if (event.getMessage().equalsIgnoreCase("/f disband")) {
            if (FRaidManager.isRaidActive(faction)) {
                event.setCancelled(true);
                faction.sendMessage("You are unable to disband while you are being raided.");
                return;
            }
        }
    }
}
