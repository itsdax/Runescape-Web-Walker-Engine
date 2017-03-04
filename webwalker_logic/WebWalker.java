package scripts.webwalker_logic;


import org.tribot.api2007.Game;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSTile;
import scripts.webwalker_logic.local.walker_engine.WalkerEngine;
import scripts.webwalker_logic.local.walker_engine.WalkingCondition;
import scripts.webwalker_logic.local.walker_engine.bfs.BFS;
import scripts.webwalker_logic.shared.helpers.BankHelper;

import java.util.ArrayList;

public class WebWalker {

    private static WebWalker instance;
    private boolean logging;

    private WebWalker(){
        logging = true;
    }

    private static WebWalker getInstance(){
        return instance != null ? instance : (instance = new WebWalker());
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
     *
     * @param destination
     * @return Whether destination was successfully reached.
     */
    public static boolean walkTo(RSTile destination){
        return walkTo(destination, null);
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
    public static boolean walkTo(RSTile destination, WalkingCondition walkingCondition){
        ArrayList<RSTile> path = WebPath.getPath(destination);
        if (!Player.getPosition().equals(destination) && path.size() == 0){
            return false;
        }
        return WalkerEngine.getInstance().walkPath(path, walkingCondition);
    }

    public static boolean walkToBank(){
        return walkToBank(null);
    }

    public static boolean walkToBank(WalkingCondition walkingCondition) {
        return BankHelper.isInBank() || WalkerEngine.getInstance().walkPath(WebPath.getPathToBank(), ((WalkingCondition) () -> {
            RSTile destination = Game.getDestination();
            return destination != null && BankHelper.isInBank(destination) ? WalkingCondition.State.EXIT_OUT_WALKER_SUCCESS : WalkingCondition.State.CONTINUE_WALKER;
        }).combine(walkingCondition));
    }

    public static void setLocal(boolean b){
        WebPathCore.setLocal(b);
    }

}
