package com.example.projecttanks;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles all collision detection logic int the game.
 * It detects collision of each tank with other tanks, bullets and walls.
 * And can optionally visualise collision zones and points (wall or corner the tank or the bullet collided with).
 */
public class CollisionDetector {
    private final BattleField battleField;
    private final Tank player1, player2;
    private final List<Rectangle> walls;

    private final List<Vector2D> player1Corners = new ArrayList<>();
    private final List<Vector2D> player2Corners = new ArrayList<>();
    private final List<Vector2D> wallsCorners = new ArrayList<>();

    private final List<Circle> allPlayersCorners = new ArrayList<>();
    private final List<Circle> allWallsCorners = new ArrayList<>();

    private final Rectangle detectedWall = new Rectangle();
    private final Circle detectedCorner = new Circle();

    private final static boolean IS_COLLISION_COLORING_ALLOWED = false;
    private final static boolean IS_CORNER_COLORING_ALLOWED = false;

    /**
     * Constructs the collision detector for two tanks and a battlefield
     * @param battleField the battlefield where the detection will take place
     * @param player1 the first player (tank)
     * @param player2 the second player (tank)
     */
    CollisionDetector(BattleField battleField, Tank player1, Tank player2) {
        this.battleField = battleField;
        this.player1 = player1;
        this.player2 = player2;
        this.walls = battleField.getWalls();

        definePlayerCorners();
        defineWallsCorners();
    }

    private void definePlayerCorners() {
        calculatePlayerCorners(player1, player1Corners);
        calculatePlayerCorners(player2, player2Corners);

        rotatePlayerCorners(player1, player1Corners);
        rotatePlayerCorners(player2, player2Corners);

        if (IS_CORNER_COLORING_ALLOWED){
            clearPane();
            drawPlayerCorners(player1, player1Corners);
            drawPlayerCorners(player2, player2Corners);
        }
    }

    /**
     * Calculates all corners for every wall on the battlefield.
     * If corner coloring is allowed by IS_CORNER_COLORING_ALLOWED constant, draws red circles to mark those corners.
     */
    public void defineWallsCorners() {
        calculateWallsCorners();
        if (IS_CORNER_COLORING_ALLOWED) drawWallsCorners();
    }

    private void calculatePlayerCorners(Tank player, List<Vector2D> playerCorners) {
        playerCorners.clear();
        Vector2D position = player.getPosition();
        double width = player.getLength();
        double height = player.getWidth();
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
        double angle = player.getAngle();
        for (Vector2D playerCorner : playerCorners) {
            double defaultAngle = playerCorner.getAngle();
            playerCorner.setAngleAndLength(defaultAngle - Math.PI * angle / 180.0, playerCorner.getLength());
        }
    }

    private Vector2D getPlayerCornerGlobalPosition(Vector2D playerRawCorner, Tank player) {
        return playerRawCorner.getAdded(player.getPosition());
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
        if (IS_COLLISION_COLORING_ALLOWED){
            battleField.getChildren().remove(detectedCorner);
            battleField.getChildren().add(detectedCorner);
        }
    }

    /**
     * Checks if the given tanks is currently colliding with any wall or the other tank
     * @param player the tank to check
     * @return True if collision was detected, false otherwise
     */
    public boolean isDetectedForPlayer(Tank player) {
        definePlayerCorners();

        List<Vector2D> playerCorners = player1Corners;
        List<Vector2D> enemyCorners = player2Corners;
        Tank enemy = battleField.secondPlayer;
        if (player.getPlayer() == Player.TWO) {
            playerCorners = player2Corners;
            enemyCorners = player1Corners;
            enemy = battleField.firstPlayer;
        }

        for (Rectangle wall : walls) {
            for (Vector2D playerCorner : playerCorners) {
                Vector2D position = getPlayerCornerGlobalPosition(playerCorner, player);
                if (wall.contains(new Point2D(position.x, position.y))) {
                    if (IS_COLLISION_COLORING_ALLOWED) colourWallCollidedWith(wall);
                    return true;
                }
            }
        }
        for (Vector2D wallCorner : wallsCorners) {
            if (checkCircleIntersection(wallCorner.x, wallCorner.y, 0, playerCorners, player)) {
                if (IS_COLLISION_COLORING_ALLOWED) colourCornerCollidedWith(wallCorner);
                return true;
            }
        }

        for (Vector2D corner : enemyCorners) {
            Vector2D globalPosition = getPlayerCornerGlobalPosition(corner, enemy);
            if (checkCircleIntersection(globalPosition.x, globalPosition.y, 0, playerCorners, player)) {
                return true;
            }
        }

        for (Vector2D corner : playerCorners) {
            Vector2D globalPosition = getPlayerCornerGlobalPosition(corner, player);
            if (checkCircleIntersection(globalPosition.x, globalPosition.y, 0, enemyCorners, enemy)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the given bullet is currently colliding with any vertical wall.
     * @param bullet the bullet to check
     * @return True if collision was detected, false otherwise
     */
    public boolean isDetectedBulletVertical(Bullet bullet) {
        double x = bullet.position.x;
        double y = bullet.position.y;
        double r = bullet.radius;
        for (Rectangle wall : walls) {
            if (wall.contains(new Point2D(x + r, y)) ||
                    wall.contains(new Point2D(x - r, y))) {
                if (IS_COLLISION_COLORING_ALLOWED) colourWallCollidedWith(wall);
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the given bullet is currently colliding with any horizontal wall.
     * @param bullet the bullet to check
     * @return True if collision was detected, false otherwise
     */
    public boolean isDetectedBulletHorizontal(Bullet bullet) {
        double x = bullet.position.x;
        double y = bullet.position.y;
        double r = bullet.radius;
        for (Rectangle wall : walls) {
            if (wall.contains(new Point2D(x, y + r)) ||
                    wall.contains(new Point2D(x, y - r))) {
                if (IS_COLLISION_COLORING_ALLOWED) colourWallCollidedWith(wall);
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the given bullet is currently colliding with the given tank.
     * @param bullet the bullet to check
     * @param player the tank to check against
     * @return True if collision was detected, false otherwise
     */
    public boolean isDetectedBulletWithPlayer(Bullet bullet, Tank player) {
        definePlayerCorners();
        List<Vector2D> playerCorners = player1Corners;
        if (player.getPlayer() == Player.TWO) playerCorners = player2Corners;

        return checkCircleIntersection(bullet.getX(), bullet.getY(), bullet.radius, playerCorners, player);
    }

    private boolean checkCircleIntersection(double circleX, double circleY, double radius, List<Vector2D> playerRawCorners, Tank player) {
        List<Vector2D> playerCorners = getAllGlobalPositions(playerRawCorners, player);
        PointsProjection projection = new PointsProjection(circleX, circleY, radius, playerCorners, player.getAngle());
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
}
