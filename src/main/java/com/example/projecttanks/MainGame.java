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
import javafx.stage.Stage;
import javafx.util.Duration;


public class MainGame extends Application {
    public static int LOBBY_WIDTH = 1000;
    public static int LOBBY_HEIGHT = 600;

    public GameState gameState;
    LobbyPane lobbyPane;
    Scene lobbyScene;
    BorderPane borderPane = new BorderPane();
    BattleField battleField;
    Scene battleScene;
    Stage stage;

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

        borderPane.widthProperty().addListener((observableValue, previous, current) -> {
            battleField.setCellSize();
            battleField.update();
            updateMargin();
        });

        borderPane.heightProperty().addListener((observableValue, previous, current) -> {
            battleField.setCellSize();
            battleField.update();
            updateMargin();
        });

        quit.setOnAction(e -> btnAction(quit));
        generate.setOnAction(e -> btnAction(generate));
        newRound.setOnAction(e -> btnAction(newRound));
        home.setOnAction(e -> btnAction(home));

        Timeline animation = getAnimation();
        animation.play();

        Timeline timeline = new Timeline(new KeyFrame(new Duration(1000), e -> {
            if (gameState == GameState.GAME){
                time++;
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        setKeyEvents();

        stage.setTitle("Tanks2D");
        stage.setScene(lobbyScene);
        stage.show();
    }

    private Timeline getAnimation() {
        Timeline animation = new Timeline(new KeyFrame(new Duration(20), e -> {
            if (gameState == GameState.LOBBY) {
                lobbyPane.update();
            } else if (gameState == GameState.GAME){
                battleField.firstPlayer.move(0.02);
                battleField.secondPlayer.move(0.02);
                battleField.update();
                unfocus();
                updateTimeLabel();
                updatePlayer1Label();
                updatePlayer2Label();
            }
        }));
        animation.setCycleCount(Timeline.INDEFINITE);
        return animation;
    }

    private void updateTimeLabel() {
//        lbTime.setText("Time: " + time + " / " + timeLimit);
        lbTime.setText("");

    }

    private void updatePlayer1Label() {
        if (battleField.firstPlayer.position != null)
            lbPlayer1.setText("position1: " + battleField.firstPlayer.position.x + " , " +
                battleField.firstPlayer.position.y + " angle: " + battleField.firstPlayer.angle);
    }

    private void updatePlayer2Label() {
//        lbPlayer2.setText("Player 2: " + "killcount" + " / " + "goal");
        if (battleField.firstPlayer.velocity != null)
            lbPlayer2.setText("velocity: " + battleField.firstPlayer.velocity.x + " , " +
                battleField.firstPlayer.velocity.y + " speed: " + battleField.firstPlayer.speed);
    }

    public void btnAction(Button btn){
        if (btn == lobbyPane.start) {
            stage.setScene(battleScene);
            startNewRound();
            unfocus();
        }
        else if (btn == generate) {
            resetTime();
            battleField.generateBtn();
            unfocus();
        }
        else if (btn == newRound) {
            battleField.getChildren().clear();
            startNewRound();
            unfocus();
        }
        else if (btn == home) {
            gameState = GameState.LOBBY;
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
        gameState = GameState.PAUSE;
        battleField.initField();

        updateMargin();
        battleField.draw();
        battleField.initPlayers();
        updateLabels();
        gameState = GameState.GAME;
    }

    private void updateLabels(){
        resetTime();
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
            if (gameState == GameState.GAME) {
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
            if (gameState == GameState.GAME) {
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
}
