package dax.walker.handlers.passive_action;

import dax.walker.models.enums.ActionResult;

public interface PassiveAction {

    default boolean shouldActivate() {
        return true;
    }

    ActionResult activate();

}
