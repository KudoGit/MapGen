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
  
  public Color getColor() {
    if(walkable && shootable) return Color.GREEN;
    if(!walkable && shootable) return Color.RED;
    if(walkable && !shootable) return Color.YELLOW; // anti-bullet field
    if(!walkable && !shootable) return Color.GRAY;
    return Color.MAGENTA;
  }
  
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
