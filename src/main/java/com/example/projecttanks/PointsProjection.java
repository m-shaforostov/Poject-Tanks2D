package com.example.projecttanks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PointsProjection {
    double newAxisXCornerProjection;
    double newAxisYCornerProjection;

    List<Double> newAxisXPlayerCornerProjection;
    List<Double> newAxisYPlayerCornerProjection;

    PointsProjection(double cornerX, double cornerY, List<Vector2D> playerCorners, double angle) {
        Vector2D newAxisX = new Vector2D();
        Vector2D newAxisY = new Vector2D();
        newAxisX.setAngleAndLength(-Math.PI * angle / 180.0, 1);
        newAxisY.setAngleAndLength(-Math.PI * (angle + 90) / 180.0, 1);

        Vector2D corner = new Vector2D(cornerX, cornerY);
        newAxisXCornerProjection = corner.dot(newAxisX);
        newAxisYCornerProjection = corner.dot(newAxisY);

        newAxisXPlayerCornerProjection = new ArrayList<>();
        newAxisYPlayerCornerProjection = new ArrayList<>();
        for (Vector2D playerCorner : playerCorners) {
            newAxisXPlayerCornerProjection.add(playerCorner.dot(newAxisX));
            newAxisYPlayerCornerProjection.add(playerCorner.dot(newAxisY));
        }
    }

    public boolean isWithinBoundsX() {
        return newAxisXCornerProjection >= Collections.min(newAxisXPlayerCornerProjection) &&
                newAxisXCornerProjection <= Collections.max(newAxisXPlayerCornerProjection);
    }

    public boolean isWithinBoundsY() {
        return newAxisYCornerProjection >= Collections.min(newAxisYPlayerCornerProjection) &&
                newAxisYCornerProjection <= Collections.max(newAxisYPlayerCornerProjection);
    }
}
