package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;

/**
 * @author Ursula El-Khoury (340994)
 * @author Omar Hammoud (341700)
 */

/**
 * Public final class offering static methods that can convert
 * between the WGS 84 and the Web Mercator coordinates
 */
public final class WebMercator {

    private WebMercator() {}

    /**
     * Public method that gives the coordinate x from the longitude
     * @param lon : the longitude
     * @return the coordinate x in radians of a point projected from the longitude given
     */
    public static double x(double lon) {
        return (lon + Math.PI) / (2 * Math.PI);
    }

    /**
     * Public method that gives the coordinate y from the latitude
     * @param lat : the latitude
     * @return the coordinate y in radians of a point projected from the latitude given
     */
    public static double y(double lat) {
        return (Math.PI - Math2.asinh(Math.tan(lat))) / (2 * Math.PI);
    }

    /**
     * Public method that gives the longitude from the coordinate x
     * @param x : the coordinate x
     * @return the longitude lon in radians of a point projected from the coordinate x given
     */
    public static double lon(double x) {
        return (2 * Math.PI * x) - Math.PI;
    }

    /**
     * Public method that gives the latitude from the coordinate y
     * @param y : the coordinate y
     * @return the latitude lat in radians of a point projected from the coordinate y given
     */
    public static double lat(double y) {
        return Math.atan(Math.sinh(Math.PI - 2 * Math.PI * y));
    }
}
