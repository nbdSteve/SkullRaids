package gg.steve.mc.skullwars.raids.featherboard;

import be.maximvdw.featherboard.api.FeatherBoardAPI;
import gg.steve.mc.skullwars.raids.framework.yml.Files;
import org.bukkit.entity.Player;

public class FeatherboardIntegration {

    public static void showRaidBoard(Player player) {
        FeatherBoardAPI.showScoreboard(player, Files.CONFIG.get().getString("boards.raid"), true);
    }

    public static void showDefendBoard(Player player) {
        FeatherBoardAPI.showScoreboard(player, Files.CONFIG.get().getString("boards.defend"), true);
    }

    public static void removeRaidBoards(Player player) {
        FeatherBoardAPI.showScoreboard(player, Files.CONFIG.get().getString("boards.default"), true);
    }
}
