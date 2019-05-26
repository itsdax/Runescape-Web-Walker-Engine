package scripts.dax_api.walker_engine.interaction_handling;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.interfaces.Clickable07;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.*;
import org.tribot.api2007.types.*;
import scripts.dax_api.walker.utils.AccurateMouse;
import scripts.dax_api.walker_engine.WaitFor;


public class InteractionHelper {

    public static boolean click(Clickable07 clickable, String... actions){
        return click(clickable, actions, null);
    }

    public static boolean click(Clickable07 clickable, String action, WaitFor.Condition condition){
        return click(clickable, new String[]{action}, condition);
    }

    /**
     * Interacts with nearby object and waits for {@code condition}.
     *
     * @param clickable clickable entity
     * @param actions actions to click
     * @param condition condition to wait for after the click action
     * @return if {@code condition} is null, then return the outcome of condition.
     *          Otherwise, return the result of the click action.
     */
    public static boolean click(Clickable07 clickable, String[] actions, WaitFor.Condition condition){
        if (clickable == null){
            return false;
        }

        if (clickable instanceof RSItem){
            return clickable.click(actions) && (condition == null || WaitFor.condition(General.random(7000, 8000), condition) == WaitFor.Return.SUCCESS);
        }

        RSTile position = ((Positionable) clickable).getPosition();

        if (!isOnScreenAndClickable(clickable)){
            Walking.blindWalkTo(position);
        }

        WaitFor.Return result = WaitFor.condition(WaitFor.getMovementRandomSleep(position), new WaitFor.Condition() {
            final long startTime = System.currentTimeMillis();
            @Override
            public WaitFor.Return active() {
                if (isOnScreenAndClickable(clickable)){
                    return WaitFor.Return.SUCCESS;
                }
                if (Timing.timeFromMark(startTime) > 2000 && !Player.isMoving()){
                    return WaitFor.Return.FAIL;
                }
                return WaitFor.Return.IGNORE;
            }
        });

        if (result != WaitFor.Return.SUCCESS){
            return false;
        }

        if (!AccurateMouse.click(clickable, actions)){
            if (Camera.getCameraAngle() < 90){
                Camera.setCameraAngle(General.random(90, 100));
            }
            return false;
        }

        return condition == null || WaitFor.condition(General.random(7000, 8500), condition) == WaitFor.Return.SUCCESS;
    }

    public static RSItem getRSItem(Filter<RSItem> filter){
        RSItem[] rsItems = Inventory.find(filter);
        return rsItems.length > 0 ? rsItems[0] : null;
    }

    public static RSNPC getRSNPC(Filter<RSNPC> filter){
        RSNPC[] rsnpcs = NPCs.findNearest(filter);
        return rsnpcs.length > 0 ? rsnpcs[0] : null;
    }

    public static RSObject getRSObject(Filter<RSObject> filter){
        RSObject[] objects = Objects.findNearest(15, filter);
        return objects.length > 0 ? objects[0] : null;
    }

    public static RSGroundItem getRSGroundItem(Filter<RSGroundItem> filter){
        RSGroundItem[] groundItems = GroundItems.findNearest(filter);
        return groundItems.length > 0 ? groundItems[0] : null;
    }

    public static boolean focusCamera(Clickable07 clickable){
        if (clickable == null){
            return false;
        }
        if (isOnScreenAndClickable(clickable)){
            return true;
        }
        RSTile tile = ((Positionable) clickable).getPosition();
        Camera.turnToTile(tile);
        Camera.setCameraAngle(100 - (tile.distanceTo(Player.getPosition()) * 4));
        return isOnScreenAndClickable(clickable);
    }

    private static boolean isOnScreenAndClickable(Clickable07 clickable){
        if (clickable instanceof RSCharacter && !((RSCharacter) clickable).isOnScreen()){
            return false;
        }
        if (clickable instanceof RSObject && !((RSObject) clickable).isOnScreen()){
            return false;
        }
        if (clickable instanceof RSGroundItem && !((RSGroundItem) clickable).isOnScreen()){
            return false;
        }
        return clickable.isClickable();
    }


}
