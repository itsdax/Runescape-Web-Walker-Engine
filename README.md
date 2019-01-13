# Runescape-Web-Walker-Engine
**Path Finding** for RuneScape's graph-based map with over **10 million** nodes optimized to generate any path in **<200ms**.

## Visit **[Explv's](https://explv.github.io/)** Project to check out pathfinding with Dax Path generation!
<p align="center">
  <img src="https://i.imgur.com/Haf7BNb.gif"/>
</p>


<p align="center">
  <b>Debugger -> Explv's Map</b>
  <img src="https://i.imgur.com/80Qo9nY.png"/>
</p>

## Importing to Tribot.
- **DO NOT CLONE INTO SCRIPTS DIRECTORY!** Tribot will not be able to detect it.
- Copy the dax_api package inside your scripts directory. If you need further help, the visit the [Tribot Forums](https://tribot.org/forums/topic/68923-universal-web-walker-open-source/). 

## Documentation
View [JavaDocs](https://itsdax.github.io/Runescape-Web-Walker-Engine/)

## API Keys
Please visit https://admin.dax.cloud/ for more information. To use your Api Keys, please configure your Dax API Key provider.
```java8
        DaxWalker.setCredentials(new DaxCredentialsProvider() {
            @Override
            public DaxCredentials getDaxCredentials() {
                return new DaxCredentials("YOUR-PUBLIC-KEY", "YOUR-SECRET-KEY");
            }
        });
```
[![Api Keys](https://i.imgur.com/Qwc0115.png)](https://admin.dax.cloud)

## About
- This is the **client side** of dax walker, which includes a wrapper for the server-client interaction of generating paths from point A to point B and navigating through the path.
- Back-end pathfinding is coded using a combination of dijkstra's and A\* algorithm. Dijkstra's is mainly for region limiting for performance purposes whereas A\* calculates the actual path. Wayports (Node jumps) cannot be calculated using a heuristic value so Dijkstra's is needed in this scenario.
- The Walker Engine includes path walking, waypoint navigation (Ship Chartering/Portals/etc), and path randomization using BFS to prevent trackable walking patterns.


## Features
- Speed. Despite RuneScape's huge world of 10M tiles in a sparse map of **15000x15000x4**, my optimized engine will generate a path from any two points in less than **200ms**, guaranteed. My custom heuristic function for A* will calculate portals and teleports without loss of accuracy for your individual character.

- Ease of use. Implement the engine into your script by simply calling:
```java8
        DaxWalker.walkTo(new RSTile(1,2,3));
        DaxWalker.walkToBank(Bank.VARROCK_EAST);
```

- Shortcuts. Using all and only the shortcuts that your Player can access, whether it is skill level (Agility level needed for shortcut) or inventory item requirements (Such as gold needed for ship or fee to enter dungeon). This also includes quest requirements.

- Obstacles. All obstacles such as doors/ladders/etc are supported as long as area is mapped.

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
      WebWalkerPaint.getInstance().drawDebug(graphics);
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

## What's Included
- Client side shortest path calculation for every location in the current region in a single call.

###### Real time visualization:

  ```java8
  Reachable.getMap();
  ```
<p align="center">
  <img src="https://i.imgur.com/4hZi3eM.gif"/>
</p>

## Visualization of the server generating a path from point A to point B (Slowed Down)
The algorithm is designed to limit itself to only the regions that will lead towards the destination. This is how we will generate paths in the fastest time possible.
###### UI is coded on a canvas in JavaFX. Code is not included in this repository.

<p align="center">
  <img src="http://i.imgur.com/ZD7hKWZ.gif"/>
</p>
