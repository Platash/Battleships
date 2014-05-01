package battleship;

import java.util.*;

public class Battleships {
    
    
    public static final int EMPTY = 0;
    public static final int SHIP = 1;
    public static final int SHIP_DEAD = 2;
    public static final int DMZ = 3;
    public static final int SHOT = 4;
    public static final int CELLS_IN_ROW = 10;
    public static final int SHIP_COUNT = 21;
    
    private boolean game;
    private boolean moveAI = false;
    private int [][] myField = new int [10][10];
    private int [][] enemyField = new int [10][10];
    private int [][] enemyFieldShow = new int [10][10];
    
    private boolean horyzontal;
    private boolean right;
    private boolean down;
    private int scorePlayer;
    private int scoreAI;
    private int hits;
    private int misses;
    private int shipSize;
    
    private int [] firstHit = new int [2];
    private int [] nextMove = new int [2];
    private int [] lastMove = new int [2];
    
    private static GUI window;
    
    //Battleship class constructor. Creates and randomly places 7 ships on each board - player's and PC's
    public Battleships () {
        placeShipAuto (5, myField);
        placeShipAuto (4, myField);
        placeShipAuto (3, myField);
        placeShipAuto (3, myField);
        placeShipAuto (2, myField);
        placeShipAuto (2, myField);
        placeShipAuto (2, myField);

        placeShipAuto (5, enemyField);
        placeShipAuto (4, enemyField);
        placeShipAuto (3, enemyField);
        placeShipAuto (3, enemyField);
        placeShipAuto (2, enemyField);
        placeShipAuto (2, enemyField);
        placeShipAuto (2, enemyField);
        
        removeDMZ (); 
    }
    
    
    
    public static void main(String[] args) {
        
        window = new GUI ();
    }
    
    
    //Players move analysis
    public int movePlayer (int x, int y) {
         int result = SHOT;
         switch (enemyField[y][x]) {
                    case SHIP :
                        result = SHIP;
                        enemyFieldShow[y][x] = SHIP_DEAD;
                        enemyField[y][x] = SHIP_DEAD;
                        scorePlayer +=1;
                        if (scorePlayer == SHIP_COUNT) {
                            gameOver();
                        }
                        break;
                    case EMPTY :
                        enemyFieldShow[y][x] = SHOT;
                        enemyField[y][x] = SHOT;
                        result = EMPTY;
                        moveAI = true;
                        break;
                    default:
                        break;
                }
            return result;
    }
    
    //Choosing Move algorithm depending on having enemy ship hit already or not
    public void chooseMove () {
        if (hits == 0) {
            aiMove();
        } else if (hits == 1) {
            aiMove2 ();
        } else {
            aiMove3 ();
        }
        
    }
    
