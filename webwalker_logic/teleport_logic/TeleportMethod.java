package scripts.webwalker_logic.teleport_logic;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Equipment;
import org.tribot.api2007.Game;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Player;
import org.tribot.api2007.WorldHopper;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;
import org.tribot.api2007.types.RSTile;
import scripts.webwalker_logic.local.walker_engine.interaction_handling.NPCInteraction;
import scripts.webwalker_logic.local.walker_engine.WaitFor;
import scripts.webwalker_logic.shared.helpers.RSItemHelper;
import scripts.webwalker_logic.shared.helpers.magic.Spell;

import java.util.ArrayList;
import java.util.Arrays;

import static scripts.webwalker_logic.teleport_logic.TeleportLocation.*;

public enum TeleportMethod implements Validatable {

    VARROCK_TELEPORT (TeleportConstants.LEVEL_20_WILDERNESS_LIMIT, VARROCK_CENTER),
    LUMBRIDGE_TELEPORT  (TeleportConstants.LEVEL_20_WILDERNESS_LIMIT, LUMBRIDGE_CASTLE),
    FALADOR_TELEPORT  (TeleportConstants.LEVEL_20_WILDERNESS_LIMIT, FALADOR_CENTER),
    CAMELOT_TELEPORT  (TeleportConstants.LEVEL_20_WILDERNESS_LIMIT, CAMELOT),
    ARDOUGNE_TELPORT  (TeleportConstants.LEVEL_20_WILDERNESS_LIMIT, ARDOUGNE_MARKET_PLACE),
    GLORY (TeleportConstants.LEVEL_30_WILDERNESS_LIMIT, EDGEVILLE, DRAYNOR_VILLAGE, KARAMJA_BANANA_PLANTATION, AL_KHARID),
    COMBAT_BRACE  (TeleportConstants.LEVEL_30_WILDERNESS_LIMIT, CHAMPIONS_GUILD, WARRIORS_GUILD, RANGED_GUILD, MONASTRY_EDGE),
    GAMES_NECKLACE  (TeleportConstants.LEVEL_20_WILDERNESS_LIMIT, CORPOREAL_BEAST, BURTHORPE_GAMES_ROOM, WINTERTODT_CAMP, BARBARIAN_OUTPOST),
    DUELING_RING (TeleportConstants.LEVEL_20_WILDERNESS_LIMIT, DUEL_ARENA, CASTLE_WARS, CLAN_WARS),
    ECTOPHIAL (TeleportConstants.LEVEL_20_WILDERNESS_LIMIT, ECTO),
    SKILLS_NECKLACE (TeleportConstants.LEVEL_30_WILDERNESS_LIMIT, FISHING_GUILD, MOTHERLOAD_MINE, CRAFTING_GUILD, COOKING_GUILD, WOOD_CUTTING_GUILD),
    RING_OF_WEALTH (TeleportConstants.LEVEL_30_WILDERNESS_LIMIT, GRAND_EXCHANGE, FALADOR_PARK)
    ;

    private TeleportLocation[] destinations;
    private TeleportLimit teleportLimit;

    TeleportMethod(TeleportLimit teleportLimit, TeleportLocation... destinations){
        this.destinations = destinations;
        this.teleportLimit = teleportLimit;
    }
    
    private static Filter<RSItem> isNotNoted = new Filter<RSItem>(){

		@Override
		public boolean accept(RSItem arg0) {
			RSItemDefinition def = arg0.getDefinition();
			if(def == null)
				return false;
			return !def.isNoted();
		}
    	
    };
    private static boolean inMembersWorld(){
    	return WorldHopper.isMembers(WorldHopper.getWorld());
    }

