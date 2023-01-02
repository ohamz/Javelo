package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.projection.PointCh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Ursula El-Khoury (340994)
 * @author Omar Hammoud (341700)
 */

public final class SingleRoute implements Route {
    private final double length;
    private final double[] edgesLengths;
    private final List<Edge> edges;
    private final List<PointCh> points;

    /**
     * Public constructor of the object SingleRoute
     *
     * @param edges : list of all edges of the SingleRoute
     */
    public SingleRoute(List<Edge> edges) {
        Preconditions.checkArgument(!edges.isEmpty());
        this.edges = List.copyOf(edges);
        edgesLengths = new double[edges.size() + 1];
        edgesLengths[0] = 0d;
        for (int i = 1; i < edgesLengths.length; i++) {
            edgesLengths[i] = (edgesLengths[i - 1] + edges.get(i - 1).length());
        }
        this.length = edgesLengths[edgesLengths.length - 1];
        this.points = new ArrayList<>();
        for (Edge edge : edges) {
            points.add(edge.fromPoint());
        }
        points.add(edges.get(edges.size() - 1).toPoint());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int indexOfSegmentAt(double position) {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double length() {
        return this.length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Edge> edges() {
        return edges;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PointCh> points() {
        return List.copyOf(points);
    }

    /**
     * private method that gives the index after doing a binary search on the position along the itinerary
     *
     * @param position : the position given
     */
    private int findEdgeIndex(double position) {
        position = Math2.clamp(0, position, this.length());
        int index = Arrays.binarySearch(edgesLengths, position);
        if (index == -1) {
            index = 0;
        }
        return index;
    }

    /**
     * private method that returns the correct positive index
     *
     * @param index : the index given by the binary search
     */
    private int checkIndexBounds(int index) {
        if (index < 0) {
            index = -index - 2;
        }
        if (index == edges.size()) {
            index -= 1;
        }
        return index;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PointCh pointAt(double position) {
        position = Math2.clamp(0, position, this.length());
        int index = findEdgeIndex(position);

        if (index < 0) {
            index = -index - 2;
        }
        if (index >= edges.size()) {
            index = edges.size();
        }

        double totalLengthFromEdge = edgesLengths[index];

        if (index == edges.size()) {
            return edges.get(index - 1).toPoint();
        }
        return edges.get(index).pointAt(position - totalLengthFromEdge);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double elevationAt(double position) {
        position = Math2.clamp(0, position, this.length());
        int index = checkIndexBounds(findEdgeIndex(position));

        double totalLengthFromEdge = edgesLengths[index];

        return edges.get(index).elevationAt(position - totalLengthFromEdge);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int nodeClosestTo(double position) {
        position = Math2.clamp(0, position, this.length());

        int index = findEdgeIndex(position);
        if (index >= 0) {
            if (index == edges.size()) {
                return edges.get(index - 1).toNodeId();
            } else {
                return edges.get(index).fromNodeId();
            }
        } else {
            index = -index - 2;
            Edge edge = edges.get(index);

            double totalLengthFromNode = edgesLengths[index];

            if (position - totalLengthFromNode <= edge.length() / 2)
                return edge.fromNodeId();
            else
                return edge.toNodeId();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        double positionClosestTo;
        PointCh pointClosestTo;
        RoutePoint routePoint = RoutePoint.NONE;

        for (int i = 0; i < edges.size(); ++i) {
            positionClosestTo = Math2.clamp(edgesLengths[i], edges.get(i).positionClosestTo(point) + edgesLengths[i], edgesLengths[i + 1]);
            pointClosestTo = pointAt(positionClosestTo);
            routePoint = routePoint.min(pointClosestTo, positionClosestTo, pointClosestTo.distanceTo(point));
        }

        return routePoint;
    }
}
