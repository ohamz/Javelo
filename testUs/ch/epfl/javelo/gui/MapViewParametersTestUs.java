package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MapViewParametersTestUs {

    PointWebMercator point1 = PointWebMercator.of(10, 135735, 92327);
    MapViewParameters map1 = new MapViewParameters(10, 135735, 92327);

    PointWebMercator point2 = PointWebMercator.of(18, 33554432, 40265318.4);
    MapViewParameters map2 = new MapViewParameters(18, 33554432, 40265318.4);

    PointWebMercator point3 = PointWebMercator.of(0, 0.2, 0);
    MapViewParameters map3 = new MapViewParameters(0, 0.2, 0);

    PointWebMercator point4 = PointWebMercator.of(19, 134217728, 134217728);
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
    void viewXWorksOnCornerPoints() {
        assertEquals(0.0, map1.viewX(point1));
        assertEquals(0.0, map2.viewX(point2));
        assertEquals(0.0, map3.viewX(point3));
        assertEquals(0.0, map4.viewX(point4));
    }

    @Test
    void viewXWorksOnRandomPoints() {
        PointWebMercator point0 = new PointWebMercator(0,0);
        PointWebMercator point1 = new PointWebMercator(0.1, 0.2);
        PointWebMercator point2 = new PointWebMercator(0.5, 0.4);
        PointWebMercator point3 = new PointWebMercator(0.4, 0.8);
        PointWebMercator point4 = new PointWebMercator(1, 1);

        assertEquals(-135735.0, map1.viewX(point0));
        assertEquals(-109520.6, map1.viewX(point1));
        assertEquals(-4663.0, map1.viewX(point2));
        assertEquals(-30877.399999999994, map1.viewX(point3));
        assertEquals(126409.0, map1.viewX(point4));

        assertEquals(-3.3554432E7, map2.viewX(point0));
        assertEquals(-2.68435456E7, map2.viewX(point1));
        assertEquals(0.0, map2.viewX(point2));
        assertEquals(-6710886.3999999985, map2.viewX(point3));
        assertEquals(3.3554432E7, map2.viewX(point4));

        assertEquals(-0.2, map3.viewX(point0));
        assertEquals(25.400000000000002, map3.viewX(point1));
        assertEquals(127.8, map3.viewX(point2));
        assertEquals(102.2, map3.viewX(point3));
        assertEquals(255.8, map3.viewX(point4));

        assertEquals(-1.34217728E8, map4.viewX(point0));
        assertEquals(-1.207959552E8, map4.viewX(point1));
        assertEquals(-6.7108864E7, map4.viewX(point2));
        assertEquals(-8.05306368E7, map4.viewX(point3));
        assertEquals(0.0, map4.viewX(point4));
    }

    @Test
    void viewYWorksOnCornerPoints() {
        assertEquals(0.0, map1.viewY(point1));
        assertEquals(0.0, map2.viewY(point2));
        assertEquals(0.0, map3.viewY(point3));
        assertEquals(0.0, map4.viewY(point4));
    }

    @Test
    void viewYWorksOnRandomPoints() {
        PointWebMercator point0 = new PointWebMercator(0,0);
        PointWebMercator point1 = new PointWebMercator(0.1, 0.2);
        PointWebMercator point2 = new PointWebMercator(0.5, 0.4);
        PointWebMercator point3 = new PointWebMercator(0.4, 0.8);
        PointWebMercator point4 = new PointWebMercator(1, 1);

        assertEquals(-92327.0, map1.viewY(point0));
        assertEquals(-39898.2, map1.viewY(point1));
        assertEquals(12530.600000000006, map1.viewY(point2));
        assertEquals(117388.20000000001, map1.viewY(point3));
        assertEquals(169817.0, map1.viewY(point4));

        assertEquals(-4.02653184E7, map2.viewY(point0));
        assertEquals(-2.6843545599999998E7, map2.viewY(point1));
        assertEquals(-1.3421772799999997E7, map2.viewY(point2));
        assertEquals(1.3421772800000004E7, map2.viewY(point3));
        assertEquals(2.68435456E7, map2.viewY(point4));

        assertEquals(0.0, map3.viewY(point0));
        assertEquals(51.2, map3.viewY(point1));
        assertEquals(102.4, map3.viewY(point2));
        assertEquals(204.8, map3.viewY(point3));
        assertEquals(256.0, map3.viewY(point4));

        assertEquals(-1.34217728E8, map4.viewY(point0));
        assertEquals(-1.073741824E8, map4.viewY(point1));
        assertEquals(-8.05306368E7, map4.viewY(point2));
        assertEquals(-2.6843545599999994E7, map4.viewY(point3));
        assertEquals(0.0, map4.viewY(point4));
    }

}
