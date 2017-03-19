package scripts.webwalker_logic.teleport_logic;

import org.tribot.api.General;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSTile;
import scripts.webwalker_logic.WebPath;
import scripts.webwalker_logic.local.walker_engine.Loggable;
import scripts.webwalker_logic.local.walker_engine.WaitFor;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class TeleportManager implements Loggable {

    private int offset;
    private HashSet<TeleportMethod> blacklistTeleportMethods;
    private ExecutorService executorService;

    private static TeleportManager teleportManager;
    private static TeleportManager getInstance(){
        return teleportManager != null ? teleportManager : (teleportManager = new TeleportManager());
    }

    public TeleportManager(){
        offset = 25;
        blacklistTeleportMethods = new HashSet<>();
        executorService = Executors.newFixedThreadPool(5);
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

        TeleportAction teleportAction = Arrays.stream(TeleportMethod.values())
                .filter(TeleportMethod::canUse)
                        .map(teleportMethod -> Arrays.stream(teleportMethod.getDestinations()) //map to destinations
                        .map(teleportLocation -> getInstance().executorService.submit(new PathComputer(teleportMethod, teleportLocation, destination)))) //map to future
                .flatMap(futureStream -> futureStream).collect(Collectors.toList()).stream().map(teleportActionFuture -> { //flatten out futures
                    try {
                        return teleportActionFuture.get();
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                        return null;
                    }
                }).filter(teleportAction1 -> teleportAction1 != null && teleportAction1.path.size() > 0).min(Comparator.comparingInt(o -> o.path.size())).orElse(null);

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

    private static class PathComputer implements Callable<TeleportAction>{

        private TeleportMethod teleportMethod;
        private TeleportLocation teleportLocation;
        private RSTile destination;

        private PathComputer(TeleportMethod teleportMethod, TeleportLocation teleportLocation, RSTile destination){
            this.teleportMethod = teleportMethod;
            this.teleportLocation = teleportLocation;
            this.destination = destination;
        }

        @Override
        public TeleportAction call() throws Exception {
            return new TeleportAction(WebPath.getPath(teleportLocation.getRSTile(), destination), teleportMethod, teleportLocation);
        }

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
