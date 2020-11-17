package gg.steve.mc.skullwars.raids.raid;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import gg.steve.mc.skullwars.raids.combat.CombatUtil;
import gg.steve.mc.skullwars.raids.core.BaseClaim;
import gg.steve.mc.skullwars.raids.core.FBase;
import gg.steve.mc.skullwars.raids.core.FBaseManager;
import gg.steve.mc.skullwars.raids.featherboard.FeatherboardIntegration;
import gg.steve.mc.skullwars.raids.framework.message.GeneralMessage;
import gg.steve.mc.skullwars.raids.framework.utils.ColorUtil;
import gg.steve.mc.skullwars.raids.framework.utils.LogUtil;
import gg.steve.mc.skullwars.raids.framework.utils.TimeUtil;
import gg.steve.mc.skullwars.raids.framework.yml.Files;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

public class FRaid {
    private UUID raidId;
    private FRaidPhase phase;
    private int remaining, timeSinceLastShot, incrementing;
    private Faction attacking, defending;
    private boolean isGen, isAntiLeach, isMainFBase, isRaided;
    private FRaidFile fRaidData;
    private Chunk origin;
    private BaseClaim claim;
    private FBase fBase;
    private Map<UUID, Integer> playerBlockPlacement;
    private List<UUID> antiLeachPlayers;

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
        this.incrementing = conf.getInt("raid.incrementing");
        this.timeSinceLastShot = conf.getInt("raid.last-shot");
        this.isGen = conf.getBoolean("isGen");
        this.isAntiLeach = conf.getBoolean("isAntiLeach");
        this.isMainFBase = conf.getBoolean("isMainFBase");
        this.isRaided = conf.getBoolean("isRaided");
        this.antiLeachPlayers = new ArrayList<>();
        for (String id : conf.getStringList("players-pending-leach-teleport")) {
            this.antiLeachPlayers.add(UUID.fromString(id));
        }
        if (this.isMainFBase) {
            this.fBase = FBaseManager.getFBase(this.defending);
        } else {
            this.claim = new BaseClaim(this.origin, this.defending);
        }
        this.playerBlockPlacement = new HashMap<>();
    }

    public FRaid(UUID raidId, Faction defending, Faction attacking, Chunk origin) {
        this.raidId = raidId;
        this.attacking = attacking;
        this.defending = defending;
        this.phase = FRaidPhase.PHASE_1;
        this.remaining = this.phase.getDuration();
        this.incrementing = 0;
        this.timeSinceLastShot = 0;
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
//        GeneralMessage.PHASE_1_DEFENDING.doFactionMessage(this.defending, this.attacking.getTag());
        this.playerBlockPlacement = new HashMap<>();
        this.antiLeachPlayers = new ArrayList<>();
    }

    public void addPlayer(UUID playerId) {
        this.playerBlockPlacement.put(playerId, 0);
    }

    public void incrementPlayer(UUID playerId) {
        if (!this.playerBlockPlacement.containsKey(playerId)) addPlayer(playerId);
        this.playerBlockPlacement.put(playerId, this.playerBlockPlacement.get(playerId) + 1);
    }

    public boolean isAtMax(UUID playerId) {
        if (!this.playerBlockPlacement.containsKey(playerId)) return false;
        return this.playerBlockPlacement.get(playerId) >= Files.CONFIG.get().getInt("raid-blocks-per-minute");
    }

    public void decrementRemaining() {
        if (this.remaining % 60 == 0) this.playerBlockPlacement.clear();
        if (this.remaining <= 0) {
            this.phase = FRaidPhase.getNextPhase(this.phase);
            if (this.phase == FRaidPhase.COMPLETE) {
                if (Files.CONFIG.get().getBoolean("boards.enabled")
                        && Bukkit.getPluginManager().getPlugin("FeatherBoard") != null) {
                    this.defending.getOnlinePlayers().forEach(FeatherboardIntegration::removeRaidBoards);
                    this.attacking.getOnlinePlayers().forEach(FeatherboardIntegration::removeRaidBoards);
                }
                FRaidManager.removeFRaid(this.raidId);
                this.fRaidData.delete();
                return;
            } else {
                if (this.phase == FRaidPhase.PHASE_2) {
                    if (Files.CONFIG.get().getBoolean("boards.enabled") && Bukkit.getPluginManager().getPlugin("FeatherBoard") != null)
                        defending.getOnlinePlayers().forEach(FeatherboardIntegration::showDefendBoard);
                    GeneralMessage.PHASE_2_ATTACKING.doFactionMessage(this.attacking, this.defending.getTag());
                    GeneralMessage.PHASE_2_DEFENDING.doFactionMessage(this.defending, this.attacking.getTag());
                    this.isGen = true;
                } else if (this.phase == FRaidPhase.PHASE_3) {
                    GeneralMessage.PHASE_3_ATTACKING.doFactionMessage(this.attacking, this.defending.getTag(), new TimeUtil(this.incrementing).getTimeAsString());
                    GeneralMessage.PHASE_3_DEFENDING.doFactionMessage(this.defending, this.attacking.getTag(), new TimeUtil(this.incrementing).getTimeAsString());
                    if (this.isRaided) {
                        for (String line : Files.CONFIG.get().getStringList("complete-unsuccess-broadcast")) {
                            Bukkit.broadcastMessage(ColorUtil.colorize(line)
                                    .replace("{attacking}", attacking.getTag())
                                    .replace("{defending}", defending.getTag())
                                    .replace("{raid_duration}", new TimeUtil(this.incrementing).getTimeAsString()));
                        }
                    } else {
                        for (String line : Files.CONFIG.get().getStringList("complete-success-broadcast")) {
                            Bukkit.broadcastMessage(ColorUtil.colorize(line)
                                    .replace("{attacking}", attacking.getTag())
                                    .replace("{defending}", defending.getTag())
                                    .replace("{raid_duration}", new TimeUtil(this.incrementing).getTimeAsString()));
                        }
                    }
                    this.isAntiLeach = false;
                }
                this.remaining = this.phase.getDuration();
            }
        } else {
            this.remaining--;
        }
        if (this.incrementing >= Files.CONFIG.get().getInt("anti-leach-delay")) {
            if (Files.CONFIG.get().getBoolean("boards.enabled") && Bukkit.getPluginManager().getPlugin("FeatherBoard") != null) {
                this.defending.getOnlinePlayers().forEach(FeatherboardIntegration::showDefendBoard);
            }
        }
        if (this.incrementing == Files.CONFIG.get().getInt("anti-leach-delay") && this.isMainFBase && this.phase == FRaidPhase.PHASE_1 && !this.isAntiLeach) {
//            if (Files.CONFIG.get().getBoolean("boards.enabled") && Bukkit.getPluginManager().getPlugin("FeatherBoard") != null)
//                this.defending.getOnlinePlayers().forEach(FeatherboardIntegration::showDefendBoard);
            this.isAntiLeach = true;
            this.initiateAntiLeach();
        }
        if (this.antiLeachPlayers != null && !this.antiLeachPlayers.isEmpty()) {
            List<UUID> removed = new ArrayList<>();
            for (UUID playerId : this.antiLeachPlayers) {
                Player player = Bukkit.getPlayer(playerId);
                if (player == null) continue;
                if (Bukkit.getPluginManager().getPlugin("CombatLogX") != null) {
                    if (CombatUtil.isInCombat(player)) {
                        if (this.antiLeachPlayers == null) this.antiLeachPlayers = new ArrayList<>();
                        this.antiLeachPlayers.add(player.getUniqueId());
                        continue;
                    }
                }
//                if (Bukkit.getServer().getPluginManager().getPlugin("CombatTagPlus") != null) {
//                    CombatTagPlus CombatTag = (CombatTagPlus) Bukkit.getServer().getPluginManager().getPlugin("CombatTagPlus");
//                    if (CombatTag.getTagManager().isTagged(player.getUniqueId())) continue;
//                }
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
                removed.add(playerId);
            }
            if (!removed.isEmpty()) this.antiLeachPlayers.removeAll(removed);
        }
        this.incrementing++;
        this.timeSinceLastShot++;
    }

    public boolean isLeachPlayer(Player player) {
        if (this.antiLeachPlayers == null || this.antiLeachPlayers.isEmpty()) return false;
        return this.antiLeachPlayers.contains(player.getUniqueId());
    }

    public void forceEnd() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Faction faction = FPlayers.getInstance().getByPlayer(player).getFaction();
            boolean allowed = this.defending.equals(faction);
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
        if (Files.CONFIG.get().getBoolean("boards.enabled") && Bukkit.getPluginManager().getPlugin("FeatherBoard") != null) {
            attacking.getOnlinePlayers().forEach(FeatherboardIntegration::removeRaidBoards);
            defending.getOnlinePlayers().forEach(FeatherboardIntegration::removeRaidBoards);
        }
        GeneralMessage.RAID_END.doFactionMessage(this.defending);
        GeneralMessage.RAID_END.doFactionMessage(this.attacking);
        FRaidManager.removeFRaid(this.raidId);
        this.fRaidData.delete();
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
            if (Bukkit.getPluginManager().getPlugin("CombatLogX") != null) {
                if (CombatUtil.isInCombat(player)) {
                    if (this.antiLeachPlayers == null) this.antiLeachPlayers = new ArrayList<>();
                    this.antiLeachPlayers.add(player.getUniqueId());
                    continue;
                }
            }
//            if (Bukkit.getServer().getPluginManager().getPlugin("CombatTagPlus") != null) {
//                CombatTagPlus CombatTag = (CombatTagPlus) Bukkit.getServer().getPluginManager().getPlugin("CombatTagPlus");
//                if (CombatTag.getTagManager().isTagged(player.getUniqueId())) {
//                    if (this.antiLeachPlayers == null) this.antiLeachPlayers = new ArrayList<>();
//                    this.antiLeachPlayers.add(player.getUniqueId());
//                    continue;
//                }
//            }
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
        conf.set("raid.incrementing", this.incrementing);
        conf.set("raid.last-shot", this.timeSinceLastShot);
        conf.set("isGen", this.isGen);
        conf.set("isAntiLeach", this.isAntiLeach);
        conf.set("isMainFBase", this.isMainFBase);
        conf.set("isRaided", this.isRaided);
        List<String> leach = new ArrayList<>();
        for (UUID id : this.antiLeachPlayers) {
            leach.add(String.valueOf(id));
        }
        conf.set("players-pending-leach-teleport", leach);
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
