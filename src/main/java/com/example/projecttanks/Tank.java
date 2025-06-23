package com.example.projecttanks;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

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
    private static double speedLimit; // px per second

    private boolean isMoving = false;
    private boolean isRotatingL = false;
    private boolean isRotatingR = false;

    public Player player;
    private Color color;

    private Integer bulletCount;
    public Integer killCount;
    public Integer deathCount;
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
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public void setPosition(Vector2D position) {
        this.position = position;
    }

    private void updateRotation(){
        rotation.setAngle(-angle);
        rotation.setPivotX(position.x);
        rotation.setPivotY(position.y);
    }

    public void move(double dt) {
        if (!isRotatingL && !isRotatingR) rotationSpeed = 0;
        angle = (angle + 360 + rotationSpeed * dt) % 360;

        updateRotation();

        if (!isMoving) speed = 0;
        velocity.setAngleAndLength(-Math.PI * angle / 180.0, speed);
        Vector2D ds = velocity.getMultiplied(dt);
        position = getNewPosition(ds);
        update();
    }

    private Vector2D getNewPosition(Vector2D ds) {
        Vector2D newPos = position.getAdded(ds);
        if (checkBounds(newPos)) return newPos;
        newPos = position.getAdded(new Vector2D(ds.x, 0));
        if (checkBounds(newPos)) return newPos;
        newPos = position.getAdded(new Vector2D(0, ds.y));
        if (checkBounds(newPos)) return newPos;
        return position;
    }

    private boolean checkBounds(Vector2D newPos) {
        return newPos.x >= 0 && newPos.x < battleField.getPrefWidth() && newPos.y >= 0 && newPos.y < battleField.getPrefHeight();
    }

    public void init(){
        speedLimit = battleField.cellSize * 1.7; // cells per second
        speed = 0;
        rotationSpeed = 0;
        angle = 0;
        isMoving = false;
        isRotatingL = false;
        isRotatingR = false;

        rotation = new Rotate(0, position.x, position.y);

        base.getTransforms().clear();
        muzzle.getTransforms().clear();
        turret.getTransforms().clear();

        base.getTransforms().add(rotation);
        muzzle.getTransforms().add(rotation);
        turret.getTransforms().add(rotation);

        update();
    }

    public void update(){
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
        speed = speedLimit;
        isMoving = true;
    }

    public void startMovementBackwards() {
        speed = -speedLimit;
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
    }

}
