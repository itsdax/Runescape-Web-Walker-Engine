package scripts.dax_api.api_lib.models;

import com.allatori.annotations.DoNotRename;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.types.RSTile;

@DoNotRename
public class Point3D {


    @DoNotRename
    private int x, y, z;

    public Point3D(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public JsonElement toJson() {
        return new Gson().toJsonTree(this);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }

    public Positionable toPositionable() {
        return new Positionable() {
            @Override
            public RSTile getAnimablePosition() {
                return new RSTile(x, y, z);
            }

            @Override
            public boolean adjustCameraTo() {
                return false;
            }

            @Override
            public RSTile getPosition() {
                return new RSTile(x, y, z);
            }
        };
    }

    public static Point3D fromPositionable(Positionable positionable) {
        RSTile rsTile = positionable.getPosition();
        return new Point3D(rsTile.getX(), rsTile.getY(), rsTile.getPlane());
    }

}
