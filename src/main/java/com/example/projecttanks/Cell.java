package com.example.projecttanks;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class Cell {
    public int x; // cells
    public int y; // cells
    public Vector2D position; // pixels
    public double size = 0;

    public boolean isVisited = false;

    public boolean wallTop = true;
    public boolean wallBottom = true;
    public boolean wallLeft = true;
    public boolean wallRight = true;

    public Rectangle wallTopR = new Rectangle();
    public Rectangle wallBottomR = new Rectangle();
    public Rectangle wallLeftR = new Rectangle();
    public Rectangle wallRightR = new Rectangle();

    public Rectangle[] walls;
    public Rectangle cell;

    private Color BG_COLOR = Color.WHITE;
    private Color WALL_COLOR = Color.BLACK;
    private double wallNarrowSide = 0.1;
    private double wallWideSide = 1.05;

    Cell(int x, int y, double size) {
        this.x = x;
        this.y = y;
        this.position = new Vector2D(x * size, y * size);
        this.size = size;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public void reset(){
        isVisited = false;
        wallTop = true;
        wallBottom = true;
        wallLeft = true;
        wallRight = true;
    }

    public void setWallTop(boolean wall_top){
        this.wallTop = wall_top;
    }

    public void setWallBottom(boolean wall_bottom){
        this.wallBottom = wall_bottom;
    }

    public void setWallLeft(boolean wall_left){
        this.wallLeft = wall_left;
    }

    public void setWallRight(boolean wall_right){
        this.wallRight = wall_right;
    }

    public List<Rectangle> initElements(){
        List<Rectangle> elements = new ArrayList<>();

        cell = new Rectangle();
        updateCell();
        elements.add(cell);

        updateWalls();
        if (wallTop) elements.add(wallTopR);
        if (wallBottom) elements.add(wallBottomR);
        if (wallLeft) elements.add(wallLeftR);
        if (wallRight) elements.add(wallRightR);
        return elements;
    }

    private void updateWallTop() {
        double x = position.x - wallNarrowSide * size / 2.0;
        double y = position.y - wallNarrowSide * size / 2.0;
        wallTopR.setX(x);
        wallTopR.setY(y);
        wallTopR.setWidth(wallWideSide * size);
        wallTopR.setHeight(wallNarrowSide * size);
        wallTopR.setFill(WALL_COLOR);
    }

    private void updateWallBottom() {
        double x = position.x;
        double y = position.y + size - wallNarrowSide * size / 2.0;
        wallBottomR.setX(x);
        wallBottomR.setY(y);
        wallBottomR.setWidth(wallWideSide * size);
        wallBottomR.setHeight(wallNarrowSide * size);
        wallBottomR.setFill(WALL_COLOR);
    }

    private void updateWallLeft() {
        double x = position.x - wallNarrowSide * size / 2.0;
        double y = position.y;
        wallLeftR.setX(x);
        wallLeftR.setY(y);
        wallLeftR.setWidth(wallNarrowSide * size);
        wallLeftR.setHeight(wallWideSide * size);
        wallLeftR.setFill(WALL_COLOR);
    }

    private void updateWallRight() {
        double x = position.x + size - wallNarrowSide * size / 2.0;
        double y = position.y - wallNarrowSide * size / 2.0;
        wallRightR.setX(x);
        wallRightR.setY(y);
        wallRightR.setWidth(wallNarrowSide * size);
        wallRightR.setHeight(wallWideSide * size);
        wallRightR.setFill(WALL_COLOR);
    }

    public void updateSize(double size) {
        this.size = size;
        this.position = new Vector2D(x * size, y * size);
        updateCell();
        updateWalls();
    }

    public void updateCell() {
        cell.setWidth(size);
        cell.setHeight(size);
        cell.setFill(BG_COLOR);
        cell.setX(position.x);
        cell.setY(position.y);
    }

    private void updateWalls() {
        updateWallTop();
        updateWallBottom();
        updateWallLeft();
        updateWallRight();
    }

    public void setVisited() {
        isVisited = true;
    }
}
