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

    public List<Tank> tanks = new ArrayList<Tank>();
    public Tank firstPlayer;
    public Tank secondPlayer;

    CollisionDetector collisionDetector;

    List<Rectangle> walls = new ArrayList<>();

    BattleField(MainGame game) {
        this.game = game;
    }

    public void initPlayers() {
        firstPlayer = new Tank(Player.ONE, this);
        secondPlayer = new Tank(Player.TWO, this);
        tanks.add(firstPlayer);
        tanks.add(secondPlayer);

        generatePlayersPosition();

        firstPlayer.update();
        secondPlayer.update();

        getChildren().addAll(firstPlayer.base, firstPlayer.muzzle, firstPlayer.turret);
        getChildren().addAll(secondPlayer.base, secondPlayer.muzzle, secondPlayer.turret);

        collisionDetector = new CollisionDetector(this, firstPlayer, secondPlayer);
        firstPlayer.setCollisionDetector(collisionDetector);
        secondPlayer.setCollisionDetector(collisionDetector);
    }


    private void generatePlayersPosition() {
        int spawnAreaWidth = fieldWidth / 2;
        int spawnAreaHeight = fieldHeight / 2;

        Random rand = new Random();
        double firstPlayerX = rand.nextInt(spawnAreaWidth);
        double firstPlayerY = rand.nextInt(spawnAreaHeight);

        double secondPlayerX = fieldWidth - rand.nextInt(spawnAreaWidth);
        double secondPlayerY = fieldHeight - rand.nextInt(spawnAreaHeight);

        firstPlayer.setPosition((firstPlayerX + 0.5) * cellSize, (firstPlayerY + 0.5) * cellSize);
        secondPlayer.setPosition((secondPlayerX - 0.5) * cellSize, (secondPlayerY - 0.5) * cellSize);
    }

    public void draw(){
        getChildren().clear();
        walls.clear();
        for (int x = 0; x < fieldWidth; x++) {
            for (int y = 0; y < fieldHeight; y++) {
                boolean drawBorderWalls = (x == fieldWidth - 1 || y == fieldHeight - 1);

                List<Rectangle> elements = field[x][y].initElements(drawBorderWalls);

                getChildren().addAll(elements.removeFirst()); // draw the cell
                walls.addAll(elements); // save cell's walls
            }
        }
        getChildren().addAll(walls); // draw the walls
    }

    public void update() {
        setCellSize();
        updateCellSize();
        updatePlayers();
        if (collisionDetector != null) collisionDetector.defineWallsCorners();
    }

    private void updateCellSize() {
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

            updateResizedPlayersX(getPrefWidth(), centralPaneWidth);
            setPrefWidth(centralPaneWidth);

            updateResizedPlayersY(getPrefHeight(), fieldHeight * cellSize);
            setPrefHeight(fieldHeight * cellSize);
        } else {
            cellSize = centralPaneHeight / fieldHeight;

            updateResizedPlayersX(getPrefWidth(), fieldWidth * cellSize);
            setPrefWidth(fieldWidth * cellSize);

            updateResizedPlayersY(getPrefHeight(), centralPaneHeight);
            setPrefHeight(centralPaneHeight);
        }
    }

    public void initField() {
        generateFieldDimensions();
        setCellSize();
        fillUpFieldWithCells();
        generateMaze();
    }

    public void generateFieldDimensions() {
        Random rand = new Random();
        fieldWidth = dimensions[rand.nextInt(dimensions.length)];
        fieldHeight = dimensions[rand.nextInt(dimensions.length)];
        System.out.println("fieldWidth: " + fieldWidth);
        System.out.println("fieldHeight: " + fieldHeight);
        field = new Cell[fieldWidth][fieldHeight];
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
        draw();
        initPlayers();
    }

    public void updateResizedPlayersX(Number prev, Number curr) {
        if (firstPlayer != null && secondPlayer != null) {
            double coefficient = curr.doubleValue() / prev.doubleValue();
            firstPlayer.setPosition(firstPlayer.position.x * coefficient,
                    firstPlayer.position.y);
            secondPlayer.setPosition(secondPlayer.position.x * coefficient,
                    secondPlayer.position.y);
        }
    }

    public void updateResizedPlayersY(Number prev, Number curr) {
        if (firstPlayer != null && secondPlayer != null) {
            double coefficient = curr.doubleValue() / prev.doubleValue();
            firstPlayer.setPosition(firstPlayer.position.x,
                    firstPlayer.position.y * coefficient);
            secondPlayer.setPosition(secondPlayer.position.x,
                    secondPlayer.position.y * coefficient);
        }
    }

    public void updatePlayers() {
        if (firstPlayer != null) {
            firstPlayer.update();
//            firstPlayer.updateBullets(0);
        }
        if (secondPlayer != null) {
            secondPlayer.update();
//            secondPlayer.updateBullets(0);
        }
    }
}
