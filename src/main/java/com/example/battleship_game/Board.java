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
 * Spielfeld Klasse
 * Benötigt um die Spielfelder für die Spieler zu erstellen
 * Subklasse: Cell - Segmente / Zellen des Boards
 */
public class Board extends Parent {
    // Kapselt die horizontalen Boxen und ordnet sie Vertikal an -> UI Komponente Spielfeld
    private VBox rows = new VBox();
    // Setzt dest ob Spieler oder Gegner Feld
    private boolean enemy = false;
    // Anzahl der Schiffe pro Feld
    public int ships = 5;

    // Der Kosntruktor bekommt einen EventHandler (=Funktion) übergeben, der das Event "Mouse Klick" verarbeitet
    public Board(boolean enemy, EventHandler<? super MouseEvent> handler) {
        this.enemy = enemy;

        // Erstelle 10 Reihen = Horizontal Box...
        for (int y = 0; y < 10; y++) {
            HBox row = new HBox();
            // ... fülle jede Reihe mit 10 Zellen und füge einen onClickListener hinzu
            for (int x = 0; x < 10; x++) {
                Cell c = new Cell(x, y, this);
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
            int length = ship.type;

            //Platziert das Schiff und färbt die Zellen ein für horizontale und vertikale Platzierung
            if (ship.vertical) {
                for (int i = y; i < y + length; i++) {
                    Cell cell = getCell(x, i);
                    cell.ship = ship;
                    if (!enemy) {
                        cell.setFill(Color.WHITE);
                        cell.setStroke(Color.GREEN);
                    }
                }
            }
            else {
                for (int i = x; i < x + length; i++) {
                    Cell cell = getCell(i, y);
                    cell.ship = ship;
                    if (!enemy) {
                        cell.setFill(Color.BLUE);
                        cell.setStroke(Color.GREEN);
                    }
                }
            }

            return true;
        }

        return false;
    }

    // Holt sich das entsprechende "Cell" Objekt vom Board
    public Cell getCell(int x, int y) {
        return (Cell)((HBox)rows.getChildren().get(y)).getChildren().get(x);
    }

    // Erstellt eine Sammlung von benachbarten Punkten, die sich um die Koordinaten x,y befinden
    public Cell[] getNeighbors(int x, int y, boolean validationFlag) {
        // Erstellt ein Basis Array
        Point2D[] points = new Point2D[] {
                new Point2D(x - 1, y),
                new Point2D(x + 1, y),
                new Point2D(x, y - 1),
                new Point2D(x, y + 1)
        };

        List<Cell> neighbors = new ArrayList<Cell>();

        // Prüft für jeden der Punkte, ob sie innerhalb des Spielfelds liegen
        for (Point2D p : points) {
            if(validationFlag && this.isValidPoint((int)p.getX(), (int)p.getY())){
                Cell checkCell = getCell((int)p.getX(), (int)p.getY());
                if(checkCell.wasShot)
                    continue;
            }

            if (isValidPoint(p)) {
                neighbors.add(getCell((int)p.getX(), (int)p.getY()));
            }
        }

        return neighbors.toArray(new Cell[0]);
    }

    // Prüft, ob ein bestimmtes Schiff auf einer bestimmten Koordinate gesetzt werden kann
    private boolean canPlaceShip(Ship ship, int x, int y) {
        int length = ship.type;

        // Vertikale und horizontale Prüfung, ob ...
        if (ship.vertical) {
            for (int i = y; i < y + length; i++) {
                //... eines der Zellen des Schiffs den Spielfeldrand überschreitet...
                if (!isValidPoint(x, i))
                    return false;

                // ... bereits ein Schiff auf einer der Zellen platziert ist ...
                Cell cell = getCell(x, i);
                if (cell.ship != null)
                    return false;

                // ... bereits ein Schiff in direkter Nähe zum ausgewählten Punkt liegt
                for (Cell neighbor : getNeighbors(x, i, false)) {
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

                Cell cell = getCell(i, y);
                if (cell.ship != null)
                    return false;

                for (Cell neighbor : getNeighbors(i, y, false)) {
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
     * Cell Klasse
     * Erbt von der Klasse Rechteck und stellt die einzelenen Segmente (Zellen) des Spielfelds dar
     */
    public class Cell extends Rectangle {
        // Kooridnaten Variablen
        public int x, y;
        // Ordnet das entsprechende Schiff der Zelle hinzu
        public Ship ship = null;
        // Zustandsvariable - Wurde das Feld bereits "beschossen" -> j/n
        public boolean wasShot = false;
        //Ordnet die Zelle einem Board zu
        private Board board;

        //Konstruktor - Standrad Zelle is grau und Rahmen schwarz
        public Cell(int x, int y, Board board) {
            super(30, 30);
            this.x = x;
            this.y = y;
            this.board = board;
            setFill(Color.LIGHTGRAY);
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
                    board.ships--;
                }
                return true;
            }
            return false;
        }
    }
}