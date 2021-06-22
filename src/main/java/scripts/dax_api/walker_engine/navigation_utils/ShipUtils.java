package scripts.dax_api.walker_engine.navigation_utils;

import org.tribot.api.General;
import org.tribot.api2007.Game;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Player;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import scripts.dax_api.walker_engine.WaitFor;
import scripts.dax_api.walker_engine.interaction_handling.InteractionHelper;


public class ShipUtils {

    private static final RSTile[] SPECIAL_CASES = new RSTile[]{new RSTile(2663, 2676, 1)};

    public static boolean isOnShip() {
        RSTile playerPos = Player.getPosition();
        for (RSTile specialCase : SPECIAL_CASES){
            if (new RSArea(specialCase, 5).contains(playerPos)){
                return true;
            }
        }
        return getGangplank() != null
                && Player.getPosition().getPlane() == 1
                && Objects.getAll(10, Filters.Objects.nameEquals("Ship's wheel", "Ship's ladder", "Anchor")).length > 0;
    }

    public static boolean crossGangplank() {
        RSObject gangplank = getGangplank();
        if (gangplank == null){
            return false;
        }
        if (!gangplank.click("Cross")){
            return false;
        }
        if (WaitFor.condition(1000, () -> Game.getCrosshairState() == 2 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) != WaitFor.Return.SUCCESS){
            return false;
        }
        return WaitFor.condition(General.random(2500, 3000), () -> !ShipUtils.isOnShip() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
    }

    private static RSObject getGangplank(){
        return InteractionHelper.getRSObject(Filters.Objects.nameEquals("Gangplank").combine(Filters.Objects.actionsContains("Cross"), true));
    }

}
