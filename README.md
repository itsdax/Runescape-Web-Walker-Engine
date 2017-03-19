# Runescape-Web-Walker-Engine
#### An open-sourced world walker for Rune Scape written in Java for TriBot.

## How to add
- Simply copy the webwalker_logic package over to your scripts directory!

<p align="center">
  <img src="http://i.imgur.com/Age76Qx.png"/>
</p>

<p align="center">
  <img src="http://i.imgur.com/Fxvn5C1.png"/>
</p>

## API Keys

The public key is limited to 400 calls ever minute. The public api key is already included in the source.
If you require more calls, or wish to support my project, please refer to [here](https://tribot.org/forums/topic/68923-universal-web-walker-open-source/) for API Keys. 


## About
- This is the **front end** of my web walker, which includes a wrapper for the server-client interaction of generating paths from point A to point B and navigating through the path.
- Back-end pathfinding is coded using a combination of dijkstra's and A\* algorithm. Dijkstra's is mainly for region limiting for performance purposes whereas A\* calculates the actual path. Wayports (Node jumps) cannot be calculated using a heuristic value so Dijkstra's is needed in this scenario.
- The Walker Engine includes path walking, waypoint navigation (Ship Chartering/Portals/etc), and path randomization using BFS to prevent trackable walking patterns.


## Features
- Speed. Will generate a path from any two points (Given that it is mapped) in less than a second, guaranteed.

- Simplicity. Implement the engine into your script by simply calling:
      ```
      WebWalker.walkTo(new RSTile(x, y, z));
      WebWalker.walkTo(new RSTile(x, y, z), walkingCondition);
      WebWalker.walkToBank();
      ```

- Shortcuts. Using all and only the shortcuts that your Player can access, whether it is skill level (Agility level needed for shortcut) or inventory item requirements (Such as gold needed for ship or fee to enter dungeon). This also includes quest requirements.


## Supported Areas (Currently roughly 90% of the game world)
- All Cities (Except Lletya) including Zeah
- Wilderness
- Gnome Slayer Dungeon
- Relleka Slayer Dungeon
- Stronghold of Security
- Most underground locations (Falador Mine, Varrock Sewers, etc)


## Debug
- Debugging visualization. Draw a live feed of the **Path** and **Collision Data** the engine is working with on the minimap using:
      ```
      WebWalkerPaint.getInstance().drawDebug(graphics, WebPath.previousPath(), true);
      ```
      
<p align="center">
  <img src="http://i.imgur.com/17hx5iK.png"/>
</p>

<p align="center">
  <img src="http://i.imgur.com/gLMRq0O.png"/>
</p>

## Directed Nodes
- Nodes that direct to another, and not vice versa (Such as one way entrances) are supported as well.

###### Here is an example of Draynor Manor's one way door

<p align="center">Path from outside to inside. (Enters front door.)
<p align="center">
      <img src="http://i.imgur.com/2B2MyZ8.png"/>
</p>

<p align="center">Path from inside to outside. (Exits through back door.)
<p align="center">
<img src="http://i.imgur.com/Ne2Ydy1.png"/>
</p>

## Visualization of the server generating a path from point A to point B (Slowed Down)
The algorithm is designed to limit itself to only the regions that will lead towards the destination. This is how we will generate paths in the fastest time possible.
###### UI is coded on a canvas in JavaFX. Code is not included in this repository.

<p align="center">
  <img src="http://i.imgur.com/ZD7hKWZ.gif"/>
</p>
