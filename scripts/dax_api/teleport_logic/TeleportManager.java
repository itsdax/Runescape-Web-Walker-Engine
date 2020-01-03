package scripts.dax_api.teleport_logic;

import org.tribot.api.General;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSTile;
import scripts.dax_api.api_lib.WebWalkerServerApi;
import scripts.dax_api.api_lib.models.PathResult;
import scripts.dax_api.api_lib.models.PlayerDetails;
import scripts.dax_api.api_lib.models.Point3D;
import scripts.dax_api.api_lib.models.RunescapeBank;
import scripts.dax_api.walker_engine.Loggable;
import scripts.dax_api.walker_engine.WaitFor;

import java.util.*;
import java.util.concurrent.*;

import static scripts.dax_api.api_lib.models.PathStatus.RATE_LIMIT_EXCEEDED;
import static scripts.dax_api.api_lib.models.PathStatus.SUCCESS;


public class TeleportManager implements Loggable {

    public static TeleportAction previousAction;

    private static int offset;
    private HashSet<TeleportMethod> blacklistTeleportMethods;
    private HashSet<TeleportLocation> blacklistTeleportLocations;
    private ExecutorService executorService;
    private ExecutorService customThreadPool = new ForkJoinPool(10);


    private static TeleportManager teleportManager;

    private static TeleportManager getInstance() {
        return teleportManager != null ? teleportManager : (teleportManager = new TeleportManager());
    }

    public TeleportManager() {
        offset = 25;
        blacklistTeleportMethods = new HashSet<>();
        blacklistTeleportLocations = new HashSet<>();
        executorService = Executors.newFixedThreadPool(15);
    }

    /**
     * Blacklist teleport methods. This method is NOT threadsafe.
     *
     * @param teleportMethods
     */
    public static void ignoreTeleportMethods(TeleportMethod... teleportMethods) {
        getInstance().blacklistTeleportMethods.addAll(Arrays.asList(teleportMethods));
    }

    public static void ignoreTeleportLocations(TeleportLocation... teleportLocations) {
        getInstance().blacklistTeleportLocations.addAll(Arrays.asList(teleportLocations));
    }

    public static void clearTeleportMethodBlackList() {
        getInstance().blacklistTeleportMethods.clear();
    }

    public static void clearTeleportLocationBlackList() {
        getInstance().blacklistTeleportLocations.clear();
    }

    /**
     * Sets the threshold of tile difference that will trigger teleport action.
     *
     * @param offset distance in tiles
     */
    public static void setOffset(int offset) {
        getInstance().offset = offset;
    }

