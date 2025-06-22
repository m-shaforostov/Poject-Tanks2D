package com.example.projecttanks;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Collections;
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
            List<Rectangle> walls = new ArrayList<>();
            for (int x = 0; x < fieldWidth; x++) {
                for (int y = 0; y < fieldHeight; y++) {
                    List<Rectangle> elements = field[x][y].initElements();
                    getChildren().addAll(elements.removeFirst()); // draw the cell
                    walls.addAll(elements); // save cell's walls
                }
            }
            getChildren().addAll(walls); // draw the walls
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
        fillUpFieldWithCells();
        setCellSize();
        generateMaze();
    }

    private void fillUpFieldWithCells(){
        for (int x = 0; x < fieldWidth; x++) {
            for (int y = 0; y < fieldHeight; y++) {
                field[x][y] = new Cell(x, y, cellSize);
            }
        }
    }


    public void setCellsConnection(boolean isConnected, Cell cell1, Cell cell2){
        int x1 = cell1.getX();
        int y1 = cell1.getY();
        int x2 = cell2.getX();
        int y2 = cell2.getY();
        if (Math.abs(x1 - x2) > 1 && Math.abs(y1 - y2) > 1) return;
        if (x1 == x2 && y1 == y2) return;
        if (x1 != x2 && y1 != y2) return;
        if (x1 < x2) {
            cell1.setWallRight(isConnected);
            cell2.setWallLeft(isConnected);
        } else if (x1 > x2){
            cell1.setWallLeft(isConnected);
            cell2.setWallRight(isConnected);
        } else if (y1 < y2){
            cell1.setWallBottom(isConnected);
            cell2.setWallTop(isConnected);
        } else if (y1 > y2){
            cell1.setWallTop(isConnected);
            cell2.setWallBottom(isConnected);}
    }

    private void generateMaze() {
        Cell start = getStartCell();
        backTrack(null, start);
    }

    private void backTrack(Cell previous, Cell current) {
        if (previous != null) setCellsConnection(false, previous, current);
        current.setVisited();
        List<Cell> notVisited = getNotVisited(current);
        while (!notVisited.isEmpty()) {
            backTrack(current, notVisited.getFirst());
            notVisited = getNotVisited(current);
        }
    }

    private List<Cell> getNotVisited(Cell current) {
        int x = current.getX();
        int y = current.getY();
        List<Cell> res = new ArrayList<Cell>();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i != 0 && j != 0) continue;
                if(!checkBounds(x + i, y + j)) continue;
                if (!field[x + i][y + j].isVisited) res.add(field[x + i][y + j]);
            }
        }
        Collections.shuffle(res);
        return res;
    }

    private boolean checkBounds(int x, int y) {
        return x >= 0 && x < fieldWidth && y >= 0 && y < fieldHeight;
    }

    private Cell getStartCell() {
        Random rand = new Random();
        int x = rand.nextInt(fieldWidth);
        int y = rand.nextInt(fieldHeight);
        return field[x][y];
    }

    public void resetCells() {
        for (int x = 0; x < fieldWidth; x++) {
            for (int y = 0; y < fieldHeight; y++) {
                field[x][y].reset();
            }
        }
    }

    public void generateBtn() {
        resetCells();
        generateMaze();

        getChildren().clear();

        draw();
//        drawTanks();
    }
}
