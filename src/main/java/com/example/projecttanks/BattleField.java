package com.example.projecttanks;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.List;

public class BattleField extends Pane {
    MainGame game;

    BattleField(MainGame game) {
        this.game = game;
    }

    public void draw(){
        if (game.gameState == GameState.GAME) {
            for (int x = 0; x < game.fieldWidth; x++) {
                for (int y = 0; y < game.fieldHeight; y++) {
                    List<Rectangle> a = game.field[x][y].initElements();
                    getChildren().addAll(a);
                }
            }
        }
    }

    public void update() {
        for (int x = 0; x < game.fieldWidth; x++) {
            for (int y = 0; y < game.fieldHeight; y++) {
                game.field[x][y].updateSize(game.cellSize);
            }
        }
    }
}
