package ch.epfl.javelo.data;

import java.nio.*;

import ch.epfl.javelo.Q28_4;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GraphEdgesTestUs {

    ByteBuffer edgesBuffer = ByteBuffer.allocate(30);
    IntBuffer profileIds = IntBuffer.wrap(new int[] {(1 << 30) | 1});
    ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000
    });
    GraphEdges edges = new GraphEdges(edgesBuffer, profileIds, elevations);


    ByteBuffer edgesBuffer0 = ByteBuffer.allocate(10);
    ByteBuffer edgesBuffer1 = ByteBuffer.allocate(10);
    ByteBuffer edgesBuffer2 = ByteBuffer.allocate(10);
    ByteBuffer edgesBuffer3 = ByteBuffer.allocate(10);

    IntBuffer profileIds0 = IntBuffer.wrap(new int[] {(0 << 30) | 1});
    IntBuffer profileIds1 = IntBuffer.wrap(new int[] {(1 << 30) | 1});
    IntBuffer profileIds2 = IntBuffer.wrap(new int[] {(2 << 30) | 1});
    IntBuffer profileIds3 = IntBuffer.wrap(new int[] {(3 << 30) | 1});

    ShortBuffer elevations0 = ShortBuffer.wrap(new short[]{
            (short) 0,
            (short) 0x180C, (short) 0xFEFF,
            (short) 0xFFFE, (short) 0xF000
    });
    ShortBuffer elevations1 = ShortBuffer.wrap(new short[]{
            0,
            (short) -0000010000001000, (short) -0000010000000100,
            (short) -0000010000000100, (short) -0000010000001000,
            (short) -0000010000000000
    });
    ShortBuffer elevations2 = ShortBuffer.wrap(new short[]{
            (short) 0,
            (short) 0x180C, (byte) 0xFE, (byte)0xFF,
            (byte) 0xFF, (byte) 0xFE, (byte) 0xF0, (byte) 0x00,
    });
    ShortBuffer elevations3 = ShortBuffer.wrap(new short[]{
            (short) 0,
            (short) 0x180C, (short) 0xFEFF,
            (short) 0xFFFE, (short) 0xF000
    });

    GraphEdges edges0 = new GraphEdges(edgesBuffer0, profileIds0, elevations0);
    GraphEdges edges1 = new GraphEdges(edgesBuffer1, profileIds1, elevations1);
    GraphEdges edges2 = new GraphEdges(edgesBuffer2, profileIds2, elevations2);
    GraphEdges edges3 = new GraphEdges(edgesBuffer3, profileIds3, elevations3);

    void create() {
        edgesBuffer0.putInt(0, ~12);
        edgesBuffer0.putShort(4, (short) 0x10_b);
        edgesBuffer0.putShort(6, (short) 0x10_0);
        edgesBuffer0.putShort(8, (short) 2022);

        edgesBuffer1.putInt(0, ~12);
        edgesBuffer1.putShort(4, (short) 0x10_b);
        edgesBuffer1.putShort(6, (short) 0x10_0);
        edgesBuffer1.putShort(8, (short) 2022);

        edgesBuffer2.putInt(0, ~12);
        edgesBuffer2.putShort(4, (short) 0x10_b);
        edgesBuffer2.putShort(6, (short) 0x10_0);
        edgesBuffer2.putShort(8, (short) 2022);

        edgesBuffer3.putInt(0, ~12);
        edgesBuffer3.putShort(4, (short) 0x10_b);
        edgesBuffer3.putShort(6, (short) 0x10_0);
        edgesBuffer3.putShort(8, (short) 2022);

    }
    void create2() {
        edgesBuffer.putInt(0, 8);
        edgesBuffer.putShort(4, (short) 0x05_3);
        edgesBuffer.putShort(6, (short) 0x32_5);
        edgesBuffer.putShort(8, (short) 9105);

        edgesBuffer.putInt(10, ~43);
        edgesBuffer.putShort(14, (short) 0x12_3);
        edgesBuffer.putShort(16, (short) 0x42_2);
        edgesBuffer.putShort(18, (short) 6217);

        edgesBuffer.putInt(20, 72);
        edgesBuffer.putShort(24, (short) 0xff_f);
        edgesBuffer.putShort(26, (short) 0x2a_c);
        edgesBuffer.putShort(28, (short) 7491);
    }

    @Test
    void isInverted() {
        create();
        create2();

        assertTrue(edges0.isInverted(0));
        assertFalse(edges.isInverted(0));
        assertTrue(edges.isInverted(1));
        assertFalse(edges.isInverted(2));
    }

    @Test
    void targetNodeId() {
        create();
        create2();

        assertEquals(12, edges1.targetNodeId(0));
        assertEquals(8, edges.targetNodeId(0));
        assertEquals(43, edges.targetNodeId(1));
        assertEquals(72, edges.targetNodeId(2));
    }

    @Test
    void length() {
        create();
        create2();

        assertEquals(16.6875, edges1.length(0));
        assertEquals(5.1875, edges.length(0));
        assertEquals(18.1875, edges.length(1));
        assertEquals(255.9375, edges.length(2));
    }

    @Test
    void elevationGain() {
        create();
        create2();

        assertEquals(16.0, edges1.elevationGain(0));
        assertEquals(50.3125, edges.elevationGain(0));
        assertEquals(66.125, edges.elevationGain(1));
        assertEquals(42.75, edges.elevationGain(2));
    }

    @Test
    void attributesIndex() {
        create();
        create2();

        assertEquals(2022, edges1.attributesIndex(0));
        assertEquals(9105, edges.attributesIndex(0));
        assertEquals(6217, edges.attributesIndex(1));
        assertEquals(7491, edges.attributesIndex(2));
    }

    @Test
    void profileSamples(){
        create();

        float[] expected;

        float[] expectedSamplesInverted = new float[]{
                384.0625f, 384.125f, 384.25f, 384.3125f, 384.375f,
                384.4375f, 384.5f, 384.5625f, 384.6875f, 384.75f
        };

        float[] expectedSamples = new float[]{
                384.75f, 384.6875f, 384.5625f, 384.5f, 384.4375f,
                384.375f, 384.3125f, 384.25f, 384.125f, 384.0625f,
        };


        for (int i = 0; i < 4 ; ++i) {
            if (i == 1) {
                float[] tableau = edges0.profileSamples(0);
                assertArrayEquals(new float[0], tableau);
            } else if (i == 1) {

                if (edges1.isInverted(0)) {
                    expected = expectedSamplesInverted;
                } else {expected = expectedSamples;}

                float[] tableau = edges1.profileSamples(0);
                assertArrayEquals(expected, tableau);

            } else if (i == 2) {

                if (edges2.isInverted(0)) {
                    expected = expectedSamplesInverted;
                } else {expected = expectedSamples;}

                float[] tableau = edges2.profileSamples(0);
                assertArrayEquals(expected, tableau);

            } else if (i == 3) {

                if (edges3.isInverted(0)) {
                    expected = expectedSamplesInverted;
                } else {expected = expectedSamples;}

                float[] tableau = edges3.profileSamples(0);
                assertArrayEquals(expected, tableau);
            }
        }
    }

    @Test
    void hasProfile(){
        create();

        assertFalse(edges0.hasProfile(0));
        assertTrue(edges1.hasProfile(0));
        assertTrue(edges2.hasProfile(0));
        assertTrue(edges3.hasProfile(0));
    }

