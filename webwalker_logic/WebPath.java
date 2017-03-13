package scripts.webwalker_logic;

import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSTile;
import scripts.webwalker_logic.shared.GetPathResponseContainer;
import scripts.webwalker_logic.shared.PlayerInformation;

import java.util.ArrayList;

public class WebPath {

    /**
     *
     * @param destination
     * @return Path from current player position to destination
     */
    public static ArrayList<RSTile> getPath(RSTile destination){
        return getPathResponseContainer(Player.getPosition(), destination).getRSTilePath();
    }

    public static ArrayList<RSTile> getPath(RSTile start, RSTile destination){
        return getPathResponseContainer(start, destination).getRSTilePath();
    }

    /**
     *
     * @return Path to bank
     */
    public static ArrayList<RSTile> getPathToBank(){
        return getPathToBank(Player.getPosition()).getRSTilePath();
    }

    /**
     *
     * @return Previous path generated, even if the last call was null.
     */
    public static ArrayList<RSTile> previousPath(){
        return WebPathCore.previousPath();
    }

    private static GetPathResponseContainer getPathToBank(RSTile position){
        PlayerInformation playerInformation = PlayerInformation.generatePlayerInformation();
        return WebPathCore.getPathToBank(position.getX(), position.getY(), position.getPlane(), playerInformation);
    }

    private static GetPathResponseContainer getPathResponseContainer(RSTile start, RSTile end){
        PlayerInformation playerInformation = PlayerInformation.generatePlayerInformation();
        return WebPathCore.getPath(start.getX(), start.getY(), start.getPlane(), end.getX(), end.getY(), end.getPlane(), playerInformation);
    }



}
