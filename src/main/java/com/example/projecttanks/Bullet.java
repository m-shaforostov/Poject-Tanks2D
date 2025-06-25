package com.example.projecttanks;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Represents the bullet, which is a one of the bullet's types.
 * Bullets look like a circle, can move, bounce off the walls and kill one tank on contact.
 */
public class Bullet extends Projectile {
    private final int LIVE_TIME_SEC = 10;
    private final static Color BULLET_COLOR = Color.BLACK;
    private final long start = System.currentTimeMillis();
    private final long end = start + (LIVE_TIME_SEC) * 1000;

    /** Radius of the bullet (radius of the circle) */
    public double radius;
    private Circle bulletCircle = new Circle();

    private static final double disappearanceTime = 0.1;
    private double disappearanceStep;

    /**
     * Constructs a bullet at the given position and velocity vector
     * @param position the starting position of the bullet
     * @param velocity the starting velocity vector
     * @param player the player (tank) who fired the bullet
     * @param battleField the battlefield the bullet moves on
     */
    Bullet(Vector2D position, Vector2D velocity, Tank player, BattleField battleField) {
        super(position, velocity, player, battleField);
        updateSize();
        draw();
        player.drawTankElements();
    }

    /**
     * Updates the bullet's state for the given time change
     * @param dt the time passed after the previous update
     */
    @Override
    public void update(double dt) {
        if(isDestroyed || System.currentTimeMillis() >= end) eliminate();
        else calculatePosition(dt);
        updatePosition();
    }

    /** Updates the size of the bullet */
    @Override
    public void updateSize() {
        radius = player.getWidth() * 0.1;
        bulletCircle.setRadius(radius);
        disappearanceStep = radius * MainGame.REFRESH_TIME_MS / (disappearanceTime * 1000);
    }

    /** Updates the speed of the bullet */
    @Override
    public void updateSpeed(){
        speed = player.bulletSpeedLimit;
        Vector2D direction = velocity.getNormalized();
        velocity = direction.getMultiplied(speed);
    }

    /**
     * Calculates new position of the bullet for the given time change
     * @param dt the time passed after the previous update
     */
    @Override
    public void calculatePosition(double dt) {
        Vector2D currentPosition = new Vector2D(position.x, position.y);
        position = currentPosition.getAdded(velocity.getMultiplied(dt));
        if (collision.isDetectedBulletVertical(this))
            velocity.x *= -1;
        if (collision.isDetectedBulletHorizontal(this))
            velocity.y *= -1;
        if (collision.isDetectedBulletWithPlayer(this, battleField.firstPlayer)){
            kill(battleField.firstPlayer);
        }
        else if (collision.isDetectedBulletWithPlayer(this, battleField.secondPlayer)){
            kill(battleField.secondPlayer);
        }
        position = currentPosition.getAdded(velocity.getMultiplied(dt));
    }

    /** Updates position of the bullet */
    @Override
    public void updatePosition() {
        bulletCircle.setCenterX(position.x);
        bulletCircle.setCenterY(position.y);
    }

    /** Draws the bullet on the battlefield pane */
    @Override
    public void draw() {
        bulletCircle = new Circle(position.x, position.y, radius, BULLET_COLOR);
        battleField.getChildren().remove(bulletCircle);
        battleField.getChildren().add(bulletCircle);
    }

    /** Steadily collapses the bullet by changing its radius
     * and removes from existence when collapsed */
    @Override
    public void eliminate() {
        isDestroyed = true;
        radius -= disappearanceStep;
        bulletCircle.setRadius(radius);
        if (radius <= 0) {
            battleField.getChildren().remove(bulletCircle);
            player.removeProjectile(this);
        }
    }

    /**
     * Kills the corresponding tank and removes the bullet from existence
     * @param victim the tank which collided with the bullet
     */
    @Override
    public void kill(Tank victim){
        isDestroyed = true;
        battleField.getChildren().remove(bulletCircle);
        player.removeProjectile(this);

        gameState.murdered(player, victim);
        victim.die();
    }
}