    //First (random) AI move
    public void aiMove () {

            Random random = new Random();
            int x;
            int y;
            hits = 0;
            misses = 0;
            x = random.nextInt(10);
            y = random.nextInt(10);
            switch (myField [y][x]) {
                case SHIP:
                    firstHit[0] = x;
                    firstHit[1] = y;
                    lastMove[0] = x;
                    lastMove[1] = y;
                    myField[y][x] = SHIP_DEAD;
                    scoreAI +=1;
                    hits +=1;
                    if (random.nextBoolean()) {
                        switch (x) {
                            case 0: 
                                misses += 1;
                                x +=1;
                                right = true;
                                break;
                            case 9:
                                misses += 1;
                                x -=1;
                                right = false;
                                break;
                            default: 
                                x +=1;
                                right = true;
                                break;
                        }
                        horyzontal = true;
                    } else {
                        switch (y) {
                            case 0: 
                                misses += 1;
                                y +=1;
                                down = true;
                                break;
                            case 9:
                                misses += 1;
                                y -=1;
                                down = false;
                                break;
                            default: 
                                y +=1;
                                down = true;
                                break;
                        }
                        horyzontal = false;
                    }
                    nextMove[0] = x;
                    nextMove[1] = y;
                    break;
                case EMPTY:
                    myField[y][x] = SHOT;
                    moveAI = false;
                    break;
                default:
                    break;
            }
            window.getPanel().paintImmediately(GUI.FIELD_2_X+GUI.CELL_SIZE*x, GUI.FIELD_2_Y+GUI.CELL_SIZE*y, 40,40);
       }
        
       
    public void aiMove2 () {
            try {
                Thread.sleep(200);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            int x = nextMove [0];
            int y = nextMove [1];
            lastMove[0] = x;
            lastMove[1] = y;
            
            switch (myField [y][x]) {
                case SHIP:
                    myField[y][x] = SHIP_DEAD;
                    hits += 1;
                    scoreAI += 1;
                    misses = 0;
                    if (scoreAI == SHIP_COUNT) {
                        gameOver ();
                        break;
                    }
                    
                    if (horyzontal) {
                        if (right){
                            x += 1;
                            if (x > 9) {
                                y = firstHit[1];
                                x = firstHit[0]-1;
                                right = false;
                                misses +=1;
                                
                            } 
                        } else {
                            x -= 1; 
                            if (x < 0) {
                                shipSize = hits;
                                hits = 0;
                                createDMZ();
                                break;
                            }
                        }
                    } else {
                        if (down){
                            y +=1;
                            if (y > 9) {
                                y = firstHit[1] - 1;
                                x = firstHit[0];
                                down = false;
                                misses +=1;
                            } 
                        } else {
                            y -=1;
                            if (y < 0) {
                                shipSize = hits;
                                createDMZ();
                                hits = 0;
                                break;
                            }
                        }
                    }
                    nextMove[0] = x;
                    nextMove[1] = y;
                    break;
                //End of case SHIP
                    
                case EMPTY :
                    myField[y][x] = SHOT;
                    moveAI = false;
                    misses +=1;
                    if (horyzontal && misses == 1) {
                        if (right) {
                            x = firstHit[0]-1;
                            right = false;
                        } else {
                                x = firstHit[0]+1;
                                right = true;
                        }
                    } else if (!horyzontal && misses == 1){
                        if (down) {
                                y = firstHit[1]-1;
                                down = false;
                        } else {
                                y = firstHit[1]+1;
                                down = true;
                         }
                    } else if (horyzontal && misses == 2) {
                        horyzontal = false;
                        x = firstHit[0];
                        if (firstHit[1] == 9){
                            down = false;
                            y = firstHit[1] - 1;
                        } else {
                            down = true;
                            y = firstHit[1] + 1;
                        }
                    } else if (!horyzontal && misses == 2){
                        horyzontal = true;
                        y = firstHit[1];
                        if (firstHit[0] == 9){
                            right = false;
                            x = firstHit[0] - 1;
                        } else {
                            right = true;
                            x = firstHit[0] + 1;
                        }
                    } else if (horyzontal && misses > 2){
                        right = false;
                        x = firstHit[0]-1;
                    } else {
                        down = false;
                        y = firstHit[1] - 1;
                    }
                    
                    nextMove[0] = x;
                    nextMove[1] = y;
                    break;
                //End of case EMPTY
                    
                default :
                     misses +=1;
                     if (horyzontal && misses == 1) {
                        if (right) {
                            x = firstHit[0]-1;
                            right = false;
                        } else {
                                x = firstHit[0]+1;
                                right = true;
                        }
                    } else if (!horyzontal && misses == 1){
                        if (down) {
                                y = firstHit[1]-1;
                                down = false;
                        } else {
                                y = firstHit[1]+1;
                                down = true;
                         }
                    } else if (horyzontal && misses == 2) {
                        horyzontal = false;
                        x = firstHit[0];
                        if (firstHit[1] == 9){
                            down = false;
                            y = firstHit[1] - 1;
                        } else {
                            down = true;
                            y = firstHit[1] + 1;
                        }
                    } else if (!horyzontal && misses == 2){
                        horyzontal = true;
                        y = firstHit[1];
                        if (firstHit[0] == 9){
                            right = false;
                            x = firstHit[0] - 1;
                        } else {
                            right = true;
                            x = firstHit[0] + 1;
                        }
                    } else if (horyzontal && misses > 2){
                        right = false;
                        x = firstHit[0]-1;
                    } else {
                        down = false;
                        y = firstHit[1] - 1;
                    }
                    nextMove[0] = x;
                    nextMove[1] = y;
                    break;
                //End of default
            }
            window.getPanel().paintImmediately(GUI.FIELD_2_X+GUI.CELL_SIZE*lastMove[0], 
                                                GUI.FIELD_2_Y+GUI.CELL_SIZE*lastMove[1], 40,40);
       }
        
        
    public void aiMove3 () {
            try {
                Thread.sleep(200);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            
            int x = nextMove [0];
            int y = nextMove [1];
            lastMove[0] = x;
            lastMove[1] = y;
            
            switch (myField [y][x]) {
                case SHIP: 
                    myField[y][x] = SHIP_DEAD;
                    hits += 1;
                    scoreAI += 1;
                    
                    if (hits == 5) {
                        shipSize = hits;
                        createDMZ ();
                        hits = 0;
                        break;
                    }
                    if (horyzontal) {
                        if (right) {
                            x += 1;
                            if (x == CELLS_IN_ROW) {
                                x = firstHit[0] - 1;
                                right = false;
                            }
                        } else {
                            x -= 1;
                            if (x < 0) {
                                x = firstHit[0] + 1;
                                right = true;
                            }
                        }
                    } else {
                        if (down) {
                            y += 1;
                            if (y == CELLS_IN_ROW) {
                                y = firstHit[1] - 1;
                                down = false;
                            }
                        } else {
                            y -= 1;
                            if (y < 0) {
                                y = firstHit[1] + 1;
                                down = true;
                            }
                        }
                    }
                    nextMove[0] = x;
                    nextMove[1] = y;
                    break;
                //End of case SHIP  
                
                case SHIP_DEAD:
                    shipSize = hits;
                    hits = 0;
                    createDMZ ();
                    break;
                //End of case DEAD
                    
                case EMPTY:
                    moveAI = false;
                    myField[y][x] = SHOT;
                    if (horyzontal) {
                        x = firstHit[0] - 1;
                        if (x < 0) {
                            shipSize = hits;
                            hits = 0;
                            createDMZ ();
                            break;
                        }
                        switch (myField[y][x]){
                            case DMZ :
                                shipSize = hits;
                                hits = 0;
                                createDMZ ();
                                break;
                            case EMPTY :
                                nextMove[0] = x;
                                right = false;
                                break;
                            default :
                                nextMove[0] = x;
                                right = false;
                                break;
                        }
                    } else {
                        y = firstHit[1] - 1;
                        if (y < 0) {
                            shipSize = hits;
                            hits = 0;
                            createDMZ ();
                            break;
                        }
                        switch (myField[y][x]){
                           case DMZ :
                                shipSize = hits;
                                hits = 0;
                                createDMZ ();
                                break;
                           case EMPTY :
                                nextMove[1] = y;
                                right = false;
                                break;
                            default :
                                nextMove[1] = y;
                                down = false;
                                break;
                        }
                    }
                    break;
                //End of case EMPTY
                    
                default:
                    if (horyzontal) {
                        x = firstHit[0] - 1;
                        if (x < 0) {
                            shipSize = hits;
                            hits = 0;
                            createDMZ ();
                            break;
                        }
                        switch (myField[y][x]){
                            case SHIP :
                                nextMove[0] = x;
                                right = false;
                                break;
                            case EMPTY :
                                nextMove[0] = x;
                                right = false;
                                break;
                            default :
                                shipSize = hits;
                                hits = 0;
                                createDMZ ();
                                break;
                        }
                    } else {
                        y = firstHit[1] - 1;
                        if (y < 0) {
                            shipSize = hits;
                            hits = 0;
                            createDMZ ();
                            break;
                        }
                        switch (myField[y][x]){
                            case SHIP :
                                nextMove[1] = y;
                                down = false;
                                break;
                            case EMPTY :
                                nextMove[1] = y;
                                right = false;
                                break;
                            default :
                                shipSize = hits;
                                hits = 0;
                                createDMZ ();
                                break;
                        }
                    }
                    break;
                //End of default
            }
        }
    
    
    //Randomly deciding is the ship is going to be verticl or horizontal
    public void placeShipAuto (int placeShipSize, int [][] field) {
        Random random = new Random();
        if (random.nextBoolean()) {
            placeShipAutoH (placeShipSize, field);
        } else {
            placeShipAutoV (placeShipSize, field);
        }
    }
    
    
     //Randomly places given horizontal ship.
    private void placeShipAutoH (int placeShipSize, int [][] field) {
        Random random = new Random();
        int x;
        int y;
       
        x = random.nextInt(CELLS_IN_ROW-(placeShipSize-1));
        y = random.nextInt(CELLS_IN_ROW);
        while (!checkSpaceH (placeShipSize, field, x, y)) {
            x = random.nextInt(CELLS_IN_ROW-(placeShipSize-1));
            y = random.nextInt(CELLS_IN_ROW);
        }
        
        for (int i=0; i<placeShipSize; i++) {
            field[y][x+i] = SHIP;
        }
        markDMZH (placeShipSize, field, x, y);
    }
    
    
    //Randomly places given vertical ship
    private void placeShipAutoV (int placeShipSize, int [][] field) {
        Random random = new Random();
        int x;
        int y;
       
        x = random.nextInt(CELLS_IN_ROW);
        y = random.nextInt(CELLS_IN_ROW-(placeShipSize-1));
        while (!checkSpaceV (placeShipSize, field, x, y)) {
            x = random.nextInt(CELLS_IN_ROW);
            y = random.nextInt(CELLS_IN_ROW-(placeShipSize-1));
        }
        for (int i=0; i<placeShipSize; i++) {
            field[y+i][x] = SHIP;
        }
        markDMZV (placeShipSize, field, x, y);
    }
    
    
    //Removing DMZ created around the ships while placing them.
    private void removeDMZ () {
        for (int i=0; i<CELLS_IN_ROW; i++) {
            for (int j=0; j<CELLS_IN_ROW; j++){
                if (myField[i][j] == DMZ) {
                    myField[i][j] = EMPTY;
                }
                if (enemyField[i][j] == DMZ) {
                    enemyField[i][j] = EMPTY;
                }
            }
        }
    }
    
    //Finding X and Y coordinates of the first section of the ship 
    private void createDMZ () {
            int x = firstHit[0];
            int y = firstHit[1];
            int firstX = 0;
            int firstY = 0;
            if (horyzontal) {
                for (int i = 0; i < 6; i++) {
                    if ((x - i) < 0) {
                        firstX = 0;
                        firstY = y;
                        break;
                    } else if (myField[y][x-i] != SHIP_DEAD){
                        firstX = x-i+1;
                        firstY = y;
                        break;
                    }               
                }
            } else {
                for (int i = 0; i < 6; i++) {
                    if ((y - i) < 0) {
                        firstX = x;
                        firstY = 0;
                        break;
                    } else if (myField[y-i][x] != SHIP_DEAD){
                        firstX = x;
                        firstY = y-i+1;
                        break;
                        
                    }               
                }
            }
            if (horyzontal) {
                markDMZH (shipSize, myField, firstX, firstY);
            } else {
                markDMZV (shipSize, myField, firstX, firstY);
            }
        }
    
    //Marking Demilitarised Zone around the horyzontal ship. 
    //To let computer know where not to shoot (because it makes no sence).
    private void markDMZH ( int shipSize, int [][] field, int x, int y) {
        
        int dmzSize = shipSize + 2;
        int dmzY = y - 1;
        if (dmzY<0) { dmzY++; }
        int dmzX = x - 1;
        if (dmzX<0) { dmzX++; dmzSize --;}
        
       for(int i=0; i<3; i++){
            if ((dmzY+i) >= CELLS_IN_ROW) {
                break;
            }
            for (int j=0; j<dmzSize; j++) {
                if ((dmzX+j) >= CELLS_IN_ROW) {
                   continue;
                } 
                if (field[dmzY+i][dmzX+j] != SHIP && field[dmzY+i][dmzX+j] != SHIP_DEAD && field[dmzY+i][dmzX+j] != SHOT) {
                    field[dmzY+i][dmzX+j] = DMZ;
                }
            }
            if ( dmzY+i == 1 && y == 0) {
                i++;
            }
        }
    }
        
        
    //Marking Demilitarised Zone around the vertical ship. 
    //To let computer know where not to shoot (because it makes no sence).
    private void markDMZV (int shipSize, int [][] field, int x, int y) {
        int dmzSize = shipSize +2;
        int dmzY = y - 1;
        if (dmzY<0) { dmzY++; dmzSize--;}
        int dmzX = x - 1;
        if (dmzX<0) { dmzX++; }
        for(int i=0; i<3; i++){
            if ((dmzX+i) >= CELLS_IN_ROW) {
                break;
            }
            for (int j=0; j<dmzSize; j++) {
                if ((dmzY+j) >= CELLS_IN_ROW) {
                   continue;
                } 
                if (field[dmzY+j][dmzX+i] == EMPTY) {
                    field[dmzY+j][dmzX+i] = DMZ;
                }
            }
            if ( dmzX+i == 1 && x == 0) {
                i++;
            }
        }
    }
    
    
    //Checkin if there is enough space to place the horyzontal ship starting from the given point.
    private boolean checkSpaceH (int shipSize, int [][] field, int x, int y) {
        boolean freeSpace = false;
        for (int i=0; i<shipSize; i++) {
            if (field[y][x+i] != 0) {
                freeSpace = false;
                break;
            }
            freeSpace = true;
            } 
        return freeSpace;
        } 

    //Checkin if there is enough space to place the vertical ship starting from the given point.
    private boolean checkSpaceV (int shipSize, int [][] field, int x, int y) {
        boolean freeSpace = false;
        for (int i=0; i<shipSize; i++) {
            if (field[y+i][x] != 0) {
                freeSpace = false;
                break;
            }
            freeSpace = true;
        } 
        return freeSpace;
    }
    
    //Game Over.
    public void gameOver () {
        if (scoreAI > scorePlayer) {
            window.getJLabel().setText("Game over. I win!");
            
        } else {
            window.getJLabel().setText("Game over. You win!");
        }
        window.getPanel().paintImmediately(120, 20, 400,40);
        moveAI = false;
        game = false;
    }
    
    public boolean getGame () {
        return this.game;
    }
    
    public boolean getMoveAI () {
        return this.moveAI;
    } 
    
    public int [][] getMyField () {
        return this.myField;
    }
    
    public int [][] getEnemyField () {
        return this.enemyFieldShow;
    }
    
    public int getScorePlayer () {
        return this.scorePlayer;
    }
    
    public int getScoreAI () {
        return this.scoreAI;
    }
    
    public int getHits () {
        return this.hits;
    }
    
    public int getMisses () {
        return this.misses;
    }
    
    public int [] getNextMove () {
        return this.nextMove;
    }
    
    public int [] getLastMove () {
        return this.lastMove;
    }
    
    public void setGame (boolean game) {
        this.game = game;
    }
    
    public void getMoveAI (boolean moveAI) {
        this.moveAI = moveAI;
    } 
    
    public void getMyField (int [][] myField) {
        this.myField = myField;
    }
    
    public void getEnemyField (int [][] enemyField) {
        this.enemyField = enemyField;
    }
    
    public void getScorePlayer (int scorePlayer) {
        this.scorePlayer = scorePlayer;
    }
    
    public void getScoreAI (int scoreAI) {
        this.scoreAI = scoreAI;
    }
    
    public void getHits (int hits) {
        this.hits = hits;
    }
    
    public void getMisses (int misses) {
        this.misses = misses;
    }
    
    public void getNextMove (int [] nextMove) {
        this.nextMove = nextMove;
    }
    
    public void getLastMove (int [] lastMove) {
        this.lastMove = lastMove;
    }
}
