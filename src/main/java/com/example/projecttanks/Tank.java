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
    private static double speedLimit; // px per second

    private boolean isMoving = false;
    private boolean isRotating = false;

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
        if (!isRotating) rotationSpeed = 0;
        angle = (angle + 360 + rotationSpeed * dt) % 360;

        updateRotation();

        if (!isMoving) speed = 0;
        velocity.setAngleAndLength(-Math.PI * angle / 180.0, speed);
        position.add(velocity.getMultiplied(dt));
        update();
    }

    public void init(){
        speedLimit = battleField.cellSize * 1.7; // cells per second
        speed = 0;
        rotationSpeed = 0;
        angle = 0;
        isMoving = false;
        isRotating = false;

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

    public void rotateLeft() {
        rotationSpeed = ROTATION_SPEED;
        startRotation();
    }

    public void rotateRight() {
        rotationSpeed = -ROTATION_SPEED;
        startRotation();
    }

    public void moveForward() {
        speed = speedLimit;
        startMovement();
    }

    public void moveBack() {
        speed = -speedLimit;
        startMovement();
    }

    public void startMovement(){
        isMoving = true;
    }

    public void stopMovement(){
        isMoving = false;
    }

    public void startRotation(){
        isRotating = true;
    }

    public void stopRotation(){
        isRotating = false;
    }

    public void shoot() {
    }

}
