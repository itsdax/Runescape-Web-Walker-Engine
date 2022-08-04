package dax.shared.helpers.questing;

import dax.shared.helpers.InterfaceHelper;
import org.tribot.api2007.types.RSInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class QuestHelper {

    private static final int QUEST_MASTER_INTERFACE = 399;

    private static final List<org.tribot.script.sdk.Quest> NMZ_QUESTS = new ArrayList<>(Arrays.asList(
            org.tribot.script.sdk.Quest.THE_ASCENT_OF_ARCEUUS,
            org.tribot.script.sdk.Quest.CONTACT,
            org.tribot.script.sdk.Quest.THE_CORSAIR_CURSE,
            org.tribot.script.sdk.Quest.THE_DEPTHS_OF_DESPAIR,
            org.tribot.script.sdk.Quest.DESERT_TREASURE,
            org.tribot.script.sdk.Quest.DRAGON_SLAYER_I,
            org.tribot.script.sdk.Quest.DREAM_MENTOR,
            org.tribot.script.sdk.Quest.FAIRYTALE_I__GROWING_PAINS,
            org.tribot.script.sdk.Quest.FAMILY_CREST,
            org.tribot.script.sdk.Quest.FIGHT_ARENA,
            org.tribot.script.sdk.Quest.THE_FREMENNIK_ISLES,
            org.tribot.script.sdk.Quest.GETTING_AHEAD,
            org.tribot.script.sdk.Quest.THE_GRAND_TREE,
            org.tribot.script.sdk.Quest.THE_GREAT_BRAIN_ROBBERY,
            org.tribot.script.sdk.Quest.GRIM_TALES,
            org.tribot.script.sdk.Quest.HAUNTED_MINE,
            org.tribot.script.sdk.Quest.HOLY_GRAIL,
            org.tribot.script.sdk.Quest.HORROR_FROM_THE_DEEP,
            org.tribot.script.sdk.Quest.IN_SEARCH_OF_THE_MYREQUE,
            org.tribot.script.sdk.Quest.LEGENDS_QUEST,
            org.tribot.script.sdk.Quest.LOST_CITY,
            org.tribot.script.sdk.Quest.LUNAR_DIPLOMACY,
            org.tribot.script.sdk.Quest.MONKEY_MADNESS_I,
            org.tribot.script.sdk.Quest.MOUNTAIN_DAUGHTER,
            org.tribot.script.sdk.Quest.MY_ARMS_BIG_ADVENTURE,
            org.tribot.script.sdk.Quest.ONE_SMALL_FAVOUR,
            org.tribot.script.sdk.Quest.RECIPE_FOR_DISASTER,
            org.tribot.script.sdk.Quest.ROVING_ELVES,
            org.tribot.script.sdk.Quest.SHADOW_OF_THE_STORM,
            org.tribot.script.sdk.Quest.SHILO_VILLAGE,
            org.tribot.script.sdk.Quest.SONG_OF_THE_ELVES,
            org.tribot.script.sdk.Quest.TALE_OF_THE_RIGHTEOUS,
            org.tribot.script.sdk.Quest.TREE_GNOME_VILLAGE,
            org.tribot.script.sdk.Quest.TROLL_ROMANCE,
            org.tribot.script.sdk.Quest.TROLL_STRONGHOLD,
            org.tribot.script.sdk.Quest.VAMPYRE_SLAYER,
            org.tribot.script.sdk.Quest.WHAT_LIES_BELOW,
            org.tribot.script.sdk.Quest.WITCHS_HOUSE
    ));

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

    public static int getNmzQuestsCompleted(){
        return (int) NMZ_QUESTS.stream().filter(q -> q.getState() == org.tribot.script.sdk.Quest.State.COMPLETE).count();
    }

}
