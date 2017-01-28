# Runescape-Web-Walker-Engine
####A world walker for Rune Scape written in Java for TriBot.

##About
- This is the **front end** of my web walker, which includes a wrapper for the server-client interaction of generating paths from point A to point B and navigating through the path.
- Back-end pathfinding is coded using a combination of dijkstra's and A\* algorithm. Dijstra's is mainly for region limiting for performance purposes whereas A\* calculates the actual path.
- The Walker Engine includes path walking, waypoint navigation (Ship Chartering/Portals/etc), and path randomization using BFS to prevent trackable walking patterns.

##Features
- Speed. Will generate a path from any two points (Given that it is mapped) in less than a second, guaranteed.

- Simplicity. Implement the engine into your script by simply calling
      ```
      WebWalker.walkTo(new RSTile(x, y, z)
      ```
- Shortcuts. Using all and only the shortcuts that your Player can access, whether it is skill level requirements, or inventory item requirements.

- Debugging visualization. Draw a live feed of **Path** and **Collision Data** the engine is working with.
      ```
      WebWalkerPaint.getInstance().drawDebug(graphics, WebPath.previousPath(), true);
      ```
      
<p align="center">
  <img src="http://i.imgur.com/17hx5iK.png"/>
</p>

<p align="center">
  <img src="http://i.imgur.com/gLMRq0O.png"/>
</p>


###Visualization of the server generating a path from point A to point B (Slowed Down)

<p align="center">
  <img src="http://i.imgur.com/ZD7hKWZ.gif"/>
</p>

##Supported Areas (Roughly 90% of the world)
- All Cities (Except Lletya)
- Gnome Slayer Dungeon
- Relleka Slayer Dungeon
- Most Dungeons (Falador Mine, Varrock Sewers, etc)


