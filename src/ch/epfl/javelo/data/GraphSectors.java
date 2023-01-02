package ch.epfl.javelo.data;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Short.toUnsignedInt;

/**
 * @author Ursula El-Khoury (340994)
 * @author Omar Hammoud (341700)
 */

public record GraphSectors(ByteBuffer buffer) {

    private static final int SECTOR_NUMBER = 128;
    private static final int OFFSET_SECTOR = Short.BYTES + Integer.BYTES;
    private static final double WIDTH_OF_SECTOR = SwissBounds.WIDTH / SECTOR_NUMBER;
    private static final double HEIGHT_OF_SECTOR = SwissBounds.HEIGHT / SECTOR_NUMBER;

    /**
     * Protected record that creates sectors
     *
     * @param startNodeId : determines the first node in the sector
     * @param endNodeId   : determines the last node in the sector
     */
    public record Sector(int startNodeId, int endNodeId) {
    }

    /**
     * Public method that finds all the sectors within a set distance of a given PointCh
     *
     * @param center   : the point which we want to find nearby sectors
     * @param distance : the distance needed to find nearby sectors
     * @return the sectors within the distance at the point center
     */
    public List<Sector> sectorsInArea(PointCh center, double distance) {
        List<Sector> sectorsList = new ArrayList<>();

        //making sure that the coordinates of the square (which contains the sectors) are in the bounds of SwissBounds
        double eMin = Math2.clamp(SwissBounds.MIN_E, center.e() - distance, SwissBounds.MAX_E);
        double eMax = Math2.clamp(SwissBounds.MIN_E, center.e() + distance, SwissBounds.MAX_E);
        double nMin = Math2.clamp(SwissBounds.MIN_N, center.n() - distance, SwissBounds.MAX_N);
        double nMax = Math2.clamp(SwissBounds.MIN_N, center.n() + distance, SwissBounds.MAX_N);

        int leftBound = (int) Math.floor((eMin - SwissBounds.MIN_E) / WIDTH_OF_SECTOR);
        int rightBound = (int) Math.ceil((eMax - SwissBounds.MIN_E) / WIDTH_OF_SECTOR);
        int lowerBound = (int) Math.floor((nMin - SwissBounds.MIN_N) / HEIGHT_OF_SECTOR);
        int upperBound = (int) Math.ceil((nMax - SwissBounds.MIN_N) / HEIGHT_OF_SECTOR);

        for (int i = leftBound; i < rightBound; i++) {
            for (int j = lowerBound; j < upperBound; j++) {
                int index = j * SECTOR_NUMBER + i;
                int firstNode = buffer.getInt(index * OFFSET_SECTOR);
                int lastNode = toUnsignedInt(buffer.getShort(index * OFFSET_SECTOR + Integer.BYTES)) + firstNode;
                sectorsList.add(new Sector(firstNode, lastNode));
            }
        }
        return sectorsList;
    }

}
