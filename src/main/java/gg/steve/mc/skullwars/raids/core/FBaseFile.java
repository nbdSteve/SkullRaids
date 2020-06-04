package gg.steve.mc.skullwars.raids.core;

import com.massivecraft.factions.Faction;
import gg.steve.mc.skullwars.raids.SkullRaids;
import gg.steve.mc.skullwars.raids.framework.utils.LogUtil;
import gg.steve.mc.skullwars.raids.framework.yml.PluginFile;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FBaseFile extends PluginFile {
    //Store the file name string
    private String fileName;
    //Store the player file
    private File file;
    //Store the yaml config
    private YamlConfiguration config;
    // store the faction
    private Faction faction;
    // store location
    private Location location;

    public FBaseFile(Faction faction) {
        this.load(faction.getId(), SkullRaids.getInstance());
    }

    public FBaseFile(Faction faction, Location location) {
        this.load(faction.getId(), SkullRaids.getInstance());
        setupFactionFileDefaults(this.config, location);
    }

    @Override
    public PluginFile load(String fileName, JavaPlugin instance) {
        //Set instance variable
        this.fileName = fileName;
        //Get the player file
        file = new File(SkullRaids.getInstance().getDataFolder(), "base-data" + File.separator + fileName + ".yml");
        //Load the configuration for the file
        config = YamlConfiguration.loadConfiguration(file);
        //If the file doesn't exist then set the defaults
        save();
        return this;
    }

    private void setupFactionFileDefaults(YamlConfiguration config, Location location) {
        //Set defaults for the information about the players tiers and currency
        int x = location.getChunk().getX(), z = location.getChunk().getZ();
        config.set("base.world", location.getWorld().getName());
        config.set("base.chunk-x", x);
        config.set("base.chunk-z", z);
        List<String> spawnerChunks = new ArrayList<>();
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = z - 1; j <= z + 1; j++) {
                if (i == x && j == z) continue;
                spawnerChunks.add(i + ":" + j);
            }
        }
        config.set("spawner-chunks", spawnerChunks);
        save();
        //Send a nice message
        LogUtil.info("Successfully created a new faction roster file for faction with id: " + fileName + ", defaults have been set.");
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
