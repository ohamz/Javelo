package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Q28_4;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static java.lang.Short.toUnsignedInt;

/**
 * @author Ursula El-Khoury (340994)
 * @author Omar Hammoud (341700)
 */

public record GraphEdges(ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations) {

    private static final int INT_OFFSET = 0;
    private static final int SHORT1_OFFSET = INT_OFFSET + 4;
    private static final int SHORT2_OFFSET = SHORT1_OFFSET + 2;
    private static final int SHORT3_OFFSET = SHORT2_OFFSET + 2;
    private static final int EDGE_BYTE = SHORT3_OFFSET + 2;

    private final static int PROFILE_0 = 0;
    private final static int PROFILE_1 = 1;
    private final static int PROFILE_3 = 3;
    private final static int PROFILE_INT_INDEX = 30;
    private final static int MAGIC_NUMBER_1 = 4;
    private final static int MAGIC_NUMBER_2 = 2;


    /**
     * Public method that checks if the edge of given ID is inverted
     *
     * @param edgeId : id of the edge
     * @return true if the edge is inverted
     */
    public boolean isInverted(int edgeId) {
        int direction = edgesBuffer.getInt(edgeId * EDGE_BYTE);
        return direction < 0;
    }

    /**
     * Public method that gives the ID of the edge, considering if it is inverted
     *
     * @param edgeId : id of the edge
     * @return the identity of the edge
     */
    public int targetNodeId(int edgeId) {
        int bit = edgesBuffer.getInt(edgeId * EDGE_BYTE);
        if (bit < 0) {
            return ~bit;
        }
        return bit;
    }

    /**
     * Public method that gives the length of the edge of given ID
     *
     * @param edgeId : id of the edge
     * @return the length of the edge (in meters)
     */
    public double length(int edgeId) {
        return Q28_4.asDouble(getIntBuffer(edgeId, SHORT1_OFFSET));
    }

    /**
     * Public method that gives the positive elevation of the edge of given ID
     *
     * @param edgeId : id of the edge
     * @return the positive height difference between the edge and the surface
     */
    public double elevationGain(int edgeId) {
        return Q28_4.asDouble(getIntBuffer(edgeId, SHORT2_OFFSET));
    }

    /**
     * Public method that checks if the edge of given ID has a valid profile
     *
     * @param edgeId : id of the edge
     * @return true if the edge has a profile (that is not 0)
     */
    public boolean hasProfile(int edgeId) {
        int profileType = Bits.extractUnsigned(profileIds.get(edgeId), PROFILE_INT_INDEX, 2);
        return profileType != PROFILE_0;
    }

    /**
     * Public method that gives the profile samples of the edge of given ID
     *
     * @param edgeId : id of the edge
     * @return the table consisting of the samples of the profile
     */
    public float[] profileSamples(int edgeId) {
        int profileType = Bits.extractUnsigned(profileIds.get(edgeId), PROFILE_INT_INDEX, 2);
        if (!hasProfile(edgeId)) { //profile type 0
            return new float[0];
        } else {
            int length0 = getIntBuffer(edgeId, SHORT1_OFFSET);
            int length = 1 + Math2.ceilDiv(length0, Q28_4.ofInt(2));
            float[] tab = new float[length];
            int id = Bits.extractUnsigned(profileIds.get(edgeId), 0, PROFILE_INT_INDEX);
            tab[0] = Q28_4.asFloat(toUnsignedInt(elevations.get(id)));

            if (profileType == PROFILE_1) {
                for (int i = 1; i < length; i++) {
                    tab[i] = Q28_4.asFloat(toUnsignedInt(elevations.get(id + i)));
                }
            } else {
                int magicIndex = (profileType == PROFILE_3) ? MAGIC_NUMBER_1 : MAGIC_NUMBER_2;
                int start = (profileType == PROFILE_3) ? MAGIC_NUMBER_1 * 3 : MAGIC_NUMBER_1 * 2;
                int bitLength = (profileType == PROFILE_3) ? magicIndex : start;

                fill(tab, length, id, magicIndex, start, bitLength);
            }

            if (isInverted(edgeId)) {
                int index = tab.length - 1;
                float temp;
                for (int i = 0; i < (tab.length / 2); i++) {
                    temp = tab[i];
                    tab[i] = tab[index];
                    tab[index] = temp;
                    --index;
                }
            }
            return tab;
        }

    }


    /**
     * Private method that gives the value of the elevation considering the last value found
     *
     * @param lastValue : the value of the last height
     * @param index     : the index of the bit in the buffer
     * @param id        : the id of the edge in the buffer
     * @param buffer    : the source buffer
     * @param start     : the starting point of the extractor
     * @param length    : the length of the bit extracted
     * @return the value of the height of the given bit
     */
    private float type(float lastValue, int index, int id, ShortBuffer buffer, int start, int length) {
        float extractedDistance = Q28_4.asFloat(Bits.extractSigned(buffer.get(index + id), start, length));
        return lastValue + extractedDistance;
    }

    /**
     * Public method that gives the ID of the attributes of the edge of given ID
     *
     * @param edgeId : id of the edge
     * @return the identity of the attributes tied to the edge
     */
    public int attributesIndex(int edgeId) {
        return toUnsignedInt(edgesBuffer.getShort(edgeId * EDGE_BYTE + SHORT3_OFFSET));
    }

    private int getIntBuffer(int edgeId, int offset) {
        return toUnsignedInt(edgesBuffer.getShort(edgeId * EDGE_BYTE + offset));
    }

    private void fill(float[] tab, int length, int id, int magicInt, int start, int bitLength) {
        int index = 1;
        for (int i = 1; i < length; i += magicInt) {
            for (int j = 0; j < magicInt; j++) {
                if (i + j < length)
                    tab[i + j] = type(tab[i + j - 1], index, id, elevations,
                            start - j * bitLength, bitLength);
            }
            ++index;
        }
    }

}
