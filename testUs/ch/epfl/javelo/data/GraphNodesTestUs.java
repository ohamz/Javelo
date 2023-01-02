package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.nio.IntBuffer;

import static org.junit.jupiter.api.Assertions.*;

class GraphNodesTestUs {
    IntBuffer b = IntBuffer.wrap(new int[]{
            2_600_000 << 4,
            1_200_000 << 4,
            0x2_000_1234,

            1_600_000 << 4,
            2_200_000 << 4,
            0x1_000_2469,

            3_400_000 << 4,
            2_250_000 << 4,
            0x5_000_3513,
    });
    GraphNodes ns = new GraphNodes(b);

    private static final GraphNodes nodes = getGraphNodes();
    private static final GraphNodes nodes2 = getGraphNodes2();

    @Test
    void count() {
        assertTrue(!(ns.count() != 3));
        assertEquals(3, ns.count());
    }

    @Test
    void nodeE(){
        assertEquals(2_600_000, ns.nodeE(0));
        assertEquals(1_600_000, ns.nodeE(1));
        assertEquals(3_400_000, ns.nodeE(2));
    }

    @Test
    void nodeN(){
        assertEquals(1_200_000, ns.nodeN(0));
        assertEquals(2_200_000, ns.nodeN(1));
        assertEquals(2_250_000, ns.nodeN(2));
    }

    @Test
    void outDegree() {
        assertEquals(2, ns.outDegree(0));
        assertEquals(1, ns.outDegree(1));
        assertEquals(5, ns.outDegree(2));
    }

    @Test
    void edgeId() {
        assertEquals(0x1234, ns.edgeId(0, 0));
        assertEquals(0x1235, ns.edgeId(0, 1));
        assertEquals(0x2469, ns.edgeId(1, 0));
        assertEquals(0x3513, ns.edgeId(2, 0));
        assertEquals(0x3514, ns.edgeId(2, 1));
        assertEquals(0x3515, ns.edgeId(2, 2));
        assertEquals(0x3516, ns.edgeId(2, 3));
        assertEquals(0x3517, ns.edgeId(2, 4));
    }


    @Test
    void countHassan() {
        assertEquals(1, nodes.count());
        assertEquals(3,nodes2.count());

    }

    @Test
    void nodeEHassan() {
        assertEquals(2_600_000, nodes.nodeE(0));
        assertEquals((int) SwissBounds.MIN_E+1,nodes2.nodeE(2));
    }

    @Test
    void nodeNHassan() {
        assertEquals(1_200_000, nodes.nodeN(0));
        assertEquals((int) SwissBounds.MIN_N-1,nodes2.nodeN(1));
    }

    @Test
    void outDegreeHassan() {
        assertEquals(2, nodes.outDegree(0));

        assertEquals(1, nodes2.outDegree(0));
        assertEquals(2, nodes2.outDegree(1));
        assertEquals(5, nodes2.outDegree(2));
    }

    @Test
    void edgeIdHassan() {
        assertEquals(0x1234, nodes.edgeId(0, 0));
        assertEquals(0x1235, nodes.edgeId(0, 1));

        assertEquals(1, nodes2.edgeId(0, 0));
        assertEquals(4, nodes2.edgeId(1, 1));
        assertEquals(10, nodes2.edgeId(2, 3));
    }

    private static GraphNodes getGraphNodes() {
        IntBuffer b = IntBuffer.wrap(new int[]{
                2_600_000 << 4, //E coordinate
                1_200_000 << 4, //N coordinate
                0x2_000_1234
        });

        return new GraphNodes(b);
    }
    private static GraphNodes getGraphNodes2() {
        IntBuffer b2 = IntBuffer.allocate(9);
        b2.put((int) SwissBounds.MIN_E<<4);
        b2.put((int) SwissBounds.MIN_N<<4);
        //b2.put(0b0001_0000_0000_0000_0000_0000_0000_0001);
        b2.put(0x1_0_0_0_0_0_0_1);


        b2.put((int) SwissBounds.MAX_E<<4);
        b2.put(((int) SwissBounds.MIN_N-1)<<4);
        b2.put(0x2_0_0_0_0_0_0_3);

        b2.put(((int) SwissBounds.MIN_E+1)<<4);
        b2.put((int) SwissBounds.MAX_N<<4);
        b2.put(0x5_0_0_0_0_0_0_7);
        return new GraphNodes(b2);
    }
}