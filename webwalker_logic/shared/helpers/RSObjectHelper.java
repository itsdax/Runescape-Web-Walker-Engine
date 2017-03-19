package scripts.webwalker_logic.shared.helpers;

import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSObjectDefinition;


public class RSObjectHelper {

    public static String[] getObjectActions(RSObject object){
        String[] emptyActions = new String[0];
        RSObjectDefinition definition = object.getDefinition();
        if (definition == null){
            return emptyActions;
        }
        String[] actions = definition.getActions();
        return actions != null ? actions : emptyActions;
    }

}
