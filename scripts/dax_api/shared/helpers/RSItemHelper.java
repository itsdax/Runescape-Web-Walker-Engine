package scripts.dax_api.shared.helpers;

import org.tribot.api.input.Mouse;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.Banking;
import org.tribot.api2007.ChooseOption;
import org.tribot.api2007.Game;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSGroundItem;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;
import org.tribot.api2007.types.RSMenuNode;

import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class RSItemHelper {

    public static boolean click(String itemNameRegex, String itemAction){
        return click(new Filter<RSItem>() {
            @Override
            public boolean accept(RSItem item) {
                return getItemName(item).matches(itemNameRegex) && Arrays.stream(getItemActions(item)).anyMatch(s -> s.equals(itemAction));
            }
        }, itemAction);
    }

    public static boolean clickMatch(RSItem item, String regex){
        return item.click(new Filter<RSMenuNode>() {
            @Override
            public boolean accept(RSMenuNode rsMenuNode) {
                String action = rsMenuNode.getAction();
                return action != null && action.matches(regex);
            }
        });
    }

    public static boolean click(int itemID){
        return click(itemID, null);
    }

    public static boolean click(int itemID, String action){
        return click(Filters.Items.idEquals(itemID), action, true);
    }

    public static boolean click(Filter<RSItem> filter, String action){
        return click(filter, action, true);
    }

    /**
     *
     * @param filter filter for items
     * @param action action to click
     * @param one click only one item.
     * @return
     */
    public static boolean click(Filter<RSItem> filter, String action, boolean one){
        if (action == null){
            action = "";
        }
        List<RSItem> list = Arrays.stream(Inventory.find(filter)).collect(Collectors.toList());
        if (one) {
            RSItem closest = getClosestToMouse(list);
            return closest != null && closest.click(action);
        }
        boolean value = false;
        while (!list.isEmpty()){
            RSItem item = getClosestToMouse(list);
            if (item != null) {
                list.remove(item);
                if (item.click(action)){
                    value = true;
                }
            }
        }
        return value;
    }

    public static boolean click(RSItem item, String action){
        if (Banking.isBankScreenOpen()){
            Banking.close();
        }
        return action != null ? item.click(action) : item.click();
    }

    public static boolean use(int itemID){
        String name = Game.getSelectedItemName();
        RSItemDefinition rsItemDefinition = RSItemDefinition.get(itemID);
        String itemName;
        if (Game.getItemSelectionState() == 1 && name != null && rsItemDefinition != null && (itemName = rsItemDefinition.getName()) != null && name.equals(itemName)){
            return true;
        } else if (Game.getItemSelectionState() == 1){
            Mouse.click(3);
            ChooseOption.select("Cancel");
        }
        return RSItemHelper.click(itemID, "Use");
    }

    public static RSItem getClosestToMouse(List<RSItem> rsItems){
        Point mouse = Mouse.getPos();
        rsItems.sort(Comparator.comparingInt(o -> (int) getCenter(o.getArea()).distance(mouse)));
        return rsItems.size() > 0 ? rsItems.get(0) : null;
    }

    private static Point getCenter(Rectangle rectangle){
        return new Point(rectangle.x + rectangle.width/2, rectangle.y + rectangle.height/2);
    }


    public static RSItem getItem(Filter<RSItem> filter){
        return getClosestToMouse(Arrays.stream(Inventory.find(filter)).collect(Collectors.toList()));
    }

    public static boolean isNoted(RSItem item) {
        return item != null && isNoted(item.getID());
    }

    public static boolean isNoted(int id) {
        RSItemDefinition definition = RSItemDefinition.get(id);
        return definition != null && definition.isNoted();
    }


    public static String[] getItemActions(RSGroundItem rsGroundItem){
        return getItemActions(rsGroundItem.getDefinition());
    }

    public static String[] getItemActions(RSItem rsItem){
        return getItemActions(rsItem.getDefinition());
    }


    public static String getItemName(int id){
        return getItemName(RSItemDefinition.get(id));
    }

    public static String getItemName(RSGroundItem rsGroundItem){
        return getItemName(rsGroundItem.getDefinition());
    }

    public static String getItemName(RSItem rsItem){
        return getItemName(rsItem.getDefinition());
    }


    public static boolean isStackable(int id) {
        RSItemDefinition definition = RSItemDefinition.get(id);
        return definition != null && definition.isStackable();
    }


    public static boolean isStackable(RSItem rsItem) {
        RSItemDefinition definition = rsItem.getDefinition();
        return definition != null && definition.isStackable();
    }

    private static String[] getItemActions(RSItemDefinition rsItemDefinition){
        if (rsItemDefinition == null){
            return new String[0];
        }
        String[] actions = rsItemDefinition.getActions();
        return actions != null ? actions : new String[0];
    }

    private static String getItemName(RSItemDefinition definition){
        String name = definition.getName();
        return name != null ? name : "null";
    }

    public static int distanceToMouse(RSItem item){
        Rectangle rectangle = item.getArea();
        if (rectangle == null){
            return Integer.MAX_VALUE;
        }
        return (int) Mouse.getPos().distance(rectangle.x + rectangle.width, rectangle.y + rectangle.height);
    }


}
