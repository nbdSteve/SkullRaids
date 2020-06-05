package gg.steve.mc.skullwars.raids.listener;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import gg.steve.mc.skullwars.raids.raid.FRaid;
import gg.steve.mc.skullwars.raids.raid.FRaidManager;
import gg.steve.mc.skullwars.raids.raid.FRaidPhase;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.material.Dispenser;

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
        if (faction.isWilderness() || faction.isWarZone() || faction.isSafeZone()){
            event.getPlayer().sendRawMessage("You may only place dispensers in faction claims.");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void dispenser(BlockDispenseEvent event) {
        if (Bukkit.isGracePeriod()) return;
        if (event.getItem().getType() != Material.TNT) return;
        Faction attacking = Board.getInstance().getFactionAt(new FLocation(event.getBlock().getLocation()));
        if (attacking.isWilderness() || attacking.isWarZone() || attacking.isSafeZone()){
            event.setCancelled(true);
            return;
        }
        event.setCancelled(true);
        Dispenser dispenser = (Dispenser) event.getBlock().getState().getData();
        BlockFace face = dispenser.getFacing();
        TNTPrimed primed = (TNTPrimed) event.getBlock().getWorld().spawnEntity(event.getBlock().getRelative(face).getLocation().add(0.5, 0, 0.5), EntityType.PRIMED_TNT);
        factionTnT.put(primed.getUniqueId(), attacking);
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
            if (!FRaidManager.isAttackingFaction(attacking, defending)) {
                attacking.sendMessage(FRaidManager.getFRaid(defending).getAttacking().getTag() + " is already raiding "+ defending.getTag() + ", your shots are not effective.");
                event.setCancelled(true);
            }
            FRaid fRaid = FRaidManager.getFRaid(defending);
            if (fRaid.getPhase() == FRaidPhase.PHASE_3) {
                event.setCancelled(true);
            }
        } else {
            FRaidManager.addFRaid(defending, attacking);
        }
    }
}
