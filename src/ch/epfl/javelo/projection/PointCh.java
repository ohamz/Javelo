package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

/**
 * @author Ursula El-Khoury (340994)
 * @author Omar Hammoud (341700)
 */

public record PointCh(double e, double n) {

    /**
     * Public constructor of the Record PointCh that throws IllegalArgumentException
     * Checks that e and n are in the swiss territory
     * @param e : given e coordinate
     * @param n : given n coordinate
     */
    public PointCh {
        Preconditions.checkArgument(SwissBounds.containsEN(e, n));
    }

    /**
     * Public method that gives the squared distance between two points
     * @param that : An objet PointCh with e and n as attributes
     * @return the distance squared between this (current object) and that
     */
    public double squaredDistanceTo(PointCh that) {
        return Math2.squaredNorm(that.e - this.e, that.n - this.n);
    }

    /**
     * Public method that gives the distance between two points
     * @param that : An objet PointCh with e and n as attributes
     * @return the distance between this (current object) and that
     */
    public double distanceTo(PointCh that) {
        return Math.sqrt(squaredDistanceTo(that));
    }

    /**
     * Public method that gives the longitude of the point in radians
     * @return the longitude of the current point in the system WGS84 in radians
     */
    public double lon() {
        return Ch1903.lon(e, n);
    }

    /**
     * Public method that gives the latitude of the point in radians
     * @return the latitude of the current point in the system WGS854 in radians
     */
    public double lat() {
        return Ch1903.lat(e, n);
    }
}