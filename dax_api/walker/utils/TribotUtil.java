package scripts.dax_api.walker.utils;

import org.tribot.api2007.types.*;

import java.util.Arrays;
import java.util.List;

public class TribotUtil {

    public static String getName(Object o) {
        if (o == null) return null;

        if (o instanceof RSObject) {
            RSObjectDefinition definition = ((RSObject) o).getDefinition();
            if (definition == null) return null;
            return definition.getName();
        }

        if (o instanceof RSItem) {
            RSItemDefinition definition = ((RSItem) o).getDefinition();
            if (definition == null) return null;
            return definition.getName();
        }

        if (o instanceof RSGroundItem) {
            RSItemDefinition definition = ((RSGroundItem) o).getDefinition();
            if (definition == null) return null;
            return definition.getName();
        }

        if (o instanceof RSPlayer) {
            return ((RSPlayer) o).getName();
        }

        if (o instanceof RSNPC) {
            return ((RSNPC) o).getName();
        }

        throw new IllegalStateException("Unknown object. Must be qualifying TriBot Object.");
    }

    public static List<String> getActions(Object o) {
        if (o == null) return null;

        if (o instanceof RSObject) {
            RSObjectDefinition definition = ((RSObject) o).getDefinition();
            if (definition == null) return null;
            return Arrays.asList(definition.getActions());
        }

        if (o instanceof RSItem) {
            RSItemDefinition definition = ((RSItem) o).getDefinition();
            if (definition == null) return null;
            return Arrays.asList(definition.getActions());
        }

        if (o instanceof RSGroundItem) {
            RSItemDefinition definition = ((RSGroundItem) o).getDefinition();
            if (definition == null) return null;
            return Arrays.asList(definition.getActions());
        }

        throw new IllegalStateException("Unknown object. Must be qualifying TriBot Object.");
    }

}
