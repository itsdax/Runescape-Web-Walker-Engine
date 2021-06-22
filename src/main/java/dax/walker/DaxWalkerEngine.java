package dax.walker;

import dax.walker.handlers.move_task.impl.DefaultObjectHandler;
import dax.walker.handlers.move_task.impl.DefaultWalkHandler;
import dax.walker.handlers.passive_action.PassiveAction;
import dax.walker.handlers.special_cases.SpecialCaseHandler;
import dax.walker.handlers.special_cases.SpecialCaseHandlers;
import dax.walker.models.DaxLogger;
import dax.walker.models.MoveTask;
import dax.walker.models.enums.MoveActionResult;
import dax.walker.models.enums.Situation;
import dax.walker.utils.path.DaxPathFinder;
import dax.walker.utils.path.PathUtils;
import org.tribot.api2007.Game;
import org.tribot.api2007.types.RSTile;

import java.util.ArrayList;
import java.util.List;

public class DaxWalkerEngine implements DaxLogger {

    private List<PassiveAction> passiveActions;

    public DaxWalkerEngine() {
        passiveActions = new ArrayList<>();
    }

    public void addPassiveAction(PassiveAction passiveAction) {
        passiveActions.add(passiveAction);
    }

    public List<PassiveAction> getPassiveActions() {
        return passiveActions;
    }

    public boolean walkPath(List<RSTile> path) {
        int failAttempts = 0;

        while (failAttempts < 3) {
            MoveActionResult moveActionResult = walkNext(path);
            if (reachedEnd(path)) return true;
            if (moveActionResult == MoveActionResult.FATAL_ERROR) break;
            if (moveActionResult == MoveActionResult.SUCCESS) {
                failAttempts = 0;
            } else {
                log(String.format("Failed action [%d]", ++failAttempts));
            }
        }

        return false;
    }

    private boolean reachedEnd(List<RSTile> path) {
        if (path == null || path.size() == 0) return true;
        RSTile tile = Game.getDestination();
        return tile != null && tile.equals(path.get(path.size() - 1));
    }

    private MoveActionResult walkNext(List<RSTile> path) {
        MoveTask moveTask = determineNextAction(path);
        debug("Move task: " + moveTask);

        SpecialCaseHandler specialCaseHandler = SpecialCaseHandlers.getSpecialCaseHandler(moveTask);
        if (specialCaseHandler != null) {
            log(String.format("Overriding normal behavior with special handler: %s", specialCaseHandler.getName()));
            return specialCaseHandler.handle(moveTask, passiveActions);
        }

        switch (moveTask.getSituation()) {

            case Situation.COLLISION_BLOCKING:
            case Situation.DISCONNECTED_PATH:
                return new DefaultObjectHandler().handle(moveTask, passiveActions);

            case Situation.NORMAL_PATH_HANDLING:
                return new DefaultWalkHandler().handle(moveTask, passiveActions);

            case Situation.PATH_TOO_FAR:

            default:
                return MoveActionResult.FAILED;
        }
    }

    private MoveTask determineNextAction(List<RSTile> path) {
        RSTile furthestClickable = PathUtils.getFurthestReachableTileInMinimap(path);
        if (furthestClickable == null) {
            return new MoveTask(Situation.PATH_TOO_FAR, null, null);
        }

        RSTile next;
        try {
            next = PathUtils.getNextTileInPath(furthestClickable, path);
        } catch (PathUtils.NotInPathException e) {
            return new MoveTask(Situation.PATH_TOO_FAR, null, null);
        }

        if (next != null) {
            if (furthestClickable.distanceToDouble(next) >= 2D) {
                return new MoveTask(Situation.DISCONNECTED_PATH, furthestClickable, next);
            }

            if (!DaxPathFinder.canReach(next)) {
                return new MoveTask(Situation.COLLISION_BLOCKING, furthestClickable, next);
            }
        }

        return new MoveTask(Situation.NORMAL_PATH_HANDLING, furthestClickable, next);
    }


}
