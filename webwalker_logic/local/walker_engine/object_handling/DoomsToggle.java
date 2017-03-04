package scripts.webwalker_logic.local.walker_engine.object_handling;

import org.tribot.api2007.Interfaces;
import org.tribot.api2007.types.RSInterface;
import scripts.webwalker_logic.shared.helpers.InterfaceHelper;
import scripts.webwalker_logic.local.walker_engine.WaitFor;
import sun.security.provider.SHA;

import java.util.Arrays;
import java.util.Optional;


public class DoomsToggle {

    private static final int STRONGHOLD_TOGGLE = 579, WILDERNESS_TOGGLE = 475, SHANTY_TOGGLE = 565;

    private static final int[] GENERAL_CASES = {STRONGHOLD_TOGGLE, WILDERNESS_TOGGLE, SHANTY_TOGGLE};

    public static void handleToggle(){
        for (int generalCase : GENERAL_CASES){
            handle(generalCase, "Yes", "Enter Wilderness");
        }
    }

    public static void handle(int parentInterface, String... option){
        Optional<RSInterface> optional = InterfaceHelper.getAllInterfaces(parentInterface).stream().filter(rsInterface -> {
            String[] actions = rsInterface.getActions();
            return actions != null && Arrays.stream(option).anyMatch(s -> Arrays.stream(actions).anyMatch(s1 -> s1.equals(s)));
        }).findAny();
        optional.ifPresent(rsInterface -> rsInterface.click(option));
        WaitFor.milliseconds(500, 1500);
    }

}
