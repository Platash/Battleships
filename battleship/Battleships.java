package battleship;

import java.awt.Point;
import java.util.*;

public class Battleships {
    
    protected static boolean game;
    protected static boolean moveAI;
    private static Field playerField;
    private static Field enemyField;
    private static Field playerProbField;
    private static boolean hr;
    private static boolean hl;
    private static boolean vd;
    private static boolean vu;
    private static boolean limitR;
    private static boolean limitL;
    private static boolean limitU;
    private static boolean limitD;
    private static int hits;
    private static int firstHitX;
    private static int firstHitY;
    private static Random random;
    protected static GUI window;
    
    
    public static void main(String[] args) {
        startNewGame();
    }
    
    public static void startNewGame() {
        playerField = new Field(true);
        enemyField = new Field(true);
        playerProbField = new Field(false);
        game = true;
        moveAI = false;
        hits = 0;
        random = new Random();
        hr = false;
        hl = false;
        vu = false;
        vd = false;
        limitR = false;
        limitL = false;
        limitD = false;
        limitU = false;
        if(window == null) {
            window = new GUI();
        } else {
            window.updateGraphics();
        }
    }
    
    //Players move analysis
    public static void runMovePlayer(int x, int y) {
        switch (enemyField.getCell(x, y)) {
                case Field.SHIP :
                    enemyField.setCell(x, y, Field.SHIP_DEAD);
                    enemyField.playerScore++;
                    if(enemyField.playerScore == Field.SHIPCELL_COUNT) {
                        gameOver();
                        return;
                    }
                    break;
                case Field.EMPTY :
                    enemyField.setCell(x, y, Field.SHOT);
                    moveAI = true;
                    runMoveAI();
                    break;
            }
        window.updateGraphics(Field.FIELD_2_X + Field.CELL_SIZE * x, 
                Field.FIELD_2_Y + Field.CELL_SIZE * y, Field.CELL_SIZE, Field.CELL_SIZE);
    }
    
       
    //Choosing Move algorithm depending on having enemy ship hit already or not
    public static void runMoveAI() {
        if (hits == 0) {
            runMoveAI1();
        } else if (hits > 0) {
            runMoveAI2 (firstHitX, firstHitY);
        }         
    }
    
     public static void runMoveAI1() {
        if(playerField.allShipsKilled()){
            gameOver();
            return;
        }
        playerProbField.setProbabilities();
        Point point = playerProbField.getRandomHighestProbabilityCell();
        playerProbField.toString();
        switch (playerField.getCell(point.x, point.y)) {
            case Field.SHIP:
                playerField.setCell(point.x, point.y, Field.SHIP_DEAD);
                playerProbField.setCell(point.x, point.y, Field.SHIP_DEAD);
                window.updateGraphics(Field.FIELD_1_X + Field.CELL_SIZE * point.x, 
                        Field.FIELD_1_Y + Field.CELL_SIZE * point.y, Field.CELL_SIZE, Field.CELL_SIZE);
                hits += 1;
                firstHitX = point.x;
                firstHitY = point.y;
                updateBasicLimits(point.x, point.y);
                chooseRandomDirection();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {}
                runMoveAI2(point.x, point.y);
                break;
            case Field.EMPTY:
                playerField.setCell(point.x, point.y, Field.SHOT);
                playerProbField.setCell(point.x, point.y, Field.SHOT);
                window.updateGraphics(Field.FIELD_1_X + Field.CELL_SIZE * point.x, 
                        Field.FIELD_1_Y + Field.CELL_SIZE * point.y, Field.CELL_SIZE, Field.CELL_SIZE);
                moveAI = false;
                break;
         }
     }
     
     public static void runMoveAI2(int x, int y) {
        if(isDestroyed()) {
            sinkShip();
            runMoveAI();
            return;
        } 
        if(hr) {
            proceedHR(x, y);
        } else if(hl) {
            proceedHL(x, y);
        } else if(vd) {
            proceedVD(x, y);
        } else if(vu) {
            proceedVU(x, y);
        }
     }
     
     private static void chooseRandomDirection() {
         hr = false;
         hl = false;
         vd = false;
         vu = false;
         if(random.nextBoolean()) {
             if(random.nextBoolean()) {
                 hr = true;
             } else {
                 hl = true;
             }
         } else {
             if(random.nextBoolean()) {
                vd = true;
             } else {
                vu = true;
             }
         }
         if((hr && limitR) || (hl && limitL) || (vd && limitD) || (vu && limitU)) {
             chooseRandomDirection();
         } 
     }
    
        
    //Game Over.
    public static void gameOver () {
        if (playerField.allShipsKilled()) {
            window.getJLabel().setText("Game over. I win!");
        } else {
            window.getJLabel().setText("Game over. You win!");
        }
        window.getPanel().paintImmediately(120, 20, 400,40);
        moveAI = false;
        game = false;
    }

