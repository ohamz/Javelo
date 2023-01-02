package ch.epfl.javelo.projection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WebMercatorTestUs {

    @Test
    void TestX() {
        double expected = 0.518275214444;
        double actual = WebMercator.x(Math.toRadians(6.5790772));
        assertEquals(expected, actual, 1e-12);
    }

    @Test
    void TestY() {
        double expected = 0.353664894749;
        double actual = WebMercator.y(Math.toRadians(46.5218976));
        assertEquals(expected, actual, 1e-11);
    }

    @Test
    void TestLon() {
        double expected = Math.toRadians(6.5790772);
        double actual = WebMercator.lon(0.518275214444);
        assertEquals(expected, actual, 1e-7);
    }

    @Test
    void TestLat() {
        double expected = Math.toRadians(46.5218976);
        double actual = WebMercator.lat(0.353664894749);
        assertEquals(expected, actual, 1e-7);
    }
}