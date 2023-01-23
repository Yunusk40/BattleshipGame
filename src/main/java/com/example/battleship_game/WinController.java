package com.example.battleship_game;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

import static com.example.battleship_game.BattleshipMain.*;

public class WinController implements Initializable {
    @FXML
    private Pane rulesPane;
    @FXML
    private Text winTitle;

    @FXML
    void startComputer() {
        stageSave.setTitle("Battleship Bot vs Player");

        stageSave.setScene(sceneSaveBot);
        stageSave.show();
    }

    @FXML
    void exit() {
        System.exit(0);
    }

    @FXML
    void rulesButton() {
        rulesPane.setVisible(!rulesPane.isVisible());
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (win){
            winTitle.setText("You won");
        } else {
            winTitle.setText("You lost");
        }
    }
}
