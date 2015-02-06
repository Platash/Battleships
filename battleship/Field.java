package battleship;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author AP
 */
public class Field {
    
    protected static final int EMPTY = 0;
    protected static final int SHIP = -1;
    protected static final int SHIP_DEAD = -2;
    protected static final int DMZ = -3;
    protected static final int SHOT = -4;
    
    protected static final int CELLS_IN_ROW = 10;
    protected static final int CELL_SIZE = 40;
    protected static final int UNIT_SIZE = 38;
    protected static final int FIELD_SIZE = 400;
    protected static final int FIELD_1_X = 30;
    protected static final int FIELD_1_Y = 100;
    protected static final int FIELD_2_X = 460;
    protected static final int FIELD_2_Y = 100;
    protected static final int SHIPCELL_COUNT = 21;
    protected int playerScore;
    private int[][] cells;
    private int[] ships = {5, 4, 3, 3, 2, 2, 2};
    
    
    public Field(boolean inPlay) {
        cells = new int[10][10];
        playerScore = 0;
        if(inPlay) {
            for(int i = 0; i < ships.length; i++) {
               placeShipAuto(ships[i]); 
            }
            removeDMZ(); 
        }
    }
    
    public void setProbabilities() {
        //resetting probabilities from the previous turn
        for(int i = 0; i < CELLS_IN_ROW; i++) {
            for(int j = 0; j < CELLS_IN_ROW; j++) {
                if(cells[i][j] > SHIP_DEAD) {
                    cells[i][j] = 0;
                }
            }
        }
        //setiing new probabilities
        for(int i = 0; i < CELLS_IN_ROW; i++) {
            for(int j = 0; j < CELLS_IN_ROW; j++) {
                if(cells[i][j] < SHIP) {
                        continue;
                }
                for(int k = 0; k < ships.length; k++) {
                    updateProbabilityHR(ships[k], j, i);
                    updateProbabilityVD(ships[k], j, i);
                    updateProbabilityHL(ships[k], j, i);
                    updateProbabilityVU(ships[k], j, i);
                }
            }
        }
    }
    
    public Point getRandomHighestProbabilityCell() {
        Random ran = new Random();
        int highestValue = SHIP_DEAD;
        ArrayList<Point> highProbCells = new ArrayList<>();
        for(int i = 0; i < CELLS_IN_ROW; i++) {
            for(int j = 0; j < CELLS_IN_ROW; j++) {
                if(highestValue < cells[i][j]) {
                    highestValue = (cells[i][j]);
                }
            }
        }
        if(highestValue <= 0) {
            return null;
        }
        for(int i = 0; i < CELLS_IN_ROW; i++) {
            for(int j = 0; j < CELLS_IN_ROW; j++) {
                if(highestValue == cells[i][j]) {
                    highProbCells.add(new Point(j, i));
                }
            }
        }
        return highProbCells.get(ran.nextInt(highProbCells.size()));
    }
    
    public int getProbability(int x, int y) {
        return cells[y][x];
    }
    
    public void placeShipAuto (int placeShipSize) {
        Random random = new Random();
        if (random.nextBoolean()) {
            placeShipAutoH (placeShipSize);
        } else {
            placeShipAutoV (placeShipSize);
        }
    }
    
    //Randomly places given horizontal ship.
    private void placeShipAutoH (int placeShipSize) {
        Random random = new Random();
        int x;
        int y;
       
        x = random.nextInt(CELLS_IN_ROW-(placeShipSize - 1));
        y = random.nextInt(CELLS_IN_ROW);
        while (!checkSpaceForPlacingH (placeShipSize, x, y)) {
            x = random.nextInt(CELLS_IN_ROW-(placeShipSize - 1));
            y = random.nextInt(CELLS_IN_ROW);
        }
        
        for (int i=0; i<placeShipSize; i++) {
            cells[y][x+i] = SHIP;
        }
        markDMZH (placeShipSize, x, y);
    }
    
