package scripts.webwalker_logic.local.walker_engine.navigation_utils;

import org.tribot.api.types.generic.Filter;
import org.tribot.api.util.Sorting;
import org.tribot.api2007.Game;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Player;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import scripts.webwalker_logic.local.walker_engine.Loggable;
import scripts.webwalker_logic.local.walker_engine.NPCInteraction;
import scripts.webwalker_logic.local.walker_engine.WaitFor;
import scripts.webwalker_logic.local.walker_engine.WalkerEngine;
import scripts.webwalker_logic.local.walker_engine.object_handling.ObjectHandler;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Optional;


public class NavigationSpecialCase implements Loggable{

    private static NavigationSpecialCase instance = null;

    private NavigationSpecialCase(){

    }

    private static NavigationSpecialCase getInstance(){
        return instance != null ? instance : (instance = new NavigationSpecialCase());
    }

    @Override
    public String getName() {
        return "Navigation Special Case";
    }

    /**
     * THE ABSOLUTE POSITION
     */
    public enum SpecialLocation {

        DWARF_CARTS_GE (3141, 3504, 0),
        DWARFS_CARTS_KELDAGRIM (2922, 10170, 0),

        BRIMHAVEN_DUNGEON_SURFACE (2744, 3152, 0),
        BRIMHAVEN_DUNGEON (2713, 9564, 0),

        GNOME_ENTRANCE (2461, 3382, 0), //entrance side
        GNOME_EXIT (2461, 3385, 0), //exit side

        GNOME_TREE_ENTRANCE (2465, 3493, 0), //entrance side
        GNOME_TREE_EXIT (2465, 3493, 0), //exit side

        ZEAH_SAND_CRAB (1784, 3458, 0),
        ZEAH_SAND_CRAB_ISLAND (1778, 3418, 0),

        PORT_SARIM_PAY_FARE (3029, 3217, 0),
        PORT_SARIM_PEST_CONTROL (3041, 3202, 0),
        PORT_SARIM_VEOS (3057, 3192, 0),
        KARAMJA_PAY_FARE (2953, 3146, 0),
        ARDOUGNE_PAY_FARE (2681, 3275, 0),
        BRIMHAVEN_PAY_FARE (2772, 3225, 0),
        GREAT_KOUREND (1824, 3691, 0),
        LANDS_END (1504, 3399, 0),
        PEST_CONTROL (2659, 2676, 0);



