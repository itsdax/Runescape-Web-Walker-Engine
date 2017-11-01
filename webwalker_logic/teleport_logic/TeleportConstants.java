package scripts.webwalker_logic.teleport_logic;

import org.tribot.api2007.Interfaces;
import org.tribot.api2007.types.RSInterface;

public class TeleportConstants {
    
    private static int WILD_LVL_MASTER = 90, WILD_LVL_CHILD = 46;

    public static final TeleportLimit
            LEVEL_20_WILDERNESS_LIMIT = () -> getWildernessLevel() < 20,
            LEVEL_30_WILDERNESS_LIMIT = () ->  getWildernessLevel() < 30;
    
    private static int getWildernessLevel(){
        try{
			RSInterface level = Interfaces.get(WILD_LVL_MASTER, WILD_LVL_CHILD);
			if(level == null)
				return 0;
			String txt = level.getText();
			if(txt == null)
				return 0;
			return Integer.parseInt(txt.replaceAll("[^0-9]", ""));
		} catch(Exception e){
			return 0;
		}
    }

}
