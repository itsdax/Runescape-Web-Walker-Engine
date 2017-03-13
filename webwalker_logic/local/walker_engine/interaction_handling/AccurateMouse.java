package scripts.webwalker_logic.local.walker_engine.interaction_handling;

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
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This class does NOT examine objects.
 *
 * clickAction should never include the target entity's name. Just the action.
 */
public class AccurateMouse {

    public static void click(int button){
        click(Mouse.getPos(), button);
    }

    public static void click(int x, int y){
        click(x, y, 1);
    }

    public static void click(int x, int y, int button){
        click(new Point(x, y), button);
    }

    public static void click(Point point){
        click(point.x, point.y, 1);
    }

    public static void click(Point point, int button){
        if (!Mouse.getPos().equals(point)) {
            Mouse.move(point.x, point.y);
        }
        Mouse.sendPress(point, button);
        General.sleep(General.randomSD(5, 90, 30, 20));
        Mouse.sendRelease(point, button);
    }


    public static boolean click(Clickable clickable, String... clickActions){
        return action(clickable, false, clickActions);
    }

    public static boolean hover(Clickable clickable, String... clickActions){
        return action(clickable, true, clickActions);
    }

    private static boolean action(Clickable clickable, boolean hover, String... clickActions){
        String name = null;
        RSModel model = null;
        if (clickable instanceof RSCharacter){
            RSCharacter rsCharacter = ((RSCharacter) clickable);
            name = rsCharacter.getName();
            model = rsCharacter.getModel();
        } else if (clickable instanceof RSGroundItem){
            RSGroundItem rsGroundItem = ((RSGroundItem) clickable);
            RSItemDefinition rsItemDefinition = rsGroundItem.getDefinition();
            name = rsItemDefinition != null ? rsItemDefinition.getName() : null;
            model = rsGroundItem.getModel();
        } else if (clickable instanceof RSObject){
            RSObject rsObject = ((RSObject) clickable);
            RSObjectDefinition rsObjectDefinition = rsObject.getDefinition();
            name = rsObjectDefinition != null ? rsObjectDefinition.getName() : null;
            model = rsObject.getModel();
        }
        return action(model, clickable, name, hover, clickActions);
    }

    /**
     *
     * @param model model of {@code clickable}
     * @param clickable target entity
     * @param clickActions actions to click or hover. Do not include {@code targetName}
     * @param targetName name of the {@code clickable} entity
     * @param hover True to hover the OPTION, not the entity model. It will right click {@code clickable} and hover over option {@code clickAction}
     * @return whether action was successful.
     */
    private static boolean action(RSModel model, Clickable clickable, String targetName, boolean hover, String... clickActions){
        for (int i = 0; i < General.random(4, 7); i++) {
            if (attemptAction(model, clickable, targetName, hover, clickActions)){
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
                click(1);
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
     * @param clickActions actions
     * @param targetName name of target
     * @param hover hover option or not
     * @return result of action
     */
    private static boolean attemptAction(RSModel model, Clickable clickable, String targetName, boolean hover, String... clickActions){
//        System.out.println((hover ? "Hovering over" : "Clicking on") + " " + targetName + " with [" + Arrays.stream(clickActions).reduce("", String::concat) + "]");
        if (model == null){
            return false;
        }

        if (ChooseOption.isOpen()){
            RSMenuNode menuNode = getValidMenuNode(clickable, targetName, ChooseOption.getMenuNodes(), clickActions);
            if (handleMenuNode(menuNode, hover)){
                return true;
            } else {
                ChooseOption.close();
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

        if (hover && clickActions.length == 0){
            return true;
        }

        String regex = "(" + String.join("|", Arrays.stream(clickActions).map(Pattern::quote).collect(Collectors.toList())) + ")" + " (-> )?" + targetName + "(.*)";
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
                click(1);
                return waitResponse() == State.RED;
            }

            click(3);
            RSMenuNode menuNode = getValidMenuNode(clickable, targetName, ChooseOption.getMenuNodes(), clickActions);
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
                click(1);
            } else {
                Mouse.clickBox(rectangle, 1);
            }
        }
        return true;
    }

    private static RSMenuNode getValidMenuNode(Clickable clickable, String targetName, RSMenuNode[] menuNodes, String... clickActions){
        if (clickable == null || targetName == null || menuNodes == null){
            return null;
        }
        List<RSMenuNode> list = Arrays.stream(menuNodes).filter(rsMenuNode -> {
            String target = rsMenuNode.getTarget(), action = rsMenuNode.getAction();
            return target != null && action != null && Arrays.stream(clickActions).anyMatch(s -> s.equals(action)) && target.startsWith(targetName);
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
