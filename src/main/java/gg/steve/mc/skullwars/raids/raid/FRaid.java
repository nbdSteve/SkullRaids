package gg.steve.mc.skullwars.raids.raid;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import gg.steve.mc.skullwars.raids.core.BaseClaim;
import gg.steve.mc.skullwars.raids.core.FBase;
import gg.steve.mc.skullwars.raids.core.FBaseManager;
import gg.steve.mc.skullwars.raids.framework.message.GeneralMessage;
import gg.steve.mc.skullwars.raids.framework.utils.ColorUtil;
import gg.steve.mc.skullwars.raids.framework.yml.Files;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.UUID;

public class FRaid {
    private UUID raidId;
    private FRaidPhase phase;
    private int remaining, timeSinceLastShot;
    private Faction attacking, defending;
    private boolean isGen, isAntiLeach, isMainFBase, isRaided;
    private FRaidFile fRaidData;
    private Chunk origin;
    private BaseClaim claim;
    private FBase fBase;

    public FRaid(UUID raidId) {
        this.raidId = raidId;
        this.fRaidData = new FRaidFile(this.raidId);
        YamlConfiguration conf = this.fRaidData.get();
        this.defending = Factions.getInstance().getFactionById(conf.getString("faction.defending"));
        this.attacking = Factions.getInstance().getFactionById(conf.getString("faction.attacking"));
        World world = Bukkit.getWorld(conf.getString("origin.world"));
        this.origin = world.getChunkAt(conf.getInt("origin.chunk-x"), conf.getInt("origin.chunk-z"));
        this.phase = FRaidPhase.valueOf(conf.getString("raid.phase"));
        this.remaining = conf.getInt("raid.remaining");
        this.timeSinceLastShot = conf.getInt("raid.last-shot");
        this.isGen = conf.getBoolean("isGen");
        this.isAntiLeach = conf.getBoolean("isAntiLeach");
        this.isMainFBase = conf.getBoolean("isMainFBase");
        this.isRaided = conf.getBoolean("isRaided");
        if (this.isMainFBase) {
            this.fBase = FBaseManager.getFBase(this.defending);
        } else {
            this.claim = new BaseClaim(this.origin, this.defending);
        }
    }

    public FRaid(UUID raidId, Faction defending, Faction attacking, Chunk origin) {
        this.raidId = raidId;
        this.attacking = attacking;
        this.defending = defending;
        this.phase = FRaidPhase.PHASE_1;
        this.remaining = this.phase.getDuration();
        this.isGen = false;
        this.isAntiLeach = false;
        this.isRaided = false;
        if (FBaseManager.isFBaseSet(defending)) {
            if (FBaseManager.getFBase(defending).isChunkRegistered(origin)) {
                this.isMainFBase = true;
                this.fBase = FBaseManager.getFBase(defending);
            } else {
                this.isMainFBase = false;
                this.claim = new BaseClaim(origin, defending);
            }
        } else {
            this.isMainFBase = false;
            this.claim = new BaseClaim(origin, defending);
        }
        this.origin = origin;
        this.fRaidData = new FRaidFile(raidId, defending, attacking, origin, isMainFBase);
        GeneralMessage.PHASE_1_ATTACKING.doFactionMessage(this.attacking, this.defending.getTag());
        GeneralMessage.PHASE_1_DEFENDING.doFactionMessage(this.defending, this.attacking.getTag());
    }

    public void decrementRemaining() {
        if (this.remaining <= 0) {
            this.phase = FRaidPhase.getNextPhase(this.phase);
            if (this.phase == FRaidPhase.COMPLETE) {
                for (String line : Files.CONFIG.get().getStringList("complete-broadcast")) {
                    Bukkit.broadcastMessage(ColorUtil.colorize(line).replace("{attacking}", attacking.getTag()).replace("{defending}", defending.getTag()));
                }
                FRaidManager.removeFRaid(this.raidId);
                this.fRaidData.delete();
            } else {
                if (this.phase == FRaidPhase.PHASE_2) {
                    GeneralMessage.PHASE_2_ATTACKING.doFactionMessage(this.attacking, this.defending.getTag());
                    GeneralMessage.PHASE_2_DEFENDING.doFactionMessage(this.defending, this.attacking.getTag());
                    this.isGen = true;
                } else if (this.phase == FRaidPhase.PHASE_3) {
                    GeneralMessage.PHASE_3_ATTACKING.doFactionMessage(this.attacking, this.defending.getTag());
                    GeneralMessage.PHASE_3_DEFENDING.doFactionMessage(this.defending, this.attacking.getTag());
                    this.isAntiLeach = false;
                }
                // do update message
                this.remaining = this.phase.getDuration();
            }
        } else {
            if (this.remaining == Files.CONFIG.get().getInt("anti-leach-delay") && this.isMainFBase && this.phase == FRaidPhase.PHASE_1 && !this.isAntiLeach) {
                this.isAntiLeach = true;
                this.initiateAntiLeach();
            }
            this.remaining--;
        }
        this.timeSinceLastShot++;
    }

    public void reset() {
        this.phase = FRaidPhase.PHASE_1;
        this.isGen = false;
        this.remaining = this.phase.getDuration();
        this.timeSinceLastShot = 0;
    }

    public void initiateAntiLeach() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Faction faction = FPlayers.getInstance().getByPlayer(player).getFaction();
            boolean allowed = this.attacking.equals(faction) || this.defending.equals(faction);
            if (allowed) continue;
            if (this.isMainFBase) {
                if (this.fBase.isChunkRegistered(player.getLocation().getChunk())) {
                    player.teleport(this.origin.getWorld().getSpawnLocation());
                    GeneralMessage.ANTI_LEACH.message(player);
                }
            } else {
                if (this.claim.isClaimChunk(player.getLocation().getChunk())) {
                    player.teleport(this.origin.getWorld().getSpawnLocation());
                    GeneralMessage.ANTI_LEACH.message(player);
                }
            }
        }
    }

    public void saveToFile() {
        YamlConfiguration conf = this.fRaidData.get();
        conf.set("raid-id", String.valueOf(this.raidId));
        conf.set("origin.world", this.origin.getWorld().getName());
        conf.set("origin.chunk-x", this.origin.getX());
        conf.set("origin.chunk-z", this.origin.getZ());
        conf.set("faction.defending", this.defending.getId());
        conf.set("faction.attacking", this.attacking.getId());
        conf.set("raid.phase", this.phase.name());
        conf.set("raid.remaining", this.remaining);
        conf.set("raid.last-shot", this.timeSinceLastShot);
        conf.set("isGen", this.isGen);
        conf.set("isAntiLeach", this.isAntiLeach);
        conf.set("isMainFBase", this.isMainFBase);
        conf.set("isRaided", this.isRaided);
        this.fRaidData.save();
    }

    public FRaidPhase getPhase() {
        return phase;
    }

    public int getRemaining() {
        return remaining;
    }

    public Faction getAttacking() {
        return attacking;
    }

    public Faction getDefending() {
        return defending;
    }

    public boolean isGen() {
        return isGen;
    }

    public boolean isAntiLeach() {
        return isAntiLeach;
    }

    public BaseClaim getClaim() {
        return claim;
    }

    public FBase getFBase() {
        return fBase;
    }

    public boolean isMainFBase() {
        return isMainFBase;
    }

    public boolean isRaided() {
        return isRaided;
    }

    public int getTimeSinceLastShot() {
        return timeSinceLastShot;
    }

    public void setRaided(boolean raided) {
        isRaided = raided;
    }
}
