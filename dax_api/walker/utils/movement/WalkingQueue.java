package scripts.dax_api.walker.utils.movement;

import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSCharacter;
import org.tribot.api2007.types.RSTile;

import java.util.ArrayList;


public class WalkingQueue {

    public static boolean isWalkingTowards(RSTile tile){
        RSTile tile1 = getWalkingTowards();
        return tile1 != null && tile1.equals(tile);
    }

    public static RSTile getWalkingTowards(){
        ArrayList<RSTile> tiles = getWalkingQueue();
        return tiles.size() > 0 && !tiles.get(0).equals(Player.getPosition()) ? tiles.get(0) : null;
    }

    public static ArrayList<RSTile> getWalkingQueue(){
        return getWalkingQueue(Player.getRSPlayer());
    }

    public static RSTile getWalkingTowards(RSCharacter rsCharacter){
        ArrayList<RSTile> tiles = getWalkingQueue(rsCharacter);
        return tiles.size() > 0 && !tiles.get(0).equals(rsCharacter.getPosition()) ? tiles.get(0) : null;
    }

    public static ArrayList<RSTile> getWalkingQueue(RSCharacter rsCharacter){
        ArrayList<RSTile> walkingQueue = new ArrayList<>();
        if (rsCharacter == null){
            return walkingQueue;
        }
        int[] xIndex = rsCharacter.getWalkingQueueX(), yIndex = rsCharacter.getWalkingQueueY();
        int plane = rsCharacter.getPosition().getPlane();

        for (int i = 0; i < xIndex.length && i < yIndex.length; i++) {
            walkingQueue.add(new RSTile(xIndex[i], yIndex[i], plane, RSTile.TYPES.LOCAL).toWorldTile());
        }
        return walkingQueue;
    }

}
