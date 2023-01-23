package com.example.battleship_game;

import java.util.ArrayList;
import java.util.List;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Spielfeld Klasse -
 * Klasse wird benötigt, um die Spielfelder für die Spieler zu erstellen
 * Subklasse: Box - Segmente / Zellen des Boards
 */
public class Gameboard extends Parent {
    // Kapselt die horizontalen Boxen und ordnet sie Vertikal an → UI Komponente Spielfeld
    public final VBox rows = new VBox();
    // Setzt dest ob Spieler oder Gegner Feld
    private final boolean enemy;
    // Anzahl der Schiffe pro Feld
    public int ships = 5;

    // Der Konstruktor bekommt einen EventHandler (=Funktion) übergeben, der das Event "Mouse Klick" verarbeitet
    public Gameboard(boolean enemy, EventHandler<? super MouseEvent> handler) {
        this.enemy = enemy;

        // Erstelle 10 Reihen = Horizontal Box...
        for (int y = 0; y < 10; y++) {
            HBox row = new HBox();
            // ... fülle jede Reihe mit 10 Zellen und füge einen onClickListener hinzu
            for (int x = 0; x < 10; x++) {
                Box c = new Box(x, y, this);
                c.setOnMouseClicked(handler);
                row.getChildren().add(c);
            }

            rows.getChildren().add(row);
        }

        getChildren().add(rows);
    }

    // Fügt ein Schiff auf dem Board hinzu
    public boolean placeShip(Ship ship, int x, int y) {
        // Darf auf der Stelle platziert werden
        if (canPlaceShip(ship, x, y)) {
            int length = ship.ships;

            //Platziert das Schiff und färbt die Zellen ein für horizontale und vertikale Platzierung
            if (ship.vertical) {
                for (int i = y; i < y + length; i++) {
                    Box box = getBox(x, i);
                    box.ship = ship;
                    if (!enemy) {
                        box.setFill(Color.GRAY);
                        box.setStroke(Color.GREEN);
                    }
                }
            }
            else {
                for (int i = x; i < x + length; i++) {
                    Box box = getBox(i, y);
                    box.ship = ship;
                    if (!enemy) {
                        box.setFill(Color.GRAY);
                        box.setStroke(Color.GREEN);
                    }
                }
            }

            return true;
        }

        return false;
    }

    // Holt sich das entsprechende "Box" Objekt vom Board
    public Box getBox(int x, int y) {
        return (Box)((HBox)rows.getChildren().get(y)).getChildren().get(x);
    }

    // Erstellt eine Sammlung von benachbarten Punkten, die sich um die Koordinaten x und y befinden
    public Box[] getNeighbors(int x, int y, boolean validationFlag) {
        // Erstellt ein Basisarray
        Point2D[] points = new Point2D[] {
                new Point2D(x - 1, y),
                new Point2D(x + 1, y),
                new Point2D(x, y - 1),
                new Point2D(x, y + 1)
        };

        List<Box> neighbors = new ArrayList<>();

        // Prüft für jeden der Punkte, ob sie innerhalb des Spielfelds liegen
        for (Point2D p : points) {
            if(validationFlag && this.isValidPoint((int)p.getX(), (int)p.getY())){
                Box checkBox = getBox((int)p.getX(), (int)p.getY());
                if(checkBox.wasShot)
                    continue;
            }

            if (isValidPoint(p)) {
                neighbors.add(getBox((int)p.getX(), (int)p.getY()));
            }
        }

        return neighbors.toArray(new Box[0]);
    }

    // Prüft, ob ein bestimmtes Schiff auf einer bestimmten Koordinate gesetzt werden kann
    private boolean canPlaceShip(Ship ship, int x, int y) {
        int length = ship.ships;

        // Vertikale und horizontale Prüfung, ob ...
        if (ship.vertical) {
            for (int i = y; i < y + length; i++) {
                //... eines der Zellen des Schiffs den Spielfeldrand überschreitet...
                if (!isValidPoint(x, i))
                    return false;

                // ... bereits ein Schiff auf einer der Zellen platziert ist ...
                Box box = getBox(x, i);
                if (box.ship != null)
                    return false;

                // ... bereits ein Schiff in direkter Nähe zum ausgewählten Punkt liegt
                for (Box neighbor : getNeighbors(x, i, false)) {
                    if (neighbor.ship != null)
                        return false;
                }
            }
        }
        //Analoge Prüfung für horizontale Platzierung
        else {
            for (int i = x; i < x + length; i++) {
                if (!isValidPoint(i, y))
                    return false;

                Box box = getBox(i, y);
                if (box.ship != null)
                    return false;

                for (Box neighbor : getNeighbors(i, y, false)) {
                    if (!isValidPoint(i, y))
                        return false;

                    if (neighbor.ship != null)
                        return false;
                }
            }
        }

        return true;
    }

    //Method overloading: Prüft, ob der eingegebene Punkt innerhalb des Spielfeldes liegt
    private boolean isValidPoint(Point2D point) {
        return isValidPoint(point.getX(), point.getY());
    }

    public boolean isValidPoint(double x, double y) {
        return x >= 0 && x < 10 && y >= 0 && y < 10;
    }

    /**
     * Box Klasse
     * Zelle erbt von der Klasse Rechteck und stellt die einzelnen Segmente (Zellen) des Spielfelds dar
     */
    public static class Box extends Rectangle {
        // Koordinaten Variablen
        public int x, y;
        // Ordnet das entsprechende Schiff der Zelle hinzu
        public Ship ship = null;
        // Zustandsvariable - Wurde das Feld bereits "beschossen" -> j/n
        public boolean wasShot = false;
        //Ordnet die Zelle einem Board zu
        private final Gameboard gameboard;

        //Konstruktor - Standrad Zelle is grau und Rahmen schwarz
        public Box(int x, int y, Gameboard gameboard) {
            super(30, 30);
            this.x = x;
            this.y = y;
            this.gameboard = gameboard;
            setFill(Color.BLUE);
            setStroke(Color.BLACK);
        }

        // Verarbeitet was passiert, wenn eine Zelle beschossen wird
        public boolean shoot() {
            // Zustand ändern und Füllung = Schwarz
            wasShot = true;
            setFill(Color.BLACK);

            // Prüfung, ob die Zelle zu einem Schiff gehört
            if (ship != null) {
                // hit() Methode aufrufen, Füllung ändern und prüfen ...
                ship.hit();
                setFill(Color.RED);
                //..., ob das Schiff alle Leben verloren hat
                if (!ship.isAlive()) {
                    gameboard.ships--;
                }
                return true;
            }
            return false;
        }
    }
}