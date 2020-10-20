package gg.steve.mc.skullwars.raids.featherboard;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import gg.steve.mc.skullwars.raids.framework.yml.Files;
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
                && Files.CONFIG.get().getBoolean("raid-scoreboard.enabled")
                && Bukkit.getPluginManager().getPlugin("Featherboard") != null) {
            FeatherboardIntegration.showRaidBoard(event.getPlayer());
        }
    }
}
