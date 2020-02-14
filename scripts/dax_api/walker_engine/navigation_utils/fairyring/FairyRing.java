package scripts.dax_api.walker_engine.navigation_utils.fairyring;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Player;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import scripts.dax_api.walker_engine.WaitFor;
import scripts.dax_api.walker_engine.interaction_handling.InteractionHelper;
import scripts.dax_api.walker_engine.navigation_utils.fairyring.letters.FirstLetter;
import scripts.dax_api.walker_engine.navigation_utils.fairyring.letters.SecondLetter;
import scripts.dax_api.walker_engine.navigation_utils.fairyring.letters.ThirdLetter;

import static scripts.dax_api.walker_engine.navigation_utils.fairyring.letters.FirstLetter.*;
import static scripts.dax_api.walker_engine.navigation_utils.fairyring.letters.SecondLetter.*;
import static scripts.dax_api.walker_engine.navigation_utils.fairyring.letters.ThirdLetter.*;

public class FairyRing {

	public static final int
		INTERFACE_MASTER = 398;

	private static RSObject[] ring;


	private static RSInterface getTeleportButton() {
		return Interfaces.get(398, 26);
	}

	public static boolean takeFairyRing(Locations location){
		General.println("Target location: " + location);
		if(!hasInterface()){
			if(hasCachedLocation(location)){
				return takeLastDestination(location);
			} else if(!openFairyRing()){
				return false;
			}
		}
		final RSTile myPos = Player.getPosition();
		return location.turnTo() && pressTeleport() && Timing.waitCondition(() -> myPos.distanceTo(Player.getPosition()) > 20,8000);
	}

	private static boolean hasInterface(){
		return Interfaces.isInterfaceSubstantiated(INTERFACE_MASTER);
	}

	private static boolean hasCachedLocation(Locations location){
		if(location == null)
			return false;
		ring = Objects.find(25,"Fairy ring");
		return ring.length > 0 && Filters.Objects.actionsContains(location.toString()).test(ring[0]);
	}

	private static boolean takeLastDestination(Locations location){
		final RSTile myPos = Player.getPosition();
		return InteractionHelper.click(ring[0],"Last-destination (" + location + ")") &&
				Timing.waitCondition(() -> myPos.distanceTo(Player.getPosition()) > 20,8000);
	}

	private static boolean pressTeleport(){
		RSInterface iface = getTeleportButton();
		return iface != null && iface.click();
	}

	private static boolean openFairyRing(){
		if(ring.length == 0)
			return false;
		return InteractionHelper.click(ring[0],"Configure") &&
				Timing.waitCondition(() -> Interfaces.isInterfaceSubstantiated(INTERFACE_MASTER),10000);
	}

	public enum Locations {
	    MUDSKIPPER_POINT(A,I,Q),
	    ISLAND_SOUTHEAST_ARDOUGNE(A,I,R),
	    DORGESH_KAAN_SOUTHERN_CAVE(A,J,Q),
	    RELLEKKA_SLAYER_CAVE(A,J,R),
	    MISCELLANIA_PENGUINS(A,J,S),
	    PISCATORIS_HUNTER_AREA(A,K,Q),
	    FELDIP_HILLS_HUNTER_AREA(A,K,S),
	    LIGHTHOUSE(A,L,P),
	    HAUNTED_WOODS(A,L,Q),
	    ABYSSAL_AREA(A,L,R),
	    MCGRUBOR_WOODS(A,L,S),
	    MORT_MYRE_ISLAND(B,I,P),
	    KALPHITE_HIVE(B,I,Q),
	    ARDOUGNE_ZOO(B,I,S),
	    FISHER_KINGS_REALM(B,J,R),
	    ZUL_ANDRA(B,J,S),
	    SOUTH_CASTLE_WARS(B,K,P),
	    ENCHANTED_VALLEY(B,K,Q),
	    MORT_MYRE_SWAMP(B,K,R),
	    ZANARIS(B,K,S),
	    TZHAAR(B,L,P),
	    LEGENDS_GUILD(B,L,R),
	    MISCELLANIA(C,I,P),
	    YANILLE(C,I,Q),
	    MOUNT_KARUULM(C,I,R),
	    ARCEUUS_LIBRARY(C,I,S),
	    SINCLAIR_MANSION(C,J,R),
	    COSMIC_ENTITYS_PLANE(C,K,P),
	    SHILO_VILLAGE(C,K,R),
	    CANIFIS(C,K,S),
	    DRAYNOR_VILLAGE_ISLAND(C,L,P),
	    APE_ATOLL(C,L,R),
	    HAZELMERE(C,L,S),
	    ABYSSAL_NEXUS(D,I,P),
	    POH(D,I,Q),
	    GORAKS_PLANE(D,I,R),
	    WIZARDS_TOWER(D,I,S),
	    TOWER_OF_LIFE(D,J,P),
	    CHASM_OF_FIRE(D,J,R),
	    KARAMJA_KARAMBWAN_SPOT(D,K,P),
	    EDGEVILLE(D,K,R),
	    POLAR_HUNTER_AREA(D,K,S),
	    NORTH_OF_NARDHA(D,L,Q),
	    POISON_WASTE(D,L,R),
	    MYREQUE_HIDEOUT(D,L,S)
	    ;
	    FirstLetter first;
	    SecondLetter second;
	    ThirdLetter third;
	    Locations(FirstLetter first, SecondLetter second, ThirdLetter third){
	        this.first = first;
	        this.second = second;
	        this.third = third;
	    }

	    public boolean turnTo(){
	        return first.turnTo() && WaitFor.milliseconds(200,800) != null &&
			        second.turnTo() && WaitFor.milliseconds(200,800) != null &&
			        third.turnTo() && WaitFor.milliseconds(200,800) != null;
	    }

	    @Override
	    public String toString(){
	        return "" + first + second + third;
	    }
	}
}
