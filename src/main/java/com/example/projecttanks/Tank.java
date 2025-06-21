package com.example.projecttanks;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class Tank {
    public Vector2D position;
    public Vector2D direction = new Vector2D();
    public Vector2D velocity = new Vector2D();
    public Player player;
    private Color color;
    private Integer bulletCount;
    public Integer killCount;
    public Integer deathCount;
    private List<Bonus> bonusesEarned = new ArrayList<>();
    public Bonus activeBonus = null;


    Tank(Vector2D position, Player player) {
        this.position = position;
        this.player = player;
        color = player == Player.ONE ? Color.RED : Color.LIGHTGREEN;
    }
}
