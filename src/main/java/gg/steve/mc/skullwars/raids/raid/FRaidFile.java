package gg.steve.mc.skullwars.raids.raid;

import com.massivecraft.factions.Faction;
import gg.steve.mc.skullwars.raids.SkullRaids;
import gg.steve.mc.skullwars.raids.framework.utils.LogUtil;
import gg.steve.mc.skullwars.raids.framework.yml.PluginFile;
import org.bukkit.Chunk;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class FRaidFile extends PluginFile {
    //Store the file name string
    private String fileName;
    //Store the player file
    private File file;
    //Store the yaml config
    private YamlConfiguration config;

    public FRaidFile(UUID raidId) {
        this.load(String.valueOf(raidId), SkullRaids.getInstance());
    }

    public FRaidFile(UUID raidId, Faction defending, Faction raiding, Chunk origin, boolean isMainFBase) {
        this.load(String.valueOf(raidId), SkullRaids.getInstance());
        setupFactionFileDefaults(this.config, raidId, defending, raiding, origin, isMainFBase);
    }

    @Override
    public PluginFile load(String fileName, JavaPlugin instance) {
        //Set instance variable
        this.fileName = fileName;
        //Get the player file
        file = new File(SkullRaids.getInstance().getDataFolder(), "raid-data" + File.separator + fileName + ".yml");
        //Load the configuration for the file
        config = YamlConfiguration.loadConfiguration(file);
        //If the file doesn't exist then set the defaults
        save();
        return this;
    }

    private void setupFactionFileDefaults(YamlConfiguration config, UUID raidId, Faction defending, Faction attacking, Chunk origin, boolean isMainFBase) {
        //Set defaults for the information about the players tiers and currency
        config.set("raid-id", String.valueOf(raidId));
        config.set("origin.world", origin.getWorld().getName());
        config.set("origin.chunk-x", origin.getX());
        config.set("origin.chunk-z", origin.getZ());
        config.set("faction.defending", defending.getId());
        config.set("faction.attacking", attacking.getId());
        config.set("raid.phase", FRaidPhase.PHASE_1.name());
        config.set("raid.remaining", FRaidPhase.PHASE_1.getDuration());
        config.set("raid.last-shot", 0);
        config.set("isGen", false);
        config.set("isAntiLeach", false);
        config.set("isMainFBase", isMainFBase);
        config.set("isRaided", false);
        config.set("players-pending-leach-teleport", new ArrayList<>());
        save();
    }

    @Override
    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            LogUtil.warning("Critical error saving the file: " + fileName + ", please contact nbdSteve#0583 on discord.");
        }
        reload();
    }

    @Override
    public void reload() {
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            LogUtil.warning("Critical error loading the file: " + fileName + ", please contact nbdSteve#0583 on discord.");
        }
    }

    @Override
    public YamlConfiguration get() {
        return this.config;
    }

    public void delete() {
        file.delete();
    }
}
