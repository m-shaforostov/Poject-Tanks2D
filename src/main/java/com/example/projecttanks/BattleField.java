package com.example.projecttanks;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Represents the battlefield where the main game takes place.
 * Handles maze generation, player positioning and resizing logic.
 * Is the central container for whole tank-related gameplay.
 */
public class BattleField extends Pane {
    private final MainGame game;
    private CollisionDetector collisionDetector;
    /**
     * Instance of the {@link GameState}
     */
    public GameState gameState;

    private Cell[][] field;
    private static final int[] dimensions = new int[]{3, 4, 5, 6, 7, 8, 9, 10};
    private int fieldWidth;
    private int fieldHeight;
    private double cellSize;

    private final List<Rectangle> walls = new ArrayList<>();

    private static final int closedCellRate = 30;
    private static final Random rand = new Random();

    /**
     * Instance of the {@link Tank}.
     * Represents tank of the {@link Player} one.
     */
    public Tank firstPlayer;

    /**
     * Instance of the {@link Tank}.
     * Represents tank of the {@link Player} two.
     */
    public Tank secondPlayer;

    /**
     * Constructs the battlefield.
     * @param game the main game instance.
     */
    BattleField(MainGame game) {
        this.game = game;
    }

    /**
     * Initializes a new game state and saves it.
     */
    public void initGameState(){
        this.gameState = new GameState(game);
    }

    /**
     * Initializes two players in instance of {@link Tank} and saves them.
     * Generates random but valid starting positions for the tanks.
     * Draws tanks on the battlefield and sets up a collision detection mechanism for both of them.
     */
    public void initPlayers() {
        SoundPlayer soundPlayer = new SoundPlayer();

        firstPlayer = new Tank(Player.ONE, this);
        secondPlayer = new Tank(Player.TWO, this);

        generatePlayersPosition();

        firstPlayer.update();
        secondPlayer.update();

        firstPlayer.drawTankElements();
        secondPlayer.drawTankElements();

        collisionDetector = new CollisionDetector(this, firstPlayer, secondPlayer);
        firstPlayer.setCollisionDetector(collisionDetector);
        secondPlayer.setCollisionDetector(collisionDetector);
    }

    private void generatePlayersPosition() {
        int spawnAreaWidth = fieldWidth / 2;
        int spawnAreaHeight = fieldHeight / 2;

        while (true){
            int firstPlayerX = rand.nextInt(spawnAreaWidth);
            int firstPlayerY = rand.nextInt(spawnAreaHeight);
            if (!field[firstPlayerX][firstPlayerY].isClosed) {
                firstPlayer.setPosition((firstPlayerX + 0.5) * cellSize, (firstPlayerY + 0.5) * cellSize);
                break;
            }
        }
        while (true){
            int secondPlayerX = fieldWidth - rand.nextInt(spawnAreaWidth);
            int secondPlayerY = fieldHeight - rand.nextInt(spawnAreaHeight);
            if (!field[secondPlayerX - 1][secondPlayerY - 1].isClosed) {
                secondPlayer.setPosition((secondPlayerX - 0.5) * cellSize, (secondPlayerY - 0.5) * cellSize);
                break;
            }
        }
    }

    /**
     * Clears the pane.
     * For every initialized cell gets all needed elements (background rectangle and walls).
     * Then displays them on the pane.
     */
    public void draw(){
        getChildren().clear();
        walls.clear();
        drawBackground();
        for (int x = 0; x < fieldWidth; x++) {
            for (int y = 0; y < fieldHeight; y++) {
                boolean drawBorderWalls = (x == fieldWidth - 1 || y == fieldHeight - 1);
                List<Rectangle> elements = field[x][y].initElements(drawBorderWalls);

                getChildren().addAll(elements.removeFirst()); // draw the cell background
                walls.addAll(elements); // save cell's walls
            }
        }
        getChildren().addAll(walls); // draw the walls
    }

    private void drawBackground() {
        Rectangle background = new Rectangle(0, 0, getWidth(), getHeight());
        background.setFill(Cell.BG_COLOR);
        background.widthProperty().bind(widthProperty());
        background.heightProperty().bind(heightProperty());
        getChildren().add(background);
    }

