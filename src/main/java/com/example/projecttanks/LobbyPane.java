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

public class LobbyPane extends Pane {
    MainGame game;

    public Button start = new Button("Start");
    public Button quit = new Button("Quit");
    private Rectangle lobbyBG = new Rectangle(0, 0);
    Text text = new Text("Tanks Battle!");
    double shadowOffsetStartBtn = 5;
    double shadowOffsetQuitBtn = 5;

    private final Background lobbyBtnBackground = new Background(new BackgroundFill(Color.DARKGREEN, new CornerRadii(5), null));
    private final Font lobbyBtnFont = Font.font("System", FontWeight.BOLD, 16);
    private final Color lobbyBtnColor = Color.BLACK;
    public final DropShadow shadow = new DropShadow(5, 3, 3, Color.BLACK);

    private final double lobbyBtnWidth = 120;
    private final double lobbyBtnHeight = 50;
    private static final double GAP_BETWEEN_BUTTONS = 50;


    LobbyPane(MainGame game) {
        this.game = game;
    }

    public void draw() {
        if (game.appState == AppState.LOBBY) {
            buttonInit(start);
            buttonInit(quit);
            update();

            game.lobbyPane.getChildren().add(lobbyBG);
            game.lobbyPane.getChildren().add(start);
            game.lobbyPane.getChildren().add(quit);
            game.lobbyPane.getChildren().add(text);
        }
    }

    private void colorBG() {
        lobbyBG.setFill(Color.rgb(70, 120, 80));
        lobbyBG.setWidth(game.lobbyPane.getWidth());
        lobbyBG.setHeight(game.lobbyPane.getHeight());
    }

    private void updateLobbyText() {
        text.setFill(Color.BLACK);
        text.setStrokeWidth(2);
        text.setFont(Font.font("System", FontWeight.BOLD, 50));
        text.setLayoutX(game.lobbyPane.getWidth() / 2 - text.getLayoutBounds().getWidth() / 2);
        text.setLayoutY(game.lobbyPane.getHeight() / 2 - text.getLayoutBounds().getHeight());
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
        double posY = game.lobbyPane.getHeight() / 2 - lobbyBtnHeight / 2;

        double startPosX = game.lobbyPane.getWidth() / 2 - lobbyBtnWidth - GAP_BETWEEN_BUTTONS / 2;
        updateBtn(startPosX, posY, start, shadowOffsetStartBtn);

        double quitPosX = game.lobbyPane.getWidth() / 2 + GAP_BETWEEN_BUTTONS / 2;
        updateBtn(quitPosX, posY, quit, shadowOffsetQuitBtn);
    }

    private void updateBtn(double posX, double posY, Button btn, double offset) {
        btn.setLayoutX(posX - offset);
        btn.setLayoutY(posY - offset);
    }

    public void lobbyBtnPressed(MouseEvent e) {
        Button btn = (Button) e.getSource();
        if (btn == start) {
            shadowOffsetStartBtn = 0;
        } else if (btn == quit) {
            shadowOffsetQuitBtn = 0;
        }
        btn.setEffect(null);
    }

    public void lobbyBtnReleased(MouseEvent e) {
        Button btn = (Button) e.getSource();
        if (btn == start) {
            shadowOffsetStartBtn = 5;
        } else if (btn == quit) {
            shadowOffsetQuitBtn = 5;
        }
        btn.setEffect(shadow);
        game.btnAction(btn);
    }

    public void update() {
        colorBG();
        updateLobbyText();
        updateAllBtns();
    }
}
