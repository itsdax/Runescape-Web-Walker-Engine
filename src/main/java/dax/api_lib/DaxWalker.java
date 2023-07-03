package dax.api_lib;

import dax.api_lib.models.*;
import dax.shared.helpers.InterfaceHelper;
import dax.teleports.Teleport;
import dax.walker_engine.Loggable;
import dax.walker_engine.WaitFor;
import dax.walker_engine.WalkerEngine;
import dax.walker_engine.WalkingCondition;
import dax.walker_engine.navigation_utils.ShipUtils;
import org.tribot.api.ScriptCache;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Player;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSTile;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DaxWalker implements Loggable {

    private final Map<RSTile, Teleport> map;
    private final Map<WalkerPreferences, Integer> walkerPreferences;
    public static DaxWalker getInstance() {
        Map<String, Object> cache = ScriptCache.get();
        DaxWalker daxWalker = (DaxWalker)cache.get("daxwalker");

        if (daxWalker == null) {
            daxWalker = new DaxWalker();
            cache.put("daxwalker", daxWalker);
        }

        return daxWalker;
    }

    private WalkingCondition globalWalkingCondition;

    private DaxWalker() {
        globalWalkingCondition = () -> WalkingCondition.State.CONTINUE_WALKER;

        map = new ConcurrentHashMap<>();
        walkerPreferences = new ConcurrentHashMap<>();
        Arrays.stream(WalkerPreferences.values()).forEach(p -> walkerPreferences.put(p, 0));
        for (Teleport teleport : Teleport.values()) {
            map.put(teleport.getLocation(), teleport);
        }
    }

    public static void enableWalkerPreference(WalkerPreferences... preferences){
        for(WalkerPreferences preference: preferences){
            getInstance().walkerPreferences.replace(preference, 1);
        }
    }
    public static void disableWalkerPreference(WalkerPreferences... preferences){
        for(WalkerPreferences preference: preferences){
            getInstance().walkerPreferences.replace(preference, 0);
        }
    }
    public static boolean getWalkerPreference(WalkerPreferences preference){
        return getInstance().walkerPreferences.get(preference) == 1;
    }
    public static List<IntPair> getWalkerPreferences(){
        return getInstance().walkerPreferences.keySet().stream().map(p -> new IntPair(p.ordinal(), getInstance().walkerPreferences.get(p))).collect(Collectors.toList());
    }

    public static WalkingCondition getGlobalWalkingCondition() {
        return getInstance().globalWalkingCondition;
    }

    public void useLocalDevelopmentServer(boolean b) {
        WebWalkerServerApi.getInstance().setTestMode(b);
    }

    public static void setGlobalWalkingCondition(WalkingCondition walkingCondition) {
        getInstance().globalWalkingCondition = walkingCondition;
    }

    public static void setCredentials(DaxCredentialsProvider daxCredentialsProvider) {
        WebWalkerServerApi.getInstance().setDaxCredentialsProvider(daxCredentialsProvider);
    }

    public static boolean walkTo(Positionable positionable) {
        return walkTo(positionable, null);
    }

    public static boolean walkTo(Positionable destination, WalkingCondition walkingCondition) {
        if (ShipUtils.isOnShip()) {
            ShipUtils.crossGangplank();
            WaitFor.milliseconds(500, 1200);
        }
        RSTile start = Player.getPosition();
        if (start.equals(destination)) {
            return true;
        }
        if(Objects.getAt(start, Filters.Objects.nameEquals("Fairy ring")).length > 0){
            start = start.translate(0, 1);
        }

        PlayerDetails playerDetails = PlayerDetails.generate();
        boolean isInPvpWorld = InterfaceHelper.getAllInterfaces(90).stream()
                .anyMatch(i -> i.getText().startsWith("" + Player.getRSPlayer().getCombatLevel()));

        List<PathRequestPair> pathRequestPairs = getInstance().getPathTeleports(playerDetails.isMember(), isInPvpWorld, destination.getPosition());

        pathRequestPairs.add(new PathRequestPair(Point3D.fromPositionable(start), Point3D.fromPositionable(destination)));

	    List<PathResult> pathResults = WebWalkerServerApi.getInstance().getPaths(new BulkPathRequest(playerDetails,pathRequestPairs));

	    List<PathResult> validPaths = getInstance().validPaths(pathResults);

	    PathResult pathResult = getInstance().getBestPath(validPaths);
	    if (pathResult == null) {
            getInstance().log(Level.WARNING, "No valid path found");
		    return false;
	    }

	    return WalkerEngine.getInstance().walkPath(pathResult.toRSTilePath(), getGlobalWalkingCondition().combine(walkingCondition));
    }

    public static boolean walkToBank() {
        return walkToBank(null, null);
    }

    public static boolean walkToBank(RunescapeBank bank) {
        return walkToBank(bank, null);
    }

    public static boolean walkToBank(WalkingCondition walkingCondition) {
        return walkToBank(null, walkingCondition);
    }

    public static boolean walkToBank(RunescapeBank bank, WalkingCondition walkingCondition) {
        if (ShipUtils.isOnShip()) {
            ShipUtils.crossGangplank();
            WaitFor.milliseconds(500, 1200);
        }

        if(bank != null)
            return walkTo(bank.getPosition(), walkingCondition);

        PlayerDetails playerDetails = PlayerDetails.generate();
        boolean isInPvpWorld = InterfaceHelper.getAllInterfaces(90).stream()
                .anyMatch(i -> i.getTextureID() == 1046 && Interfaces.isInterfaceSubstantiated(i));

        List<BankPathRequestPair> pathRequestPairs = getInstance().getBankPathTeleports(playerDetails.isMember(), isInPvpWorld);

        RSTile start = Player.getPosition();
        if(Objects.getAt(start, Filters.Objects.nameEquals("Fairy ring")).length > 0){
            start = start.translate(0, 1);
        }

        pathRequestPairs.add(new BankPathRequestPair(Point3D.fromPositionable(start),null));

        List<PathResult> pathResults = WebWalkerServerApi.getInstance().getBankPaths(new BulkBankPathRequest(playerDetails,pathRequestPairs));

        List<PathResult> validPaths = getInstance().validPaths(pathResults);
        PathResult pathResult = getInstance().getBestPath(validPaths);
        if (pathResult == null) {
            getInstance().log(Level.WARNING, "No valid path found");
            return false;
        }
        return WalkerEngine.getInstance().walkPath(pathResult.toRSTilePath(), getGlobalWalkingCondition().combine(walkingCondition));
    }

    public static List<Teleport> getBlacklist() {
        return (List<Teleport>) ScriptCache.get().computeIfAbsent(
                "DaxWalkerTeleport.blacklist",
                key -> new ArrayList<>());
    }

    public static void blacklistTeleports(Teleport... teleports){
        getBlacklist().addAll(Arrays.asList(teleports));
    }

    public static void clearTeleportBlacklist(){
        getBlacklist().clear();
    }

    private List<PathRequestPair> getPathTeleports(boolean members, boolean pvp, RSTile start) {
        return Teleport.getValidStartingRSTiles(members, pvp, getBlacklist()).stream()
                .filter(t -> !getBlacklist().contains(t))
                .map(t -> new PathRequestPair(Point3D.fromPositionable(t),
                        Point3D.fromPositionable(start)))
                .collect(Collectors.toList());
    }

    private List<BankPathRequestPair> getBankPathTeleports(boolean members, boolean pvp) {
        return Teleport.getValidStartingRSTiles(members, pvp, getBlacklist()).stream()
                .map(t -> new BankPathRequestPair(Point3D.fromPositionable(t), null))
                .collect(Collectors.toList());
    }

    public List<PathResult> validPaths(List<PathResult> list) {
        List<PathResult> result = list.stream().filter(pathResult -> pathResult.getPathStatus() == PathStatus.SUCCESS).collect(
		        Collectors.toList());
        if (!result.isEmpty()) {
            return result;
        }
        return Collections.emptyList();
    }

    public PathResult getBestPath(List<PathResult> list) {
        return list.stream().min(Comparator.comparingInt(this::getPathMoveCost)).orElse(null);
    }

    private int getPathMoveCost(PathResult pathResult) {
        if (Player.getPosition().equals(pathResult.getPath().get(0).toPositionable().getPosition())) return pathResult.getCost();
        Teleport teleport = map.get(pathResult.getPath().get(0).toPositionable().getPosition());
        if (teleport == null) return pathResult.getCost();
        return teleport.getMoveCost() + pathResult.getCost();
    }

    @Override
    public String getName() {
        return "DaxWalker";
    }
}
