package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.function.DoubleUnaryOperator;

/**
 * @author Ursula El-Khoury (340994)
 * @author Omar Hammoud (341700)
 */

public record Edge(int fromNodeId, int toNodeId, PointCh fromPoint, PointCh toPoint,
                   double length, DoubleUnaryOperator profile) {

    /**
     * Public method that gives an instance of the Edge given the parameters
     *
     * @param graph      : given Graph
     * @param edgeId     : given edge identity
     * @param fromNodeId : given node identity of the first node
     * @param toNodeId   : given node identity of the last node
     * @return an instance of Edge
     */
    public static Edge of(Graph graph, int edgeId, int fromNodeId, int toNodeId) {
        return new Edge(fromNodeId, toNodeId, graph.nodePoint(fromNodeId), graph.nodePoint(toNodeId),
                graph.edgeLength(edgeId), graph.edgeProfile(edgeId));
    }

    /**
     * Public method that gives the position on the Edge that is closest to the point
     *
     * @param point : given point (PointCh)
     * @return the position along the edge that is closest to the given point in meters
     */
    public double positionClosestTo(PointCh point) {
        return Math2.projectionLength(fromPoint.e(), fromPoint.n(), toPoint.e(),
                toPoint.n(), point.e(), point.n());
    }

    /**
     * Public method that gives the point at the given position on the edge
     *
     * @param position : given position in meters
     * @return the point located at the given position on the edge
     */
    public PointCh pointAt(double position) {
        if (length == 0) return fromPoint;
        double result = position / length;
        double e = Math2.interpolate(fromPoint.e(), toPoint.e(), result);
        double n = Math2.interpolate(fromPoint.n(), toPoint.n(), result);
        return new PointCh(e, n);
    }

    /**
     * Public method that gives the elevation at the given position
     *
     * @param position : given position in meters
     * @return the altitude at the given position on the edge in meters
     */
    public double elevationAt(double position) {
        return profile.applyAsDouble(position);
    }
}
