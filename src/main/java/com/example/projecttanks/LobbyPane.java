package com.example.projecttanks;

import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * The lobby screen for the Tanks2D game application.
 * Displays the name of the game and two buttons allowing to start the game of to quit the application.
 */
public class LobbyPane extends Pane {
    private final MainGame game;

    private final Rectangle lobbyBG = new Rectangle(0, 0);
    private final Text text = new Text("Tanks Battle!");
    private final Button start = new Button("Start");
    private final Button quit = new Button("Quit");

    private static final double GAP_BETWEEN_BUTTONS = 50;
    private final double lobbyBtnWidth = 120;
    private final double lobbyBtnHeight = 50;
    private double shadowOffsetStartBtn = 5;
    private double shadowOffsetQuitBtn = 5;

    private final Background lobbyBtnBackground = new Background(new BackgroundFill(Color.DARKGREEN, new CornerRadii(5), null));
    private final Font lobbyBtnFont = Font.font("System", FontWeight.BOLD, 16);
    private final Color lobbyBtnColor = Color.BLACK;
    private final DropShadow shadow = new DropShadow(5, 3, 3, Color.BLACK);

    /**
     * Constructs the lobby pane.
     * Saves reference to the main game instance.
     * Binds mouse event handlers to the "Start" and "Quit" buttons.
     * Sets {@link AppState} value to Lobby.
     * Draws background, text and buttons on the scene
     * @param game the main game instance
     */
    LobbyPane(MainGame game) {
        this.game = game;

        start.setOnMousePressed(this::lobbyBtnPressed);
        start.setOnMouseReleased(this::lobbyBtnReleased);

        quit.setOnMousePressed(this::lobbyBtnPressed);
        quit.setOnMouseReleased(this::lobbyBtnReleased);

        game.setAppState(AppState.LOBBY);
        draw();
    }

    private void draw() {
        if (game.getAppState() == AppState.LOBBY) {
            buttonInit(start);
            buttonInit(quit);
            update();

            getChildren().add(lobbyBG);
            getChildren().add(start);
            getChildren().add(quit);
            getChildren().add(text);
        }
    }

    private void colorBG() {
        lobbyBG.setFill(Color.rgb(70, 120, 80));
        lobbyBG.setWidth(getWidth());
        lobbyBG.setHeight(getHeight());
    }

    private void updateLobbyText() {
        text.setFill(Color.BLACK);
        text.setStrokeWidth(2);
        text.setFont(Font.font("System", FontWeight.BOLD, 50));
        text.setLayoutX(getWidth() / 2 - text.getLayoutBounds().getWidth() / 2);
        text.setLayoutY(getHeight() / 2 - text.getLayoutBounds().getHeight());
    }

    private void buttonInit(Button btn) {
        btn.setPrefWidth(lobbyBtnWidth);
        btn.setPrefHeight(lobbyBtnHeight);
        btn.setBackground(lobbyBtnBackground);
        btn.setFont(lobbyBtnFont);
        btn.setTextFill(lobbyBtnColor);
        btn.setEffect(shadow);
    }

    private void updateAllBtns(){
        double posY = getHeight() / 2 - lobbyBtnHeight / 2;

        double startPosX = getWidth() / 2 - lobbyBtnWidth - GAP_BETWEEN_BUTTONS / 2;
        updateBtn(startPosX, posY, start, shadowOffsetStartBtn);

        double quitPosX = getWidth() / 2 + GAP_BETWEEN_BUTTONS / 2;
        updateBtn(quitPosX, posY, quit, shadowOffsetQuitBtn);
    }

    private void updateBtn(double posX, double posY, Button btn, double offset) {
        btn.setLayoutX(posX - offset);
        btn.setLayoutY(posY - offset);
    }

    private void lobbyBtnPressed(MouseEvent e) {
        Button btn = (Button) e.getSource();
        if (btn == start) {
            shadowOffsetStartBtn = 0;
        } else if (btn == quit) {
            shadowOffsetQuitBtn = 0;
        }
        btn.setEffect(null);
    }

    private void lobbyBtnReleased(MouseEvent e) {
        Button btn = (Button) e.getSource();
        if (btn == start) {
            shadowOffsetStartBtn = 5;
        } else if (btn == quit) {
            shadowOffsetQuitBtn = 5;
        }
        btn.setEffect(shadow);
        game.btnAction(btn.getText());
    }

    /**
     * Updates background size, buttons and text position to fit the window.
     * Used for resizing.
     */
    public void update() {
        colorBG();
        updateLobbyText();
        updateAllBtns();
    }
}
