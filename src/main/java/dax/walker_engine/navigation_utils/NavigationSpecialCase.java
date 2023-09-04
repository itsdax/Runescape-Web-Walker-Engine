package dax.walker_engine.navigation_utils;

import dax.shared.helpers.RSItemHelper;
import dax.shared.helpers.RSObjectHelper;
import dax.walker.utils.AccurateMouse;
import dax.walker_engine.Loggable;
import dax.walker_engine.WaitFor;
import dax.walker_engine.interaction_handling.DoomsToggle;
import dax.walker_engine.interaction_handling.InteractionHelper;
import dax.walker_engine.interaction_handling.NPCInteraction;
import dax.walker_engine.navigation_utils.fairyring.FairyRing;
import org.tribot.api.General;
import org.tribot.api.ScriptCache;
import org.tribot.api.types.generic.Filter;
import org.tribot.api.util.Sorting;
import org.tribot.api2007.*;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import static dax.walker_engine.navigation_utils.NavigationSpecialCase.SpecialLocation.*;


public class NavigationSpecialCase implements Loggable {

    private NavigationSpecialCase(){

    }

    private static NavigationSpecialCase getInstance(){
        return (NavigationSpecialCase) ScriptCache.get().computeIfAbsent("DaxWalker.NavigationSpecialCase", k -> new NavigationSpecialCase());
    }

    @Override
    public String getName() {
        return "Navigation Special Case";
    }

    /**
     * THE ABSOLUTE POSITION
     */
    public enum SpecialLocation {


        RELLEKA_UPPER_PORT (2621, 3688, 0),
        SMALL_PIRATES_COVE_AREA (2213, 3794, 0),

        PIRATE_COVE_SHIP_TILE (2138, 3900, 2),
        CAPTAIN_BENTLY_PIRATES_COVE (2223, 3796, 2),
        CAPTAIN_BENTLY_LUNAR_ISLE (2130, 3899, 2),

        SHANTAY_PASS(3311, 3109, 0),
        UZER (3468, 3110, 0),
        BEDABIN_CAMP (3181, 3043, 0),
        POLLNIVNEACH_NORTH(3350, 3002, 0),
        POLLNIVNEACH_SOUTH(3352, 2941, 0),
        NARDAH(3400, 2917, 0),

        SHILO_ENTRANCE (2881, 2953, 0),
        SHILO_INSIDE (2864, 2955, 0),

        RELLEKKA_WEST_BOAT(2621, 3682, 0),
        WATERBIRTH (2546, 3760, 0),

        SPIRIT_TREE_GRAND_EXCHANGE (3183, 3508, 0),
        SPIRIT_TREE_STRONGHOLD (2461, 3444, 0),
        SPIRIT_TREE_KHAZARD (2555, 3259, 0),
        SPIRIT_TREE_VILLAGE (2542, 3170, 0),
        SPIRIT_TREE_FELDIP(2488, 2850, 0),
        SPIRIT_TREE_PRIF (3274, 6123, 0),


        GNOME_TREE_GLIDER (GnomeGlider.Location.TA_QUIR_PRIW.getX(), GnomeGlider.Location.TA_QUIR_PRIW.getY(), GnomeGlider.Location.TA_QUIR_PRIW.getZ()),
        AL_KHARID_GLIDER (
		        GnomeGlider.Location.KAR_HEWO.getX(), GnomeGlider.Location.KAR_HEWO.getY(), GnomeGlider.Location.KAR_HEWO.getZ()),
        DIG_SITE_GLIDER (GnomeGlider.Location.LEMANTO_ANDRA.getX(), GnomeGlider.Location.LEMANTO_ANDRA.getY(), GnomeGlider.Location.LEMANTO_ANDRA.getZ()),
        WOLF_MOUNTAIN_GLIDER (
		        GnomeGlider.Location.SINDARPOS.getX(), GnomeGlider.Location.SINDARPOS.getY(), GnomeGlider.Location.SINDARPOS.getZ()),
        GANDIUS_GLIDER (
		        GnomeGlider.Location.GANDIUS.getX(), GnomeGlider.Location.GANDIUS.getY(), GnomeGlider.Location.GANDIUS.getZ()),

        ZANARIS_RING (2452, 4473, 0),
        LUMBRIDGE_ZANARIS_SHED (3201, 3169, 0),

        ROPE_TO_ROCK (2512, 3476, 0),
        FINISHED_ROPE_TO_ROCK (2513, 3468, 0),

        ROPE_TO_TREE (2512, 3466, 0),
        WATERFALL_DUNGEON_ENTRANCE(2511, 3463, 0),

        WATERFALL_LEDGE (2511, 3463, 0),
        WATERFALL_DUNGEON (2575, 9861, 0),
        WATERFALL_FALL_DOWN (2527, 3413, 0),

        KALPHITE_TUNNEL (3226, 3108, 0),
        KALPHITE_TUNNEL_INSIDE (3483, 9510, 2),

        DWARF_CARTS_GE (3141, 3504, 0),
        DWARFS_CARTS_KELDAGRIM (2922, 10170, 0),

        BRIMHAVEN_DUNGEON_SURFACE (2744, 3152, 0),
        BRIMHAVEN_DUNGEON (2713, 9564, 0),

        GNOME_ENTRANCE (2461, 3382, 0), //entrance side
        GNOME_EXIT (2461, 3385, 0), //exit side

        GNOME_SHORTCUT_ELKOY_ENTER (2504, 3191, 0),
        GNOME_SHORTCUT_ELKOY_EXIT (2515, 3160, 0),

        GNOME_TREE_ENTRANCE (2465, 3493, 0), //entrance side
        GNOME_TREE_EXIT (2465, 3493, 0), //exit side

        ZEAH_SAND_CRAB (1784, 3458, 0),
        ZEAH_SAND_CRAB_ISLAND (1778, 3418, 0),

        PORT_SARIM_PAY_FARE (3029, 3217, 0),
        PORT_SARIM_PEST_CONTROL (3041, 3202, 0),
        PORT_SARIM_VEOS (3054, 3245, 0),
        KARAMJA_PAY_FARE (2953, 3146, 0),
        ARDOUGNE_PAY_FARE (2681, 3275, 0),
        BRIMHAVEN_PAY_FARE (2772, 3225, 0),
        RIMMINGTON_PAY_FARE(2915, 3225, 0),
        GREAT_KOUREND (1824, 3691, 0),
        LANDS_END (1504, 3399, 0),
        PEST_CONTROL (2659, 2676, 0),

        ARDY_LOG_WEST (2598, 3336, 0),
        ARDY_LOG_EAST (2602, 3336, 0),

        GNOME_TREE_DAERO (2482, 3486, 1),
        GNOME_WAYDAR (2649, 4516, 0),
        CRASH_ISLAND (2894, 2726, 0),
        APE_ATOLL_GLIDER_CRASH (2802, 2707, 0),
        GNOME_DROPOFF (2393, 3466, 0),

        HAM_OUTSIDE (3166, 3251, 0),
        HAM_INSIDE (3149, 9652, 0),

        CASTLE_WARS_DOOR(2444, 3090, 0),
        FEROX_ENCLAVE_PORTAL_F2P(3141, 3627, 0),

        FOSSIL_ISLAND_BARGE(3362, 3445, 0),
//        DIGSITE_BARGE(3724, 3808, 0),

        PORT_SARIM_TO_ENTRANA(3048, 3234, 0),
        ENTRANA_TO_PORT_SARIM(2834, 3335, 0),

        RELLEKKA_TO_MISCELLANIA(2629, 3693, 0),
        MISCELLANIA_TO_RELLEKKA(2577, 3853, 0),

