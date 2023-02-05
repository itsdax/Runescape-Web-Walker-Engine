package dax.walker_engine.interaction_handling;

import dax.walker_engine.Loggable;
import dax.walker_engine.WalkerEngine;
import dax.walker_engine.bfs.BFS;
import dax.walker_engine.local_pathfinding.PathAnalyzer;
import dax.walker_engine.local_pathfinding.Reachable;
import dax.walker_engine.real_time_collision.RealTimeCollisionTile;
import org.tribot.api.General;
import org.tribot.api.ScriptCache;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.*;
import org.tribot.api2007.Objects;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.*;
import dax.shared.helpers.RSObjectHelper;
import dax.walker_engine.WaitFor;

import java.util.*;
import java.util.stream.Collectors;


public class PathObjectHandler implements Loggable {

    private final TreeSet<String> sortedOptions, sortedBlackList, sortedBlackListOptions, sortedHighPriorityOptions;

    private PathObjectHandler(){
        sortedOptions = new TreeSet<>(
		        Arrays.asList("Enter", "Cross", "Pass", "Open", "Close", "Walk-through", "Use", "Pass-through", "Exit",
                "Walk-Across", "Go-through", "Walk-across", "Climb", "Climb-up", "Climb-down", "Climb-over", "Climb over", "Climb-into", "Climb-through", "Climb through",
                "Board", "Jump-from", "Jump-across", "Jump-to", "Squeeze-through", "Jump-over", "Pay-toll(10gp)", "Step-over", "Walk-down", "Walk-up","Walk-Up", "Travel", "Get in",
                "Investigate", "Operate", "Climb-under","Jump","Crawl-down","Crawl-through","Activate","Push","Squeeze-past","Walk-Down",
                "Swing-on", "Climb up", "Ascend", "Descend","Channel","Teleport","Pass-Through","Jump-up","Jump-down","Swing across", "Climb Up", "Climb Down", "Jump-Down"));

        sortedBlackList = new TreeSet<>(Arrays.asList("Coffin","Drawers","null"));
        sortedBlackListOptions = new TreeSet<>(Arrays.asList("Chop down"));
        sortedHighPriorityOptions = new TreeSet<>(Arrays.asList("Pay-toll(10gp)","Squeeze-past"));
    }

    private static PathObjectHandler getInstance(){
        return (PathObjectHandler) ScriptCache.get().computeIfAbsent("DaxWalker.PathObjectHandler", k -> new PathObjectHandler());
    }

