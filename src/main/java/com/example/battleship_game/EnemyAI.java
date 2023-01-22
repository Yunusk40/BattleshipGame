package com.example.battleship_game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EnemyAI {
    private Random random = new Random();
    private boolean shootRandom = true;
    Board board;
    Board.Cell lastHit = null;
    Board.Cell firstHit = null;
    boolean directionSet = false;
    int directionX = 0;
    int directionY = 0;

    List<Board.Cell> neighborsToCheck = new ArrayList<>();

    public EnemyAI(Board playerBoard) {
        this.board = playerBoard;
    }

    public void intelligentShoot() {
        boolean shooting = true;
        // Check if to shoot randomly or as a follow-up
        while (shooting) {
            if (shootRandom) {
                int x = random.nextInt(10);
                int y = random.nextInt(10);

                // Wurde die Zelle bereits getroffen dann nächste Schleifenwiederholung
                Board.Cell cell = board.getCell(x, y);
                if (cell.wasShot)
                    continue;

                shooting = cell.shoot();

                if (shooting) {
                    this.firstHit = cell;
                    shooting = this.findDirection();
                }

            } else if (!directionSet && !shootRandom) {
                shooting = this.findDirection();
            } else if (directionSet && !shootRandom) {
                boolean reverse = true;

                if (board.isValidPoint(lastHit.x + directionX, lastHit.y + directionY)){
                    Board.Cell nextHit = board.getCell(lastHit.x + directionX, lastHit.y + directionY);
                    if (!nextHit.wasShot){
                        shooting = nextHit.shoot();
                        lastHit = nextHit;
                        reverse = false;
                    }
                }

                if (reverse && checkReverse()) {
                    Board.Cell nextHit = board.getCell(lastHit.x + directionX, lastHit.y + directionY);
                    shooting = nextHit.shoot();
                    lastHit = nextHit;
                } else if ((reverse && !checkReverse()) || !shooting) {
                    shootRandom = true;
                    directionSet = false;
                }

            }
        }

        if (board.ships == 0) {
            System.out.println("YOU LOSE");
            System.exit(0);
        }
    }

    public boolean findDirection() {
        boolean foundDirection = false;
        int x = 0, y = 0;
        for (Board.Cell neighbor : board.getNeighbors(firstHit.x, firstHit.y, true)) {
            foundDirection = neighbor.shoot();
            lastHit = neighbor;
            directionX = neighbor.x - firstHit.x;
            directionY = neighbor.y - firstHit.y;
            break;
        }

        if (foundDirection) {
            directionSet = true;
        }
        shootRandom = false;
        return foundDirection;
    }

    private boolean checkReverse() {
        if (!board.isValidPoint(firstHit.x + (directionX * (-1)), firstHit.y + (directionY * (-1))))
            return false;

        Board.Cell nextHit = board.getCell(firstHit.x + (directionX * (-1)), firstHit.y + (directionY * (-1)));
        if (nextHit.wasShot)
            return false;

        directionX = directionX * (-1);
        directionY = directionY * (-1);

        return true;
    }

}