    private static final Filter<RSItem>
            GLORY_FILTER = isNotNoted.combine(Filters.Items.nameContains("Glory").combine(Filters.Items.nameContains("("), true).combine(Filters.Items.nameNotContains("(t)"), false),false),
            GAMES_FILTER = isNotNoted.combine(Filters.Items.nameContains("Games").combine(Filters.Items.nameContains("("), true),false),
            DUELING_FILTER = isNotNoted.combine(Filters.Items.nameContains("dueling").combine(Filters.Items.nameContains("("), true),false),
            COMBAT_FILTER = isNotNoted.combine(Filters.Items.nameContains("Combat").combine(Filters.Items.nameContains("("), true),false),
            SKILLS_FILTER = isNotNoted.combine(Filters.Items.nameContains("Skills necklace").combine(Filters.Items.nameContains("("), true),false),
            WEALTH_FILTER = isNotNoted.combine(Filters.Items.nameContains("Ring of wealth").combine(Filters.Items.nameContains("("), true),false)
    ;

    public TeleportLocation[] getDestinations() {
        return destinations;
    }

    @Override
    public boolean canUse() {
        switch (this){
            case ECTOPHIAL: return inMembersWorld() && (Inventory.find(Filters.Items.nameContains("Ectophial")).length > 0) && teleportLimit.canCast();
            case VARROCK_TELEPORT: return (Spell.VARROCK_TELEPORT.canUse() || (inMembersWorld() && Inventory.getCount("Varrock teleport") > 0)) && teleportLimit.canCast();
            case LUMBRIDGE_TELEPORT: return (Spell.LUMBRIDGE_TELEPORT.canUse() || (inMembersWorld() && Inventory.getCount("Lumbridge teleport") > 0)) && teleportLimit.canCast();
            case FALADOR_TELEPORT: return (Spell.FALADOR_TELEPORT.canUse() || (inMembersWorld() && Inventory.getCount("Falador teleport") > 0)) && teleportLimit.canCast();
            case CAMELOT_TELEPORT: return (Spell.CAMELOT_TELEPORT.canUse() || (inMembersWorld() && Inventory.getCount("Camelot teleport") > 0)) && teleportLimit.canCast();
            case ARDOUGNE_TELPORT: return (Game.getSetting(165) >= 30 && (Spell.ARDOUGNE_TELEPORT.canUse() || Inventory.getCount("Ardougne teleport") > 0)) && teleportLimit.canCast();
            case GLORY: return inMembersWorld() && (Inventory.find(GLORY_FILTER).length > 0 || Equipment.find(GLORY_FILTER).length > 0) && teleportLimit.canCast();
            case COMBAT_BRACE: return inMembersWorld() && (Inventory.find(COMBAT_FILTER).length > 0 || Equipment.find(COMBAT_FILTER).length > 0) && teleportLimit.canCast();
            case GAMES_NECKLACE: return inMembersWorld() && (Inventory.find(GAMES_FILTER).length > 0 || Equipment.find(GAMES_FILTER).length > 0) && teleportLimit.canCast();
            case DUELING_RING: return inMembersWorld() && (Inventory.find(DUELING_FILTER).length > 0 || Equipment.find(DUELING_FILTER).length > 0) && teleportLimit.canCast();
        }
        return false;
    }

