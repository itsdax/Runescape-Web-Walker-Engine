package dax.teleports;

import dax.walker_engine.interaction_handling.NPCInteraction;
import org.tribot.api.General;
import org.tribot.api2007.Equipment;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Player;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSTile;
import dax.shared.helpers.RSItemHelper;
import dax.walker_engine.WaitFor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;

public class WearableItemTeleport {
	
	private static final Predicate<RSItem> NOT_NOTED = i -> !i.getDefinition().isNoted();

	public static final Predicate<RSItem> RING_OF_WEALTH_FILTER = Filters.Items.nameContains("Ring of wealth (").and(i -> i.getDefinition().getName().matches(".*[1-5]\\)")).and(NOT_NOTED);
	public static final Predicate<RSItem> RING_OF_DUELING_FILTER = Filters.Items.nameContains("Ring of dueling").and(NOT_NOTED);

	public static final Predicate<RSItem> RING_OF_SHADOWS_FILTER = Filters.Items.nameContains("Ring of shadows").and(NOT_NOTED);
	public static final Predicate<RSItem> NECKLACE_OF_PASSAGE_FILTER = Filters.Items.nameContains("Necklace of passage").and(NOT_NOTED);
	public static final Predicate<RSItem> COMBAT_BRACE_FILTER = Filters.Items.nameContains("Combat bracelet(").and(NOT_NOTED);
	public static final Predicate<RSItem> GAMES_NECKLACE_FILTER = Filters.Items.nameContains("Games necklace").and(NOT_NOTED);
	public static final Predicate<RSItem> GLORY_FILTER = Filters.Items.nameContains("glory").and(Filters.Items.nameContains("eternal","(")).and(NOT_NOTED);
	public static final Predicate<RSItem> SKILLS_FILTER = Filters.Items.nameContains("Skills necklace(").and(NOT_NOTED);
	public static final Predicate<RSItem> BURNING_AMULET_FILTER = Filters.Items.nameContains("Burning amulet(").and(NOT_NOTED);
	public static final Predicate<RSItem> DIGSITE_PENDANT_FILTER = Filters.Items.nameContains("Digsite pendant");
	public static final Predicate<RSItem> TELEPORT_CRYSTAL_FILTER = Filters.Items.nameContains("Teleport crystal");
	public static final Predicate<RSItem> XERICS_TALISMAN_FILTER = Filters.Items.nameEquals("Xeric's talisman");
	public static final Predicate<RSItem> RADAS_BLESSING_FILTER = Filters.Items.nameContains("Rada's blessing");
	public static final Predicate<RSItem> CRAFTING_CAPE_FILTER = Filters.Items.nameContains("Crafting cape");
	public static final Predicate<RSItem> EXPLORERS_RING_FILTER = Filters.Items.nameContains("Explorer's ring 3", "Explorer's ring 4");
	public static final Predicate<RSItem> QUEST_CAPE_FILTER = Filters.Items.nameContains("Quest point cape");
	public static final Predicate<RSItem> ARDOUGNE_CLOAK_FILTER = Filters.Items.nameContains("Ardougne cloak");
	public static final Predicate<RSItem> CONSTRUCTION_CAPE_FILTER = Filters.Items.nameContains("Construct. cape");
	public static final Predicate<RSItem> SLAYER_RING = Filters.Items.nameContains("Slayer ring");
	public static final Predicate<RSItem> FARMING_CAPE_FILTER = Filters.Items.nameContains("Farming cape");
	public static final Predicate<RSItem> DRAKANS_MEDALLION_FILTER = Filters.Items.nameEquals("Drakan's medallion");


	private WearableItemTeleport() {

	}

	public static boolean has(Predicate<RSItem> filter) {
		return Inventory.find(filter).length > 0 || Equipment.find(filter).length > 0;
	}

	public static boolean teleport(Predicate<RSItem> filter, String action) {
		return teleportWithItem(filter,action);
	}


	private static boolean teleportWithItem(Predicate<RSItem> itemFilter, String regex) {
		ArrayList<RSItem> items = new ArrayList<>();
		items.addAll(Arrays.asList(Equipment.find(itemFilter)));
		items.addAll(Arrays.asList(Inventory.find(itemFilter)));

		if (items.size() == 0) {
			return false;
		}

		RSItem teleportItem = items.get(0);
		final RSTile startingPosition = Player.getPosition();

		return RSItemHelper.clickMatch(teleportItem, "(Rub|Teleport|" + regex + ")") && WaitFor.condition(
				General.random(3800, 4600), () -> {
					NPCInteraction.handleConversationRegex(regex);
					if (startingPosition.distanceTo(Player.getPosition()) > 5) {
						return WaitFor.Return.SUCCESS;
					}
					return WaitFor.Return.IGNORE;
				}) == WaitFor.Return.SUCCESS;
	}

}