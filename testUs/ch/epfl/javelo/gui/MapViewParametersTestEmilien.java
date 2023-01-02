package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MapViewParametersTestEmilien {
    PointWebMercator point1 = PointWebMercator.of(10, 124624, 103438);
    double lon1 = Math.toDegrees(point1.lon());
    double lat1 = Math.toDegrees(point1.lat());
    MapViewParameters map1 = new MapViewParameters(10, 135735, 92327);

    PointWebMercator point2 = PointWebMercator.of(18, 0, 40269228.6);
    double lon2 = Math.toDegrees(point2.lon());
    double lat2 = Math.toDegrees(point2.lat());
    MapViewParameters map2 = new MapViewParameters(18, 33554432, 40265318.4);

    PointWebMercator point3 = PointWebMercator.of(0, 0.2, 0);
    double lon3 = Math.toDegrees(point3.lon());
    double lat3 = Math.toDegrees(point3.lat());
    MapViewParameters map3 = new MapViewParameters(0, 0.2, 0);

    PointWebMercator point4 = PointWebMercator.of(19, 134217720, 134210000);
    double lon4 = Math.toDegrees(point4.lon());
    double lat4 = Math.toDegrees(point4.lat());
    MapViewParameters map4 = new MapViewParameters(19, 134217728, 134217728);

    @Test
    void MapViewParameterThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new MapViewParameters(-1, 0, 0);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new MapViewParameters(0, -1, 0);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new MapViewParameters(0, 0, -1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new MapViewParameters(0, 260, 0);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new MapViewParameters(0, 0, 260);
        });
    }

    @Test
    void TopLeftWorks() {
        Point2D expectedPoint1 = new Point2D(135735, 92327);
        Point2D expectedPoint2 = new Point2D(33554432, 40265318.4);
        Point2D expectedPoint3 = new Point2D(0.2, 0);
        Point2D expectedPoint4 = new Point2D(134217728, 134217728);

        assertEquals(expectedPoint1, map1.topLeft());
        assertEquals(expectedPoint2, map2.topLeft());
        assertEquals(expectedPoint3, map3.topLeft());
        assertEquals(expectedPoint4, map4.topLeft());
    }

    @Test
    void WithMinXYWorks() {
        MapViewParameters expectedMap1 = new MapViewParameters(10, 235929.6, 183500.8);
        MapViewParameters expectedMap2 = new MapViewParameters(18, 46976204.8, 60397977.6);
        MapViewParameters expectedMap3 = new MapViewParameters(0, 0.9, 0.1);
        MapViewParameters expectedMap4 = new MapViewParameters(19, 120795955.2, 93952409.6);

        assertEquals(expectedMap1, map1.withMinXY(235929.6, 183500.8));
        assertEquals(expectedMap2, map2.withMinXY(46976204.8, 60397977.6));
        assertEquals(expectedMap3, map3.withMinXY(0.9, 0.1));
        assertEquals(expectedMap4, map4.withMinXY(120795955.2, 93952409.6));
    }

    @Test
    void PointAtWorks() {
        PointWebMercator expected1 = new PointWebMercator(0.5178260803222656, 0.3522377014160156);
        PointWebMercator expected2 = new PointWebMercator(0.5000000014901161, 0.6000000014901161);
        PointWebMercator expected3 = new PointWebMercator(0.0023437500000000003, 0.003125);
        PointWebMercator expected4 = new PointWebMercator(1.0, 1.0);

        assertEquals(expected1, map1.pointAt(10, 10));
        assertEquals(expected2, map2.pointAt(0.1, 0.1));
        assertEquals(expected3, map3.pointAt(0.4, 0.8));
        assertEquals(expected4, map4.pointAt(0,0));
    }

    @Test
    void viewXWorks() {
        double expected1 = -11111;
        double expected2 = -33554432;
        double expected3 = 0;
        double expected4 = -8;

        assertEquals(expected1, map1.viewX(point1));
        assertEquals(expected2, map2.viewX(point2));
        assertEquals(expected3, map3.viewX(point3));
        assertEquals(expected4, map4.viewX(point4));
    }

    @Test
    void viewYWorks() {
        double expected1 = 11111;
        double expected2 = 3910.2;
        double expected3 = 0;
        double expected4 = -7728;

        assertEquals(expected1, map1.viewY(point1));
        assertEquals(expected2, map2.viewY(point2),1e-6);
        assertEquals(expected3, map3.viewY(point3));
        assertEquals(expected4, map4.viewY(point4));
    }

}
