package ch.epfl.javelo.projection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PointWebMercatorTestUs {
    PointWebMercator pointWebMercator = new PointWebMercator(0.518275214444, 0.353664894749);

    @Test
    void of() {
        double expectedX = 0.518275214444;
        double expectedY = 0.353664894749;
        PointWebMercator actual = PointWebMercator.of(19, 69561722, 47468099);
        assertEquals(expectedX, actual.x(), 1e-8);
        assertEquals(expectedY, actual.y(), 1e-8);
    }

    @Test
    void ofPointCh() {
        PointCh test = new PointCh(2800000,1199999);
        double expectedLon = WebMercator.x(0.17566529683995633);
        double expectedLat = WebMercator.y(0.8189253004839152);
        PointWebMercator expected = new PointWebMercator(expectedLon,expectedLat);
        PointWebMercator actual = PointWebMercator.ofPointCh(test);
        assertEquals(expected.x(),actual.x(),Math.pow(10,-10));
        assertEquals(expected.y(),actual.y(),Math.pow(10,-10));
    }

    @Test
    void xAtZoomLevel() {
        assertEquals(69561722, pointWebMercator.xAtZoomLevel(19), 1e6);
    }

    @Test
    void yAtZoomLevel() {
        assertEquals(47468099, pointWebMercator.yAtZoomLevel(19), 1e6);
    }

    @Test
    void lon() {
        assertEquals(Math.toRadians(6.5790772), pointWebMercator.lon(), 1e-6);
    }

    @Test
    void lat() {
        assertEquals(Math.toRadians(46.5218976), pointWebMercator.lat(), 1e-6);
    }

    @Test
    void toPointCh() {
        double expectedE = Ch1903.e(Math.toRadians(6.5790772), Math.toRadians(46.5218976));
        double expectedN = Ch1903.n(Math.toRadians(6.5790772), Math.toRadians(46.5218976));
        assertEquals(expectedE, pointWebMercator.toPointCh().e(), 1e-3);
        assertEquals(expectedN, pointWebMercator.toPointCh().n(), 1e-3);
    }
}