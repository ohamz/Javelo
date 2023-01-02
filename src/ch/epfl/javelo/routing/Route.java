package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

import java.util.List;

/**
 * @author Ursula El-Khoury (340994)
 * @author Omar Hammoud (341700)
 */


public interface Route {

    /**
     * Method that gives the index of the segment of a given position
     * @param position : given position
     * @return the index of the segment at said position
     */
    int indexOfSegmentAt(double position);

    /**
     * Method that gives the length of the route
     * @return the length of the itinerary in meters
     */
    double length();

    /**
     * Method that gives the List of edges of the route
     * @return a list of the edges of the itinerary
     */
    List<Edge> edges();

    /**
     * Method that gives the List of points of the route
     * @return a list of the points that are at the position along the itinerary
     */
    List<PointCh> points();

    /**
     * Method that gives the PointCh at the given position
     * @param position : given position
     * @return the point at the position given along the itinerary
     */
    PointCh pointAt(double position);

    /**
     * Method that gives the elevation at the given position
     * @param position : given position
     * @return the elevation at the position given along the itinerary
     */
    double elevationAt(double position);

    /**
     * Method that gives the ID of the node closest to the position given
     * @param position : given position
     * @return the identity of the closest node in the itinerary
     */
    int nodeClosestTo(double position);

    /**
     * Method that gives the point closest to the given position
     * @param point : given point (PointCh)
     * @return the closest point to the referenced point in the itinerary
     */
    RoutePoint pointClosestTo(PointCh point);

}
