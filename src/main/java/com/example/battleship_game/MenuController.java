package com.example.battleship_game;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import static com.example.battleship_game.BattleshipMain.sceneSaveBot;
import static com.example.battleship_game.BattleshipMain.stageSave;

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
    void startComputer(MouseEvent event) {
        stageSave.setTitle("Battleship Bot vs Player");

        stageSave.setScene(sceneSaveBot);
        stageSave.show();
    }

}