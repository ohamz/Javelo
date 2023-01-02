package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;

/**
 * @author Ursula El-Khoury (340994)
 * @author Omar Hammoud (341700)
 */

/**
 * Public record that represents a Waypoint on the map,
 * it has 2 attributes (point, node identity)
 */
public record Waypoint(PointCh point, int nodeId) {
}
