package scripts.dax_api.engine.models;

import org.tribot.api2007.types.RSTile;

public class WalkTask {

    private WalkTaskType walkTaskType;
    private RSTile current, next;

    public WalkTask(WalkTaskType walkTaskType, RSTile current, RSTile next) {
        this.walkTaskType = walkTaskType;
        this.current = current;
        this.next = next;
    }

    public WalkTaskType getWalkTaskType() {
        return walkTaskType;
    }

    public RSTile getCurrent() {
        return current;
    }

    public RSTile getNext() {
        return next;
    }

}