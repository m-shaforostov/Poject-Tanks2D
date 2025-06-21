package com.example.projecttanks;

import javafx.scene.layout.Pane;

public class ButtleField extends Pane {
    MainGame game;

    ButtleField(MainGame game) {
        this.game = game;
    }

    public void draw(){
        if (game.gameState == GameState.GAME) {
            for (int x = 0; x < game.fieldWidth; x++) {
                for (int y = 0; y < game.fieldHeight; y++) {
                    getChildren().addAll(game.field[x][y].initElements(game.offset));
                }
            }
        }
    }
}
