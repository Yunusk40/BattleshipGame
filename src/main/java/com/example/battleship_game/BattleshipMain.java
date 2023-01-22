package com.example.battleship_game;

import java.util.Random;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import com.example.battleship_game.Board.Cell;

public class BattleshipMain extends Application {

    private boolean running = false;
    private Board enemyBoard, playerBoard, secondPlayerBoard;

    private int shipsToPlace = 5;

    private boolean enemyTurn = false;

    private Random random = new Random();

    private EnemyAI enemyAI;

    //Erstellt das UI
    public Parent createContent(String mode) {
        // Erstellt eine Basis Pberfläche
        BorderPane root = new BorderPane();
        root.setMinSize(600,800);
        root.setMaxSize(1000,1200);

        // Fügt ein Panel rechts hinzu
        root.setRight(new Text("RIGHT SIDEBAR - CONTROLS"));

        // Event = Klicken auf eine Zelle im gegnerischen Spielfeld
        enemyBoard = new Board(true, event -> {
            // Sind wir im Zustand "Abwechselndes Schießen" wenn nicht Event = Schiff setzen
            if (!running)
                return;

            Cell cell = (Cell) event.getSource();
            if (cell.wasShot)
                return;

            // Wurde ein gegnerisches Schiff getroffen? -> Wenn ja dann darf man nochmal da moveEnemy() nicht aufgerufen wird
            enemyTurn = !cell.shoot();

            // Befinden sich noch Schiffe auf dem gegenerischen Feld?
            if (enemyBoard.ships == 0) {
                System.out.println("YOU WIN");
                System.exit(0);
            }

            // Wenn nicht getroffen wurde, dann ist der Gegner am Zug
            if (enemyTurn)
                moveEnemy();
        });

        // Event = Klicken auf eine Zelle im eigenen Spielfeld
        playerBoard = new Board(false, event -> {
            // Sind wir im Zustand "Abwechselndes Schießen" wenn nicht Event = Schiff setzen
            if (running)
                return;

            Cell cell = (Cell) event.getSource();

            // Setzt neues Schiff (horizonzal/vertiakl). Wenn alle Schiffe gesetzt sind, dann starte das Siel -> running = true!
            if (playerBoard.placeShip(new Ship(shipsToPlace, event.getButton() == MouseButton.PRIMARY), cell.x, cell.y)) {
                if (--shipsToPlace == 0 && mode == "pvc") {
                    startGame();
                }
            }
        });

        secondPlayerBoard = new Board(false, event->{
            // Sind wir im Zustand "Abwechselndes Schießen" wenn nicht Event = Schiff setzen
            if (running)
                return;

            Cell cell = (Cell) event.getSource();

            // Setzt neues Schiff (horizonzal/vertiakl). Wenn alle Schiffe gesetzt sind, dann starte das Siel -> running = true!
            if (secondPlayerBoard.placeShip(new Ship(shipsToPlace, event.getButton() == MouseButton.PRIMARY), cell.x, cell.y)) {
                if (--shipsToPlace == 0 && mode == "pvp") {
                    startGame();
                }
            }
        });

        this.enemyAI = new EnemyAI(playerBoard);

        // Setzt die boards in eine Vertikale Box und positioniert sie zentral mittig

        VBox vbox = null;

        if(mode == "pvc"){
            vbox = new VBox(50, enemyBoard, playerBoard);
        } else if (mode == "pvp"){
            vbox = new VBox(50, secondPlayerBoard, playerBoard);
        }

        vbox.setAlignment(Pos.CENTER);

        root.setCenter(vbox);

        return root;
    }

    // Führt einen intelligenten Zug vom PC aus
    private void moveEnemy() {
        enemyAI.intelligentShoot();
    }

    private void startGame() {
        // Setzt die gegnerischen Schiffe
        int type = 5;

        while (type > 0) {
            int x = random.nextInt(10);
            int y = random.nextInt(10);

            if (enemyBoard.placeShip(new Ship(type, Math.random() < 0.5), x, y)) {
                type--;
            }
        }

        running = true;
    }

    static Stage stageSave = null;
    static Scene sceneSaveBot = null;
    static Scene sceneSaveReal = null;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(BattleshipMain.class.getResource("MainMenu.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 800);
        stage.setTitle("Battleship");
        stage.setScene(scene);
        stage.setFullScreenExitHint("");
        stage.setResizable(false);
        stage.show();

        stageSave = stage;
        sceneSaveBot = new Scene(createContent("pvc"));
        sceneSaveReal = new Scene(createContent("pvp"));
    }

    public static void main(String[] args) {
        launch(args);
    }
}