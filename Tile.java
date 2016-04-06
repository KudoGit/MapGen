import java.awt.Color;
import java.awt.Dimension;
import java.util.LinkedList;
import java.util.List;


public class Tile {
  boolean walkable;
  boolean shootable;
  boolean enemySpot;
  boolean special;
  boolean selected;
  Dimension pos;
  float interest = 0;
  List<Tile> walkreachable;
  boolean found;
  
  LinkedList<Tile> neighbors = null;
  
  Tile(boolean walk, boolean shoot) {
    walkable = walk;
    shootable = shoot;
    
  }
  

  //finds the color of a tile based on its properties
  public Color getColor() {
    if(walkable && shootable) return Color.GREEN; //regular space
    if(!walkable && shootable) return Color.RED; //can attack through cannot walk on
    if(walkable && !shootable) return Color.YELLOW; // anti-bullet field
    if(!walkable && !shootable) return Color.GRAY;  // unplayable space
    //TEMPORARY NOTE- add more properities here
    
    return Color.MAGENTA; //error
  }
  
  //neighbors include the 4 tiles directly up, down, left, and right from the current tile.
  //locations off the map are ignored, making it possible for a tile to only have 2 neighbors
  public List<Tile> getNeighbors(Tile[][] map) {
    if(neighbors != null) return neighbors;
    neighbors = new LinkedList<Tile>();
    if(pos.width+1 < map.length)              neighbors.add(map[pos.width+1][pos.height]);
    if(pos.width-1 >= 0)                      neighbors.add(map[pos.width-1][pos.height]);
    if(pos.height+1 < map[pos.width].length)  neighbors.add(map[pos.width][pos.height+1]);
    if(pos.height-1 >= 0)                     neighbors.add(map[pos.width][pos.height-1]);
    
    return neighbors;
  }
}
