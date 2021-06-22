package scripts.dax_api.shared.helpers.questing;

import org.tribot.api2007.types.RSInterface;
import scripts.dax_api.shared.helpers.InterfaceHelper;

import java.util.ArrayList;
import java.util.Arrays;


public class QuestHelper {

    private static final int QUEST_MASTER_INTERFACE = 399;

    public static ArrayList<Quest> getAllQuests(){
        ArrayList<Quest> quests = new ArrayList<>();
        for (RSInterface rsInterface : InterfaceHelper.getAllInterfaces(QUEST_MASTER_INTERFACE)){
            String[] actions = rsInterface.getActions();
            if (actions == null){
                continue;
            }
            System.out.println(rsInterface.getText());
            if (Arrays.asList(actions).contains("Read Journal:")){
                quests.add(new Quest(rsInterface.getText(), Quest.State.getState(rsInterface.getTextColour())));
            }
        }
        return quests;
    }

}
