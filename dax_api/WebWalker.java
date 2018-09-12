package scripts.dax_api;

import org.tribot.api.interfaces.Positionable;
import org.tribot.api.util.abc.ABCUtil;
import org.tribot.api2007.Game;
import org.tribot.api2007.Options;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSTile;
import scripts.dax_api.walker_engine.WalkerEngine;
import scripts.dax_api.walker_engine.WalkingCondition;
import scripts.dax_api.walker_engine.bfs.BFS;
import scripts.dax_api.shared.helpers.BankHelper;
import scripts.dax_api.teleport_logic.TeleportManager;

import java.util.ArrayList;

public class WebWalker {

    private static final WalkingCondition EMPTY_WALKING_CONDITION = () -> WalkingCondition.State.CONTINUE_WALKER;
    private final String version = "1.1.3"; //TODO: Grab from server

    private static WebWalker instance;
    private boolean logging;
    private WalkingCondition globalWalkingCondition;
    private int runAt;

    private org.tribot.api.util.ABCUtil abcUtilV1;
    private ABCUtil abcUtilV2;

    private WebWalker() {
        this(new ABCUtil());
    }

    private WebWalker(ABCUtil util) {
        logging = true;
        abcUtilV2 = util;
        runAt = abcUtilV2.generateRunActivation();
        globalWalkingCondition = () -> {
            if (!Game.isRunOn() && Game.getRunEnergy() > runAt){
                Options.setRunOn(true);
                runAt = abcUtilV2.generateRunActivation();
            }
            return WalkingCondition.State.CONTINUE_WALKER;
        };
    }

    private WebWalker(org.tribot.api.util.ABCUtil util) {
        logging = true;
        abcUtilV1 = util;
        runAt = abcUtilV1.INT_TRACKER.NEXT_RUN_AT.next();
        globalWalkingCondition = () -> {
            if (!Game.isRunOn() && Game.getRunEnergy() > runAt){
                Options.setRunOn(true);
                runAt = abcUtilV1.INT_TRACKER.NEXT_RUN_AT.next();
                abcUtilV1.INT_TRACKER.NEXT_RUN_AT.reset();
            }
            return WalkingCondition.State.CONTINUE_WALKER;
        };
    }

    public WalkingCondition getGlobalWalkingCondition() {
        return globalWalkingCondition;
    }

    private static WebWalker getInstance(){
        return instance != null ? instance : (instance = new WebWalker());
    }

    private static WebWalker getInstance(ABCUtil abcUtil){
        return instance != null ? instance : (instance = new WebWalker(abcUtil));
    }

    private static WebWalker getInstance(org.tribot.api.util.ABCUtil abcUtil){
        return instance != null ? instance : (instance = new WebWalker(abcUtil));
    }

    /**
     *
     * @return Whether the walker will be outputting debug.
     */
    public static boolean isLogging(){
        return getInstance().logging;
    }

    /**
     *
     * @param value True to output debug.
     */
    public static void setLogging(boolean value){
        getInstance().logging = value;
    }

    public enum Offset {
        NONE (0), LOW (8), MEDIUM (16), HIGH (24), VERY_HIGH(32);
        private int value;
        Offset (int value){
            this.value = value;
        }
    }

    /**
     *
     * @param offset How much we deviate from the original path.
     */
    public static void setPathOffset(Offset offset){
        BFS.OFFSET_SEARCH = offset.value;
    }

    /**
     * Sets the global walking condition. {@code walkingCondition} will be automatically
     * set in wall webwalker calls.
     *
     * @param walkingCondition global walking condition
     */
    public static void setGlobalWalkingCondition(WalkingCondition walkingCondition){
        getInstance().globalWalkingCondition = walkingCondition;
    }

    /**
     *
     * @return version number of webwalker.
     */
    public static String getVersion(){
        return getInstance().version;
    }

    /**
     *
     * @param destination
     * @return Whether destination was successfully reached.
     */
    public static boolean walkTo(Positionable destination){
        return walkTo(destination, EMPTY_WALKING_CONDITION);
    }

    /**
     *
     * @param destination
     * @param walkingCondition Refer to @{@link WalkingCondition}. #WalkingCondition action()
     *                         is called roughly 3- 5 times every second unless walker engine
     *                         is handling an object.
     * @return Whether destination was successfully reached or depending on what your walking
     *         condition returns.
     */
    public static boolean walkTo(Positionable destination, WalkingCondition walkingCondition){
        if (Player.getPosition().equals(destination)){
            return true;
        }
        ArrayList<RSTile> path = WebPath.getPath(destination.getPosition());
        if (path.size() == 0){
            return false;
        }
        ArrayList<RSTile> bestPath = TeleportManager.teleport(path.size(), destination.getPosition());
        if (bestPath != null){
            path = bestPath;
        }
        return WalkerEngine.getInstance().walkPath(path, walkingCondition.combine(getInstance().globalWalkingCondition));
    }

    public static boolean walkToBank(){
        return walkToBank(EMPTY_WALKING_CONDITION);
    }

    public static boolean walkToBank(WalkingCondition walkingCondition) {
        if (BankHelper.isInBank()){
            System.out.println("already in bank");
            return true;
        }

        ArrayList<RSTile> bankPath = WebPath.getPathToBank();

        if (bankPath.size() == 0){
            return false;
        }

        ArrayList<RSTile> bestPath = TeleportManager.teleport(bankPath.size(), bankPath.get(bankPath.size() - 1));
        if (bestPath != null){
            bankPath = bestPath;
        }

        return WalkerEngine.getInstance().walkPath(bankPath, ((WalkingCondition) () -> {
            RSTile destination = Game.getDestination();
            return destination != null && BankHelper.isInBank(destination) ? WalkingCondition.State.EXIT_OUT_WALKER_SUCCESS : WalkingCondition.State.CONTINUE_WALKER;
        }).combine(walkingCondition.combine(getInstance().globalWalkingCondition)));
    }

    public static void setLocal(boolean b){
        if (b){
            setApiKey("8d18a428-5474-40d3-aec8-0bc79243ad05", "3E5C311D338664EE");
            System.out.println("Switching to local api key");
        }
        WebPathCore.setLocal(b);
    }

    public static void setApiKey(String apiKey, String secretKey){
        WebPathCore.setAuth(apiKey, secretKey);
    }

    private static void setABCUtil(ABCUtil util) {
        WebWalker walker = getInstance(util);

        if (walker.abcUtilV2 != util) {
            walker.abcUtilV2.close();
            walker.abcUtilV2 = util;
        }

        if (walker.abcUtilV1 != null) {
            walker.abcUtilV1 = null;
        }
    }

    private static void setABCUtil(org.tribot.api.util.ABCUtil util) {
        WebWalker walker = getInstance(util);

        if (walker.abcUtilV1 != util) {
            walker.abcUtilV1 = null;
            walker.abcUtilV1 = util;
        }

        if (walker.abcUtilV2 != null) {
            walker.abcUtilV2.close();
        }
    }
}
