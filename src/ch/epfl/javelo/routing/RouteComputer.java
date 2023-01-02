package ch.epfl.javelo.routing;

import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.*;

/**
 * @author Ursula El-Khoury (340994)
 * @author Omar Hammoud (341700)
 */

public final class RouteComputer {
    private final Graph graph;
    private final CostFunction costFunction;

    /**
     * Public constructor of the object RouteComputer
     *
     * @param graph        : immutable graph
     * @param costFunction : immutable cost function
     */
    public RouteComputer(Graph graph, CostFunction costFunction) {
        this.graph = graph;
        this.costFunction = costFunction;
    }

    /**
     * Public method that finds the best minimal total cost route from startNodeId to endNodeId
     *
     * @param startNodeId : given start node identity
     * @param endNodeId   : given end node identity
     * @return the minimal total cost route from tartNodeId to endNodeId in the graph, or null if no route exists
     */
    public Route bestRouteBetween(int startNodeId, int endNodeId) {
        Preconditions.checkArgument(startNodeId != endNodeId);

        float UNUSED_VALUE = Float.NEGATIVE_INFINITY;

        record WeightedNode(int nodeId, float distance) implements Comparable<WeightedNode> {
            @Override
            public int compareTo(WeightedNode that) {
                return Float.compare(this.distance, that.distance);
            }
        }

        float[] distance = new float[graph.nodeCount()];
        int[] predecessor = new int[graph.nodeCount()];
        Arrays.fill(distance, Float.POSITIVE_INFINITY);
        PointCh endPointCh = graph.nodePoint(endNodeId);

        PriorityQueue<WeightedNode> inExploration = new PriorityQueue<>();

        WeightedNode startNode = new WeightedNode(startNodeId, distance[startNodeId]);
        distance[startNodeId] = 0;
        inExploration.add(startNode);
        while (!inExploration.isEmpty()) {
            WeightedNode currentNodeN = inExploration.remove();

            if (distance[currentNodeN.nodeId] == UNUSED_VALUE) {
                continue;
            }

            if (currentNodeN.nodeId == endNodeId) {
                return createRoute(graph, predecessor, startNodeId, endNodeId);
            }

            for (int i = 0; i < graph.nodeOutDegree(currentNodeN.nodeId); i++) {
                int currentEdgeId = graph.nodeOutEdgeId(currentNodeN.nodeId, i);
                float currentEdgeLength = (float) graph.edgeLength(currentEdgeId);

                int targetNodeId = graph.edgeTargetNodeId(currentEdgeId);
                if (distance[targetNodeId] == UNUSED_VALUE) {
                    continue;
                }

                double dist = distance[currentNodeN.nodeId] +
                        currentEdgeLength * costFunction.costFactor(currentNodeN.nodeId, currentEdgeId);

                if (dist < distance[targetNodeId]) {
                    distance[targetNodeId] = (float) dist;
                    predecessor[targetNodeId] = currentNodeN.nodeId;
                    inExploration.add(new WeightedNode(targetNodeId, (float) (distance[targetNodeId] +
                            graph.nodePoint(targetNodeId).distanceTo(endPointCh))));
                }
            }
            distance[currentNodeN.nodeId] = UNUSED_VALUE;
        }
        return null;
    }

    /**
     * Private method that creates a SingleRoute
     *
     * @param graph       : given graph
     * @param predecessor : given predecessor table
     * @param startNodeId : given start node identity
     * @param endNodeId   : given end node identity
     * @return the SingleRoute created using all the parameters
     */
    private Route createRoute(Graph graph, int[] predecessor, int startNodeId, int endNodeId) {
        List<Edge> finalEdges = new ArrayList<>();
        int id = endNodeId;
        while (id != startNodeId) {
            int edgesFromPredecessor = graph.nodeOutDegree(predecessor[id]);
            for (int i = 0; i < edgesFromPredecessor; i++) {
                int edgeId = graph.nodeOutEdgeId(predecessor[id], i);
                if (id == graph.edgeTargetNodeId(edgeId)) {
                    Edge edge = Edge.of(graph, edgeId, predecessor[id], id);
                    finalEdges.add(edge);
                    break;
                }
            }
            id = predecessor[id];
        }
        Collections.reverse(finalEdges);
        return new SingleRoute(finalEdges);
    }
}
