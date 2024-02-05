package dax.walker_engine.navigation_utils;

import dax.shared.helpers.InterfaceHelper;
import dax.walker_engine.WaitFor;
import dax.walker_engine.interaction_handling.InteractionHelper;
import org.tribot.api.General;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Player;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSTile;

public class ZeahMineCarts {

	public static int MINECART_WIDGET = 187;

	public enum Location {
		ARCEUUS("Arceuus", 1670, 3833, 0),
		FARMING_GUILD("Farming Guild", 1218, 3737, 0),
		HOSIDIUS_SOUTH("Hosidius South", 1808, 3479, 0),
		HOSIDIUS_WEST("Hosidius West", 1655, 3543, 0),
		KINGSTOWN("Kingstown", 1699, 3660, 0),
		KOUREND_WOODLAND("Kourend Woodland", 1572, 3466, 0),
		LOVAKENGJ("Lovakengj", 1518, 3733, 0),
		MOUNT_QUIDAMORTEM("Mount Quidamortem", 1255, 3548, 0),
		NORTHERN_TUNDRAS("Northern Tundras", 1648, 3931, 0),
		PORT_PISCARILIUS("Port Piscarilius", 1761, 3710, 0),
		SHAYZIEN_EAST("Shayzien East", 1590, 3620, 0),
		SHAYZIEN_WEST("Shayzien West", 1415, 3577, 0)
		;

		private int x, y, z;
		private String name;
		Location(String name, int x, int y, int z){
			this.x = x;
			this.y = y;
			this.z = z;
			this.name = name;
		}
		public String getName() {
			return name;
		}
		public RSTile getRSTile(){
			return new RSTile(x, y, z);
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public int getZ() {
			return z;
		}
	}

	public static boolean to(Location location){
		if (!Interfaces.isInterfaceSubstantiated(MINECART_WIDGET)
					&& !InteractionHelper.click(InteractionHelper.getRSObject(Filters.Objects.nameEquals("Minecart").and(Filters.Objects.actionsContains("Travel"))), "Travel", () -> Interfaces.isInterfaceSubstantiated(MINECART_WIDGET) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)) {
			return false;
		}

		RSInterface option = InterfaceHelper.getAllInterfaces(MINECART_WIDGET).stream().filter(rsInterface -> {
			String text = rsInterface.getText();
			return text != null && text.contains(location.getName());
		}).findAny().orElse(null);
		if (option == null || !option.click()){
			return false;
		}

		if (WaitFor.condition(General.random(5400, 6500), () -> location.getRSTile().distanceTo(Player.getPosition()) < 10 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS){
			WaitFor.milliseconds(250, 500);
			return true;
		}
		return false;
	}
}