    private void setCellSize(){
        double centralPaneHeight = game.getBorderpaneHeight() - 100;
        double centralPaneWidth = game.getBorderpaneWidth();
        double centralPaneRatio = (double) centralPaneWidth / centralPaneHeight;

        double battleFieldRatio = (double) fieldWidth / fieldHeight;
        if (battleFieldRatio > centralPaneRatio) {
            cellSize = centralPaneWidth / fieldWidth;

            updateResizedItemsX(getPrefWidth(), centralPaneWidth);
            setPrefWidth(centralPaneWidth);

            updateResizedPlayersY(getPrefHeight(), fieldHeight * cellSize);
            setPrefHeight(fieldHeight * cellSize);
        } else {
            cellSize = centralPaneHeight / fieldHeight;

            updateResizedItemsX(getPrefWidth(), fieldWidth * cellSize);
            setPrefWidth(fieldWidth * cellSize);

            updateResizedPlayersY(getPrefHeight(), centralPaneHeight);
            setPrefHeight(centralPaneHeight);
        }
    }

    private void updateCellSize() {
        for (int x = 0; x < fieldWidth; x++) {
            for (int y = 0; y < fieldHeight; y++) {
                field[x][y].updateSize(cellSize);
            }
        }
    }

    /**
     * Initializes the field by generating random dimensions of the field,
     * Calculating the size of the cells,
     * Constructing instances of {@link Cell} and filling up the field with them,
     * Then generating a correct maze, which will be the battlefield.
     */
    public void initField() {
        generateFieldDimensions();
        setCellSize();
        fillUpFieldWithCells();
        generateMaze();
    }

    private void generateFieldDimensions() {
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

    private void generateMaze() {
        generateClosedCells();
        Cell start = getStartCell();
        backTrack(null, start);
    }

    private void generateClosedCells() {
        for (int x = 0; x < fieldWidth; x++) {
            for (int y = 0; y < fieldHeight; y++) {
                if (rand.nextInt(closedCellRate) == 0) {
                    field[x][y].isVisited = true;
                    field[x][y].isClosed = true;
                }
            }
        }
    }

    private void setCellsConnection(boolean isConnected, Cell cell1, Cell cell2){
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
        if (!field[x][y].isClosed) return field[x][y];
        return getStartCell();
    }

    /**
     * Handles "Generate" button.
     * Resets all cells to their initial state,
     * Generates the maze,
     * Draws it, and initialises players.
     */
    public void generateBtn() {
        resetCells();
        generateMaze();
        draw();
        initPlayers();
    }

    private void resetCells() {
        for (int x = 0; x < fieldWidth; x++) {
            for (int y = 0; y < fieldHeight; y++) {
                field[x][y].reset();
            }
        }
    }

    private void updateResizedItemsX(Number prev, Number curr) {
        double coefficient = curr.doubleValue() / prev.doubleValue();
        updateResizedPlayerAndBullets(firstPlayer, coefficient, 1);
        updateResizedPlayerAndBullets(secondPlayer, coefficient, 1);
    }

    private void updateResizedPlayersY(Number prev, Number curr) {
        double coefficient = curr.doubleValue() / prev.doubleValue();
        updateResizedPlayerAndBullets(firstPlayer, 1, coefficient);
        updateResizedPlayerAndBullets(secondPlayer, 1, coefficient);
    }

    private void updateResizedPlayerAndBullets(Tank player, double coefX, double coefY) {
        if (player != null){
            player.setPosition(player.getX() * coefX, player.getY() * coefY);
            for (Projectile projectile : player.getFiredProjectiles()){
                projectile.setPosition(projectile.getX() * coefX, projectile.getY() * coefY);
            }
        }
    }

    /**
     * Updates the battlefield elements size and position after window resizing
     */
    public void update() {
        setCellSize();
        updateCellSize();
        updatePlayers();
        if (collisionDetector != null) collisionDetector.defineWallsCorners();
    }

    private void updatePlayers() {
        if (firstPlayer != null) {
            firstPlayer.update();
            firstPlayer.updateProjectileResizing();
        }
        if (secondPlayer != null) {
            secondPlayer.update();
            secondPlayer.updateProjectileResizing();
        }
    }

    /**
     * @return size of the cell in px.
     */
    public double getCellSize() {return cellSize;}

    /**
     * returns the list of all walls displayed on the battlefield
     * @return list of the walls (Rectangles)
     */
    public List<Rectangle> getWalls() {return walls;}

    /**
     * @return instance of the {@link CollisionDetector}
     */
    public CollisionDetector getCollisionDetector() {return collisionDetector;}
}
