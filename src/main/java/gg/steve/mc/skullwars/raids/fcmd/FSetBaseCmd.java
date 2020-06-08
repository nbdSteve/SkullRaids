package gg.steve.mc.skullwars.raids.fcmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.zcore.util.TL;
import gg.steve.mc.skullwars.raids.core.FBaseManager;
import gg.steve.mc.skullwars.raids.framework.message.DebugMessage;
import gg.steve.mc.skullwars.raids.framework.permission.PermissionNode;
import gg.steve.mc.skullwars.raids.framework.yml.Files;

public class FSetBaseCmd extends FCommand {

    public FSetBaseCmd() {
        super();
        this.aliases.add("setbase");
        this.aliases.add("sb");
    }

    @Override
    public void perform(CommandContext context) {
        FPlayer sender = context.fPlayer;
        Faction faction = context.faction;
        if (!PermissionNode.SET.hasPermission(sender.getPlayer())) {
            DebugMessage.INSUFFICIENT_PERMISSION.message(sender.getPlayer(), PermissionNode.SET.get());
            return;
        }
        if (faction.isWilderness() || faction.isSafeZone() || faction.isWarZone()) {
            sender.sendMessage("You can not set the base region if you do not have a faction.");
            return;
        }
        if (!sender.equals(faction.getFPlayerLeader())) {
            sender.sendMessage("Only the leader of the faction can use this command.");
            return;
        }
        if (!sender.isInOwnTerritory()) {
            sender.sendMessage("You must be in your own territory to use that command.");
            return;
        }
        if (FBaseManager.isSpawnerChunk(faction, sender.getPlayer().getLocation().getChunk())) {
            sender.sendMessage("This chunk is already a spawner chunk, you can not change it.");
            return;
        }
        if (!FBaseManager.isFBaseSet(faction)) {
            FBaseManager.createFBase(faction, sender.getPlayer().getLocation());
            sender.sendMessage("Successfully registered your faction base.");
        } else if (FBaseManager.getFBase(faction).getSpawnerChunkCount() >= Files.CONFIG.get().getInt("max-spawner-chunks")) {
            sender.sendMessage("You already have the maximum amount of spawner chunks.");
        } else if (!FBaseManager.getFBase(faction).canBeSpawnerChunk(sender.getPlayer().getLocation().getChunk())) {
            sender.sendMessage("All spawner chunks must be connected, this claim can not be a spawner chunk.");
        } else {
            FBaseManager.getFBase(faction).addSpawnerChunk(sender.getPlayer().getLocation().getChunk());
            sender.sendMessage("You have successfully added a spawner chunk to your factions claim.");
        }
        // then register the fbase
    }

    @Override
    public TL getUsageTranslation() {
        return null;
    }
}