        FAIRY_RING_ABYSSAL_AREA(3059, 4875, 0),
        FAIRY_RING_ABYSSAL_NEXUS(3037, 4763, 0),
        FAIRY_RING_APE_ATOLL(2740, 2738, 0),
        FAIRY_RING_ARCEUUS_LIBRARY(1639, 3868, 0),
        FAIRY_RING_ARDOUGNE_ZOO(2635, 3266, 0),
        FAIRY_RING_CANIFIS(3447, 3470, 0),
        FAIRY_RING_CHASM_OF_FIRE(1455, 3658, 0),
        FAIRY_RING_COSMIC_ENTITYS_PLANE(2075, 4848, 0),
        FAIRY_RING_DRAYNOR_VILLAGE_ISLAND(3082, 3206, 0),
        FAIRY_RING_EDGEVILLE(3129, 3496, 0),
        FAIRY_RING_ENCHANTED_VALLEY(3041, 4532, 0),
        FAIRY_RING_FELDIP_HILLS_HUNTER_AREA(2571, 2956, 0),
        FAIRY_RING_FISHER_KINGS_REALM(2650, 4730, 0),
        FAIRY_RING_GORAKS_PLANE(3038, 5348, 0),
        FAIRY_RING_HAUNTED_WOODS(3597, 3495, 0),
        FAIRY_RING_HAZELMERE(2682, 3081, 0),
        FAIRY_RING_ISLAND_SOUTHEAST_ARDOUGNE(2700, 3247, 0),
        FAIRY_RING_KALPHITE_HIVE(3251, 3095, 0),
        FAIRY_RING_KARAMJA_KARAMBWAN_SPOT(2900, 3111, 0),
        FAIRY_RING_LEGENDS_GUILD(2740, 3351, 0),
        FAIRY_RING_LIGHTHOUSE(2503, 3636, 0),
        FAIRY_RING_MCGRUBOR_WOODS(2644, 3495, 0),
        FAIRY_RING_MISCELLANIA(2513, 3884, 0),
        FAIRY_RING_MISCELLANIA_PENGUINS(2500, 3896, 0),
        FAIRY_RING_MORT_MYRE_ISLAND(3410, 3324, 0),
        FAIRY_RING_MORT_MYRE_SWAMP(3469, 3431, 0),
        FAIRY_RING_MOUNT_KARUULM(1302, 3762, 0),
        FAIRY_RING_MUDSKIPPER_POINT(2996, 3114, 0),
        FAIRY_RING_MYREQUE_HIDEOUT(3447, 9824, 0),
        FAIRY_RING_NORTH_OF_NARDAH(3423, 3016, 0),
        FAIRY_RING_PISCATORIS_HUNTER_AREA(2319, 3619, 0),
        FAIRY_RING_POISON_WASTE(2213, 3099, 0),
        FAIRY_RING_POLAR_HUNTER_AREA(2744, 3719, 0),
        FAIRY_RING_RELLEKKA_SLAYER_CAVE(2780, 3613, 0),
        FAIRY_RING_SHILO_VILLAGE(2801, 3003, 0),
        FAIRY_RING_SINCLAIR_MANSION(2705, 3576, 0),
        FAIRY_RING_SOUTH_CASTLE_WARS(2385, 3035, 0),
        FAIRY_RING_STRANGLEWOOD(1295, 3493, 0),
        FAIRY_RING_TOWER_OF_LIFE(2658, 3230, 0),
        FAIRY_RING_TZHAAR(2437, 5126, 0),
        FAIRY_RING_WIZARDS_TOWER(3108, 3149, 0),
        FAIRY_RING_YANILLE(2528, 3127, 0),
        FAIRY_RING_ZANARIS(2412, 4434, 0),
        FAIRY_RING_ZUL_ANDRA(2150, 3070, 0),

        FOSSIL_ISLAND_FERRY_NORTH(3734, 3893, 0),
        FOSSIL_ISLAND_FERRY_CAMP(3724, 3808, 0),
        FOSSIL_ISLAND_FERRY_ISLAND(3769, 3898, 0),

        WITCHHAVEN_FERRY(2720, 3303, 0),
        FISHING_PLATFORM_FERRY(2785, 3275, 0),

        RELLEKKA_DOCK_FROM_ISLES(2645, 3710, 0),
        JATIZSO_DOCK(2418, 3782, 0),
        NEITIZNOT_DOCK(2311, 3781, 0),

        OBSERVATORY_OUTSIDE(2449, 3155, 0),
        OBSERVATORY_INSIDE(2444, 3165, 0),

        MOSS_GIANT_ISLAND_ROPE(2709, 3209, 0),
        MOSS_GIANT_ISLAND_ROPE_LANDING(2704, 3209, 0),

        SHANTAY_PASS_ENTRANCE(3304, 3117, 0),
        SHANTAY_PASS_EXIT(3304, 3115, 0),

        PATERDOMUS_EAST_EXIT(3423, 3485, 0),
        PATERDOMUS_EAST_ENTRANCE(3440, 9887, 0),

        SWAMP_BOATY(3500, 3380, 0),
        SWAMP_BOATY_MORTTON(3522, 3285, 0),

        BRINE_RAT_CAVE_TREE(2748, 3733, 0),
        BRINE_RAT_CAVE_ENTER(2697, 10120, 0),

        FERRY_AL_KHARID_TO_UNKAH(3148, 2842, 0),
        FERRY_UNKAH_TO_AL_KHARID(3271, 3144, 0),

        UNKAH_SHANTAY_PASS_SOUTH_ENTRANCE(3167, 2819, 0),
        UNKAH_SHANTAY_PASS_SOUTH_EXIT(3167, 2816, 0),
        UNKAH_SHANTAY_PASS_EAST_ENTRANCE(3193, 2842, 0),
        UNKAH_SHANTAY_PASS_EAST_EXIT(3196, 2842, 0),

        YANILLE_BALANCE_EDGE_NORTH(2580, 9520, 0),
        YANILLE_BALANCE_EDGE_SOUTH(2580, 9512, 0),
        YANILLE_MONKEY_BARS_WEST(2572, 9506, 0),
        YANILLE_MONKEY_BARS_EAST(2578, 9506, 0),

        LIZARDMAN_CANYON_EAST(1475, 3687, 0),
        LIZARDMAN_CANYON_WEST(1470, 3687, 0),

        LIZARDMAN_SHAMANS_CANYON_EAST(1460, 3690, 0),
        LIZARDMAN_SHAMANS_CANYON_WEST(1456, 3690, 0),

        SLEPE_BOAT_FROM_PORT(3671, 3542, 0),
        SLEPE_BOAT_FROM_SLEPE(3661, 3278, 0),

        SOUL_WARS_PORTAL(2206, 2858, 0),
        FEROX_ENCLAVE_PORTAL_TO_ISLE_OF_SOULS(3158, 10027, 0),
        EDGEVILLE_PORTAL_TO_ISLE_OF_SOULS(3081, 3475, 0),
	    
        KBD_LAIR(2271, 4680, 0),
        KBD_LAIR_LOBBY(3067, 10253, 0),

        GAMES_ROOM_TOP(2898, 3565, 0),
        GAMES_ROOM_MIDDLE(2207, 4934, 1),
        GAMES_ROOM_BOTTOM(2207, 4938, 0),

        MOLCH_NORTHERN_CAVE_ENTRANCE(1312, 3685, 0),
        MOLCH_NORTHERN_CAVE_DROPDOWN(1312, 10086, 0),
        MOLCH_SOUTHERN_CAVE_ENTRANCE(1293, 3659, 0),
        MOLCH_SOUTHERN_CAVE_DROPDOWN(1292, 10058, 0),

        BOATY_MOLCH_ISLAND(1369, 3639, 0),
        BOATY_SHAYZIEN(1408, 3612, 0),
        BOATY_BATTLEFRONT(1384, 3665, 0),
        BOATY_MOLCH(1342, 3645, 0),

        MORT_MYRE_BRIDGE_N(3502, 3432, 0),
        MORT_MYRE_BRIDGE_S(3502, 3425, 0),

        BOATY_SLEPE(3661, 3277, 0),
        BOATY_ICYENE_GRAVEYARD(3685, 3174, 0),
        BOATY_BURGH(3525, 3170, 0),

        LIGHTHOUSE_LADDER(2510, 3644, 0),
        LIGHTHOUSE_UNDERGROUND(2518, 9994, 0),

        LOKAR_SEARUNNER_RELLEKKA(2620, 3692, 0),
        LOKAR_SEARUNNER_PIRATES_COVE(2213, 3794, 0),

        CAPTAIN_BENTLEY_PIRATES_COVE(2222, 3797, 2),
        CAPTAIN_BENTLEY_LUNAR_ISLE(2138, 3899, 2),

        PRIF_MINE_INSIDE(3302, 12454, 0),
        PRIF_MINE_OUTSIDE(3271, 6051, 0),

        SHILO_CART_FROM_BRIMHAVEN(2777, 3214, 0),
        SHILO_CART_FROM_SHILO(2834, 2951, 0),

        KILLERWAT_PLANE_ENTRANCE(3110, 3363, 2),
        KILLERWAT_PLANE_EXIT(2677, 5214, 2),

        STRANGLEWOOD_ROWBOAT_ENTRANCE(1194, 3452, 0),
        STRANGLEWOOD_ROWBOAT_EXIT(1229, 3470, 0),

        STRANGLEWOOD_ENTRY_ENTRANCE(1146, 3433, 0),
        STRANGLEWOOD_ENTRY_EXIT(1149,3444,0),
        ;

        int x, y, z;
        SpecialLocation(int x, int y, int z){
            this.x = x;
            this.y = y;
            this.z = z;
        }

        RSTile getRSTile(){
            return new RSTile(x, y, z);
        }
    }


    public static SpecialLocation getLocation(RSTile rsTile){
        return Arrays.stream(
                SpecialLocation.values()).filter(tile -> tile.z == rsTile.getPlane()
                && tile.getRSTile().distanceTo(rsTile) <= 2).min(Comparator.comparingInt(t -> t.getRSTile().distanceTo(rsTile))).orElse(null);
    }


