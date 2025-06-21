package com.example.projecttanks;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
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
    LobbyPane lobbyPane;
    Scene lobbyScene;
    BorderPane borderPane = new BorderPane();
    ButtleField buttleField;
    Scene buttleScene = new Scene(borderPane);
    Stage stage;

    Button quit = new Button("Quit");
    Label lbPlayer1 = new Label();
    Label lbTime = new Label();
    Label lbPlayer2 = new Label();

    HBox topPane = new HBox(lbPlayer1, lbTime, lbPlayer2);
    HBox bottomPane = new HBox(quit);

    VBox leftPane = new VBox();
    VBox rightPane = new VBox();

    public List<Tank> tanks = new ArrayList<Tank>();

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;

        lobbyPane = new LobbyPane(this);
        lobbyScene = new Scene(lobbyPane, LOBBY_WIDTH * 2, LOBBY_HEIGHT * 2);

        lobbyPane.start.setOnMousePressed(e -> lobbyPane.lobbyBtnPressed(e));
        lobbyPane.start.setOnMouseReleased(e -> lobbyPane.lobbyBtnReleased(e));

        lobbyPane.quit.setOnMousePressed(e -> lobbyPane.lobbyBtnPressed(e));
        lobbyPane.quit.setOnMouseReleased(e -> lobbyPane.lobbyBtnReleased(e));

        gameState = GameState.LOBBY;
        lobbyPane.draw();

        // ButtleField
        borderPane = new BorderPane();
        buttleScene = new Scene(borderPane);

        borderPane.setPrefSize(800, 600);

        topPane.setAlignment(Pos.CENTER);
        topPane.setSpacing(40);
        topPane.setStyle("-fx-background-color: yellow;");
        borderPane.setTop(topPane);

        bottomPane.setAlignment(Pos.CENTER);
        bottomPane.setSpacing(40);
        bottomPane.setStyle("-fx-background-color: yellow;");
        borderPane.setBottom(bottomPane);

        leftPane.setStyle("-fx-background-color: yellow;");
        borderPane.setLeft(leftPane);
        rightPane.setStyle("-fx-background-color: yellow;");
        borderPane.setRight(rightPane);

        buttleField = new ButtleField(this);
        borderPane.setCenter(buttleField);

        borderPane.widthProperty().addListener((observableValue, number, t1) -> {
            updateMargin();
        });

        borderPane.heightProperty().addListener((observableValue, number, t1) -> {
            updateMargin();
        });

        stage.heightProperty().addListener((observableValue, number, t1) -> {
            updateFontSize();
        });

        Timeline animation = new Timeline(new KeyFrame(new Duration(16), e -> {
            // TODO
        }));
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();

        stage.setTitle("Tanks2D");
        stage.setScene(lobbyScene);
        stage.show();
    }

    private void updateFontSize() {
        double fontSize = buttleField.getHeight() / 25.0;
        Font font = Font.font(fontSize);

        lbPlayer1.setFont(font);
        lbTime.setFont(font);
        lbPlayer2.setFont(font);

        quit.setFont(font);
    }

    private void updateMargin() {
        double size = Math.min(borderPane.getWidth(), borderPane.getHeight());
        double horizontalMargin = (borderPane.getWidth() - size) / 2.0;
        double verticalMargin = (borderPane.getHeight() - size) / 2.0;
        topPane.setPrefHeight(verticalMargin);
        bottomPane.setPrefHeight(verticalMargin);
        leftPane.setPrefWidth(horizontalMargin);
        rightPane.setPrefWidth(horizontalMargin);
    }

    public void btnAction(Button btn){
        if (btn == lobbyPane.start) {
            gameState = GameState.GAME;
            generateField();
            stage.setScene(buttleScene);
            buttleField.draw();
        }
        else if (btn == lobbyPane.quit) {
            Platform.exit();
        }
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
