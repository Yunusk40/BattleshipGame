package com.example.battleship_game;

import java.io.IOException;
import java.util.Random;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import com.example.battleship_game.Gameboard.Box;

public class MainBattleship extends Application {

    private boolean running = false;
    private Gameboard enemyGameboard, playerGameboard;

    private int shipsToPlace = 5;
    private boolean enemyTurn = false;
    private final Random random = new Random();
    static Stage stageSave = null;
    static Scene sceneSaveBot = null;
    static Scene sceneSaveReal = null;
    public static boolean win = false;


    private EnemyAI enemyAI;

    //Erstellt das UI
    public Parent createContent() {
        // Erstellt eine Basis Oberfläche
        running = false;
        shipsToPlace = 5;
        enemyTurn = false;
        BorderPane root = new BorderPane();
        root.setMinSize(500,500);
        root.setMaxSize(500,500);

        // Event = Klicken auf eine Zelle im gegnerischen Spielfeld
        enemyGameboard = new Gameboard(true, event -> {
            // Sind wir im Zustand "Abwechselndes Schießen" wenn nicht Event = Schiff setzen
            if (!running)
                return;

            Box box = (Box) event.getSource();
            if (box.wasShot)
                return;

            // Wurde ein gegnerisches Schiff getroffen? Wenn "ja" dann darf man nochmal da moveEnemy() nicht aufgerufen wird
            enemyTurn = !box.shoot();

            // Befinden sich noch Schiffe auf dem gegnerischen Feld?
            if (enemyGameboard.ships == 0) {
                win = true;
                try {
                    changeScene();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            // Wenn nicht getroffen wurde, dann ist der Gegner am Zug
            if (enemyTurn) {
                try {
                    moveEnemy();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // Event = Klicken auf eine Zelle im eigenen Spielfeld
        playerGameboard = new Gameboard(false, event -> {
            // Sind wir im Zustand "Abwechselndes Schießen" wenn nicht Event = Schiff setzen
            if (running)
                return;

            Box box = (Box) event.getSource();

            // Setzt neues Schiff (horizontal/vertikal). Wenn alle Schiffe gesetzt sind, dann starte das Siel -> running = true!
            if (playerGameboard.placeShip(new Ship(shipsToPlace, event.getButton() == MouseButton.PRIMARY), box.x, box.y)) {
                if (--shipsToPlace == 0) {
                    startGame();
                }
            }
        });

        this.enemyAI = new EnemyAI(playerGameboard);

        // Setzt die boards in eine Horizontale Box und positioniert sie zentral mittig

        HBox hbox = new HBox(20, enemyGameboard, playerGameboard);

        hbox.setAlignment(Pos.CENTER);

        root.setCenter(hbox);

        return root;
    }

    // Führt einen intelligenten Zug vom PC aus
    private void moveEnemy() throws IOException {
        enemyAI.intelligentShoot();

        if (playerGameboard.ships == 0) {
            System.out.println("YOU LOSE");
            win = false;
            changeScene();
        }
    }

    // Erstellt die JavaFX Szene für das Spiel
    private void changeScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainBattleship.class.getResource("endScreen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 670);
        sceneSaveBot = new Scene(createContent(), 700, 500);
        Stage stage = stageSave;
        stage.setTitle("Battleship");
        stage.setScene(scene);
        stage.setFullScreenExitHint("");
        stage.setResizable(false);
        stage.show();

        stageSave = stage;
    }

    private void startGame() {
        // Setzt die gegnerischen Schiffe
        int type = 5;

        while (type > 0) {
            int x = random.nextInt(10);
            int y = random.nextInt(10);

            if (enemyGameboard.placeShip(new Ship(type, Math.random() < 0.5), x, y)) {
                type--;
            }
        }

        running = true;
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(MainBattleship.class.getResource("MainMenu.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 800);
        stage.setTitle("Battleship");
        stage.setScene(scene);
        stage.setFullScreenExitHint("");
        stage.setResizable(false);
        stage.show();

        stageSave = stage;
        sceneSaveBot = new Scene(createContent(),700, 500);
    }

    public static void main(String[] args) {
        launch(args);
    }
}