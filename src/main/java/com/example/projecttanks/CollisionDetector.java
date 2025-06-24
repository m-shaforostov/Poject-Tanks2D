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
    List<Vector2D> wallsCorners = new ArrayList<>();

    List<Circle> allPlayersCorners = new ArrayList<>();
    List<Circle> allWallsCorners = new ArrayList<>();

    Rectangle detectedWall = new Rectangle();
    Circle detectedCorner = new Circle();

    boolean isCollisionColoringAllowed = false;
    boolean isCornerColoringAllowed = false;

    CollisionDetector(BattleField battleField, Tank player1, Tank player2) {
        this.battleField = battleField;
        this.player1 = player1;
        this.player2 = player2;
        this.walls = battleField.walls;

        definePlayerCorners();
        defineWallsCorners();
    }

    public void definePlayerCorners() {
        calculatePlayerCorners(player1, player1Corners);
        calculatePlayerCorners(player2, player2Corners);

        rotatePlayerCorners(player1, player1Corners);
        rotatePlayerCorners(player2, player2Corners);

        if (isCornerColoringAllowed){
            clearPane();
            drawPlayerCorners(player1, player1Corners);
            drawPlayerCorners(player2, player2Corners);
        }
    }

    public void defineWallsCorners() {
        calculateWallsCorners();
        if (isCornerColoringAllowed) drawWallsCorners();
    }

    private void calculatePlayerCorners(Tank player, List<Vector2D> playerCorners) {
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

    private void rotatePlayerCorners(Tank player, List<Vector2D> playerCorners) {
        double angle = player.angle;
        for (Vector2D playerCorner : playerCorners) {
            double defaultAngle = playerCorner.getAngle();
            playerCorner.setAngleAndLength(defaultAngle - Math.PI * angle / 180.0, playerCorner.getLength());
        }
    }

    private Vector2D getPlayerCornerGlobalPosition(Vector2D playerRawCorner, Tank player) {
        return playerRawCorner.getAdded(player.position);
    }

    private List<Vector2D> getAllGlobalPositions(List<Vector2D> playerRawCorners, Tank player) {
        List<Vector2D> globalPositions = new ArrayList<>();
        for (Vector2D playerCorner : playerRawCorners) {
            globalPositions.add(getPlayerCornerGlobalPosition(playerCorner, player));
        }
        return globalPositions;
    }

    private void drawPlayerCorners(Tank player, List<Vector2D> playerCorners) {
        for (Vector2D playerCorner : playerCorners) {
            Vector2D position = getPlayerCornerGlobalPosition(playerCorner, player);
            Circle corner = new Circle(position.x, position.y, 3, Color.RED);
            corner.setStroke(Color.BLACK);
            corner.setStrokeWidth(1);
            allPlayersCorners.add(corner);
            battleField.getChildren().add(corner);
        }
    }

    private void clearPane(){
        for (Circle circle : allPlayersCorners) {
            battleField.getChildren().remove(circle);
        }
        allPlayersCorners.clear();
    }

    private void calculateWallsCorners() {
        wallsCorners.clear();
        for (Rectangle wall : walls) {
            double x = wall.getX();
            double y = wall.getY();
            double width = wall.getWidth();
            double height = wall.getHeight();
            Vector2D leftTopCorner = new Vector2D(x, y);
            Vector2D leftBottomCorner = new Vector2D(x, y + height);
            Vector2D rightTopCorner = new Vector2D(x + width, y);
            Vector2D rightBottomCorner = new Vector2D(x + width, y + height);
            wallsCorners.add(leftTopCorner);
            wallsCorners.add(leftBottomCorner);
            wallsCorners.add(rightTopCorner);
            wallsCorners.add(rightBottomCorner);
        }
    }

    private void drawWallsCorners() {
        for (Vector2D wallCorner : wallsCorners) {
            Circle corner = new Circle(wallCorner.x, wallCorner.y, 3, Color.RED);
            corner.setStroke(Color.BLACK);
            corner.setStrokeWidth(1);
            allWallsCorners.add(corner);
            battleField.getChildren().add(corner);
        }
        if (isCollisionColoringAllowed){
            battleField.getChildren().remove(detectedCorner);
            battleField.getChildren().add(detectedCorner);
        }
    }

    public boolean isDetectedForPlayer(Tank player) {
        definePlayerCorners();

        List<Vector2D> playerCorners = player1Corners;
        if (player.player == Player.TWO) playerCorners = player2Corners;

        for (Rectangle wall : walls) {
            for (Vector2D playerCorner : playerCorners) {
                Vector2D position = getPlayerCornerGlobalPosition(playerCorner, player);
                if (wall.contains(new Point2D(position.x, position.y))) {
                    if (isCollisionColoringAllowed) colourWallCollidedWith(wall);
                    return true;
                }
            }
        }
        for (Vector2D wallCorner : wallsCorners) {
            if (checkCornerBounds(wallCorner.x, wallCorner.y, playerCorners, player)) {
                if (isCollisionColoringAllowed) colourCornerCollidedWith(wallCorner);
                return true;
            }
        }
        return false;
    }

    public boolean isDetectedBulletHorizontal(Bullet bullet) {
        double x = bullet.position.x;
        double y = bullet.position.y;
        double r = bullet.radius;
        for (Rectangle wall : walls) {
            if (wall.contains(new Point2D(x + r, y)) ||
                    wall.contains(new Point2D(x - r, y))) {
                if (isCollisionColoringAllowed) colourWallCollidedWith(wall);
                return true;
            }
        }
        return false;
    }

    public boolean isDetectedBulletVertical(Bullet bullet) {
        double x = bullet.position.x;
        double y = bullet.position.y;
        double r = bullet.radius;
        for (Rectangle wall : walls) {
            if (wall.contains(new Point2D(x, y + r)) ||
                    wall.contains(new Point2D(x, y - r))) {
                if (isCollisionColoringAllowed) colourWallCollidedWith(wall);
                return true;
            }
        }
        return false;
    }

//    public boolean isDetectedBulletWithPlayer(Bullet bullet, Tank player) {
//        definePlayerCorners();
//
//        List<Vector2D> playerCorners = player1Corners;
//        if (player.player == Player.TWO) playerCorners = player2Corners;
//        if (checkCornerBounds(bullet.getX(), bullet.getY(), playerCorners, player)) {
//            if (isCollisionColoringAllowed) colourCornerCollidedWith(wallCorner);
//            return true;
//        }
//        return false;
//    }

    private boolean checkCornerBounds(double cornerX, double cornerY, List<Vector2D> playerRawCorners, Tank player) {
        List<Vector2D> playerCorners = getAllGlobalPositions(playerRawCorners, player);
        PointsProjection projection = new PointsProjection (cornerX, cornerY, playerCorners, player.angle);
        return projection.isWithinBoundsX() && projection.isWithinBoundsY();
    }

    private void colourWallCollidedWith(Rectangle wall) {
        detectedWall.setX(wall.getX());
        detectedWall.setY(wall.getY());
        detectedWall.setWidth(wall.getWidth());
        detectedWall.setHeight(wall.getHeight());
        detectedWall.setFill(Color.GREEN);
        battleField.getChildren().remove(detectedWall);
        battleField.getChildren().add(detectedWall);
    }

    private void colourCornerCollidedWith(Vector2D wallCorner) {
        if (wallCorner == null) return;
        detectedCorner.setCenterX(wallCorner.x);
        detectedCorner.setCenterY(wallCorner.y);
        detectedCorner.setRadius(3);
        detectedCorner.setFill(Color.GREEN);
    }

    public void updateCornersWhenResizing(){

    }
}
