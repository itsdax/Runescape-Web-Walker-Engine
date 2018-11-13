package scripts.dax_api.engine;

import org.tribot.api2007.types.RSTile;
import scripts.dax_api.engine.models.WalkTask;
import scripts.dax_api.engine.models.WalkTaskType;
import scripts.dax_api.engine.utils.DaxPathFinder;
import scripts.dax_api.engine.utils.PathUtils;
import scripts.dax_api.walker_engine.WaitFor;

import java.util.List;

public class DaxWalkerEngine {

    public static WalkTask walkPath(List<RSTile> path) {
        int failAttempts = 0;
        while (true) {
            WalkTask walkTask = determineWalkTask(path);
            switch (walkTask.getWalkTaskType()) {
                case COLLISION_BLOCKING:
                    failAttempts = 0;
                    break;

                case DISCONNECTED_PATH:
                    failAttempts = 0;
                    break;

                case NORMAL_PATH_HANDLING:
                    failAttempts = 0;
                    break;

                case PATH_TOO_FAR:
                    failAttempts++;
                    break;
            }


            if (failAttempts > 0) {
                WaitFor.milliseconds(2000, 3500);
            }

            if (failAttempts > 3) {
                return walkTask;
            }

        }

    }


    private static WalkTask determineWalkTask(List<RSTile> path) {
        RSTile furthestClickable = PathUtils.getFurthestReachableTileInMinimap(path);
        if (furthestClickable == null) {
            return new WalkTask(WalkTaskType.PATH_TOO_FAR, null, null);
        }

        RSTile next;
        try {
            next = PathUtils.getNextTileInPath(furthestClickable, path);
        } catch (PathUtils.NotInPathException e) {
            return new WalkTask(WalkTaskType.PATH_TOO_FAR, null, null);
        }

        if (furthestClickable.distanceToDouble(next) >= 2D) {
            return new WalkTask(WalkTaskType.DISCONNECTED_PATH, furthestClickable, next);
        }

        if (!DaxPathFinder.canReach(next)) {
            return new WalkTask(WalkTaskType.COLLISION_BLOCKING, furthestClickable, next);
        }

        return new WalkTask(WalkTaskType.NORMAL_PATH_HANDLING, furthestClickable, next);
    }


}
