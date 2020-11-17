package gg.steve.mc.skullwars.raids.listener;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import gg.steve.mc.skullwars.raids.framework.message.GeneralMessage;
import gg.steve.mc.skullwars.raids.framework.utils.LogUtil;
import gg.steve.mc.skullwars.raids.raid.FRaid;
import gg.steve.mc.skullwars.raids.raid.FRaidManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class PlacementListener implements Listener {

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        Faction faction = FPlayers.getInstance().getByPlayer(event.getPlayer()).getFaction();
        if (faction.isWilderness() || faction.isWarZone() || faction.isSafeZone()) return;
        if (!FRaidManager.isRaidActive(faction)) return;
        FRaid raid = FRaidManager.getFRaid(faction);
        if (raid == null) {
            LogUtil.warning("Null raid used, contact dev");
            return;
        }
        if (raid.isAtMax(event.getPlayer().getUniqueId())) {
            GeneralMessage.BLOCK_MESSAGE.message(event.getPlayer());
            event.setCancelled(true);
        } else {
            raid.incrementPlayer(event.getPlayer().getUniqueId());
        }
    }
}