    private static void updateBasicLimits(int x, int y) {
        if(x == 0) {
            limitL = true;
        }
        if(x == Field.CELLS_IN_ROW) {
            limitR = true;
        }
        if(y == 0) {
            limitU = true;
        }
        if(y == Field.CELLS_IN_ROW) {
            limitD = true;
        }
    }

    private static boolean isDestroyed() {
        boolean horyzontal = (hr || hl);
        if(limitR && limitL && limitU && limitD) {
            return true;
        } 
        if(limitR && limitL && horyzontal && hits > 1) {
            return true;
        }
        if(limitD && limitU && !horyzontal && hits > 1) {
            return true;
        }
        if(hits >= playerField.getBiggestShip()) {
            return true;
        }
        return false;
    }
    
    private static void sinkShip() {
        boolean horyzontal = (hr || hl);
        playerField.createDMZ(hits, firstHitX, firstHitY, horyzontal);
        playerProbField.createDMZ(hits, firstHitX, firstHitY, horyzontal);
        playerField.removeShip(hits);
        playerProbField.removeShip(hits);
        window.updateGraphics();
        limitR = false;
        limitL = false;
        limitU = false;
        limitD = false;
        hits = 0;
    }

    private static void proceedHR(int x, int y) {
        int thisMoveX = x + 1;
        int thisMoveY = y;
        if(thisMoveX >= Field.CELLS_IN_ROW && hits == 1) {
            limitR = true;
            chooseRandomDirection();
            runMoveAI2(firstHitX, firstHitY);
            return;
        } else if (thisMoveX >= Field.CELLS_IN_ROW) {
            limitR = true;
            hr = false;
            hl = true;
            runMoveAI2(firstHitX, firstHitY);
            return;
        } 
        switch (playerField.getCell(thisMoveX, thisMoveY)) {
            case Field.SHIP:
                playerField.setCell(thisMoveX, thisMoveY, Field.SHIP_DEAD);
                playerProbField.setCell(x, y, Field.SHIP_DEAD);
                window.updateGraphics(Field.FIELD_1_X + Field.CELL_SIZE * thisMoveX, 
                        Field.FIELD_1_Y + Field.CELL_SIZE * thisMoveY, Field.CELL_SIZE, Field.CELL_SIZE);
                hits += 1;
                runMoveAI2(thisMoveX, thisMoveY);
                break;
            case Field.EMPTY:
                limitR = true;
                playerField.setCell(thisMoveX, thisMoveY, Field.SHOT);
                playerProbField.setCell(thisMoveX, thisMoveY, Field.SHOT);
                window.updateGraphics(Field.FIELD_1_X + Field.CELL_SIZE * thisMoveX, 
                        Field.FIELD_1_Y + Field.CELL_SIZE * thisMoveY, Field.CELL_SIZE, Field.CELL_SIZE);
                moveAI = false;
                if(hits == 1) {
                    chooseRandomDirection();
                } else {
                    hr = false;
                    hl = true;
                }
                break;
            default:
                limitR = true;
                if(hits == 1) {
                   chooseRandomDirection();
                   runMoveAI2(firstHitX, firstHitY);
                } else {
                   hr = false;
                   hl = true;
                   runMoveAI2(firstHitX, firstHitY);
                }
                break;
        }
    }

    private static void proceedHL(int x, int y) {
        int thisMoveX = x - 1;
        int thisMoveY = y;
        if(thisMoveX < 0 && hits == 1) {
            limitL = true;
            chooseRandomDirection();
            runMoveAI2(firstHitX, firstHitY);
            return;
        } else if (thisMoveX < 0) {
            limitL = true;
            hl = false;
            hr = true;
            runMoveAI2(firstHitX, firstHitY);
            return;
        } 
        switch (playerField.getCell(thisMoveX, thisMoveY)) {
            case Field.SHIP:
                playerField.setCell(thisMoveX, thisMoveY, Field.SHIP_DEAD);
                playerProbField.setCell(thisMoveX, thisMoveY, Field.SHIP_DEAD);
                window.updateGraphics(Field.FIELD_1_X + Field.CELL_SIZE * thisMoveX, 
                        Field.FIELD_1_Y + Field.CELL_SIZE * thisMoveY, Field.CELL_SIZE, Field.CELL_SIZE);
                hits += 1;
                runMoveAI2(thisMoveX, thisMoveY);
                break;
            case Field.EMPTY:
                limitL = true;
                playerField.setCell(thisMoveX, thisMoveY, Field.SHOT);
                playerProbField.setCell(thisMoveX, thisMoveY, Field.SHOT);
                window.updateGraphics(Field.FIELD_1_X + Field.CELL_SIZE * thisMoveX, 
                        Field.FIELD_1_Y + Field.CELL_SIZE * thisMoveY, Field.CELL_SIZE, Field.CELL_SIZE);
                moveAI = false;
                if(hits == 1) {
                   chooseRandomDirection();
                } else {
                   hl = false;
                   hr = true;
                }
                break;
            default:
                limitL = true;
                if(hits == 1) {
                   chooseRandomDirection();
                   runMoveAI2(firstHitX, firstHitY);
                } else {
                   hl = false;
                   hr = true;
                   runMoveAI2(firstHitX, firstHitY);
                }
                break;
        }
    }

