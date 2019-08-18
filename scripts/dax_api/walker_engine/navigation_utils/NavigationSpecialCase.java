package scripts.dax_api.walker_engine.navigation_utils;

import com.google.common.collect.ImmutableSet;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import org.tribot.api.General;
import org.tribot.api.util.Sorting;
import org.tribot.api2007.Equipment;
import org.tribot.api2007.Game;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Player;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Walking;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSObjectDefinition;
import org.tribot.api2007.types.RSTile;
import scripts.dax_api.shared.helpers.RSItemHelper;
import scripts.dax_api.shared.helpers.RSObjectHelper;
import scripts.dax_api.walker.utils.AccurateMouse;
import scripts.dax_api.walker_engine.Loggable;
import scripts.dax_api.walker_engine.WaitFor;
import scripts.dax_api.walker_engine.interaction_handling.InteractionHelper;
import scripts.dax_api.walker_engine.interaction_handling.NPCInteraction;
import static scripts.dax_api.walker_engine.navigation_utils.NavigationSpecialCase.SpecialLocation.*;


public class NavigationSpecialCase implements Loggable
{
	private static NavigationSpecialCase instance = null;

	private NavigationSpecialCase()
	{
	}

	private static NavigationSpecialCase getInstance()
	{
		return instance != null ? instance : (instance = new NavigationSpecialCase());
	}

	@Override
	public String getName()
	{
		return "Navigation Special Case";
	}

	/**
	 * THE ABSOLUTE POSITION
	 */
	public enum SpecialLocation
	{
		RELLEKA_UPPER_PORT(2621, 3688, 0),
		SMALL_PIRATES_COVE_AREA(2213, 3794, 0),

		PIRATE_COVE_SHIP_TILE(2138, 3900, 2),
		CAPTAIN_BENTLY_PIRATES_COVE(2223, 3796, 2),
		CAPTAIN_BENTLY_LUNAR_ISLE(2130, 3899, 2),

		SHANTAY_PASS(3311, 3109, 0),
		UZER(3468, 3110, 0),
		BEDABIN_CAMP(3181, 3043, 0),
		POLLNIVNEACH(3350, 3002, 0),

		SHILO_ENTRANCE(2881, 2953, 0),
		SHILO_INSIDE(2864, 2955, 0),

		RELEKKA_WEST_BOAT(2621, 3682, 0),
		WATERBIRTH(2546, 3760, 0),

		SPIRIT_TREE_GRAND_EXCHANGE(3183, 3508, 0),
		SPIRIT_TREE_STRONGHOLD(2461, 3444, 0),
		SPIRIT_TREE_KHAZARD(2555, 3259, 0),
		SPIRIT_TREE_VILLAGE(2542, 3170, 0),

		GNOME_TREE_GLIDER(GnomeGlider.Location.TA_QUIR_PRIW.getX(), GnomeGlider.Location.TA_QUIR_PRIW.getY(), GnomeGlider.Location.TA_QUIR_PRIW.getZ()),
		AL_KHARID_GLIDER(GnomeGlider.Location.KAR_HEWO.getX(), GnomeGlider.Location.KAR_HEWO.getY(), GnomeGlider.Location.KAR_HEWO.getZ()),
		DIG_SITE_GLIDER(GnomeGlider.Location.LEMANTO_ANDRA.getX(), GnomeGlider.Location.LEMANTO_ANDRA.getY(), GnomeGlider.Location.LEMANTO_ANDRA.getZ()),
		WOLF_MOUNTAIN_GLIDER(GnomeGlider.Location.SINDARPOS.getX(), GnomeGlider.Location.SINDARPOS.getY(), GnomeGlider.Location.SINDARPOS.getZ()),
		GANDIUS_GLIDER(GnomeGlider.Location.GANDIUS.getX(), GnomeGlider.Location.GANDIUS.getY(), GnomeGlider.Location.GANDIUS.getZ()),

		ZANARIS_RING(2452, 4473, 0),
		LUMBRIDGE_ZANARIS_SHED(3201, 3169, 0),

		ROPE_TO_ROCK(2512, 3476, 0),
		FINISHED_ROPE_TO_ROCK(2513, 3468, 0),

		ROPE_TO_TREE(2512, 3466, 0),
		WATERFALL_DUNGEON_ENTRANCE(2511, 3463, 0),

