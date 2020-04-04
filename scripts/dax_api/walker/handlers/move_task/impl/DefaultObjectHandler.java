package scripts.dax_api.walker.handlers.move_task.impl;

import org.tribot.api2007.Objects;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSObjectDefinition;
import scripts.dax_api.walker.handlers.move_task.MoveTaskHandler;
import scripts.dax_api.walker.handlers.passive_action.PassiveAction;
import scripts.dax_api.walker.models.DaxLogger;
import scripts.dax_api.walker.models.MoveTask;
import scripts.dax_api.walker.models.enums.ActionResult;
import scripts.dax_api.walker.models.enums.MoveActionResult;
import scripts.dax_api.walker.utils.AccurateMouse;
import scripts.dax_api.walker.utils.TribotUtil;
import scripts.dax_api.walker.utils.camera.DaxCamera;
import scripts.dax_api.walker.utils.path.DaxPathFinder;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class DefaultObjectHandler implements MoveTaskHandler, DaxLogger {

    private static final List<String> MATCHES = Arrays.asList("Open", "Climb", "Climb-down", "Climb-up");

    @Override
    public MoveActionResult handle(MoveTask moveTask, List<PassiveAction> passiveActionList) {
        log("Starting...");

        if (!moveTask.getDestination().isClickable()) {
            if (!AccurateMouse.clickMinimap(moveTask.getDestination())) {
                return MoveActionResult.FAILED;
            }

            if (!waitForConditionOrNoMovement(() -> DaxPathFinder.distance(moveTask.getDestination()) < 6, 15000, passiveActionList).isSuccess()) {
                log("We did not reach our destination.");
                return MoveActionResult.FAILED;
            }
        }

        RSObject closest = getClosest(moveTask);
        if (closest == null) {
            log("Failed to grab closest object to handle.");
            return MoveActionResult.FAILED;
        }

        log("We are interacting with " + TribotUtil.getName(closest));
        if (!handle(moveTask, closest, MATCHES)) {
            log("Failed to interact with closest object.");
            return MoveActionResult.FAILED;
        }

        ActionResult actionResult = waitForConditionOrNoMovement(() -> DaxPathFinder.canReach(moveTask.getNext()), 15000, passiveActionList);
        log("Interaction result: " + actionResult);
        if (!actionResult.isSuccess()) {
            log("Action resulted in failed state.");
            return MoveActionResult.FAILED;
        }

        return MoveActionResult.SUCCESS;
    }

    private RSObject getClosest(MoveTask moveTask) {
        RSObject[] objects = getValid(moveTask);
        return objects.length > 0 ? objects[0] : null;
    }

    private RSObject[] getValid(MoveTask moveTask) {
        RSObject[] objects = Objects.findNearest(15, filter());

        if (getDirection(moveTask) == Direction.UP) {
            log("This object is leading us upwards.");
            objects = Arrays.stream(objects).filter(object -> TribotUtil.getActions(object).stream().anyMatch(s -> s.toLowerCase().contains("up")))
                    .toArray(RSObject[]::new);
        }

        if (getDirection(moveTask) == Direction.DOWN) {
            log("This object is leading us downwards.");
            objects = Arrays.stream(objects).filter(object -> TribotUtil.getActions(object).stream().anyMatch(s -> s.toLowerCase().contains("down")))
                    .toArray(RSObject[]::new);
        }

        Arrays.sort(objects, Comparator.comparingDouble(o -> o.getPosition().distanceToDouble(moveTask.getDestination())));
        return objects;
    }

    private Predicate<RSObject> filter() {
        return rsObject -> {
            RSObjectDefinition definition = rsObject.getDefinition();
            return Stream.of(definition).anyMatch(rsObjectDefinition ->
                    Arrays.stream(rsObjectDefinition.getActions()).anyMatch(MATCHES::contains)
                                                 );
        };
    }

    private boolean handle(MoveTask moveTask, RSObject object, List<String> actions) {
        return handle(moveTask, object, actions.toArray(new String[0]));
    }

    private boolean handle(MoveTask moveTask, RSObject object, String... action) {
        if (!object.isOnScreen() || !object.isClickable()) {
            DaxCamera.focus(object);
        }

        String[] clickActions = action;

        if (getDirection(moveTask) == Direction.UP) {
            clickActions = TribotUtil.getActions(object).stream().filter(s -> s.toLowerCase().contains("up")).toArray(
		            String[]::new);
        }

        if (getDirection(moveTask) == Direction.DOWN) {
            clickActions = TribotUtil.getActions(object).stream().filter(s -> s.toLowerCase().contains("down")).toArray(
		            String[]::new);
        }

        log(String.format("Clicking %s with %s", TribotUtil.getName(object), Arrays.toString(clickActions)));
        return AccurateMouse.click(object, clickActions);
    }

    private Direction getDirection(MoveTask moveTask) {
        int playerPlane = Player.getPosition().getPlane();
        int plane = moveTask.getNext() != null ? moveTask.getNext().getPlane() : playerPlane;
        if (plane > playerPlane) return Direction.UP;
        if (plane < playerPlane) return Direction.DOWN;
        return Direction.SAME_FLOOR;
    }

    private enum Direction {
        UP,
        DOWN,
        SAME_FLOOR
    }
}
