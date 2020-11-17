package gg.steve.mc.skullwars.raids.framework.cmd.misc;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import gg.steve.mc.skullwars.raids.framework.cmd.SubCommand;
import gg.steve.mc.skullwars.raids.framework.permission.PermissionNode;
import gg.steve.mc.skullwars.raids.framework.utils.TimeUtil;
import gg.steve.mc.skullwars.raids.raid.FRaidManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ProtectSubCmd extends SubCommand {

    public ProtectSubCmd() {
        super("protect", 3, 3, false, PermissionNode.PROTECT);
        addAlias("p");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Faction faction = null;
        try {
            faction = Factions.getInstance().getBestTagMatch(args[1]);
            if (faction == null) throw new Exception();
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Please enter a valid faction.");
            return;
        }
        int duration = 0;
        try {
            duration = Integer.parseInt(args[2]);
            if (duration <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Please enter an integer that is greater than 0.");
        }
        if (!FRaidManager.setProtected(faction, duration)) {
            sender.sendMessage(ChatColor.RED + "That faction cannot be protected until they have set and fbase.");
        } else {
            TimeUtil time = new TimeUtil(duration);
            sender.sendMessage(ChatColor.GREEN + "That faction is now protected from raiding for " + time.getTimeAsString() + ".");
        }
    }
}
