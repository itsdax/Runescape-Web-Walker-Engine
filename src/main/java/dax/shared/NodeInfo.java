package dax.shared;

import dax.walker_engine.real_time_collision.RealTimeCollisionTile;

import java.util.concurrent.ConcurrentHashMap;


public class NodeInfo {

    private static ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Details>>> observableXMap = new ConcurrentHashMap<>(), pathTileXMap = new ConcurrentHashMap<>(), realTime = new ConcurrentHashMap<>();

    public static Details get(PathFindingNode pathFindingNode){

        ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Details>>> xMap = null;
        if (pathFindingNode instanceof RealTimeCollisionTile){
            xMap = realTime;
        }
        if (xMap == null){
            throw new NullPointerException("xMap should never be null for NodeInfo.");
        }

        ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Details>> yMap = xMap.get(pathFindingNode.getX());
        if (yMap == null){
            return null;
        }
        ConcurrentHashMap<Integer, Details> zMap = yMap.get(pathFindingNode.getY());
        if (zMap == null){
            return null;
        }
        Details details = zMap.get(pathFindingNode.getZ());
        if (details == null){
            return null;
        }
        return details;
    }

    public static Details create(PathFindingNode pathFindingNode){
        ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Details>>> xMap = null;
        if (pathFindingNode instanceof RealTimeCollisionTile){
            xMap = realTime;
        }
        if (xMap == null){
            throw new NullPointerException("xMap should never be null for NodeInfo.");
        }
        ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Details>> yMap = xMap.computeIfAbsent(pathFindingNode.getX(), k -> new ConcurrentHashMap<>());
        ConcurrentHashMap<Integer, Details> zMap = yMap.computeIfAbsent(pathFindingNode.getY(), k -> new ConcurrentHashMap<>());
        return zMap.computeIfAbsent(pathFindingNode.getZ(), k -> new Details());
    }


    public static void clearMemory(Class c){
//        System.out.println("Clearing memory of " + c.getSimpleName());
        if (c == RealTimeCollisionTile.class){
            realTime.clear();
            realTime = new ConcurrentHashMap<>();
        }
    }

    public static class Details {
        public PathFindingNode parent;
        public int moveCost, heuristic, f;
        public boolean current, traversed, start, end, path;
    }

}