    public boolean use(TeleportLocation teleportLocation){
    	if(Banking.isBankScreenOpen()){
    		if(Banking.close()){
    			Timing.waitCondition(new Condition(){

					@Override
					public boolean active() {
						General.sleep(40,200);
						return !Banking.isBankScreenOpen();
					}
    				
    			},8000);
    		} else{
    			return false;
    		}
    	}
        switch (teleportLocation) {

            case VARROCK_CENTER: return RSItemHelper.click("Varrock t.*", "Break") || Spell.VARROCK_TELEPORT.cast();
            case LUMBRIDGE_CASTLE: return RSItemHelper.click("Lumbridge t.*", "Break") || Spell.LUMBRIDGE_TELEPORT.cast();
            case FALADOR_CENTER: return RSItemHelper.click("Falador t.*", "Break") || Spell.FALADOR_TELEPORT.cast();
            case CAMELOT: return RSItemHelper.click("Camelot t.*", "Break") || Spell.CAMELOT_TELEPORT.cast();
            case ARDOUGNE_MARKET_PLACE: return RSItemHelper.click("Ardougne t.*", "Break") || Spell.ARDOUGNE_TELEPORT.cast();

            case DUEL_ARENA: return teleportWithItem(DUELING_FILTER, "(Duel.*|Al K.*)");
            case CASTLE_WARS: return teleportWithItem(DUELING_FILTER, "Castle War.*");
            case CLAN_WARS: return teleportWithItem(DUELING_FILTER, "Clan Wars.*");

            case AL_KHARID: return teleportWithItem(GLORY_FILTER, "Al .*");
            case EDGEVILLE: return teleportWithItem(GLORY_FILTER, "Edge.*");
            case KARAMJA_BANANA_PLANTATION: return teleportWithItem(GLORY_FILTER, "Karamja.*");
            case DRAYNOR_VILLAGE: return teleportWithItem(GLORY_FILTER, "Draynor.*");

            case BURTHORPE_GAMES_ROOM: return teleportWithItem(GAMES_FILTER, "Burthorpe.*");
            case WINTERTODT_CAMP: return teleportWithItem(GAMES_FILTER, "Winter.*");
            case CORPOREAL_BEAST: return teleportWithItem(GAMES_FILTER, "Corp.*");
            case BARBARIAN_OUTPOST: return teleportWithItem(GAMES_FILTER, "Barb.*");

            case WARRIORS_GUILD: return teleportWithItem(COMBAT_FILTER, "Warrior.*");
            case CHAMPIONS_GUILD: return teleportWithItem(COMBAT_FILTER, "Champ.*");
            case MONASTRY_EDGE: return teleportWithItem(COMBAT_FILTER, "Mona.*");
            case RANGED_GUILD: return teleportWithItem(COMBAT_FILTER, "Rang.*");

            case ECTO: return RSItemHelper.click(Filters.Items.nameContains("Ectophial"), "Empty");

            case FISHING_GUILD: return teleportWithItem(SKILLS_FILTER, "Fishing.*");
            case MOTHERLOAD_MINE: return teleportWithItem(SKILLS_FILTER, "Mother.*");
            case CRAFTING_GUILD: return teleportWithItem(SKILLS_FILTER, "Crafting.*");
            case COOKING_GUILD: return teleportWithItem(SKILLS_FILTER, "Cooking.*");
            case WOOD_CUTTING_GUILD: return teleportWithItem(SKILLS_FILTER, "Woodcutting.*");

            case GRAND_EXCHANGE: return teleportWithItem(WEALTH_FILTER, "Grand.*");
            case FALADOR_PARK: return teleportWithItem(WEALTH_FILTER, "Falad.*");
        }
        return false;
    }


    private static boolean itemAction(String name, String... actions){
        RSItem[] items = Inventory.find(name);
        if (items.length == 0){
            return false;
        }
        return items[0].click(actions);
    }

    private static boolean teleportWithItem(Filter<RSItem> itemFilter, String regex){
        ArrayList<RSItem> items = new ArrayList<>();
        items.addAll(Arrays.asList(Inventory.find(itemFilter)));
        items.addAll(Arrays.asList(Equipment.find(itemFilter)));

        if (items.size() == 0){
            return false;
        }

        RSItem teleportItem = items.get(0);
        if (!RSItemHelper.clickMatch(teleportItem, "(Rub|" + regex + ")")){
            return false;
        }

        RSTile startingPosition = Player.getPosition();
        return WaitFor.condition(General.random(3800, 4600), () -> {
            NPCInteraction.handleConversationRegex(regex);
            if (startingPosition.distanceTo(Player.getPosition()) > 5){
                return WaitFor.Return.SUCCESS;
            }
            return WaitFor.Return.IGNORE;
        }) == WaitFor.Return.SUCCESS;
    }

}
