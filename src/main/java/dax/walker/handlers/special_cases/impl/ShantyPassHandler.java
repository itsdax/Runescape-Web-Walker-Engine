package dax.walker.handlers.special_cases.impl;

import dax.walker.handlers.move_task.MoveTaskHandler;
import dax.walker.handlers.passive_action.PassiveAction;
import dax.walker.models.DaxLogger;
import dax.walker.models.MoveTask;
import dax.walker.models.enums.MoveActionResult;
import dax.walker.handlers.special_cases.SpecialCaseHandler;

import java.util.List;

public class ShantyPassHandler implements MoveTaskHandler, DaxLogger, SpecialCaseHandler {

    @Override
    public boolean shouldHandle(MoveTask moveTask) {
        return false;
    }

    @Override
    public MoveActionResult handle(MoveTask moveTask, List<PassiveAction> passiveActionList) {
        return null;
    }

    
}
