package com.example.projecttanks;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a tank controlled by a player on the battlefield.
 * Handles tank's movement, rotation, resizing, shooting and updates visual components
 */
public class Tank {
    private Vector2D position;
    private double width;
    private double length;

    private final Vector2D velocity = new Vector2D();
    private double speed = 0;
    private double angle = 0;
    private double rotationSpeed = 0;
    private final Rotate rotation;

    private static final double ROTATION_SPEED = 300; // angle per second
    private double tankSpeedLimit; // px per second

    /** Linear speed of the bullet. It is calculated based on the speed of tank */
    public double bulletSpeedLimit; // px per second

    private boolean isMoving = false;
    private boolean isRotatingL = false;
    private boolean isRotatingR = false;
    private boolean isDead = false;

    private final Player player;
    private Color color;
    private CollisionDetector collision;

    private static final int PROJECTILES_LIMIT = 5;
    private int projectilesCount = PROJECTILES_LIMIT;
    private final List<Projectile> firedProjectiles = new ArrayList<>();
    private final List<Projectile> projectilesToRemove = new ArrayList<>();

    private final Rectangle base = new Rectangle();
    private final Circle turret = new Circle();
    private final Rectangle muzzle = new Rectangle();
    private double muzzleLengthCoefficient;

    private final BattleField battleField;

    /**
     * Constructs the tank.
     * Sets the player it is connected to and color based on the player given.
     * Saves battlefield pane for later work.
     * Sets the size of the tank and initiate rotation transformation for each visual component.
     * @param player the {@link Player} of the tank
     * @param battleField the {@link BattleField} where the tank has to be displayed
     */
    Tank(Player player, BattleField battleField) {
        this.player = player;
        this.battleField = battleField;
        color = player == Player.ONE ? Color.RED : Color.GREEN;

        updateSize();

        rotation = new Rotate();

        base.getTransforms().clear();
        muzzle.getTransforms().clear();
        turret.getTransforms().clear();

        base.getTransforms().add(rotation);
        muzzle.getTransforms().add(rotation);
        turret.getTransforms().add(rotation);
    }

    /**
     * Adds every visual component of the tank to the {@link BattleField} pane.
     * Before that removes every component from the {@link BattleField} pane to prevent component multiple appearance.
     */
    public void drawTankElements() {
        battleField.getChildren().remove(base);
        battleField.getChildren().remove(muzzle);
        battleField.getChildren().remove(turret);

        battleField.getChildren().add(base);
        battleField.getChildren().add(muzzle);
        battleField.getChildren().add(turret);
    }

    private void updateSize() {
        this.width = battleField.getCellSize() * 0.3;
        this.length = battleField.getCellSize() * 0.4;
    }

    private void updateRotation(){
        rotation.setAngle(-angle);
        rotation.setPivotX(position.x);
        rotation.setPivotY(position.y);
    }

    /**
     * Calculates rotation transformation and position of the tank based on given time passed and collision detection.
     * Prevents rotation or certain position change when respective collision is detected (with walls of other tank).
     * After such calculations updates visual components' parameters.
     * @param dt passed time frame after the previous position update
     */
    public void move(double dt) {
        if (isDead) return;
        if (!isRotatingL && !isRotatingR) rotationSpeed = 0;
        angle = (angle + 360 + rotationSpeed * dt) % 360;
        if (collision.isDetectedForPlayer(this))
            angle = (angle + 360 - rotationSpeed * dt) % 360;

        if (!isMoving) speed = 0;
        velocity.setAngleAndLength(-Math.PI * angle / 180.0, speed);
        setNewPosition(dt);
        update();
    }

    private void setNewPosition(double dt) {
        Vector2D ds = velocity.getMultiplied(dt);
        Vector2D currentPosition = new Vector2D(position.x, position.y);

        position = currentPosition.getAdded(ds);
        if (!collision.isDetectedForPlayer(this)) return;
        position = currentPosition.getAdded(new Vector2D(ds.x, 0));
        if (!collision.isDetectedForPlayer(this)) return;
        position = currentPosition.getAdded(new Vector2D(0, ds.y));
        if (!collision.isDetectedForPlayer(this)) return;
        position = currentPosition;
    }

