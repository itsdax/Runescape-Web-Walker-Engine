package scripts.dax_api.walker_engine.navigation_utils;

import org.tribot.api.General;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Player;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSTile;
import scripts.dax_api.shared.helpers.InterfaceHelper;
import scripts.dax_api.walker_engine.WaitFor;
import scripts.dax_api.walker_engine.interaction_handling.InteractionHelper;

/**
 * Created by Me on 3/13/2017.
 */
public class SpiritTree {

    private static final int SPIRIT_TREE_MASTER_INTERFACE = 187;

    public enum Location {
        SPIRIT_TREE_GRAND_EXCHANGE("Grand Exchange", 3183, 3508, 0),
        SPIRIT_TREE_STRONGHOLD("Gnome Stronghold", 2461, 3444, 0),
        SPIRIT_TREE_KHAZARD("Battlefield of Khazard", 2555, 3259, 0),
        SPIRIT_TREE_VILLAGE("Tree Gnome Village", 2542, 3170, 0);

        private int x, y, z;
        private String name;
        Location(String name, int x, int y, int z){
            this.x = x;
            this.y = y;
            this.z = z;
            this.name = name;
        }
        public String getName() {
            return name;
        }
        public RSTile getRSTile(){
            return new RSTile(x, y, z);
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getZ() {
            return z;
        }
    }

    public static boolean to(Location location){
        if (!Interfaces.isInterfaceValid(SPIRIT_TREE_MASTER_INTERFACE)
                && !InteractionHelper.click(InteractionHelper.getRSObject(Filters.Objects.actionsContains("Travel")), "Travel", () -> Interfaces.isInterfaceValid(SPIRIT_TREE_MASTER_INTERFACE) ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE)) {
            return false;
        }

        RSInterface option = InterfaceHelper.getAllInterfaces(SPIRIT_TREE_MASTER_INTERFACE).stream().filter(rsInterface -> {
            String text = rsInterface.getText();
            return text != null && text.contains(location.getName());
        }).findAny().orElse(null);

        if (option == null){
            return false;
        }

        if (!option.click()){
            return false;
        }

        if (WaitFor.condition(General.random(5400, 6500), () -> location.getRSTile().distanceTo(Player.getPosition()) < 10 ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS){
            WaitFor.milliseconds(250, 500);
            return true;
        }
        return false;
    }

}
