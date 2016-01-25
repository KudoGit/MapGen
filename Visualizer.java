import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class Visualizer extends JPanel {
  Random random;

  public File currentFile;
  
  public long seed = 75;
  public long eseed = 75;
  
  int unit_width = 15;
  int unit_height = 15;
  
  int minSize = 1;
  int maxSize = unit_width*unit_height;
  
  int minIslands = 1;
  int maxIslands = unit_width*unit_height;
  
  double enemyDensity = 0.15;
  double enemyLeniency = 0.6;
  
  Tile[][] map;
  boolean checkIslands = false;
  boolean fixIslands = false;
  boolean showEnemies = false;
  
  boolean unwalkable = true;
  double unwalkableChance = 0.15;
  boolean unshootable = true;
  double unshootableChance = 0.15;
  boolean specialTile = true;
  double specialChance = 0.05;
  
  boolean seededMap = true;

  
  
  public Visualizer() {
    random = new Random(seed);
  }
  
  public void rebuildMap() {
    random.setSeed(seed);
    buildMap(unit_width, unit_height);
  }
  
  public void load() throws FileNotFoundException {
    if(currentFile == null) return;
    Scanner in = new Scanner(currentFile);
    seed = in.nextLong();
    eseed = in.nextLong();
    
    unit_width = in.nextInt();
    unit_height = in.nextInt();
    
    minSize = in.nextInt();
    maxSize = in.nextInt();
    
    minIslands = in.nextInt();
    maxIslands = in.nextInt();
    
    enemyDensity = in.nextDouble();
    enemyLeniency = in.nextDouble();
    //showEnemies = in.nextBoolean();
    in.close();
    rebuildMap();
  }
  
  public void save() throws IOException {
    if(currentFile == null) return;
    String info = Long.toString(seed) + " " + Long.toString(eseed) + " " +
                  Integer.toString(unit_width) + " " + Integer.toString(unit_height) + " " +
                  Integer.toString(minSize) + " " + Integer.toString(maxSize) + " " +
                  Integer.toString(minIslands) + " " + Integer.toString(maxIslands) + " " +
                  Double.toString(enemyDensity) + " " + Double.toString(enemyLeniency) + " ";// +
                  //Boolean.toString(showEnemies);
    BufferedWriter output = null;
    try {
      output = new BufferedWriter(new FileWriter(currentFile));
      output.write(info);
     } catch ( IOException e ) {
      e.printStackTrace();
     } finally {
      if ( output != null ) output.close();
     }
  }
  
  public void buildMap(int width, int height) {
    map = new Tile[width][height];
    // Initialize tiles
    for(int i=0; i < width; i++) {
      for(int j=0; j < height; j++) {
        map[i][j] = new Tile(true, true);
        map[i][j].pos = new Dimension(i,j);
      }
    }
    // plant terrain type seeds and grow them
    double check;
    for(int i=0; i < width; i++) {
      for(int j=0; j < height; j++) {
        check = random.nextDouble();
        // seed for tile traits
        if(check < 0.3 && seededMap) {
          
          // set seed type
          boolean walk = true;
          boolean shoot = true;
          if(check >= 0.2) {
            walk = false;
          } else if(check >= 0.1) {
            shoot = false;
          } else {
            walk = false;
            shoot = false;
          }
          
          // apply traits to seed tile
          map[i][j].walkable = walk;
          map[i][j].shootable = shoot;
          
          // for each neighbor, maybe match trait
          for(int dx = -1; dx < 1; dx++) {
            for(int dy = -1; dy < 1; dy++) {
              if(dx == 0 && dy == 0) continue; // don't roll for seed tile
              if(i+dx < 0 || i+dx >= map.length || j+dy < 0 || j+dy >= map[i+dx].length) continue; // don't leave map bounds
              if(random.nextFloat() < 0.6) {
                map[i+dx][j+dy].walkable = walk;
                map[i+dx][j+dy].shootable = shoot;
              }
            }
          }
        } else if(!seededMap) {
          //unseeded method, all random
          if(check <= unwalkableChance) map[i][j].walkable = false;
          check = random.nextDouble();
          if(check <= unshootableChance) map[i][j].shootable = false;
          check = random.nextDouble();
          if(check <= specialChance && map[i][j].walkable) map[i][j].special = true;
        }
      }
    }

    // calculate pathability
    for(int i=0; i < width; i++) {
      for(int j=0; j < height; j++) {
        checkInterest(i, j, true); //fixIslands);
      }
    }
    random.setSeed(eseed);
    for(int i=0; i < width; i++) {
      for(int j=0; j < height; j++) {
        if(random.nextDouble() > 1.0-enemyDensity  && map[i][j].walkable) map[i][j].enemySpot = true;
        if(map[i][j].interest < enemyLeniency) map[i][j].enemySpot = false;
      }
    }
    
    boolean islandSizeCheck = false;
    if(true) { //checkIslands) {
      Set<Tile> mainland = new HashSet<>();
      List<Set<Tile>> islands = new LinkedList<>();
      for(int i=0; i < width; i++) {
        for(int j=0; j < height; j++) {
          Set<Tile> island = bfs(i, j);
          if(island.size() > 0) {
            if(island.size() < minSize || island.size() > maxSize) {
              islandSizeCheck = true;
              break;
            }
            islands.add(island);
          }
          if(island.size() > mainland.size()) {
            //System.out.println("mainland size: "+mainland.size()+" island size: "+island.size());
            //for(Tile w: mainland) { w.walkable = false; }
            mainland = island;
            //System.out.println("mainland size: "+mainland.size());
            //System.out.println("Inside checkIslands");
          } //else {
            //for(Tile w: island) { w.walkable = false; }
          //}
        }
      }
      if(islands.size() > maxIslands || islands.size() < minIslands || islandSizeCheck) {
        System.out.println("Number of islands: "+islands.size());
        seed = random.nextLong() * System.nanoTime();
        rebuildMap();
      }
    }
    return;
  }

  /*package private*/ void checkInterest(int i, int j, boolean fixIslands) {
    // for each tile, check nearby tiles for reachability
    List<Tile> walkreachable = new LinkedList<>();
    List<Pair<Tile>> queue = new LinkedList<>();
    queue.add(new Pair<Tile>(map[i][j],map[i][j])); // the pair (tile, prevtile) for deprecated reasons
    while(!queue.isEmpty()) {
      Pair<Tile> p = queue.remove(0);
      Tile t = p.first;
      if(t.pos.width < i-5 || t.pos.width > i+5 || t.pos.height < j-5 || t.pos.height > j+5) continue;
      if(walkreachable.contains(t)) continue; // already pathed here
      if(!t.walkable) continue; // untraversable tile
      
      // place into pathing for 
      if(t.walkable) walkreachable.add(t);
      
      // put adjacent neighbors on the queue
      if(t.pos.width+1 < map.length) queue.add(new Pair<Tile>(map[t.pos.width+1][t.pos.height], t));
      if(t.pos.width-1 >= 0) queue.add(new Pair<Tile>(map[t.pos.width-1][t.pos.height], t));
      if(t.pos.height+1 < map[t.pos.width].length) queue.add(new Pair<Tile>(map[t.pos.width][t.pos.height+1], t));
      if(t.pos.height-1 >= 0) queue.add(new Pair<Tile>(map[t.pos.width][t.pos.height-1], t));
    }
    
    
    
    if(fixIslands) {
      if(walkreachable.size() < minSize || walkreachable.size() > maxSize) {
        //System.out.println("Illegal Island! at ("+i+","+j+")");
        //System.out.println("Illegal Island size: " + walkreachable.size());
        map[i][j].walkable = false;
        for(Tile w: walkreachable) {
          w.walkable = false;
          //w.walkreachable.clear();
        }
        walkreachable.clear();
      }
    }

    map[i][j].walkreachable = walkreachable;
    
    float percentwalk = ((float) walkreachable.size())/120;
    int unshootable = 0;
    for(Tile w : walkreachable) { 
      //System.out.println(w.pos);
      if(!w.shootable) unshootable++;
    }
    float percentShootable = ((float) (1.0 - (unshootable/(walkreachable.size()+1.0))));
    map[i][j].interest = percentShootable;
    
    //System.out.println("Tile ("+i+","+j+") Number Walkable: "+walkreachable.size()+" Percent Walkable: "+percentwalk + " Percent Shootable: "+percentShootable);
  }
   
  /*package private*/ Set<Tile> bfs(int i, int j) {
    if(map[i][j].walkable == false || map[i][j].found == true) return new HashSet<>();
    List<Tile> queue = new LinkedList<>();
    Set<Tile> discovered = new HashSet<>();
    map[i][j].found = true;
    queue.add(map[i][j]);
    discovered.add(map[i][j]);
    while(!queue.isEmpty()) {
      Tile t = queue.remove(0);
      List<Tile> neighbors = new LinkedList<>();
      
      if(t.pos.width+1 < map.length) neighbors.add(map[t.pos.width+1][t.pos.height]);
      if(t.pos.width-1 >= 0) neighbors.add(map[t.pos.width-1][t.pos.height]);
      if(t.pos.height+1 < map[t.pos.width].length) neighbors.add(map[t.pos.width][t.pos.height+1]);
      if(t.pos.height-1 >= 0) neighbors.add(map[t.pos.width][t.pos.height-1]);
      
      for(Tile w : neighbors) {
        if(w.walkable == false) continue;
        if(discovered.contains(w)) continue;
        w.found = true;
        queue.add(w);
        discovered.add(w);
      }
    }
    System.out.println("Discovered size: "+discovered.size());
    return discovered;
  }
  
  
  @Override
  public void paint(Graphics g) {
    g.setColor(Color.CYAN);
    int s_width = getWidth();
    int s_height = getHeight();
    g.fillRect(0, 0, s_width, s_height);
    
    int box_width = (s_width - 6) / (map.length);
    int box_height = (s_height - 6) / (map[0].length);
    
    for(int w = 0; w < map.length; w++) {
      for(int h = 0; h < map[0].length; h++) {
        g.setColor(Color.BLACK);
        g.drawRect(3 + (w*box_width), 3 + (h*box_height), box_width, box_height);
        g.setColor(map[w][h].getColor());
        g.fillRect(4 + (w*box_width), 4 + (h*box_height), box_width-1, box_height-1);
        if(map[w][h].enemySpot && showEnemies && map[w][h].walkable) {
          g.setColor(Color.BLUE);       
          g.fillArc(4 + (w*box_width), 4 + (h*box_height), box_width-1, box_height-1, 0, 360);
        }
      }
    }
  }
  
}
