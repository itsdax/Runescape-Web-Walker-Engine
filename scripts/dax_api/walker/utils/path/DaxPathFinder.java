package scripts.dax_api.walker.utils.path;

import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.Game;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Player;
import org.tribot.api2007.Projection;
import org.tribot.api2007.types.RSCharacter;
import org.tribot.api2007.types.RSTile;
import scripts.dax_api.walker_engine.local_pathfinding.AStarNode;
import scripts.dax_api.walker_engine.local_pathfinding.Reachable;

import java.awt.*;
import java.util.List;
import java.util.Queue;
import java.util.*;

/**
 * For local pathing ONLY. Anything outside your region will return unexpected results.
 */
public class DaxPathFinder {

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

        public RSTile getWorldTile() {
            return tile.toWorldTile();
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

    /**
     * Method for grabbing the path your character is currently walking.
     *
     * @return The path your character is following.
     */
    public static List<RSTile> getWalkingQueue() {
        return getWalkingQueue(getMap());
    }

    /**
     * Method for grabbing the path your character is currently walking.
     *
     * @param map
     * @return The path your character is following.
     */
    public static List<RSTile> getWalkingQueue(Destination[][] map) {
        RSTile destination = Game.getDestination();
        if (destination == null) {
            destination = getNextWalkingTile();
        }
        return destination != null ? getPath(map, destination) : null;
    }

    /**
     *
     * Method to check if your character is walking to a destination.
     *
     * @param tile
     * @return true if your character is walking or will walk to that tile in the next game tick.
     */
    public static boolean isWalkingTowards(RSTile tile){
        RSTile tile1 = getNextWalkingTile();
        return tile1 != null && tile1.equals(tile);
    }

    /**
     *
     * Next tile that your character is moving to in the current/next game tick.
     *
     * @return The next tile that your character is walking to
     */
    public static RSTile getNextWalkingTile(){
        ArrayList<RSTile> tiles = getWalkingHistory();
        return tiles.size() > 0 && !tiles.get(0).equals(Player.getPosition()) ? tiles.get(0) : null;
    }

    /**
     *
     * @param tile
     * @return Distance to a tile accounting for collision. Integer.MAX_VALUE if not reachable.
     */
    public static int distance(Positionable tile) {
        return distance(getMap(), tile.getPosition());
    }

    public static int distance(Destination[][] map, Positionable tile) {
        RSTile worldTile = tile.getPosition().toLocalTile();
        int x = worldTile.getX(), y = worldTile.getY();

        if (!validLocalBounds(tile)) {
            return Integer.MAX_VALUE;
        }

        Destination destination = map[x][y];
        return destination == null ? Integer.MAX_VALUE : destination.distance;
    }

    public static boolean canReach(RSTile tile) {
        return canReach(getMap(), tile);
    }

    public static boolean canReach(Destination[][] map, RSTile tile) {
        if (tile.getPlane() != Player.getPosition().getPlane()) return false;
        RSTile worldTile = tile.getType() != RSTile.TYPES.LOCAL ? tile.toLocalTile() : tile;
        int x = worldTile.getX(), y = worldTile.getY();
        if (!validLocalBounds(tile) || x > map.length || y > map[x].length) {
            return false;
        }
        Destination destination = map[x][y];
        return destination != null;
    }

    public static List<RSTile> getPath(RSTile tile) {
        return getPath(getMap(), tile);
    }

    public static List<RSTile> getPath(Destination destination) {
        Stack<RSTile> rsTiles = new Stack<>();
        Destination parent = destination;
        while (parent != null) {
            rsTiles.add(parent.getWorldTile());
            parent = parent.parent;
        }
        return new ArrayList<>(rsTiles);
    }

    public static List<RSTile> getPath(Destination[][] map, RSTile tile) {
        RSTile worldTile = tile.getType() != RSTile.TYPES.LOCAL ? tile.toLocalTile() : tile;
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
        if(collisionData == null || collisionData.length < home.getX() || collisionData[home.getX()].length < home.getY()){
            return map;
        }

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

    public static void drawQueue(Destination[][] map, Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;
        List<RSTile> path = getWalkingQueue(map);
        if (path == null) {
            return;
        }

        RSTile previousTile = path.get(0);
        for (int i = 1; i < path.size(); i++) {
            Point point1 = Projection.tileToScreen(path.get(i), 0);
            Point point2 = Projection.tileToScreen(previousTile, 0);
            if (point1 == null || point1.x == -1 || point2 == null || point2.x == -1) {
                continue;
            }
            g.setColor(new Color(255, 0, 11, 116));
            g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(point1.x, point1.y, point2.x, point2.y);
            previousTile = path.get(i);
        }

    }

    public static void drawPaths(Destination[][] map, Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;
        for (Destination[] destinations : map) {
            for (Destination destination : destinations) {

                if (destination == null || destination.getParent() == null) {
                    continue;
                }

                RSTile tile = destination.getWorldTile();
                RSTile parent = destination.getParent().getWorldTile();

                if (!tile.isOnScreen() && !parent.isOnScreen()) {
                    continue;
                }

                Point point1 = Projection.tileToScreen(tile, 0);
                Point point2 = Projection.tileToScreen(parent, 0);

                if (point1 == null || point1.x == -1 || point2 == null || point2.x == -1) {
                    continue;
                }

                g.setColor(new Color(255, 255, 255, 60));
                g.setStroke(new BasicStroke(1));
                g.drawLine(point1.x, point1.y, point2.x, point2.y);
            }
        }
    }

    private static boolean validLocalBounds(Positionable positionable) {
        RSTile tile = positionable.getPosition().getType() == RSTile.TYPES.LOCAL ? positionable.getPosition() : positionable.getPosition().toLocalTile();
        return tile.getX() >= 0 && tile.getX() < 104 && tile.getY() >= 0 && tile.getY() < 104;
    }

    private static ArrayList<RSTile> getWalkingHistory(){
        return getWalkingHistory(Player.getRSPlayer());
    }

    private static ArrayList<RSTile> getWalkingHistory(RSCharacter rsCharacter){
        ArrayList<RSTile> walkingQueue = new ArrayList<>();
        if (rsCharacter == null){
            return walkingQueue;
        }
        int plane = rsCharacter.getPosition().getPlane();
        int[] xIndex = rsCharacter.getWalkingQueueX(), yIndex = rsCharacter.getWalkingQueueY();
        for (int i = 0; i < xIndex.length && i < yIndex.length; i++) {
            walkingQueue.add(new RSTile(xIndex[i], yIndex[i], plane, RSTile.TYPES.LOCAL).toWorldTile());
        }
        return walkingQueue;
    }

}
