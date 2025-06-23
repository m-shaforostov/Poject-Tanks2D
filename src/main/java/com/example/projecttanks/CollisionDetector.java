package com.example.projecttanks;

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

    CollisionDetector(BattleField battleField, Tank player1, Tank player2) {
        this.battleField = battleField;
        this.player1 = player1;
        this.player2 = player2;
        this.walls = battleField.walls;

        defineCorners();
    }

    public void defineCorners() {
        calculateCorners(player1, player1.base, player1Corners);
        calculateCorners(player2, player2.base, player2Corners);

        rotateCorners(player1, player1Corners);
        rotateCorners(player2, player2Corners);

        clearPane();
        drawCorners(player1, player1Corners);
        drawCorners(player2, player2Corners);
    }

    private void calculateCorners(Tank player, Rectangle base, List<Vector2D> playerCorners) {
        playerCorners.clear();
        Vector2D position = player.position;
        double widthBase = base.getWidth();
        double heightBase = base.getHeight();
        double xBase = base.getX();
        double yBase = base.getY();
        Vector2D defaultLeftTopCorner = new Vector2D(xBase, yBase).getSubtracted(position);
        Vector2D defaultLeftBottomCorner = new Vector2D(xBase, yBase + heightBase).getSubtracted(position);
        Vector2D defaultRightTopCorner = new Vector2D(xBase + widthBase, yBase).getSubtracted(position);
        Vector2D defaultRightBottomCorner = new Vector2D(xBase + widthBase, yBase + heightBase).getSubtracted(position);
        playerCorners.add(defaultLeftTopCorner);
        playerCorners.add(defaultLeftBottomCorner);
        playerCorners.add(defaultRightTopCorner);
        playerCorners.add(defaultRightBottomCorner);
    }

    private void rotateCorners(Tank player, List<Vector2D> playerCorners) {
        double angle = player.angle;
        for (Vector2D playerCorner : playerCorners) {
            double defaultAngle = playerCorner.getAngle();
            playerCorner.setAngleAndLength(defaultAngle - Math.PI * angle / 180.0, playerCorner.getLength());
        }
    }

    private void drawCorners(Tank player, List<Vector2D> playerCorners) {
        for (Vector2D playerCorner : playerCorners) {
            Vector2D position = playerCorner.getAdded(player.position);
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
}