//ohamz & urs :)

    private static final GraphEdges edgesH = getGraphEdges();
    private static final GraphEdges edgesH2 = getGraphEdges2();

    private static GraphEdges getGraphEdges() {
        ByteBuffer edgesBuffer = ByteBuffer.allocate(10);
        edgesBuffer.putInt(0, ~12);                 // Sens : inversé. Nœud destination : 12.
        edgesBuffer.putShort(4, (short) 0x10_b);    // Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(6, (short) 0x10_0);    // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(8, (short) 2022);      // Identité de l'ensemble d'attributs OSM : 1

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                (3 << 30) | 1   // Type : 3. Index du premier échantillon : 1.
        });

        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000
        });

        return new GraphEdges(edgesBuffer, profileIds, elevations);
    }


    private static GraphEdges getGraphEdges2() {
        ByteBuffer edgesBuffer = ByteBuffer.allocate(20);
        edgesBuffer.putInt(0, 6);                 // Sens : normal. Nœud destination : 6.
        edgesBuffer.putShort(4, (short) 0x43_c);    // Longueur : 0x43.c m (= 67.75 m)
        edgesBuffer.putShort(6, (short) 0x12_e);    // Dénivelé : 0x12.e m (= 18.875 m)
        edgesBuffer.putShort(8, (short) 100);      // Identité de l'ensemble d'attributs OSM : 100

        edgesBuffer.putInt(10, 12);                 // Sens : normal. Nœud destination : 12.
        edgesBuffer.putShort(14, (short) 0x10_b);    // Longueur : 0x10.b m (= 16.6875 m)
        edgesBuffer.putShort(16, (short) 0x10_0);    // Dénivelé : 0x10.0 m (= 16.0 m)
        edgesBuffer.putShort(18, (short) 2022);      // Identité de l'ensemble d'attributs OSM : 1

        IntBuffer profileIds = IntBuffer.wrap(new int[]{
                (0 << 30) | 1,   // Type : 0. Index du premier échantillon : 1.
                (2 << 30) | 1   // Type : 2. Index du premier échantillon : 1.
        });


        ShortBuffer elevations = ShortBuffer.wrap(new short[]{
                (short) 0,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000,
                (short) 0x180C, (short) 0xFEFF,
                (short) 0xFFFE, (short) 0xF000
        });

        return new GraphEdges(edgesBuffer, profileIds, elevations);
    }

    @Test
    void isInvertedWorksOnKnownValueHassan() {
        assertTrue(edgesH.isInverted(0));
        assertTrue(!edgesH2.isInverted(0));
        assertTrue(!edgesH2.isInverted(1));
    }

    @Test
    void targetNodeIdWorksOnKnownValueHassan() {
        assertEquals(12, edgesH.targetNodeId(0));
        assertEquals(6,edgesH2.targetNodeId(0));
        assertEquals(12,edgesH2.targetNodeId(1));
    }

    @Test
    void lengthWorksOnKnownValueHassan() {
        assertEquals(16.6875, edgesH.length(0));
        assertEquals(67.75,edgesH2.length(0));
        assertEquals(16.6875, edgesH2.length(1));

    }

    @Test
    void elevationGainWorksOnKnownValueHassan() {
        assertEquals(16.0, edgesH.elevationGain(0));
        assertEquals(18.875, edgesH2.elevationGain(0));
        assertEquals(16.0, edgesH2.elevationGain(1));


    }

    @Test
    void hasProfileWorksOnKnownValueHassan() {
        assertTrue(edgesH.hasProfile(0));
        assertTrue(!edgesH2.hasProfile(0));
        assertTrue(edgesH2.hasProfile(1));
    }

    @Test
    void profileSamplesWorksOnKnownValueHassan() {
        float[] expectedSamples = new float[]{
                384.0625f, 384.125f, 384.25f, 384.3125f, 384.375f,
                384.4375f, 384.5f, 384.5625f, 384.6875f, 384.75f
        };

        assertArrayEquals(expectedSamples, edgesH.profileSamples(0));

        float[] expectedSamples2 = new float[10];
        expectedSamples2[0]=384.75f;
        expectedSamples2[1]=expectedSamples2[0]+ Q28_4.asFloat(((short) (byte)0xFE));
        expectedSamples2[2]=expectedSamples2[1]+ Q28_4.asFloat(((short) (byte)0xFF));
        expectedSamples2[3]=expectedSamples2[2]+ Q28_4.asFloat(((short) (byte)0xFF));
        expectedSamples2[4]=expectedSamples2[3]+ Q28_4.asFloat(((short) (byte)0xFE));
        expectedSamples2[5]=expectedSamples2[4]+ Q28_4.asFloat(((short) (byte)0xF0));
        expectedSamples2[6]=expectedSamples2[5]+ Q28_4.asFloat(((short) (byte)0x00));
        expectedSamples2[7]=expectedSamples2[6]+ Q28_4.asFloat(((short) (byte)0x18));
        expectedSamples2[8]=expectedSamples2[7]+ Q28_4.asFloat(((short) (byte)0x0C));
        expectedSamples2[9]=expectedSamples2[8]+ Q28_4.asFloat(((short) (byte)0xFE));
        assertArrayEquals(expectedSamples2,edgesH2.profileSamples(1));
    }

    @Test
    void attributesIndexWorksOnKnownValueHassan() {
        assertEquals(2022, edgesH.attributesIndex(0));
        assertEquals(100,edgesH2.attributesIndex(0));
        assertEquals(2022, edgesH2.attributesIndex(1));

    }
}