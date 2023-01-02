package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoutePointTestUs {

    // Tests Sofia

    @Test
    void withPositionShiftedByWorks(){
        RoutePoint routePoint = new RoutePoint(new PointCh(2607098, 1107654), 13500, 450);
        RoutePoint expectedPoint1 = new RoutePoint(new PointCh(2607098, 1107654), 13500 + 100, 450);
        RoutePoint expectedPoint2 = new RoutePoint(new PointCh(2607098, 1107654), 13500 - 100, 450);
        assertEquals(expectedPoint1, routePoint.withPositionShiftedBy(100));
        assertEquals(expectedPoint2, routePoint.withPositionShiftedBy(-100));
    }

    @Test
    void min1Works(){
        RoutePoint routePoint1 = new RoutePoint(new PointCh(2607098, 1107654), 13500, 450);
        RoutePoint routePoint2  = new RoutePoint(new PointCh(2601098, 1101654), 4500, 150);
        assertEquals(routePoint2, routePoint1.min(routePoint2));
        assertEquals(routePoint2, routePoint2.min(routePoint1));
    }

    @Test
    void min2Works(){
        RoutePoint routePoint1 = new RoutePoint(new PointCh(2607098, 1107654), 13500, 450);
        assertEquals(new RoutePoint(new PointCh(2601098, 1101654), 4500, 150),routePoint1.min(new PointCh(2601098, 1101654),4500,150));
        assertEquals(routePoint1,routePoint1.min(new PointCh(2601098, 1101654),4500,850));
    }

}