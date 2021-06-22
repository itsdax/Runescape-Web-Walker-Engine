package scripts.dax_api.teleports.teleport_utils;

import org.tribot.api.Timing;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSTile;
import scripts.dax_api.shared.helpers.magic.Validatable;

public enum TeleportScrolls implements Validatable {
	NARDAH("Nardah teleport",new RSTile(3419, 2916, 0)),
	DIGSITE("Digsite teleport",new RSTile(3325, 3411, 0)),
	FELDIP_HILLS("Feldip hills teleport",new RSTile(2540, 2924, 0)),
	LUNAR_ISLE("Lunar isle teleport",new RSTile(2095, 3913, 0)),
	MORTTON("Mort'ton teleport",new RSTile(3487, 3287, 0)),
	PEST_CONTROL("Pest control teleport",new RSTile(2658, 2658, 0)),
	PISCATORIS("Piscatoris teleport",new RSTile(2340, 3650, 0)),
	TAI_BWO_WANNAI("Tai bwo wannai teleport",new RSTile(2789,3065,0)),
	ELF_CAMP("Elf camp teleport",new RSTile(2193, 3258, 0)),
	MOS_LE_HARMLESS("Mos le'harmless teleport", new RSTile(3700, 2996, 0)),
	LUMBERYARD("Lumberyard teleport",new RSTile(3302, 3487, 0)),
	ZULLANDRA("Zul-andra teleport",new RSTile(2195, 3055, 0)),
	KEY_MASTER("Key master teleport",new RSTile(1311, 1251, 0)),
	REVENANT_CAVES("Revenant cave teleport",new RSTile(3130, 3832, 0)),
	WATSON("Watson teleport", new RSTile(1645, 3579,0))
	;
	private String name;
	private RSTile location;
	TeleportScrolls(String name, RSTile location){
		this.name = name;
		this.location = location;
	}

	public int getX(){
		return location.getX();
	}
	public int getY(){
		return location.getY();
	}
	public int getZ(){
		return location.getPlane();
	}
	
	public boolean teleportTo(boolean shouldWait){
		RSItem[] scroll = Inventory.find(this.name);
		return scroll[0].click() && (!shouldWait || Timing.waitCondition(() -> this.location.distanceTo(Player.getPosition()) < 15, 8000));
	}
	
	public boolean hasScroll(){
		RSItem[] scroll = Inventory.find(this.name);
		return scroll.length > 0;
	}

	public RSTile getLocation(){
		return location;
	}

	@Override
	public boolean canUse(){
		return this.hasScroll() || (MasterScrollBook.hasBook() && this.scrollbookContains());
	}

	public boolean scrollbookContains(){
		if(!MasterScrollBook.has())
			return false;
		switch(this){

			case NARDAH:
				return MasterScrollBook.Teleports.NARDAH.getCount() > 0;
			case DIGSITE:
				return MasterScrollBook.Teleports.DIGSITE.getCount() > 0;
			case FELDIP_HILLS:
				return MasterScrollBook.Teleports.FELDIP_HILLS.getCount() > 0;
			case LUNAR_ISLE:
				return MasterScrollBook.Teleports.LUNAR_ISLE.getCount() > 0;
			case MORTTON:
				return MasterScrollBook.Teleports.MORTTON.getCount() > 0;
			case PEST_CONTROL:
				return MasterScrollBook.Teleports.PEST_CONTROL.getCount() > 0;
			case PISCATORIS:
				return MasterScrollBook.Teleports.PISCATORIS.getCount() > 0;
			case TAI_BWO_WANNAI:
				return MasterScrollBook.Teleports.TAI_BWO_WANNAI.getCount() > 0;
			case ELF_CAMP:
				return MasterScrollBook.Teleports.ELF_CAMP.getCount() > 0;
			case MOS_LE_HARMLESS:
				return MasterScrollBook.Teleports.MOS_LE_HARMLESS.getCount() > 0;
			case LUMBERYARD:
				return MasterScrollBook.Teleports.LUMBERYARD.getCount() > 0;
			case ZULLANDRA:
				return MasterScrollBook.Teleports.ZULLANDRA.getCount() > 0;
			case KEY_MASTER:
				return MasterScrollBook.Teleports.KEY_MASTER.getCount() > 0;
			case REVENANT_CAVES:
				return MasterScrollBook.Teleports.REVENANT_CAVES.getCount() > 0;
		}
		return false;
	}

}
