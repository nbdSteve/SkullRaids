package gg.steve.mc.skullwars.raids.raid;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import gg.steve.mc.skullwars.raids.framework.utils.LogUtil;
import org.bukkit.configuration.file.YamlConfiguration;

public class FRaid {
    private FRaidPhase phase;
    private int remaining;
    private Faction attacking, defending;
    private boolean isGen, isAntiLeach;
    private FRaidFile fRaidData;

    public FRaid(Faction defending) {
        this.defending = defending;
        this.fRaidData = new FRaidFile(defending);
        YamlConfiguration conf = this.fRaidData.get();
        this.attacking = Factions.getInstance().getFactionById(conf.getString("faction.attacking"));
        this.phase = FRaidPhase.valueOf(conf.getString("raid.phase"));
        this.remaining = conf.getInt("raid.remaining");
        this.isGen = conf.getBoolean("isGen");
        this.isAntiLeach = conf.getBoolean("isAntiLeach");
    }

    public FRaid(Faction defending, Faction attacking) {
        this.attacking = attacking;
        this.defending = defending;
        this.phase = FRaidPhase.PHASE_1;
        this.remaining = this.phase.getDuration();
        this.isGen = false;
        this.isAntiLeach = false;
        this.fRaidData = new FRaidFile(defending, attacking);
        defending.sendMessage(attacking.getTag() + " has started raiding you!");
        attacking.sendMessage("You have started raiding " + defending.getTag());
    }

    public void decrementRemaining() {
        if (this.remaining <= 0) {
            this.phase = FRaidPhase.getNextPhase(this.phase);
            LogUtil.info("changed raid phase to: " + this.phase.name());
            if (this.phase == FRaidPhase.COMPLETE) {
                FRaidManager.removeFRaid(this.defending);
                this.fRaidData.delete();
            } else {
                if (this.phase == FRaidPhase.PHASE_2) {
                    this.isGen = true;
                }
                // do update message
                this.remaining = this.phase.getDuration();
            }
        } else {
            if (this.remaining == 600) {
                this.isAntiLeach = true;
                this.initiateAntiLeach();
            }
            this.remaining--;
        }
    }

    public void reset() {
        this.phase = FRaidPhase.PHASE_1;
        this.remaining = this.phase.getDuration();
        // send message
    }

    public void initiateAntiLeach() {

    }

    public void saveToFile() {
        YamlConfiguration conf = this.fRaidData.get();
        conf.set("faction.defending", this.defending.getId());
        conf.set("faction.attacking", this.attacking.getId());
        conf.set("raid.phase", this.phase.name());
        conf.set("raid.remaining", this.remaining);
        conf.set("isGen", this.isGen);
        conf.set("isAntiLeach", this.isAntiLeach);
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
}
