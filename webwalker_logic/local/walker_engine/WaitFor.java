package scripts.webwalker_logic.local.walker_engine;


import org.tribot.api.General;
import org.tribot.api.interfaces.Clickable;
import org.tribot.api.interfaces.Clickable07;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.Game;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.*;

import java.util.Random;

public class WaitFor {

    public static int getMovementRandomSleep(Positionable positionable){
        return getMovementRandomSleep(Player.getPosition().distanceTo(positionable));
    }

    public static int getMovementRandomSleep(int distance){
        final int multiplier =  Game.isRunOn() ? 1 : 2, base = random(900, 1100);
        if (distance > 25){
            return base;
        }
        int sleep = (multiplier * distance);
        return base + (int) General.randomSD(sleep * .7, sleep * 1.3, sleep, sleep * .125);
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
                case IGNORE: milliseconds(25);
            }
        }
        return Return.TIMEOUT;
    }

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

    private static int random(int low, int high) {
        return new Random().nextInt((high - low) + 1) + low;
    }

    public static Return milliseconds(int low, int high){
        try {
            Thread.sleep(random(low, high));
        } catch (InterruptedException e){

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
