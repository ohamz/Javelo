package ch.epfl.javelo.projection;

import ch.epfl.javelo.Preconditions;

import static java.lang.Math.scalb;

/**
 * @author Ursula El-Khoury (340994)
 * @author Omar Hammoud (341700)
 */

public record PointWebMercator(double x, double y) {
    private final static int MAGIC_VALUE = 8;

    /**
     * Public constructor of the object PointWebMercator that checks if the parameters are valid
     * @param x : coordinate x
     * @param y : coordinate y
     */
    public PointWebMercator {
        Preconditions.checkArgument(x >= 0 && x <= 1 && y >= 0 && y <= 1);
    }

    /**
     * Public method that gives the PointWebMercator zoomed at given zoom level
     * @param zoomLevel : given zoom level
     * @param x : coordinate x
     * @param y : coordinate y
     * @return the point of coordinates x and y with the given zoom level
     */
    public static PointWebMercator of(int zoomLevel, double x, double y) {
        double newX = scalb(x, -(MAGIC_VALUE + zoomLevel));
        double newY = scalb(y, -(MAGIC_VALUE + zoomLevel));
        return new PointWebMercator(newX, newY);
    }

    /**
     * Public method that gives a new PointWebMercator corresponding to the pointCh given in parameter
     * @param pointCh : given PointCh
     * @return the point Web Mercator corresponding to the point given in swiss coordinates
     */
    public static PointWebMercator ofPointCh(PointCh pointCh) {
        return new PointWebMercator(WebMercator.x(pointCh.lon()), WebMercator.y(pointCh.lat()));
    }

    /**
     * Public method that gives the coordinate x at the zoom level given
     * @param zoomLevel : given zoom level
     * @return the coordinate x at the given zoom level
     */
    public double xAtZoomLevel(int zoomLevel) {
        return scalb(x, MAGIC_VALUE + zoomLevel);
    }

    /**
     * Public method that gives the coordinate y at the zoom level given
     * @param zoomLevel : given zoom level
     * @return the coordinate y at the given zoom level
     */
    public double yAtZoomLevel(int zoomLevel) {
        return scalb(y, MAGIC_VALUE + zoomLevel);
    }

    /**
     * Public method that gives the longitude of the PointWebMercator
     * @return the longitude of the point in radians
     */
    public double lon() {
        return WebMercator.lon(x);
    }

    /**
     * Public method that gives the latitude of the PointWebMercator
     * @return the latitude of the point in radians
     */
    public double lat() {
        return WebMercator.lat(y);
    }

    /**
     * Public method that gives the PointCh corresponding to the PointWebMercator if it is in the SwissBounds
     * @return the point in swiss coordinates of the current's point position (this),
     * or null if the point isn't defined within the swiss territory
     **/
    public PointCh toPointCh() {
        double lon = this.lon();
        double lat = this.lat();
        double e = Ch1903.e(lon, lat);
        double n = Ch1903.n(lon, lat);
        if (SwissBounds.containsEN(e, n)) {
            return new PointCh(e, n);
        } else {
            return null;
        }
    }

}
