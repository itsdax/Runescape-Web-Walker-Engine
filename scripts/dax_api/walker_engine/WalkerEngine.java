package scripts.dax_api.walker_engine;


import org.tribot.api.General;
import org.tribot.api.input.Mouse;
import org.tribot.api2007.*;
import org.tribot.api2007.types.RSTile;
import scripts.dax_api.teleports.Teleport;
import scripts.dax_api.shared.PathFindingNode;
import scripts.dax_api.walker.utils.AccurateMouse;
import scripts.dax_api.walker.utils.path.PathUtils;
import scripts.dax_api.walker_engine.bfs.BFS;
import scripts.dax_api.walker_engine.interaction_handling.PathObjectHandler;
import scripts.dax_api.walker_engine.local_pathfinding.PathAnalyzer;
import scripts.dax_api.walker_engine.local_pathfinding.Reachable;
import scripts.dax_api.walker_engine.navigation_utils.Charter;
import scripts.dax_api.walker_engine.navigation_utils.NavigationSpecialCase;
import scripts.dax_api.walker_engine.navigation_utils.ShipUtils;
import scripts.dax_api.walker_engine.real_time_collision.CollisionDataCollector;
import scripts.dax_api.walker_engine.real_time_collision.RealTimeCollisionTile;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class WalkerEngine implements Loggable{

    private static WalkerEngine walkerEngine;

    private int attemptsForAction;
    private final int failThreshold;
    private boolean navigating;
    private ArrayList<RSTile> currentPath;

    private WalkerEngine(){
        attemptsForAction = 0;
        failThreshold = 3;
        navigating = false;
        currentPath = null;
    }

    public static WalkerEngine getInstance(){
        return walkerEngine != null ? walkerEngine : (walkerEngine = new WalkerEngine());
    }

    public boolean walkPath(ArrayList<RSTile> path){
        return walkPath(path, null);
    }

    public ArrayList<RSTile> getCurrentPath() {
        return currentPath;
    }

    /**
     *
     * @param path
     * @param walkingCondition
     * @return
     */
    public boolean walkPath(ArrayList<RSTile> path, WalkingCondition walkingCondition){
        if (path.size() == 0) {
            log("Path is empty");
            return false;
        }


        if (!handleTeleports(path)) {
            log(Level.WARNING, "Failed to handle teleports...");
            return false;
        }


        navigating = true;
        currentPath = path;
        try {
            PathAnalyzer.DestinationDetails destinationDetails;
            resetAttempts();

            while (true) {

                if (Login.getLoginState() != Login.STATE.INGAME){
                    return false;
                }

                if (ShipUtils.isOnShip()) {
                    if (!ShipUtils.crossGangplank()) {
                        log("Failed to exit ship via gangplank.");
                        failedAttempt();
                    }
                    WaitFor.milliseconds(50);
                    continue;
                }

                if (isFailedOverThreshhold()) {
                    log("Too many failed attempts");
                    return false;
                }

                destinationDetails = PathAnalyzer.furthestReachableTile(path);
                if (PathUtils.getFurthestReachableTileInMinimap(path) == null || destinationDetails == null) {
                    log("Could not grab destination details.");
                    failedAttempt();
                    continue;
                }

                RealTimeCollisionTile currentNode = destinationDetails.getDestination();
                RSTile assumedNext = destinationDetails.getAssumed();

                if (destinationDetails.getState() != PathAnalyzer.PathState.FURTHEST_CLICKABLE_TILE) {
                    log(destinationDetails.toString());
                }

                final RealTimeCollisionTile destination = currentNode;
                if (!Projection.isInMinimap(Projection.tileToMinimap(new RSTile(destination.getX(), destination.getY(), destination.getZ())))) {
                    log("Closest tile in path is not in minimap: " + destination);
                    failedAttempt();
                    continue;
                }

                CustomConditionContainer conditionContainer = new CustomConditionContainer(walkingCondition);
                switch (destinationDetails.getState()) {
                    case DISCONNECTED_PATH:
                        if (currentNode.getRSTile().distanceTo(Player.getPosition()) > 10){
                            clickMinimap(currentNode);
                            WaitFor.milliseconds(1200, 3400);
                        }
                        NavigationSpecialCase.SpecialLocation specialLocation = NavigationSpecialCase.getLocation(currentNode.getRSTile()),
                                specialLocationDestination = NavigationSpecialCase.getLocation(assumedNext);
                        if (specialLocation != null && specialLocationDestination != null) {
                            log("[SPECIAL LOCATION] We are at " + specialLocation + " and our destination is " + specialLocationDestination);
                            if (!NavigationSpecialCase.handle(specialLocationDestination)) {
                                failedAttempt();
                            } else {
                                successfulAttempt();
                            }
                            break;
                        }

                        Charter.LocationProperty
                                locationProperty = Charter.LocationProperty.getLocation(currentNode.getRSTile()),
                                destinationProperty = Charter.LocationProperty.getLocation(assumedNext);
                        if (locationProperty != null && destinationProperty != null) {
                            log("Chartering to: " + destinationProperty);
                            if (!Charter.to(destinationProperty)) {
                                failedAttempt();
                            } else {
                                successfulAttempt();
                            }
                            break;
                        }
                        //DO NOT BREAK OUT
                    case OBJECT_BLOCKING:
                        RSTile walkingTile = Reachable.getBestWalkableTile(destination.getRSTile(), new Reachable());
                        if (isDestinationClose(destination) || (walkingTile != null ? AccurateMouse.clickMinimap(walkingTile) : clickMinimap(destination))) {
                            log("Handling Object...");
                            if (!PathObjectHandler.handle(destinationDetails, path)) {
                                failedAttempt();
                            } else {
                                successfulAttempt();
                            }
                            break;
                        }
                        break;

                    case FURTHEST_CLICKABLE_TILE:
                        if (clickMinimap(currentNode)) {
                            long offsetWalkingTimeout = System.currentTimeMillis() + General.random(2500, 4000);
                            WaitFor.condition(10000, () -> {
                                switch (conditionContainer.trigger()) {
                                    case EXIT_OUT_WALKER_SUCCESS:
                                    case EXIT_OUT_WALKER_FAIL:
                                        return WaitFor.Return.SUCCESS;
                                }

                                PathAnalyzer.DestinationDetails furthestReachable = PathAnalyzer.furthestReachableTile(path);
                                PathFindingNode currentDestination = BFS.bfsClosestToPath(path, RealTimeCollisionTile.get(destination.getX(), destination.getY(), destination.getZ()));
                                if (currentDestination == null) {
                                    log("Could not walk to closest tile in path.");
                                    failedAttempt();
                                    return WaitFor.Return.FAIL;
                                }
                                int indexCurrentDestination = path.indexOf(currentDestination.getRSTile());

                                PathFindingNode closestToPlayer = PathAnalyzer.closestTileInPathToPlayer(path);
                                if (closestToPlayer == null) {
                                    log("Could not detect closest tile to player in path.");
                                    failedAttempt();
                                    return WaitFor.Return.FAIL;
                                }
                                int indexCurrentPosition = path.indexOf(closestToPlayer.getRSTile());
                                if (furthestReachable == null) {
                                    System.out.println("Furthest reachable is null/");
                                    return WaitFor.Return.FAIL;
                                }
                                int indexNextDestination = path.indexOf(furthestReachable.getDestination().getRSTile());
                                if (indexNextDestination - indexCurrentDestination > 5 || indexCurrentDestination - indexCurrentPosition < 5) {
                                    return WaitFor.Return.SUCCESS;
                                }
                                if (System.currentTimeMillis() > offsetWalkingTimeout && !Player.isMoving()){
                                    return WaitFor.Return.FAIL;
                                }
                                return WaitFor.milliseconds(100);
                            });
                        }
                        break;

                    case END_OF_PATH:
                        clickMinimap(destinationDetails.getDestination());
                        log("Reached end of path");
                        return true;
                }

                switch (conditionContainer.getResult()) {
                    case EXIT_OUT_WALKER_SUCCESS:
                        return true;
                    case EXIT_OUT_WALKER_FAIL:
                        return false;
                }

                WaitFor.milliseconds(50, 100);

            }
        } finally {
            navigating = false;
        }
    }

    boolean isNavigating() {
        return navigating;
    }

    boolean isDestinationClose(PathFindingNode pathFindingNode){
        final RSTile playerPosition = Player.getPosition();
        return new RSTile(pathFindingNode.getX(), pathFindingNode.getY(), pathFindingNode.getZ()).isClickable()
                && playerPosition.distanceTo(new RSTile(pathFindingNode.getX(), pathFindingNode.getY(), pathFindingNode.getZ())) <= 12
                && (BFS.isReachable(RealTimeCollisionTile.get(playerPosition.getX(), playerPosition.getY(), playerPosition.getPlane()), RealTimeCollisionTile.get(pathFindingNode.getX(), pathFindingNode.getY(), pathFindingNode.getZ()), 200));
    }

    public boolean clickMinimap(PathFindingNode pathFindingNode){
        final RSTile playerPosition = Player.getPosition();
        if (playerPosition.distanceTo(pathFindingNode.getRSTile()) <= 1){
            return true;
        }
        PathFindingNode randomNearby = BFS.getRandomTileNearby(pathFindingNode);

        if (randomNearby == null){
            log("Unable to generate randomization.");
            return false;
        }

        log("Randomize(" + pathFindingNode.getX() + "," + pathFindingNode.getY() + "," + pathFindingNode.getZ() + ") -> (" + randomNearby.getX() + "," + randomNearby.getY() + "," + randomNearby.getZ() + ")");
        return AccurateMouse.clickMinimap(new RSTile(randomNearby.getX(), randomNearby.getY(), randomNearby.getZ())) || AccurateMouse.clickMinimap(new RSTile(pathFindingNode.getX(), pathFindingNode.getY(), pathFindingNode.getZ()));
    }

    public void hoverMinimap(PathFindingNode pathFindingNode){
        if (pathFindingNode == null){
            return;
        }
        Point point = Projection.tileToMinimap(new RSTile(pathFindingNode.getX(), pathFindingNode.getY(), pathFindingNode.getZ()));
        Mouse.move(point);
    }

    private boolean resetAttempts(){
        return successfulAttempt();
    }

    private boolean successfulAttempt(){
        attemptsForAction = 0;
        return true;
    }

    private void failedAttempt(){
        if (Camera.getCameraAngle() < 90) {
            Camera.setCameraAngle(General.random(90, 100));
        }
        if (++attemptsForAction > 1) {
            Camera.setCameraRotation(General.random(0, 360));
        }
        log("Failed attempt on action.");
        WaitFor.milliseconds(450 * (attemptsForAction + 1), 850 * (attemptsForAction + 1));
        CollisionDataCollector.generateRealTimeCollision();
    }

    private boolean isFailedOverThreshhold(){
        return attemptsForAction >= failThreshold;
    }

    private class CustomConditionContainer {
        private WalkingCondition walkingCondition;
        private WalkingCondition.State result;
        CustomConditionContainer(WalkingCondition walkingCondition){
            this.walkingCondition = walkingCondition;
            this.result = WalkingCondition.State.CONTINUE_WALKER;
        }
        public WalkingCondition.State trigger(){
            result = (walkingCondition != null ? walkingCondition.action() : result);
            return result != null ? result : WalkingCondition.State.CONTINUE_WALKER;
        }
        public WalkingCondition.State getResult() {
            return result;
        }
    }

    @Override
    public String getName() {
        return "Walker Engine";
    }

    private boolean handleTeleports(List<RSTile> path) {
        RSTile startPosition = path.get(0);
        RSTile playerPosition = Player.getPosition();
        if(startPosition.equals(playerPosition))
            return true;
        if(Banking.isBankScreenOpen())
            Banking.close();
        for (Teleport teleport : Teleport.values()) {
            if (!teleport.getRequirement().satisfies()) continue;
            if(teleport.isAtTeleportSpot(startPosition)){
                log("Using teleport method: " + teleport);
                teleport.trigger();
                return WaitFor.condition(General.random(3000, 20000),
                        () -> startPosition.distanceTo(Player.getPosition()) < 10 ?
                                WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
            }
        }
        return true;
    }

}
