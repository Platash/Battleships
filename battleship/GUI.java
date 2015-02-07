
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
    protected static final int WINDOW_SIZE_X = 900;
    protected static final int WINDOW_SIZE_Y = 650;
    private static BufferedImage ship;
    private static BufferedImage shipDead;
    private static BufferedImage shot;
    protected JLabel text;
    protected MyPanel panel;
        
    public GUI (){
        super("Battleships");
        setSize(WINDOW_SIZE_X, WINDOW_SIZE_Y);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel = new MyPanel ();
        add(panel);
        setResizable(false);
        setVisible(true);
    }
    
   
    
    class MyPanel extends JPanel{
        
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
                        Battleships.startNewGame();
                        text.setText("Let's start. Your move!");
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
            addMouseListener(new MyMouseListener());
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
            g2.drawImage(field, null, Field.FIELD_1_X-2, Field.FIELD_1_Y-2);
            g2.drawImage(field, null, Field.FIELD_2_X-2,Field. FIELD_2_Y-2);
            
            for (int i=0; i<Field.CELLS_IN_ROW; i++) {
                for (int j=0; j<10; j++){
                    switch (Battleships.getPlayerFieldCell(j, i)) {
                        case Field.SHIP_DEAD:
                            g2.drawImage(shipDead, null, Field.FIELD_1_X + Field.CELL_SIZE * j, 
                                    Field.FIELD_1_Y + Field.CELL_SIZE * i);
                            break;
                        case Field.SHIP: 
                            g2.drawImage(ship, null, Field.FIELD_1_X + Field.CELL_SIZE * j, 
                                    Field.FIELD_1_Y + Field.CELL_SIZE * i);
                            break;
                        case Field.SHOT:
                            g2.drawImage(shot, null, Field.FIELD_1_X + Field.CELL_SIZE * j, 
                                    Field.FIELD_1_Y + Field.CELL_SIZE * i);
                            break;
                   }
                }
            }
            for (int i=0; i<10; i++) {
                for (int j=0; j<10; j++){
                    switch (Battleships.getEnemyFieldCell(j, i)) {
                        case Field.SHIP_DEAD:
                            g2.drawImage(shipDead, null, Field.FIELD_2_X + Field.CELL_SIZE * j, 
                                    Field.FIELD_2_Y + Field.CELL_SIZE * i);
                            break;
//                        case Field.SHIP: 
//                            g2.drawImage(ship, null, Field.FIELD_2_X + Field.CELL_SIZE * j, 
//                                    Field.FIELD_2_Y + Field.CELL_SIZE * i);
//                            break;
                        case Field.SHOT:
                            g2.drawImage(shot, null, Field.FIELD_2_X + Field.CELL_SIZE*j, 
                                    Field.FIELD_2_Y + Field.CELL_SIZE * i);
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
    }
    
    public void setText(String text) {
        this.text.setText(text);
    }
    
    public void updateGraphics() {
        panel.repaint();
    }
    
    public void updateGraphics(int x1, int y1, int x2, int y2) {
        panel.paintImmediately(x1, y1, x2, y2);
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
 