package scripts.webwalker_logic.shared.helpers;

import org.tribot.api.input.Mouse;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.Banking;
import org.tribot.api2007.ChooseOption;
import org.tribot.api2007.Game;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;
import org.tribot.api2007.types.RSMenuNode;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


public class RSItemAction {

    public static boolean click(String itemNameRegex, String itemAction){
        return click(new Filter<RSItem>() {
            @Override
            public boolean accept(RSItem item) {
                RSItemDefinition definition = item.getDefinition();
                if (definition == null) {
                    return false;
                }
                String name = definition.getName();
                if (name == null || !name.matches(itemNameRegex)) {
                    return false;
                }
                String[] actions = definition.getActions();
                return actions != null && Arrays.stream(actions).anyMatch(s -> s.equals(itemAction));
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

    public static boolean click(Filter<RSItem> filter, String action ){
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
        return RSItemAction.click(itemID, "Use");
    }

    private static RSItem getClosestToMouse(List<RSItem> rsItems){
        Point mouse = Mouse.getPos();
        rsItems.sort(Comparator.comparingInt(o -> (int) getCenter(o.getArea()).distance(mouse)));
        return rsItems.size() > 0 ? rsItems.get(0) : null;
    }

    private static Point getCenter(Rectangle rectangle){
        return new Point(rectangle.x + rectangle.width/2, rectangle.y + rectangle.height/2);
    }

    private static String getItemName(RSItem rsItem){
        RSItemDefinition definition = rsItem.getDefinition();
        if (definition == null){
            return null;
        }
        return definition.getName();
    }

}
