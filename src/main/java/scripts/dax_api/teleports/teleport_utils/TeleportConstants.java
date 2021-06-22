package scripts.dax_api.teleports.teleport_utils;

import org.tribot.api2007.Combat;
import org.tribot.api2007.types.RSVarBit;

public class TeleportConstants {


    public static final TeleportLimit
            LEVEL_20_WILDERNESS_LIMIT = () -> getWildernessLevel() <= 20,
            LEVEL_30_WILDERNESS_LIMIT = () -> getWildernessLevel() <= 30;

    public static final int
            GE_TELEPORT_VARBIT = 4585, SPELLBOOK_INTERFACE_MASTER = 218, SCROLL_INTERFACE_MASTER = 187;

    private static int getWildernessLevel() {
        return Combat.getWildernessLevel();
    }

    public static boolean isVarrockTeleportAtGE(){
        return RSVarBit.get(GE_TELEPORT_VARBIT).getValue() > 0;
    }

}
