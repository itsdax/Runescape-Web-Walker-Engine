package scripts.dax_api.engine.utils;

import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSTile;
import scripts.dax_api.walker_engine.local_pathfinding.AStarNode;
import scripts.dax_api.walker_engine.local_pathfinding.Reachable;

import java.util.*;

/**
 * For local pathing ONLY. Anything outside your region will return unexpected results.
 */
public class DaxPathFinder {

    public static int distance(RSTile tile) {
        return distance(getMap(), tile);
    }

    public static int distance(Destination[][] map, RSTile tile) {
        RSTile worldTile = tile.toLocalTile();
        int x = worldTile.getX(), y = worldTile.getY();

        Destination destination = map[x][y];
        return destination == null ? Integer.MAX_VALUE : destination.distance;
    }

    public static boolean canReach(RSTile tile) {
        return getPath(tile) == null;
    }

    public static boolean canReach(Destination[][] map, RSTile tile) {
        return getPath(map, tile) == null;
    }

    public static List<RSTile> getPath(RSTile tile) {
        return getPath(getMap(), tile);
    }

    public static List<RSTile> getPath(Destination destination) {
        Destination parent = destination;

        List<RSTile> rsTiles = new ArrayList<>();
        rsTiles.add(destination.tile.toWorldTile());

        while (true) {
            parent = parent.parent;
            if (parent == null) {
                break;
            }
            rsTiles.add(parent.tile.toWorldTile());
        }

        Collections.reverse(rsTiles);
        return rsTiles;
    }

    public static List<RSTile> getPath(Destination[][] map, RSTile tile) {
        RSTile worldTile = tile.toLocalTile();
        int x = worldTile.getX(), y = worldTile.getY();

        Destination destination = map[x][y];

        if (destination == null) {
            return null;
        }

        return destination.getPath();
    }

    public static Destination[][] getMap() {
        final RSTile home = Player.getPosition().toLocalTile();
        Destination[][] map = new Destination[104][104];
        int[][] collisionData = PathFinding.getCollisionData();

        Queue<Destination> queue = new LinkedList<>();
        queue.add(new Destination(home, null, 0));
        map[home.getX()][home.getY()] = queue.peek();

        while (!queue.isEmpty()) {
            Destination currentLocal = queue.poll();

            int x = currentLocal.getLocalTile().getX(), y = currentLocal.getLocalTile().getY();
            Destination destination = map[x][y];

            for (Reachable.Direction direction : Reachable.Direction.values()) {
                if (!direction.isValidDirection(x, y, collisionData)) {
                    continue; //Cannot traverse to tile from current.
                }

                RSTile neighbor = direction.getPointingTile(currentLocal.getLocalTile());
                int destinationX = neighbor.getX(), destinationY = neighbor.getY();

                if (!AStarNode.isWalkable(collisionData[destinationX][destinationY])) {
                    continue;
                }

                if (map[destinationX][destinationY] != null) {
                    continue; //Traversed already
                }

                map[destinationX][destinationY] = new Destination(neighbor, currentLocal, destination.getDistance() + 1);
                queue.add(map[destinationX][destinationY]);
            }

        }
        return map;
    }

    public static class Destination {
        private RSTile tile;
        private Destination parent;
        private int distance;

        public Destination(RSTile tile, Destination parent, int distance) {
            this.tile = tile;
            this.parent = parent;
            this.distance = distance;
        }

        public RSTile getLocalTile() {
            return tile;
        }

        public Destination getParent() {
            return parent;
        }

        public int getDistance() {
            return distance;
        }

        public List<RSTile> getPath() {
            return DaxPathFinder.getPath(this);
        }

    }

}
