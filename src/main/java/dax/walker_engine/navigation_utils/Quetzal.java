package dax.walker_engine.navigation_utils;

import dax.shared.helpers.RSItemHelper;
import dax.walker_engine.WaitFor;
import dax.walker_engine.interaction_handling.InteractionHelper;
import org.tribot.api.General;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Player;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSTile;
import org.tribot.api2007.types.RSVarBit;
import org.tribot.script.sdk.MessageListening;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Quetzal {


	public static final int
			QUETZAL_WIDGET_ROOT = 874,
			WHISTLE_WIDGET_ROOT = 949,
			WHISTLE_WIDGET_CHILD = 12,
			LAST_LOCATION_VARBIT = 9950;


	private final Map<String, Integer> chargeManager;

	private static Quetzal instance;

	private Quetzal(){
		chargeManager = new HashMap<>();
		MessageListening.addServerMessageListener(message -> {
			int whistleCharges = getWhistleCharges(message);
			if (whistleCharges >= 0) {
				Quetzal.getInstance().setCurrentCharges(whistleCharges);
			}
		});
	}
	public static Quetzal getInstance(){
		return instance != null ? instance : (instance = new Quetzal());
	}


	public int getCurrentCharges() {
		return chargeManager.getOrDefault(Player.getRSPlayer().getName(), 0);
	}

	public void setCurrentCharges(int currentCharges) {
		chargeManager.put(Player.getRSPlayer().getName(), currentCharges);
	}


	public enum Location {
		ALDARIN("Aldarin", 1390, 2901, 0, 11377, 9),
		AUBURNVALE("Auburnvale", 1411, 3361, 0, 17756, 13),
		CAM_TORUM_ENTRANCE("Cam Torum Entrance", 1446, 3108, 0, 9955, 5),
		CIVITAS_ILLA_FORTIS("Civitas illa Fortis", 1696, 3140, 0, 9951, 1),
		COLOSSAL_WYRM_REMAINS("Colossal Wyrm Remains", 1670, 2934, 0, 9956, 6),
		FORTIS_COLOSSEUM("Fortis Colosseum", 1779, 3111, 0, 9958, 8),
		HUNTER_GUILD("Hunter Guild", 1585, 3053, 0, 9954, 4),
		KASTORI("Kastori", 1344, 3022, 0, 17757, 14),
		OUTER_FORTIS("Outer Fortis", 1700, 3035, 0, 9957, 7),
		QUETZACALLI_GORGE("Quetzacalli Gorge", 1510, 3222, 0, 11378, 10),
		SALVAGER_OVERLOOK("Salvager Overlook", 1614, 3300, 0, 11379, 11),
		SUNSET_COAST("Sunset Coast", 1548, 2995, 0, 9953, 3),
		TAL_TEKLAN("Tal Teklan", 1226, 3091, 0, 17755, 12),
		THE_TEOMAT("The Teomat", 1437, 3171, 0, 9952, 2),

		;

		private int x, y, z, lastLocationValue;
		private String name;
		Location(String name, int x, int y, int z, int constructedVarbit, int lastLocationVarbit){
			this.x = x;
			this.y = y;
			this.z = z;
			this.name = name;
			this.lastLocationValue = lastLocationVarbit;
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
		public int getLastLocationValue() {
			return lastLocationValue;
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

	public static boolean teleport(Location location){
		int lastLocation = RSVarBit.get(LAST_LOCATION_VARBIT).getValue();
		if(lastLocation != location.getLastLocationValue()){
			if (!Interfaces.isInterfaceSubstantiated(WHISTLE_WIDGET_ROOT) &&
					!RSItemHelper.click(Filters.Items.nameContains("quetzal whistle"), "Signal") &&
					WaitFor.condition(4500, () -> Interfaces.isInterfaceSubstantiated(QUETZAL_WIDGET_ROOT) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) != WaitFor.Return.SUCCESS){
				RSInterface chatOption = Interfaces.get(229, i -> {
					String text = i.getText();
					return text != null && text.matches(".*whistle.*no charges.*");
				});
				if (Interfaces.isInterfaceSubstantiated(chatOption)) {
					getInstance().setCurrentCharges(0);
				}
				return false;
			}
			RSInterface option = Interfaces.findWhereAction(location.getName(), QUETZAL_WIDGET_ROOT);

			if (option == null){
				return false;
			}

			if (!option.click()){
				return false;
			}
		} else if(!RSItemHelper.click(Filters.Items.nameContains("quetzal whistle"), "Last-destination")){
			return false;
		}
		if (WaitFor.condition(General.random(5400, 6500), () -> location.getRSTile().distanceTo(Player.getPosition()) < 10 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS){
			WaitFor.milliseconds(250, 500);
			return true;
		}
		return false;
	}



	private static final Pattern WHISTLE_CHARGE_PATTERN =
			Pattern.compile("^Your (?:basic |enhanced |perfected )?quetzal whistle has (\\d+) charges remaining\\.$", Pattern.CASE_INSENSITIVE);

	private int getWhistleCharges(String message) {
		String cleanMessage = message.replaceAll("<[^>]*>", "");

		Matcher matcher = WHISTLE_CHARGE_PATTERN.matcher(cleanMessage);

		if (matcher.find()) {
			try {
				return Integer.parseInt(matcher.group(1));
			} catch (NumberFormatException e) {
				return -1;
			}
		}

		return -1;
	}
}
