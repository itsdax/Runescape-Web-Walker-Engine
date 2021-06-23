package dax.walker.handlers.special_cases;

import dax.walker.handlers.passive_action.PassiveAction;
import dax.walker.models.MoveTask;
import dax.walker.models.enums.MoveActionResult;

import java.util.List;

public interface SpecialCaseHandler {

    boolean shouldHandle(MoveTask moveTask);

    MoveActionResult handle(MoveTask moveTask, List<PassiveAction> passiveActionList);

    default String getName() {
        return this.getClass().getSimpleName();
    }

}
