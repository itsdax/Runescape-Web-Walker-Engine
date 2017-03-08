package scripts.webwalker_logic.local.walker_engine;

import org.tribot.api.General;
import org.tribot.api.input.Keyboard;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.util.ThreadSettings;
import scripts.webwalker_logic.local.walker_engine.interaction_handling.InteractionHelper;
import scripts.webwalker_logic.shared.helpers.InterfaceHelper;
import scripts.webwalker_logic.local.walker_engine.real_time_collision.RealTimeCollisionTile;

import java.util.*;
import java.util.stream.Collectors;


public class NPCInteraction implements Loggable {

    public static String[] GENERAL_RESPONSES = {"OK then.", "Yes.", "Okay..."};

    private static final int
            ITEM_ACTION_INTERFACE_WINDOW = 193,
            NPC_TALKING_INTERFACE_WINDOW = 231,
            PLAYER_TALKING_INTERFACE_WINDOW = 217,
            SELECT_AN_OPTION_INTERFACE_WINDOW = 219,
            SINGLE_OPTION_DIALOGUE_WINDOW = 229;

    private static final int[] ALL_WINDOWS = new int[]{ITEM_ACTION_INTERFACE_WINDOW, NPC_TALKING_INTERFACE_WINDOW, PLAYER_TALKING_INTERFACE_WINDOW, SELECT_AN_OPTION_INTERFACE_WINDOW, SINGLE_OPTION_DIALOGUE_WINDOW};


    private static NPCInteraction instance;

    private NPCInteraction(){

    }

    private static NPCInteraction getInstance(){
        return instance != null ? instance : (instance = new NPCInteraction());
    }

    public static boolean talkTo(Filter<RSNPC> rsnpcFilter, String[] talkOptions, String[] replyAnswers) {
        return talkTo(rsnpcFilter, talkOptions) && handleConversation(replyAnswers);
    }

    public static boolean talkTo(Filter<RSNPC> rsnpcFilter, String[] options) {
        RSNPC[] rsnpcs = NPCs.findNearest(rsnpcFilter);
        if (rsnpcs.length < 1) {
            getInstance().log("Cannot find NPC.");
        }

        RSNPC npc = rsnpcs[0];
        return InteractionHelper.click(npc, options) && waitForConversationWindow();

    }

    public static boolean waitForConversationWindow(){
        return WaitFor.condition(10000, () -> {
            if (isConversationWindowUp()) {
                return WaitFor.Return.SUCCESS;
            }
            return WaitFor.Return.IGNORE;
        }) == WaitFor.Return.SUCCESS;
    }

    public static boolean isConversationWindowUp(){
        return Arrays.stream(ALL_WINDOWS).anyMatch(Interfaces::isInterfaceValid);
    };

    public static boolean handleConversation(String... options){
        while (true){
            if (WaitFor.condition(General.random(550, 700), () -> {
                if (isConversationWindowUp()){
                    return WaitFor.Return.SUCCESS;
                }
                return WaitFor.Return.IGNORE;
            }) != WaitFor.Return.SUCCESS){
                break;
            }

            RSInterface clickContinue = getClickHereToContinue();
            if (clickContinue != null){
                clickHereToContinue(clickContinue);
                continue;
            }

            List<RSInterface> selectableOptions = getAllOptions(options);
            if (selectableOptions != null && selectableOptions.size() > 0){
                General.sleep(General.randomSD(350, 2250, 775, 350));
                getInstance().log("Replying with option: " + selectableOptions.get(0).getText());
                Keyboard.typeString(selectableOptions.get(0).getComponentIndex() + "");
                waitForNextOption();
            }
            WaitFor.milliseconds(100, 200);
        }
        return true;
    }

    private static RSInterface getClickHereToContinue(){
        List<RSInterface> list = getConversationDetails();
        if (list == null){
            return null;
        }
        Optional<RSInterface> optional = list.stream().filter(rsInterface -> rsInterface.getText().equals("Click here to continue")).findAny();
        return optional.isPresent() ? optional.get() : null;
    }

    private static void clickHereToContinue(RSInterface clickContinue){
        getInstance().log("Clicking continue.");
        Keyboard.typeKeys(' ');
//        if (clickContinue.click()) {
            waitForNextOption();
//        }
    }

    private static void waitForNextOption(){
        List<String> interfaces = getAllInterfaces().stream().map(RSInterface::getText).collect(Collectors.toList());
        WaitFor.condition(5000, () -> {
            if (!interfaces.equals(getAllInterfaces().stream().map(RSInterface::getText).collect(Collectors.toList()))){
               return WaitFor.Return.SUCCESS;
            }
            return WaitFor.Return.IGNORE;
        });
    }

    private static List<RSInterface> getConversationDetails(){
        StringBuilder sb = new StringBuilder();
        for (int window : ALL_WINDOWS){
            List<RSInterface> details = InterfaceHelper.getAllInterfaces(window).stream().filter(rsInterfaceChild -> {
                if (rsInterfaceChild.getTextureID() != -1) {
                    return false;
                }
                String text = rsInterfaceChild.getText();
                return text != null;
            }).collect(Collectors.toList());
            details.forEach(rsInterface -> sb.append(rsInterface.getText()).append(", " ));
            if (details.size() > 0) {
                getInstance().log("Dialogue: " + sb);
                return details;
            }
        }
        return null;
    }

    private static List<RSInterface> getAllInterfaces(){
        ArrayList<RSInterface> interfaces = new ArrayList<>();
        for (int window : ALL_WINDOWS) {
            interfaces.addAll(InterfaceHelper.getAllInterfaces(window));
        }
        return interfaces;
    }

    private static List<RSInterface> getAllOptions(String[] options){
        final List<String> optionList = Arrays.stream(options).map(String::toLowerCase).collect(Collectors.toList());
        List<RSInterface> list = getConversationDetails();
        return list != null ? list.stream().filter(rsInterface -> optionList.contains(rsInterface.getText().trim().toLowerCase())).collect(Collectors.toList()) : null;
    }

    @Override
    public String getName() {
        return "NPC Interaction";
    }

}
