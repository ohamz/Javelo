package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.Q28_4;
import javafx.scene.Node;

import java.nio.IntBuffer;

/**
 * @author Ursula El-Khoury (340994)
 * @author Omar Hammoud (341700)
 */

public record GraphNodes(IntBuffer buffer) {
    private static final int OFFSET_E = 0;
    private static final int OFFSET_N = OFFSET_E + 1;
    private static final int OFFSET_OUT_EDGES = OFFSET_N + 1;
    private static final int NODE_INTS = OFFSET_OUT_EDGES + 1;
    private static final int FIRST_INDEX_OUT_DEGREE = 28;
    private static final int OUT_DEGREE_LENGTH = 4;
    private static final int FIRST_INDEX_EDGE_ID = 0;
    private static final int EDGE_ID_LENGTH = 28;

    /**
     * Public method
     *
     * @return the total number of nodes
     */
    public int count() {
        return buffer.capacity() / NODE_INTS;
    }

    /**
     * Public method that gives the coordinate E of the node of given ID
     *
     * @param nodeId : given identity of the node
     * @return the coordinate e of the given node
     */
    public double nodeE(int nodeId) {
        return Q28_4.asDouble(buffer.get(nodeId * NODE_INTS + OFFSET_E));
    }

    /**
     * Public method that gives the coordinate N of the node of given ID
     *
     * @param nodeId : given identity of the node
     * @return the coordinate n of the given node
     */
    public double nodeN(int nodeId) {
        return Q28_4.asDouble(buffer.get(nodeId * NODE_INTS + OFFSET_N));
    }

    /**
     * Public method that gives the number of edges going out of node of given ID
     *
     * @param nodeId : given identity of the node
     * @return the number of edges going out of the given node
     */
    public int outDegree(int nodeId) {
        int node = buffer.get(nodeId * NODE_INTS + OFFSET_OUT_EDGES);
        return Bits.extractUnsigned(node, FIRST_INDEX_OUT_DEGREE, OUT_DEGREE_LENGTH);
    }

    /**
     * Public method that gives the ID of the edge of given ID coming out of the node of given ID
     *
     * @param nodeId    : given identity of the node
     * @param edgeIndex : index of the edgeId edge
     * @return the identity of the edge of index edgeId going out of the given node
     */
    public int edgeId(int nodeId, int edgeIndex) {
        Preconditions.checkArgument(edgeIndex >= 0 && edgeIndex <= outDegree(nodeId));
        int node = buffer.get(nodeId * NODE_INTS + OFFSET_OUT_EDGES);
        return Bits.extractUnsigned(node, FIRST_INDEX_EDGE_ID, EDGE_ID_LENGTH) + edgeIndex;
    }

}
