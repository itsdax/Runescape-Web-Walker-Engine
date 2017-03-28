package scripts.webwalker_logic.local.walker_engine.interaction_handling;

import org.tribot.api2007.Interfaces;
import org.tribot.api2007.types.RSInterface;
import scripts.webwalker_logic.local.walker_engine.Loggable;
import scripts.webwalker_logic.shared.helpers.InterfaceHelper;
import scripts.webwalker_logic.local.walker_engine.WaitFor;

import java.util.Arrays;
import java.util.Optional;


public class DoomsToggle implements Loggable{

    private static final int STRONGHOLD_TOGGLE = 579, WILDERNESS_TOGGLE = 475, SHANTY_TOGGLE = 565, WATERBIRTH = 574;

    private static final int[] GENERAL_CASES = {STRONGHOLD_TOGGLE, WILDERNESS_TOGGLE, SHANTY_TOGGLE, WATERBIRTH};

    private static DoomsToggle instance;

    private static DoomsToggle getInstance(){
        return instance != null ? instance : (instance = new DoomsToggle());
    }


    public static void handleToggle(){
        for (int generalCase : GENERAL_CASES){
            handle(generalCase, "Yes", "Enter Wilderness");
        }
    }

    public static void handle(int parentInterface, String... option){
        if (!Interfaces.isInterfaceValid(parentInterface)){
            return;
        }
        getInstance().log("Handling Interface: " + parentInterface);
        Optional<RSInterface> optional = InterfaceHelper.getAllInterfaces(parentInterface).stream().filter(rsInterface -> {
            String[] actions = rsInterface.getActions();
            return actions != null && Arrays.stream(option).anyMatch(s -> Arrays.stream(actions).anyMatch(s1 -> s1.equals(s)));
        }).findAny();
        optional.ifPresent(rsInterface -> rsInterface.click(option));
        WaitFor.milliseconds(500, 1500);
    }

    @Override
    public String getName() {
        return "Dooms Toggle";
    }

}