    //Randomly places given vertical ship
    private void placeShipAutoV (int placeShipSize) {
        Random random = new Random();
        int x;
        int y;
       
        x = random.nextInt(CELLS_IN_ROW);
        y = random.nextInt(CELLS_IN_ROW-(placeShipSize-1));
        while (!checkSpaceForPlacingV (placeShipSize, x, y)) {
            x = random.nextInt(CELLS_IN_ROW);
            y = random.nextInt(CELLS_IN_ROW-(placeShipSize-1));
        }
        for (int i=0; i<placeShipSize; i++) {
            cells[y+i][x] = SHIP;
        }
        markDMZV (placeShipSize, x, y);
    }
    
    //Removing DMZ created around the ships while placing them.
    private void removeDMZ () {
        for (int i=0; i<CELLS_IN_ROW; i++) {
            for (int j=0; j<CELLS_IN_ROW; j++){
                if (cells[i][j] == DMZ) {
                    cells[i][j] = EMPTY;
                }
            }
        }
    }
    
    //Finding X and Y coordinates of the first section of the ship 
    public void createDMZ (int shipSize, int firstHitX, int firstHitY, boolean horyzontal) {
        int firstX = findFirstX(firstHitX, firstHitY, horyzontal);
        int firstY = findFirstY(firstHitX, firstHitY, horyzontal);
        if (horyzontal) {
            markDMZH (shipSize, firstX, firstY);
        } else {
            markDMZV (shipSize, firstX, firstY);
        }
    }
    
    private int findFirstX(int firstHitX, int firstHitY, boolean horyzontal) {
        int x = firstHitX;
        int y = firstHitY;
        int firstX = 0;
        if (horyzontal) {
            for (int i = 0; i < 6; i++) {
                if ((x - i) < 0) {
                    firstX = 0;
                    break;
                } else if (cells[y][x-i] != SHIP_DEAD){
                    firstX = x-i+1;
                    break;
                }               
            }
        } else {
            for (int i = 0; i < 6; i++) {
                if ((y - i) < 0) {
                    firstX = x;
                    break;
                } else if (cells[y-i][x] != SHIP_DEAD){
                    firstX = x;
                    break;
                }               
            }
        }
        return firstX;
     }
    
     private int findFirstY(int firstHitX, int firstHitY, boolean horyzontal) {
        int x = firstHitX;
        int y = firstHitY;
        int firstY = 0;
        if (horyzontal) {
            for (int i = 0; i < 6; i++) {
                if ((x - i) < 0) {
                    firstY = y;
                    break;
                } else if (cells[y][x-i] != SHIP_DEAD){
                    firstY = y;
                    break;
                }               
            }
        } else {
            for (int i = 0; i < 6; i++) {
                if ((y - i) < 0) {
                    firstY = 0;
                    break;
                } else if (cells[y-i][x] != SHIP_DEAD){
                    firstY = y-i+1;
                    break;
                }               
            }
        }
        return firstY;
    }
    
    //Marking Demilitarised Zone around the horyzontal ship. 
    //To let computer know where not to shoot (because it makes no sence).
    private void markDMZH (int shipSize, int firstX, int firstY) {
        
        int dmzSize = shipSize + 2;
        int dmzY = firstY - 1;
        if (dmzY<0) { dmzY++; }
        int dmzX = firstX - 1;
        if (dmzX<0) { dmzX++; dmzSize --;}
        
       for(int i=0; i<3; i++){
            if ((dmzY+i) >= CELLS_IN_ROW) {
                break;
            }
            for (int j=0; j<dmzSize; j++) {
                if ((dmzX+j) >= CELLS_IN_ROW) {
                   continue;
                } 
                if (cells[dmzY+i][dmzX+j] != SHIP && cells[dmzY+i][dmzX+j] != SHIP_DEAD && cells[dmzY+i][dmzX+j] != SHOT) {
                    cells[dmzY+i][dmzX+j] = DMZ;
                }
            }
            if ( dmzY+i == 1 && firstY == 0) {
                i++;
            }
        }
    }
        
