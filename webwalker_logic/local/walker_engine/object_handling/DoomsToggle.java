package scripts.webwalker_logic.local.walker_engine.object_handling;

import org.tribot.api2007.Interfaces;
import org.tribot.api2007.types.RSInterface;
import scripts.webwalker_logic.shared.helpers.InterfaceHelper;
import scripts.webwalker_logic.local.walker_engine.WaitFor;

import java.util.Arrays;
import java.util.Optional;


public class DoomsToggle {

    private static final int STRONGHOLD_TOGGLE = 579;
    private static final int WILDERNESS_TOGGLE = 475;

    public static void handleToggle(){
        if (Interfaces.isInterfaceValid(STRONGHOLD_TOGGLE)) {
            handle(STRONGHOLD_TOGGLE, "Yes");
        }
        if (Interfaces.isInterfaceValid(WILDERNESS_TOGGLE)){
            handle(WILDERNESS_TOGGLE, "Enter Wilderness");
        }
    }

    public static void handle(int parentInterface, String option){
        Optional<RSInterface> optional = InterfaceHelper.getAllInterfaces(parentInterface).stream().filter(rsInterface -> {
            String[] actions = rsInterface.getActions();
            return actions != null && Arrays.asList(actions).contains(option);
        }).findAny();
        optional.ifPresent(rsInterface -> rsInterface.click(option));
        WaitFor.milliseconds(500, 1500);
    }

}
