package scripts.dax_api.walker_engine;


import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.interfaces.Clickable07;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.Game;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSCharacter;
import org.tribot.api2007.types.RSGroundItem;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;

import java.util.Random;

public class WaitFor {

    private static final Random random = new Random();

    public static Condition getNotMovingCondition(){
        return new Condition() {
            final RSTile initialTile = Player.getPosition();
            final long movingDelay = 1300, startTime = System.currentTimeMillis();

            @Override
            public Return active() {
                if (Timing.timeFromMark(startTime) > movingDelay && initialTile.equals(Player.getPosition()) && !Player.isMoving()) {
                    return Return.FAIL;
                }
                return Return.IGNORE;
            }
        };
    }

    public static int getMovementRandomSleep(Positionable positionable){
        return getMovementRandomSleep(Player.getPosition().distanceTo(positionable));
    }

    public static int getMovementRandomSleep(int distance){
        final double multiplier =  Game.isRunOn() ? 0.3 : 0.6;
        final int base = random(1800, 2400);
        if (distance > 25){
            return base;
        }
        int sleep = (int) (multiplier * distance);
        return (int)General.randomSD(base * .8, base * 1.2, base, base * 0.1) + sleep;
    }


    public static Return isOnScreenAndClickable(Clickable07 clickable){
        Positionable positionable = (Positionable) clickable;
        return WaitFor.condition(getMovementRandomSleep(positionable), () -> (
                clickable instanceof RSCharacter ? ((RSCharacter) clickable).isOnScreen() :
                clickable instanceof RSObject ? ((RSObject) clickable).isOnScreen() :
                clickable instanceof RSGroundItem && ((RSGroundItem) clickable).isOnScreen())
                && clickable.isClickable() ? Return.SUCCESS : Return.IGNORE);
    }

    public static Return condition(int timeout, Condition condition){
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() < startTime + timeout){
            switch (condition.active()){
                case SUCCESS: return Return.SUCCESS;
                case FAIL: return Return.FAIL;
                case IGNORE: milliseconds(75);
            }
        }
        return Return.TIMEOUT;
    }

    /**
     *
     * @param timeout
     * @param condition
     * @param <V>
     * @return waits {@code timeout} for the return value to not be null.
     */
    public static <V> V getValue(int timeout, ReturnCondition<V> condition){
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() < startTime + timeout){
            V v = condition.getValue();
            if (v != null){
                return v;
            }
            milliseconds(25);
        }
        return null;
    }

    public static int random(int low, int high) {
        return random.nextInt((high - low) + 1) + low;
    }

    public static Return milliseconds(int low, int high){
        try {
            Thread.sleep(random(low, high));
        } catch (InterruptedException e){
            throw new IllegalStateException("Break out");
        }
        return Return.IGNORE;
    }

    public static Return milliseconds(int amount){
        return milliseconds(amount, amount);
    }


    public enum Return {
        TIMEOUT,    //EXIT CONDITION BECAUSE OF TIMEOUT
        SUCCESS,    //EXIT CONDITION BECAUSE SUCCESS
        FAIL,       //EXIT CONDITION BECAUSE OF FAILURE
        IGNORE      //NOTHING HAPPENS, CONTINUE CONDITION

    }

    public interface ReturnCondition <V> {
        V getValue();
    }

    public interface Condition{
        Return active();
        default Condition combine(Condition a){
            Condition b = this;
            return () -> {
                Return result = a.active();
                return result != Return.IGNORE ? result : b.active();
            };
        }
    }

}
