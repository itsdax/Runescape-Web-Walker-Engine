package scripts.webwalker_logic.shared.helpers;


import org.tribot.api2007.Interfaces;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSInterfaceComponent;
import org.tribot.api2007.types.RSInterfaceMaster;

import java.util.*;

public class InterfaceHelper {

    /**
     *
     * @param id
     * @return never null
     */
    public static List<RSInterface> getAllInterfaces(int id){
        ArrayList<RSInterface> interfaces = new ArrayList<>();
        Queue<RSInterface> queue = new LinkedList<>();
        RSInterfaceMaster master = Interfaces.get(id);

        if (master == null){
            return interfaces;
        }

        queue.add(master);
        RSInterfaceComponent[] components = master.getComponents();
        if (components != null) {
            Collections.addAll(queue, components);
        }

        while (!queue.isEmpty()){
            RSInterface rsInterface = queue.poll();
            interfaces.add(rsInterface);
            RSInterface[] children = rsInterface.getChildren();
            if (children != null) {
                Collections.addAll(queue, children);
            }
        }

        return interfaces;
    }

    public static List<RSInterface> getAllInterfaces(RSInterface parent){
        ArrayList<RSInterface> interfaces = new ArrayList<>();
        Queue<RSInterface> queue = new LinkedList<>();

        if (parent == null){
            return interfaces;
        }

        queue.add(parent);
        while (!queue.isEmpty()){
            RSInterface rsInterface = queue.poll();
            interfaces.add(rsInterface);
            RSInterface[] children = rsInterface.getChildren();
            if (children != null) {
                Collections.addAll(queue, children);
            }
        }

        return interfaces;
    }
}
