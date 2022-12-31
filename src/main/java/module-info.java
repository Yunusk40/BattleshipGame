module com.example.battleship_game {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.battleship_game to javafx.fxml;
    exports com.example.battleship_game;
}