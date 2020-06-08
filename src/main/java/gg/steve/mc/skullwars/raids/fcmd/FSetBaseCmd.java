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
            DebugMessage.NO_FACTION.message(sender.getPlayer());
            return;
        }
        if (!sender.equals(faction.getFPlayerLeader())) {
            DebugMessage.NOT_LEADER.message(sender.getPlayer());
            return;
        }
        if (!sender.isInOwnTerritory()) {
            DebugMessage.NOT_IN_TERRITORY.message(sender.getPlayer());
            return;
        }
        if (FBaseManager.isSpawnerChunk(faction, sender.getPlayer().getLocation().getChunk())) {
            DebugMessage.ALREADY_SPAWNER_CHUNK.message(sender.getPlayer());
            return;
        }
        if (!FBaseManager.isFBaseSet(faction)) {
            FBaseManager.createFBase(faction, sender.getPlayer().getLocation());
            DebugMessage.FBASE_REGISTER.message(sender.getPlayer());
        } else if (FBaseManager.getFBase(faction).getSpawnerChunkCount() >= Files.CONFIG.get().getInt("max-spawner-chunks")) {
            DebugMessage.MAX_SPAWNER_CHUNKS.message(sender.getPlayer());
        } else if (!FBaseManager.getFBase(faction).canBeSpawnerChunk(sender.getPlayer().getLocation().getChunk())) {
            DebugMessage.NOT_CONNECTED.message(sender.getPlayer());
        } else {
            FBaseManager.getFBase(faction).addSpawnerChunk(sender.getPlayer().getLocation().getChunk());
            DebugMessage.ADD_SPAWNER_CHUNK.message(sender.getPlayer());
        }
    }

    @Override
    public TL getUsageTranslation() {
        return null;
    }
}
