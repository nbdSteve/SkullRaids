package gg.steve.mc.skullwars.raids.fcmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.zcore.util.TL;
import gg.steve.mc.skullwars.raids.core.FBaseManager;

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
