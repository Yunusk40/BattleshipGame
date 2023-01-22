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
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import com.example.battleship_game.Board.Cell;

public class BattleshipMain extends Application {

    private boolean running = false;
    private Board enemyBoard, playerBoard;
    private int shipsToPlace = 5;
    private boolean enemyTurn = false;
    private Random random = new Random();
    static Stage stageSave = null;
    static Scene sceneSaveBot = null;
    public static boolean win = false;
    private static Stage stage;

    public Parent createContentBot() {
        running = false;
        shipsToPlace = 5;
        enemyTurn = false;
        BorderPane root = new BorderPane();
        root.setMinSize(500,500);
        root.setMaxSize(500,500);

        enemyBoard = new Board(true, event -> {
            if (!running)
                return;

            Cell cell = (Cell) event.getSource();
            if (cell.wasShot)
                return;

            enemyTurn = !cell.shoot();

            if (enemyBoard.ships == 0) {
                System.out.println("YOU WIN");
                win = true;
                try {
                    changeScene();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            if (enemyTurn) {
                try {
                    moveBot();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        playerBoard = new Board(false, event -> {
            if (running)
                return;

            Cell cell = (Cell) event.getSource();
            if (playerBoard.placeShip(new Ship(shipsToPlace, event.getButton() == MouseButton.PRIMARY), cell.x, cell.y)) {
                if (--shipsToPlace == 0) {
                    startGame();
                }
            }
        });

        HBox hbox = new HBox(20, enemyBoard, playerBoard);
        hbox.setAlignment(Pos.CENTER);

        root.setCenter(hbox);

        return root;
    }

    private void moveBot() throws IOException {
        while (enemyTurn) {
            int x = random.nextInt(10);
            int y = random.nextInt(10);

            Cell cell = playerBoard.getCell(x, y);
            if (cell.wasShot)
                continue;

            enemyTurn = cell.shoot();

            if (playerBoard.ships == 0) {
                System.out.println("YOU LOSE");
                win = false;
                changeScene();
            }
        }
    }

    private void changeScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(BattleshipMain.class.getResource("endScreen.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 670);
        sceneSaveBot = new Scene(createContentBot(), 700, 500);
        Stage stage = stageSave;
        stage.setTitle("Battleship");
        stage.setScene(scene);
        stage.setFullScreenExitHint("");
        stage.setResizable(false);
        stage.show();

        stageSave = stage;
    }

    private void startGame() {
        // place enemy ships
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
        sceneSaveBot = new Scene(createContentBot(), 700, 500);
    }

    public static void main(String[] args) {
        launch(args);
    }
}