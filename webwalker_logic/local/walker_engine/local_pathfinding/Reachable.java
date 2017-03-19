package scripts.webwalker_logic.local.walker_engine.local_pathfinding;


import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSTile;

import java.util.LinkedList;
import java.util.Queue;

public class Reachable {

    private RSTile[][] map;

    private Reachable(){
        map = generateMap();
    }

    /**
     *
     * @param x
     * @param y
     * @return parent tile of tile(x,y)
     */
    public RSTile getParent(int x, int y){
        if (x <= 0 || y <= 0){
            return null;
        }
        if (x >= 104 || y >= 104){
            return null;
        }
        return map[x][y];
    }

    /**
     *
     * @return gets collision map.
     */
    public static Reachable getMap(){
        return new Reachable();
    }

    /**
     *
     * @return local reachable tiles
     */
    private static RSTile[][] generateMap(){
        RSTile localPlayerPosition = Player.getPosition().toLocalTile();
        boolean[][] traversed = new boolean[104][104];
        RSTile[][] parentMap = new RSTile[104][104];
        Queue<RSTile> queue = new LinkedList<>();
        int[][] collisionData = PathFinding.getCollisionData();

        queue.add(localPlayerPosition);
        traversed[localPlayerPosition.getX()][localPlayerPosition.getY()] = true;
        parentMap[localPlayerPosition.getX()][localPlayerPosition.getY()] = null;

        while (!queue.isEmpty()){
            RSTile currentLocal = queue.poll();
            int x = currentLocal.getX(), y = currentLocal.getY();

            int currentCollisionFlags = collisionData[x][y];
            if (!AStarNode.isWalkable(currentCollisionFlags)){
                continue;
            }

            for (Direction direction : Direction.values()){
                if (!direction.isValidDirection(x, y, collisionData)){
                    continue; //Cannot traverse to tile from current.
                }

                RSTile neighbor = direction.getPointingTile(currentLocal);
                int destinationX = neighbor.getX(), destinationY = neighbor.getY();
                if (traversed[destinationX][destinationY]){
                    continue; //Traversed already
                }
                traversed[destinationX][destinationY] = true;
                parentMap[destinationX][destinationY] = currentLocal;
                queue.add(neighbor);
            }

        }
        return parentMap;
    }

    public enum Direction {
        NORTH (0, 1),
        EAST (1, 0),
        SOUTH (0, -1),
        WEST (-1, 0),
        ;

        int x, y;

        Direction (int x, int y){
            this.x = x;
            this.y = y;
        }

        public RSTile getPointingTile(RSTile tile){
            return tile.translate(x, y);
        }

        public boolean isValidDirection(int x, int y, int[][] collisionData){
            switch (this) {
                case NORTH:     return !AStarNode.blockedNorth(collisionData[x][y]);
                case EAST:      return !AStarNode.blockedEast(collisionData[x][y]);
                case SOUTH:     return !AStarNode.blockedSouth(collisionData[x][y]);
                case WEST:      return !AStarNode.blockedWest(collisionData[x][y]);
                default:        return false;
            }
        }
    }

}