    private enum SpecialObject {
        WEB("Web", "Slash", null, new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return Objects.find(15,
                        Filters.Objects.inArea(new RSArea(destinationDetails.getAssumed(), 1))
                                .combine(Filters.Objects.nameEquals("Web"), true)
                                .combine(Filters.Objects.actionsContains("Slash"), true)).length > 0;
            }
        }),
        ROCKFALL("Rockfall", "Mine", null, new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return Objects.find(15,
                        Filters.Objects.inArea(new RSArea(destinationDetails.getAssumed(), 1))
                                .combine(Filters.Objects.nameEquals("Rockfall"), true)
                                .combine(Filters.Objects.actionsContains("Mine"), true)).length > 0;
            }
        }),
        ROOTS("Roots", "Chop", null, new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return Objects.find(15,
                        Filters.Objects.inArea(new RSArea(destinationDetails.getAssumed(), 1))
                                .combine(Filters.Objects.nameEquals("Roots"), true)
                                .combine(Filters.Objects.actionsContains("Chop"), true)).length > 0;
            }
        }),
        ROCK_SLIDE("Rockslide", "Climb-over", null, new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return Objects.find(15,
                        Filters.Objects.inArea(new RSArea(destinationDetails.getAssumed(), 1))
                                .combine(Filters.Objects.nameEquals("Rockslide"), true)
                                .combine(Filters.Objects.actionsContains("Climb-over"), true)).length > 0;
            }
        }),
        ROOT("Root", "Step-over", null, new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return Objects.find(15,
                        Filters.Objects.inArea(new RSArea(destinationDetails.getAssumed(), 1))
                                .combine(Filters.Objects.nameEquals("Root"), true)
                                .combine(Filters.Objects.actionsContains("Step-over"), true)).length > 0;
            }
        }),
        BRIMHAVEN_VINES("Vines", "Chop-down", null, new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return Objects.find(15,
                        Filters.Objects.inArea(new RSArea(destinationDetails.getAssumed(), 1))
                                .combine(Filters.Objects.nameEquals("Vines"), true)
                                .combine(Filters.Objects.actionsContains("Chop-down"), true)).length > 0;
            }
        }),
        AVA_BOOKCASE ("Bookcase", "Search", new RSTile(3097, 3359, 0), new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return destinationDetails.getDestination().getX() >= 3097 && destinationDetails.getAssumed().equals(new RSTile(3097, 3359, 0));
            }
        }),
        AVA_LEVER ("Lever", "Pull", new RSTile(3096, 3357, 0), new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return destinationDetails.getDestination().getX() < 3097 && destinationDetails.getAssumed().equals(new RSTile(3097, 3359, 0));
            }
        }),
        ARDY_DOOR_LOCK_SIDE("Door", "Pick-lock", new RSTile(2565, 3356, 0), new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return Player.getPosition().getX() >= 2565 && Player.getPosition().distanceTo(new RSTile(2565, 3356, 0)) < 3;
            }
        }),
        ARDY_DOOR_UNLOCKED_SIDE("Door", "Open", new RSTile(2565, 3356, 0), new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return Player.getPosition().getX() < 2565 && Player.getPosition().distanceTo(new RSTile(2565, 3356, 0)) < 3;
            }
        }),
        YANILLE_DOOR_LOCK_SIDE("Door", "Pick-lock", new RSTile(2601, 9482, 0), new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return Player.getPosition().getY() <= 9481 && Player.getPosition().distanceTo(new RSTile(2601, 9482, 0)) < 3;
            }
        }),
        YANILLE_DOOR_UNLOCKED_SIDE("Door", "Open", new RSTile(2601, 9482, 0), new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return Player.getPosition().getY() > 9481 && Player.getPosition().distanceTo(new RSTile(2601, 9482, 0)) < 3;
            }
        }),
        EDGEVILLE_UNDERWALL_TUNNEL("Underwall tunnel", "Climb-into", new RSTile(3138, 3516, 0), new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return destinationDetails.getAssumed().equals(new RSTile(3138, 3516, 0));
            }
        }),
        VARROCK_UNDERWALL_TUNNEL("Underwall tunnel", "Climb-into", new RSTile(3141, 3513, 0), new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return destinationDetails.getAssumed().equals(new RSTile(3141, 3513, 0 ));
            }
        }),
        GAMES_ROOM_STAIRS("Stairs", "Climb-down", new RSTile(2899, 3565, 0), new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return destinationDetails.getDestination().getRSTile().equals(new RSTile(2899, 3565, 0)) &&
                    destinationDetails.getAssumed().equals(new RSTile(2205, 4934, 1));
            }
        }),
        CANIFIS_BASEMENT_WALL("Wall", "Search", new RSTile(3480, 9836, 0),new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return destinationDetails.getDestination().getRSTile().equals(new RSTile(3480, 9836, 0)) ||
                    destinationDetails.getAssumed().equals(new RSTile(3480, 9836, 0));
            }
        }),
        BRINE_RAT_CAVE_BOULDER("Cave", "Exit", new RSTile(2690, 10125, 0), new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return destinationDetails.getDestination().getRSTile().equals(new RSTile(2690, 10125, 0))
                    && NPCs.find(Filters.NPCs.nameEquals("Boulder").and(Filters.NPCs.actionsContains("Roll"))).length > 0;
            }
        }),
        FOSSIL_ISLAND_LADDER_DOWN_WEST("Ladder", "Climb Down", new RSTile(3730, 3831, 1), new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return destinationDetails.getNextTile() != null && destinationDetails.getNextTile().getRSTile().equals(new RSTile(3728, 3831, 0));
            }
        }),
        FOSSIL_ISLAND_LADDER_DOWN_EAST("Ladder", "Climb Down", new RSTile(3745, 3831, 1), new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return destinationDetails.getNextTile() != null &&  destinationDetails.getNextTile().getRSTile().equals(new RSTile(3747, 3831, 0));
            }
        }),
        HAM_JAIL("Door","Pick-lock",new RSTile(3183, 9611, 0), new PathObjectHandler.SpecialCondition() {

            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return destinationDetails.getNextTile() != null && destinationDetails.getNextTile().getRSTile().equals(new RSTile(3182, 9611, 0));
            }
        }),
        ARDOUGNE_LOCKED_HOUSE("Door", "Pick-lock", new RSTile(2611, 3316, 0), new SpecialCondition() {
            @Override
            boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails) {
                return destinationDetails.getAssumed().equals(new RSTile(2611, 3316, 0)) && destinationDetails.getDestination().getRSTile().equals(new RSTile(2610, 3316, 0));
            }
        });

        private String name, action;
        private RSTile location;
        private SpecialCondition specialCondition;

        SpecialObject(String name, String action, RSTile location, SpecialCondition specialCondition){
            this.name = name;
            this.action = action;
            this.location = location;
            this.specialCondition = specialCondition;
        }

        public String getName() {
            return name;
        }

        public String getAction() {
            return action;
        }

        public RSTile getLocation() {
            return location;
        }

        public boolean isSpecialCondition(PathAnalyzer.DestinationDetails destinationDetails){
            return specialCondition.isSpecialLocation(destinationDetails);
        }

        public static SpecialObject getValidSpecialObjects(PathAnalyzer.DestinationDetails destinationDetails){
            for (SpecialObject object : values()){
                if (object.isSpecialCondition(destinationDetails)){
                    return object;
                }
            }
            return null;
        }

    }

    private abstract static class SpecialCondition {
        abstract boolean isSpecialLocation(PathAnalyzer.DestinationDetails destinationDetails);
    }

    public static boolean handle(PathAnalyzer.DestinationDetails destinationDetails, List<RSTile> path){
        RealTimeCollisionTile start = destinationDetails.getDestination(), end = destinationDetails.getNextTile();

        RSObject[] interactiveObjects = null;

        String action = null;
        SpecialObject specialObject = SpecialObject.getValidSpecialObjects(destinationDetails);
        if (specialObject == null) {
            if ((interactiveObjects = getInteractiveObjects(start.getX(), start.getY(), start.getZ(), destinationDetails)).length < 1 && end != null) {
                interactiveObjects = getInteractiveObjects(end.getX(), end.getY(), end.getZ(), destinationDetails);
            }
        } else {
            action = specialObject.getAction();
            Filter<RSObject> specialObjectFilter = Filters.Objects.nameEquals(specialObject.getName())
                    .combine(Filters.Objects.actionsContains(specialObject.getAction()), true)
                    .combine(Filters.Objects.inArea(new RSArea(specialObject.getLocation() != null ? specialObject.getLocation() : destinationDetails.getAssumed(), 1)), true);
            interactiveObjects = Objects.findNearest(15, specialObjectFilter);
        }

        if (interactiveObjects.length == 0) {
            return false;
        }

        StringBuilder stringBuilder = new StringBuilder("Sort Order: ");
        Arrays.stream(interactiveObjects).forEach(rsObject -> stringBuilder.append(rsObject.getDefinition().getName()).append(" ").append(
		        Arrays.asList(rsObject.getDefinition().getActions())).append(", "));
        getInstance().log(stringBuilder);

        return handle(path, interactiveObjects[0], destinationDetails, action, specialObject);
    }

    private static boolean handle(List<RSTile> path, RSObject object, PathAnalyzer.DestinationDetails destinationDetails, String action, SpecialObject specialObject){
        PathAnalyzer.DestinationDetails current = PathAnalyzer.furthestReachableTile(path);

        if (current == null){
            return false;
        }

        RealTimeCollisionTile currentFurthest = current.getDestination();
        if (!Player.isMoving() && (!object.isOnScreen() || !object.isClickable())){
            if (!WalkerEngine.getInstance().clickMinimap(destinationDetails.getDestination())){
                return false;
            }
        }
        if (WaitFor.condition(General.random(5000, 8000), () -> object.isOnScreen() && object.isClickable() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) != WaitFor.Return.SUCCESS) {
            return false;
        }

        boolean successfulClick = false;

        if (specialObject != null) {
            getInstance().log("Detected Special Object: " + specialObject);
            switch (specialObject){
                case WEB:
                    List<RSObject> webs;
                    int iterations = 0;
                    while ((webs = Arrays.stream(Objects.getAt(object.getPosition()))
                            .filter(object1 -> Arrays.stream(RSObjectHelper.getActions(object1))
                                    .anyMatch(s -> s.equals("Slash"))).collect(Collectors.toList())).size() > 0){
                        RSObject web = webs.get(0);
                        if (canLeftclickWeb()) {
                            InteractionHelper.click(web, "Slash");
                        } else {
                            useBladeOnWeb(web);
                        }
                        if(Game.isUptext("->")){
                            Walking.blindWalkTo(Player.getPosition());
                        }
                        if (web.getPosition().distanceTo(Player.getPosition()) <= 1) {
                            WaitFor.milliseconds(General.randomSD(50, 800, 250, 150));
                        } else {
                            WaitFor.milliseconds(2000, 4000);
                        }
                        if (Reachable.getMap().getParent(destinationDetails.getAssumedX(), destinationDetails.getAssumedY()) != null &&
                                (webs = Arrays.stream(Objects.getAt(object.getPosition())).filter(object1 -> Arrays.stream(RSObjectHelper.getActions(object1))
                                        .anyMatch(s -> s.equals("Slash"))).collect(Collectors.toList())).size() == 0){
                            successfulClick = true;
                            break;
                        }
                        if (iterations++ > 5){
                            break;
                        }
                    }
                    break;
                case ARDY_DOOR_LOCK_SIDE:
                    for (int i = 0; i < General.random(15, 25); i++) {
                        if (!clickOnObject(object, new String[]{specialObject.getAction()})){
                            continue;
                        }
                        if (Player.getPosition().distanceTo(specialObject.getLocation()) > 1){
                            WaitFor.condition(General.random(3000, 4000), () -> Player.getPosition().distanceTo(specialObject.getLocation()) <= 1 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
                        } else {
                            WaitFor.milliseconds(400, 1200);
                        }
                        if (Player.getPosition().equals(new RSTile(2564, 3356, 0))){
                            successfulClick = true;
                            break;
                        }
                    }
                    break;
                case YANILLE_DOOR_LOCK_SIDE:
                    for (int i = 0; i < General.random(15, 25); i++) {
                        if (!clickOnObject(object, new String[]{specialObject.getAction()})){
                            continue;
                        }
                        if (Player.getPosition().distanceTo(specialObject.getLocation()) > 1){
                            WaitFor.condition(General.random(3000, 4000), () -> Player.getPosition().distanceTo(specialObject.getLocation()) <= 1 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
                        } else {
                            WaitFor.milliseconds(400, 1200);
                        }
                        if (Player.getPosition().equals(new RSTile(2601, 9482, 0))){
                            successfulClick = true;
                            break;
                        }
                    }
                    break;
                case VARROCK_UNDERWALL_TUNNEL:
                    if(!clickOnObject(object,specialObject.getAction())){
                        return false;
                    }
                    successfulClick = true;
                    WaitFor.condition(10000, () ->
                            SpecialObject.EDGEVILLE_UNDERWALL_TUNNEL.getLocation().equals(Player.getPosition()) ?
                                    WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
                    break;
                case EDGEVILLE_UNDERWALL_TUNNEL:
                    if(!clickOnObject(object,specialObject.getAction())){
                        return false;
                    }
                    successfulClick = true;
                    WaitFor.condition(10000, () ->
                            SpecialObject.VARROCK_UNDERWALL_TUNNEL.getLocation().equals(Player.getPosition()) ?
                                    WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
                    break;
                case BRINE_RAT_CAVE_BOULDER:
                    RSNPC boulder = InteractionHelper.getRSNPC(Filters.NPCs.nameEquals("Boulder").and(Filters.NPCs.actionsContains("Roll")));
                    if(InteractionHelper.click(boulder, "Roll")){
                        if(WaitFor.condition(12000,
                            () -> NPCs.find(Filters.NPCs.nameEquals("Boulder").and(Filters.NPCs.actionsContains("Roll"))).length == 0 ?
                                WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS){
                            WaitFor.milliseconds(3500, 6000);
                        }
                    }
                    break;
                case FOSSIL_ISLAND_LADDER_DOWN_WEST:
                case FOSSIL_ISLAND_LADDER_DOWN_EAST:
                    RSObject ladder = InteractionHelper.getRSObject(Filters.Objects.nameEquals("Ladder").and(Filters.Objects.actionsContains("Climb Down")));
                    if(ladder == null)
                        return false;
                    ladder.setClickHeight(General.random(-100, -40));
                    if(InteractionHelper.click(ladder, "Climb Down")){
                        WaitFor.condition(10000, () -> Game.getPlane() == 0 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
                    }
                    break;
                case ARDOUGNE_LOCKED_HOUSE:
                    for (int i = 0; i < General.random(10, 15); i++) {
                        if (!clickOnObject(object, specialObject.getAction())) {
                            continue;
                        }
                        if (Player.getPosition().distanceTo(specialObject.getLocation()) > 1) {
                            WaitFor.condition(General.random(3000, 4000), () -> Player.getPosition().distanceTo(specialObject.getLocation()) <= 1 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
                        }
                        if (Player.getPosition().equals(specialObject.getLocation())) {
                            successfulClick = true;
                            break;
                        }
                    }
                    break;
            }
        }

        if (!successfulClick){
            String[] validOptions = action != null ? new String[]{action} : getViableOption(
		            Arrays.stream(object.getDefinition().getActions()).filter(getInstance().sortedOptions::contains).collect(
				            Collectors.toList()), destinationDetails);
            if (!clickOnObject(object, validOptions)) {
                return false;
            }
        }

        boolean strongholdDoor = isStrongholdDoor(object);

        if (strongholdDoor){
            if (WaitFor.condition(General.random(6700, 7800), () -> {
                RSTile playerPosition = Player.getPosition();
                if (BFS.isReachable(RealTimeCollisionTile.get(playerPosition.getX(), playerPosition.getY(), playerPosition.getPlane()), destinationDetails.getNextTile(), 50)) {
                    WaitFor.milliseconds(500, 1000);
                    return WaitFor.Return.SUCCESS;
                }
                if (NPCInteraction.isConversationWindowUp()) {
                    handleStrongholdQuestions();
                    return WaitFor.Return.SUCCESS;
                }
                return WaitFor.Return.IGNORE;
            }) != WaitFor.Return.SUCCESS){
                return false;
            }
        }

        WaitFor.condition(General.random(8500, 11000), () -> {
            DoomsToggle.handleToggle();
            PathAnalyzer.DestinationDetails destinationDetails1 = PathAnalyzer.furthestReachableTile(path);
            if (NPCInteraction.isConversationWindowUp()) {
                NPCInteraction.handleConversation(NPCInteraction.GENERAL_RESPONSES);
            }
            if (destinationDetails1 != null) {
                if (!destinationDetails1.getDestination().equals(currentFurthest)){
                    return WaitFor.Return.SUCCESS;
                }
            }
            if (current.getNextTile() != null){
                PathAnalyzer.DestinationDetails hoverDetails = PathAnalyzer.furthestReachableTile(path, current.getNextTile());
                if (hoverDetails != null && hoverDetails.getDestination() != null && hoverDetails.getDestination().getRSTile().distanceTo(Player.getPosition()) > 7 && !strongholdDoor && Player.getPosition().distanceTo(object) <= 2){
                    WalkerEngine.getInstance().hoverMinimap(hoverDetails.getDestination());
                }
            }
            return WaitFor.Return.IGNORE;
        });
        if (strongholdDoor){
            General.sleep(800, 1200);
        }
        return true;
    }

    public static RSObject[] getInteractiveObjects(int x, int y, int z, PathAnalyzer.DestinationDetails destinationDetails){
        RSObject[] objects = Objects.getAll(25, interactiveObjectFilter(x, y, z, destinationDetails));
        final RSTile base = new RSTile(x, y, z);
        Arrays.sort(objects, (o1, o2) -> {
            int c = Integer.compare(o1.getPosition().distanceTo(base), o2.getPosition().distanceTo(base));
            int assumedZ = destinationDetails.getAssumedZ(), destinationZ = destinationDetails.getDestination().getZ();
            List<String> actions1 = Arrays.asList(o1.getDefinition().getActions());
            List<String> actions2 = Arrays.asList(o2.getDefinition().getActions());

            if (assumedZ > destinationZ){
                if (actions1.contains("Climb-up")){
                    return -1;
                }
                if (actions2.contains("Climb-up")){
                    return 1;
                }
            } else if (assumedZ < destinationZ){
                if (actions1.contains("Climb-down")){
                    return -1;
                }
                if (actions2.contains("Climb-down")){
                    return 1;
                }
            } else if(destinationDetails.getAssumed().distanceTo(destinationDetails.getDestination().getRSTile()) > 20){
                if(actions1.contains("Climb-up") || actions1.contains("Climb-down")){
                    return -1;
                } else if(actions2.contains("Climb-up") || actions2.contains("Climb-down")){
                    return 1;
                }
            } else if(actions1.contains("Climb-up") || actions1.contains("Climb-down")){
                return 1;
            } else if(actions2.contains("Climb-up") || actions2.contains("Climb-down")){
                return -1;
            }
            return c;
        });
        StringBuilder a = new StringBuilder("Detected: ");
        Arrays.stream(objects).forEach(object -> a.append(object.getDefinition().getName()).append(" "));
        getInstance().log(a);



        return objects;
    }

    /**
     * Filter that accepts only interactive objects to progress in path.
     *
     * @param x
     * @param y
     * @param z
     * @param destinationDetails context where destination is at
     * @return
     */
    private static Filter<RSObject> interactiveObjectFilter(int x, int y, int z, PathAnalyzer.DestinationDetails destinationDetails){
        final RSTile position = new RSTile(x, y, z);
        return new Filter<RSObject>() {
            @Override
            public boolean accept(RSObject rsObject) {
                RSObjectDefinition def = rsObject.getDefinition();
                if (def == null){
                    return false;
                }
                String name = def.getName();
                if (getInstance().sortedBlackList.contains(name)) {
                    return false;
                }
                if (RSObjectHelper.getActionsList(rsObject).stream().anyMatch(s -> getInstance().sortedBlackListOptions.contains(s))){
                    return false;
                }
                if (rsObject.getPosition().distanceTo(destinationDetails.getDestination().getRSTile()) > 5) {
                    return false;
                }
                if (Arrays.stream(rsObject.getAllTiles()).noneMatch(rsTile -> rsTile.distanceTo(position) <= 2)) {
                    return false;
                }
                List<String> options = Arrays.asList(def.getActions());
                return options.stream().anyMatch(getInstance().sortedOptions::contains);
            }
        };
    }

    private static String[] getViableOption(Collection<String> collection, PathAnalyzer.DestinationDetails destinationDetails){
        Set<String> set = new HashSet<>(collection);
        if (set.retainAll(getInstance().sortedHighPriorityOptions) && set.size() > 0){
            return set.toArray(new String[set.size()]);
        }
        if (destinationDetails.getAssumedZ() > destinationDetails.getDestination().getZ()){
            if (collection.contains("Climb-up")){
                return new String[]{"Climb-up"};
            }
        }
        if (destinationDetails.getAssumedZ() < destinationDetails.getDestination().getZ()){
            if (collection.contains("Climb-down")){
                return new String[]{"Climb-down"};
            }
        }
        if (destinationDetails.getAssumedY() > 5000 && destinationDetails.getDestination().getZ() == 0 && destinationDetails.getAssumedZ() == 0){
            if (collection.contains("Climb-down")){
                return new String[]{"Climb-down"};
            }
        }
        String[] options = new String[collection.size()];
        collection.toArray(options);
        return options;
    }

    private static boolean clickOnObject(RSObject object, String... options){
        boolean result;

        if (isClosedTrapDoor(object, options)){
            result = handleTrapDoor(object);
        } else {
            result = InteractionHelper.click(object, options);
            getInstance().log("Interacting with (" + RSObjectHelper.getName(object) + ") at " + object.getPosition() + " with options: " + Arrays.toString(options) + " " + (result ? "SUCCESS" : "FAIL"));
            WaitFor.milliseconds(250,800);
        }

        return result;
    }

    private static boolean isStrongholdDoor(RSObject object){
        List<String> doorNames = Arrays.asList("Gate of War", "Rickety door", "Oozing barrier", "Portal of Death");
        return  doorNames.contains(object.getDefinition().getName());
    }



    private static void handleStrongholdQuestions() {
        NPCInteraction.handleConversation("Use the Account Recovery System.",
            "No, you should never buy an account.",
            "Nobody.",
            "Don't tell them anything and click the 'Report Abuse' button.",
            "Decline the offer and report that player.",
            "Me.",
            "Only on the RuneScape website.",
            "Report the incident and do not click any links.",
            "Authenticator and two-step login on my registered email.",
            "No way! You'll just take my gold for your own! Reported!",
            "No.",
            "Don't give them the information and send an 'Abuse Report'.",
            "Don't give them my password.",
            "The birthday of a famous person or event.",
            "Through account settings on runescape.com.",
            "Secure my device and reset my RuneScape password.",
            "Report the player for phishing.",
            "Don't click any links, forward the email to reportphishing@jagex.com.",
            "Inform Jagex by emailing reportphishing@jagex.com.",
            "Don't give out your password to anyone. Not even close friends.",
            "Politely tell them no and then use the 'Report Abuse' button.",
            "Set up 2 step authentication with my email provider.",
            "No, you should never buy a RuneScape account.",
            "Do not visit the website and report the player who messaged you.",
            "Only on the RuneScape website.",
            "Don't type in my password backwards and report the player.",
            "Virus scan my device then change my password.",
            "No, you should never allow anyone to level your account.",
            "Don't give out your password to anyone. Not even close friends.",
            "Report the stream as a scam. Real Jagex streams have a 'verified' mark.",
            "Report the stream as a scam. Real Jagex streams have a 'verified' mark",
            "Read the text and follow the advice given.",
            "No way! I'm reporting you to Jagex!",
            "Talk to any banker in RuneScape.",
            "Secure my device and reset my RuneScape password.",
            "Secure my device and reset my password.",
            "Delete it - it's a fake!",
            "Use the account management section on the website.",
            "Politely tell them no and then use the 'Report Abuse' button.",
            "Through account setting on oldschool.runescape.com",
            "Through account setting on oldschool.runescape.com.",
            "Nothing, it's a fake.",
            "Only on the Old School RuneScape website.",
            "Don't share your information and report the player.");
    }


    private static boolean isClosedTrapDoor(RSObject object, String[] options){
        return  (object.getDefinition().getName().equals("Trapdoor") && Arrays.asList(options).contains("Open"));
    }

    private static boolean handleTrapDoor(RSObject object){
        if (getActions(object).contains("Open")){
            if (!InteractionHelper.click(object, "Open", () -> {
                RSObject[] objects = Objects.find(15, Filters.Objects.actionsContains("Climb-down").combine(Filters.Objects.inArea(new RSArea(object, 2)), true));
                if (objects.length > 0 && getActions(objects[0]).contains("Climb-down")){
                    return WaitFor.Return.SUCCESS;
                }
                return WaitFor.Return.IGNORE;
            })){
                return false;
            } else {
                RSObject[] objects = Objects.find(15, Filters.Objects.actionsContains("Climb-down").combine(Filters.Objects.inArea(new RSArea(object, 2)), true));
                return objects.length > 0 && handleTrapDoor(objects[0]);
            }
        }
        getInstance().log("Interacting with (" + object.getDefinition().getName() + ") at " + object.getPosition() + " with option: Climb-down");
        return InteractionHelper.click(object, "Climb-down");
    }

    public static List<String> getActions(RSObject object){
        List<String> list = new ArrayList<>();
        if (object == null){
            return list;
        }
        RSObjectDefinition objectDefinition = object.getDefinition();
        if (objectDefinition == null){
            return list;
        }
        String[] actions = objectDefinition.getActions();
        if (actions == null){
            return list;
        }
        return Arrays.asList(actions);
    }

    @Override
    public String getName() {
        return "Object Handler";
    }

    private static List<Integer> SLASH_WEAPONS = new ArrayList<>(Arrays.asList(1,4,9,10,12,17,20,21));

    private static boolean canLeftclickWeb(){
        RSVarBit weaponType = RSVarBit.get(357);
        return (weaponType != null && SLASH_WEAPONS.contains(weaponType.getValue())) || Inventory.find("Knife").length > 0;
    }
    private static boolean useBladeOnWeb(RSObject web){
        if(!Game.isUptext("->")){
            RSItem[] slashable = Inventory.find(Filters.Items.nameEquals("Knife").or(Filters.Items.nameContains("whip", "sword", "dagger", "claws", "scimitar", " axe", "halberd", "machete", "rapier").and(Filters.Items.nameNotEquals("Swordfish"))));
            if(slashable.length == 0 || !slashable[0].click("Use"))
                return false;
        }
        return InteractionHelper.click(web, "Use " + Game.getSelectedItemName() + " -> " + web.getDefinition().getName());
    }

}
