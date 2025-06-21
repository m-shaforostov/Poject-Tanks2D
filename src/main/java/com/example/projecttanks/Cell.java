package com.example.projecttanks;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class Cell {
    public Vector2D position;
    public double size;
    public boolean checked;
    public boolean wallTop = true;
    public boolean wallBottom = true;
    public boolean wallLeft = true;
    public boolean wallRight = true;
    public Rectangle[] walls;

    private Color BG_COLOR = Color.LIGHTGRAY;
    private Color WALL_COLOR = Color.BLACK;
    private double wallNarrowSide = 0.05;
    private double wallWideSide = 1;

    Cell(int x, int y, double size) {
        this.position = new Vector2D(x * size, y * size);
        this.size = size;
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

    public List<Rectangle> initElements(Vector2D offset){
        List<Rectangle> elements = new ArrayList<>();
        Vector2D globalPosition = position.getAdded(offset);

        Rectangle cell = new Rectangle(size, size);
        cell.setFill(Color.LIGHTGRAY);
        cell.setX(globalPosition.x);
        cell.setY(globalPosition.y);
        elements.add(cell);

        if (wallTop){
            Rectangle wallTopR = getWall(globalPosition.x, globalPosition.y, wallWideSide * size, wallNarrowSide * size);
            elements.add(wallTopR);
        }
        if (wallBottom){
            double y = globalPosition.y + size - wallNarrowSide * size;
            Rectangle wallBottomR = getWall(globalPosition.x, y, wallWideSide * size, wallNarrowSide * size);
            elements.add(wallBottomR);
        }
        if (wallLeft){
            Rectangle wallLeftR = getWall(globalPosition.x, globalPosition.y, wallNarrowSide * size, wallWideSide * size);
            elements.add(wallLeftR);
        }
        if (wallRight){
            double x = globalPosition.x + size - wallNarrowSide * size;
            Rectangle wallRightR = getWall(x, globalPosition.y, wallNarrowSide * size, wallWideSide * size);
            elements.add(wallRightR);
        }

        return elements;
    }

    private Rectangle getWall(double x, double y, double width, double height) {
        Rectangle wall = new Rectangle(x, y, width, height);
        wall.setFill(WALL_COLOR);
        return wall;
    }
}
