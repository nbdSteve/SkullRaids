package gg.steve.mc.skullwars.raids.fcmd;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.zcore.util.TL;
import gg.steve.mc.skullwars.raids.core.FBaseManager;
import org.bukkit.entity.Player;

public class FUnsetBaseCmd extends FCommand {

    public FUnsetBaseCmd() {
        super();
        this.aliases.add("unsetbase");
        this.aliases.add("usb");
    }

    @Override
    public void perform(CommandContext context) {
        Player player = context.fPlayer.getPlayer();
        Faction faction = Board.getInstance().getFactionAt(new FLocation(player.getLocation()));
        if (faction == null || faction.isWilderness() || faction.isSafeZone() || faction.isWarZone()) {
            player.sendMessage("You are not standing in a faction claim at the moment.");
            return;
        }
        if (!FBaseManager.isFBaseSet(faction)) {
            player.sendMessage("That faction does not have an fbase set.");
            return;
        }
        FBaseManager.unsetFBase(faction);
        player.sendMessage("You have successfully removed " + faction.getTag() + "'s base.");
    }

    @Override
    public TL getUsageTranslation() {
        return null;
    }
}
