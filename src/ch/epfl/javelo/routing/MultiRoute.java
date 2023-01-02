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

public final class MultiRoute implements Route {
    private final List<Route> segments;
    private final double[] segmentsLengths;
    private final double length;

    /**
     * Public constructor of the object MultiRoute
     *
     * @param segments : list of all segments of the MultiRoute
     */
    public MultiRoute(List<Route> segments) {
        Preconditions.checkArgument(!segments.isEmpty());
        this.segments = List.copyOf(segments);
        segmentsLengths = new double[segments.size() + 1];
        segmentsLengths[0] = 0d;
        for (int i = 1; i < segmentsLengths.length; i++) {
            segmentsLengths[i] = segmentsLengths[i - 1] + this.segments.get(i - 1).length();
        }
        this.length = segmentsLengths[segmentsLengths.length - 1];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int indexOfSegmentAt(double position) {
        position = Math2.clamp(0, position, length);
        int indexOfRoute = 0;
        for (int i = 1; i < segmentsLengths.length; i++) {
            if (segmentsLengths[i] < position) {
                indexOfRoute += segments.get(i - 1).indexOfSegmentAt(segmentsLengths[i]) + 1;
            } else {
                indexOfRoute += segments.get(i - 1).indexOfSegmentAt(position - segmentsLengths[i - 1]);
            }
        }
        return indexOfRoute;
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
        List<Edge> edges = new ArrayList<>();
        for (Route segment : segments) {
            edges.addAll(segment.edges());
        }
        return edges;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PointCh> points() {
        List<PointCh> points = new ArrayList<>();
        for (Edge edge : edges()) {
           points.add(edge.fromPoint());
        }
        points.add(edges().get(edges().size() - 1).toPoint());
        return points;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PointCh pointAt(double position) {
        position = Math2.clamp(0, position, length);
        int index = checkIndexBounds(findSegmentIndex(position));
        if (index == segments.size()) index -= 1;

        return segments.get(index).pointAt(position - segmentsLengths[index]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double elevationAt(double position) {
        position = Math2.clamp(0, position, length);
        int index = checkIndexBounds(findSegmentIndex(position));
        if (index == segments.size()) index -= 1;

        return segments.get(index).elevationAt(position - segmentsLengths[index]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int nodeClosestTo(double position) {
        position = Math2.clamp(0, position, length);
        int index = checkIndexBounds(findSegmentIndex(position));
        if (index == segments.size()) index -= 1;

        return segments.get(index).nodeClosestTo(position - segmentsLengths[index]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        RoutePoint routePoint = RoutePoint.NONE;

        for (int i = 0; i < segments.size(); i++) {
            RoutePoint tempRoute = routePoint.min(segments.get(i).pointClosestTo(point));
            if (!routePoint.equals(tempRoute)) {
                routePoint = tempRoute.withPositionShiftedBy(segmentsLengths[i]);
            }
        }

        return routePoint;
    }

    /**
     * private method that gives the index after doing a binary search on the position along the itinerary
     *
     * @param position : the position given
     */
    private int findSegmentIndex(double position) {
        position = Math2.clamp(0, position, length);
        int index = Arrays.binarySearch(segmentsLengths, position);
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
        if (index == segments.size()) {
            index -= 1;
        }
        return index;
    }

}
