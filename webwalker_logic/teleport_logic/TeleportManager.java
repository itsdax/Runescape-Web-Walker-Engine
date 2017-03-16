package scripts.webwalker_logic.teleport_logic;

import org.tribot.api.General;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSTile;
import scripts.webwalker_logic.WebPath;
import scripts.webwalker_logic.local.walker_engine.Loggable;
import scripts.webwalker_logic.local.walker_engine.WaitFor;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class TeleportManager implements Loggable {

    private int offset;
    private HashSet<TeleportMethod> blacklistTeleportMethods;

    private static TeleportManager teleportManager;
    private static TeleportManager getInstance(){
        return teleportManager != null ? teleportManager : (teleportManager = new TeleportManager());
    }

    public TeleportManager(){
        offset = 25;
        blacklistTeleportMethods = new HashSet<>();
    }

    /**
     * Blacklist teleport methods. This method is NOT threadsafe.
     * @param teleportMethods
     */
    public static void ignoreTeleportMethods(TeleportMethod... teleportMethods){
        getInstance().blacklistTeleportMethods.addAll(Arrays.asList(teleportMethods));
    }

    /**
     * Clears blacklist
     */
    public static void clearBlackList(){
        getInstance().blacklistTeleportMethods.clear();
    }

    /**
     * Sets the threshold of tile difference that will trigger teleport action.
     * @param offset distance in tiles
     */
    public static void setOffset(int offset){
        getInstance().offset = offset;
    }

    public static ArrayList<RSTile> teleport(int originalPathLength, RSTile destination){
        if (originalPathLength < getInstance().offset){
            return null;
        }

        List<TeleportAction> teleportActions =
                Arrays.stream(TeleportMethod.values()).parallel().map(teleportMethod ->
                        Arrays.stream((teleportMethod.canUse() && !getInstance().blacklistTeleportMethods.contains(teleportMethod) ? teleportMethod.getDestinations() : new TeleportLocation[]{})).parallel().map(teleportLocation ->
                                new TeleportAction(WebPath.getPath(teleportLocation.getRSTile(), destination), teleportMethod, teleportLocation)
                        )
                ).flatMap(teleportActionStream -> teleportActionStream).collect(Collectors.toList());

        TeleportAction teleportAction = teleportActions.stream().min(Comparator.comparingInt(o -> o.path.size())).orElse(null);

        if (teleportAction == null || teleportAction.path.size() >= originalPathLength || teleportAction.path.size() == 0){
            return null;
        }

        if (originalPathLength - teleportAction.path.size() < getInstance().offset){
            getInstance().log("No efficient teleports!");
            return null;
        }

        getInstance().log("We will be using " + teleportAction.teleportMethod + " to " + teleportAction.teleportLocation);
        if (!teleportAction.teleportMethod.use(teleportAction.teleportLocation)){
            getInstance().log("Failed to teleport");
        }
        WaitFor.condition(General.random(3000, 54000), () -> teleportAction.teleportLocation.getRSTile().distanceTo(Player.getPosition()) < 10 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
        return teleportAction.path;

    }

    private static class TeleportAction {
        ArrayList<RSTile> path;
        TeleportMethod teleportMethod;
        TeleportLocation teleportLocation;
        TeleportAction(ArrayList<RSTile> path, TeleportMethod teleportMethod, TeleportLocation teleportLocation){
            this.path = path;
            this.teleportMethod = teleportMethod;
            this.teleportLocation = teleportLocation;
        }
    }

    @Override
    public String getName() {
        return "Teleport Manager";
    }
}