		WATERFALL_LEDGE(2511, 3463, 0),
		WATERFALL_DUNGEON(2575, 9861, 0),
		WATERFALL_FALL_DOWN(2527, 3413, 0),

		KALPHITE_TUNNEL(3226, 3108, 0),
		KALPHITE_TUNNEL_INSIDE(3483, 9510, 2),

		DWARF_CARTS_GE(3141, 3504, 0),
		DWARFS_CARTS_KELDAGRIM(2922, 10170, 0),

		BRIMHAVEN_DUNGEON_SURFACE(2744, 3152, 0),
		BRIMHAVEN_DUNGEON(2713, 9564, 0),

		GNOME_ENTRANCE(2461, 3382, 0), //entrance side
		GNOME_EXIT(2461, 3385, 0), //exit side

		GNOME_SHORTCUT_ELKOY_ENTER(2504, 3191, 0),
		GNOME_SHORTCUT_ELKOY_EXIT(2515, 3160, 0),

		GNOME_TREE_ENTRANCE(2465, 3493, 0), //entrance side
		GNOME_TREE_EXIT(2465, 3493, 0), //exit side

		ZEAH_SAND_CRAB(1784, 3458, 0),
		ZEAH_SAND_CRAB_ISLAND(1778, 3418, 0),

		PORT_SARIM_PAY_FARE(3029, 3217, 0),
		PORT_SARIM_PEST_CONTROL(3041, 3202, 0),
		PORT_SARIM_VEOS(3054, 3245, 0),
		KARAMJA_PAY_FARE(2953, 3146, 0),
		ARDOUGNE_PAY_FARE(2681, 3275, 0),
		BRIMHAVEN_PAY_FARE(2772, 3225, 0),
		GREAT_KOUREND(1824, 3691, 0),
		LANDS_END(1504, 3399, 0),
		PEST_CONTROL(2659, 2676, 0),

		ARDY_LOG_WEST(2598, 3336, 0),
		ARDY_LOG_EAST(2602, 3336, 0),

		GNOME_TREE_DAERO(2482, 3486, 1),
		GNOME_WAYDAR(2649, 4516, 0),
		CRASH_ISLAND(2894, 2726, 0),
		APE_ATOLL_GLIDER_CRASH(2802, 2707, 0),
		GNOME_DROPOFF(2393, 3466, 0),

		HAM_OUTSIDE(3166, 3251, 0),
		HAM_INSIDE(3149, 9652, 0),

		CASTLE_WARS_DOOR_F2P(2444, 3090, 0),
		CLAN_WARS_PORTAL_F2P(3368, 3175, 0);

		int x, y, z;

		SpecialLocation(int x, int y, int z)
		{
			this.x = x;
			this.y = y;
			this.z = z;
		}

		RSTile getRSTile()
		{
			return new RSTile(x, y, z);
		}
	}

	public static SpecialLocation getLocation(RSTile rsTile)
	{
		return Arrays.stream(
			SpecialLocation.values()).filter(tile -> tile.z == rsTile.getPlane()
			&& Point2D.distance(tile.x, tile.y, rsTile.getX(), rsTile.getY()) <= 2)
			.findFirst().orElse(null);
	}

