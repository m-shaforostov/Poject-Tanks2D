package com.example.projecttanks;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Bullet extends Projectile {
    private int LIVE_TIME_SEC = 10;
    private final long start = System.currentTimeMillis();
    private final long end = start + (LIVE_TIME_SEC) * 1000;

    public double radius;
    public static Color BULLET_COLOR = Color.BLACK;
    Circle bulletCircle = new Circle();

    private static final double disappearanceTime = 0.1;
    private double disappearanceStep;

    Bullet(Vector2D position, Vector2D velocity, Tank player, BattleField battleField) {
        super(position, velocity, player, battleField);
        updateSize();
        draw();
        player.drawTankElements();
    }

    @Override
    public void update(double dt) {
        if(isDestroyed || System.currentTimeMillis() >= end) eliminate();
        else calculatePosition(dt);
        updatePosition();
    }

    @Override
    public void updateSize() {
        radius = player.getWidth() * 0.1;
        bulletCircle.setRadius(radius);
        disappearanceStep = radius * MainGame.REFRESH_TIME_MS / (disappearanceTime * 1000);
    }

    @Override
    public void updateSpeed(){
        speed = player.bulletSpeedLimit;
        Vector2D direction = velocity.getNormalized();
        velocity = direction.getMultiplied(speed);
    }

    @Override
    public void calculatePosition(double dt) {
        Vector2D currentPosition = new Vector2D(position.x, position.y);
        position = currentPosition.getAdded(velocity.getMultiplied(dt));
        if (collision.isDetectedBulletHorizontal(this))
            velocity.x *= -1;
        if (collision.isDetectedBulletVertical(this))
            velocity.y *= -1;
        if (collision.isDetectedBulletWithPlayer(this, battleField.firstPlayer)){
            kill(battleField.firstPlayer);
        }
        else if (collision.isDetectedBulletWithPlayer(this, battleField.secondPlayer)){
            kill(battleField.secondPlayer);
        }
        position = currentPosition.getAdded(velocity.getMultiplied(dt));
    }

    @Override
    public void updatePosition() {
        bulletCircle.setCenterX(position.x);
        bulletCircle.setCenterY(position.y);
    }

    @Override
    public void draw() {
        bulletCircle = new Circle(position.x, position.y, radius, BULLET_COLOR);
        battleField.getChildren().remove(bulletCircle);
        battleField.getChildren().add(bulletCircle);
    }

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

    @Override
    public void kill(Tank victim){
        isDestroyed = true;
        battleField.getChildren().remove(bulletCircle);
        player.removeProjectile(this);

        gameState.murdered(player, victim);
        victim.die();
    }
}
