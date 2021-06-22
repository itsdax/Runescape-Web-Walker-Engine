package scripts.dax_api.walker_engine.real_time_collision;

import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSTile;


public class CollisionDataCollector {

    public static void generateRealTimeCollision(){
        RealTimeCollisionTile.clearMemory();

        RSTile playerPosition = Player.getPosition();
        int[][] collisionData = PathFinding.getCollisionData();

        if (collisionData == null) {
            return;
        }

        for (int i = 0; i < collisionData.length; i++) {
            for (int j = 0; j < collisionData[i].length; j++) {
                RSTile localTile = new RSTile(i, j, playerPosition.getPlane(), RSTile.TYPES.LOCAL);
                RSTile worldTile = localTile.toWorldTile();
                RealTimeCollisionTile.create(worldTile.getX(), worldTile.getY(), worldTile.getPlane(), collisionData[i][j]);
            }
        }
    }

    public static void updateRealTimeCollision(){
        RSTile playerPosition = Player.getPosition();
        int[][] collisionData = PathFinding.getCollisionData();
        if(collisionData == null)
            return;
        for (int i = 0; i < collisionData.length; i++) {
            for (int j = 0; j < collisionData[i].length; j++) {
                RSTile localTile = new RSTile(i, j, playerPosition.getPlane(), RSTile.TYPES.LOCAL);
                RSTile worldTile = localTile.toWorldTile();
                RealTimeCollisionTile realTimeCollisionTile = RealTimeCollisionTile.get(worldTile.getX(), worldTile.getY(), worldTile.getPlane());
                if (realTimeCollisionTile != null){
                    realTimeCollisionTile.setCollisionData(collisionData[i][j]);
                } else {
                    RealTimeCollisionTile.create(worldTile.getX(), worldTile.getY(), worldTile.getPlane(), collisionData[i][j]);
                }
            }
        }
    }

}
