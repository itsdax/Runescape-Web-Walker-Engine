package dax.walker_engine.interaction_handling;

import dax.walker_engine.Loggable;
import org.tribot.api.ScriptCache;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.types.RSInterface;
import dax.shared.helpers.InterfaceHelper;
import dax.walker_engine.WaitFor;

import java.util.Arrays;
import java.util.Optional;


public class DoomsToggle implements Loggable {

    public static final int STRONGHOLD_TOGGLE = 579, WILDERNESS_TOGGLE = 475, SHANTY_TOGGLE = 565, WATERBIRTH = 574, MORT_MYRE = 580, LUMBRIDGE_SWAMP = 572,
            OBSERVATORY_TOGGLE = 560, REVENANTS_TOGGLE = 720;

    private static final int[] GENERAL_CASES = {STRONGHOLD_TOGGLE, WILDERNESS_TOGGLE, SHANTY_TOGGLE, WATERBIRTH, MORT_MYRE, LUMBRIDGE_SWAMP, OBSERVATORY_TOGGLE, REVENANTS_TOGGLE};

    private static DoomsToggle getInstance() {
        return (DoomsToggle) ScriptCache.get().computeIfAbsent("DaxWalker.DoomsToggle", k -> new DoomsToggle());
    }


    public static void handleToggle(){
        for (int generalCase : GENERAL_CASES){
            handle(generalCase, "Yes", "Enter Wilderness","Enter the swamp.","I'll be fine without a tinderbox.",
                    "Proceed regardless", "Let me jump, and don't warn me again!");
        }
    }

    public static void handle(int parentInterface, String... option){
        if (!Interfaces.isInterfaceSubstantiated(parentInterface)){
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
