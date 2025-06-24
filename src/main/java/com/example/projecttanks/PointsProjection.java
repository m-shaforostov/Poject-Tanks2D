package com.example.projecttanks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PointsProjection {
    double newAxisXCircleProjectionLeftPoint;
    double newAxisXCircleProjectionRightPoint;
    double newAxisYCircleProjectionLeftPoint;
    double newAxisYCircleProjectionRightPoint;

    List<Double> newAxisXPlayerCircleProjection;
    List<Double> newAxisYPlayerCircleProjection;

    PointsProjection(double circleX, double circleY, double radius, List<Vector2D> playerCorners, double angle) {
        Vector2D newAxisX = new Vector2D();
        Vector2D newAxisY = new Vector2D();
        newAxisX.setAngleAndLength(-Math.PI * angle / 180.0, 1);
        newAxisY.setAngleAndLength(-Math.PI * (angle + 90) / 180.0, 1);

        Vector2D circle = new Vector2D(circleX, circleY);
        newAxisXCircleProjectionLeftPoint = circle.dot(newAxisX) - radius;
        newAxisXCircleProjectionRightPoint = circle.dot(newAxisX) + radius;

        newAxisYCircleProjectionLeftPoint = circle.dot(newAxisY) - radius;
        newAxisYCircleProjectionRightPoint = circle.dot(newAxisY) + radius;

        newAxisXPlayerCircleProjection = new ArrayList<>();
        newAxisYPlayerCircleProjection = new ArrayList<>();
        for (Vector2D playerCorner : playerCorners) {
            newAxisXPlayerCircleProjection.add(playerCorner.dot(newAxisX));
            newAxisYPlayerCircleProjection.add(playerCorner.dot(newAxisY));
        }
    }

    public boolean isWithinBoundsX() {
        return newAxisXCircleProjectionLeftPoint >= Collections.min(newAxisXPlayerCircleProjection) &&
                newAxisXCircleProjectionLeftPoint <= Collections.max(newAxisXPlayerCircleProjection) ||
                newAxisXCircleProjectionRightPoint >= Collections.min(newAxisXPlayerCircleProjection) &&
                        newAxisXCircleProjectionRightPoint <= Collections.max(newAxisXPlayerCircleProjection);
    }

    public boolean isWithinBoundsY() {
        return newAxisYCircleProjectionLeftPoint >= Collections.min(newAxisYPlayerCircleProjection) &&
                newAxisYCircleProjectionLeftPoint <= Collections.max(newAxisYPlayerCircleProjection) ||
               newAxisYCircleProjectionRightPoint >= Collections.min(newAxisYPlayerCircleProjection) &&
                        newAxisYCircleProjectionRightPoint <= Collections.max(newAxisYPlayerCircleProjection);
    }
}
