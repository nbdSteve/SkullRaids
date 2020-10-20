package gg.steve.mc.skullwars.raids.framework.cmd.misc;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import gg.steve.mc.skullwars.raids.framework.cmd.SubCommand;
import gg.steve.mc.skullwars.raids.framework.permission.PermissionNode;
import gg.steve.mc.skullwars.raids.raid.FRaidManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class EndSubCmd extends SubCommand {

    public EndSubCmd() {
        super("end", 2, 2, false, PermissionNode.END);
        addAlias("e");
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
        if (!FRaidManager.endFRaid(faction)) {
            sender.sendMessage(ChatColor.RED + "That faction is not being raided at the moment.");
        } else {
            sender.sendMessage(ChatColor.GREEN + "You have ended the raid on " + faction.getTag() + ", attacking players have been sent to spawn.");
        }
    }
}
