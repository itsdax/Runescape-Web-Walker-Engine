package dax.walker_engine.navigation_utils;

import dax.walker_engine.WaitFor;
import dax.walker_engine.interaction_handling.InteractionHelper;
import org.tribot.api.General;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Player;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSTile;

public class Quetzal {

	public static final int QUETZAL_WIDGET_ROOT = 874;

	public enum Location {
		ALDARIN("Aldarin", 1390, 2901, 0),
		CAM_TORUM_ENTRANCE("Cam Torum Entrance", 1446, 3108, 0),
		CIVITAS_ILLA_FORTIS("Civitas illa Fortis", 1696, 3140, 0),
		COLOSSAL_WYRM_REMAINS("Colossal Wyrm Remains", 1670, 2934, 0),
		//FORTIS_COLOSSEUM("Fortis Colosseum", ),
		OUTER_FORTIS("Outer Fortis", 1700, 3035, 0),
		QUETZACALLI_GORGE("Quetzacalli Gorge", 1510, 3222, 0),
		HUNTER_GUILD("Hunter Guild", 1585, 3053, 0),
		SALVAGER_OUTLOOK("Salvager Outlook", 1614, 3300, 0),
		SUNSET_COAST("Sunset Coast", 1548, 2995, 0),
		THE_TEOMAT("The Teomat", 1437, 3171, 0),
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

		if (!Interfaces.isInterfaceSubstantiated(QUETZAL_WIDGET_ROOT)
					&& !InteractionHelper.click(InteractionHelper.getRSNPC(Filters.NPCs.nameEquals("Renu")), "Travel", () -> Interfaces.isInterfaceSubstantiated(QUETZAL_WIDGET_ROOT) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)) {
			return false;
		}

		RSInterface option = Interfaces.findWhereAction(location.getName(), QUETZAL_WIDGET_ROOT);

		if (option == null){
			return false;
		}

		if (!option.click()){
			return false;
		}

		if (WaitFor.condition(General.random(5400, 6500), () -> location.getRSTile().distanceTo(Player.getPosition()) < 10 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS){
			WaitFor.milliseconds(250, 500);
			return true;
		}
		return false;
	}
}
