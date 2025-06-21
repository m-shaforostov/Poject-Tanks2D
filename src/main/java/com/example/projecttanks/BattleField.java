package com.example.projecttanks;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.List;
import java.util.Random;

public class BattleField extends Pane {
    MainGame game;
    public Cell[][] field;
    private static int[] dimensions = new int[]{3, 4, 5, 6, 7, 8, 9, 10};
    public int fieldWidth;
    public int fieldHeight;
    public double cellSize;

    BattleField(MainGame game) {
        this.game = game;
    }

    public void draw(){
        if (game.gameState == GameState.GAME) {
            for (int x = 0; x < fieldWidth; x++) {
                for (int y = 0; y < fieldHeight; y++) {
                    List<Rectangle> a = field[x][y].initElements();
                    getChildren().addAll(a);
                }
            }
        }
    }

    public void update() {
        for (int x = 0; x < fieldWidth; x++) {
            for (int y = 0; y < fieldHeight; y++) {
                field[x][y].updateSize(cellSize);
            }
        }
    }

    public void setCellSize(){
        double centralPaneHeight = game.borderPane.getHeight() - 100;
        double centralPaneWidth = game.borderPane.getWidth();
        double centralPaneRatio = (double) centralPaneWidth / centralPaneHeight;

        double battleFieldRatio = (double) fieldWidth / fieldHeight;
        if (battleFieldRatio > centralPaneRatio) {
            cellSize = centralPaneWidth / fieldWidth;
            setPrefWidth(centralPaneWidth);
            setPrefHeight(fieldHeight * cellSize);
        } else {
            cellSize = centralPaneHeight / fieldHeight;
            setPrefWidth(fieldWidth * cellSize);
            setPrefHeight(centralPaneHeight);
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
