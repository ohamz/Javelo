package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import static java.lang.Double.NaN;
import static java.lang.Double.POSITIVE_INFINITY;

/**
 * @author Ursula El-Khoury (340994)
 * @author Omar Hammoud (341700)
 */


public record RoutePoint(PointCh point, double position, double distanceToReference) {

    /**
     * RoutePoint that does not exist
     */
    public final static RoutePoint NONE = new RoutePoint(null, NaN, POSITIVE_INFINITY);

    /**
     * Public method that creates a similar route that is shifted with a given difference
     *
     * @param positionDifference : given position difference
     * @return an identical point to this but with a position difference
     */
    public RoutePoint withPositionShiftedBy(double positionDifference) {
        return positionDifference == 0 ?
                this : new RoutePoint(this.point, this.position + positionDifference, this.distanceToReference);
    }

    /**
     * Public method that gives the route that has a lower distance to the referenced point
     *
     * @param that : another RoutePoint
     * @return the point which has the lower distance to the reference
     */
    public RoutePoint min(RoutePoint that) {
        return this.distanceToReference <= that.distanceToReference ? this : that;
    }

    /**
     * Public method that gives the route that has a lower distance to the referenced point
     *
     * @param thatPoint               : given point (PointCh)
     * @param thatPosition            : given position
     * @param thatDistanceToReference : give distance
     * @return the initial point if its distance to the reference is lower than the distance given as parameter,
     * and a new RoutePoint with the given information otherwise
     */
    public RoutePoint min(PointCh thatPoint, double thatPosition, double thatDistanceToReference) {
        if (this.distanceToReference <= thatDistanceToReference) {
            return this;
        }
        return new RoutePoint(thatPoint, thatPosition, thatDistanceToReference);
    }

}
