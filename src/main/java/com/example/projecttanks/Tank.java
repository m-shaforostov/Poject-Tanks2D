package com.example.projecttanks;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

import java.util.ArrayList;
import java.util.List;

public class Tank {
    public Vector2D position;
    public double width;
    public double length;

    public Vector2D velocity = new Vector2D();
    public double speed = 0;
    public double angle = 0;
    private double rotationSpeed = 0;
    Rotate rotation;

    private static double ROTATION_SPEED = 300; // angle per second
    private double tankSpeedLimit; // px per second
    private double bulletSpeed; // px per second

    private boolean isMoving = false;
    private boolean isRotatingL = false;
    private boolean isRotatingR = false;

    public Player player;
    private Color color;
    private CollisionDetector collision;

    private static int PROJECTILES_LIMIT = 5;
    private int projectilesCount = PROJECTILES_LIMIT;
    public List<Projectile> firedProjectiles = new ArrayList<>();
    public List<Projectile> projectilesToRemove = new ArrayList<>();

    public int killCount;
    public int deathCount;

    private List<Bonus> bonusesEarned = new ArrayList<>();
    public Bonus activeBonus = null;

    Rectangle base = new Rectangle();
    Circle turret = new Circle();
    Rectangle muzzle = new Rectangle();

    BattleField battleField;

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

    public void setCollisionDetector(CollisionDetector collisionDetector) {
        this.collision = collisionDetector;
    }

    public void updateSize() {
        this.width = battleField.cellSize * 0.3;
        this.length = battleField.cellSize * 0.5;
    }

    public void setPosition(double x, double y) {
        this.position = new Vector2D(x, y);
    }

    private void updateRotation(){
        rotation.setAngle(-angle);
        rotation.setPivotX(position.x);
        rotation.setPivotY(position.y);
    }

    public void move(double dt) {
        if (!isRotatingL && !isRotatingR) rotationSpeed = 0;
        angle = (angle + 360 + rotationSpeed * dt) % 360;
        if (collision.isDetectedForPlayer(this))
            angle = (angle + 360 - rotationSpeed * dt) % 360;

        if (!isMoving) speed = 0;
        velocity.setAngleAndLength(-Math.PI * angle / 180.0, speed);
        setNewPosition(dt);
        update();
        updateBullets(dt);
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
        // TODO
        if (collision.isDetectedForPlayer(this)) System.out.println("PROBLEM!!");
    }

    public void update(){
        tankSpeedLimit = battleField.cellSize * 1.7; // cells per second
        bulletSpeed = tankSpeedLimit;
        updateSize();
        updateRotation();

        base.setX(position.x - length / 2);
        base.setY(position.y - width / 2);
        base.setWidth(length);
        base.setHeight(width);
        base.setFill(color);
        base.setStroke(Color.BLACK);
        base.setStrokeWidth(1);

        muzzle.setWidth(length * 0.5);
        muzzle.setHeight(width * 0.2);
        muzzle.setX(position.x);
        muzzle.setY(position.y - muzzle.getHeight() / 2);
        muzzle.setFill(color);
        muzzle.setStroke(Color.BLACK);
        muzzle.setStrokeWidth(1);

        turret.setCenterX(position.x);
        turret.setCenterY(position.y);
        turret.setRadius(width * 0.4);
        turret.setFill(color);
        turret.setStroke(Color.BLACK);
        turret.setStrokeWidth(1);
    }

    public void startMovementForward(){
        speed = tankSpeedLimit;
        isMoving = true;
    }

    public void startMovementBackwards() {
        speed = -tankSpeedLimit;
        isMoving = true;
    }

    public void stopMovement(){
        speed = 0;
        isMoving = false;
    }

    public void startRotationL(){
        rotationSpeed = isRotatingR ? 0 : ROTATION_SPEED;
        isRotatingL = true;
    }
    public void startRotationR(){
        rotationSpeed = isRotatingL ? 0 : -ROTATION_SPEED;
        isRotatingR = true;
    }

    public void stopRotationL(){
        rotationSpeed = isRotatingR ? -ROTATION_SPEED : 0;
        isRotatingL = false;
    }
    public void stopRotationR(){
        rotationSpeed = isRotatingR ? ROTATION_SPEED : 0;
        isRotatingR = false;
    }

    public void shoot() {
        if (projectilesCount == 0) return;
        Vector2D velocity = new Vector2D();
        velocity.setAngleAndLength(-Math.PI * angle / 180.0, bulletSpeed);
        firedProjectiles.add(new Bullet(position, velocity, this, battleField));
        projectilesCount--;
    }

    public void removeProjectile(Projectile projectile) {
        projectilesToRemove.add(projectile);
        projectilesCount++;
    }

    private void updateBullets(double dt) {
        for (Projectile projectile : firedProjectiles) {
            projectile.update(dt);
        }
        for (Projectile projectile : projectilesToRemove) {
            firedProjectiles.remove(projectile);
        }
        projectilesToRemove.clear();
    }

}
