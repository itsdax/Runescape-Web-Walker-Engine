package scripts.webwalker_logic.shared.helpers;

import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.Game;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Player;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;

import java.util.HashSet;

public class BankHelper {

    public static boolean isInBank(){
        return isInBank(Player.getPosition());
    }

    public static boolean isInBank(Positionable positionable){
        RSObject[] bankObjects = Objects.findNearest(15, Filters.Objects.nameContains("bank", "Bank", "Exchange booth", "Open chest").combine(Filters.Objects.actionsContains("Collect"), true));
        if (bankObjects.length == 0){
            return false;
        }
        RSObject bankObject = bankObjects[0];
        HashSet<RSTile> building = getBuilding(bankObject);
        return building.contains(positionable.getPosition()) || (building.size() == 0 && positionable.getPosition().distanceTo(bankObject) < 5);
    }

    private static HashSet<RSTile> getBuilding(Positionable positionable){
        return computeBuilding(positionable, Game.getSceneFlags(), new HashSet<>());
    }

    private static HashSet<RSTile> computeBuilding(Positionable positionable, byte[][][] sceneFlags, HashSet<RSTile> tiles){
        RSTile local = positionable.getPosition().toLocalTile();
        int localX = local.getX(), localY = local.getY(), localZ = local.getPlane();
        if (localX == -1 || localY == -1 || localZ == -1){
            return tiles;
        }
        if (sceneFlags.length <= localZ || sceneFlags[localZ].length <= localX || sceneFlags[localZ][localX].length <= localY){ //Not within bounds
            return tiles;
        }
        if (sceneFlags[localZ][localX][localY] < 4){ //Not a building
            return tiles;
        }
        if (!tiles.add(local.toWorldTile())){ //Already computed
            return tiles;
        }
        computeBuilding(new RSTile(localX, localY + 1, localZ, RSTile.TYPES.LOCAL).toWorldTile(), sceneFlags, tiles);
        computeBuilding(new RSTile(localX + 1, localY, localZ, RSTile.TYPES.LOCAL).toWorldTile(), sceneFlags, tiles);
        computeBuilding(new RSTile(localX, localY - 1, localZ, RSTile.TYPES.LOCAL).toWorldTile(), sceneFlags, tiles);
        computeBuilding(new RSTile(localX - 1, localY, localZ, RSTile.TYPES.LOCAL).toWorldTile(), sceneFlags, tiles);
        return tiles;
    }

    private static boolean isInBuilding(RSTile localRSTile, byte[][][] sceneFlags) {
        return !(sceneFlags.length <= localRSTile.getPlane()
                    || sceneFlags[localRSTile.getPlane()].length <= localRSTile.getX()
                    || sceneFlags[localRSTile.getPlane()][localRSTile.getX()].length <= localRSTile.getY())
                && sceneFlags[localRSTile.getPlane()][localRSTile.getX()][localRSTile.getY()] >= 4;
    }

}
