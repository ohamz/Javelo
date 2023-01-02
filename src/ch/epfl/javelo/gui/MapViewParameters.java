package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import javafx.geometry.Point2D;

/**
 * @author Ursula El-Khoury (340994)
 * @author Omar Hammoud (341700)
 */

/**
 * Public record that represents the settings of the basemap presented in the GUI,
 * it has 3 attributes (zoom level, x coordinate, y coordinate)
 */
public record MapViewParameters(int zoomLevel, double x, double y) {

    /**
     * Public method that gives the topLeft coordinates in the form of a Point2D vector
     * @return : a Point2D
     */
    public Point2D topLeft() {
        return new Point2D(x, y);
    }

    /**
     * Public method that gives a new map with the same zoom level but different topLeft coordinates
     * @param x : the x coordinate of the new topLeft
     * @param y : the y coordinate of the new topLeft
     * @return the new MapViewParameters
     */
    public MapViewParameters withMinXY(double x, double y) {
        return new MapViewParameters(this.zoomLevel, x, y);
    }

    /**
     * Public method that gives the PointWebMercator at the given x and y coordinates
     * @param x : the x coordinate of the point
     * @param y : the y coordinate of the point
     * @return the PointWebMercator
     */
    public PointWebMercator pointAt(double x, double y) {
        return PointWebMercator.of(zoomLevel, x + this.x, y + this.y);
    }

    /**
     * Public method that gives the x coordinate of the point at the given zoom level
     * @param point : the given PointWebMercator
     * @return the x coordinate that is zoomed
     */
    public double viewX(PointWebMercator point) {
        return point.xAtZoomLevel(zoomLevel) - this.x;
    }

    /**
     * Public method that gives the y coordinate of the point at the given zoom level
     * @param point : the given PointWebMercator
     * @return the y coordinate that is zoomed
     */
    public double viewY(PointWebMercator point) {
        return point.yAtZoomLevel(zoomLevel) - this.y;
    }

}
