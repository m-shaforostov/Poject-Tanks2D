package com.example.projecttanks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A helper class used to project the circle (bullet or corner) with specified radius and the tank's corners
 * onto rotated axes which corresponds to the local axes of the tank, which may be rotated.
 * It is used to perform collision detection using the Separating Axis Theorem (SAT)
 */
public class PointsProjection {
    private final double newAxisXCircleProjectionLeftPoint;
    private final double newAxisXCircleProjectionRightPoint;
    private final double newAxisYCircleProjectionLeftPoint;
    private final double newAxisYCircleProjectionRightPoint;

    private final List<Double> newAxisXPlayerCircleProjection;
    private final List<Double> newAxisYPlayerCircleProjection;

    /**
     * Constructs the points projection.
     * @param circleX the x-coordinate of the circle center (bullet or entity's corner)
     * @param circleY the y-coordinate of the circle center (bullet or entity's corner)
     * @param radius the radius of the circle (bullet or entity's corner)
     * @param playerCorners the list of four corners of the tank
     * @param angle the rotation angle of the tank in degrees
     */
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

    /**
     * Checks whether the circle projection overlaps with the tank's projection on the local X axis
     * @return true if there is overlap, false otherwise
     */
    public boolean isWithinBoundsX() {
        return newAxisXCircleProjectionLeftPoint >= Collections.min(newAxisXPlayerCircleProjection) &&
                newAxisXCircleProjectionLeftPoint <= Collections.max(newAxisXPlayerCircleProjection) ||
                newAxisXCircleProjectionRightPoint >= Collections.min(newAxisXPlayerCircleProjection) &&
                        newAxisXCircleProjectionRightPoint <= Collections.max(newAxisXPlayerCircleProjection);
    }

    /**
     * Checks whether the circle projection overlaps with the tank's projection on the local Y axis
     * @return true if there is overlap, false otherwise
     */
    public boolean isWithinBoundsY() {
        return newAxisYCircleProjectionLeftPoint >= Collections.min(newAxisYPlayerCircleProjection) &&
                newAxisYCircleProjectionLeftPoint <= Collections.max(newAxisYPlayerCircleProjection) ||
               newAxisYCircleProjectionRightPoint >= Collections.min(newAxisYPlayerCircleProjection) &&
                        newAxisYCircleProjectionRightPoint <= Collections.max(newAxisYPlayerCircleProjection);
    }
}
