package com.example.projecttanks;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainGame extends Application {
    public static int LOBBY_WIDTH = 400;
    public static int LOBBY_HEIGHT = 300;

    public GameState gameState = GameState.LOBBY;
    UI pane;
    Button save = new Button("Save");


    @Override
    public void start(Stage stage) throws Exception {
        pane = new UI(this);
        Scene scene = new Scene(pane, LOBBY_WIDTH * 2, LOBBY_HEIGHT * 2);
        stage.setMinWidth(LOBBY_WIDTH);
        stage.setMinHeight(LOBBY_HEIGHT);

        pane.start.setOnMousePressed(e -> pane.lobbyBtnPressed(e));
        pane.start.setOnMouseReleased(e -> pane.lobbyBtnReleased(e));

        pane.quit.setOnMousePressed(e -> pane.lobbyBtnPressed(e));
        pane.quit.setOnMouseReleased(e -> pane.lobbyBtnReleased(e));

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

}
