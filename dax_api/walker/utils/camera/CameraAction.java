package scripts.dax_api.walker.utils.camera;


import org.tribot.api.General;
import org.tribot.api.input.Mouse;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.types.RSCharacter;
import org.tribot.api2007.types.RSTile;
import org.tribot.api2007.types.RSVarBit;
import scripts.dax_api.walker.utils.movement.WalkingQueue;
import scripts.dax_api.walker_engine.WaitFor;

import java.awt.*;

public class CameraAction {

    private static final Rectangle HOVER_BOX = new Rectangle(140, 20, 260, 110);

    public static void moveCamera(Positionable destination){
        if (isMiddleMouseCameraOn()){
            DaxCamera.focus(destination);
        } else {
            AsynchronousCamera.focus(destination);
        }
    }

    public static boolean focusCamera(Positionable positionable){
        RSTile tile = positionable.getPosition();
        if (tile.isOnScreen() && tile.isClickable()){
            return true;
        }

        if (isMiddleMouseCameraOn()){
            DaxCamera.focus(tile);
            return tile.isOnScreen() && tile.isClickable();
        } else {
            AsynchronousCamera.focus(tile);
            if (!HOVER_BOX.contains(Mouse.getPos())) {
                Mouse.moveBox(HOVER_BOX);
            }
            return WaitFor.condition(General.random(3000, 5000), () -> tile.isOnScreen() && tile.isClickable() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
        }
    }

    public static boolean focusCamera(RSCharacter rsCharacter){
        if (rsCharacter.isOnScreen() && rsCharacter.isClickable()){
            return true;
        }

        RSTile destination = rsCharacter.getPosition();
        RSTile newDestination = WalkingQueue.getWalkingTowards(rsCharacter);
        if (newDestination != null){
            destination = newDestination;
        }

        if (isMiddleMouseCameraOn()){
            DaxCamera.focus(destination);
            return rsCharacter.isOnScreen() && rsCharacter.isClickable();
        } else {
            AsynchronousCamera.focus(destination);
            if (!HOVER_BOX.contains(Mouse.getPos())) {
                Mouse.moveBox(HOVER_BOX);
            }
            return WaitFor.condition(General.random(3000, 5000), () -> rsCharacter.isOnScreen() && rsCharacter.isClickable() ? WaitFor.Return.SUCCESS : WaitFor.Return.IGNORE) == WaitFor.Return.SUCCESS;
        }
    }


    public static boolean isMiddleMouseCameraOn() {
        return RSVarBit.get(4134).getValue() == 0;
    }

}
