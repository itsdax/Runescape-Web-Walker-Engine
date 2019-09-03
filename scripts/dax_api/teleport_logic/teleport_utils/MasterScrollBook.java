package scripts.dax_api.teleport_logic.teleport_utils;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.interfaces.Clickable07;
import org.tribot.api2007.*;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSTile;
import org.tribot.api2007.types.RSVarBit;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;

public class MasterScrollBook {

	public static final int
			INTERFACE_MASTER = 597, DEFAULT_VARBIT = 5685,
			SELECT_OPTION_MASTER = 219, SELECT_OPTION_CHILD = 1,
			GAMETABS_INTERFACE_MASTER = 161;
	private static Map<String, Integer> cache = new HashMap<String, Integer>();
	
	public enum Teleports {
		NARDAH(5672,"Nardah",new RSTile(3419, 2916, 0)),
		DIGSITE(5673,"Digsite",new RSTile(3325, 3411, 0)),
		FELDIP_HILLS(5674,"Feldip Hills",new RSTile(2540, 2924, 0)),
		LUNAR_ISLE(5675,"Lunar Isle",new RSTile(2095, 3913, 0)),
		MORTTON(5676,"Mort'ton",new RSTile(3487, 3287, 0)),
		PEST_CONTROL(5677,"Pest Control",new RSTile(2658, 2658, 0)),
		PISCATORIS(5678,"Piscatoris",new RSTile(2340, 3650, 0)),
		TAI_BWO_WANNAI(5679,"Tai Bwo Wannai",new RSTile(2789,3065,0)),
		ELF_CAMP(5680,"Elf Camp",new RSTile(2193, 3258, 0)),
		MOS_LE_HARMLESS(5681,"Mos Le'Harmless", new RSTile(3700, 2996, 0)),
		LUMBERYARD(5682,"Lumberyard",new RSTile(3302, 3487, 0)),
		ZULLANDRA(5683,"Zul-Andra",new RSTile(2195, 3055, 0)),
		KEY_MASTER(5684,"Key Master",new RSTile(1311, 1251, 0)),
		REVENANT_CAVES(6056,"Revenant cave",new RSTile(3130, 3832, 0));
		
		private int varbit;
		private String name;
		private RSTile destination;
		Teleports(int varbit, String name, RSTile destination){
			this.varbit = varbit;
			this.name = name;
		}
		
		//Returns the number of scrolls stored in the book.
		public int getCount(){
			RSVarBit var = RSVarBit.get(varbit);
			return var != null ? var.getValue() : 0;
		}
		
		//Returns the name of the teleport.
		public String getName(){
			return name;
		}
		
		//Returns the destination that the teleport will take you to.
		public RSTile getDestination(){
			return destination;
		}
		
		//Sets the teleport as the default left-click option of the book.
		public boolean setAsDefault(){
			if(NPCChat.getOptions() != null){
				String text = getDefaultTeleportText();
				if(text.contains(this.getName())){
					return NPCChat.selectOption("Yes", true);
				}
			}
			if(!isOpen()){
				openBook();
			}
			RSInterface target = getInterface(this);
			if(target == null)
				return false;
			return click(target,"Set as default") && waitForOptions() && NPCChat.selectOption("Yes", true);
			
		}
		
		//Uses the teleport and waits until you arrive at the destination.
		public boolean use(){
			if(this == getDefault()){
				RSItem[] book = getBook();
				return book.length > 0 && click(book[0],"Teleport") && waitTillAtDestination(this);
			}
			if(this == REVENANT_CAVES) // bug where you can't activate it from the interface for whatever reason.
				return setAsDefault() && use();
			if(!isOpen())
				openBook();
			RSInterface target = getInterface(this);
			return target != null && click(target, "Activate") && waitTillAtDestination(this);
		}
		
	}
	
	public static boolean teleport(Teleports teleport){
		return teleport != null && teleport.getCount() > 0 && teleport.use();
	}
	
	public static int getCount(Teleports teleport){
		return teleport != null ? teleport.getCount() : 0;
	}
	
