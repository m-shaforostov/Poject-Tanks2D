package com.example.projecttanks;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Rotate;

import java.util.Random;

public class BonusBox {
    private final Vector2D position;
    private final int x;
    private final int y;
    private double size;
    private static final double SIZE_COEFICIENT = 0.33;
    private final int angle;

    private final ImageView imageView = new ImageView();
    public Image image = new Image("file:speedBoost.png", 50, 50, false, false);

    private final BattleField battleField;
    private final Random rand = new Random();

    BonusBox(BattleField battleField, int x, int y) {
        this.battleField = battleField;
        this.x = x;
        this.y = y;
        this.position = new Vector2D(
                (x + 0.5) * battleField.getCellSize(),
                (y + 0.5) * battleField.getCellSize()
        );
        this.size = battleField.getCellSize() * SIZE_COEFICIENT;
        this.angle = rand.nextInt(360);
        draw();
    }

    public void update(){
        size = battleField.getCellSize() * SIZE_COEFICIENT;
        position.x = (x + 0.5) * battleField.getCellSize() - size / 2;
        position.y = (y + 0.5) * battleField.getCellSize() - size / 2;
        imageView.setImage(image);
//        imageView.setRotate(angle);
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        imageView.setX(position.x);
        imageView.setY(position.y);
    }

    public void draw() {
        battleField.getChildren().remove(imageView);
        battleField.getChildren().add(imageView);
    }

    public Vector2D getPosition() { return position; }

    public double getWidth() { return size; }

    public double getHeight() { return size; }

    public double getAngle() { return angle; }
}
