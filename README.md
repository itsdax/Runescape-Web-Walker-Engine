# Runescape-Web-Walker-Engine
####A world walker for Rune Scape written for TriBot

##About
- This is the front end of my web walker, which includes a wrapper for the server-client interaction of generating paths from point A to point B and navigating through the path.
- Back-end pathfinding is coded using a combination of dijkstra's and A\* algorithm. Dijstra's is mainly for region limiting for performance purposes whereas A* calculates the actual path.
- The Walker Engine includes path walking, waypoint navigation (Ship Chartering/Portals/etc), and path randomization using BFS to prevent trackable walking patterns.

##Features
- Speed. Will generate a path from any two point (Given that it is mapped) in less than a second.

- Simplicity. Implement the engine into your script by simply calling
```
WebWalker.walkTo(new RSTile(x, y, z)
```

- Debugging. Visualization. Draw a live feed of **Path** and **Collision Data** the engine is working with.
```
WebWalkerPaint.getInstance().drawDebug(graphics, WebPath.previousPath(), true);
```
<p align="center">
  <img src="http://i.imgur.com/17hx5iK.png"/>
</p>

<p align="center">
  <img src="http://i.imgur.com/gLMRq0O.png"/>
</p>


