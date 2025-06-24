package com.example.projecttanks;

public abstract class Projectile {
    public Vector2D position;
    public Vector2D velocity;
    public BattleField battleField;
    public CollisionDetector collision;


    Projectile(Vector2D position, Vector2D velocity, BattleField battleField) {
        this.position = position;
        this.velocity = velocity;
        this.battleField = battleField;
        this.collision = battleField.collisionDetector;
    }

    public abstract void update(double dt);
    public abstract void calculatePosition(double dt);
    public abstract void updatePosition();
    public abstract void draw();
}

