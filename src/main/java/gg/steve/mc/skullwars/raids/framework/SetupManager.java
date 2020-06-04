package gg.steve.mc.skullwars.raids.framework;

import com.massivecraft.factions.FactionsPlugin;
import gg.steve.mc.skullwars.raids.core.FBase;
import gg.steve.mc.skullwars.raids.core.FBaseManager;
import gg.steve.mc.skullwars.raids.fcmd.FSetBaseCmd;
import gg.steve.mc.skullwars.raids.fcmd.FUnsetBaseCmd;
import gg.steve.mc.skullwars.raids.framework.yml.Files;
import gg.steve.mc.skullwars.raids.framework.yml.utils.FileManagerUtil;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Class that handles setting up the plugin on start
 */
public class SetupManager {
    private static FileManagerUtil fileManager;

    private SetupManager() throws IllegalAccessException {
        throw new IllegalAccessException("Manager class cannot be instantiated.");
    }

    /**
     * Loads the files into the file manager
     */
    public static void setupFiles(FileManagerUtil fm) {
        fileManager = fm;
        Files.CONFIG.load(fm);
        Files.PERMISSIONS.load(fm);
        Files.DEBUG.load(fm);
        Files.MESSAGES.load(fm);
    }

    public static void registerCommands(JavaPlugin instance) {
        FactionsPlugin.getInstance().cmdBase.addSubCommand(new FSetBaseCmd());
        FactionsPlugin.getInstance().cmdBase.addSubCommand(new FUnsetBaseCmd());
    }

    /**
     * Register all of the events for the plugin
     *
     * @param instance Plugin, the main plugin instance
     */
    public static void registerEvents(JavaPlugin instance) {
        PluginManager pm = instance.getServer().getPluginManager();
    }

    public static void registerEvent(JavaPlugin instance, Listener listener) {
        instance.getServer().getPluginManager().registerEvents(listener, instance);
    }

    public static void loadPluginCache() {
        FBaseManager.loadFactionBases();
    }

    public static void shutdownPluginCache() {
        FBaseManager.shutdown();
    }

    public static FileManagerUtil getFileManager() {
        return fileManager;
    }
}
