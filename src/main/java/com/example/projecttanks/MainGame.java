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
    public static int LOBBY_WIDTH = 1000;
    public static int LOBBY_HEIGHT = 600;


    Vector2D offset = new Vector2D(); // if the field (0,0) point was moved
    public Cell[][] field;
    private static int[] dimensions = new int[]{3, 4, 5, 6, 7, 8, 9, 10};
    public int fieldWidth;
    public int fieldHeight;
    public double cellSize;

    public GameState gameState;
    LobbyPane lobbyPane;
    Scene lobbyScene;
    BorderPane borderPane = new BorderPane();
    BattleField battleField;
    Scene battleScene;
    Stage stage;

    Button quit = new Button("Quit");
    Label lbPlayer1 = new Label();
    Label lbTime = new Label();
    Label lbPlayer2 = new Label();

    HBox topPane = new HBox(lbPlayer1, lbTime, lbPlayer2);
    HBox bottomPane = new HBox(quit);

    VBox leftPane = new VBox();
    VBox rightPane = new VBox();

    public int time = 0;
    public int timeLimit = 60;

    public List<Tank> tanks = new ArrayList<Tank>();

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        this.stage.setMinWidth((double) LOBBY_WIDTH / 2);
        this.stage.setMinHeight((double) LOBBY_HEIGHT / 2);

        lobbyPane = new LobbyPane(this);
        lobbyScene = new Scene(lobbyPane, LOBBY_WIDTH, LOBBY_HEIGHT);

        lobbyPane.start.setOnMousePressed(e -> lobbyPane.lobbyBtnPressed(e));
        lobbyPane.start.setOnMouseReleased(e -> lobbyPane.lobbyBtnReleased(e));

        lobbyPane.quit.setOnMousePressed(e -> lobbyPane.lobbyBtnPressed(e));
        lobbyPane.quit.setOnMouseReleased(e -> lobbyPane.lobbyBtnReleased(e)); // lobby quit

        gameState = GameState.LOBBY;
        lobbyPane.draw();

        // ButtleField
        borderPane = new BorderPane();
        battleScene = new Scene(borderPane);

        borderPane.setPrefSize(800, 600);

        topPane.setAlignment(Pos.CENTER);
        topPane.setSpacing(50);
        topPane.setStyle("-fx-background-color: linear-gradient(white, white, white, lightgray);" +
                "-fx-border-width: 0 0 0.5px 0;" +
                "-fx-border-color: gray;");
        borderPane.setTop(topPane);

        bottomPane.setAlignment(Pos.CENTER);
        bottomPane.setSpacing(40);
        bottomPane.setStyle("-fx-background-color: linear-gradient(lightgray, white, white, white);" +
                "-fx-border-width: 0.5px 0 0 0;" +
                "-fx-border-color: gray;");

        borderPane.setBottom(bottomPane);

//        leftPane.setStyle("-fx-background-color: lightgray;");
        borderPane.setLeft(leftPane);
//        rightPane.setStyle("-fx-background-color: lightgray;");
        borderPane.setRight(rightPane);

        battleField = new BattleField(this);
        borderPane.setCenter(battleField);

        borderPane.widthProperty().addListener((observableValue, number, t1) -> {
            setCellSize();
            battleField.update();
            updateMargin();
        });

        borderPane.heightProperty().addListener((observableValue, number, t1) -> {
            setCellSize();
            battleField.update();
            updateMargin();
        });

        quit.setOnAction(e -> btnAction(quit)); // game quit

        Timeline animation = new Timeline(new KeyFrame(new Duration(20), e -> {
            if (gameState == GameState.LOBBY) {
                lobbyPane.update();
            } else if (gameState == GameState.GAME){
                battleField.update();
                updateTimeLabel();
                updatePlayer1Label();
                updatePlayer2Label();
            }
        }));
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();

        Timeline timeline = new Timeline(new KeyFrame(new Duration(1000), e -> {
            if (gameState == GameState.GAME){
                time++;
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        stage.setTitle("Tanks2D");
        stage.setScene(lobbyScene);
        stage.show();
    }

    private void updateTimeLabel() {
        lbTime.setText("Time: " + time + " / " + timeLimit);
    }

    private void updatePlayer1Label() {
        lbPlayer1.setText("Player 1: " + "killcount" + " / " + "goal");
    }

    private void updatePlayer2Label() {
        lbPlayer2.setText("Player 2: " + "killcount" + " / " + "goal");
    }

    public void btnAction(Button btn){
        if (btn == lobbyPane.start) {
            gameState = GameState.GAME;
            stage.setScene(battleScene);

            generateFieldDimensions();
            setCellSize();
            fillUpFieldWithCells();
            updateMargin();

            updateTimeLabel();
            updatePlayer1Label();
            updatePlayer2Label();

            battleField.draw();
        }
        else if (btn == lobbyPane.quit || btn == quit) {
            Platform.exit();
        }
    }

    private void updateMargin() {
        double horizontalMargin = (borderPane.getWidth() - battleField.getPrefWidth()) / 2.0;
        double verticalMargin = (borderPane.getHeight() - battleField.getPrefHeight()) / 2.0;
        topPane.setPrefHeight(verticalMargin);
        bottomPane.setPrefHeight(verticalMargin);
        leftPane.setPrefWidth(horizontalMargin);
        rightPane.setPrefWidth(horizontalMargin);
    }

    private void setCellSize(){
        double centralPaneHeight = borderPane.getHeight() - 100;
        double centralPaneWidth = borderPane.getWidth();
        double centralPaneRatio = (double) centralPaneWidth / centralPaneHeight;

        double battleFieldRatio = (double) fieldWidth / fieldHeight;
        if (battleFieldRatio > centralPaneRatio) {
            cellSize = centralPaneWidth / fieldWidth;
            battleField.setPrefWidth(centralPaneWidth);
            battleField.setPrefHeight(fieldHeight * cellSize);
        } else {
            cellSize = centralPaneHeight / fieldHeight;
            battleField.setPrefWidth(fieldWidth * cellSize);
            battleField.setPrefHeight(centralPaneHeight);
        }
    }

    public void generateFieldDimensions() {
        Random rand = new Random();
        fieldWidth = dimensions[rand.nextInt(dimensions.length)];
        fieldHeight = dimensions[rand.nextInt(dimensions.length)];
        System.out.println("fieldWidth: " + fieldWidth);
        System.out.println("fieldHeight: " + fieldHeight);
        field = new Cell[fieldWidth][fieldHeight];
    }

    public void fillUpFieldWithCells(){
        for (int x = 0; x < fieldWidth; x++) {
            for (int y = 0; y < fieldHeight; y++) {
                field[x][y] = new Cell(x, y, cellSize);
            }
        }
    }
}
