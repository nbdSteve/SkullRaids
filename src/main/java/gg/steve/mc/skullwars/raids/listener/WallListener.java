package gg.steve.mc.skullwars.raids.listener;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import gg.steve.mc.skullwars.raids.framework.message.GeneralMessage;
import gg.steve.mc.skullwars.raids.raid.FRaid;
import gg.steve.mc.skullwars.raids.raid.FRaidManager;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class WallListener implements Listener {

    @EventHandler
    public void move(PlayerMoveEvent event) {
        Chunk chunk = event.getTo().getChunk();
        Faction defending = Board.getInstance().getFactionAt(new FLocation(event.getTo()));
        Faction attacking = FPlayers.getInstance().getByPlayer(event.getPlayer()).getFaction();
        if (!attacking.isWilderness() && !attacking.isWarZone() && !attacking.isSafeZone() && defending.equals(attacking))
            return;
//        if (defending.equals(attacking)) return;
        if (FRaidManager.isRaidActive(defending)
                && !FRaidManager.isAttacking(attacking, defending, chunk)
                && FRaidManager.getFRaid(defending, chunk).isAntiLeach()) {
            FRaid raid = FRaidManager.getFRaid(defending, chunk);
            if (raid != null && raid.isLeachPlayer(event.getPlayer())) return;
            GeneralMessage.ANTI_LEACH_WALL.message(event.getPlayer(), defending.getTag());
            event.setCancelled(true);
        }
    }
}