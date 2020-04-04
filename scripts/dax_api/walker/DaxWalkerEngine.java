package scripts.dax_api.walker;

import org.tribot.api2007.Game;
import org.tribot.api2007.types.RSTile;
import scripts.dax_api.walker.handlers.move_task.impl.DefaultObjectHandler;
import scripts.dax_api.walker.handlers.move_task.impl.DefaultWalkHandler;
import scripts.dax_api.walker.handlers.passive_action.PassiveAction;
import scripts.dax_api.walker.handlers.special_cases.SpecialCaseHandler;
import scripts.dax_api.walker.handlers.special_cases.SpecialCaseHandlers;
import scripts.dax_api.walker.models.DaxLogger;
import scripts.dax_api.walker.models.MoveTask;
import scripts.dax_api.walker.models.enums.MoveActionResult;
import scripts.dax_api.walker.models.enums.Situation;
import scripts.dax_api.walker.utils.path.DaxPathFinder;
import scripts.dax_api.walker.utils.path.PathUtils;

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

            case COLLISION_BLOCKING:
            case DISCONNECTED_PATH:
                return new DefaultObjectHandler().handle(moveTask, passiveActions);

            case NORMAL_PATH_HANDLING:
                return new DefaultWalkHandler().handle(moveTask, passiveActions);

            case PATH_TOO_FAR:

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