    private static void proceedVD(int x, int y) {
        int thisMoveX = x;
        int thisMoveY = y + 1;
        if(thisMoveY >= Field.CELLS_IN_ROW && hits == 1) {
            limitD = true;
            chooseRandomDirection();
            runMoveAI2(firstHitX, firstHitY);
            return;
        } else if (thisMoveY >= Field.CELLS_IN_ROW) {
            limitD = true;
            vd = false;
            vu = true;
            runMoveAI2(firstHitX, firstHitY);
            return;
        } 
        switch (playerField.getCell(thisMoveX, thisMoveY)) {
            case Field.SHIP:
                playerField.setCell(thisMoveX, thisMoveY, Field.SHIP_DEAD);
                playerProbField.setCell(thisMoveX, thisMoveY, Field.SHIP_DEAD);
                window.updateGraphics(Field.FIELD_1_X + Field.CELL_SIZE * thisMoveX, 
                        Field.FIELD_1_Y + Field.CELL_SIZE * thisMoveY, Field.CELL_SIZE, Field.CELL_SIZE);
                hits += 1;
                runMoveAI2(thisMoveX, thisMoveY);
                break;
            case Field.EMPTY:
                limitD = true;
                playerField.setCell(thisMoveX, thisMoveY, Field.SHOT);
                playerProbField.setCell(thisMoveX, thisMoveY, Field.SHOT);
                window.updateGraphics(Field.FIELD_1_X + Field.CELL_SIZE * thisMoveX, 
                        Field.FIELD_1_Y + Field.CELL_SIZE * thisMoveY, Field.CELL_SIZE, Field.CELL_SIZE);
                moveAI = false;
                if(hits == 1) {
                   chooseRandomDirection();
                } else {
                   vd = false;
                   vu = true;
                }
                break;
            default:
                limitD = true;
                if(hits == 1) {
                   chooseRandomDirection();
                   runMoveAI2(firstHitX, firstHitY);
                } else {
                   vd = false;
                   vu = true;
                   runMoveAI2(firstHitX, firstHitY);
                }
                break;
        }
    }

    private static void proceedVU(int x, int y) {
        int thisMoveX = x;
        int thisMoveY = y - 1;
        if(thisMoveY < 0 && hits == 1) {
            limitU = true;
            chooseRandomDirection();
            runMoveAI2(firstHitX, firstHitY);
            return;
        } else if (thisMoveY < 0) {
            limitU = true;
            vu = false;
            vd = true;
            runMoveAI2(firstHitX, firstHitY);
            return;
        } 
        switch (playerField.getCell(thisMoveX, thisMoveY)) {
            case Field.SHIP:
                playerField.setCell(thisMoveX, thisMoveY, Field.SHIP_DEAD);
                playerProbField.setCell(thisMoveX, thisMoveY, Field.SHIP_DEAD);
                window.updateGraphics(Field.FIELD_1_X + Field.CELL_SIZE * thisMoveX, 
                        Field.FIELD_1_Y + Field.CELL_SIZE * thisMoveY, Field.CELL_SIZE, Field.CELL_SIZE);
                hits += 1;
                runMoveAI2(thisMoveX, thisMoveY);
                break;
            case Field.EMPTY:
                limitU = true;
                playerField.setCell(thisMoveX, thisMoveY, Field.SHOT);
                playerProbField.setCell(thisMoveX, thisMoveY, Field.SHOT);
                window.updateGraphics(Field.FIELD_1_X + Field.CELL_SIZE * thisMoveX, 
                        Field.FIELD_1_Y + Field.CELL_SIZE * thisMoveY, Field.CELL_SIZE, Field.CELL_SIZE);
                moveAI = false;
                if(hits == 1) {
                   chooseRandomDirection();
                } else {
                   vu = false;
                   vd = true;
                }
                break;
            default:
                limitU = true;
                if(hits == 1) {
                   chooseRandomDirection();
                   runMoveAI2(firstHitX, firstHitY);
                } else {
                   vu = false;
                   vd = true;
                   runMoveAI2(firstHitX, firstHitY);
                }
                break;
        }
    }
        
    public void setGame(boolean g) {
        game = g;
    }
    
    public static int getPlayerFieldCell(int x, int y) {
        return playerField.getCell(x, y);
    }
    
    public static int getEnemyFieldCell(int x, int y) {
        return enemyField.getCell(x, y);
    }
    
}
