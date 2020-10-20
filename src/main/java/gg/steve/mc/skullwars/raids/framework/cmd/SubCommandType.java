package gg.steve.mc.skullwars.raids.framework.cmd;


import gg.steve.mc.skullwars.raids.framework.cmd.misc.EndSubCmd;
import gg.steve.mc.skullwars.raids.framework.cmd.misc.HelpSubCmd;
import gg.steve.mc.skullwars.raids.framework.cmd.misc.ProtectSubCmd;
import gg.steve.mc.skullwars.raids.framework.cmd.misc.ReloadSubCmd;

public enum SubCommandType {
    HELP_CMD(new HelpSubCmd()),
    RELOAD_CMD(new ReloadSubCmd()),
    PROTECT_CMD(new ProtectSubCmd()),
    END_CMD(new EndSubCmd());

    private SubCommand sub;

    SubCommandType(SubCommand sub) {
        this.sub = sub;
    }

    public SubCommand getSub() {
        return sub;
    }
}
