package ch.epfl.javelo.routing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ElevationProfileTestUs {

    // Tests Sofia

    @Test
    void classRaisesIllegalArgument() {
        float[] l = {3, 4, 5};
        float[] j = {2};
        float[] er = {};
        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfile el = new ElevationProfile(-1, l);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfile el = new ElevationProfile(0, l);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfile el = new ElevationProfile(1, j);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfile el = new ElevationProfile(2, er);
        });
    }

    @Test
    void classWorks() {
        float[] l = {2.25F, 1.67F, 7.1F, 4.1F};
        ElevationProfile el = new ElevationProfile(2, l);

        assertEquals(2, el.length());
        assertEquals(1.67F, el.minElevation());
        assertEquals(7.1F, el.maxElevation());
        assertEquals(5.429999947547913, el.totalAscent(), 1e-6);
        assertEquals(3.5800000429153442, el.totalDescent());

        assertEquals(4.384999930858613, el.elevationAt(1), 1e-12);
        assertEquals(2.25F, el.elevationAt(-5));
        assertEquals(4.1F, el.elevationAt(200));
    }

}