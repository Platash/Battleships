
package battleship;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 *
 * @author AP
 */
public class MyMouseListener implements MouseListener{

    @Override
        public void mouseClicked(MouseEvent e) {
            if (Battleships.game && !Battleships.moveAI) {
                int x = (int)Math.floor((e.getX() - (double)Field.FIELD_2_X) / (double)Field.CELL_SIZE);
                int y = (int)Math.floor((e.getY() - (double)Field.FIELD_2_Y) / (double)Field.CELL_SIZE);
                if (x >= 0 && y >= 0 && x < Field.CELLS_IN_ROW && y < Field.CELLS_IN_ROW ) {
                    Battleships.runMovePlayer(x, y);
                } 
            }
        }

    @Override
    public void mousePressed(MouseEvent me) {
        
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        
    }

    @Override
    public void mouseEntered(MouseEvent me) {
        
    }

    @Override
    public void mouseExited(MouseEvent me) {
        
    }
    
}
