package com.example.battleship_game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Diese Klasse verbessert die Schusslogik des Computers
 * Der Computer prüft in welche Richtung es Sinn macht weiter zu schießen
 */
public class EnemyAI {
    private Random random = new Random();
    // Zustandsvariablen: Definiert, ob zufällig oder gezielt geschossen werden soll
    private boolean shootRandom = true;
    private boolean directionSet = false;
    private Board board;
    // Die letzte beschossene Zelle und im Falle eines Treffers die erste Zelle
    private Board.Cell lastHit = null;
    private Board.Cell firstHit = null;

    private int directionX = 0;
    private int directionY = 0;

    public EnemyAI(Board playerBoard) {
        this.board = playerBoard;
    }

    public void intelligentShoot() {
        boolean shooting = true;
        // Schießt so lange ein Schiff getroffen wird
        while (shooting) {
            // Zufällige Zelle schießen
            if (shootRandom) {
                int x = random.nextInt(10);
                int y = random.nextInt(10);

                // Wurde die Zelle bereits getroffen dann nächste Schleifenwiederholung
                Board.Cell cell = board.getCell(x, y);
                if (cell.wasShot)
                    continue;

                shooting = cell.shoot();

                // Bei einem Treffer...
                if (shooting) {
                    // ...die Richtung ermitteln, wohin weiter geschossen werden soll
                    this.firstHit = cell;
                    shooting = this.findDirection();
                }

            } else if (!directionSet && !shootRandom) { // Die Richtung ist nach einem Treffer noch nicht gefunden
                shooting = this.findDirection();

            } else if (directionSet && !shootRandom) { // Die Richtung ist gefunden
                boolean reverse = true;

                // In der gefundenen Richtung weiter schießen, solange die Zellen gültig sind
                if (board.isValidPoint(lastHit.x + directionX, lastHit.y + directionY) && lastHit.ship != null){
                    Board.Cell nextHit = board.getCell(lastHit.x + directionX, lastHit.y + directionY);
                    if (!nextHit.wasShot){
                        shooting = nextHit.shoot();
                        lastHit = nextHit;
                        reverse = false;
                    }
                }

                // Wenn die Zellen nicht gültig waren oder der letzte Treffer kein Schiff mehr war (Ende des Schiffs)
                // Prüfen ob es in der anderen Richtung weiter geht
                if ((reverse && checkReverse()) || (reverse && checkReverse() && lastHit.ship == null)) {
                    Board.Cell nextHit = board.getCell(firstHit.x + directionX, firstHit.y + directionY);
                    shooting = nextHit.shoot();
                    lastHit = nextHit;
                } else if (reverse && !checkReverse()) { // Wenn es nicht weiter geht, dann versenkt! Und zurück zu random Modus
                    shootRandom = true;
                    directionSet = false;
                }

            }
        }

        // Wenn alle Schiffe versenkt sind, dann Ende
        if (board.ships == 0) {
            System.out.println("YOU LOSE");
            System.exit(0);
        }
    }

    public boolean findDirection() {
        boolean foundDirection = false;
        int x = 0, y = 0;
        // Alle nicht getroffenen oder ungültigen Nachbarzellen holen
        Board.Cell[] allNeighbors = board.getNeighbors(firstHit.x, firstHit.y, true);

        // Wenn es keine gibt, dann in den Zufällig Modus gehen
        if(allNeighbors.length == 0 || allNeighbors == null) {
            shootRandom = true;
            directionSet = false;
            return true;
        }

        // Die erste Zelle nehmen und schießen
        for (Board.Cell neighbor : allNeighbors) {
            foundDirection = neighbor.shoot();
            lastHit = neighbor;
            // Gibt die Richtung an in die weiter geschossen werden soll
            directionX = neighbor.x - firstHit.x;
            directionY = neighbor.y - firstHit.y;
            break;
        }

        // Richtung gefunden?
        if (foundDirection)
            directionSet = true;

        // Random Modus verlassen
        shootRandom = false;
        return foundDirection;
    }


    private boolean checkReverse() {
        // Gibt es eine gültige noch nicht beschossene Zelle am anderen Ende
        if (!board.isValidPoint(firstHit.x + (directionX * (-1)), firstHit.y + (directionY * (-1))))
            return false;

        Board.Cell nextHit = board.getCell(firstHit.x + (directionX * (-1)), firstHit.y + (directionY * (-1)));
        if (nextHit.wasShot)
            return false;

        // Wenn ja dann die Richtung umkehren
        directionX = directionX * (-1);
        directionY = directionY * (-1);

        return true;
    }

}