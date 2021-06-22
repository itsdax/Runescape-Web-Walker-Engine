package scripts.dax_api.walker.handlers.passive_action.impl;

import org.tribot.api.General;
import org.tribot.api2007.Game;
import org.tribot.api2007.Options;
import scripts.dax_api.walker.handlers.passive_action.PassiveAction;
import scripts.dax_api.walker.models.enums.ActionResult;

public class RunEnergyPassiveAction implements PassiveAction {

    private int random;

    public RunEnergyPassiveAction() {
        random = General.randomSD(3, 20, 10, 3);
    }

    @Override
    public boolean shouldActivate() {
        return !Options.isRunEnabled() && Game.getRunEnergy() > random;
    }

    @Override
    public ActionResult activate() {
        random = General.randomSD(3, 20, 10, 3);
        return Options.setRunEnabled(true) ? ActionResult.CONTINUE : ActionResult.FAILURE;
    }

}
