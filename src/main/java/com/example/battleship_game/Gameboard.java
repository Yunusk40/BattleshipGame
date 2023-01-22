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

public class Gameboard extends Parent {
    private VBox rows = new VBox();
    private boolean enemy = false;
    public int ships = 5;

    public Gameboard(boolean enemy, EventHandler<? super MouseEvent> handler) {
        this.enemy = enemy;
        for (int y = 0; y < 10; y++) {
            HBox row = new HBox();
            for (int x = 0; x < 10; x++) {
                Box c = new Box(x, y, this);
                c.setOnMouseClicked(handler);
                row.getChildren().add(c);
            }

            rows.getChildren().add(row);
        }

        getChildren().add(rows);
    }

    public boolean placeShip(Ship ship, int x, int y) {
        if (canPlaceShip(ship, x, y)) {
            int length = ship.ships;

            if (ship.vertical) {
                for (int i = y; i < y + length; i++) {
                    Box box = getBox(x, i);
                    box.ship = ship;
                    if (!enemy) {
                        box.setFill(Color.DARKGRAY);
                        box.setStroke(Color.GREEN);
                    }
                }
            }
            else {
                for (int i = x; i < x + length; i++) {
                    Box box = getBox(i, y);
                    box.ship = ship;
                    if (!enemy) {
                        box.setFill(Color.DARKGRAY);
                        box.setStroke(Color.GREEN);
                    }
                }
            }

            return true;
        }

        return false;
    }

    public Box getBox(int x, int y) {
        return (Box)((HBox)rows.getChildren().get(y)).getChildren().get(x);
    }

    private Box[] getNeighbors(int x, int y) {
        Point2D[] points = new Point2D[] {
                new Point2D(x - 1, y),
                new Point2D(x + 1, y),
                new Point2D(x, y - 1),
                new Point2D(x, y + 1)
        };

        List<Box> neighbors = new ArrayList<Box>();

        for (Point2D p : points) {
            if (isValidPoint(p)) {
                neighbors.add(getBox((int)p.getX(), (int)p.getY()));
            }
        }

        return neighbors.toArray(new Box[0]);
    }

    private boolean canPlaceShip(Ship ship, int x, int y) {
        int length = ship.ships;

        if (ship.vertical) {
            for (int i = y; i < y + length; i++) {
                if (!isValidPoint(x, i))
                    return false;

                Box box = getBox(x, i);
                if (box.ship != null)
                    return false;

                for (Box neighbor : getNeighbors(x, i)) {
                    if (!isValidPoint(x, i))
                        return false;

                    if (neighbor.ship != null)
                        return false;
                }
            }
        }
        else {
            for (int i = x; i < x + length; i++) {
                if (!isValidPoint(i, y))
                    return false;

                Box box = getBox(i, y);
                if (box.ship != null)
                    return false;

                for (Box neighbor : getNeighbors(i, y)) {
                    if (!isValidPoint(i, y))
                        return false;

                    if (neighbor.ship != null)
                        return false;
                }
            }
        }

        return true;
    }

    private boolean isValidPoint(Point2D point) {
        return isValidPoint(point.getX(), point.getY());
    }

    private boolean isValidPoint(double x, double y) {
        return x >= 0 && x < 10 && y >= 0 && y < 10;
    }

    public class Box extends Rectangle {
        public int x, y;
        public Ship ship = null;
        public boolean wasShot = false;

        private Gameboard gameBoard;

        public Box(int x, int y, Gameboard gameBoard) { //erstellt die BOX bei der Scene
            super(30, 30);
            this.x = x;
            this.y = y;
            this.gameBoard = gameBoard;
            setFill(Color.BLUE);
            setStroke(Color.BLACK);
        }

        public boolean shoot() { //Methode
            wasShot = true;
            setFill(Color.BLACK);

            if (ship != null) {
                ship.hit();
                setFill(Color.RED);
                if (!ship.isAlive()) {
                    gameBoard.ships--;
                }
                return true;
            }

            return false;
        }
    }

}
