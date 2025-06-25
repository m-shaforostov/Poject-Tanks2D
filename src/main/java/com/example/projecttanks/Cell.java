package com.example.projecttanks;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single cell in the battlefield grid.
 * Stores its position, size, configuration of walls and JavaFx shapes as a graphical representation.
 */
public class Cell {
    private final int x; // cells
    private final int y; // cells
    private Vector2D position; // pixels
    private double size = 0;

    /**
     * Tells whether the cell was visited while the process of maze generation (backtracking) or not
     */
    public boolean isVisited = false;

    /**
     * Tells whether the cell is chosen to be closed (won't be connected to other cells)
     */
    public boolean isClosed = false;

    private boolean wallTop = true;
    private boolean wallBottom = true;
    private boolean wallLeft = true;
    private boolean wallRight = true;

    private final Rectangle wallTopR = new Rectangle();
    private final Rectangle wallBottomR = new Rectangle();
    private final Rectangle wallLeftR = new Rectangle();
    private final Rectangle wallRightR = new Rectangle();

    private Rectangle cell;

    /**
     * Background color of the cell
     */
    public static Color BG_COLOR = Color.LIGHTGRAY;
    private static final Color WALL_COLOR = Color.BLACK;
    private final double wallNarrowSide = 0.1;
    private final double wallWideSide = 1.1;

    /**
     * Constructs a square cell with a given size and location in the grid
     * @param x the cell's X coordinate in the grid
     * @param y the cell's Y coordinate in the grid
     * @param size the size of the cell's side in pixels
     */
    Cell(int x, int y, double size) {
        this.x = x;
        this.y = y;
        this.position = new Vector2D(x * size, y * size);
        this.size = size;
    }

    /**
     * @return the cell's X coordinate in the grid
     */
    public int getX(){
        return x;
    }

    /**
     * @return the cell's Y coordinate in the grid
     */
    public int getY(){
        return y;
    }

    /**
     * Sets needed boolean variables to the default values.
     * Used before maze regeneration
     */
    public void reset(){
        isVisited = false;
        isClosed = false;
        wallTop = true;
        wallBottom = true;
        wallLeft = true;
        wallRight = true;
    }

    /**
     * Sets value for the boolean variable, which represents whether the top wall of the cell will be displayed
     * @param wall_top is the top wall displayed in the grid
     */
    public void setWallTop(boolean wall_top){
        this.wallTop = wall_top;
    }

    /**
     * Sets value for the boolean variable, which represents whether the bottom wall of the cell will be displayed
     * @param wall_bottom is the bottom wall displayed in the grid
     */
    public void setWallBottom(boolean wall_bottom){
        this.wallBottom = wall_bottom;
    }

    /**
     * Sets value for the boolean variable, which represents whether the left wall of the cell will be displayed
     * @param wall_left is the left wall displayed in the grid
     */
    public void setWallLeft(boolean wall_left){
        this.wallLeft = wall_left;
    }

    /**
     * Sets value for the boolean variable, which represents whether the right wall of the cell will be displayed
     * @param wall_right is the right wall displayed in the grid
     */
    public void setWallRight(boolean wall_right){
        this.wallRight = wall_right;
    }

    /**
     * Initializes all elements of the cell (background cell and walls' rectangles).
     * Doesn't draw bottom and right walls if the cell is located far from the broad.
     * If the cell is located close to the broad it shows bottom and right walls to constract the border
     * @param drawBorderWalls tells whether we should draw bottom and right walls.
     * @return list of Rectangles each represents the cell itself ir the wall
     */
    public List<Rectangle> initElements( boolean drawBorderWalls ){
        List<Rectangle> elements = new ArrayList<>();

        cell = new Rectangle();
        updateCellRect();
        elements.add(cell);

        updateWalls();
        if (wallTop) elements.add(wallTopR);
        if (wallLeft) elements.add(wallLeftR);
        if (!drawBorderWalls) return elements;

        if (wallRight) elements.add(wallRightR);
        if (wallBottom) elements.add(wallBottomR);
        return elements;
    }

    private void updateWallTop(double x, double y) {
        wallTopR.setX(x);
        wallTopR.setY(y);
        wallTopR.setWidth(wallWideSide * size);
        wallTopR.setHeight(wallNarrowSide * size);
        wallTopR.setFill(WALL_COLOR);
    }

    private void updateWallBottom(double x, double y) {
        y += size;
        wallBottomR.setX(x);
        wallBottomR.setY(y);
        wallBottomR.setWidth(wallWideSide * size);
        wallBottomR.setHeight(wallNarrowSide * size);
        wallBottomR.setFill(WALL_COLOR);
    }

    private void updateWallLeft(double x, double y) {
        wallLeftR.setX(x);
        wallLeftR.setY(y);
        wallLeftR.setWidth(wallNarrowSide * size);
        wallLeftR.setHeight(wallWideSide * size);
        wallLeftR.setFill(WALL_COLOR);
    }

    private void updateWallRight(double x, double y) {
        x += size;
        wallRightR.setX(x);
        wallRightR.setY(y);
        wallRightR.setWidth(wallNarrowSide * size);
        wallRightR.setHeight(wallWideSide * size);
        wallRightR.setFill(WALL_COLOR);
    }

    /**
     * Saves given size and calculates new position accordingly to the position in th grid and the size of the cell
     * @param size the new size value in px
     */
    public void updateSize(double size) {
        this.size = size;
        this.position = new Vector2D(x * size, y * size);
        updateCellRect();
        updateWalls();
    }

    private void updateCellRect() {
        cell.setWidth(size);
        cell.setHeight(size);
        cell.setX(position.x);
        cell.setY(position.y);
        if (isClosed) cell.setFill(Color.WHITE);
        else cell.setFill(BG_COLOR);
    }

    private void updateWalls() {
        double x = position.x - wallNarrowSide * size / 2.0;
        double y = position.y - wallNarrowSide * size / 2.0;
        updateWallTop(x, y);
        updateWallBottom(x, y);
        updateWallLeft(x, y);
        updateWallRight(x, y);
    }

    /**
     * Sets variable value to true, saying that this cell was visited while maze generation process (backtracking)
     */
    public void setVisited() {
        isVisited = true;
    }
}
