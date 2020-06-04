package gg.steve.mc.skullwars.raids;

import com.massivecraft.factions.FactionsPlugin;
import gg.steve.mc.skullwars.raids.fcmd.FSetBaseCmd;
import gg.steve.mc.skullwars.raids.framework.utils.LogUtil;
import gg.steve.mc.skullwars.raids.framework.SetupManager;
import gg.steve.mc.skullwars.raids.framework.yml.utils.FileManagerUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class SkullRaids extends JavaPlugin {
    private static SkullRaids instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        LogUtil.setInstance(instance, true);
        if (Bukkit.getPluginManager().getPlugin("Factions") == null) {
            LogUtil.warning("Unable to find the required plugin, Factions, please install SaberFactions to use this plugin.");
            Bukkit.getPluginManager().disablePlugin(instance);
        } else {
            LogUtil.info("Factions plugin found, hooking into it now...");
        }
        SetupManager.setupFiles(new FileManagerUtil(instance));
        SetupManager.registerCommands(instance);
        SetupManager.registerEvents(instance);
        SetupManager.loadPluginCache();
        LogUtil.info("Successfully enabled SkullRaids v" + getDescription().getVersion() + ", please contact nbdSteve#0583 if you find any bugs.");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        SetupManager.shutdownPluginCache();
        LogUtil.info("Successfully disabled SkullRaids v" + getDescription().getVersion() + ", please contact nbdSteve#0583 if you find any bugs.");
    }

    public static SkullRaids getInstance() {
        return instance;
    }
}
