package dax.walker_engine.navigation_utils;

import dax.walker_engine.Loggable;
import org.tribot.api.ScriptCache;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSTile;
import dax.shared.helpers.InterfaceHelper;
import dax.walker_engine.WaitFor;
import dax.walker_engine.interaction_handling.InteractionHelper;
import dax.walker_engine.interaction_handling.NPCInteraction;

import java.util.HashMap;
import java.util.stream.Collectors;

public class Charter implements Loggable {

    private static final int CHARTER_INTERFACE_MASTER = 72;

    private static Charter getInstance(){
        return (Charter) ScriptCache.get().computeIfAbsent("DaxWalker.Charter", k -> new Charter());
    }

    public static boolean to(LocationProperty locationProperty){
        if (locationProperty == null){
            return false;
        }
        if (!openCharterMenu()){
            getInstance().log("Failed to open charter menu.");
            return false;
        }
        HashMap<LocationProperty, Location> charterLocations = getCharterLocations();
        Location location = charterLocations.get(locationProperty);
        if (location == null){
            getInstance().log("Location: " + locationProperty + " is not available. " + charterLocations.keySet());
            return false;
        }
        if (!location.click()){
            getInstance().log("Failed to click charter location.");
            return false;
        }
        if (!NPCInteraction.waitForConversationWindow()){
            getInstance().log("Confirmation dialogue did not appear.");
        }
        NPCInteraction.handleConversation(new String[]{"Ok", "Okay"});
        return WaitFor.condition(10000, () -> ShipUtils.isOnShip() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
    }

    private static boolean openCharterMenu() {
        return Interfaces.isInterfaceValid(CHARTER_INTERFACE_MASTER) ||
                InteractionHelper.click(InteractionHelper.getRSNPC(Filters.NPCs.actionsEquals("Charter")), "Charter", () -> Interfaces.isInterfaceValid(CHARTER_INTERFACE_MASTER) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE);
    }

    private static boolean isValidCharterInterface(RSInterface rsInterface) {
        return rsInterface != null
                && rsInterface.getModelID() == -1
                && rsInterface.getTextureID() == -1
                && rsInterface.getActions() != null
                && rsInterface.getActions().length == 1
                && !rsInterface.isHidden();
    }

    private static HashMap<LocationProperty, Location> getCharterLocations(){
        HashMap<LocationProperty, Location> locations = new HashMap<>();
        InterfaceHelper.getAllInterfaces(CHARTER_INTERFACE_MASTER).stream()
                .filter(Charter::isValidCharterInterface)
                .collect(Collectors.toList())
                .forEach(rsInterface -> {
                    String[] actions = rsInterface.getActions();
                    if(actions == null || actions.length == 0)
                        return;
                    locations.put(
                            LocationProperty.stringToLocation(actions[0]), new Location(rsInterface));
                });
        return locations;
    }

    @Override
    public String getName() {
        return "Charter";
    }

    public enum LocationProperty {
        ALDARIN("Aldarin", new RSTile(1455, 2968, 0)),
        BRIMHAVEN ("Brimhaven", new RSTile(2760, 3237, 0)),
        CATHERBY ("Catherby", new RSTile(2796, 3414, 0)),
        CORSAIR_COVE ("Corsair Cove", new RSTile(2587, 2851, 0)),
        CIVITAS_ILLA_FORTIS("Civitas illa Fortis", new RSTile(1746, 3136, 0)),
        MUSA_POINT("Musa Point", new RSTile(2954, 3158, 0)),
        MOS_LE_HARMLESS ("Mos Le'Harmless", new RSTile(3671, 2931, 0)),
        PORT_KHAZARD ("Port Khazard", new RSTile(2674, 3144, 0)),
        PORT_PHASMATYS ("Port Phasmatys", new RSTile(3702, 3503, 0)),
        PORT_SARIM ("Port Sarim", new RSTile(3038, 3192, 0)),
        PORT_TYRAS ("Port Tyras", new RSTile(2142, 3122, 0)),
        PRIFDDINAS ("Prifddinas", new RSTile(2159, 3329, 0)),
        SHIPYARD ("Shipyard", new RSTile(3001, 3032, 0)),
        SUNSET_COAST("Sunset Coast", new RSTile(1514, 2971, 0)),
        ;

        private String name;
        private RSArea area;

        LocationProperty(String name, RSTile center){
            this.name = name;
            if (center != null) {
                this.area = new RSArea(center, 15);
            }
        }

        public boolean valid(RSTile tile) {
            return area != null && tile != null && area.contains(tile);
        }

        public static LocationProperty stringToLocation(String name){
            for (LocationProperty locationProperty : values()){
                if (name.equals(locationProperty.name)){
                    return locationProperty;
                }
            }
            return null;
        }

        public static LocationProperty getLocation(RSTile tile){
            for (LocationProperty locationProperty : values()){
                if (locationProperty.valid(tile)){
                    return locationProperty;
                }
            }
            return null;
        }

        @Override
        public String toString(){
            return name;
        }
    }


    public static class Location {

        private String name;
        private RSInterface rsInterface;

        private Location(RSInterface rsInterface){
            this.name = rsInterface.getText();
            this.rsInterface = rsInterface;
        }

        public String getName() {
            return name;
        }

        public RSInterface getRsInterface() {
            return rsInterface;
        }

        public boolean click(String... options){
            return rsInterface.click(options);
        }

        @Override
        public String toString(){
            return name;
        }
    }

}