	public static boolean handle(SpecialLocation specialLocation)
	{
		Predicate<RSNPC> pred;
		switch (specialLocation)
		{
			case BRIMHAVEN_DUNGEON:
				if (Game.getSetting(393) != 1)
				{
					if (!InteractionHelper.click(InteractionHelper.getRSNPC(Filters.NPCs.nameEquals("Saniboch")), "Pay"))
					{
						getInstance().log("Could not pay saniboch");
						break;
					}
					NPCInteraction.handleConversation();
					return true;
				}

				if (clickObject(Filters.Objects.nameEquals("Dungeon entrance"), "Enter", () -> Player.getPosition().getY() > 4000 ?
					WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE))
				{
					return true;
				}
				getInstance().log("Could not enter dungeon");
				break;
			case RELLEKA_UPPER_PORT:
			case SMALL_PIRATES_COVE_AREA:
				pred = npc -> npc.getName().contains("Lokar");
				if (!NPCInteraction.talkTo(pred, new String[]{"Travel"}, new String[]{"That's fine, I'm just going to Pirates' Cove."}))
				{
					System.out.println("Was not able to travel with Lokar");
					break;
				}
				WaitFor.milliseconds(3300, 5200);
				break;
			case CAPTAIN_BENTLY_PIRATES_COVE:
			case CAPTAIN_BENTLY_LUNAR_ISLE:
				pred = npc -> npc.getName().contains("Captain");
				if (!NPCInteraction.talkTo(pred, new String[]{"Travel"}, new String[]{}))
				{
					System.out.println("Was not able to travel with Captain");
					break;
				}
				WaitFor.milliseconds(5300, 7200);
				break;
			case SHANTAY_PASS:
			case UZER:
			case BEDABIN_CAMP:
			case POLLNIVNEACH:
				pred = npc -> {
					Set<String> tmp = ImmutableSet.copyOf(npc.getActions());
					return tmp.contains("Travel");
				};

				final String carpetDestination = specialLocation == SHANTAY_PASS ? "Shantay Pass" : specialLocation == UZER ? "Uzer" : specialLocation == BEDABIN_CAMP ? "Bedabin camp" : "Pollnivneach";

				if (NPCInteraction.talkTo(pred, new String[]{"Travel"}, new String[]{carpetDestination}))
				{
					WaitFor.milliseconds(3500, 5000); //wait for board carpet before starting moving condition
					WaitFor.condition(30000, WaitFor.getNotMovingCondition());
					WaitFor.milliseconds(2250, 3250);
					return true;
				}
				break;
			case SHILO_INSIDE:
				pred = npc -> npc.getName().equals("Mosol Rei");
				return NPCInteraction.talkTo(pred, new String[]{"Talk-to"}, new String[]{"Yes, Ok, I'll go into the village!"});
			case RELEKKA_WEST_BOAT:
				pred = npc -> {
					Set<String> tmp = ImmutableSet.copyOf(npc.getActions());
					return tmp.contains("Travel");
				};
				if (NPCInteraction.talkTo(pred, new String[]{"Travel"}, new String[0]))
				{
					WaitFor.milliseconds(2000, 3000);
				}
				break;
			case WATERBIRTH:
				final String option = NPCs.find(Filters.NPCs.nameContains("Jarvald").and(Filters.NPCs.actionsContains("Travel"))).length > 0 ? "Travel" : "Talk-to";
				pred = npc -> npc.getName().equals("Jarvald");
				if (NPCInteraction.talkTo(pred, new String[]{option}, new String[]{
					"What Jarvald is doing.",
					"Can I come?",
					"YES",
					"Yes"
				}))
				{
					WaitFor.milliseconds(2000, 3000);
				}
				break;
			case SPIRIT_TREE_GRAND_EXCHANGE:
				return SpiritTree.to(SpiritTree.Location.SPIRIT_TREE_GRAND_EXCHANGE);
			case SPIRIT_TREE_STRONGHOLD:
				return SpiritTree.to(SpiritTree.Location.SPIRIT_TREE_STRONGHOLD);
			case SPIRIT_TREE_KHAZARD:
				return SpiritTree.to(SpiritTree.Location.SPIRIT_TREE_KHAZARD);
			case SPIRIT_TREE_VILLAGE:
				return SpiritTree.to(SpiritTree.Location.SPIRIT_TREE_VILLAGE);
			case GNOME_TREE_GLIDER:
				return GnomeGlider.to(GnomeGlider.Location.TA_QUIR_PRIW);
			case AL_KHARID_GLIDER:
				return GnomeGlider.to(GnomeGlider.Location.KAR_HEWO);
			case DIG_SITE_GLIDER:
				return GnomeGlider.to(GnomeGlider.Location.LEMANTO_ANDRA);
			case WOLF_MOUNTAIN_GLIDER:
				return GnomeGlider.to(GnomeGlider.Location.SINDARPOS);
			case GANDIUS_GLIDER:
				return GnomeGlider.to(GnomeGlider.Location.GANDIUS);
			case ZANARIS_RING:
				if (Equipment.getCount(772) == 0)
				{
					if (!InteractionHelper.click(InteractionHelper.getRSItem(Filters.Items.idEquals(772)), "Wield"))
					{
						getInstance().log("Could not equip Dramen staff.");
						break;
					}
				}
				if (InteractionHelper.click(InteractionHelper.getRSObject(Filters.Objects.nameEquals("Door")), "Open",
					() -> ZANARIS_RING.getRSTile().distanceTo(Player.getPosition()) < 5 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE))
				{
					return true;
				}
				break;
			case LUMBRIDGE_ZANARIS_SHED:
				if (InteractionHelper.click(InteractionHelper.getRSObject(Filters.Objects.nameEquals("Fairy ring")), "Use",
					() -> LUMBRIDGE_ZANARIS_SHED.getRSTile().distanceTo(Player.getPosition()) < 5 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE))
				{
					return true;
				}
				break;
			case FINISHED_ROPE_TO_ROCK:
				if (RSItemHelper.use(954))
				{
					InteractionHelper.focusCamera(InteractionHelper.getRSObject(Filters.Objects.actionsContains("Swim to")));
					if (InteractionHelper.click(InteractionHelper.getRSObject(Filters.Objects.actionsContains("Swim to")), "Use Rope",
						() -> Player.getPosition().equals(new RSTile(2513, 3468, 0)) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE))
					{
						return true;
					}
				}
				getInstance().log("Could not rope grab to rock.");
				break;
			case WATERFALL_DUNGEON_ENTRANCE:
				if (WATERFALL_DUNGEON.getRSTile().distanceTo(Player.getPosition()) < 500)
				{
					return InteractionHelper.click(InteractionHelper.getRSObject(Filters.Objects.nameEquals("Door")), "Open",
						() -> WATERFALL_DUNGEON_ENTRANCE.getRSTile().distanceTo(Player.getPosition()) < 5 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
				}
				else if (RSItemHelper.use(954))
				{
					if (InteractionHelper.click(InteractionHelper.getRSObject(Filters.Objects.nameContains("Dead tree")), "Use Rope",
						() -> Player.getPosition().equals(new RSTile(2511, 3463, 0)) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE))
					{
						return true;
					}
				}
				getInstance().log("Could not reach entrance to waterfall dungeon.");
				break;
			case WATERFALL_DUNGEON:
				if (InteractionHelper.click(InteractionHelper.getRSObject(Filters.Objects.idEquals(2010)), "Open",
					() -> Player.getPosition().getX() == WATERFALL_DUNGEON.x ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE))
				{
					return true;
				}
				getInstance().log("Failed to get to waterfall dungeon");
				break;
			case WATERFALL_FALL_DOWN:
				if (InteractionHelper.click(InteractionHelper.getRSObject(Filters.Objects.actionsContains("Get in")), "Get in",
					() -> Player.getPosition().distanceTo(new RSTile(2527, 3413, 0)) < 5 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE))
				{
					return true;
				}
				getInstance().log("Failed to fall down waterfall");
				break;
			case KALPHITE_TUNNEL:
			case KALPHITE_TUNNEL_INSIDE:
				if (clickObject(Filters.Objects.nameEquals("Tunnel entrance"), "Climb-down", () -> Player.getPosition().getY() > 4000 ?
					WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE))
				{
					return true;
				}
				final RSObject[] objects = Objects.findNearest(20, "Tunnel entrance");
				if (objects.length > 0 && walkToObject(objects[0]))
				{
					final RSObjectDefinition definition = objects[0].getDefinition();
					final String[] actions = definition != null ? definition.getActions() : null;
					if (actions != null && Arrays.stream(actions).noneMatch(s -> s.startsWith("Climb-down")))
					{
						final RSItem[] items = Inventory.find(954);
						if (items.length > 0 && items[0].click() && clickObject(Filters.Objects.nameEquals("Tunnel entrance"), "Use", () -> WaitFor.Return.SUCCESS))
						{
							WaitFor.milliseconds(3000, 6000);
						}
					}
				}
				getInstance().log("Unable to go inside tunnel.");
				break;
			case DWARF_CARTS_GE:
				final Predicate<RSObject> objPred = obj -> obj.getPosition().getY() == 10171;
				final RSObject[] objs = Objects.find(15, Filters.Objects.nameEquals("Train cart").and(objPred));
				Sorting.sortByDistance(objs, new RSTile(2935, 10172, 0), true);
				if (clickObject(objs[0], "Ride", () -> Player.getPosition().getX() == specialLocation.x ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE))
				{
					getInstance().log("Rode cart to GE");
					return true;
				}
				getInstance().log("Could not ride card to GE.");
				break;
			case BRIMHAVEN_DUNGEON_SURFACE:
				if (clickObject(Filters.Objects.nameEquals("Exit"), "Leave", () -> Player.getPosition().getY() < 8000 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE))
				{
					return true;
				}
				else
				{
					getInstance().log("Failed to exit dungeon.");
				}
				break;
			case GNOME_ENTRANCE:
			case GNOME_EXIT:
				if (clickObject(Filters.Objects.nameEquals("Gate").and(Filters.Objects.actionsContains("Open")), "Open",
					() -> {
						if (NPCInteraction.isConversationWindowUp())
						{
							NPCInteraction.handleConversation(NPCInteraction.GENERAL_RESPONSES);
						}
						return Player.getPosition().getY() == 3383 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE;
					}))
				{
					WaitFor.milliseconds(1060, 1500);
					return true;
				}
				else
				{
					getInstance().log("Could not navigate through gnome door.");
				}
				break;
			case GNOME_SHORTCUT_ELKOY_ENTER:
			case GNOME_SHORTCUT_ELKOY_EXIT:
				pred = npc -> npc.getName().equals("Elkoy");
				if (NPCInteraction.clickNpc(pred, new String[]{"Follow"}))
				{
					final RSTile current = Player.getPosition();
					if (WaitFor.condition(8000, () -> Player.getPosition().distanceTo(current) > 20 ? WaitFor.Return.SUCCESS : WaitFor.Return.FAIL) != WaitFor.Return.SUCCESS)
					{
						return false;
					}
					WaitFor.milliseconds(1000, 2000);
					return true;
				}
				break;
			case GNOME_TREE_ENTRANCE:
			case GNOME_TREE_EXIT:
				if (clickObject(Filters.Objects.nameEquals("Tree Door").and(Filters.Objects.actionsContains("Open")), "Open",
					() -> Player.getPosition().getY() == 3492 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE))
				{
					WaitFor.milliseconds(1060, 1500);
					return true;
				}
				getInstance().log("Could not navigate through gnome door.");
				break;
			case ZEAH_SAND_CRAB:
				if (InteractionHelper.click(InteractionHelper.getRSNPC(Filters.NPCs.nameEquals("Sandicrahb")), "Quick-travel")
					&& WaitFor.condition(10000, () -> Player.getPosition().getY() >= 3457 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS)
				{
					getInstance().log("Paid for travel.");
					return true;
				}
				getInstance().log("Failed to pay travel.");
				break;
			case ZEAH_SAND_CRAB_ISLAND:
				if (InteractionHelper.click(InteractionHelper.getRSNPC(Filters.NPCs.nameEquals("Sandicrahb")), "Quick-travel")
					&& WaitFor.condition(10000, () -> Player.getPosition().getY() < 3457 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS)
				{
					getInstance().log("Paid for travel.");
					return true;
				}
				getInstance().log("Failed to pay travel.");
				break;
			case KARAMJA_PAY_FARE:
				if (handlePayFare("Karamja."))
				{
					getInstance().log("Successfully boarded ship!");
					return true;
				}
				getInstance().log("Failed to pay fare.");
			case PORT_SARIM_PAY_FARE:
				if (handlePayFare("Port Sarim."))
				{
					getInstance().log("Successfully boarded ship!");
					return true;
				}
				getInstance().log("Failed to pay fare.");
			case ARDOUGNE_PAY_FARE:
				if (handlePayFare("Ardougne."))
				{
					getInstance().log("Successfully boarded ship!");
					return true;
				}
				getInstance().log("Failed to pay fare.");
			case BRIMHAVEN_PAY_FARE:
				if (handlePayFare("Brimhaven."))
				{
					getInstance().log("Successfully boarded ship!");
					return true;
				}
				getInstance().log("Failed to pay fare.");
			case PEST_CONTROL:
			case PORT_SARIM_PEST_CONTROL:
				return InteractionHelper.click(InteractionHelper.getRSNPC(Filters.NPCs.actionsContains("Travel").and(Filters.NPCs.nameEquals("Squire"))), "Travel")
					&& WaitFor.condition(10000, () -> ShipUtils.isOnShip() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
			case PORT_SARIM_VEOS:
				return handleZeahBoats("Travel to Port Sarim.");
			case GREAT_KOUREND:
				return handleZeahBoats("Travel to Port Piscarilius.");
			case LANDS_END:
				return handleZeahBoats("Travel to Land's End.");
			case ARDY_LOG_WEST:
			case ARDY_LOG_EAST:
				final RSObject[] logSearch = Objects.findNearest(15,
					Filters.Objects.nameEquals("Log balance").and(Filters.Objects.actionsContains("Walk-across"))
				);
				if (logSearch.length > 0 && AccurateMouse.click(logSearch[0], "Walk-across"))
				{
					int agilityXP = Skills.getXP(Skills.SKILLS.AGILITY);
					if (WaitFor.condition(General.random(7600, 1200), () -> Skills.getXP(Skills.SKILLS.AGILITY) > agilityXP ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS)
					{
						return true;
					}
					if (Player.isMoving())
					{
						WaitFor.milliseconds(1200, 2300);
					}
				}
				getInstance().log("Could not navigate through gnome door.");
				break;
			case GNOME_WAYDAR:
				pred = npc -> npc.getName().equals("Daero");
				if (NPCInteraction.clickNpc(pred, new String[]{"Travel"}))
				{
					if (WaitFor.condition(5000, () -> Player.getPosition().distanceTo(GNOME_WAYDAR.getRSTile()) < 10 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) != WaitFor.Return.SUCCESS)
					{
						break;
					}
					WaitFor.milliseconds(1000, 2000);
					return true;
				}
				break;
			case APE_ATOLL_GLIDER_CRASH:
				pred = npc -> npc.getName().equals("Lumdo");
				if (NPCInteraction.clickNpc(pred, new String[]{"Travel"}))
				{
					if (WaitFor.condition(5000, () -> Player.getPosition().distanceTo(APE_ATOLL_GLIDER_CRASH.getRSTile()) < 10 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) != WaitFor.Return.SUCCESS)
					{
						break;
					}
					WaitFor.milliseconds(1000, 2000);
					return true;
				}
				break;
			case GNOME_DROPOFF:
			case CRASH_ISLAND:
				pred = npc -> npc.getName().equals("Waydar");
				if (NPCInteraction.clickNpc(pred, new String[]{"Travel"}))
				{
					if (WaitFor.condition(5000, () -> Player.getPosition().distanceTo(CRASH_ISLAND.getRSTile()) < 10 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) != WaitFor.Return.SUCCESS)
					{
						break;
					}
					WaitFor.milliseconds(1000, 2000);
					return true;
				}
				break;
			case HAM_OUTSIDE:
				if (clickObject(Filters.Objects.nameEquals("Ladder"), "Climb-up", () -> Player.getPosition().getY() < 4000 ?
					WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE))
				{
					return true;
				}
				break;
			case HAM_INSIDE:
				if (RSObjectHelper.exists(Filters.Objects.actionsContains("Pick-Lock")))
				{
					if (InteractionHelper.click(RSObjectHelper.get(Filters.Objects.actionsContains("Pick-Lock")), "Pick-Lock"))
					{
						WaitFor.condition(WaitFor.random(6000, 9000), () -> !RSObjectHelper.exists(Filters.Objects.actionsContains("Pick-Lock")) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
						return true;
					}
				}
				else
				{
					if (InteractionHelper.click(RSObjectHelper.get(Filters.Objects.actionsContains("Climb-down")), "Climb-down"))
					{
						WaitFor.condition(3000, () -> HAM_INSIDE.getRSTile().distanceTo(Player.getPosition()) < 10 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
						return true;
					}
				}
				break;

			case CASTLE_WARS_DOOR_F2P:
				if (NPCInteraction.isConversationWindowUp() || InteractionHelper.click(RSObjectHelper.get(Filters.Objects.nameEquals("Large door")), "Open"))
				{
					if (WaitFor.condition(10000, () -> NPCInteraction.isConversationWindowUp() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS)
					{
						NPCInteraction.handleConversationRegex("Yes");
						return WaitFor.condition(3000,
							() -> CLAN_WARS_PORTAL_F2P.getRSTile().distanceTo(Player.getPosition()) < 10 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
					}
				}
				break;
			case CLAN_WARS_PORTAL_F2P:
				if (NPCInteraction.isConversationWindowUp() || InteractionHelper.click(RSObjectHelper.get(Filters.Objects.nameEquals("Castle Wars portal")), "Enter"))
				{
					if (WaitFor.condition(10000, () -> NPCInteraction.isConversationWindowUp() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS)
					{
						NPCInteraction.handleConversationRegex("Yes");
						return WaitFor.condition(3000,
							() -> CASTLE_WARS_DOOR_F2P.getRSTile().distanceTo(Player.getPosition()) < 10 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
					}
				}
				break;
		}

		return false;
	}

	private static boolean handleZeahBoats(String locationOption)
	{
		String travelOption = "Travel";
		final RSNPC[] npcs = NPCs.find("Veos", "Captain Magoro");

		if (npcs.length > 0)
		{
			String[] actions = npcs[0].getActions();
			if (actions != null)
			{
				List<String> asList = Arrays.asList(actions);
				if (asList.stream().anyMatch(a -> a.equals("Port Sarim") || a.equals("Land's End")))
				{
					if (locationOption.contains("Port Sarim"))
					{
						travelOption = "Port Sarim";
					}
					else if (locationOption.contains("Piscarilius"))
					{
						travelOption = "Port Piscarilius";
					}
					else if (locationOption.contains("Land"))
					{
						travelOption = "Land's End";
					}
				}
				else if (!asList.contains("Travel"))
				{
					return handleFirstTripToZeah(locationOption);
				}
			}
		}

		final Predicate<RSNPC> pred = npc -> npc.getName().equals("Veos") || npc.getName().equals("Captain Magoro");
		if (NPCInteraction.clickNpc(pred, new String[]{travelOption}))
		{
			final RSTile current = Player.getPosition();
			if (WaitFor.condition(8000, () -> (ShipUtils.isOnShip() || Player.getPosition().distanceTo(current) > 20) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) != WaitFor.Return.SUCCESS)
			{
				return false;
			}
			WaitFor.milliseconds(1800, 2800);
			return true;
		}
		return false;
	}

	private static boolean handleFirstTripToZeah(String locationOption)
	{
		getInstance().log("First trip to zeah");
		final Predicate<RSNPC> pred = npc -> npc.getName().equals("Veos") || npc.getName().equals("Captain Magoro");
		if (NPCInteraction.talkTo(pred, new String[]{"Talk-to"}, new String[]{locationOption, "Can you take me somewhere?", "That's great, can you take me there please?"}))
		{
			final RSTile current = Player.getPosition();
			if (WaitFor.condition(8000, () -> (ShipUtils.isOnShip() || Player.getPosition().distanceTo(current) > 20) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) != WaitFor.Return.SUCCESS)
			{
				return false;
			}
			WaitFor.milliseconds(1800, 2800);
			return true;
		}
		return false;
	}

	private static boolean handlePayFare(String... actions)
	{
		final String[] options = {"Pay-fare", "Pay-Fare"};
		final List<String> all = new ArrayList<>(Arrays.asList(actions));
		all.addAll(Arrays.asList("Yes please.", "Can I journey on this ship?", "Search away, I have nothing to hide.", "Ok."));
		final Predicate<RSNPC> pred = npc -> {
			Set<String> tmp = ImmutableSet.copyOf(npc.getActions());
			return Arrays.stream(options).anyMatch(tmp::contains);
		};
		if (NPCInteraction.talkTo(pred, options, all.toArray(new String[0]))
			&& WaitFor.condition(10000, () -> ShipUtils.isOnShip() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS)
		{
			WaitFor.milliseconds(1800, 2800);
			return true;
		}
		return false;
	}

	private static boolean walkToObject(RSObject object)
	{
		if (!object.isOnScreen() || !object.isClickable())
		{
			Walking.blindWalkTo(object);
			if (WaitFor.isOnScreenAndClickable(object) != WaitFor.Return.SUCCESS)
			{
				return false;
			}
		}
		return object.isOnScreen() && object.isClickable();
	}

	private static boolean clickObject(RSObject object, String action, WaitFor.Condition condition)
	{
		return InteractionHelper.click(object, action, condition);
	}

	private static boolean clickObject(Predicate<RSObject> filter, String action, WaitFor.Condition condition)
	{
		final RSObject[] objects = Objects.findNearest(15, filter);
		if (objects.length == 0)
		{
			return false;
		}
		return clickObject(objects[0], action, condition);
	}

}
