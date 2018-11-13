package scripts.dax_api.engine.utils;

import org.tribot.api2007.Player;
import org.tribot.api2007.Projection;
import org.tribot.api2007.types.RSTile;
import scripts.dax_api.walker_engine.local_pathfinding.Reachable;

import java.awt.Point;
import java.util.*;

public class PathUtils {

    public static RSTile getNextTileInPath(RSTile current, List<RSTile> path) {
        int index = path.indexOf(current);

        if (index == -1) {
            throw new NotInPathException();
        }

        int next = index + 1;
        return next <= path.size() ? path.get(next) : null;
    }

    public static RSTile getClosestTileInPath(List<RSTile> path) {
        RSTile player = Player.getPosition();
        return path.stream().min(Comparator.comparingInt(player::distanceTo)).orElse(null);
    }

    public static RSTile getFurthestReachableTileInMinimap(List<RSTile> path) {
        List<RSTile> reversed = new ArrayList<>(path);
        Collections.reverse(reversed);

        DaxPathFinder.Destination[][] map = DaxPathFinder.getMap();
        for (RSTile tile : reversed) {
            Point point = Projection.tileToMinimap(tile);
            if (point == null) {
                continue;
            }
            if (DaxPathFinder.canReach(map, tile) && Projection.isInMinimap(point)) {
                return tile;
            }
        }
        return null;
    }

    public static RSTile getFurthestReachableTileOnScreen(List<RSTile> path) {
        List<RSTile> reversed = new ArrayList<>(path);
        Collections.reverse(reversed);

        DaxPathFinder.Destination[][] map = DaxPathFinder.getMap();
        for (RSTile tile : reversed) {
            if (DaxPathFinder.canReach(map, tile) && tile.isOnScreen() && tile.isClickable()) {
                return tile;
            }
        }
        return null;
    }

    public static class NotInPathException extends RuntimeException {
        public NotInPathException() {
        }
    }

}
