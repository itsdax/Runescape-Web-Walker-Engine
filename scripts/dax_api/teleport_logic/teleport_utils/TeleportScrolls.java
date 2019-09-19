package scripts.dax_api.teleport_logic.teleport_utils;

import org.tribot.api.Timing;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSTile;
import scripts.dax_api.teleport_logic.Validatable;

public enum TeleportScrolls implements Validatable {
	NARDAH("Nardah teleport",new RSArea(new RSTile(3400,2875,0),new RSTile(3520,2960,0))),
	PISCATORIS("Piscatoris teleport",new RSArea(new RSTile(2309, 3646, 0), new RSTile(2320, 3635, 0)));
	
	String name;
	RSArea location;	
	TeleportScrolls(String name, RSArea location){
		this.name = name;
		this.location = location;
	}
	
	public boolean teleportTo(boolean shouldWait){
		RSItem[] scroll = Inventory.find(this.name);
		return scroll.length > 0 && scroll[0].click() && (!shouldWait || Timing.waitCondition(() -> this.location.contains(Player.getPosition()), 8000));
	}
	
	public boolean hasScroll(){
		RSItem[] scroll = Inventory.find(this.name);
		return scroll.length > 0;
	}

	@Override
	public boolean canUse(){
		return this.hasScroll() || this.scrollbookContains();
	}

	public boolean scrollbookContains(){
		switch(this){

			case NARDAH:
				return MasterScrollBook.Teleports.NARDAH.getCount() > 0;
			case PISCATORIS:
				return MasterScrollBook.Teleports.PISCATORIS.getCount() > 0;
		}
		return false;
	}

}
