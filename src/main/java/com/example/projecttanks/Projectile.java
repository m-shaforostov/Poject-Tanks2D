package com.example.projecttanks;

/**
 * An abstract class representing a projectile which can be shot by a tank.
 * Each projectile has its position, vector velocity, linear speed and owner (a tank).
 * It can interact with battlefield (walls) and tanks.
 * There are different types of projectile (bullets, rockets, etc.)
 */
public abstract class Projectile {
    /** Current position of the projectile in pixels.*/
    public Vector2D position;

    /** Current position of the projectile in pixels. */
    public Vector2D velocity;
    /** Current linear speed of the projectile */
    public double speed;
    /** The tank (player) who fired the projectile */
    public Tank player;
    /** The battlefield where the projectile moves */
    public BattleField battleField;
    /** The game state which tracks rounds, kills, wins and other events. */
    public GameState gameState;
    /** The collision detector for handling all collisions */
    public CollisionDetector collision;

    /** Indicates whether the projectile is ready for removal (is eliminated or exploded) */
    public boolean isDestroyed = false;

    /**
     * Constructs a new projectile
     * @param position the initial position of the projectile
     * @param velocity the initial velocity vector of the projectile
     * @param player the tank that fired the projectile
     * @param battleField the battlefield where the projectile moves
     */
    Projectile(Vector2D position, Vector2D velocity, Tank player, BattleField battleField) {
        this.position = position;
        this.velocity = velocity;
        this.player = player;
        this.battleField = battleField;
        this.gameState = battleField.gameState;
        this.collision = battleField.getCollisionDetector();
    }

    /**
     * Updates the projectile's state for the given time change
     * @param dt the time passed after the previous update
     */
    public abstract void update(double dt);
    /** Updates the size of the projectile */
    public abstract void updateSize();
    /** Updates the speed of the projectile */
    public abstract void updateSpeed();
    /** Updates position of the projectile */
    public abstract void updatePosition();

    /**
     * Calculates new position of the projectile for the given time change
     * @param dt the time passed after the previous update
     */
    public abstract void calculatePosition(double dt);
    /** Draws the projectile on the battlefield pane */
    public abstract void draw();

    /** Steadily collapses the projectile and removes from existence */
    public abstract void eliminate();

    /**
     * Kills the corresponding tank and removes the projectile from existence
     * @param victim the tank which collided with the projectile
     */
    public abstract void kill(Tank victim);

    /** @return the current x-coordinate of the projectile */
    public double getX() { return position.x; }
    /** @return the current y-coordinate of the projectile */
    public double getY(){ return position.y; }

    /**
     * Sets a new position of the projectile.
     * @param x the new x-coordinate
     * @param y the new y-coordinate
     */
    public void setPosition(double x, double y) { position = new Vector2D(x, y); }
}

