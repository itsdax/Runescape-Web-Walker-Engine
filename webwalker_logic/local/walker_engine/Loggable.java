package scripts.webwalker_logic.local.walker_engine;


import scripts.webwalker_logic.WebWalker;

public interface Loggable {

    enum Level {
        VERBOSE,
        INFO,
        SEVERE
    }

    String getName();

    default void log(CharSequence debug){
        if (!WebWalker.isLogging()){
            return;
        }
        System.out.println("[" + getName() + "] " + debug);
    }

    default void log(Level level, CharSequence debug){
        if (!WebWalker.isLogging()){
            return;
        }
        System.out.println(level + " [" + getName() + "] " + debug);
    }
}
