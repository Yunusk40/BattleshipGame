package com.example.battleship_game;

import javafx.scene.Parent;

/**
 * Schiff Klasse
 */
public class Ship extends Parent {
    public int type;
    public boolean vertical;
    private int health;

    public Ship(int ships, boolean vertical) {
        this.type = ships;
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