    /**
     * Updates visual components' parameters (rotation, position, size, color, stroke)
     * based on precalculated class parameters.
     */
    public void update(){
        tankSpeedLimit = battleField.getCellSize() * 1.7; // cells per second
        bulletSpeedLimit = tankSpeedLimit * 1.2;
        updateSize();
        updateRotation();

        base.setX(position.x - length / 2);
        base.setY(position.y - width / 2);
        base.setWidth(length);
        base.setHeight(width);
        base.setFill(color);
        base.setStroke(Color.BLACK);
        base.setStrokeWidth(1);

        muzzleLengthCoefficient = 0.5;
        muzzle.setWidth(length * muzzleLengthCoefficient);
        muzzle.setHeight(width * 0.2);
        muzzle.setX(position.x);
        muzzle.setY(position.y - muzzle.getHeight() / 2);
        muzzle.setFill(color);
        muzzle.setStroke(Color.BLACK);
        muzzle.setStrokeWidth(1);

        turret.setCenterX(position.x);
        turret.setCenterY(position.y);
        turret.setRadius(width * 0.3);
        turret.setFill(color);
        turret.setStroke(Color.BLACK);
        turret.setStrokeWidth(1);
    }

    /**
     * Starts movement of the tank forwards by changing its linear speed and setting the corresponding flag to true
     */
    public void startMovementForward(){
        speed = tankSpeedLimit;
        isMoving = true;
    }

    /**
     * Starts movement of the tank backwards by changing its linear speed and setting the corresponding flag to true
     */
    public void startMovementBackwards() {
        speed = -tankSpeedLimit;
        isMoving = true;
    }

    /**
     * Stops movement of the tank (regardless of direction)
     * by setting its linear speed to zero and setting the corresponding flag to false
     */
    public void stopMovement(){
        speed = 0;
        isMoving = false;
    }

    /**
     * Starts rotation of the tank to the left by changing rotation speed and setting the corresponding flag to true
     * If there was started rotation to opposite direction, the function stops the rotation.
     * That prevents non-intuitive rotation while opposite-rotation keys are pressed.
     */
    public void startRotationL(){
        rotationSpeed = isRotatingR ? 0 : ROTATION_SPEED;
        isRotatingL = true;
    }

    /**
     * Starts rotation of the tank to the right by changing rotation speed and setting the corresponding flag to true
     * If there was started rotation to opposite direction, the function stops the rotation.
     * That prevents non-intuitive rotation while opposite-rotation keys are pressed.
     */
    public void startRotationR(){
        rotationSpeed = isRotatingL ? 0 : -ROTATION_SPEED;
        isRotatingR = true;
    }

    /**
     * Stops rotation of the tank to the left
     * by setting its rotation speed to zero and setting the corresponding flag to false.
     * If there was requested rotation in opposite direction, it starts such rotation immediately.
     * That prevents delays during rapid rotation direction changes.
     */
    public void stopRotationL(){
        rotationSpeed = isRotatingR ? -ROTATION_SPEED : 0;
        isRotatingL = false;
    }

    /**
     * Stops rotation of the tank to the right
     * by setting its rotation speed to zero and setting the corresponding flag to false.
     * If there was requested rotation in opposite direction, it starts such rotation immediately.
     * That prevents delays during rapid rotation direction changes.
     */
    public void stopRotationR(){
        rotationSpeed = isRotatingR ? ROTATION_SPEED : 0;
        isRotatingR = false;
    }

    /**
     * Handles corresponding for the tank shoot button press.
     * Avoids shooting after tank destruction, handles number of projectiles left,
     * sets starting position of the bullet visually to the muzzle end,
     * calculates vector velocity of new bullet.
     * Creates new bullet with calculated parameters and saves it to the list of fired projectiles.
     */
    public void shoot() {
        if (isDead) return;
        if (projectilesCount == 0) return;
        Vector2D deltaPosition = new Vector2D();
        deltaPosition.setAngleAndLength(-Math.PI * angle / 180.0, length * muzzleLengthCoefficient);

        Vector2D velocity = new Vector2D();
        velocity.setAngleAndLength(-Math.PI * angle / 180.0, bulletSpeedLimit);
        firedProjectiles.add(new Bullet(position.getAdded(deltaPosition), velocity, this, battleField));
        projectilesCount--;
    }

