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
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;


public class MainGame extends Application {
    public static int LOBBY_WIDTH = 1000;
    public static int LOBBY_HEIGHT = 600;
    public static double REFRESH_TIME_MS = 16;

    public AppState appState;
    LobbyPane lobbyPane;
    Scene lobbyScene;
    BorderPane borderPane = new BorderPane();
    BattleField battleField;
    Scene battleScene;
    Stage stage;

    private Rectangle winTable = new Rectangle();
    private Text winText = new Text();

    Button quit = new Button("Quit");
    Button generate = new Button("Generate");
    Button newRound = new Button("New round");
    Button home = new Button("Home");
    Label lbPlayer1 = new Label();
    Label lbTime = new Label();
    Label lbPlayer2 = new Label();

    HBox topPane = new HBox(lbPlayer1, lbTime, lbPlayer2);
    HBox bottomPane = new HBox(home, newRound, generate, quit);

    VBox leftPane = new VBox();
    VBox rightPane = new VBox();

    public int time = 0;
    public int timeLimit = 60;

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

        appState = AppState.LOBBY;
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

        borderPane.widthProperty().addListener((observableValue, previous, current) -> {
            battleField.update();
            updateMargin();
        });

        borderPane.heightProperty().addListener((observableValue, previous, current) -> {
            battleField.update();
            updateMargin();
        });

        quit.setOnAction(e -> btnAction(quit));
        generate.setOnAction(e -> btnAction(generate));
        newRound.setOnAction(e -> btnAction(newRound));
        home.setOnAction(e -> btnAction(home));

        setWinWindow();

        Timeline animation = getAnimation();
        animation.play();

