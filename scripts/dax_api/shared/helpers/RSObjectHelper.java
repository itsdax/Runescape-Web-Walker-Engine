package scripts.dax_api.shared.helpers;

import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.Objects;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSObjectDefinition;

import java.util.Arrays;
import java.util.List;


public class RSObjectHelper {

    public static RSObject get(Filter<RSObject> filter){
        RSObject[] objects = Objects.find(10, filter);
        return objects.length > 0 ? objects[0] : null;
    }

    public static boolean exists(Filter<RSObject> filter){
        return Objects.find(10, filter).length > 0;
    }

    public static List<String> getActionsList(RSObject object){
        return Arrays.asList(getActions(object));
    }

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