    /**
     * Saves the projectile to the list of projectiles with expired lifetime
     * and increments counter of the projectiles left
     * @param projectile the projectile with expired lifetime
     */
    public void removeProjectile(Projectile projectile) {
        projectilesToRemove.add(projectile);
        projectilesCount++;
    }

    /**
     * Updates each projectile based on passed after previous update time frame
     * and then removes each with expired lifetime
     * @param dt the time passed after previous update
     */
    public void updateProjectile(double dt) {
        for (Projectile projectile : firedProjectiles) {
            projectile.update(dt);
        }
        for (Projectile projectile : projectilesToRemove) {
            firedProjectiles.remove(projectile);
        }
        projectilesToRemove.clear();
    }

    /**
     * Updates size and speed of the projectile while window resizing
     */
    public void updateProjectileResizing() {
        for (Projectile projectile : firedProjectiles) {
            projectile.updateSize();
            projectile.updateSpeed();
        }
    }

    /**
     * Makes the tank to die (become destroyed).
     * Used when the tank collided with a projectile
     */
    public void die() {
        isDead = true;
        color = Color.rgb(70, 70, 70);
        update();
    }

    /**
     * Returns the position of the tank
     * @return the position of the tank
     */
    public Vector2D getPosition() { return position; }

    /**
     * Sets position of the tank to a new given position
     * @param position the new position of the tank
     */
    public void setPosition(Vector2D position) { this.position = position; }

    /**
     * Sets position of the tank based on given coordinates
     * @param x the x-coordinate of the new position
     * @param y the y-coordinate of the new position
     */
    public void setPosition(double x, double y) {
        this.position = new Vector2D(x, y);
    }

    /**
     * Returns the x-coordinate of the tank's position
     * @return the x-coordinate of the tank's position
     */
    public double getX() { return position.x; }

    /**
     * Sets the x-coordinate of the position of the tank
     * @param x the x-coordinate of the new position
     */
    public void setX(double x) { position.x = x; }

    /**
     * Returns the y-coordinate of the tank's position
     * @return the y-coordinate of the tank's position
     */
    public double getY() { return position.y; }

    /**
     * Sets the y-coordinate of the position of the tank
     * @param y the y-coordinate of the new position
     */
    public void setY(double y) { position.y = y; }

    /**
     * Returns the width of the tank (width of its base)
     * @return the width of the tank
     */
    public double getWidth() { return width; }

    /**
     * Sets the width of the tank (width of its base)
     * @param width the width of the tank
     */
    public void setWidth(double width) { this.width = width; }

    /**
     * Returns the length of the tank (length of its base)
     * @return the length of the tank
     */
    public double getLength() { return length; }

    /**
     * Sets the length of the tank (length of its base)
     * @param length the length of the tank
     */
    public void setLength(double length) { this.length = length; }

    /**
     * Saves collision detector which was initialized in the battlefield for further work
     * @param collisionDetector the detector of all collisions on the battlefield
     */
    public void setCollisionDetector(CollisionDetector collisionDetector) { this.collision = collisionDetector; }

    /**
     * Returns the current rotation angle of the tank.
     * @return the angle of tank's rotation in degrees
     */
    public double getAngle() { return angle; }

    /**
     * Sets the current rotation angle of the tank.
     * @param angle the angle of the new tank's rotation in degrees
     */
    public void setAngle(double angle) { this.angle = angle; }

    /**
     * Returns the corresponding for the tank player
     * @return the player who controls the tank
     */
    public Player getPlayer() { return player; }

    /**
     * Returns the list of all shot projectiles with not expired lifetime
     * @return the list of all shot and still displayed projectiles
     */
    public List<Projectile> getFiredProjectiles() { return firedProjectiles; }
}
