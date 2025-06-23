package com.example.projecttanks;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class CollisionDetector {
    BattleField battleField;
    Tank player1, player2;
    List<Rectangle> walls;

    List<Vector2D> player1Corners = new ArrayList<>();
    List<Vector2D> player2Corners = new ArrayList<>();

    List<Circle> allCorners = new ArrayList<>();

    Rectangle detectedWall = new Rectangle();

    CollisionDetector(BattleField battleField, Tank player1, Tank player2) {
        this.battleField = battleField;
        this.player1 = player1;
        this.player2 = player2;
        this.walls = battleField.walls;

        defineCorners();
    }

    public void defineCorners() {
        calculateCorners(player1, player1Corners);
        calculateCorners(player2, player2Corners);

        rotateCorners(player1, player1Corners);
        rotateCorners(player2, player2Corners);

        clearPane();
        drawCorners(player1, player1Corners);
        drawCorners(player2, player2Corners);
    }

    private void calculateCorners(Tank player, List<Vector2D> playerCorners) {
        playerCorners.clear();
        Vector2D position = player.position;
        double width = player.length;
        double height = player.width;
        Vector2D defaultLeftTopCorner = new Vector2D(position.x - width / 2, position.y - height / 2);
        Vector2D defaultLeftBottomCorner = new Vector2D(position.x - width / 2, position.y + height / 2);
        Vector2D defaultRightTopCorner = new Vector2D(position.x + width / 2, position.y - height / 2);
        Vector2D defaultRightBottomCorner = new Vector2D(position.x + width / 2, position.y + height / 2);
        playerCorners.add(defaultLeftTopCorner.getSubtracted(position));
        playerCorners.add(defaultLeftBottomCorner.getSubtracted(position));
        playerCorners.add(defaultRightTopCorner.getSubtracted(position));
        playerCorners.add(defaultRightBottomCorner.getSubtracted(position));
    }

    private void rotateCorners(Tank player, List<Vector2D> playerCorners) {
        double angle = player.angle;
        for (Vector2D playerCorner : playerCorners) {
            double defaultAngle = playerCorner.getAngle();
            playerCorner.setAngleAndLength(defaultAngle - Math.PI * angle / 180.0, playerCorner.getLength());
        }
    }

    private Vector2D getCornerGlobalPosition(Vector2D playerCorner, Tank player) {
        return playerCorner.getAdded(player.position);
    }

    private void drawCorners(Tank player, List<Vector2D> playerCorners) {
        for (Vector2D playerCorner : playerCorners) {
            Vector2D position = getCornerGlobalPosition(playerCorner, player);
            Circle corner = new Circle(position.x, position.y, 3, Color.RED);
            corner.setStroke(Color.BLACK);
            corner.setStrokeWidth(1);
            allCorners.add(corner);
            battleField.getChildren().add(corner);
        }
    }

    private void clearPane(){
        for (Circle circle : allCorners) {
            battleField.getChildren().remove(circle);
        }
        allCorners.clear();
    }

    public boolean isDetected(Tank player) {
        defineCorners();

        List<Vector2D> playerCorners = player1Corners;
        if (player.player == Player.TWO) playerCorners = player2Corners;

        for (Rectangle wall : walls) {
            for (Vector2D playerCorner : playerCorners) {
                Vector2D position = getCornerGlobalPosition(playerCorner, player);
                if (wall.contains(new Point2D(position.x, position.y))) {
                    System.out.println(wall);
                    detectedWall.setFill(Color.RED);
                    detectedWall.setX(wall.getX());
                    detectedWall.setY(wall.getY());
                    detectedWall.setWidth(wall.getWidth());
                    detectedWall.setHeight(wall.getHeight());
                    battleField.getChildren().remove(detectedWall);
                    battleField.getChildren().add(detectedWall);
                    return true;
                }
            }
        }
        return false;
    }
}