        Timeline timeline = new Timeline(new KeyFrame(new Duration(1000), e -> {
            if (appState == AppState.GAME){
                time++;
                if (battleField.gameState.isRoundOver) battleField.gameState.decrementCountDown();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        setKeyEvents();

        stage.setTitle("Tanks2D");
        stage.setScene(lobbyScene);
        stage.show();
    }

    private void setWinWindow() {
        double width = 200;
        double height = 80;
        winTable.xProperty().bind(battleField.widthProperty().divide(2).subtract(width / 2));
        winTable.yProperty().bind(battleField.heightProperty().divide(2).subtract(height / 2));

        winTable.setWidth(width);
        winTable.setHeight(height);

        winTable.setFill(Color.WHITE);
        winTable.setStroke(Color.BLACK);

        winText.setFont(new Font(20));
        winText.xProperty().bind(battleField.widthProperty().divide(2).subtract(width / 2 - 25));
        winText.yProperty().bind(battleField.heightProperty().divide(2).add(5));
    }

    private Timeline getAnimation() {
        Timeline animation = new Timeline(new KeyFrame(new Duration(REFRESH_TIME_MS), e -> {
            if (appState == AppState.LOBBY) {
                lobbyPane.update();
            } else if (appState == AppState.GAME){
                battleField.firstPlayer.move(REFRESH_TIME_MS / 1000);
                battleField.firstPlayer.updateBullets(REFRESH_TIME_MS / 1000);

                battleField.secondPlayer.move(REFRESH_TIME_MS / 1000);
                battleField.secondPlayer.updateBullets(REFRESH_TIME_MS / 1000);

                updateLabels();
            } else if (appState == AppState.END){
                updateLabels();
            }
        }));
        animation.setCycleCount(Timeline.INDEFINITE);
        return animation;
    }

    private void updateTimeLabel() {
        lbTime.setTextFill(Color.BLACK);
        lbTime.setText("Time: " + time);
        if (battleField.gameState.isRoundOver) {
            lbTime.setTextFill(Color.RED);
            lbTime.setText("Time: " + battleField.gameState.countDown);
        }
    }

    private void updatePlayer1Label() {
        lbPlayer1.setText("Player 1: " + battleField.gameState.roundsWon1 + " / " + GameState.ROUNDS_TO_WIN);
    }

    private void updatePlayer2Label() {
        lbPlayer2.setText("Player 2: " + battleField.gameState.roundsWon2 + " / " + GameState.ROUNDS_TO_WIN);
    }

    public void btnAction(Button btn){
        if (btn == lobbyPane.start) {
            stage.setScene(battleScene);
            battleField.initGameState();
            startNewRound();
            unfocus();
        }
        else if (btn == generate) {
            resetTime();
            battleField.gameState.evaluateRound();
            if (appState == AppState.END) return;
            battleField.generateBtn();
            unfocus();
        }
        else if (btn == newRound) {
            battleField.gameState.evaluateRound();
            if (appState == AppState.END) return;
            battleField.getChildren().clear();
            startNewRound();
            unfocus();
        }
        else if (btn == home) {
            appState = AppState.LOBBY;
            battleField.getChildren().clear();
            stage.setScene(lobbyScene);
        }
        else if (btn == lobbyPane.quit || btn == quit) {
            Platform.exit();
        }
    }

    public void unfocus(){
        battleField.requestFocus();
    }

    public void startNewRound(){
        appState = AppState.PAUSE;
        battleField.initField();

        updateMargin();
        battleField.draw();
        battleField.initPlayers();
        resetTime();
        updateLabels();
        appState = AppState.GAME;
    }

    private void updateLabels(){
        unfocus();
        updateTimeLabel();
        updatePlayer1Label();
        updatePlayer2Label();
    }

    private void resetTime() {
        time = 0;
    }

    private void updateMargin() {
        double horizontalMargin = (borderPane.getWidth() - battleField.getPrefWidth()) / 2.0;
        double verticalMargin = (borderPane.getHeight() - battleField.getPrefHeight()) / 2.0;
        topPane.setPrefHeight(verticalMargin);
        bottomPane.setPrefHeight(verticalMargin);
        leftPane.setPrefWidth(horizontalMargin);
        rightPane.setPrefWidth(horizontalMargin);
    }

    private void setKeyEvents() {
        battleScene.setOnKeyPressed(event -> {
            if (appState == AppState.GAME) {
                switch (event.getCode()) {
                    case LEFT:
                        battleField.secondPlayer.startRotationL();
                        break;
                    case UP:
                        battleField.secondPlayer.startMovementForward();
                        break;
                    case DOWN:
                        battleField.secondPlayer.startMovementBackwards();
                        break;
                    case RIGHT:
                        battleField.secondPlayer.startRotationR();
                        break;
                    case ENTER:
                        battleField.secondPlayer.shoot();
                        break;

                    case A:
                        battleField.firstPlayer.startRotationL();
                        break;
                    case W:
                        battleField.firstPlayer.startMovementForward();
                        break;
                    case S:
                        battleField.firstPlayer.startMovementBackwards();
                        break;
                    case D:
                        battleField.firstPlayer.startRotationR();
                        break;
                    case Q:
                        battleField.firstPlayer.shoot();
                        break;
                }
            }
        });
        battleScene.setOnKeyReleased(event -> {
            if (appState == AppState.GAME) {
                switch (event.getCode()) {
                    case LEFT:
                        battleField.secondPlayer.stopRotationL();
                        break;
                    case RIGHT:
                        battleField.secondPlayer.stopRotationR();
                        break;
                    case UP:
                    case DOWN:
                        battleField.secondPlayer.stopMovement();
                        break;

                    case A:
                        battleField.firstPlayer.stopRotationL();
                        break;
                    case D:
                        battleField.firstPlayer.stopRotationR();
                        break;
                    case W:
                    case S:
                        battleField.firstPlayer.stopMovement();
                        break;
                }
            }
        });
    }

    public void endTheGame(Player winner) {
        appState = AppState.END;

        winText.setText("Player " + winner + " won!" );
        battleField.getChildren().removeAll(winTable, winText);
        battleField.getChildren().addAll(winTable, winText);
    }
}
