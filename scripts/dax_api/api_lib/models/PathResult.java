package scripts.dax_api.api_lib.models;

import com.allatori.annotations.DoNotRename;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.tribot.api2007.types.RSTile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@DoNotRename
public class PathResult {

    @DoNotRename
    private PathStatus pathStatus;

    @DoNotRename
    private List<Point3D> path;

    @DoNotRename
    private int cost;

    private PathResult () {

    }

    public PathResult (PathStatus pathStatus) {
        this.pathStatus = pathStatus;
    }

    public PathResult(PathStatus pathStatus, List<Point3D> path, int cost) {
        this.pathStatus = pathStatus;
        this.path = path;
        this.cost = cost;
    }

    public PathStatus getPathStatus() {
        return pathStatus;
    }

    public void setPathStatus(PathStatus pathStatus) {
        this.pathStatus = pathStatus;
    }

    public List<Point3D> getPath() {
        return path;
    }

    public void setPath(List<Point3D> path) {
        this.path = path;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public ArrayList<RSTile> toRSTilePath() {
        if (getPath() == null) {
            return new ArrayList<>();
        }
        ArrayList<RSTile> path = new ArrayList<>();
        for (Point3D point3D : getPath()) {
            path.add(point3D.toPositionable().getPosition());
        }
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PathResult that = (PathResult) o;
        return cost == that.cost &&
                pathStatus == that.pathStatus &&
                Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pathStatus, path, cost);
    }

    public static PathResult fromJson(JsonElement jsonObject) {
        return new Gson().fromJson(jsonObject, PathResult.class);
    }

}
