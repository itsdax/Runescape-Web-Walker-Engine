# Runescape-Web-Walker-Engine
####A world walker for Rune Scape written for TriBot

##About
- This is the Walker Engine, which includes a wrapper for the server-client interaction of generating paths from point A to point B. 
- Back-end pathfinding is coded using a combination of dijkstra's and A* algorithm. 
- The front-end (Walker Engine) includes path walking, and randomization using BFS.

##Usages
- Debugging **Path** and **Collision Data using** paint
```
WebWalkerPaint.getInstance().drawDebug(graphics, WebPath.previousPath(), true);
```
<p align="center">
  <img src="http://i.imgur.com/17hx5iK.png"/>
</p>

<p align="center">
  <img src="http://i.imgur.com/gLMRq0O.png"/>
</p>
