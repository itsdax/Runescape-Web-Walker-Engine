package scripts.webwalker_logic.teleport_logic;

import org.tribot.api2007.Combat;
import org.tribot.api2007.Interfaces; 

public class TeleportConstants {
    
    private static int WILDERNESS_MASTER = 90,
                       WILDERNESS_CHILD = 46;

    public static final TeleportLimit
            LEVEL_20_WILDERNESS_LIMIT = () -> Combat.getWildernessLevel() < 20,
            LEVEL_30_WILDERNESS_LIMIT = () -> Combat.getWildernessLevel() < 30;
    
    private static int getWildernessLevel(){
        try{
			RSInterface level = Interfaces.get(WILDERNESS_MASTER, WILDERNESS_CHILD);
			if(level == null)
				return 0;
			String txt = level.getText();
			if(txt == null)
				return 0;
			return Integer.parseInt(Utilities.extractNumber(txt));
		} catch(Exception e){
			return 0;
		}
    }

}
