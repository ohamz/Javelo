package ch.epfl.javelo.routing;

public interface CostFunction {

    /**
     *
     * @param nodeId : given nodeId
     * @param edgeId : given edgeId
     * @return the factor for which the length of the edge (of the given edgeId)
     * going out of the node (of nodeId) is multiplied. This factor has to be greater or equal to 1
     */
    double costFactor(int nodeId, int edgeId);
}
