package com.example.battleship_game;

import javafx.fxml.FXML;

import javafx.scene.layout.Pane;

import static com.example.battleship_game.BattleshipMain.*;

/**
 * Bildet das Menü und die Navigation ab
 */
public class MenuController {
    @FXML
    private Pane rulesPane;

    @FXML
    void exit() {
        System.exit(0);
    }

    @FXML
    void rulesButton() {
        rulesPane.setVisible(!rulesPane.isVisible());
    }

    @FXML
    void startFriends() {
        stageSave.setTitle("Battleship Player vs Player");
        stageSave.setScene(sceneSaveReal);
        stageSave.show();
    }

    @FXML
    void startComputer() {
        stageSave.setTitle("Battleship Bot vs Player");

        stageSave.setScene(sceneSaveBot);
        stageSave.show();
    }

}