package scripts.webwalker_logic.local.walker_engine.object_handling;

import org.tribot.api.General;
import org.tribot.api.input.Mouse;
import org.tribot.api.interfaces.Clickable;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.*;
import org.tribot.api2007.Objects;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.*;
import scripts.webwalker_logic.local.walker_engine.WaitFor;

import java.awt.*;
import java.awt.geom.Area;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class does NOT examine objects.
 *
 * clickAction should never include the target entity's name. Just the action.
 */
public class AccurateMouse {

    public static boolean click(RSCharacter rsCharacter, String clickAction){
        return action(rsCharacter.getModel(), rsCharacter, clickAction, rsCharacter.getName(),false);
    }

    public static boolean click(RSObject rsObject, String clickAction){
        RSObjectDefinition definition = rsObject.getDefinition();
        return definition != null && action(rsObject.getModel(), rsObject, clickAction, definition.getName(),false);
    }

    public static boolean click(RSGroundItem rsGroundItem, String clickAction){
        RSItemDefinition definition = rsGroundItem.getDefinition();
        return definition != null && action(rsGroundItem.getModel(), rsGroundItem, clickAction, definition.getName(), false);
    }

    public static boolean hover(RSCharacter rsCharacter){
        return action(rsCharacter.getModel(), rsCharacter, null, rsCharacter.getName(), true);
    }

    public static boolean hover(RSObject rsObject) {
        RSObjectDefinition definition = rsObject.getDefinition();
        return definition != null && action(rsObject.getModel(), rsObject, null, definition.getName(), true);
    }

    public static boolean hover(RSGroundItem rsGroundItem) {
        RSItemDefinition definition = rsGroundItem.getDefinition();
        return definition != null && action(rsGroundItem.getModel(), rsGroundItem, null, definition.getName(), true);
    }

    public static boolean hover(RSCharacter rsCharacter, String clickAction){
        return action(rsCharacter.getModel(), rsCharacter, clickAction, rsCharacter.getName(), true);
    }

    public static boolean hover(RSObject rsObject, String clickAction) {
        RSObjectDefinition definition = rsObject.getDefinition();
        return definition != null && action(rsObject.getModel(), rsObject, clickAction, definition.getName(), true);
    }

    public static boolean hover(RSGroundItem rsGroundItem, String clickAction) {
        RSItemDefinition definition = rsGroundItem.getDefinition();
        return definition != null && action(rsGroundItem.getModel(), rsGroundItem, clickAction, definition.getName(), true);
    }

    /**
     *
     * @param model model of {@code clickable}
     * @param clickable target entity
     * @param clickAction action to click or hover. Do not include {@code targetName}
     * @param targetName name of the {@code clickable} entity
     * @param hover True to hover the OPTION, not the entity model. It will right click {@code clickable} and hover over option {@code clickAction}
     * @return whether action was successful.
     */
    private static boolean action(RSModel model, Clickable clickable, String clickAction, String targetName, boolean hover){
        for (int i = 0; i < General.random(4, 7); i++) {
            if (attemptAction(model, clickable, clickAction, targetName, hover)){
                return true;
            }
        }
        return false;
    }


    public static boolean walkScreenTile(RSTile destination){
        for (int i = 0; i < General.random(4, 6); i++) {
            Point point = getWalkingPoint(destination);
            if (point == null){
                continue;
            }
            Mouse.move(point);
            if (WaitFor.condition(100, () -> {
                String uptext = Game.getUptext();
                return uptext != null && uptext.startsWith("Walk here") ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE;
            }) == WaitFor.Return.SUCCESS){
                Mouse.click(1);
                if (waitResponse() == State.YELLOW){
                    RSTile clicked = WaitFor.getValue(500, Game::getDestination);
                    return clicked != null && clicked.equals(destination);
                } else {
                    break;
                }
            }
        };
        return false;
    }

    public static boolean hoverScreenTileWalkHere(RSTile destination){
        for (int i = 0; i < General.random(4, 6); i++) {
            Point point = getWalkingPoint(destination);
            if (point == null){
                continue;
            }
            Mouse.move(point);
            General.sleep(20, 35);
            return isHoveringScreenTileWalkHere(destination);
        };
        return false;
    }

    public static boolean isHoveringScreenTileWalkHere(RSTile destination){
        return isWalkingPoint(Mouse.getPos(), destination);
    }

