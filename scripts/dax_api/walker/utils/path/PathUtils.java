package scripts.dax_api.walker.utils.path;

import org.tribot.api2007.Player;
import org.tribot.api2007.Projection;
import org.tribot.api2007.types.RSTile;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PathUtils {

    public static RSTile getNextTileInPath(RSTile current, List<RSTile> path) {
        int index = path.indexOf(current);

        if (index == -1) {
            return null;
        }

        int next = index + 1;
        return next < path.size() ? path.get(next) : null;
    }

    public static RSTile getClosestTileInPath(List<RSTile> path) {
        RSTile player = Player.getPosition();
        return path.stream().min(Comparator.comparingDouble(o -> o.distanceToDouble(player))).orElse(null);
    }

    public static RSTile getFurthestReachableTileInMinimap(List<RSTile> path) {
        List<RSTile> reversed = new ArrayList<>(path);
        Collections.reverse(reversed);

        DaxPathFinder.Destination[][] map = DaxPathFinder.getMap();
        for (RSTile tile : reversed) {
            Point point = Projection.tileToMinimap(tile);
            if (point == null) {
                continue;
            }
            if (DaxPathFinder.canReach(map, tile) && Projection.isInMinimap(point)) {
                return tile;
            }
        }
        return null;
    }

    public static RSTile getFurthestReachableTileOnScreen(List<RSTile> path) {
        List<RSTile> reversed = new ArrayList<>(path);
        Collections.reverse(reversed);

        DaxPathFinder.Destination[][] map = DaxPathFinder.getMap();
        for (RSTile tile : reversed) {
            if (DaxPathFinder.canReach(map, tile) && tile.isOnScreen() && tile.isClickable()) {
                return tile;
            }
        }
        return null;
    }

    public static void drawDebug(Graphics graphics, List<RSTile> path) {
        Graphics2D g = (Graphics2D) graphics;
        RSTile player = Player.getPosition();

        g.setColor(new Color(0, 191, 23, 80));
        for (RSTile tile : path) {
            if (tile.distanceTo(player) > 25) {
                continue;
            }
            Polygon polygon = Projection.getTileBoundsPoly(tile, 0);
            if (polygon == null) {
                continue;
            }
            g.fillPolygon(polygon);
        }

        RSTile closest = getClosestTileInPath(path);
        if (closest != null) {
            Polygon polygon = Projection.getTileBoundsPoly(closest, 0);
            if (polygon != null) {
                g.setColor(new Color(205, 0, 255, 80));
                g.fillPolygon(polygon);

                g.setColor(Color.BLACK);
                graphics.drawString("Closest In Path", polygon.xpoints[0] - 24, polygon.ypoints[1] + 1);
                g.setColor(Color.WHITE);
                graphics.drawString("Closest In Path", polygon.xpoints[0] - 25, polygon.ypoints[1]);
            }
        }

        RSTile furthestScreenTile = getFurthestReachableTileOnScreen(path);
        if (furthestScreenTile != null) {
            Polygon polygon = Projection.getTileBoundsPoly(furthestScreenTile, 0);
            if (polygon != null) {
                g.setColor(new Color(255, 0, 11, 157));
                g.fillPolygon(polygon);

                g.setColor(Color.BLACK);
                graphics.drawString("Furthest Screen Tile", polygon.xpoints[0] - 24, polygon.ypoints[1] + 30);
                g.setColor(Color.WHITE);
                graphics.drawString("Furthest Screen Tile", polygon.xpoints[0] - 25, polygon.ypoints[1] + 30);
            }
        }

        RSTile furthestMapTile = getFurthestReachableTileInMinimap(path);
        if (furthestMapTile != null) {
            Point p = Projection.tileToMinimap(furthestMapTile);
            if (p != null) {
                g.setColor(new Color(255, 0, 11, 157));
                g.fillRect(p.x - 3, p.y - 3, 6, 6);

                g.setColor(Color.BLACK);
                graphics.drawString("Furthest Map Tile", p.x + 1, p.y + 14);
                g.setColor(Color.WHITE);
                graphics.drawString("Furthest Map Tile", p.x, p.y + 15);
            }
        }

        RSTile nextTile = getNextTileInPath(furthestMapTile, path);
        if (nextTile != null) {
            Polygon polygon = Projection.getTileBoundsPoly(nextTile, 0);
            if (polygon != null) {
                g.setColor(new Color(255, 242, 0, 157));
                g.fillPolygon(polygon);

                g.setColor(Color.BLACK);
                graphics.drawString("Next Tile", polygon.xpoints[0] - 24, polygon.ypoints[1]);
                g.setColor(Color.WHITE);
                graphics.drawString("Next Tile", polygon.xpoints[0] - 25, polygon.ypoints[1]);
            }
        }


    }

    public static class NotInPathException extends RuntimeException {
        public NotInPathException() {
        }
    }

}
