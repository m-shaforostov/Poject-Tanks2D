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

/**
 * Main class for the Tanks2D game application.
 * It sets up a lobby pane and a scene for it,
 * border pane, which contains top, bottom, left and right Boxes,
 * top box - for game data (won rounds for each player and time of current round)
 * bottom box contains four buttons
 * left and right boxes (as well as top and bottom) are used for smooth window resizing.
 * There is also a middle zone where is located a battlefield (map and tanks)
 * This class handles some resizing updates, game state and key presses.
 * @author m-shaforostov
 */

public class MainGame extends Application {
    /** Time interval for screen refresh in milliseconds */
    public static double REFRESH_TIME_MS = 16;

    private static final int LOBBY_WIDTH = 1000;
    private static final int LOBBY_HEIGHT = 600;

    private AppState appState;
    private LobbyPane lobbyPane;
    private Scene lobbyScene;
    private BorderPane borderPane = new BorderPane();
    private BattleField battleField;
    private Scene battleScene;
    private Stage stage;

    private final Rectangle winTable = new Rectangle();
    private final Text winText = new Text();

    private final Button quit = new Button("Quit");
    private final Button generate = new Button("Generate");
    private final Button newRound = new Button("New round");
    private final Button home = new Button("Home");
    private final Label lbPlayer1 = new Label();
    private final Label lbTime = new Label();
    private final Label lbPlayer2 = new Label();

    private final HBox topPane = new HBox(lbPlayer1, lbTime, lbPlayer2);
    private final HBox bottomPane = new HBox(home, newRound, generate, quit);

    private final VBox leftPane = new VBox();
    private final VBox rightPane = new VBox();

    private int time = 0;

    /**
     * Starts the JavaFx application.
     * @param stage the primary stage for this application
     * @throws Exception if the application fails to start
     */
    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        this.stage.setMinWidth((double) LOBBY_WIDTH / 2);
        this.stage.setMinHeight((double) LOBBY_HEIGHT / 2);

        lobbyPane = new LobbyPane(this);
        lobbyScene = new Scene(lobbyPane, LOBBY_WIDTH, LOBBY_HEIGHT);

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

        borderPane.setLeft(leftPane);
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

        quit.setOnAction(e -> btnAction(quit.getText()));
        generate.setOnAction(e -> btnAction(generate.getText()));
        newRound.setOnAction(e -> btnAction(newRound.getText()));
        home.setOnAction(e -> btnAction(home.getText()));

        setWinWindow();

        Timeline animation = getAnimation();
        animation.play();

        Timeline timeline = new Timeline(new KeyFrame(new Duration(1000), e -> {
            if (getAppState() == AppState.GAME){
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

    private void updateLabels(){
        defocus();
        updateTimeLabel();
        updatePlayer1Label();
        updatePlayer2Label();
    }

    /**
     * Is used to remove focus from the buttons displayed to {@link BattleField}.
     * That allows to detect arrow key presses ("UP", "DOWN", "LEFT", "RIGHT")
     * And to avoid focus movement between the buttons instead.
     */
    public void defocus(){
        battleField.requestFocus();
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

    private void updateMargin() {
        double horizontalMargin = (borderPane.getWidth() - battleField.getPrefWidth()) / 2.0;
        double verticalMargin = (borderPane.getHeight() - battleField.getPrefHeight()) / 2.0;
        topPane.setPrefHeight(verticalMargin);
        bottomPane.setPrefHeight(verticalMargin);
        leftPane.setPrefWidth(horizontalMargin);
        rightPane.setPrefWidth(horizontalMargin);
    }

    /**
     * Handles button action based on the buttons text provided.
     * It is used as a centralized controller for button events.
     * @param btnText the text of the button which was pressed ("Start", "Generate", "New round", "Home", "Quit")
     */
    public void btnAction(String btnText){
        switch (btnText) {
            case "Start" -> {
                stage.setScene(battleScene);
                battleField.initGameState();
                startNewRound();
                defocus();
            }
            case "Generate" -> {
                resetTime();
                battleField.gameState.evaluateRound();
                if (getAppState() == AppState.END) return;
                battleField.generateBtn();
                defocus();
            }
            case "New round" -> {
                battleField.gameState.evaluateRound();
                if (getAppState() == AppState.END) return;
                startNewRound();
                defocus();
            }
            case "Home" -> {
                setAppState(AppState.LOBBY);
                battleField.getChildren().clear();
                stage.setScene(lobbyScene);
            }
            case "Quit" -> Platform.exit();
        }
    }

    private void startNewRound(){
        setAppState(AppState.PAUSE);
        battleField.initField();

        updateMargin();
        battleField.draw();
        battleField.initPlayers();
        resetTime();
        updateLabels();
        setAppState(AppState.GAME);
    }

    /**
     * Ends the game by setting {@link AppState} value
     * Displays winner of the game on the table
     * @param winner the player who won the game
     */
    public void displayWinner(Player winner) {
        setAppState(AppState.END);

        winText.setText("Player " + winner + " won!" );
        battleField.getChildren().removeAll(winTable, winText);
        battleField.getChildren().addAll(winTable, winText);
    }

    private void setKeyEvents() {
        battleScene.setOnKeyPressed(event -> {
            if (getAppState() == AppState.GAME) {
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
            if (getAppState() == AppState.GAME) {
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

    /**
     * Sets the current state of the application
     * @param state the new {@link AppState}
     */
    public void setAppState(AppState state) { appState = state; }

    private void resetTime() {
        time = 0;
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

    /**
     * Returns the current width of the BorderPane int the game scene.
     * @return the width of the BorderPane
     */
    public double getBorderpaneWidth() { return borderPane.getWidth(); }

    /**
     * Returns the current height of the BorderPane int the game scene.
     * @return the height of the BorderPane
     */
    public double getBorderpaneHeight() { return borderPane.getHeight(); }

    /**
     * Returns current state of the application
     * @return the current {@link AppState}
     */
    public AppState getAppState() { return appState; }

    private Timeline getAnimation() {
        Timeline animation = new Timeline(new KeyFrame(new Duration(REFRESH_TIME_MS), e -> {
            if (getAppState() == AppState.LOBBY) {
                lobbyPane.update();
            } else if (getAppState() == AppState.GAME){
                battleField.firstPlayer.move(REFRESH_TIME_MS / 1000);
                battleField.firstPlayer.updateBullets(REFRESH_TIME_MS / 1000);

                battleField.secondPlayer.move(REFRESH_TIME_MS / 1000);
                battleField.secondPlayer.updateBullets(REFRESH_TIME_MS / 1000);

                updateLabels();
            } else if (getAppState() == AppState.END){
                updateLabels();
            }
        }));
        animation.setCycleCount(Timeline.INDEFINITE);
        return animation;
    }

}
