package com.example.battleship_game;

import javafx.scene.Parent;

public class Ship extends Parent {
    public int ships;
    public boolean vertical = true;

    private int health;

    public Ship(int ships, boolean vertical) {
        this.ships = ships;
        this.vertical = vertical;
        health = ships;

    }

    public void hit() {
        health--;
    }

    public boolean isAlive() {
        return health > 0;
    }
}