package com.example.battleship_game;


import java.util.Random;

/**
 * Diese Klasse verbessert die Schusslogik des Computers
 * Computer prüft in welche Richtung es Sinn macht weiter zu schießen
 */
public class EnemyAI {
    private final Random random = new Random();
    // Zustandsvariablen: Definiert, ob zufällig oder gezielt geschossen werden soll
    private boolean shootRandom = true;
    private boolean directionSet = false;
    private final Gameboard gameboard;
    // Die letzte beschossene Zelle und im Falle eines Treffers die erste Zelle
    private Gameboard.Box lastHit = null;
    private Gameboard.Box firstHit = null;

    //Benötigt, um die richtige Richtung weiter zu schießen
    private int directionX = 0;
    private int directionY = 0;

    public EnemyAI(Gameboard playerGameboard) {
        this.gameboard = playerGameboard;
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
                Gameboard.Box box = gameboard.getBox(x, y);
                if (box.wasShot)
                    continue;

                shooting = box.shoot();

                // Bei einem Treffer...
                if (shooting) {
                    // ...die Richtung ermitteln, wohin weiter geschossen werden soll
                    this.firstHit = box;
                    shooting = this.findDirection();
                }

            } else if (!directionSet) { // Die Richtung ist nach einem Treffer noch nicht gefunden
                shooting = this.findDirection();

            } else { // Die Richtung ist gefunden
                boolean reverse = true;

                // In der gefundenen Richtung weiter schießen, solange die Zellen gültig sind
                if (gameboard.isValidPoint(lastHit.x + directionX, lastHit.y + directionY) && lastHit.ship != null){
                    Gameboard.Box nextHit = gameboard.getBox(lastHit.x + directionX, lastHit.y + directionY);
                    if (!nextHit.wasShot){
                        shooting = nextHit.shoot();
                        lastHit = nextHit;
                        reverse = false;
                    }
                }

                // Wenn die Zellen nicht gültig waren oder der letzte Treffer kein Schiff mehr war (Ende des Schiffs)
                // Prüfen, ob es in der anderen Richtung weiter geht
                if ((reverse && checkReverse()) || (reverse && checkReverse() && lastHit.ship == null)) {
                    Gameboard.Box nextHit = gameboard.getBox(firstHit.x + directionX, firstHit.y + directionY);
                    shooting = nextHit.shoot();
                    lastHit = nextHit;
                } else if (reverse && !checkReverse()) { // Wenn es nicht weiter geht, dann versenkt! Und zurück zu random Modus
                    shootRandom = true;
                    directionSet = false;
                }

            }
        }
    }

    public boolean findDirection() {
        boolean foundDirection = false;

        // Alle nicht getroffenen oder ungültigen Nachbarzellen holen
        Gameboard.Box[] allNeighbors = gameboard.getNeighbors(firstHit.x, firstHit.y, true);

        // Wenn es keine gibt, dann in den zufällig Modus gehen
        if(allNeighbors.length == 0) {
            shootRandom = true;
            directionSet = false;
            return true;
        }

        // Die erste Zelle nehmen und schießen
        for (Gameboard.Box neighbor : allNeighbors) {
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
        if (!gameboard.isValidPoint(firstHit.x + (directionX * (-1)), firstHit.y + (directionY * (-1))))
            return false;

        Gameboard.Box nextHit = gameboard.getBox(firstHit.x + (directionX * (-1)), firstHit.y + (directionY * (-1)));
        if (nextHit.wasShot)
            return false;

        // Wenn "ja", dann die Richtung umkehren
        directionX = directionX * (-1);
        directionY = directionY * (-1);

        return true;
    }

}