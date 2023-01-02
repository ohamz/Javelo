package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.data.*;
import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static org.junit.jupiter.api.Assertions.*;

class EdgeTestUs {
    EdgeTestUs() throws IOException {
    }

    private Graph getGraph() throws IOException {
        return Graph.loadFrom(Path.of("lausanne"));
    }

    private boolean equals(Edge edge1, Edge edge2) throws IOException {
        Graph graph = getGraph();
        return (edge1.fromNodeId() == edge2.fromNodeId()) && (edge1.toNodeId() == edge2.toNodeId()) &&
                (graph.nodePoint(edge1.fromNodeId()).equals(graph.nodePoint(edge2.fromNodeId()))) &&
                (graph.nodePoint(edge1.toNodeId()).equals(graph.nodePoint(edge2.toNodeId())));

    }

    @Test
    void ofTest() throws IOException {
        int edgeId = 3;
        for (int i = 0; i < 20; ++i) {
            for (int j = 0; j < 20; ++j) {
                int fromNodeId = i;
                int toNodeId = j;
                Edge expectedEdge = new Edge(fromNodeId, toNodeId, getGraph().nodePoint(fromNodeId), getGraph().nodePoint(toNodeId),
                        getGraph().edgeLength(edgeId), getGraph().edgeProfile(edgeId));
                Edge actualEdge = Edge.of(getGraph(), edgeId, fromNodeId, toNodeId);
                assertTrue(equals(actualEdge, expectedEdge));
            }
        }
    }

    // Tests Lina

    Graph actual1 = Graph.loadFrom(Path.of("lausanne"));

    @Test
    void ofWorks() throws IOException {
        Graph actual1 = Graph.loadFrom(Path.of("lausanne"));
        Edge hay = Edge.of(actual1, 1000, 2345, 5436);
    }

    @Test
    void positionClosestToWorks() throws IOException {
        Graph actual1 = Graph.loadFrom(Path.of("lausanne"));
        Edge actualEdge = Edge.of(actual1, 1000, 2345, 5436);
        assertEquals(-53538.84482952522, actualEdge.positionClosestTo(new PointCh(2601098, 1101654)));}

    @Test
    void pointAtWorks() throws IOException {
        Graph actual1 = Graph.loadFrom(Path.of("lausanne"));
        Edge actualEdge = Edge.of(actual1, 1000, 2345, 5436);
        assertEquals(new PointCh(2490213.793103448, 1159166.4504310344), actualEdge.pointAt(20));}

    @Test
    void elevationAtWorks() {
        Edge actualEdge = Edge.of(actual1, 1000, 2345, 5436);
        assertEquals(841.125, actualEdge.elevationAt(100));}

    @Test
    void HorizontalEdgeWorksCorrectlyForLimits() {
        PointCh fromPoint = new PointCh(2485010, 1076000);
        PointCh toPoint = new PointCh(2485020, 1076000);
        float[] type3Array = new float[]{384.75f, 384.6875f, 384.5625f, 384.5f, 384.4375f, 384.375f, 384.3125f, 384.25f, 384.125f, 384.0625f,};
        DoubleUnaryOperator profile = Functions.sampled(type3Array, 10);
        DoubleUnaryOperator squared = new DoubleUnaryOperator() {
            @Override
            public double applyAsDouble(double operand) {
                return Math.pow(operand, 2);
            }
        };
        Edge edge1 = new Edge(0, 3, fromPoint, toPoint, 10, squared);
        PointCh pointToTest = new PointCh(2600000, 1085000);
        assertEquals(0, edge1.positionClosestTo(fromPoint));
        assertEquals(10, edge1.positionClosestTo(toPoint));
        //Rajouter pr un point quelconque.
        assertEquals(fromPoint, edge1.pointAt(0));
        assertEquals(toPoint, edge1.pointAt(10));
        assertEquals(new PointCh(2485015, 1076000), edge1.pointAt(5));
        assertEquals(9, edge1.elevationAt(3));
        Edge edge2 = new Edge(0, 3, fromPoint, toPoint, 10, profile);
        assertEquals(384.75f, edge2.elevationAt(0));
    }

    @Test
    void EdgeIsFine() {
        IntBuffer forNodes = IntBuffer.wrap(new int[]{
                2_495_000 << 4,
                1_197_000 << 4,
                0x2_800_0015,
                2_657_112 << 4,
                1_080_000 << 4,
                0x2_674_0215
        });
        GraphNodes graphNodes1 = new GraphNodes(forNodes);

        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putInt(0, 12);
        //Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(4, (short) 0x10_b);
        // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(6, (short) 0x10_0);
        // Identité de l'ensemble d'attributs OSM : 1
        edgesBuffer.putShort(8, (short) 2022);
        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                // Type : 0. Index du premier échantillon : 1.
                (3 << 30) | 1
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000 //Type3
        });
        GraphEdges edges1 =
                new GraphEdges(edgesBuffer, profileIds, elevations);


        float[] type3Array = new float[]{
                384.75f, 384.6875f, 384.5625f, 384.5f, 384.4375f,
                384.375f, 384.3125f, 384.25f, 384.125f, 384.0625f,
        };

        ByteBuffer buffer = ByteBuffer.wrap(new byte[]{
                0, 0, 0, 12, 0, 10});
        GraphSectors sectors1 = new GraphSectors(buffer);

        List<AttributeSet> liste = new ArrayList<>();

        Graph graph1 = new Graph(graphNodes1, sectors1, edges1, liste);

        Edge edge1 = Edge.of(graph1, 0, 0, 1);

        PointCh pointToTest = new PointCh(2600000, 1085000);

        System.out.println(edge1.positionClosestTo(pointToTest));
    }
}