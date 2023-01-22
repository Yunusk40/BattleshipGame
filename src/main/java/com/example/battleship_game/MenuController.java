package com.example.battleship_game;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import static com.example.battleship_game.BattleshipMain.*;

/**
 * Bildet das Menü und die Navigation ab
 */
public class MenuController {
    @FXML
    private Pane rulesPane;

    @FXML
    void exit(MouseEvent event) {
        System.exit(0);
    }

    @FXML
    void rulesButton(ActionEvent event) {
        rulesPane.setVisible(!rulesPane.isVisible());
    }

    @FXML
    void startFriends(MouseEvent event) {
        stageSave.setTitle("Battleship Player vs Player");
        stageSave.setScene(sceneSaveReal);
        stageSave.show();
    }

    @FXML
    void startComputer(MouseEvent event) {
        stageSave.setTitle("Battleship Bot vs Player");

        stageSave.setScene(sceneSaveBot);
        stageSave.show();
    }

}