        int x, y, z;
        SpecialLocation(int x, int y, int z){
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    public static SpecialLocation getLocation(RSTile rsTile){
        Optional<SpecialLocation> optional = Arrays.stream(
                SpecialLocation.values()).filter(tile -> tile.z == rsTile.getPlane()
                && Point2D.distance(tile.x, tile.y, rsTile.getX(), rsTile.getY()) <= 2).findFirst();
        return optional.isPresent() ? optional.get() : null;
    }

    /**
     * action for getting to the case
     * @param specialLocation
     * @return
     */
    public static boolean handle(SpecialLocation specialLocation){
        String zeahBoatLocation = null;
        switch (specialLocation){

            case BRIMHAVEN_DUNGEON:
                if (!alreadyPaidFee()){
                    if (!NPCInteraction.clickOn(Filters.NPCs.nameEquals("Saniboch"), "Pay")) {
                        getInstance().log("Could not pay saniboch");
                        return false;
                    }
                    if (!NPCInteraction.handleConversation()){
                        return false;
                    }
                } else {
                    if (clickObject(Filters.Objects.nameEquals("Dungeon entrance"), "Enter", () -> Player.getPosition().getY() > 4000 ?
                            WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)){
                        return true;
                    } else {
                        getInstance().log("Could not enter dungeon");
                    }
                }
                break;

            case DWARF_CARTS_GE:
                RSObject[] objects = Objects.find(15, Filters.Objects.nameEquals("Train cart").combine(new Filter<RSObject>() {
                    @Override
                    public boolean accept(RSObject rsObject) {
                        return rsObject.getPosition().getY() == 10171;
                    }
                }, true));
                Sorting.sortByDistance(objects, new RSTile(2935, 10172, 0), true);
                if (clickObject(objects[0], "Ride", () -> Player.getPosition().getX() == specialLocation.x ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)){
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
                if (clickObject(Filters.Objects.nameEquals("Gate").combine(Filters.Objects.actionsContains("Open"), true), "Open",
                        () -> Player.getPosition().getY() == 3383 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)){
                    WaitFor.milliseconds(1060, 1500);
                    return true;
                } else {
                    getInstance().log("Could not navigate through gnome door.");
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
                if (NPCInteraction.clickOn(Filters.NPCs.nameEquals("Sandicrahb"), "Quick-travel") && WaitFor.condition(10000, () -> Player.getPosition().getY() >= 3457 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS){
                    getInstance().log("Paid for travel.");
                    return true;
                } else {
                    getInstance().log("Failed to pay travel.");
                }
                break;
            case ZEAH_SAND_CRAB_ISLAND:
                if (NPCInteraction.clickOn(Filters.NPCs.nameEquals("Sandicrahb"), "Quick-travel") && WaitFor.condition(10000, () -> Player.getPosition().getY() < 3457 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS){
                    getInstance().log("Paid for travel.");
                    return true;
                } else {
                    getInstance().log("Failed to pay travel.");
                }
                break;


            case KARAMJA_PAY_FARE:
            case PORT_SARIM_PAY_FARE:
            case ARDOUGNE_PAY_FARE:
            case BRIMHAVEN_PAY_FARE:
                if (handlePayFare()){
                    getInstance().log("Successfully boarded ship!");
                    return true;
                } else {
                    getInstance().log("Failed to pay fare.");
                }
                return false;
            case PEST_CONTROL:
            case PORT_SARIM_PEST_CONTROL:
                return NPCInteraction.clickOn(Filters.NPCs.actionsContains("Travel").combine(Filters.NPCs.nameEquals("Squire"), true), "Travel")
                        && WaitFor.condition(10000, () -> ShipUtils.isOnShip() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;

            case PORT_SARIM_VEOS:
                zeahBoatLocation = "Travel to Port Sarim.";
                break;
            case GREAT_KOUREND:
                zeahBoatLocation = "Travel to Great Kourend.";
                break;
            case LANDS_END:
                zeahBoatLocation = "Travel to Land's End.";
                break;
        }

        if (zeahBoatLocation != null){
            if (handleZeahBoats(zeahBoatLocation)){
                return true;
            }
        }

        return false;
    }

    public static boolean alreadyPaidFee(){
        return Game.getSetting(393) == 1;
    }

    public static boolean handleZeahBoats(String locationOption){
        if (NPCInteraction.talkTo(Filters.NPCs.nameEquals("Veos", "Captain Magoro"), new String[]{"Travel"}, new String[]{locationOption})
                && WaitFor.condition(10000, () -> ShipUtils.isOnShip() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS){
            WaitFor.milliseconds(1800, 2800);
            return true;
        }
        return false;
    }

    public static boolean handlePayFare(){
        String[] options = {"Pay-fare", "Pay-Fare"};
        if (NPCInteraction.talkTo(Filters.NPCs.actionsContains(options), options, new String[]{"Yes please.", "Can I journey on this ship?", "Search away, I have nothing to hide.", "Ok."})
                && WaitFor.condition(10000, () -> ShipUtils.isOnShip() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS){
            WaitFor.milliseconds(1800, 2800);
            return true;
        }
        return false;
    }

    public static boolean clickObject(RSObject object, String action, WaitFor.Condition condition) {
        if (!object.isOnScreen() || !object.isClickable()){
            WalkerEngine.getInstance().clickMinimap(object);
            if (WaitFor.condition(15000, () -> object.isOnScreen() && object.isClickable() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) != WaitFor.Return.SUCCESS){
                return false;
            }
        }
        return ObjectHandler.clickObjectAndWait(object, action, condition);
    }

    public static boolean clickObject(Filter<RSObject> filter, String action, WaitFor.Condition condition) {
        RSObject[] objects = Objects.find(15, filter);
        if (objects.length == 0){
            return false;
        }
        return clickObject(objects[0], action, condition);
    }

}
