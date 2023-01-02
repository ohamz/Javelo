package ch.epfl.javelo.data;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;

import java.io.IOException;
import java.nio.*;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

/**
 * @author Ursula El-Khoury (340994)
 * @author Omar Hammoud (341700)
 */

public final class Graph {
    private final GraphNodes nodes;
    private final GraphSectors sectors;
    private final GraphEdges edges;
    private final List<AttributeSet> attributeSets;

    /**
     * Public constructor of the object Graph
     *
     * @param nodes         : given nodes buffer
     * @param sectors       : given sectors buffer
     * @param edges         : given edges buffer
     * @param attributeSets : given attribute sets list
     */
    public Graph(GraphNodes nodes, GraphSectors sectors, GraphEdges edges, List<AttributeSet> attributeSets) {
        this.nodes = nodes;
        this.sectors = sectors;
        this.edges = edges;
        this.attributeSets = List.copyOf(attributeSets);
    }

    /**
     * Private method that returns a ByteBuffer of the given file based on the given base path
     *
     * @param basePath : given base path
     * @param fileName : given file name
     * @return the buffer extracted from the file
     * @throws : IOException
     */
    private static ByteBuffer buffer(Path basePath, String fileName) throws IOException {
        Path path = basePath.resolve(fileName);
        ByteBuffer buffer;
        try (FileChannel channel = FileChannel.open(path)) {
            buffer = channel
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        }
        return buffer;
    }

    /**
     * Public method that gives the Graph created from the files extracted using the base path given
     *
     * @param basePath : given base path
     * @return the JaVelo Graph created from the files found in the basePath repertory
     * @throws : IOException
     */
    public static Graph loadFrom(Path basePath) throws IOException {
        GraphNodes nodes = new GraphNodes(buffer(basePath, "nodes.bin").asIntBuffer());
        GraphSectors sectors = new GraphSectors(buffer(basePath, "sectors.bin"));

        ByteBuffer edgeBuffer = buffer(basePath, "edges.bin");
        IntBuffer profileIds = buffer(basePath, "profile_ids.bin").asIntBuffer();
        ShortBuffer elevations = buffer(basePath, "elevations.bin").asShortBuffer();
        GraphEdges edges = new GraphEdges(edgeBuffer, profileIds, elevations);

        LongBuffer attributeSetBuffer = buffer(basePath, "attributes.bin").asLongBuffer();
        List<AttributeSet> attributeSets = new ArrayList<>(attributeSetBuffer.capacity());
        for (int i = 0; i < attributeSetBuffer.capacity(); i++) {
            AttributeSet attribute = new AttributeSet(attributeSetBuffer.get(i));
            attributeSets.add(attribute);
        }

        return new Graph(nodes, sectors, edges, attributeSets);
    }

    /**
     * Public method
     *
     * @return the total number of nodes in the graph
     */
    public int nodeCount() {
        return nodes.count();
    }

    /**
     * Public method
     *
     * @param nodeId : given node identity
     * @return the PointCh on the position of the given node
     */
    public PointCh nodePoint(int nodeId) {
        double e = nodes.nodeE(nodeId);
        double n = nodes.nodeN(nodeId);
        return new PointCh(e, n);
    }

    /**
     * Public method
     *
     * @param nodeId : given node identity
     * @return the number of edges of the given node
     */
    public int nodeOutDegree(int nodeId) {
        return nodes.outDegree(nodeId);
    }

    /**
     * Public method that gives the node based on the edge ID and the node ID
     *
     * @param nodeId : given node identity
     * @param edgeId : given edge identity
     * @return the identity of the given edge
     */
    public int nodeOutEdgeId(int nodeId, int edgeId) {
        return nodes.edgeId(nodeId, edgeId);
    }

    /**
     * Public method that gives the identity of the nodeClosestTo
     *
     * @param point          : given point (PointCh)
     * @param searchDistance : given search distance
     * @return the identity of the node closest to the point given within the distance
     */
    public int nodeClosestTo(PointCh point, double searchDistance) {
        List<GraphSectors.Sector> sectors = this.sectors.sectorsInArea(point, searchDistance);
        int closestNode = -1;
        double distance = searchDistance * searchDistance;
        for (int i = 0; i < sectors.size(); i++) {
            int firstNodeId = sectors.get(i).startNodeId();
            int lastNodeId = sectors.get(i).endNodeId();
            for (int j = firstNodeId; j < lastNodeId; j++) {
                PointCh pointCh = nodePoint(j);
                double squaredDistanceTo = point.squaredDistanceTo(pointCh);
                if (squaredDistanceTo <= distance) {
                    distance = squaredDistanceTo;
                    closestNode = j;
                }
            }
        }
        return closestNode;
    }

    /**
     * @param edgeId : given edge identity
     * @return the identity of the target node of the given edge
     */
    public int edgeTargetNodeId(int edgeId) {
        return edges.targetNodeId(edgeId);
    }

    /**
     * @param edgeId : given edge identity
     * @return true if the given edge is inverted compared to the position of the OSM attributes
     */
    public boolean edgeIsInverted(int edgeId) {
        return edges.isInverted(edgeId);
    }

    /**
     * @param edgeId : given edge identity
     * @return the OSM attributes of the given edge
     */
    public AttributeSet edgeAttributes(int edgeId) {
        return attributeSets.get(edges.attributesIndex(edgeId));
    }

    /**
     * @param edgeId : given edge identity
     * @return the length of the edge in meters
     */
    public double edgeLength(int edgeId) {
        return edges.length(edgeId);
    }

    /**
     * @param edgeId : given edge identity
     * @return the total positive elevation of the given edge
     */
    public double edgeElevationGain(int edgeId) {
        return edges.elevationGain(edgeId);
    }

    /**
     * @param edgeId : given edge identity
     * @return the profile of the given edge if it has a profile
     */
    public DoubleUnaryOperator edgeProfile(int edgeId) {
        return edges.hasProfile(edgeId) ?
                Functions.sampled(edges.profileSamples(edgeId), edges.length(edgeId)) :
                Functions.constant(Double.NaN);
    }

}