    public static ArrayList<RSTile> getClosestBankPath(RunescapeBank bank, int originalMoveCost) {
        final int cost = originalMoveCost - offset;
        List<TeleportWrapper> teleport = new CopyOnWriteArrayList<>();

        try {
            getInstance().customThreadPool.submit(() -> Arrays.stream(TeleportMethod.values()).parallel().forEach(teleportMethod -> {
                if (getInstance().blacklistTeleportMethods.contains(teleportMethod)) {
                    return;
                }
                if (!teleportMethod.canUse()) {
                    return;
                }
                for (TeleportLocation teleportLocation : teleportMethod.getDestinations()) {
                    if (getInstance().blacklistTeleportLocations.contains(teleportLocation)) {
                        continue;
                    }
                    PathResult pathResult = WebWalkerServerApi.getInstance().getBankPath(
                            Point3D.fromPositionable(teleportLocation.getRSTile()),
                            bank,
                            PlayerDetails.generate()
                    );
                    teleport.add(new TeleportWrapper(pathResult, teleportMethod, teleportLocation));
                }
            })).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        boolean[] rateLimit = new boolean[]{false};

        TeleportWrapper closest = teleport.stream()
                .filter(pathResult -> {
                    if (pathResult.getPathResult().getPathStatus() == RATE_LIMIT_EXCEEDED) {
                        rateLimit[0] = true;
                    }
                    return pathResult.getPathResult().getPathStatus() == SUCCESS && pathResult.getPathResult().getCost() < cost;
                })
                .min(Comparator.comparingInt(value -> value.getPathResult().getCost()))
                .orElse(null);

        if (rateLimit[0]) {
            return null;
        }

        if (closest == null) {
            return null;
        }

        getInstance().log("Found shorter path with teleport: " + closest.getTeleportMethod() + " > " + closest.getTeleportLocation() + " (" + originalMoveCost);

        if (!closest.getTeleportMethod().use(closest.getTeleportLocation())) {
            getInstance().log("Failed to teleport");
            return null;
        } else {
            WaitFor.condition(General.random(3000, 20000), () -> closest.teleportLocation.getRSTile().distanceTo(Player.getPosition()) < 10 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
        }

        return closest.getPathResult().toRSTilePath();
    }

    public static ArrayList<RSTile> getClosestPath(int originalMoveCost, RSTile destination) {
        final int cost = originalMoveCost - 25;
        List<TeleportWrapper> teleport = new CopyOnWriteArrayList<>();

        try {
            getInstance().customThreadPool.submit(() -> Arrays.stream(TeleportMethod.values()).parallel().forEach(teleportMethod -> {
                if (getInstance().blacklistTeleportMethods.contains(teleportMethod)) {
                    return;
                }
                if (!teleportMethod.canUse()) {
                    return;
                }
                System.out.println("We can use " + teleportMethod);
                for (TeleportLocation teleportLocation : teleportMethod.getDestinations()) {
                    if (getInstance().blacklistTeleportLocations.contains(teleportLocation)) {
                        continue;
                    }

                    PathResult pathResult = WebWalkerServerApi.getInstance().getPath(
                            Point3D.fromPositionable(teleportLocation.getRSTile()),
                            Point3D.fromPositionable(destination),
                            PlayerDetails.generate()
                    );
                    teleport.add(new TeleportWrapper(pathResult, teleportMethod, teleportLocation));
                }
            })).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }


        boolean[] rateLimit = new boolean[]{false};

        TeleportWrapper closest = teleport.stream()
                .filter(pathResult -> {
                    if (pathResult.getPathResult().getPathStatus() == RATE_LIMIT_EXCEEDED) {
                        rateLimit[0] = true;
                    }
                    return pathResult.getPathResult().getPathStatus() == SUCCESS && pathResult.getPathResult().getCost() < cost;
                })
                .min(Comparator.comparingInt(value -> value.getPathResult().getCost()))
                .orElse(null);

        if (rateLimit[0]) {
            return null;
        }

        if (closest == null) {
            return null;
        }

        getInstance().log("Found shorter path with teleport: " + closest.getTeleportMethod() + " > " + closest.getTeleportLocation() + " (" + originalMoveCost);

        if (!closest.getTeleportMethod().use(closest.getTeleportLocation())) {
            getInstance().log("Failed to teleport");
            return null;
        } else {
            WaitFor.condition(General.random(3000, 20000), () -> closest.teleportLocation.getRSTile().distanceTo(Player.getPosition()) < 15 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
        }

        return closest.getPathResult().toRSTilePath();
    }


    public static class TeleportWrapper {
        private PathResult pathResult;
        private TeleportMethod teleportMethod;
        private TeleportLocation teleportLocation;

        public TeleportWrapper(PathResult pathResult, TeleportMethod teleportMethod, TeleportLocation teleportLocation) {
            this.pathResult = pathResult;
            this.teleportMethod = teleportMethod;
            this.teleportLocation = teleportLocation;
        }

        public PathResult getPathResult() {
            return pathResult;
        }

        public TeleportMethod getTeleportMethod() {
            return teleportMethod;
        }

        public TeleportLocation getTeleportLocation() {
            return teleportLocation;
        }
    }

    public static class TeleportAction {
        private ArrayList<RSTile> path;
        private TeleportMethod teleportMethod;
        private TeleportLocation teleportLocation;

        TeleportAction(ArrayList<RSTile> path, TeleportMethod teleportMethod, TeleportLocation teleportLocation) {
            this.path = path;
            this.teleportMethod = teleportMethod;
            this.teleportLocation = teleportLocation;
        }

        public ArrayList<RSTile> getPath() {
            return path;
        }

        public TeleportMethod getTeleportMethod() {
            return teleportMethod;
        }

        public TeleportLocation getTeleportLocation() {
            return teleportLocation;
        }
    }

    @Override
    public String getName() {
        return "Teleport Manager";
    }
}
