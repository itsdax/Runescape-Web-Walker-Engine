package scripts.webwalker_logic.shared.helpers;

import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSObjectDefinition;


public class RSObjectHelper {

    public static String[] getActions(RSObject object){
        String[] emptyActions = new String[0];
        RSObjectDefinition definition = object.getDefinition();
        if (definition == null){
            return emptyActions;
        }
        String[] actions = definition.getActions();
        return actions != null ? actions : emptyActions;
    }

    public static String getName(RSObject object){
        RSObjectDefinition definition = object.getDefinition();
        if (definition == null){
            return "null";
        }
        String name = definition.getName();
        return name != null ? name : "null";
    }

}
