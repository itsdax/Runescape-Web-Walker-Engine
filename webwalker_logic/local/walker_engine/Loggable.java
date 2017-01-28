package scripts.webwalker_logic.local.walker_engine;


import scripts.webwalker_logic.WebWalker;

public interface Loggable {
    String getName();
    default void log(CharSequence debug){
        if (!WebWalker.isLogging()){
            return;
        }
        System.out.println("[" + getName() + "] " + debug);
    }
}