    //Marking Demilitarised Zone around the vertical ship. 
    //To let computer know where not to shoot (because it makes no sence).
    private void markDMZV (int shipSize, int firstX, int firstY) {
        int dmzSize = shipSize +2;
        int dmzY = firstY - 1;
        if (dmzY<0) { dmzY++; dmzSize--;}
        int dmzX = firstX - 1;
        if (dmzX<0) { dmzX++; }
        for(int i=0; i<3; i++){
            if ((dmzX+i) >= CELLS_IN_ROW) {
                break;
            }
            for (int j=0; j<dmzSize; j++) {
                if ((dmzY+j) >= CELLS_IN_ROW) {
                   continue;
                } 
                if (cells[dmzY+j][dmzX+i] != SHIP && cells[dmzY+j][dmzX+i] != SHIP_DEAD && cells[dmzY+j][dmzX+i] != SHOT) {
                    cells[dmzY+j][dmzX+i] = DMZ;
                }
            }
            if ( dmzX+i == 1 && firstX == 0) {
                i++;
            }
        }
    }
    
    //Checkin if there is enough space to place the horyzontal ship starting from the given point.
    private boolean checkSpaceForPlacingH (int shipSize, int x, int y) {
        for (int i = 0; i < shipSize; i++) {
            if(x + i >= CELLS_IN_ROW) {
                return false;
            }
            if (cells[y][x+i] != EMPTY) {
                return false;
            }
        } 
        return true;
    } 
    
    //Checkin if there is enough space to place the vertical ship starting from the given point.
    private boolean checkSpaceForPlacingV (int shipSize, int x, int y) {
        for (int i = 0; i < shipSize; i++) {
            if(y + i >= CELLS_IN_ROW) {
                return false;
            }
            if (cells[y+i][x] != EMPTY) {
                return false;
            }
        }
        return true;
    }
    
   /**
    * Checks if there is enough space to place a horyzontal ship 
    * starting from the given point, rightwards.
    */     
    private void updateProbabilityHR (int shipSize, int x, int y) {
        for (int i = 0; i < shipSize; i++) {
            if(x + i >= CELLS_IN_ROW) {
                return;
            }
            if (cells[y][x + i] < SHIP) {
                return;
            }
        }
        for (int i = 0; i < shipSize; i++) {
            cells[y][x + i]++;
        }
    } 
    
    /**
    * Checks if there is enough space to place a horyzontal ship 
    * starting from the given point, leftwards.
    */   
    private void updateProbabilityHL (int shipSize, int x, int y) {
        for (int i = 0; i < shipSize; i++) {
            if(x - i < 0) {
                return;
            }
            if (cells[y][x - i] < SHIP) {
                return;
            }
        } 
        for (int i = 0; i < shipSize; i++) {
            cells[y][x - i]++;
        } 
    } 
    
    /**
    * Checks if there is enough space to place a vertical ship 
    * starting from the given point, downwards.
    */  
    private void updateProbabilityVD (int shipSize, int x, int y) {
        for (int i = 0; i < shipSize; i++) {
            if(y + i >= CELLS_IN_ROW) {
                return;
            }
            if (cells[y + i][x] < SHIP) {
                return;
            }
        } 
        for (int i = 0; i < shipSize; i++) {
            cells[y + i][x]++;
        } 
    }
    
    /**
    * Checks if there is enough space to place a vertical ship 
    * starting from the given point, upwards.
    */  
    
    private void updateProbabilityVU (int shipSize, int x, int y) {
        for (int i = 0; i < shipSize; i++) {
            if(y - i < 0) {
                return;
            }
            if (cells[y - i][x] < SHIP) {
                return;
            }
        } 
        for (int i = 0; i < shipSize; i++) {
            cells[y - i][x]++;
        } 
    }
    
    public void removeShip(int size) {
        for(int i = 0; i < ships.length; i++) {
            if(size == ships[i]) {
                ships[i] = 0;
                //Arrays.sort(ships);
                return;
            }
        }
    }
    
    public int getBiggestShip() {
        Arrays.sort(ships);
        return ships[ships.length - 1];
    }
    
    public boolean allShipsKilled() {
        Arrays.sort(ships);
        return ships[ships.length - 1] == 0;
    }
    
    public int getCell(int x, int y) {
        return this.cells[y][x];
    }
    
    public void setCell(int x, int y, int value) {
        this.cells[y][x] = value;
    }
    
    @Override
    public String toString() {
        for(int i = 0; i < 10; i++) {
            for(int j = 0; j < 10; j++) {
                System.out.print(cells[i][j] + " ");
            }
            System.out.println("");
        }
        return null;
    }
    
}
