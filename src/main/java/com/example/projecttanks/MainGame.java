package com.example.projecttanks;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainGame extends Application {
    public static int LOBBY_WIDTH = 400;
    public static int LOBBY_HEIGHT = 300;


    Vector2D offset = new Vector2D(); // if the field (0,0) point was moved
    public Cell[][] field;
    private static int[] dimensions = new int[]{3, 4, 5, 6};
    public int fieldWidth;
    public int fieldHeight;
    public double DEFAULT_CELL_SIZE = 150;

    public GameState gameState;
    UI pane;
    Scene scene;
    Stage stage;

    public List<Tank> tanks = new ArrayList<Tank>();

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        pane = new UI(this);
        scene = new Scene(pane, LOBBY_WIDTH * 2, LOBBY_HEIGHT * 2);
        stage.setMinWidth(LOBBY_WIDTH);
        stage.setMinHeight(LOBBY_HEIGHT);

        pane.start.setOnMousePressed(e -> pane.lobbyBtnPressed(e));
        pane.start.setOnMouseReleased(e -> pane.lobbyBtnReleased(e));

        pane.quit.setOnMousePressed(e -> pane.lobbyBtnPressed(e));
        pane.quit.setOnMouseReleased(e -> pane.lobbyBtnReleased(e));

        gameState = GameState.LOBBY;
        pane.draw();

        Timeline animation = new Timeline(new KeyFrame(new Duration(16), e -> {
            pane.update();
        }));
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();

        stage.setTitle("Tanks2D");
        stage.setScene(scene);
        stage.show();
    }

    public void btnAction(Button btn){
        if (btn == pane.start) {
            gameState = GameState.GAME;
            generateField();
            resizeTheWindow();
            pane.getChildren().clear();
            pane.draw();
        }
        else if (btn == pane.quit) {
            Platform.exit();
        }
    }

    private void resizeTheWindow() {
        stage.setWidth(fieldWidth * DEFAULT_CELL_SIZE);
        stage.setHeight(fieldHeight * DEFAULT_CELL_SIZE);
    }

    public void generateField() {
        Random rand = new Random();
        fieldWidth = dimensions[rand.nextInt(dimensions.length)];
        fieldHeight = dimensions[rand.nextInt(dimensions.length)];
        System.out.println("fieldWidth: " + fieldWidth);
        System.out.println("fieldHeight: " + fieldHeight);
        field = new Cell[fieldWidth][fieldHeight];

        for (int x = 0; x < fieldWidth; x++) {
            for (int y = 0; y < fieldHeight; y++) {
                field[x][y] = new Cell(x, y, DEFAULT_CELL_SIZE);
            }
        }
    }
}