    /**
     * action for getting to the case
     * @param specialLocation
     * @return
     */
    public static boolean handle(SpecialLocation specialLocation){
        switch (specialLocation){

            case BRIMHAVEN_DUNGEON:
                if (RSVarBit.get(5628).getValue() == 0 && RSVarBit.get(8122).getValue() == 0){
                    if (!clickObject(Filters.Objects.nameEquals("Dungeon entrance"), "Enter", () -> NPCInteraction.isConversationWindowUp() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)) {
                        getInstance().log("Could not pay to enter the dungeon");
                        break;
                    }
                    NPCInteraction.handleConversation("Yes","Pay 875 coins to enter once");
                    return true;
                } else {
                    if (clickObject(Filters.Objects.nameEquals("Dungeon entrance"), "Enter", () -> Player.getPosition().getY() > 4000 ?
                            WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)){
                        return true;
                    } else {
                        getInstance().log("Could not enter dungeon");
                    }
                }
                break;

            case RELLEKA_UPPER_PORT:
            case SMALL_PIRATES_COVE_AREA:
                if (!NPCInteraction.talkTo(Filters.NPCs.nameContains("Lokar"), new String[]{"Travel"}, new String[]{
                    "That's fine, I'm just going to Pirates' Cove."})){
                    System.out.println("Was not able to travel with Lokar");
                    break;
                }
                WaitFor.milliseconds(3300, 5200);
                break;
            case CAPTAIN_BENTLY_PIRATES_COVE:
            case CAPTAIN_BENTLY_LUNAR_ISLE:
                if (!NPCInteraction.talkTo(Filters.NPCs.nameContains("Captain"), new String[]{"Travel"}, new String[]{})){
                    System.out.println("Was not able to travel with Captain");
                    break;
                }
                WaitFor.milliseconds(5300, 7200);
                break;
            case SHANTAY_PASS:
                handleCarpetRide("Shantay Pass");
                break;
            case UZER:
                handleCarpetRide("Uzer");
                break;
            case BEDABIN_CAMP:
                handleCarpetRide("Bedabin camp");
                break;
            case POLLNIVNEACH_NORTH:
            case POLLNIVNEACH_SOUTH:
                handleCarpetRide("Pollnivneach");
                break;
            case NARDAH:
                handleCarpetRide("Nardah");
                break;


            case SHILO_ENTRANCE: break;
            case SHILO_INSIDE: return NPCInteraction.talkTo(Filters.NPCs.nameEquals("Mosol Rei"), new String[]{"Talk-to"}, new String[]{"Yes, Ok, I'll go into the village!"});

            case MISCELLANIA_TO_RELLEKKA:
            case RELLEKKA_TO_MISCELLANIA:
                final RSTile curr = Player.getPosition();
                if (NPCInteraction.clickNpc(Filters.NPCs.actionsEquals("Rellekka","Miscellania"), "Rellekka","Miscellania")){
                    WaitFor.condition(10000,() -> Player.getPosition().distanceTo(curr) > 20 ?
                        WaitFor.Return.SUCCESS :
                        WaitFor.Return.IGNORE);
                    WaitFor.milliseconds(4000, 5000);
                }
                break;

            case RELLEKKA_WEST_BOAT:
                RSNPC[] jarvald = NPCs.find(Filters.NPCs.nameEquals("Jarvald"));
                if(jarvald.length == 0)
                    return false;
                boolean success;
                if(Filters.NPCs.actionsContains("Rellekka").test(jarvald[0])){
                    success = NPCInteraction.clickNpc(jarvald[0], "Rellekka");
                } else {
                    success = NPCInteraction.talkTo(Filters.NPCs.nameEquals("Jarvald"), new String[]{"Talk-to"}, new String[]{
                            "What Jarvald is doing.",
                            "Can I come?",
                            "YES",
                            "Yes"
                    });
                }
                if(success){
                    return WaitFor.condition(10000, () -> Player.getPosition().distanceTo(RELLEKKA_WEST_BOAT.getRSTile()) < 10
                            ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
                }
                break;
            case WATERBIRTH:
                jarvald = NPCs.find(Filters.NPCs.nameEquals("Jarvald"));
                if(jarvald.length == 0)
                    return false;
                if(Filters.NPCs.actionsContains("Waterbirth Island").test(jarvald[0])){
                    success = NPCInteraction.clickNpc(jarvald[0], "Waterbirth Island");
                } else {
                    success = NPCInteraction.talkTo(Filters.NPCs.nameEquals("Jarvald"), new String[]{"Talk-to"}, new String[]{
                            "What Jarvald is doing.",
                            "Can I come?",
                            "YES",
                            "Yes"
                    });
                }
                if(success){
                    return WaitFor.condition(10000, () -> Player.getPosition().distanceTo(WATERBIRTH.getRSTile()) < 10
                            ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
                }
                break;

            case SPIRIT_TREE_GRAND_EXCHANGE: return SpiritTree.to(SpiritTree.Location.SPIRIT_TREE_GRAND_EXCHANGE);
            case SPIRIT_TREE_STRONGHOLD: return SpiritTree.to(SpiritTree.Location.SPIRIT_TREE_STRONGHOLD);
            case SPIRIT_TREE_KHAZARD: return SpiritTree.to(SpiritTree.Location.SPIRIT_TREE_KHAZARD);
            case SPIRIT_TREE_VILLAGE: return SpiritTree.to(SpiritTree.Location.SPIRIT_TREE_VILLAGE);
            case SPIRIT_TREE_FELDIP: return SpiritTree.to(SpiritTree.Location.SPIRIT_TREE_FELDIP);
            case SPIRIT_TREE_PRIF: return SpiritTree.to(SpiritTree.Location.SPIRIT_TREE_PRIFDDINAS);

            case GNOME_TREE_GLIDER: return GnomeGlider.to(GnomeGlider.Location.TA_QUIR_PRIW);
            case AL_KHARID_GLIDER: return GnomeGlider.to(GnomeGlider.Location.KAR_HEWO);
            case DIG_SITE_GLIDER: return GnomeGlider.to(GnomeGlider.Location.LEMANTO_ANDRA);
            case WOLF_MOUNTAIN_GLIDER: return GnomeGlider.to(GnomeGlider.Location.SINDARPOS);
            case GANDIUS_GLIDER: return GnomeGlider.to(GnomeGlider.Location.GANDIUS);

            case ZANARIS_RING:
                if (Equipment.getCount(FairyRing.DRAMEN_STAFFS) == 0){
                    if (!InteractionHelper.click(InteractionHelper.getRSItem(Filters.Items.idEquals(FairyRing.DRAMEN_STAFFS)), "Wield")){
                        getInstance().log("Could not equip Dramen staff.");
                        break;
                    }
                }
                if (InteractionHelper.click(
		                InteractionHelper.getRSObject(Filters.Objects.nameEquals("Door")), "Open", () -> ZANARIS_RING.getRSTile().distanceTo(Player.getPosition()) < 5 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)){
                    return true;
                }
                break;
            case LUMBRIDGE_ZANARIS_SHED:
                if (InteractionHelper.click(InteractionHelper.getRSObject(Filters.Objects.nameEquals("Fairy ring")),
		                "Use", () -> LUMBRIDGE_ZANARIS_SHED.getRSTile().distanceTo(Player.getPosition()) < 5 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)){
                    return true;
                }
                break;

            case ROPE_TO_ROCK:
                break;
            case FINISHED_ROPE_TO_ROCK:
                if (RSItemHelper.use(954)){
                    InteractionHelper.focusCamera(
		                    InteractionHelper.getRSObject(Filters.Objects.actionsContains("Swim to")));
                    if (InteractionHelper.click(
		                    InteractionHelper.getRSObject(Filters.Objects.actionsContains("Swim to")), "Use Rope", () -> Player.getPosition().equals(new RSTile(2513, 3468, 0)) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)){
                        return true;
                    }
                }
                getInstance().log("Could not rope grab to rock.");
                break;

            case ROPE_TO_TREE:
                break;
            case WATERFALL_DUNGEON_ENTRANCE:
                if (WATERFALL_DUNGEON.getRSTile().distanceTo(Player.getPosition()) < 500){
                    return InteractionHelper.click(InteractionHelper.getRSObject(Filters.Objects.nameEquals("Door")), "Open", () -> WATERFALL_DUNGEON_ENTRANCE.getRSTile().distanceTo(Player.getPosition()) < 5 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
                } else if (RSItemHelper.use(954)){
                    if (InteractionHelper.click(InteractionHelper.getRSObject(Filters.Objects.nameContains("Dead tree")), "Use Rope", () -> Player.getPosition().equals(new RSTile(2511, 3463, 0)) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)){
                        return true;
                    }
                }
                getInstance().log("Could not reach entrance to waterfall dungeon.");
                break;

            case WATERFALL_LEDGE:
                break;

            case WATERFALL_DUNGEON:
                if (InteractionHelper.click(
		                InteractionHelper.getRSObject(Filters.Objects.idEquals(2010)), "Open", () -> Player.getPosition().getX() == WATERFALL_DUNGEON.x ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)){
                    return true;
                }
                getInstance().log("Failed to get to waterfall dungeon");
                break;
            case WATERFALL_FALL_DOWN:
                if (InteractionHelper.click(InteractionHelper.getRSObject(Filters.Objects.actionsContains("Get in")), "Get in", () -> Player.getPosition().distanceTo(new RSTile(2527, 3413, 0)) < 5 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)){
                    return true;
                }
                getInstance().log("Failed to fall down waterfall");
                break;

            case KALPHITE_TUNNEL:
                if (clickObject(Filters.Objects.nameEquals("Rope"), "Climb-up", () -> Player.getPosition().getY() < 9000 ?
                        WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)) {
                    return true;
                }
                break;
            case KALPHITE_TUNNEL_INSIDE:
                if (clickObject(Filters.Objects.nameEquals("Tunnel entrance").and(Filters.Objects.actionsEquals("Climb-down")), "Climb-down", () -> Player.getPosition().getY() > 4000 ?
                        WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)){
                    return true;
                } else {
                    RSObject[] objects = Objects.findNearest(20, "Tunnel entrance");
                    if (objects.length > 0 && walkToObject(objects[0])){
                        RSObjectDefinition definition = objects[0].getDefinition();
                        String[] actions = definition != null ? definition.getActions() : null;
                        if (actions != null && Arrays.stream(actions).noneMatch(s -> s.startsWith("Climb-down"))){
                            RSItem[] items = Inventory.find(954);
                            if (items.length > 0 && items[0].click() && clickObject(Filters.Objects.nameEquals("Tunnel entrance"), "Use", () -> WaitFor.Return.SUCCESS)){
                                WaitFor.milliseconds(3000, 6000);
                            }
                        }
                    }
                }
                getInstance().log("Unable to go inside tunnel.");
                break;
            case DWARF_CARTS_GE:
                RSObject[] objects = Objects.find(15, Filters.Objects.nameEquals("Train cart").combine(new Filter<RSObject>() {
                    @Override
                    public boolean accept(RSObject rsObject) {
                        return rsObject.getPosition().getY() == 10171;
                    }
                }, true));
                Sorting.sortByDistance(objects, new RSTile(2935, 10172, 0), true);
                if (objects.length > 0 && clickObject(objects[0], "Ride", () -> Player.getPosition().getX() == specialLocation.x ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)){
                    getInstance().log("Rode cart to GE");
                    return true;
                } else {
                    getInstance().log("Could not ride card to GE.");
                }

                break;

            case DWARFS_CARTS_KELDAGRIM:
                break;

            case BRIMHAVEN_DUNGEON_SURFACE:
                if (clickObject(Filters.Objects.nameEquals("Exit"), "Leave", () -> Player.getPosition().getY() < 8000 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)){
                    return true;
                } else {
                    getInstance().log("Failed to exit dungeon.");
                }
                break;

            case GNOME_ENTRANCE:
            case GNOME_EXIT:
                if (clickObject(Filters.Objects.nameEquals("Gate").combine(Filters.Objects.actionsContains("Open"),
		                true), "Open",
                        () -> {
                            if (NPCInteraction.isConversationWindowUp()) {
                                NPCInteraction.handleConversation(NPCInteraction.GENERAL_RESPONSES);
                            }
                            return Player.getPosition().getY() == 3383 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE;
                        })){
                    WaitFor.milliseconds(1060, 1500);
                    return true;
                } else {
                    getInstance().log("Could not navigate through gnome door.");
                }
                break;

            case GNOME_SHORTCUT_ELKOY_ENTER:
            case GNOME_SHORTCUT_ELKOY_EXIT:
                if (NPCInteraction.clickNpc(Filters.NPCs.nameEquals("Elkoy"), new String[]{"Follow"})){
                    RSTile current = Player.getPosition();
                    if(WaitFor.condition(8000, () ->  Player.getPosition().distanceTo(current) > 20 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) != WaitFor.Return.SUCCESS){
                        return false;
                    }
                    WaitFor.milliseconds(1000, 2000);
                    return true;
                }
                break;

            case GNOME_TREE_ENTRANCE:
            case GNOME_TREE_EXIT:
                if (clickObject(Filters.Objects.nameEquals("Tree Door").combine(Filters.Objects.actionsContains("Open"), true), "Open",
                        () -> Player.getPosition().getY() == 3492 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)){
                    WaitFor.milliseconds(1060, 1500);
                    return true;
                } else {
                    getInstance().log("Could not navigate through gnome door.");
                }

                break;

            case ZEAH_SAND_CRAB:
                if (InteractionHelper.click(InteractionHelper.getRSNPC(Filters.NPCs.nameEquals("Sandicrahb")), "Travel") && WaitFor.condition(10000, () -> Player.getPosition().getY() >= 3457 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS){
                    getInstance().log("Paid for travel.");
                    return true;
                } else {
                    getInstance().log("Failed to pay travel.");
                }
                break;
            case ZEAH_SAND_CRAB_ISLAND:
                if (InteractionHelper.click(InteractionHelper.getRSNPC(Filters.NPCs.nameEquals("Sandicrahb")), "Travel") && WaitFor.condition(10000, () -> Player.getPosition().getY() < 3457 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS){
                    getInstance().log("Paid for travel.");
                    return true;
                } else {
                    getInstance().log("Failed to pay travel.");
                }
                break;


            case KARAMJA_PAY_FARE:
            case PORT_SARIM_PAY_FARE:

                if (handleKaramjaShip()){
                    getInstance().log("Successfully boarded ship!");
                    return true;
                } else {
                    getInstance().log("Failed to pay fare.");
                }
                return false;
            case ARDOUGNE_PAY_FARE:
                if (handleShip("Ardougne")){
                    getInstance().log("Successfully boarded ship!");
                    return true;
                } else {
                    getInstance().log("Failed to pay fare.");
                }
                return false;
            case BRIMHAVEN_PAY_FARE:
                if (handleShip("Brimhaven")){
                    getInstance().log("Successfully boarded ship!");
                    return true;
                } else {
                    getInstance().log("Failed to pay fare.");
                }
                return false;
            case RIMMINGTON_PAY_FARE:
                if (handleShip("Rimmington")){
                    getInstance().log("Successfully boarded ship!");
                    return true;
                } else {
                    getInstance().log("Failed to pay fare.");
                }
                return false;
            case PEST_CONTROL:
            case PORT_SARIM_PEST_CONTROL:
                return InteractionHelper.click(
		                InteractionHelper.getRSNPC(Filters.NPCs.actionsContains("Travel").combine(Filters.NPCs.nameEquals("Squire"), true)), "Travel")
                        && WaitFor.condition(10000, () -> ShipUtils.isOnShip() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;

            case PORT_SARIM_VEOS:
                return handleZeahBoats("Travel to Port Sarim.");
            case GREAT_KOUREND:
                return handleZeahBoats("Travel to Port Piscarilius.");
            case LANDS_END:
                return handleZeahBoats("Travel to Land's End.");

            case ARDY_LOG_WEST:
            case ARDY_LOG_EAST:
                RSObject[] logSearch = Objects.findNearest(15, Filters.Objects.nameEquals("Log balance").combine(Filters.Objects.actionsContains("Walk-across"), true));
                if (logSearch.length > 0 && AccurateMouse.click(logSearch[0], "Walk-across")){
                    int agilityXP = Skills.getXP(Skills.SKILLS.AGILITY);
                    if (WaitFor.condition(General.random(7600, 1200), () -> Skills.getXP(Skills.SKILLS.AGILITY) > agilityXP ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS) {
                        return true;
                    }
                    if (Player.isMoving()){
                        WaitFor.milliseconds(1200, 2300);
                    }
                }
                getInstance().log("Could not navigate through gnome door.");
                break;


            case GNOME_TREE_DAERO:
                break;

            case GNOME_WAYDAR:
                if (NPCInteraction.clickNpc(Filters.NPCs.nameEquals("Daero"), new String[]{"Travel"})){
                    if (WaitFor.condition(5000, () -> Player.getPosition().distanceTo(GNOME_WAYDAR.getRSTile()) < 10 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) != WaitFor.Return.SUCCESS){
                        break;
                    }
                    WaitFor.milliseconds(1000, 2000);
                    return true;
                }
                break;

            case CRASH_ISLAND:
                if (NPCInteraction.clickNpc(Filters.NPCs.nameEquals("Waydar"), new String[]{"Travel"})){
                    if (WaitFor.condition(5000, () -> Player.getPosition().distanceTo(CRASH_ISLAND.getRSTile()) < 10 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) != WaitFor.Return.SUCCESS){
                        break;
                    }
                    WaitFor.milliseconds(1000, 2000);
                    return true;
                }
                break;

            case APE_ATOLL_GLIDER_CRASH:
                if (NPCInteraction.clickNpc(Filters.NPCs.nameEquals("Lumdo"), new String[]{"Travel"})){
                    if (WaitFor.condition(5000, () -> Player.getPosition().distanceTo(APE_ATOLL_GLIDER_CRASH.getRSTile()) < 10 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) != WaitFor.Return.SUCCESS){
                        break;
                    }
                    WaitFor.milliseconds(1000, 2000);
                    return true;
                }
                break;
            case GNOME_DROPOFF:
                if (NPCInteraction.clickNpc(Filters.NPCs.nameEquals("Waydar"), new String[]{"Travel"})){
                    if (WaitFor.condition(5000, () -> Player.getPosition().distanceTo(CRASH_ISLAND.getRSTile()) < 10 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) != WaitFor.Return.SUCCESS){
                        break;
                    }
                    WaitFor.milliseconds(1000, 2000);
                    return true;
                }
                break;

            case HAM_OUTSIDE:
                if (clickObject(Filters.Objects.nameEquals("Ladder"), "Climb-up", () -> Player.getPosition().getY() < 4000 ?
                        WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)) {
                    return true;
                }
                break;

            case HAM_INSIDE:
                if (RSObjectHelper.exists(Filters.Objects.actionsContains("Pick-Lock"))){
                    if (InteractionHelper.click(RSObjectHelper.get(Filters.Objects.actionsContains("Pick-Lock")), "Pick-Lock")){
                        WaitFor.condition(
		                        WaitFor.random(6000, 9000), () -> !RSObjectHelper.exists(Filters.Objects.actionsContains("Pick-Lock")) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
                        return true;
                    }
                } else {
                    if (InteractionHelper.click(RSObjectHelper.get(Filters.Objects.actionsContains("Climb-down")), "Climb-down")){
                        WaitFor.condition(3000, () -> HAM_INSIDE.getRSTile().distanceTo(Player.getPosition()) < 10 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
                        return true;
                    }
                }
                break;



            case FEROX_ENCLAVE_PORTAL_F2P:
                if(NPCInteraction.isConversationWindowUp() || clickObject(Filters.Objects.nameEquals("Large door"), "Open",() -> NPCInteraction.isConversationWindowUp() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)){
                    NPCInteraction.handleConversationRegex("Yes");
                    return WaitFor.condition(3000,
                            () -> FEROX_ENCLAVE_PORTAL_F2P.getRSTile().distanceTo(Player.getPosition()) < 10 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
                }
                break;
            case CASTLE_WARS_DOOR:
                if(NPCInteraction.isConversationWindowUp() || clickObject(Filters.Objects.nameEquals("Castle Wars portal"), "Enter", () -> NPCInteraction.isConversationWindowUp() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)){
                    NPCInteraction.handleConversationRegex("Yes");
                    return WaitFor.condition(3000,
                            () -> CASTLE_WARS_DOOR.getRSTile().distanceTo(Player.getPosition()) < 10 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
                }
                break;

            case FOSSIL_ISLAND_BARGE:
                if(clickObject(Filters.Objects.nameEquals("Rowboat"),"Travel",() -> NPCInteraction.isConversationWindowUp() ?
                        WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)){
                    NPCInteraction.handleConversationRegex("Row to the barge and travel to the Digsite.");
                    return WaitFor.condition(5000,
                            () -> FOSSIL_ISLAND_BARGE.getRSTile().distanceTo(Player.getPosition()) < 10 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
                }
                break;

            case ENTRANA_TO_PORT_SARIM:
            case PORT_SARIM_TO_ENTRANA:
                if (handleShip("Take-boat")){
                    getInstance().log("Successfully boarded ship!");
                    return true;
                } else {
                    getInstance().log("Failed to take Entrana boat.");
                }
                break;

            case FAIRY_RING_ABYSSAL_AREA:
                return FairyRing.takeFairyRing(FairyRing.Locations.ABYSSAL_AREA);
            case FAIRY_RING_ABYSSAL_NEXUS:
                return FairyRing.takeFairyRing(FairyRing.Locations.ABYSSAL_NEXUS);
            case FAIRY_RING_APE_ATOLL:
                return FairyRing.takeFairyRing(FairyRing.Locations.APE_ATOLL);
            case FAIRY_RING_ARCEUUS_LIBRARY:
                return FairyRing.takeFairyRing(FairyRing.Locations.ARCEUUS_LIBRARY);
            case FAIRY_RING_ARDOUGNE_ZOO:
                return FairyRing.takeFairyRing(FairyRing.Locations.ARDOUGNE_ZOO);
            case FAIRY_RING_CANIFIS:
                return FairyRing.takeFairyRing(FairyRing.Locations.CANIFIS);
            case FAIRY_RING_CHASM_OF_FIRE:
                return FairyRing.takeFairyRing(FairyRing.Locations.CHASM_OF_FIRE);
            case FAIRY_RING_COSMIC_ENTITYS_PLANE:
                return FairyRing.takeFairyRing(FairyRing.Locations.COSMIC_ENTITYS_PLANE);
            case FAIRY_RING_DRAYNOR_VILLAGE_ISLAND:
                return FairyRing.takeFairyRing(FairyRing.Locations.DRAYNOR_VILLAGE_ISLAND);
            case FAIRY_RING_EDGEVILLE:
                return FairyRing.takeFairyRing(FairyRing.Locations.EDGEVILLE);
            case FAIRY_RING_ENCHANTED_VALLEY:
                return FairyRing.takeFairyRing(FairyRing.Locations.ENCHANTED_VALLEY);
            case FAIRY_RING_FELDIP_HILLS_HUNTER_AREA:
                return FairyRing.takeFairyRing(FairyRing.Locations.FELDIP_HILLS_HUNTER_AREA);
            case FAIRY_RING_FISHER_KINGS_REALM:
                return FairyRing.takeFairyRing(FairyRing.Locations.FISHER_KINGS_REALM);
            case FAIRY_RING_GORAKS_PLANE:
                return FairyRing.takeFairyRing(FairyRing.Locations.GORAKS_PLANE);
            case FAIRY_RING_HAUNTED_WOODS:
                return FairyRing.takeFairyRing(FairyRing.Locations.HAUNTED_WOODS);
            case FAIRY_RING_HAZELMERE:
                return FairyRing.takeFairyRing(FairyRing.Locations.HAZELMERE);
            case FAIRY_RING_ISLAND_SOUTHEAST_ARDOUGNE:
                return FairyRing.takeFairyRing(FairyRing.Locations.ISLAND_SOUTHEAST_ARDOUGNE);
            case FAIRY_RING_KALPHITE_HIVE:
                return FairyRing.takeFairyRing(FairyRing.Locations.KALPHITE_HIVE);
            case FAIRY_RING_KARAMJA_KARAMBWAN_SPOT:
                return FairyRing.takeFairyRing(FairyRing.Locations.KARAMJA_KARAMBWAN_SPOT);
            case FAIRY_RING_LEGENDS_GUILD:
                return FairyRing.takeFairyRing(FairyRing.Locations.LEGENDS_GUILD);
            case FAIRY_RING_LIGHTHOUSE:
                return FairyRing.takeFairyRing(FairyRing.Locations.LIGHTHOUSE);
            case FAIRY_RING_MCGRUBOR_WOODS:
                return FairyRing.takeFairyRing(FairyRing.Locations.MCGRUBOR_WOODS);
            case FAIRY_RING_MISCELLANIA:
                return FairyRing.takeFairyRing(FairyRing.Locations.MISCELLANIA);
            case FAIRY_RING_MISCELLANIA_PENGUINS:
                return FairyRing.takeFairyRing(FairyRing.Locations.MISCELLANIA_PENGUINS);
            case FAIRY_RING_MORT_MYRE_ISLAND:
                return FairyRing.takeFairyRing(FairyRing.Locations.MORT_MYRE_ISLAND);
            case FAIRY_RING_MORT_MYRE_SWAMP:
                return FairyRing.takeFairyRing(FairyRing.Locations.MORT_MYRE_SWAMP);
            case FAIRY_RING_MOUNT_KARUULM:
                return FairyRing.takeFairyRing(FairyRing.Locations. MOUNT_KARUULM);
            case FAIRY_RING_MUDSKIPPER_POINT:
                return FairyRing.takeFairyRing(FairyRing.Locations.MUDSKIPPER_POINT);
            case FAIRY_RING_MYREQUE_HIDEOUT:
                return FairyRing.takeFairyRing(FairyRing.Locations.MYREQUE_HIDEOUT);
            case FAIRY_RING_NORTH_OF_NARDAH:
                return FairyRing.takeFairyRing(FairyRing.Locations.NORTH_OF_NARDAH);
            case FAIRY_RING_PISCATORIS_HUNTER_AREA:
                return FairyRing.takeFairyRing(FairyRing.Locations.PISCATORIS_HUNTER_AREA);
            case FAIRY_RING_POISON_WASTE:
                return FairyRing.takeFairyRing(FairyRing.Locations.POISON_WASTE);
            case FAIRY_RING_POLAR_HUNTER_AREA:
                return FairyRing.takeFairyRing(FairyRing.Locations.POLAR_HUNTER_AREA);
            case FAIRY_RING_RELLEKKA_SLAYER_CAVE:
                return FairyRing.takeFairyRing(FairyRing.Locations.RELLEKKA_SLAYER_CAVE);
            case FAIRY_RING_SHILO_VILLAGE:
                return FairyRing.takeFairyRing(FairyRing.Locations.SHILO_VILLAGE);
            case FAIRY_RING_SINCLAIR_MANSION:
                return FairyRing.takeFairyRing(FairyRing.Locations.SINCLAIR_MANSION);
            case FAIRY_RING_SOUTH_CASTLE_WARS:
                return FairyRing.takeFairyRing(FairyRing.Locations.SOUTH_CASTLE_WARS);
            case FAIRY_RING_STRANGLEWOOD:
                return FairyRing.takeFairyRing(FairyRing.Locations.STRANGLEWOOD);
            case FAIRY_RING_TOWER_OF_LIFE:
                return FairyRing.takeFairyRing(FairyRing.Locations.TOWER_OF_LIFE);
            case FAIRY_RING_TZHAAR:
                return FairyRing.takeFairyRing(FairyRing.Locations.TZHAAR);
            case FAIRY_RING_WIZARDS_TOWER:
                return FairyRing.takeFairyRing(FairyRing.Locations.WIZARDS_TOWER);
            case FAIRY_RING_YANILLE:
                return FairyRing.takeFairyRing(FairyRing.Locations.YANILLE);
            case FAIRY_RING_ZANARIS:
                return FairyRing.takeFairyRing(FairyRing.Locations.ZANARIS);
            case FAIRY_RING_ZUL_ANDRA:
                return FairyRing.takeFairyRing(FairyRing.Locations.ZUL_ANDRA);

            case WITCHHAVEN_FERRY:
            case FISHING_PLATFORM_FERRY:
                return handleFishingPlatform();

            case FOSSIL_ISLAND_FERRY_NORTH:
                return takeFossilIslandBoat("Row to the north of the island.");
            case FOSSIL_ISLAND_FERRY_ISLAND:
                return takeFossilIslandBoat("Row out to sea.");
            case FOSSIL_ISLAND_FERRY_CAMP:
                if(NPCs.find("Barge guard").length > 0){
                    if(NPCInteraction.clickNpc(Filters.NPCs.nameEquals("Barge guard"),"Quick-Travel")){
                        return WaitFor.condition(8000,
                                () -> FOSSIL_ISLAND_FERRY_CAMP.getRSTile().distanceTo(Player.getPosition()) < 10 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
                    }
                } else {
                    return takeFossilIslandBoat("Row to the camp.");
                }
                break;
            case RELLEKKA_DOCK_FROM_ISLES:
                return NPCInteraction.clickNpc(Filters.NPCs.actionsEquals("Rellekka"),"Rellekka") &&
                        WaitFor.condition(15000,() -> RELLEKKA_DOCK_FROM_ISLES.getRSTile().distanceTo(Player.getPosition()) < 10
                                ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
            case JATIZSO_DOCK:
                return NPCInteraction.clickNpc(Filters.NPCs.nameEquals("Mord Gunnars"),"Jatizso") &&
                        WaitFor.condition(15000,() -> JATIZSO_DOCK.getRSTile().distanceTo(Player.getPosition()) < 10
                                ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
            case NEITIZNOT_DOCK:
                return NPCInteraction.clickNpc(Filters.NPCs.nameEquals("Maria Gunnars"),"Neitiznot") &&
                        WaitFor.condition(15000,() -> NEITIZNOT_DOCK.getRSTile().distanceTo(Player.getPosition()) < 10
                                ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;

            case OBSERVATORY_INSIDE:
                return clickObject(Filters.Objects.nameEquals("Rope"),"Climb", () -> OBSERVATORY_INSIDE.getRSTile().distanceTo(Player.getPosition()) < 5
                        ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) && WaitFor.milliseconds(600,1800) != null;
            case OBSERVATORY_OUTSIDE:
                return (NPCInteraction.isConversationWindowUp() ||  clickObject(Filters.Objects.nameEquals("Door"),"Open",
                        () -> NPCInteraction.isConversationWindowUp() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE))
                        && WaitFor.condition(15000,() -> {
                    if(NPCInteraction.isConversationWindowUp())
                        NPCInteraction.handleConversation("Yes.");
                    return OBSERVATORY_OUTSIDE.getRSTile().distanceTo(Player.getPosition()) < 5
                            ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE;
                }) == WaitFor.Return.SUCCESS && WaitFor.milliseconds(600,1800) != null;

            case MOSS_GIANT_ISLAND_ROPE:
            case MOSS_GIANT_ISLAND_ROPE_LANDING:
                if(Player.getPosition().distanceTo(MOSS_GIANT_ISLAND_ROPE.getRSTile()) >= 2){
                    Walking.blindWalkTo(MOSS_GIANT_ISLAND_ROPE.getRSTile());
                    WaitFor.milliseconds(200,400);
                }
                if (clickObject(Filters.Objects.nameEquals("Ropeswing"), "Swing-on", () -> Player.getPosition().getX() < 2708 ?
                        WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)) {
                    return true;
                }
                return false;
            case SHANTAY_PASS_ENTRANCE:
            case SHANTAY_PASS_EXIT:
                if(Player.getPosition().getY() < 3117){
                    return clickObject(Filters.Objects.nameEquals("Shantay pass"),"Go-through", () -> SHANTAY_PASS_ENTRANCE.getRSTile().equals(Player.getPosition())
                            ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) && WaitFor.milliseconds(600,1800) != null;
                } else if(Inventory.getCount(1854) == 0){
                    NPCInteraction.talkTo(Filters.NPCs.actionsEquals("Buy-pass"),new String[]{"Buy-pass"}, new String[]{});
                }
                return Inventory.getCount(1854) > 0 && clickObject(Filters.Objects.nameEquals("Shantay pass"),"Go-through", () -> {
                    DoomsToggle.handleToggle();
                    return SHANTAY_PASS_EXIT.getRSTile().equals(Player.getPosition())
                            ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE;
                }) && WaitFor.milliseconds(600,1800) != null;

            case PATERDOMUS_EAST_ENTRANCE:
                return clickObject(Filters.Objects.nameEquals("Trapdoor"), new String[]{"Open","Climb-down"}, () -> PATERDOMUS_EAST_ENTRANCE.getRSTile().equals(Player.getPosition())
                    ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) && WaitFor.milliseconds(600,1800) != null;
            case PATERDOMUS_EAST_EXIT:
                return clickObject(Filters.Objects.nameEquals("Holy barrier"), "Pass-through", () -> PATERDOMUS_EAST_EXIT.getRSTile().equals(Player.getPosition())
                    ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) && WaitFor.milliseconds(600,1800) != null;

            case SWAMP_BOATY:
                return InteractionHelper.click(InteractionHelper.getRSObject(Filters.Objects.nameEquals("Swamp Boaty")), "Quick-board") && WaitFor.condition( 15000,  () -> SWAMP_BOATY.getRSTile().distanceTo(Player.getPosition()) < 5
                    ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) != null && WaitFor.milliseconds(600,1800) != null;
            case SWAMP_BOATY_MORTTON:
                return InteractionHelper.click(InteractionHelper.getRSObject(Filters.Objects.nameEquals("Swamp Boaty")), "Board") && WaitFor.condition( 15000,  () -> SWAMP_BOATY_MORTTON.getRSTile().distanceTo(Player.getPosition()) < 5
                    ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) != null && WaitFor.milliseconds(600,1800) != null;

            case BRINE_RAT_CAVE_TREE:
            case BRINE_RAT_CAVE_ENTER:
                if(Player.getPosition().distanceTo(BRINE_RAT_CAVE_TREE.getRSTile()) >= 2){
                    if(Walking.blindWalkTo(BRINE_RAT_CAVE_TREE.getRSTile())){
                        WaitFor.condition(6000,
                            () -> Player.getPosition().distanceTo(BRINE_RAT_CAVE_TREE.getRSTile()) <= 2 ?
                                WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
                    }
                }
                return RSItemHelper.click("Spade","Dig") && WaitFor.condition(10000,
                    () -> Player.getPosition().distanceTo(BRINE_RAT_CAVE_ENTER.getRSTile()) < 5 ?
                        WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS && WaitFor.milliseconds(1500, 2500) != null;

            case FERRY_AL_KHARID_TO_UNKAH:
                return NPCInteraction.clickNpc(Filters.NPCs.nameEquals("Ferryman Sathwood"),"Ferry") &&
                    WaitFor.condition(15000,() -> FERRY_AL_KHARID_TO_UNKAH.getRSTile().distanceTo(Player.getPosition()) < 10
                        ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
            case FERRY_UNKAH_TO_AL_KHARID:
                return NPCInteraction.clickNpc(Filters.NPCs.nameEquals("Ferryman Nathwood"),"Ferry") &&
                    WaitFor.condition(15000,() -> FERRY_UNKAH_TO_AL_KHARID.getRSTile().distanceTo(Player.getPosition()) < 10
                        ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;


            case UNKAH_SHANTAY_PASS_EAST_ENTRANCE:
            case UNKAH_SHANTAY_PASS_EAST_EXIT:
                if(Player.getPosition().getX() > 3195){
                    return clickObject(Filters.Objects.nameEquals("Shantay pass"),"Go-through", () -> UNKAH_SHANTAY_PASS_EAST_ENTRANCE.getRSTile().equals(Player.getPosition())
                        ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) && WaitFor.milliseconds(600,1800) != null;
                } else if(Inventory.getCount(1854) == 0){
                    NPCInteraction.talkTo(Filters.NPCs.actionsEquals("Buy-pass"),new String[]{"Buy-pass"}, new String[]{});
                }
                return Inventory.getCount(1854) > 0 && clickObject(Filters.Objects.nameEquals("Shantay pass"),"Go-through", () -> {
                    DoomsToggle.handleToggle();
                    return UNKAH_SHANTAY_PASS_EAST_EXIT.getRSTile().equals(Player.getPosition())
                        ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE;
                }) && WaitFor.milliseconds(600,1800) != null;
            case UNKAH_SHANTAY_PASS_SOUTH_ENTRANCE:
            case UNKAH_SHANTAY_PASS_SOUTH_EXIT:
                if(Player.getPosition().getX() > 3195){
                    return clickObject(Filters.Objects.nameEquals("Shantay pass"),"Go-through", () -> UNKAH_SHANTAY_PASS_SOUTH_ENTRANCE.getRSTile().equals(Player.getPosition())
                        ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) && WaitFor.milliseconds(600,1800) != null;
                } else if(Inventory.getCount(1854) == 0){
                    NPCInteraction.talkTo(Filters.NPCs.actionsEquals("Buy-pass"),new String[]{"Buy-pass"}, new String[]{});
                }
                return Inventory.getCount(1854) > 0 && clickObject(Filters.Objects.nameEquals("Shantay pass"),"Go-through", () -> {
                    DoomsToggle.handleToggle();
                    return UNKAH_SHANTAY_PASS_SOUTH_EXIT.getRSTile().equals(Player.getPosition())
                        ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE;
                }) && WaitFor.milliseconds(600,1800) != null;

            case YANILLE_BALANCE_EDGE_SOUTH:
                return clickObject(Filters.Objects.nameEquals("Balancing ledge"), "Walk-across",
                        () -> Player.getPosition().distanceTo(YANILLE_BALANCE_EDGE_NORTH.getRSTile()) <= 2 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
            case YANILLE_BALANCE_EDGE_NORTH:
                return clickObject(Filters.Objects.nameEquals("Balancing edge"), "Walk-across",
                        () -> Player.getPosition().distanceTo(YANILLE_BALANCE_EDGE_SOUTH.getRSTile()) <= 2 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
            case YANILLE_MONKEY_BARS_EAST:
                return clickObject(Filters.Objects.nameEquals("Monkeybars"), "Swing across",
                        () -> Player.getPosition().distanceTo(YANILLE_MONKEY_BARS_WEST.getRSTile()) <= 2 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
            case YANILLE_MONKEY_BARS_WEST:
                return clickObject(Filters.Objects.nameEquals("Monkeybars"), "Swing across",
                        () -> Player.getPosition().distanceTo(YANILLE_MONKEY_BARS_EAST.getRSTile()) <= 2 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);

            case LIZARDMAN_CANYON_WEST:
                return clickObject(Filters.Objects.nameEquals("Handholds"), "Climb",
                        () -> Player.getPosition().equals(LIZARDMAN_CANYON_EAST.getRSTile()) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
            case LIZARDMAN_CANYON_EAST:
                return clickObject(Filters.Objects.nameEquals("Handholds"), "Climb",
                        () -> Player.getPosition().equals(LIZARDMAN_CANYON_WEST.getRSTile()) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
            case LIZARDMAN_SHAMANS_CANYON_EAST:
                return clickObject(Filters.Objects.nameEquals("Handholds"), "Climb",
                        () -> Player.getPosition().equals(LIZARDMAN_SHAMANS_CANYON_WEST.getRSTile()) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
            case LIZARDMAN_SHAMANS_CANYON_WEST:
                return clickObject(Filters.Objects.nameEquals("Handholds"), "Climb",
                        () -> Player.getPosition().equals(LIZARDMAN_SHAMANS_CANYON_EAST.getRSTile()) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);

            case SLEPE_BOAT_FROM_PORT:
                return InteractionHelper.click(InteractionHelper.getRSObject(Filters.Objects.nameEquals("Row boat")), "Travel") && WaitFor.condition( 15000,  () -> SLEPE_BOAT_FROM_SLEPE.getRSTile().distanceTo(Player.getPosition()) < 5
                        ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) != null && WaitFor.milliseconds(600,1800) != null;
            case SLEPE_BOAT_FROM_SLEPE:
                return InteractionHelper.click(InteractionHelper.getRSObject(Filters.Objects.nameEquals("Row boat")), "Travel") && WaitFor.condition( 15000,  () -> SLEPE_BOAT_FROM_PORT.getRSTile().distanceTo(Player.getPosition()) < 5
                        ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) != null && WaitFor.milliseconds(600,1800) != null;

            case SOUL_WARS_PORTAL:
                return clickObject(Filters.Objects.nameEquals("Soul Wars Portal"), "Enter",
                        () -> Player.getPosition().equals(specialLocation.getRSTile()) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
            case FEROX_ENCLAVE_PORTAL_TO_ISLE_OF_SOULS:
                return clickObject(Filters.Objects.nameEquals("Portal"), "Ferox Enclave",
                        () -> Player.getPosition().equals(specialLocation.getRSTile()) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
            case EDGEVILLE_PORTAL_TO_ISLE_OF_SOULS:
                return clickObject(Filters.Objects.nameEquals("Portal"), "Edgeville",
                        () -> Player.getPosition().equals(specialLocation.getRSTile()) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
			
          case KBD_LAIR:
          case KBD_LAIR_LOBBY:
            return clickObject(Filters.Objects.nameEquals("Lever"), "Pull",
                    () -> Player.getPosition().equals(specialLocation.getRSTile()) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);


            case GAMES_ROOM_BOTTOM:
                return clickObject(Filters.Objects.nameEquals("Staircase").and(Filters.Objects.actionsEquals("Climb-down")), "Climb-down",
                        () -> Player.getPosition().distanceTo(specialLocation.getRSTile()) < 10 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
            case GAMES_ROOM_MIDDLE:
                String action = Player.getPosition().getY() > 4000 ? "Climb-up":"Climb-down";
                return clickObject(Filters.Objects.nameEquals("Staircase").and(Filters.Objects.actionsEquals(action)), action,
                        () -> Player.getPosition().distanceTo(specialLocation.getRSTile()) < 10 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
            case GAMES_ROOM_TOP:
                return clickObject(Filters.Objects.nameEquals("Staircase").and(Filters.Objects.actionsEquals("Climb-up")), "Climb-up",
                        () -> Player.getPosition().distanceTo(specialLocation.getRSTile()) < 10 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
                

            case MOLCH_NORTHERN_CAVE_ENTRANCE:
            case MOLCH_NORTHERN_CAVE_DROPDOWN:
            case MOLCH_SOUTHERN_CAVE_DROPDOWN:
            case MOLCH_SOUTHERN_CAVE_ENTRANCE:
                return clickObject(Filters.Objects.nameEquals("Lizard dwelling"), "Enter",
                        () -> Player.getPosition().distanceTo(specialLocation.getRSTile()) < 100 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);

            case BOATY_MOLCH_ISLAND:
                return handleBoaty("Molch Island", specialLocation.getRSTile());
            case BOATY_BATTLEFRONT:
                return handleBoaty("Battlefront", specialLocation.getRSTile());
            case BOATY_MOLCH:
                return handleBoaty("Molch", specialLocation.getRSTile());
            case BOATY_SHAYZIEN:
                return handleBoaty("Shayzien", specialLocation.getRSTile());

            case MORT_MYRE_BRIDGE_N:
            case MORT_MYRE_BRIDGE_S:
                return clickObject(Filters.Objects.nameEquals("Tree").and(Filters.Objects.actionsEquals("Cross-bridge")), "Cross-bridge",
                        () -> Player.getPosition().equals(specialLocation.getRSTile()) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);

            case BOATY_BURGH:
                return handleBoaty("Burgh de Rott.", specialLocation.getRSTile());
            case BOATY_ICYENE_GRAVEYARD:
                return handleBoaty("Icyene Graveyard.", specialLocation.getRSTile());
            case BOATY_SLEPE:
                return handleBoaty("Slepe.", specialLocation.getRSTile());


            case LIGHTHOUSE_LADDER:
            case LIGHTHOUSE_UNDERGROUND:
                return clickObject(Filters.Objects.nameEquals("Iron ladder"), "Climb",
                        () -> Player.getPosition().equals(specialLocation.getRSTile()) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);

            case CAPTAIN_BENTLEY_PIRATES_COVE:
            case CAPTAIN_BENTLEY_LUNAR_ISLE:
                return NPCInteraction.clickNpc(Filters.NPCs.nameEquals("Captain Bentley"),"Travel") &&
                        WaitFor.condition(15000,() -> specialLocation.getRSTile().distanceTo(Player.getPosition()) < 10
                                ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
            case LOKAR_SEARUNNER_RELLEKKA:
                return NPCInteraction.clickNpc(Filters.NPCs.nameEquals("Lokar Searunner"),"Rellekka") &&
                        WaitFor.condition(15000,() -> specialLocation.getRSTile().distanceTo(Player.getPosition()) < 10
                                ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
            case LOKAR_SEARUNNER_PIRATES_COVE:
                return NPCInteraction.clickNpc(Filters.NPCs.nameEquals("Lokar Searunner"),"Pirate's Cove") &&
                        WaitFor.condition(15000,() -> {
                            if(NPCInteraction.isConversationWindowUp()){
                                NPCInteraction.handleConversationRegex("That's fine.*");
                            }
                            return specialLocation.getRSTile().distanceTo(Player.getPosition()) < 10
                                    ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE;
                        }) == WaitFor.Return.SUCCESS;

            case PRIF_MINE_INSIDE:
                return clickObject(Filters.Objects.nameEquals("Cave entrance"), "Enter",
                        () -> Player.getPosition().equals(specialLocation.getRSTile()) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
            case PRIF_MINE_OUTSIDE:
                return clickObject(Filters.Objects.nameEquals("Steps"), "Exit",
                        () -> Player.getPosition().equals(specialLocation.getRSTile()) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);


            case SHILO_CART_FROM_BRIMHAVEN:
            case SHILO_CART_FROM_SHILO:
                return NPCInteraction.clickNpc(Filters.NPCs.actionsContains("Pay-fare"), "Pay-fare") &&
                        WaitFor.condition(15000,() -> specialLocation.getRSTile().distanceTo(Player.getPosition()) < 10
                                ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;

            case KILLERWAT_PLANE_ENTRANCE:
            case KILLERWAT_PLANE_EXIT:
                if(NPCInteraction.isConversationWindowUp()){
                    NPCInteraction.handleConversation("Yes I want to go in and don't show me this message again.", "Thanks for the warning, but I'm not scared of any monster.");
                    if(WaitFor.condition(4500, () -> Player.getPosition().equals(specialLocation.getRSTile()) ?
                            WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS){
                        return true;
                    }
                }
                return clickObject(Filters.Objects.nameEquals("Interdimensional rift", "Portal Home"), "Enter",
                        () -> Player.getPosition().equals(specialLocation.getRSTile()) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);

            case STRANGLEWOOD_ROWBOAT_ENTRANCE:
            case STRANGLEWOOD_ROWBOAT_EXIT:
                return clickObject(Filters.Objects.nameEquals("Rowboat"), "Travel",
                        ()-> Player.getPosition().equals(specialLocation.getRSTile()) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
            case STRANGLEWOOD_ENTRY_ENTRANCE:
            case STRANGLEWOOD_ENTRY_EXIT:
                return clickObject(Filters.Objects.nameEquals("Entry"), "Enter",
                        ()-> Player.getPosition().equals(specialLocation.getRSTile()) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
        }

        return false;
    }
    public static boolean handleZeahBoats(String locationOption){
        String travelOption = "Travel";
        RSNPC[] npcs = NPCs.find("Veos","Captain Magoro");
        if(npcs.length > 0){
            String[] actions = npcs[0].getActions();
            if(actions != null){
                List<String> asList = Arrays.asList(actions);
                if(asList.stream().anyMatch(a -> a.equals("Port Sarim") || a.equals("Land's End"))){
                    if(locationOption.contains("Port Sarim")){
                        travelOption = "Port Sarim";
                    } else if(locationOption.contains("Piscarilius")){
                        travelOption = "Port Piscarilius";
                    } else if(locationOption.contains("Land")){
                        travelOption = "Land's End";
                    }
                } else if(!asList.contains("Travel")){
                    return handleFirstTripToZeah(locationOption);
                }
            }
        }
        if(NPCInteraction.clickNpc(Filters.NPCs.nameEquals("Veos", "Captain Magoro"),new String[]{travelOption})){
            RSTile current = Player.getPosition();
            if (WaitFor.condition(8000, () -> (ShipUtils.isOnShip() || Player.getPosition().distanceTo(current) > 20) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) != WaitFor.Return.SUCCESS) {
                return false;
            }
            WaitFor.milliseconds(1800, 2800);
            return true;
        }
        return false;
    }

    private static boolean handleFirstTripToZeah(String locationOption){
        getInstance().log("First trip to zeah");
        if(NPCInteraction.talkTo(Filters.NPCs.nameEquals("Veos", "Captain Magoro"), new String[]{"Talk-to"}, new String[]{
                locationOption,"Can you take me somewhere?","That's great, can you take me there please?","Can you take me to Great Kourend?"})) {
            RSTile current = Player.getPosition();
            if (WaitFor.condition(8000, () -> (ShipUtils.isOnShip() || Player.getPosition().distanceTo(current) > 20) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) != WaitFor.Return.SUCCESS) {
                return false;
            }
            WaitFor.milliseconds(1800, 2800);
            return true;
        }
        return false;
    }

    public static boolean handleShip(String... targetLocation){
        if (NPCInteraction.clickNpc(Filters.NPCs.actionsContains(targetLocation), targetLocation)
                && WaitFor.condition(10000, () -> ShipUtils.isOnShip() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS){
            WaitFor.milliseconds(1800, 2800);
            return true;
        }
        return false;
    }

    public static boolean handleKaramjaShip(){
        String[] options = {"Pay-fare", "Pay-Fare"};
        String[] chat = {"Yes please.", "Can I journey on this ship?", "Search away, I have nothing to hide.", "Ok."};
        boolean pirateTreasureComplete = Game.getSetting(71) >= 4;
        if(pirateTreasureComplete){
            return handleShip("Pay-fare","Pay-Fare");
        } else if (NPCInteraction.talkTo(Filters.NPCs.actionsContains(options), options, chat)
                && WaitFor.condition(10000, () -> ShipUtils.isOnShip() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS){
            WaitFor.milliseconds(1800, 2800);
            return true;
        }
        return false;
    }

    public static boolean walkToObject(RSObject object) {
        if (!object.isOnScreen() || !object.isClickable()){
            Walking.blindWalkTo(object);
            if (WaitFor.isOnScreenAndClickable(object) != WaitFor.Return.SUCCESS){
                return false;
            }
        }
        return object.isOnScreen() && object.isClickable();
    }

    public static boolean clickObject(RSObject object, String action, WaitFor.Condition condition) {
        return InteractionHelper.click(object, action, condition);
    }

    public static boolean clickObject(Predicate<RSObject> filter, String action, WaitFor.Condition condition) {
        return clickObject(filter, new String[]{action}, condition);
    }

    public static boolean clickObject(Predicate<RSObject> filter, String[] action, WaitFor.Condition condition){
        RSObject[] objects = Objects.findNearest(15, filter);
        if (objects.length == 0){
            return false;
        }
        return InteractionHelper.click(objects[0], action, condition);
    }


    private static boolean handleFishingPlatform(){
        RSNPC[] jeb = NPCs.find(Filters.NPCs.nameEquals("Jeb").and(Filters.NPCs.actionsEquals("Travel")));
        if(jeb.length > 0){
            return InteractionHelper.click(jeb[0],"Travel") &&
                    WaitFor.condition(20000, () -> NPCChat.getMessage() != null ?
                                    WaitFor.Return.SUCCESS :
                                    WaitFor.Return.IGNORE

                                     ) == WaitFor.Return.SUCCESS;
        } else {
            return NPCInteraction.clickNpc(Filters.NPCs.nameEquals("Holgart"),new String[]{"Travel"}) &&
                    WaitFor.condition(20000, () -> NPCChat.getMessage() != null ?
                                    WaitFor.Return.SUCCESS :
                                    WaitFor.Return.IGNORE

                                     ) == WaitFor.Return.SUCCESS;
        }
    }

    private static boolean takeFossilIslandBoat(String destination){
        if(NPCInteraction.isConversationWindowUp() || clickObject(
                Filters.Objects.nameEquals("Rowboat"),
                "Travel",
                () -> NPCInteraction.isConversationWindowUp() ?
                        WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)){
            RSTile myPos = Player.getPosition();
            NPCInteraction.handleConversation(destination);
            return WaitFor.condition(5000,() -> Player.getPosition().distanceTo(myPos) > 10 ? WaitFor.Return.SUCCESS :
                    WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
        }
        return false;
    }

    private static boolean handleCarpetRide(String carpetDestination){
        if (NPCInteraction.talkTo(Filters.NPCs.actionsContains("Travel"), new String[]{"Travel"}, new String[]{carpetDestination})){
            WaitFor.milliseconds(3500, 5000); //wait for board carpet before starting moving condition
            WaitFor.condition(30000, WaitFor.getNotMovingCondition());
            WaitFor.milliseconds(2250, 3250);
            return true;
        }
        return false;
    }

    private static boolean handleBoaty(String destination, RSTile targetTile){
        if(NPCInteraction.isConversationWindowUp()){
            NPCInteraction.handleConversationRegex("^" + destination + "$");
            return WaitFor.condition(10000, () -> Player.getPosition().distanceTo(targetTile) < 10 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS
                    && WaitFor.milliseconds(800, 1200) != null;
        }
        return clickObject(Filters.Objects.nameEquals("Boaty", "Boat"), "Board", () -> NPCInteraction.isConversationWindowUp() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) && handleBoaty(destination, targetTile);
    }
}
