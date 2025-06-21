package com.example.projecttanks;

import javafx.application.Platform;
import javafx.event.EventTarget;
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

public class UI extends Pane {
    MainGame game;

    public Button start = new Button("Start");
    public Button quit = new Button("Quit");

    private final Background lobbyBtnBackground = new Background(new BackgroundFill(Color.DARKGREEN, new CornerRadii(5), null));
    private final Font lobbyBtnFont = Font.font("System", FontWeight.BOLD, 16);
    private final Color lobbyBtnColor = Color.BLACK;
    public final DropShadow shadow = new DropShadow(5, 3, 3, Color.BLACK);

    private final double lobbyBtnWidth = 120;
    private final double lobbyBtnHeight = 50;
    private static final double GAP_BETWEEN_BUTTONS = 50;

    private Rectangle lobbyBG = new Rectangle(0, 0);


    UI(MainGame game) {
        this.game = game;
    }

    public void draw() {
        // Lobby
        if (game.gameState == GameState.LOBBY) {
            lobbyBG();
            drawLobbyBtns();
            drawLobbyText();
        }
        // Game field
        if (game.gameState == GameState.GAME) {
            for (int x = 0; x < game.fieldWidth; x++) {
                for (int y = 0; y < game.fieldHeight; y++) {
                    getChildren().addAll(game.field[x][y].initElements(game.offset));
                }
            }
        }
        // Pause message
        // End info


    }

    private void lobbyBG() {
        lobbyBG.widthProperty().bind(game.pane.widthProperty());
        lobbyBG.heightProperty().bind(game.pane.heightProperty());
        lobbyBG.setFill(Color.rgb(70, 120, 80));
        game.pane.getChildren().add(lobbyBG);
    }

    private void drawLobbyText() {
        Text text = new Text("Tanks Battle!");
        text.setFill(Color.BLACK);
        text.setStrokeWidth(2);
        text.setFont(Font.font("System", FontWeight.BOLD, 50));
        text.setLayoutX(game.pane.getWidth() / 2 - text.getLayoutBounds().getWidth() / 2);
        text.setLayoutY(game.pane.getHeight() / 2 - text.getLayoutBounds().getHeight());
        game.pane.getChildren().add(text);
    }

    private void drawLobbyBtns(){
        double shadowGap = 5;

        double startPosX = game.pane.getWidth() / 2 - lobbyBtnWidth - GAP_BETWEEN_BUTTONS / 2;
        drawBtn(shadowGap, startPosX, start);

        double quitPosX = game.pane.getWidth() / 2 + GAP_BETWEEN_BUTTONS / 2;
        drawBtn(shadowGap, quitPosX, quit);
    }

    private void drawBtn(double shadowGap, double posX, Button btn) {
        double quitPosY = game.pane.getHeight() / 2 - lobbyBtnHeight / 2;

        btn.setPrefWidth(lobbyBtnWidth);
        btn.setPrefHeight(lobbyBtnHeight);
        btn.setBackground(lobbyBtnBackground);
        btn.setFont(lobbyBtnFont);
        btn.setTextFill(lobbyBtnColor);
        btn.setLayoutX(posX - shadowGap);
        btn.setLayoutY(quitPosY - shadowGap);
        btn.setEffect(shadow);
        game.pane.getChildren().add(btn);
    }

    public void lobbyBtnPressed(MouseEvent e) {
        Button btn = (Button) e.getSource();
        btn.setLayoutX(btn.getLayoutX() + 3);
        btn.setLayoutY(btn.getLayoutY() + 3);
        btn.setEffect(null);
    }

    public void lobbyBtnReleased(MouseEvent e) {
        Button btn = (Button) e.getSource();
        btn.setLayoutX(btn.getLayoutX() - 3);
        btn.setLayoutY(btn.getLayoutY() - 3);
        btn.setEffect(shadow);
        game.btnAction(btn);
    }

    public void update() {
    }
}
