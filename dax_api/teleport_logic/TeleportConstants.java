package scripts.dax_api.teleport_logic;

import org.tribot.api2007.Combat;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSInterface;

public class TeleportConstants {


    public static final TeleportLimit
            LEVEL_20_WILDERNESS_LIMIT = () -> getWildernessLevel() < 20,
            LEVEL_30_WILDERNESS_LIMIT = () -> getWildernessLevel() < 30;

    private static int getWildernessLevel() {
        return Combat.getWildernessLevel();
    }

}
