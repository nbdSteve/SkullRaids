package gg.steve.mc.skullwars.raids.listener;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import gg.steve.mc.skullwars.raids.framework.message.GeneralMessage;
import gg.steve.mc.skullwars.raids.framework.utils.ColorUtil;
import gg.steve.mc.skullwars.raids.framework.yml.Files;
import gg.steve.mc.skullwars.raids.raid.FRaid;
import gg.steve.mc.skullwars.raids.raid.FRaidManager;
import gg.steve.mc.skullwars.raids.raid.FRaidPhase;
import net.catalyst.events.TNTDispensedEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TnTListener implements Listener {
    private static Map<UUID, Faction> factionTnT;

    public static void initialise() {
        factionTnT = new HashMap<>();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void place(BlockPlaceEvent event) {
        if (event.getBlock().getType() != Material.DISPENSER) return;
        Faction faction = Board.getInstance().getFactionAt(new FLocation(event.getBlock().getLocation()));
        if (faction.isWilderness() || faction.isWarZone() || faction.isSafeZone()) {
            GeneralMessage.DISPENSERS_IN_CLAIMS.message(event.getPlayer());
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void tnt(TNTDispensedEvent event) {
        if (Bukkit.isGracePeriod()) return;
        Faction attacking = Board.getInstance().getFactionAt(new FLocation(event.getEntity().getLocation()));
        if (attacking.isWilderness() || attacking.isWarZone() || attacking.isSafeZone()) {
            event.setCancelled(true);
            return;
        }
        factionTnT.put(event.getEntity().getUniqueId(), attacking);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void explode(EntityExplodeEvent event) {
        if (Bukkit.isGracePeriod()) return;
        if (!factionTnT.containsKey(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
            return;
        }
        if (event.getEntityType() != EntityType.PRIMED_TNT) return;
        Faction defending = Board.getInstance().getFactionAt(new FLocation(event.getEntity().getLocation()));
        if (defending.isWilderness() || defending.isWarZone() || defending.isSafeZone()) return;
        Faction attacking = factionTnT.get(event.getEntity().getUniqueId());
        factionTnT.remove(event.getEntity().getUniqueId());
        if (attacking.equals(defending)) return;
        if (FRaidManager.isRaidActive(defending)) {
            if (!FRaidManager.isAttacking(attacking, defending, event.getLocation().getChunk())) {
                GeneralMessage.ALREADY_BEING_RAIDED.doFactionMessage(attacking, defending.getTag(), FRaidManager.getFRaid(defending, event.getLocation().getChunk()).getAttacking().getTag());
                event.setCancelled(true);
            }
            FRaid fRaid = FRaidManager.getFRaid(defending, attacking, event.getLocation().getChunk());
            if (fRaid.getPhase() == FRaidPhase.PHASE_3) {
                event.setCancelled(true);
            } else {
                GeneralMessage.PHASE_1_RESET.doFactionMessage(attacking);
                GeneralMessage.PHASE_1_RESET.doFactionMessage(defending);
                fRaid.reset();
            }
            if (fRaid.isMainFBase() && !fRaid.isRaided()) {
                if (fRaid.getFBase().isSpawnerChunk(event.getLocation().getChunk())) {
                    fRaid.setRaided(true);
                    for (String line : Files.CONFIG.get().getStringList("phase-4-broadcast")) {
                        Bukkit.broadcastMessage(ColorUtil.colorize(line).replace("{attacking}", attacking.getTag()).replace("{defending}", defending.getTag()));
                    }
                }
            }
        } else {
            FRaidManager.addFRaid(defending, attacking, event.getLocation().getChunk());
        }
    }
}