	public static boolean isDefault(Teleports teleport){
		return getDefault() == teleport;
	}
	
	public static boolean setAsDefault(Teleports teleport){
		return teleport != null && teleport.setAsDefault();
	}
	
	public static Teleports getDefault(){
		RSVarBit defaultTeleport = RSVarBit.get(DEFAULT_VARBIT);
		int value;
		if(defaultTeleport == null || (value = defaultTeleport.getValue()) == 0)
			return null;
		return Teleports.values()[value-1];
	}
	
	//Removes the default left click teleport option.
	public static boolean removeDefault(){
		RSItem[] book = getBook();
		if(Game.isUptext("->")){
			resetUptext();
		}
		return book.length > 0 && click(book[0],"Remove default") && waitForOptions() && NPCChat.selectOption("Yes", true);
	}
	
	//Caches the index and returns the RSInterface associated with the selected teleport.
	private static RSInterface getInterface(Teleports teleport){
		if(cache.containsKey(teleport.getName())){
			return Interfaces.get(INTERFACE_MASTER,cache.get(teleport.getName()));
		}
		RSInterface master = Interfaces.get(INTERFACE_MASTER);
		for(RSInterface child:master.getChildren()){
			String name = child.getComponentName();
			if(name == null){
				continue;
			} else if(name.startsWith("<") && General.stripFormatting(name).contains(teleport.getName())){
				cache.put(teleport.getName(), child.getIndex());
				return child;
			}
		}
		return null;
	}
	
	//Returns true if the Master scroll book interface is open.
	public static boolean isOpen(){
		return Interfaces.isInterfaceSubstantiated(INTERFACE_MASTER);
	}
	
	//Opens the master scroll book interface.
	public static boolean openBook(){
		RSItem[] book = getBook();
		if(Game.isUptext("->")){
			resetUptext();
		}
		return book.length > 0 && click(book[0],"Open") && waitForBookToOpen();
	}
	
	private static RSItem[] getBook(){
		return Inventory.find("Master scroll book");
	}
	
	private static boolean waitForBookToOpen(){
		return Timing.waitCondition(new BooleanSupplier(){

			@Override
			public boolean getAsBoolean() {
				General.sleep(50,200);
				return isOpen();
			}
			
		}, 5000);
	}
	
	private static boolean waitForOptions(){
		return Timing.waitCondition(new BooleanSupplier(){

			@Override
			public boolean getAsBoolean() {
				General.sleep(50,200);
				return NPCChat.getOptions().length > 0;
			}
			
		}, 5000);
	}
	
	//Checks which scroll we are setting to default currently.
	private static String getDefaultTeleportText(){
		RSInterface master = Interfaces.get(SELECT_OPTION_MASTER,SELECT_OPTION_CHILD);
		if(master == null)
			return null;
		RSInterface[] ifaces = master.getChildren();
		if(ifaces == null)
			return null;
		for(RSInterface iface:ifaces){
			String txt = iface.getText();
			if(txt == null || !txt.startsWith("Set"))
				continue;
			return txt;
		}
		return null;
	}
	
	//Resets uptext.
	private static void resetUptext(){
		RSInterface master = Interfaces.get(GAMETABS_INTERFACE_MASTER);
		RSInterface[] children = master.getChildren();
		if(children == null)
			return;
		RSInterface inventory = null;
		for(RSInterface child:children){
			String[] actions = child.getActions();
			if(actions == null || actions.length == 0)
				continue;
			if(Arrays.asList(actions).contains("Inventory")){
				inventory = child;
				break;
			}
		}
		if(inventory != null)
			inventory.click();
	}
	
	private static boolean waitTillAtDestination(Teleports location){
		return Timing.waitCondition(new BooleanSupplier(){

			@Override
			public boolean getAsBoolean() {
				General.sleep(50,200);
				return location.getDestination().distanceTo(Player.getPosition()) < 10;
			}
			
		}, 8000);
	}
	
	private static boolean click(Clickable07 clickable, String action){
		if(Game.isUptext("->") && !action.contains("->")){
			resetUptext();
		}
		return clickable.click(action);
	}
	
	
}
