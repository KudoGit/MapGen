import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.AbstractButton;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;
import javax.swing.KeyStroke;


public class Main {

  public static void main(String[] args) {  
    final Visualizer v = new Visualizer();
    
    v.rebuildMap();
    final JFrame frame = new JFrame("Beginner Graphical Java Program");
    

    frame.setSize(800, 600);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().add(v, BorderLayout.CENTER);
    
    // make the menubar
    JMenuBar menuBar;
    JMenu mainmenu, enemymenu, mapmenu, blankmenu;
    JMenuItem menuItem;
    JRadioButtonMenuItem rbMenuItem;
    JCheckBoxMenuItem cbMenuItem;
    
    
    menuBar = new JMenuBar();
    
    mainmenu = new JMenu("File");
    mainmenu.setMnemonic(KeyEvent.VK_F);
    mainmenu.getAccessibleContext().setAccessibleDescription("Basic \"File\" options");
    
    menuItem = new JMenuItem("Open");
    menuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.out.println("Open");
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
          v.currentFile = fileChooser.getSelectedFile();
          try {
            v.load();
          } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            JOptionPane.showMessageDialog(frame, "File load failed.", "Error", JOptionPane.ERROR_MESSAGE);
          }
        }
        frame.repaint();
      }
    });
    mainmenu.add(menuItem);
    menuItem = new JMenuItem("Save");
    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
    menuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.out.println("Save");
        if(v.currentFile == null) {
          JFileChooser fileChooser = new JFileChooser();
          if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
            v.currentFile = fileChooser.getSelectedFile();
          }
        }
        try {
          v.save();
        } catch (IOException e1) {
          e1.printStackTrace();
          JOptionPane.showMessageDialog(frame, "Save failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    });
    mainmenu.add(menuItem);
    menuItem = new JMenuItem("Save As");
    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
    menuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.out.println("Save As");
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
          v.currentFile = fileChooser.getSelectedFile();
          // save to file
        }
      }
    });
    mainmenu.add(menuItem);
    menuItem = new JMenuItem("Quit");
    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
    menuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        frame.dispose();
        System.exit(0);
      }
    });
    mainmenu.add(menuItem);
    
    
    enemymenu = new JMenu("Enemies");
    enemymenu.setMnemonic(KeyEvent.VK_E);
    cbMenuItem = new JCheckBoxMenuItem("Show Enemies", v.showEnemies);
    cbMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        AbstractButton abstractButton = (AbstractButton) e.getSource();
        v.showEnemies = abstractButton.getModel().isSelected();
        v.rebuildMap();
        frame.repaint();
      }
    });
    enemymenu.add(cbMenuItem);
    enemymenu.addSeparator();
    menuItem = new JMenuItem("Reroll");
    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
    menuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        v.eseed = v.random.nextLong() * System.nanoTime();
        v.rebuildMap();
        frame.repaint();
      }
    });
    enemymenu.add(menuItem);
    menuItem = new JMenuItem("Custom");
    menuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JTextField enemyDensity = new JTextField(""+v.enemyDensity);
        JTextField enemyLeniency = new JTextField(""+v.enemyLeniency);

        final JComponent[] inputs = new JComponent[] {
            new JLabel("Density"),
            enemyDensity,
            new JLabel("Min. Interest"),
            enemyLeniency,

        };
        JOptionPane.showMessageDialog(null, inputs, "Enemy Options", JOptionPane.PLAIN_MESSAGE);
        double box1 = 0.0;
        double box2 = 0.0;
        try {
          box1 = Double.parseDouble(enemyDensity.getText());
        } catch(NumberFormatException f) {
          box1 = 0.0;
        }
        try {
          box2 = Double.parseDouble(enemyLeniency.getText());
        } catch(NumberFormatException f) {
          box2 = 0.0;
        }
        
        if(box1 >= 0) v.enemyDensity = box1;
        if(box2 >= 0) v.enemyLeniency = box2;
      }
    });
    enemymenu.add(menuItem);
    
    
    mapmenu = new JMenu("Map Options");
    mapmenu.setMnemonic(KeyEvent.VK_M);
    mapmenu.getAccessibleContext().setAccessibleDescription("Map stuff");
    menuItem = new JMenuItem("Set island limits");
    menuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JTextField minSize = new JTextField(""+v.minSize);
        JTextField maxSize = new JTextField(""+v.maxSize);
        JTextField minIslands = new JTextField(""+v.minIslands);
        JTextField maxIslands = new JTextField(""+v.maxIslands);
        final JComponent[] inputs = new JComponent[] {
            new JLabel("Minimum island size"),
            minSize,
            new JLabel("Maximum island size"),
            maxSize,
            new JLabel("Minimum number of islands"),
            minIslands,
            new JLabel("Maximum number of islands"),
            maxIslands
        };
        JOptionPane.showMessageDialog(null, inputs, "Map Options", JOptionPane.PLAIN_MESSAGE);
        int box1 = 0;
        int box2 = 0;
        int box3 = 0;
        int box4 = 0;
        try {
          box1 = Integer.parseInt(minSize.getText());
        } catch(NumberFormatException f) {
          box1 = 0;
        }
        try {
          box2 = Integer.parseInt(maxSize.getText());
        } catch(NumberFormatException f) {
          box2 = 0;
        }
        try {
          box3 = Integer.parseInt(minIslands.getText());
        } catch(NumberFormatException f) {
          box3 = 0;
        }
        try {
          box4 = Integer.parseInt(maxIslands.getText());
        } catch(NumberFormatException f) {
          box4 = 0;
        }
        
        if(box1 > 0) v.minSize = box1;
        if(box2 > 0) v.maxSize = box2;
        if(box3 > 0) v.minIslands = box3;
        if(box4 > 0) v.maxIslands = box4;
      }
    });
    mapmenu.add(menuItem);
    menuItem = new JMenuItem("Tile preferences");
    menuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JTextField unwalkableChance = new JTextField(""+v.unwalkableChance*100.0);
        JTextField unshootableChance = new JTextField(""+v.unshootableChance*100.0);
        JTextField specialChance = new JTextField(""+v.specialChance*100.0);
        final JComponent[] inputs = new JComponent[] {
            new JLabel("Percentage of unwalkable tiles (0-100%)"),
            unwalkableChance,
            new JLabel("Percentage of unshootable tiles (0-100%)"),
            unshootableChance,
            new JLabel("Percentage of special tiles (0-100%)"),
            specialChance,

        };
        JOptionPane.showMessageDialog(null, inputs, "Tile Preferences", JOptionPane.PLAIN_MESSAGE);
        Double box1 = 0.0;
        Double box2 = 0.0;
        Double box3 = 0.0;
        try {
          box1 = Double.parseDouble(unwalkableChance.getText());
        } catch(NumberFormatException f) {
          box1 = 0.0;
        }
        try {
          box2 = Double.parseDouble(unshootableChance.getText());
        } catch(NumberFormatException f) {
          box2 = 0.0;
        }
        try {
          box3 = Double.parseDouble(specialChance.getText());
        } catch(NumberFormatException f) {
          box3 = 0.0;
        }

        
        if(box1 <= 100 && box1 >= 0) v.unwalkableChance = box1 / 100.0;
        if(box2 <= 100 && box2 >= 0) v.unshootableChance = box2 / 100.0;
        if(box3 <= 100 && box3 >= 0) v.specialChance = box3 / 100.0;
      }
    });
    mapmenu.add(menuItem);

    
    blankmenu = new JMenu("(blank)");
    menuItem = new JMenuItem("blank item");
    blankmenu.add(menuItem);
    blankmenu.addSeparator();
    rbMenuItem = new JRadioButtonMenuItem("OP1");
    blankmenu.add(rbMenuItem);
    rbMenuItem = new JRadioButtonMenuItem("OP2");
    blankmenu.add(rbMenuItem);
    blankmenu.addSeparator();
    cbMenuItem = new JCheckBoxMenuItem("Check 1");
    blankmenu.add(cbMenuItem);
    cbMenuItem = new JCheckBoxMenuItem("Check 2");
    blankmenu.add(cbMenuItem);
    
    
    menuBar.add(mainmenu);
    menuBar.add(enemymenu);
    menuBar.add(mapmenu);
    menuBar.add(blankmenu);
    frame.setJMenuBar(menuBar);
    
    
    
    
    // make the GUI
    JPanel buttonPanel = new JPanel();
    
    /*
    JCheckBox fixIslandsBox = new JCheckBox("Remove trivial islands");
    fixIslandsBox.setSelected(v.fixIslands);
    fixIslandsBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        v.fixIslands = !v.fixIslands;
        v.rebuildMap();
        v.repaint();
      }
    });
    
    JCheckBox checkIslandsBox = new JCheckBox("Limit to 1 mainland");
    checkIslandsBox.setSelected(v.checkIslands);
    checkIslandsBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        v.checkIslands = !v.checkIslands;
        v.rebuildMap();
        frame.repaint();
      }
    });
    
    
    String[] islandOptions = new String[] {"None", "Remove trivial islands", "Limit to 1 mainland"};
    final JComboBox<String> islandOptionBox = new JComboBox<>(islandOptions);
    islandOptionBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String selection = (String) islandOptionBox.getSelectedItem();
        if(selection.equals("None")) {
          v.fixIslands = false;
          v.checkIslands = false;
        } else if (selection.equals("Remove trivial islands")) {
          v.fixIslands = true;
          v.checkIslands = false;
        } else if (selection.equals("Limit to 1 mainland")) {
          v.fixIslands = true;
          v.checkIslands = true;
        }
        v.rebuildMap();
        frame.repaint();
      }
    });
    
    
    JCheckBox showEnemiesBox = new JCheckBox("Show enemies");
    showEnemiesBox.setSelected(v.showEnemies);
    showEnemiesBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        v.showEnemies = !v.showEnemies;
        v.rebuildMap();
        frame.repaint();
      }
    });
    
    
    final JTextField densityField = new JTextField(""+v.enemyDensity);
    final JTextField leniencyField = new JTextField(""+v.enemyLeniency);
    densityField.setColumns(3);
    leniencyField.setColumns(3);
    
    JButton randomizeEnemies = new JButton("Reroll Enemies");
    randomizeEnemies.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        v.eseed = v.random.nextLong() * System.nanoTime();
        v.enemyDensity = Double.parseDouble(densityField.getText());
        v.enemyLeniency = Double.parseDouble(leniencyField.getText());
        v.rebuildMap();
        frame.repaint();
      }
    });
    */
    
    final JTextField widthField = new JTextField(""+v.unit_width);
    final JTextField heightField = new JTextField(""+v.unit_height);
    widthField.setColumns(3);
    heightField.setColumns(3);
    
    JButton randomizeSeedButton = new JButton("Random Seed");
    randomizeSeedButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        v.seed = v.random.nextLong() * System.nanoTime();
        v.unit_width = Integer.parseInt(widthField.getText());
        v.unit_height = Integer.parseInt(heightField.getText());
        v.rebuildMap();
        frame.repaint();
      }
    });
    

    /*
    buttonPanel.add(fixIslandsBox);
    buttonPanel.add(checkIslandsBox);
    buttonPanel.add(new JLabel("Island options"));
    buttonPanel.add(islandOptionBox);
    buttonPanel.add(showEnemiesBox);
    buttonPanel.add(new JLabel("Enemy density:"));
    buttonPanel.add(densityField);
    buttonPanel.add(new JLabel("Min. interest:"));
    buttonPanel.add(leniencyField);
    buttonPanel.add(randomizeEnemies);
    */
    buttonPanel.add(new JLabel("x:"));
    buttonPanel.add(widthField);
    buttonPanel.add(new JLabel("y:"));
    buttonPanel.add(heightField);
    buttonPanel.add(randomizeSeedButton);
    buttonPanel.setPreferredSize(buttonPanel.getPreferredSize());
    frame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    
    //System.out.println(fixIslandsBox.isSelected());
    
    frame.setVisible(true);
  }

}
