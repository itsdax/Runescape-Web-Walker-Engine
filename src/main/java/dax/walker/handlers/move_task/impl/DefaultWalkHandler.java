package dax.walker.handlers.move_task.impl;

import dax.walker.handlers.move_task.MoveTaskHandler;
import dax.walker.handlers.passive_action.PassiveAction;
import dax.walker.models.MoveTask;
import dax.walker.models.enums.MoveActionResult;
import dax.walker.utils.path.DaxPathFinder;
import org.tribot.api.General;
import org.tribot.api2007.Options;
import dax.walker.utils.AccurateMouse;

import java.util.List;

public class DefaultWalkHandler implements MoveTaskHandler {

    @Override
    public MoveActionResult handle(MoveTask moveTask, List<PassiveAction> passiveActionList) {
        if (!AccurateMouse.clickMinimap(moveTask.getDestination())) {
            return MoveActionResult.FAILED;
        }
        int initialDistance = DaxPathFinder.distance(moveTask.getDestination());

        if (!waitFor(() -> {
            int currentDistance = DaxPathFinder.distance(moveTask.getDestination());
            return currentDistance <= 2 || initialDistance - currentDistance > getDistanceOffset();
        }, 3500, passiveActionList).isSuccess()) {
            return MoveActionResult.FAILED;
        }

        return MoveActionResult.SUCCESS;
    }

    private int getDistanceOffset() {
        return Options.isRunEnabled() ? General.randomSD(3, 10, 7, 2) : General.randomSD(2, 10, 5, 2);
    }

}
