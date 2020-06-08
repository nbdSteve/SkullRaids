package gg.steve.mc.skullwars.raids.fcmd;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.zcore.util.TL;
import gg.steve.mc.skullwars.raids.core.FBaseManager;
import gg.steve.mc.skullwars.raids.framework.message.DebugMessage;
import gg.steve.mc.skullwars.raids.framework.permission.PermissionNode;
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
        if (!PermissionNode.UN_SET.hasPermission(player)) {
            DebugMessage.INSUFFICIENT_PERMISSION.message(player, PermissionNode.UN_SET.get());
            return;
        }
        if (faction == null || faction.isWilderness() || faction.isSafeZone() || faction.isWarZone()) {
            DebugMessage.NOT_IN_CLAIM.message(player);
            return;
        }
        if (!FBaseManager.isFBaseSet(faction)) {
            DebugMessage.NO_FBASE_SET.message(player);
            return;
        }
        FBaseManager.unsetFBase(faction);
        DebugMessage.REMOVED_FBASE.message(player);
    }

    @Override
    public TL getUsageTranslation() {
        return null;
    }
}
