package com.example.projecttanks;

public abstract class Projectile {
    public Vector2D position;
    public Vector2D velocity;
    public double speed;
    public Tank player;
    public BattleField battleField;
    public GameState gameState;
    public CollisionDetector collision;

    public boolean isDestroyed = false;

    Projectile(Vector2D position, Vector2D velocity, Tank player, BattleField battleField) {
        this.position = position;
        this.velocity = velocity;
        this.player = player;
        this.battleField = battleField;
        this.gameState = battleField.gameState;
        this.collision = battleField.collisionDetector;
    }

    public double getX() {
        return position.x;
    }
    public double getY(){
        return position.y;
    }
    public void setPosition(double x, double y) {
        position = new Vector2D(x, y);
    }

    public abstract void update(double dt);
    public abstract void updateSize();
    public abstract void updateSpeed();
    public abstract void calculatePosition(double dt);
    public abstract void updatePosition();
    public abstract void draw();

    public abstract void eliminate();
    public abstract void kill(Tank player);
}