    /**
     * Clicks or hovers desired action of entity.
     *
     * @param model target entity model
     * @param clickable target entity
     * @param clickAction action option
     * @param targetName name of target
     * @param hover hover option or not
     * @return result of action
     */
    private static boolean attemptAction(RSModel model, Clickable clickable, String clickAction, String targetName, boolean hover){
        if (model == null){
            return false;
        }

        if (ChooseOption.isOpen()){
            RSMenuNode menuNode = getValidMenuNode(clickable, targetName, clickAction, ChooseOption.getMenuNodes());
            if (handleMenuNode(menuNode, hover)){
                return true;
            }
        }

        Point point = model.getHumanHoverPoint();
        if (point == null || point.getX() == -1){
            return false;
        }

        if (point.distance(Mouse.getPos()) < Mouse.getSpeed()/10){
            Mouse.hop(point);
        } else {
            Mouse.move(point);
        }

        if (!model.getEnclosedArea().contains(point)){
            return false;
        }

        if (hover && clickAction == null){
            return true;
        }

        String regex = clickAction + " (-> )?" + targetName + "(.*)";
        if (WaitFor.condition(100, () -> Arrays.stream(ChooseOption.getOptions()).anyMatch(s -> s.matches(regex)) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS){

            boolean multipleMatches = false;

            String[] options = ChooseOption.getOptions();
            if (Arrays.stream(options).filter(s -> s.matches(regex)).count() > 1){
                multipleMatches = true;
            }

            String uptext = Game.getUptext();
            if (uptext == null){ //double check
                return false;
            }

            if (uptext.matches(regex) && !hover && !multipleMatches){
                Mouse.click(1);
                return waitResponse() == State.RED;
            }

            Mouse.click(3);
            RSMenuNode menuNode = getValidMenuNode(clickable, targetName, clickAction, ChooseOption.getMenuNodes());
            if (handleMenuNode(menuNode, hover)){
                return true;
            }

        }
        return false;
    }

    private static boolean handleMenuNode(RSMenuNode rsMenuNode, boolean hover){
        if (rsMenuNode == null){
            return false;
        }
        Rectangle rectangle = rsMenuNode.getArea();
        if (rectangle == null){
            ChooseOption.close();
            return false;
        }
        Point currentMousePosition = Mouse.getPos();
        if (hover){
            if (!rectangle.contains(currentMousePosition)){
                Mouse.moveBox(rectangle);
            }
        } else {
            if (rectangle.contains(currentMousePosition)){
                Mouse.click(1);
            } else {
                Mouse.clickBox(rectangle, 1);
            }
        }
        return true;
    }

    private static RSMenuNode getValidMenuNode(Clickable clickable, String targetName, String clickAction, RSMenuNode[] menuNodes){
        if (clickable == null || targetName == null || clickAction == null || menuNodes == null){
            return null;
        }
        List<RSMenuNode> list = Arrays.stream(menuNodes).filter(rsMenuNode -> {
            String target = rsMenuNode.getTarget(), action = rsMenuNode.getAction();
            return target != null && action != null && action.equals(clickAction) && target.startsWith(targetName);
        }).collect(Collectors.toList());
        return list.stream().filter(rsMenuNode -> rsMenuNode.correlatesTo(clickable)).findFirst().orElse(list.size() > 0 ? list.get(0) : null);
    }

    public static State waitResponse(){
        State response = WaitFor.getValue(250, () -> {
            switch (getState()){
                case YELLOW: return State.YELLOW;
                case RED: return State.RED;
            }
            return null;
        });
        return response != null ? response : State.NONE;
    }

    public static State getState(){
        int crosshairState = Game.getCrosshairState();
        for (State state : State.values()){
            if (state.id == crosshairState){
                return state;
            }
        }
        return State.NONE;
    }

    public enum State {
        NONE (0),
        YELLOW (1),
        RED (2);
        private int id;
        State (int id){
            this.id = id;
        }
    }


    private static Point getWalkingPoint(RSTile destination){
        Area area = getTileModel(destination);
        ArrayList<Polygon> polygons = new ArrayList<>();
        for (RSTile tile : new RSArea(destination, 1).getAllTiles()){
            if (tile.equals(destination)) {
                continue;
            }
            polygons.add(Projection.getTileBoundsPoly(tile, 0));
        }

        outterLoop:
        for (int i = 0; i < 1000; i++) {
            Point point = getRandomPoint(area.getBounds());
            if (Projection.isInViewport(point) && area.contains(point)){
                for (Polygon polygon : polygons){
                    if (polygon.contains(point)){
                        continue outterLoop;
                    }
                }
                return point;
            }
        }
        return null;
    }

    private static boolean isWalkingPoint(Point point, RSTile destination){
        String uptext = Game.getUptext();
        if (uptext == null || !uptext.startsWith("Walk here")){
            return false;
        }

        Area area = getTileModel(destination);
        ArrayList<Polygon> polygons = new ArrayList<>();
        for (RSTile tile : new RSArea(destination, 1).getAllTiles()){
            if (tile.equals(destination)) {
                continue;
            }
            polygons.add(Projection.getTileBoundsPoly(tile, 0));
        }

        if (!area.contains(point)){
            return false;
        }

        for (Polygon polygon : polygons){
            if (polygon.contains(point)){
                return false;
            }
        }
        return true;
    }

    private static Point getRandomPoint(Rectangle rectangle){
        return new Point(getRandomInteger(rectangle.x, rectangle.x + rectangle.width), getRandomInteger(rectangle.y, rectangle.y + rectangle.height));
    }

    private static int getRandomInteger (int min, int max) {
        return (int)(min + Math.floor(Math.random()*(max + 1 - min)));
    }

    private static Area getTileModel(RSTile tile){
        Polygon tilePolygon = Projection.getTileBoundsPoly(tile, 0);
        Area area = new Area(tilePolygon);
        for (RSObject rsObject : Objects.getAll(15, Filters.Objects.inArea(new RSArea(tile, 3)))){
            RSObjectDefinition definition = rsObject.getDefinition();
            if (definition == null){
                continue;
            }

            String[] actions = definition.getActions();

            if (actions == null || actions.length == 0){
                continue;
            }

            RSModel rsModel = rsObject.getModel();
            if (rsModel == null){
                continue;
            }
            Area objectArea = new Area(rsModel.getEnclosedArea());
            area.subtract(objectArea);
        }
        for (RSGroundItem rsGroundItem : GroundItems.find(new Filter<RSGroundItem>() {
            @Override
            public boolean accept(RSGroundItem rsGroundItem) {
                return rsGroundItem.getPosition().equals(tile);
            }
        })){
            RSItemDefinition definition = rsGroundItem.getDefinition();
            if (definition == null){
                continue;
            }

            String[] actions = definition.getActions();

            if (actions == null || actions.length == 0){
                continue;
            }

            RSModel rsModel = rsGroundItem.getModel();
            if (rsModel == null){
                continue;
            }
            Area objectArea = new Area(rsModel.getEnclosedArea());
            area.subtract(objectArea);
        }
        return area;
    }

}
