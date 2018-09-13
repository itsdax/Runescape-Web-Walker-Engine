package scripts.dax_api.teleport_logic;

import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSInterface;

public class TeleportConstants {

	public static final TeleportLimit LEVEL_20_WILDERNESS_LIMIT = () -> !isIn() || (isIn() && getWildernessLevel() <= 20);
	public static final TeleportLimit LEVEL_30_WILDERNESS_LIMIT = () -> !isIn() || (isIn() && getWildernessLevel() <= 30);

	private final static int WILDERNESS_LEVEL_MASTER = 90;
	private final static int WILDERNESS_LEVEL_CHILD = 46;

	private static boolean isIn() {
		return Interfaces.isInterfaceValid(WILDERNESS_LEVEL_MASTER) || Player.getPosition()
				.getY() > 3525;
	}

	private static int getWildernessLevel() {
		if (!isIn())
			return 0;

		try {
			RSInterface inter = Interfaces.get(WILDERNESS_LEVEL_MASTER, WILDERNESS_LEVEL_CHILD);
			return Integer.parseInt(
					inter.getText()
							.replaceAll("[^0-9]", ""));
		}
		catch (Exception e) {
			return 0;
		}
	}
}
