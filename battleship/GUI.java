
package battleship;

import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.io.*;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class GUI extends JFrame{
    public static final int WINDOW_SIZE_X = 900;
    public static final int WINDOW_SIZE_Y = 650;
    public static final int CELL_SIZE = 40;
    public static final int UNIT_SIZE = 38;
    public static final int FIELD_SIZE = 400;
    public static final int FIELD_1_X = 30;
    public static final int FIELD_1_Y = 100;
    public static final int FIELD_2_X = 460;
    public static final int FIELD_2_Y = 100;
    public static final int SHIP_COUNT = 21;
    
    //private Boolean game;
    private BufferedImage ship;
    private BufferedImage shipDead;
    private BufferedImage shot;
    private JLabel text;
    private Battleships battleships;
    private MyPanel panel;
        
    public GUI (){
        super("Battleships");
        setSize(WINDOW_SIZE_X, WINDOW_SIZE_Y);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel = new MyPanel ();
        add(panel);
        setResizable(false);
        setVisible(true);
        battleships = new Battleships();
        battleships.setGame(true);
    }
    
   
    
    class MyPanel extends JPanel implements MouseListener {
        
        public MyPanel() {
            text = new JLabel("Let's start. Your move!");
            text.setFont(new Font("Arial", Font.PLAIN, 28));
            final JButton newGame = new JButton ("New game");
            final JButton about = new JButton ("Rules");
            newGame.setFont(new Font("Arial", Font.PLAIN, 20));
            newGame.setPreferredSize(new Dimension(140, 40));
            newGame.addActionListener(new ActionListener (){
                @Override
                public void actionPerformed(ActionEvent e){
                    if (e.getSource() == newGame) {
                        battleships = new Battleships();
                        text.setText("Let's start. Your move!");
                        battleships.setGame(true);
                        repaint();
                    } 
                }
            });
            about.setFont(new Font("Arial", Font.PLAIN, 20));
            about.setPreferredSize(new Dimension(140, 40));
            about.addActionListener(new ActionListener () {
                @Override
                public void actionPerformed(ActionEvent e){
                    File file = new File ("rules.txt");
                    BufferedReader reader;
                    String textString = "Battleship game rules:";
                    textString += (System.getProperty("line.separator"));
                    int count = 0;
                    try {
                        reader = new BufferedReader(new FileReader(file));
                        String temp;
                        while ((temp = reader.readLine()) != null) {
                            textString+=temp;
                            textString += (System.getProperty("line.separator"));
                            count++;
                        }
                    } 
                    catch (FileNotFoundException ee) {
                        System.out.println("File not found.\n");
                    }
                    catch (IOException ee) {
                    }
                    JTextArea text = new JTextArea(count+5, 50);
                    text.setText(textString);
                    text.setFont(new Font("Arial", Font.PLAIN, 18));
                    text.setBackground(Color.LIGHT_GRAY);
                    text.setEditable(false);
                    final JComponent[] inputs2 = new JComponent[] {new JLabel(), text};
                    JOptionPane.showMessageDialog(null, inputs2, "Battleship rules:", JOptionPane.INFORMATION_MESSAGE);
                }
            });
            setLayout(new FlowLayout(FlowLayout.LEFT));
            setBorder(BorderFactory.createLineBorder(Color.black));
            addMouseListener(this);
            add(Box.createRigidArea(new Dimension(30,80)));
            add (newGame);
            add (about);
            add (Box.createRigidArea(new Dimension(30,80)));
            add (text);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D)g;
            BufferedImage background = openImg ("background.png");
            BufferedImage field = openImg("field.png");
            ship = openImg("ship.png");
            shipDead = openImg("shipDead.png");
            shot = openImg("shot.png");
            g2.drawImage(background, null, 0, 0);
            g2.drawImage(field, null, FIELD_1_X-2, FIELD_1_Y-2);
            g2.drawImage(field, null, FIELD_2_X-2, FIELD_2_Y-2);
            
            for (int i=0; i<Battleships.CELLS_IN_ROW; i++) {
                for (int j=0; j<10; j++){
                    switch (battleships.getMyField()[i][j]) {
                        case Battleships.SHIP_DEAD:
                            g2.drawImage(shipDead, null, FIELD_1_X+CELL_SIZE*j, FIELD_1_Y+CELL_SIZE*i);
                            break;
                        case Battleships.SHIP: 
                            g2.drawImage(ship, null, FIELD_1_X+CELL_SIZE*j, FIELD_1_Y+CELL_SIZE*i);
                            break;
                        case Battleships.SHOT:
                            g2.drawImage(shot, null, FIELD_1_X+CELL_SIZE*j, FIELD_1_Y+CELL_SIZE*i);
                            break;
                        default:
                            break;
                   }
                }
            }
            for (int i=0; i<10; i++) {
                for (int j=0; j<10; j++){
                    switch (battleships.getEnemyField()[i][j]) {
                        case Battleships.SHIP_DEAD:
                            g2.drawImage(shipDead, null, FIELD_2_X+CELL_SIZE*j, FIELD_2_Y+CELL_SIZE*i);
                            break;
                        case Battleships.SHIP: 
                            g2.drawImage(ship, null, FIELD_2_X+CELL_SIZE*j, FIELD_2_Y+CELL_SIZE*i);
                            break;
                        case Battleships.SHOT:
                            g2.drawImage(shot, null, FIELD_2_X+CELL_SIZE*j, FIELD_2_Y+CELL_SIZE*i);
                            break;
                        default:
                            break;
                    }
                }
            }
       }  


        private BufferedImage openImg (String path) {
            BufferedImage img = null;
            try {
                img = ImageIO.read(new File(path));
            } catch (IOException e) {
                System.out.printf("No %s file/n", path);
            }
            return img;
        }
        
        @Override
        public void mouseClicked(MouseEvent e) {
            if (battleships.getGame()) {
                int x = (int)Math.floor((e.getX() - (double)FIELD_2_X) / (double)CELL_SIZE);
                int y = (int)Math.floor((e.getY() - (double)FIELD_2_Y) / (double)CELL_SIZE);
                if (x < 0 || y < 0 || x >= Battleships.CELLS_IN_ROW || y >= Battleships.CELLS_IN_ROW ) {
                    return;
                }
                if (battleships.movePlayer (x, y) == Battleships.SHIP) {
                    text.setText("");
                } else {
                    text.setText("");
                }
                panel.paintImmediately(120, 20, 600,40);
                panel.paintImmediately(FIELD_2_X+CELL_SIZE*x, FIELD_2_Y+CELL_SIZE*y, 40,40);

                while (battleships.getMoveAI()){
                    battleships.chooseMove();
                    x = battleships.getLastMove()[0];
                    y = battleships.getLastMove()[1];
                    panel.paintImmediately(FIELD_1_X+CELL_SIZE*x, FIELD_1_Y+CELL_SIZE*y, 40,40);
                }
                if (battleships.getScoreAI() == Battleships.SHIP_COUNT || 
                        battleships.getScorePlayer() == Battleships.SHIP_COUNT) {
                        battleships.gameOver ();
                }
            }
            panel.repaint();
        }
        
        
     
        @Override
        public void mouseEntered(MouseEvent e) {}

        @Override
        public void mouseExited(MouseEvent e) {}

        @Override
        public void mousePressed(MouseEvent e) {}

        @Override
        public void mouseReleased(MouseEvent e) {}
    
       
    }
    
    public JLabel getJLabel () {
        return this.text;
    }
    
    public MyPanel getPanel () {
        return this.panel;
    }
    
    public void setJLabel (JLabel text) {
        this.text = text;
    }
}
 