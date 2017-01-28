package scripts.webwalker_logic.local.walker_engine;


import java.util.Random;

public class WaitFor {

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
        TIMEOUT,
        SUCCESS,
        FAIL,
        IGNORE

    }

    public interface ReturnCondition <V> {
        V getValue();
    }

    public interface Condition{
        Return active();
    }

}
