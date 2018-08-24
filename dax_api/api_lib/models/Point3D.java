package scripts.dax_api.api_lib.models;

import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.types.RSTile;
import scripts.dax_api.api_lib.json.JsonObject;

public class Point3D {

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

    public JsonObject toJson() {
        return new JsonObject().add("x", x).add("y", y).add("z", z);
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
            public RSTile getPosition() {
                return new RSTile(x, y, z);
            }
        };
    }

    public static Point3D fromPositionable(Positionable positionable) {
        RSTile rsTile = positionable.getPosition();
        return new Point3D(rsTile.getX(), rsTile.getY(), rsTile.getPlane());
    }

    public static Point3D fromJson(JsonObject jsonObject) {
        return new Point3D(
                jsonObject.getInt("x", -1),
                jsonObject.getInt("y", -1),
                jsonObject.getInt("z", -1)
        );
    }

}
