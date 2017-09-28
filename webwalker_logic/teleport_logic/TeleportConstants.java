package scripts.webwalker_logic.teleport_logic;

import org.tribot.api2007.Combat;

public class TeleportConstants {

    public static final TeleportLimit
            LEVEL_20_WILDERNESS_LIMIT = () -> Combat.getWildernessLevel() < 20,
            LEVEL_30_WILDERNESS_LIMIT = () -> Combat.getWildernessLevel() < 30;

}
