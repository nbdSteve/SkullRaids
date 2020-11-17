package gg.steve.mc.skullwars.raids.featherboard;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import gg.steve.mc.skullwars.raids.framework.yml.Files;
import gg.steve.mc.skullwars.raids.raid.FRaid;
import gg.steve.mc.skullwars.raids.raid.FRaidManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class FeatherboardListener implements Listener {

    @EventHandler
    public void join(PlayerJoinEvent event) {
        Faction faction = FPlayers.getInstance().getByPlayer(event.getPlayer()).getFaction();
        if ((FRaidManager.isRaiding(faction) || FRaidManager.isRaidActive(faction))
                && Files.CONFIG.get().getBoolean("boards.enabled")
                && Bukkit.getPluginManager().getPlugin("FeatherBoard") != null) {
            FRaid raid = FRaidManager.getFRaid(faction);
            if (raid == null) return;
            if (raid.getAttacking().equals(faction)) {
                FeatherboardIntegration.showRaidBoard(event.getPlayer());
            } else if (raid.getDefending().equals(faction) && raid.isAntiLeach()) {
                FeatherboardIntegration.showDefendBoard(event.getPlayer());
            }
        }
    }
}
