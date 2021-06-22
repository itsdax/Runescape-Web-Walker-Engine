package scripts.dax_api.walker_engine.navigation_utils.fairyring.letters;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSVarBit;
import scripts.dax_api.walker_engine.navigation_utils.fairyring.FairyRing;

public enum ThirdLetter {
    P(0),
    Q(3),
    R(2),
    S(1)
    ;
    int value;
    ThirdLetter(int value){
        this.value = value;
    }

    public static final int
            VARBIT = 3987,
            CLOCKWISE_CHILD = 23,
            ANTI_CLOCKWISE_CHILD = 24;

    public int getValue(){
        return value;
    }

    private static int get(){
        return RSVarBit.get(VARBIT).getValue();
    }

    public boolean isSelected(){
        return get() == this.value;
    }

    public boolean turnTo(){
        int current = get();
        int target = getValue();
        if(current == target)
            return true;
        int diff = current - target;
        int abs = Math.abs(diff);
        if(abs == 2){
            return General.randomBoolean() ? turnClockwise(2) : turnAntiClockwise(2);
        } else if(diff == 3 || diff == -1){
            return turnClockwise(1);
        } else {
            return turnAntiClockwise(1);
        }
    }

    public static boolean turnClockwise(int rotations){
        if(rotations == 0)
            return true;
        RSInterface iface = getClockwise();
        final int value = get();
        return iface != null && iface.click()
                && Timing.waitCondition(() -> get() != value ,2500)
                && turnClockwise(--rotations);
    }

    public static boolean turnAntiClockwise(int rotations){
        if(rotations == 0)
            return true;
        RSInterface iface = getAntiClockwise();
        final int value = get();
        return iface != null && iface.click()
                && Timing.waitCondition(() -> get() != value ,2500)
                && turnAntiClockwise(--rotations);
    }


    private static RSInterface getClockwise() {
        return Interfaces.get(FairyRing.INTERFACE_MASTER, CLOCKWISE_CHILD);
    }
    private static RSInterface getAntiClockwise() {
        return Interfaces.get(FairyRing.INTERFACE_MASTER, ANTI_CLOCKWISE_CHILD);
    }
}
