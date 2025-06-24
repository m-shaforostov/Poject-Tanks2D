package com.example.projecttanks;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Bullet extends Projectile {
    private int LIVE_TIME_SEC = 10;
    private final long start = System.currentTimeMillis();
    private final long end = start + (LIVE_TIME_SEC) * 1000;

    public static double RADIUS = 5;
    public static Color BULLET_COLOR = Color.BLACK;
    Circle bulletCircle;

    Bullet(Vector2D position, Vector2D velocity, Tank player, BattleField battleField) {
        super(position, velocity, player, battleField);
        draw();
    }

    public void update(double dt) {
        if(System.currentTimeMillis() >= end) explode();
        calculatePosition(dt);
        updatePosition();
    }

    public void calculatePosition(double dt) {
        Vector2D currentPosition = new Vector2D(position.x, position.y);
        position = currentPosition.getAdded(velocity.getMultiplied(dt));
        if (collision.isDetectedBulletHorizontal(this))
            velocity.x *= -1;
        if (collision.isDetectedBulletVertical(this))
            velocity.y *= -1;
        position = currentPosition.getAdded(velocity.getMultiplied(dt));
    }

    public void updatePosition() {
        bulletCircle.setCenterX(position.x);
        bulletCircle.setCenterY(position.y);
    }

    public void draw() {
        bulletCircle = new Circle(position.x, position.y, RADIUS, BULLET_COLOR);
        battleField.getChildren().remove(bulletCircle);
        battleField.getChildren().add(bulletCircle);
    }

    private void explode() {
        battleField.getChildren().remove(bulletCircle);
        player.removeProjectile(this);
    }
}
