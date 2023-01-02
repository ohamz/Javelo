package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.Stream;

import static java.lang.Float.NaN;
import static org.junit.jupiter.api.Assertions.*;

class MultiRouteTestUs {

    PointCh point1 = new PointCh(2535123, 1152123);
    PointCh point2 = new PointCh(2535234, 1152234);
    PointCh point3 = new PointCh(2535345, 1152345);
    PointCh point4 = new PointCh(2535460, 1152263);
    PointCh point5 = new PointCh(2535617, 1152381);
    PointCh point6 = new PointCh(2535843, 1152401);
    PointCh point7 = new PointCh(2535747.34, 1152285.4);
    PointCh point8 = new PointCh(2535656, 1152175);

    DoubleUnaryOperator profile1 = Functions.sampled(new float[]
            {50, 30, 40, 60}, point1.distanceTo(point2));
    DoubleUnaryOperator profile2 = Functions.sampled(new float[]
            {60, 80, 100, 80}, point2.distanceTo(point3));
    DoubleUnaryOperator profile3 = Functions.sampled(new float[]
            {80, 50, 10, 30}, point3.distanceTo(point4));
    DoubleUnaryOperator profile4 = Functions.sampled(new float[]
            {30, 50, 80, 60}, point4.distanceTo(point5));
    DoubleUnaryOperator profile5 = Functions.sampled(new float[]
            {60, 70, 90, 100}, point5.distanceTo(point6));
    DoubleUnaryOperator profile6 = Functions.sampled(new float[]
            {100, 90, 80, 70}, point6.distanceTo(point7));
    DoubleUnaryOperator profile7 = Functions.sampled(new float[]
            {70, 60, 80, 70}, point7.distanceTo(point8));
    DoubleUnaryOperator profile3Reverse = Functions.sampled(new float[]
            {30, 10, 50, 80}, point4.distanceTo(point3));
    DoubleUnaryOperator profile5Reverse = Functions.sampled(new float[]
            {100, 90, 70, 60}, point6.distanceTo(point5));
    DoubleUnaryOperator profile7Reverse = Functions.sampled(new float[]
            {70, 80, 60, 70}, point8.distanceTo(point7));


    Edge edge1 = new Edge(0, 1, point1, point2, point1.distanceTo(point2), profile1);
    Edge edge2 = new Edge(1, 2, point2, point3, point2.distanceTo(point3), profile2);
    Edge edge3 = new Edge(2, 3, point3, point4, point3.distanceTo(point4), profile3);
    Edge edge4 = new Edge(3, 4, point4, point5, point4.distanceTo(point5), profile4);
    Edge edge5 = new Edge(4, 5, point5, point6, point5.distanceTo(point6), profile5);
    Edge edge6 = new Edge(5, 6, point6, point7, point6.distanceTo(point7), profile6);
    Edge edge7 = new Edge(6, 7, point7, point8, point7.distanceTo(point8), profile7);
    Edge edge3Reverse = new Edge(3, 2, point4, point3, point4.distanceTo(point3), profile3Reverse);
    Edge edge5Reverse = new Edge(5, 4, point6, point5, point6.distanceTo(point5), profile5Reverse);
    Edge edge7Reverse = new Edge(7, 6, point8, point7, point8.distanceTo(point7), profile7Reverse);

    List<Edge> edges1;
    List<PointCh> points1;
    SingleRoute singleRoute1;

    List<Edge> edges2;
    List<PointCh> points2;
    SingleRoute singleRoute2;

    List<Edge> edges3;
    List<PointCh> points3;
    SingleRoute singleRoute3;

    List<Edge> edges4;
    List<PointCh> points4;
    SingleRoute singleRoute4;

    MultiRoute multiRoute1;
    MultiRoute multiRoute2;
    MultiRoute multiRoute3;
    MultiRoute multiRoute4;
    MultiRoute multiRoute5;

    void create() {
        this.edges1 = List.of(edge1, edge2);
        this.edges2 = List.of(edge3, edge3Reverse, edge3);
        this.edges3 = List.of(edge4, edge5, edge5Reverse, edge5);
        this.edges4 = List.of(edge6, edge7, edge7Reverse, edge7);
        this.points1 = List.of(point1, point2, point3);
        this.points2 = List.of(point3, point4, point3, point4);
        this.points3 = List.of(point4, point5, point6, point5, point6);
        this.points4 = List.of(point6, point7, point8, point7, point8);

        singleRoute1 = new SingleRoute(edges1);
        singleRoute2 = new SingleRoute(edges2);
        singleRoute3 = new SingleRoute(edges3);
        singleRoute4 = new SingleRoute(edges4);

        List<Route> routes1 = List.of(singleRoute1, singleRoute2, singleRoute3, singleRoute4);
        multiRoute1 = new MultiRoute(List.copyOf(routes1));

        List<Route> routeIntermediaire1 = List.of(singleRoute1, singleRoute2);
        multiRoute2 = new MultiRoute((List.copyOf(routeIntermediaire1)));

        List<Route> routes2 = List.of(singleRoute3, singleRoute4);
        multiRoute3 = new MultiRoute(List.copyOf(routes2));

        List<Route> routes3 = List.of(multiRoute2, singleRoute3, singleRoute4);
        multiRoute4 = new MultiRoute(List.copyOf(routes3));

        List<Route> routes4 = List.of(multiRoute2, multiRoute3);
        multiRoute5 = new MultiRoute(List.copyOf(routes4));

    }

    @Test
    void MultiRouteThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new MultiRoute(List.of());
        });
    }

    @Test
    void IndexOfSegmentAtWorksOnSingleRoutesOnly() {
        create();

        //multiRoute 1
        assertEquals(0, multiRoute1.indexOfSegmentAt(100));
        assertEquals(1, multiRoute1.indexOfSegmentAt(500));
        assertEquals(1, multiRoute1.indexOfSegmentAt(600));
        assertEquals(2, multiRoute1.indexOfSegmentAt(1000));
        assertEquals(3, multiRoute1.indexOfSegmentAt(2100));

        //multiRoute 2
        assertEquals(0, multiRoute2.indexOfSegmentAt(100));
        assertEquals(0, multiRoute2.indexOfSegmentAt(200));
        assertEquals(1, multiRoute2.indexOfSegmentAt(650));
        assertEquals(1, multiRoute2.indexOfSegmentAt(700));

        //multiRoute 3
        assertEquals(0, multiRoute3.indexOfSegmentAt(100));
        assertEquals(0, multiRoute3.indexOfSegmentAt(500));
        assertEquals(0, multiRoute3.indexOfSegmentAt(650));
        assertEquals(0, multiRoute3.indexOfSegmentAt(800));
        assertEquals(1, multiRoute3.indexOfSegmentAt(1000));
        assertEquals(1, multiRoute3.indexOfSegmentAt(1200));
        assertEquals(1, multiRoute3.indexOfSegmentAt(1400));
    }

    @Test
    void IndexOfSegmentAtWorksOnMixedRoutes() {
        create();

        //multiRoute4
        assertEquals(0, multiRoute4.indexOfSegmentAt(100));
        assertEquals(0, multiRoute4.indexOfSegmentAt(200));
        assertEquals(1, multiRoute4.indexOfSegmentAt(650));
        assertEquals(1, multiRoute4.indexOfSegmentAt(700));
        assertEquals(1, multiRoute4.indexOfSegmentAt(725));
        assertEquals(2, multiRoute4.indexOfSegmentAt(1000));
        assertEquals(2, multiRoute4.indexOfSegmentAt(1500));
        assertEquals(3, multiRoute4.indexOfSegmentAt(1750));
        assertEquals(3, multiRoute4.indexOfSegmentAt(2100));

    }

    @Test
    void IndexOfSegmentAtWorksOnMultiRoutesOfMultiRoutes() {
        create();

        //multiRoute5
        assertEquals(0, multiRoute5.indexOfSegmentAt(100));
        assertEquals(0, multiRoute5.indexOfSegmentAt(200));
        assertEquals(1, multiRoute5.indexOfSegmentAt(650));
        assertEquals(1, multiRoute5.indexOfSegmentAt(700));
        assertEquals(2, multiRoute5.indexOfSegmentAt(1000));
        assertEquals(2, multiRoute5.indexOfSegmentAt(1500));
        assertEquals(3, multiRoute5.indexOfSegmentAt(1750));
        assertEquals(3, multiRoute5.indexOfSegmentAt(2100));
    }

    @Test
    void IndexOfSegmentAtWorksOnEdgeCases() {
        create();

        //clamp works on negative cases
        assertEquals(0, multiRoute1.indexOfSegmentAt(-1));
        assertEquals(0, multiRoute2.indexOfSegmentAt(-1));
        assertEquals(0, multiRoute3.indexOfSegmentAt(-1));
        assertEquals(0, multiRoute4.indexOfSegmentAt(-1));
        assertEquals(0, multiRoute5.indexOfSegmentAt(-1));

        //clamp works on position > length of the road
        assertEquals(3, multiRoute1.indexOfSegmentAt(multiRoute1.length() + 1));
        assertEquals(1, multiRoute2.indexOfSegmentAt(multiRoute2.length() + 1));
        assertEquals(1, multiRoute3.indexOfSegmentAt(multiRoute3.length() + 1));
        assertEquals(3, multiRoute4.indexOfSegmentAt(multiRoute4.length() + 1));
        assertEquals(3, multiRoute5.indexOfSegmentAt(multiRoute5.length() + 1));

        //works for position = 0
        assertEquals(0, multiRoute1.indexOfSegmentAt(0));
        assertEquals(0, multiRoute2.indexOfSegmentAt(0));
        assertEquals(0, multiRoute3.indexOfSegmentAt(0));
        assertEquals(0, multiRoute4.indexOfSegmentAt(0));
        assertEquals(0, multiRoute5.indexOfSegmentAt(0));

        //works for position = length
        assertEquals(3, multiRoute1.indexOfSegmentAt(multiRoute1.length()));
        assertEquals(1, multiRoute2.indexOfSegmentAt(multiRoute2.length()));
        assertEquals(1, multiRoute3.indexOfSegmentAt(multiRoute3.length()));
        assertEquals(3, multiRoute4.indexOfSegmentAt(multiRoute4.length()));
        assertEquals(3, multiRoute5.indexOfSegmentAt(multiRoute4.length()));

    }

    @Test
    void LengthWorks() {
        create();

        double expectedLength1And4And5 = singleRoute1.length() + singleRoute2.length() + singleRoute3.length() + singleRoute4.length();
        double expectedLength2 = singleRoute1.length() + singleRoute2.length();
        double expectedLength3 = singleRoute3.length() + singleRoute4.length();

        assertEquals(expectedLength1And4And5, multiRoute1.length());
        assertEquals(expectedLength2, multiRoute2.length());
        assertEquals(expectedLength3, multiRoute3.length());
        assertEquals(expectedLength1And4And5, multiRoute4.length());
        assertEquals(expectedLength1And4And5, multiRoute5.length());
    }

    @Test
    void EdgesWorks() {
        create();

        List<Edge> expected1And4And5 = new ArrayList<>();
        expected1And4And5.addAll(edges1);
        expected1And4And5.addAll(edges2);
        expected1And4And5.addAll(edges3);
        expected1And4And5.addAll(edges4);

        List<Edge> expected2 = new ArrayList<>();
        expected2.addAll(edges1);
        expected2.addAll(edges2);

        List<Edge> expected3 = new ArrayList<>();
        expected3.addAll(edges3);
        expected3.addAll(edges4);

        assertEquals(expected1And4And5, multiRoute1.edges());
        assertEquals(expected2, multiRoute2.edges());
        assertEquals(expected3, multiRoute3.edges());
        assertEquals(expected1And4And5, multiRoute4.edges());
        assertEquals(expected1And4And5, multiRoute5.edges());
    }

    @Test
    void PointsWorks() {
        create();

        List<PointCh> expectedPoints1And4And5 = List.of(point1, point2, point3, point4, point3, point4, point5, point6, point5, point6, point7, point8, point7, point8);
        List<PointCh> expectedPoints2 = List.of(point1, point2, point3, point4, point3, point4);
        List<PointCh> expectedPoints3 = List.of(point4, point5, point6, point5, point6, point7, point8, point7, point8);

        assertEquals(expectedPoints1And4And5, multiRoute1.points());
        assertEquals(expectedPoints2, multiRoute2.points());
        assertEquals(expectedPoints3, multiRoute3.points());
        assertEquals(expectedPoints1And4And5, multiRoute4.points());
        assertEquals(expectedPoints1And4And5, multiRoute5.points());
    }

    @Test
    void PointAtWorksOnKnownValues() {
        create();

        //multiRoute1
        assertEquals(new PointCh(2535193.7106781187, 1152193.7106781187), multiRoute1.pointAt(100));
        assertEquals(new PointCh(2535423.5203409707, 1152289.0115829601), multiRoute1.pointAt(500));
        assertEquals(new PointCh(2535347.900817107, 1152342.9315912803), multiRoute1.pointAt(600));
        assertEquals(new PointCh(2535682.665075113, 1152386.811068594), multiRoute1.pointAt(1000));
        assertEquals(new PointCh(2535716.326987979, 1152247.915474851), multiRoute1.pointAt(2100));

        //multiRoute2
        assertEquals(new PointCh(2535193.7106781187, 1152193.7106781187), multiRoute2.pointAt(100));
        assertEquals(new PointCh(2535264.4213562375, 1152264.4213562373), multiRoute2.pointAt(200));
        assertEquals(new PointCh(2535388.611396146, 1152313.9031784004), multiRoute2.pointAt(650));
        assertEquals(new PointCh(2535429.3219751846, 1152284.8747655204), multiRoute2.pointAt(700));

        //multiRoute3
        assertEquals(new PointCh(2535539.9388587554, 1152323.0814352431), multiRoute3.pointAt(100));
        assertEquals(new PointCh(2535766.5819807285, 1152394.2373434273), multiRoute3.pointAt(500));
        assertEquals(new PointCh(2535617.1659131846, 1152381.0146825828), multiRoute3.pointAt(650));
        assertEquals(new PointCh(2535766.250154359, 1152394.2079782619), multiRoute3.pointAt(800));
        assertEquals(new PointCh(2535764.6152768503, 1152306.2762492567), multiRoute3.pointAt(1000));
        assertEquals(new PointCh(2535674.879029043, 1152197.8185330231), multiRoute3.pointAt(1200));
        assertEquals(new PointCh(2535692.308568288, 1152218.8851099082), multiRoute3.pointAt(1400));

        //multiRoute4
        assertEquals(new PointCh(2535193.7106781187, 1152193.7106781187), multiRoute4.pointAt(100));
        assertEquals(new PointCh(2535264.4213562375, 1152264.4213562373), multiRoute4.pointAt(200));
        assertEquals(new PointCh(2535388.611396146, 1152313.9031784004), multiRoute4.pointAt(650));
        assertEquals(new PointCh(2535429.3219751846, 1152284.8747655204), multiRoute4.pointAt(700));
        assertEquals(new PointCh(2535682.665075113, 1152386.811068594), multiRoute4.pointAt(1000));
        assertEquals(new PointCh(2535728.7186335917, 1152390.8866047426), multiRoute4.pointAt(1500));
        assertEquals(new PointCh(2535756.759730425, 1152296.7832410324), multiRoute4.pointAt(1750));
        assertEquals(new PointCh(2535716.326987979, 1152247.915474851), multiRoute4.pointAt(2100));

        //multiRoute5
        assertEquals(new PointCh(2535193.7106781187, 1152193.7106781187), multiRoute5.pointAt(100));
        assertEquals(new PointCh(2535264.4213562375, 1152264.4213562373), multiRoute5.pointAt(200));
        assertEquals(new PointCh(2535388.611396146, 1152313.9031784004), multiRoute5.pointAt(650));
        assertEquals(new PointCh(2535429.3219751846, 1152284.8747655204), multiRoute5.pointAt(700));
        assertEquals(new PointCh(2535682.665075113, 1152386.811068594), multiRoute5.pointAt(1000));
        assertEquals(new PointCh(2535728.7186335917, 1152390.8866047426), multiRoute5.pointAt(1500));
        assertEquals(new PointCh(2535756.759730425, 1152296.7832410324), multiRoute5.pointAt(1750));
        assertEquals(new PointCh(2535716.326987979, 1152247.915474851), multiRoute5.pointAt(2100));
    }

    @Test
    void PointAtWorksOnEdgeValues() {
        create();

        //works on negative values
        assertEquals(point1, multiRoute1.pointAt(-1));
        assertEquals(point1, multiRoute2.pointAt(-1));
        assertEquals(point4, multiRoute3.pointAt(-1));
        assertEquals(point1, multiRoute4.pointAt(-1));
        assertEquals(point1, multiRoute5.pointAt(-1));

        //works on values greater than length
        assertEquals(point8, multiRoute1.pointAt(multiRoute1.length() + 1));
        assertEquals(point4, multiRoute2.pointAt(multiRoute2.length() + 1));
        assertEquals(point8, multiRoute3.pointAt(multiRoute3.length() + 1));
        assertEquals(point8, multiRoute4.pointAt(multiRoute4.length() + 1));
        assertEquals(point8, multiRoute5.pointAt(multiRoute4.length() + 1));

        //works on values equal to 0
        assertEquals(point1, multiRoute1.pointAt(0));
        assertEquals(point1, multiRoute2.pointAt(0));
        assertEquals(point4, multiRoute3.pointAt(0));
        assertEquals(point1, multiRoute4.pointAt(0));
        assertEquals(point1, multiRoute5.pointAt(0));

        //works on values equal to length
        assertEquals(point8, multiRoute1.pointAt(multiRoute1.length()));
        assertEquals(point4, multiRoute2.pointAt(multiRoute2.length()));
        assertEquals(point8, multiRoute3.pointAt(multiRoute3.length()));
        assertEquals(point8, multiRoute4.pointAt(multiRoute4.length()));
        assertEquals(point8, multiRoute5.pointAt(multiRoute4.length()));
    }

    @Test
    void ElevationAtWorksOnKnownValues() {
        create();

        //multiRoute1
        assertEquals(39.11099408612291, multiRoute1.elevationAt(100), 1e-12);
        assertEquals(10.96713441948415, multiRoute1.elevationAt(500), 1e-12);
        assertEquals(77.72979530758599, multiRoute1.elevationAt(600), 1e-12);
        assertEquals(68.7166028911134, multiRoute1.elevationAt(1000), 1e-12);
        assertEquals(60.37202453747647, multiRoute1.elevationAt(2100), 1e-12);

        //multiRoute2
        assertEquals(39.11099408612291, multiRoute2.elevationAt(100), 1e-12);
        assertEquals(76.44397634449163, multiRoute2.elevationAt(200), 1e-12);
        assertEquals(44.49245619568782, multiRoute2.elevationAt(650), 1e-12);
        assertEquals(13.994074009369504, multiRoute2.elevationAt(700), 1e-12);

        //multiRoute3
        assertEquals(65.8248234904391, multiRoute3.elevationAt(100), 1e-12);
        assertEquals(89.7120302818561, multiRoute3.elevationAt(500), 1e-12);
        assertEquals(60.02202387408028, multiRoute3.elevationAt(650), 1e-12);
        assertEquals(89.62393478553496, multiRoute3.elevationAt(800), 1e-12);
        assertEquals(75.4177117448635, multiRoute3.elevationAt(1000), 1e-12);
        assertEquals(76.20068832149688, multiRoute3.elevationAt(1200), 1e-12);
        assertEquals(76.14939678896384, multiRoute3.elevationAt(1400), 1e-12);

        //multiRoute4
        assertEquals(39.11099408612291, multiRoute4.elevationAt(100), 1e-12);
        assertEquals(76.44397634449163, multiRoute4.elevationAt(200), 1e-12);
        assertEquals(44.49245619568782, multiRoute4.elevationAt(650), 1e-12);
        assertEquals(13.994074009369504, multiRoute4.elevationAt(700), 1e-12);
        assertEquals(68.7166028911134, multiRoute4.elevationAt(1000), 1e-12);
        assertEquals(79.65981422787853, multiRoute4.elevationAt(1500), 1e-12);
        assertEquals(72.95412829561062, multiRoute4.elevationAt(1750), 1e-12);
        assertEquals(60.37202453747647, multiRoute4.elevationAt(2100), 1e-12);

        //multiRoute5
        assertEquals(39.11099408612291, multiRoute5.elevationAt(100), 1e-12);
        assertEquals(76.44397634449163, multiRoute5.elevationAt(200), 1e-12);
        assertEquals(44.49245619568782, multiRoute5.elevationAt(650), 1e-12);
        assertEquals(13.994074009369504, multiRoute5.elevationAt(700), 1e-12);
        assertEquals(68.7166028911134, multiRoute5.elevationAt(1000), 1e-12);
        assertEquals(79.65981422787853, multiRoute5.elevationAt(1500), 1e-12);
        assertEquals(72.95412829561062, multiRoute5.elevationAt(1750), 1e-12);
        assertEquals(60.37202453747647, multiRoute5.elevationAt(2100), 1e-12);
    }

    @Test
    void ElevationAtWorksOnEdgeValues() {
        create();

        //works on negative values
        assertEquals(50, multiRoute1.elevationAt(-1), 1e-12);
        assertEquals(50, multiRoute2.elevationAt(-1), 1e-12);
        assertEquals(30, multiRoute3.elevationAt(-1), 1e-12);
        assertEquals(50, multiRoute4.elevationAt(-1), 1e-12);
        assertEquals(50, multiRoute5.elevationAt(-1), 1e-12);

        //works on values greater than length
        assertEquals(70, multiRoute1.elevationAt(multiRoute1.length() + 1), 1e-12);
        assertEquals(30, multiRoute2.elevationAt(multiRoute2.length() + 1), 1e-12);
        assertEquals(70, multiRoute3.elevationAt(multiRoute3.length() + 1), 1e-12);
        assertEquals(70, multiRoute4.elevationAt(multiRoute4.length() + 1), 1e-12);
        assertEquals(70, multiRoute5.elevationAt(multiRoute5.length() + 1), 1e-12);

        //works on values equal to 0
        assertEquals(50, multiRoute1.elevationAt(0), 1e-12);
        assertEquals(50, multiRoute2.elevationAt(0), 1e-12);
        assertEquals(30, multiRoute3.elevationAt(0), 1e-12);
        assertEquals(50, multiRoute4.elevationAt(0), 1e-12);
        assertEquals(50, multiRoute5.elevationAt(0), 1e-12);

        //works on values equal to length
        assertEquals(70, multiRoute1.elevationAt(multiRoute1.length()), 1e-12);
        assertEquals(30, multiRoute2.elevationAt(multiRoute2.length()), 1e-12);
        assertEquals(70, multiRoute3.elevationAt(multiRoute3.length()), 1e-12);
        assertEquals(70, multiRoute4.elevationAt(multiRoute4.length()), 1e-12);
        assertEquals(70, multiRoute5.elevationAt(multiRoute5.length()), 1e-12);
    }

    @Test
    void NodeClosestToWorksOnKnownValues() {
        create();

        //multiRoute1
        assertEquals(1, multiRoute1.nodeClosestTo(100));
        assertEquals(3, multiRoute1.nodeClosestTo(500));
        assertEquals(2, multiRoute1.nodeClosestTo(600));
        assertEquals(4, multiRoute1.nodeClosestTo(1000));
        assertEquals(6, multiRoute1.nodeClosestTo(2100));

        //multiRoute2
        assertEquals(1, multiRoute2.nodeClosestTo(100));
        assertEquals(1, multiRoute2.nodeClosestTo(200));
        assertEquals(2, multiRoute2.nodeClosestTo(650));
        assertEquals(3, multiRoute2.nodeClosestTo(700));

        //multiRoute3
        assertEquals(4, multiRoute3.nodeClosestTo(100));
        assertEquals(5, multiRoute3.nodeClosestTo(500));
        assertEquals(4, multiRoute3.nodeClosestTo(650));
        assertEquals(5, multiRoute3.nodeClosestTo(800));
        assertEquals(6, multiRoute3.nodeClosestTo(1000));
        assertEquals(7, multiRoute3.nodeClosestTo(1200));
        assertEquals(7, multiRoute3.nodeClosestTo(1400));

        //multiRoute4
        assertEquals(1, multiRoute4.nodeClosestTo(100));
        assertEquals(1, multiRoute4.nodeClosestTo(200));
        assertEquals(2, multiRoute4.nodeClosestTo(650));
        assertEquals(3, multiRoute4.nodeClosestTo(700));
        assertEquals(4, multiRoute4.nodeClosestTo(1000));
        assertEquals(4, multiRoute4.nodeClosestTo(1500));
        assertEquals(6, multiRoute4.nodeClosestTo(1750));
        assertEquals(6, multiRoute4.nodeClosestTo(2100));

        //multiRoute5
        assertEquals(1, multiRoute5.nodeClosestTo(100));
        assertEquals(1, multiRoute5.nodeClosestTo(200));
        assertEquals(2, multiRoute5.nodeClosestTo(650));
        assertEquals(3, multiRoute5.nodeClosestTo(700));
        assertEquals(4, multiRoute5.nodeClosestTo(1000));
        assertEquals(4, multiRoute5.nodeClosestTo(1500));
        assertEquals(6, multiRoute5.nodeClosestTo(1750));
        assertEquals(6, multiRoute5.nodeClosestTo(2100));

    }

    @Test
    void NodeClosestToWorksOnEdgeValues() {
        create();

        //works on negative values
        assertEquals(0, multiRoute1.nodeClosestTo(-1));
        assertEquals(0, multiRoute2.nodeClosestTo(-1));
        assertEquals(3, multiRoute3.nodeClosestTo(-1));
        assertEquals(0, multiRoute4.nodeClosestTo(-1));
        assertEquals(0, multiRoute5.nodeClosestTo(-1));

        //works on values greater than length
        assertEquals(7, multiRoute1.nodeClosestTo(multiRoute1.length() + 1));
        assertEquals(3, multiRoute2.nodeClosestTo(multiRoute2.length() + 1));
        assertEquals(7, multiRoute3.nodeClosestTo(multiRoute3.length() + 1));
        assertEquals(7, multiRoute4.nodeClosestTo(multiRoute4.length() + 1));
        assertEquals(7, multiRoute5.nodeClosestTo(multiRoute5.length() + 1));

        //works on values equal to 0
        assertEquals(0, multiRoute1.nodeClosestTo(0));
        assertEquals(0, multiRoute2.nodeClosestTo(0));
        assertEquals(3, multiRoute3.nodeClosestTo(0));
        assertEquals(0, multiRoute4.nodeClosestTo(0));
        assertEquals(0, multiRoute5.nodeClosestTo(0));

        //works on values equal to length
        assertEquals(7, multiRoute1.nodeClosestTo(multiRoute1.length()));
        assertEquals(3, multiRoute2.nodeClosestTo(multiRoute2.length()));
        assertEquals(7, multiRoute3.nodeClosestTo(multiRoute3.length()));
        assertEquals(7, multiRoute4.nodeClosestTo(multiRoute4.length()));
        assertEquals(7, multiRoute5.nodeClosestTo(multiRoute5.length()));
    }

    @Test
    void PointClosestToWorksOnValuesOutsideOfSegments() {
        create();

        //multiRoute1
        assertEquals(new RoutePoint(new PointCh(2535151.33161, 1152151.3316100002), 40.0669471060211, 23.862506270602033), multiRoute1.pointClosestTo(new PointCh(2535168.20495, 1152134.45827)));
        assertEquals(new RoutePoint(new PointCh(2535284.984775, 1152284.984775), 229.0810657028165, 41.96730365141696), multiRoute1.pointClosestTo(new PointCh(2535314.66014,1152255.30941)));
        assertEquals(new RoutePoint(new PointCh(2535423.259410883, 1152289.1976374576), 410.0722098832407, 26.526615400166015), multiRoute1.pointClosestTo(new PointCh(2535407.8589,1152267.59936)));
        assertEquals(new RoutePoint(new PointCh(2535532.0362687283, 1152317.1419089804), 827.7924046784461, 22.58244776137388), multiRoute1.pointClosestTo(new PointCh(2535518.46841,1152335.19406)));
        assertEquals(new RoutePoint(new PointCh(2535703.020791555, 1152388.6124594295), 1020.4352685523834, 25.541510684196023), multiRoute1.pointClosestTo(new PointCh(2535700.76928,1152414.05454)));
        assertEquals(new RoutePoint(new PointCh(2535797.894931484, 1152346.4929341371), 1685.477458553404, 24.148620127378198), multiRoute1.pointClosestTo(new PointCh(2535816.4996,1152331.09741)));
        assertEquals(new RoutePoint(new PointCh(2535709.192834078, 1152239.292630635), 1824.6175577146926, 26.286132011657273), multiRoute1.pointClosestTo(new PointCh(2535729.44582,1152222.53622)));

        //multiRoute2
        assertEquals(new RoutePoint(new PointCh(2535151.33161, 1152151.3316100002), 40.0669471060211, 23.862506270602033), multiRoute2.pointClosestTo(new PointCh(2535168.20495, 1152134.45827)));
        assertEquals(new RoutePoint(new PointCh(2535284.984775, 1152284.984775), 229.0810657028165, 41.96730365141696), multiRoute2.pointClosestTo(new PointCh(2535314.66014,1152255.30941)));
        assertEquals(new RoutePoint(new PointCh(2535423.259410883, 1152289.1976374576), 410.0722098832407, 26.526615400166015), multiRoute2.pointClosestTo(new PointCh(2535407.8589,1152267.59936)));

        //multiRoute3
        assertEquals(new RoutePoint(new PointCh(2535532.0362687283, 1152317.1419089804), 90.11420709481865, 22.58244776137388), multiRoute3.pointClosestTo(new PointCh(2535518.46841,1152335.19406)));
        assertEquals(new RoutePoint(new PointCh(2535703.020791555, 1152388.6124594295), 282.75707096875595, 25.541510684196023), multiRoute3.pointClosestTo(new PointCh(2535700.76928,1152414.05454)));
        assertEquals(new RoutePoint(new PointCh(2535797.894931484, 1152346.4929341371), 947.7992609697766, 24.148620127378198), multiRoute3.pointClosestTo(new PointCh(2535816.4996,1152331.09741)));
        assertEquals(new RoutePoint(new PointCh(2535709.192834078, 1152239.292630635), 1086.939360131065, 26.286132011657273), multiRoute3.pointClosestTo(new PointCh(2535729.44582,1152222.53622)));

        //multiRoute4
        assertEquals(new RoutePoint(new PointCh(2535151.33161, 1152151.3316100002), 40.0669471060211, 23.862506270602033), multiRoute4.pointClosestTo(new PointCh(2535168.20495, 1152134.45827)));
        assertEquals(new RoutePoint(new PointCh(2535284.984775, 1152284.984775), 229.0810657028165, 41.96730365141696), multiRoute4.pointClosestTo(new PointCh(2535314.66014,1152255.30941)));
        assertEquals(new RoutePoint(new PointCh(2535423.259410883, 1152289.1976374576), 410.0722098832407, 26.526615400166015), multiRoute4.pointClosestTo(new PointCh(2535407.8589,1152267.59936)));
        assertEquals(new RoutePoint(new PointCh(2535532.0362687283, 1152317.1419089804), 827.7924046784461, 22.58244776137388), multiRoute4.pointClosestTo(new PointCh(2535518.46841,1152335.19406)));
        assertEquals(new RoutePoint(new PointCh(2535703.020791555, 1152388.6124594295), 1020.4352685523834, 25.541510684196023), multiRoute4.pointClosestTo(new PointCh(2535700.76928,1152414.05454)));
        assertEquals(new RoutePoint(new PointCh(2535797.894931484, 1152346.4929341371), 1685.477458553404, 24.148620127378198), multiRoute4.pointClosestTo(new PointCh(2535816.4996,1152331.09741)));
        assertEquals(new RoutePoint(new PointCh(2535709.192834078, 1152239.292630635), 1824.6175577146926, 26.286132011657273), multiRoute4.pointClosestTo(new PointCh(2535729.44582,1152222.53622)));

        //multiRoute5
        assertEquals(new RoutePoint(new PointCh(2535151.33161, 1152151.3316100002), 40.0669471060211, 23.862506270602033), multiRoute5.pointClosestTo(new PointCh(2535168.20495, 1152134.45827)));
        assertEquals(new RoutePoint(new PointCh(2535284.984775, 1152284.984775), 229.0810657028165, 41.96730365141696), multiRoute5.pointClosestTo(new PointCh(2535314.66014,1152255.30941)));
        assertEquals(new RoutePoint(new PointCh(2535423.259410883, 1152289.1976374576), 410.0722098832407, 26.526615400166015), multiRoute5.pointClosestTo(new PointCh(2535407.8589,1152267.59936)));
        assertEquals(new RoutePoint(new PointCh(2535532.0362687283, 1152317.1419089804), 827.7924046784461, 22.58244776137388), multiRoute5.pointClosestTo(new PointCh(2535518.46841,1152335.19406)));
        assertEquals(new RoutePoint(new PointCh(2535703.020791555, 1152388.6124594295), 1020.4352685523834, 25.541510684196023), multiRoute5.pointClosestTo(new PointCh(2535700.76928,1152414.05454)));
        assertEquals(new RoutePoint(new PointCh(2535797.894931484, 1152346.4929341371), 1685.4774585534042, 24.148620127378198), multiRoute5.pointClosestTo(new PointCh(2535816.4996,1152331.09741)));
        assertEquals(new RoutePoint(new PointCh(2535709.192834078, 1152239.292630635), 1824.6175577146926, 26.286132011657273), multiRoute5.pointClosestTo(new PointCh(2535729.44582,1152222.53622)));
    }

    @Test
    void PointClosestToWorksForValuesOnSegments() {
        create();

        //multiRoute1
        assertEquals(new RoutePoint(point1, 0, 0), multiRoute1.pointClosestTo(point1));
        assertEquals(new RoutePoint(point2, 156.97770542341354, 0), multiRoute1.pointClosestTo(point2));
        assertEquals(new RoutePoint(point3, 313.9554108468271, 0), multiRoute1.pointClosestTo(point3));
        assertEquals(new RoutePoint(point4, 455.19633975909386, 0), multiRoute1.pointClosestTo(point4));
        assertEquals(new RoutePoint(point5, 934.078299416595, 0), multiRoute1.pointClosestTo(point5));
        assertEquals(new RoutePoint(point6, 1160.961529294874, 0), multiRoute1.pointClosestTo(point6));
        assertEquals(new RoutePoint(point7, 1764.7753002570983, 0), multiRoute1.pointClosestTo(point7));
        assertEquals(new RoutePoint(point8, 1908.062269657502, 0), multiRoute1.pointClosestTo(point8));

        //multiRoute2
        assertEquals(new RoutePoint(point1, 0, 0), multiRoute2.pointClosestTo(point1));
        assertEquals(new RoutePoint(point2, 156.97770542341354, 0), multiRoute2.pointClosestTo(point2));
        assertEquals(new RoutePoint(point3, 313.9554108468271, 0), multiRoute2.pointClosestTo(point3));
        assertEquals(new RoutePoint(point4, 455.19633975909386, 0), multiRoute2.pointClosestTo(point4));

        //multiRoute3
        assertEquals(new RoutePoint(point4, 0, 0), multiRoute3.pointClosestTo(point4));
        assertEquals(new RoutePoint(point5, 196.4001018329675, 0), multiRoute3.pointClosestTo(point5));
        assertEquals(new RoutePoint(point6, 423.2833317112465, 0), multiRoute3.pointClosestTo(point6));
        assertEquals(new RoutePoint(point7, 1027.0971026734708, 0), multiRoute3.pointClosestTo(point7));
        assertEquals(new RoutePoint(point8, 1170.3840720738744, 0), multiRoute3.pointClosestTo(point8));

        //multiRoute4
        assertEquals(new RoutePoint(point1, 0, 0), multiRoute4.pointClosestTo(point1));
        assertEquals(new RoutePoint(point2, 156.97770542341354, 0), multiRoute4.pointClosestTo(point2));
        assertEquals(new RoutePoint(point3, 313.9554108468271, 0), multiRoute4.pointClosestTo(point3));
        assertEquals(new RoutePoint(point4, 455.19633975909386, 0), multiRoute4.pointClosestTo(point4));
        assertEquals(new RoutePoint(point5, 934.078299416595, 0), multiRoute4.pointClosestTo(point5));
        assertEquals(new RoutePoint(point6, 1160.961529294874, 0), multiRoute4.pointClosestTo(point6));
        assertEquals(new RoutePoint(point7, 1764.7753002570983, 0), multiRoute4.pointClosestTo(point7));
        assertEquals(new RoutePoint(point8, 1908.062269657502, 0), multiRoute4.pointClosestTo(point8));

        //multiRoute5
        assertEquals(new RoutePoint(point1, 0, 0), multiRoute5.pointClosestTo(point1));
        assertEquals(new RoutePoint(point2, 156.97770542341354, 0), multiRoute5.pointClosestTo(point2));
        assertEquals(new RoutePoint(point3, 313.9554108468271, 0), multiRoute5.pointClosestTo(point3));
        assertEquals(new RoutePoint(point4, 455.19633975909386, 0), multiRoute5.pointClosestTo(point4));
        assertEquals(new RoutePoint(point5, 934.078299416595, 0), multiRoute5.pointClosestTo(point5));
        assertEquals(new RoutePoint(point6, 1160.961529294874, 0), multiRoute5.pointClosestTo(point6));
        assertEquals(new RoutePoint(point7, 1764.7753002570983, 0), multiRoute5.pointClosestTo(point7));
        assertEquals(new RoutePoint(point8, 1908.062269657502, 0), multiRoute5.pointClosestTo(point8));
    }

}

class MultiRouteTestWilliam {
    PointCh point1 = new PointCh(2595000,1200000);
    PointCh point2 = new PointCh(2600000,1204000);
    PointCh point3 = new PointCh(2603000,1202000);
    PointCh point4 = new PointCh(2605000,1203000);
    PointCh point5 = new PointCh(2609000,1199000);
    PointCh point6 = new PointCh(2609500, 1198500);

    float[] samples1 = new float[]{200, 240, 230, 360,380};
    DoubleUnaryOperator profile1 = Functions.sampled(samples1, point1.distanceTo(point2) );
    Edge edge1 = new Edge(0, 3, point1, point2, point1.distanceTo(point2), profile1);
    float[] samples2 = new float[]{380,360, 340, 350,320,350};
    DoubleUnaryOperator profile2 = Functions.sampled(samples2, point2.distanceTo(point3));
    Edge edge2 = new Edge(3, 8,point2 ,point3,point2.distanceTo(point3), profile2);
    float[] samples3 = new float[]{350, 320, 300, 280,270,250};
    DoubleUnaryOperator profile3 = Functions.sampled(samples3,point3.distanceTo(point4));
    Edge edge3 = new Edge(8,10,point3, point4,point3.distanceTo(point4),profile3);
    float[] samples4 = new float[]{250, 300, 330, 360,380,400};
    DoubleUnaryOperator profile4 = Functions.sampled(samples4,point4.distanceTo(point5));
    Edge edge4 = new Edge(10, 18,point4 ,point5, point4.distanceTo(point5),profile4);
    // Edge edge5 = new Edge(18, 5,point5 ,point6, point5.distanceTo(point6),x ->Float.NaN);

    float[] samplesReverse4 = new float[]{400, 380, 360, 330,300,250};
    DoubleUnaryOperator profileReverse4 = Functions.sampled(samplesReverse4,point4.distanceTo(point5));
    Edge edgeReverse4 = new Edge(18, 10,point5 ,point4, point4.distanceTo(point5),profileReverse4);
    float[] samplesReverse3 = new float[]{250, 270, 280, 300,320,350};
    DoubleUnaryOperator profileReverse3 = Functions.sampled(samplesReverse3,point3.distanceTo(point4));
    Edge edgeReverse3 = new Edge(10,8,point4, point3,point3.distanceTo(point4),profileReverse3);
    float[] samplesReverse2 = new float[]{350,320, 350, 340, 360,380};
    DoubleUnaryOperator profileReverse2 = Functions.sampled(samplesReverse2, point2.distanceTo(point3));
    Edge edgeReverse2 = new Edge(8, 3,point3 ,point2,point2.distanceTo(point3), profileReverse2);
    float[] samplesReverse1 = new float[]{380, 360, 230, 240,200};
    DoubleUnaryOperator profileReverse1 = Functions.sampled(samplesReverse1, point1.distanceTo(point2) );
    Edge edgeReverse1 = new Edge(3, 0, point2, point1, point1.distanceTo(point2), profileReverse1);

    List<Edge> edges1 = new ArrayList<>();
    List<Edge> edges2 = new ArrayList<>();
    List<Edge> edges3 = new ArrayList<>();
    List<Edge> edges4 = new ArrayList<>();

    @Test
    public void indexOfSegmentAtNestedMultiRoutes(){
        edges1.add(edge1);
        edges1.add(edge2);
        SingleRoute route1 = new SingleRoute(edges1);

        edges2.add(edge3);
        edges2.add(edge4);
        SingleRoute route2 = new SingleRoute(edges2);

        edges3.add(edgeReverse4);
        edges3.add(edgeReverse3);
        SingleRoute route3 = new SingleRoute(edges3);

        edges4.add(edgeReverse2);
        edges4.add(edgeReverse1);
        SingleRoute route4 = new SingleRoute(edges4);


        List<Route> routes1 = new ArrayList<>();
        routes1.add(route1);
        routes1.add(route2);
        List<Route> routes2 = new ArrayList<>();
        routes2.add(route3);
        routes2.add(route4);

        MultiRoute multiRoute1 = new MultiRoute(routes1);
        MultiRoute multiRoute2 = new MultiRoute(routes2);

        List<Route> multiRoutes = new ArrayList<>();
        multiRoutes.add(multiRoute1);
        multiRoutes.add(multiRoute2);

        MultiRoute multiRouteSupreme = new MultiRoute(multiRoutes);
        //La méthode fonctionne-t-elle pour les cas limites ?
        assertEquals(3,multiRouteSupreme.indexOfSegmentAt(multiRouteSupreme.length()));
        assertEquals(0,multiRouteSupreme.indexOfSegmentAt(0));
        assertEquals(3,multiRouteSupreme.indexOfSegmentAt(30795));
        //Fonctionne-elle pour des valeurs pratiques ?
        assertEquals(0,multiRouteSupreme.indexOfSegmentAt(5000));
        assertEquals(2,multiRouteSupreme.indexOfSegmentAt(20902));
        //Voyons si vous avez ramené la position entre 0 et la longueur totale.
        assertEquals(0,multiRouteSupreme.indexOfSegmentAt(-4));
        assertEquals(3,multiRouteSupreme.indexOfSegmentAt(multiRouteSupreme.length()+42));

    }
    @Test
    public void indexOfSegmentAtOnlySingleRoutes(){
        edges1.add(edge1);
        edges1.add(edge2);
        SingleRoute route1 = new SingleRoute(edges1);

        edges2.add(edge3);
        edges2.add(edge4);
        SingleRoute route2 = new SingleRoute(edges2);

        edges3.add(edgeReverse4);
        edges3.add(edgeReverse3);
        SingleRoute route3 = new SingleRoute(edges3);
        List<Route> routes = new ArrayList<>();
        routes.add(route1);
        routes.add(route2);
        routes.add(route3);

        MultiRoute bRoute = new MultiRoute(routes);

        assertEquals(1, bRoute.indexOfSegmentAt(13465));
        assertEquals(0, bRoute.indexOfSegmentAt(0));
        assertEquals(2, bRoute.indexOfSegmentAt(21402));
        assertEquals(2, bRoute.indexOfSegmentAt(bRoute.length()));
    }
    @Test
    public void indexOfSegmentAtInceptionMultiRoutes(){
        edges1.add(edge1);
        edges1.add(edge2);
        SingleRoute route1 = new SingleRoute(edges1);

        edges2.add(edge3);
        edges2.add(edge4);
        SingleRoute route2 = new SingleRoute(edges2);

        edges3.add(edgeReverse4);
        edges3.add(edgeReverse3);
        SingleRoute route3 = new SingleRoute(edges3);

        edges4.add(edgeReverse2);
        edges4.add(edgeReverse1);
        SingleRoute route4 = new SingleRoute(edges4);

        List<Route> routes1 = new ArrayList<>();

        routes1.add(route1);
        routes1.add(route2);

        List<Route> routes2 = new ArrayList<>();

        routes2.add(route3);
        routes2.add(route4);

        MultiRoute lucidRoute1 = new MultiRoute(routes1);
        MultiRoute lucidRoute2 = new MultiRoute(routes2);

        List<Route> multiRoutes = new ArrayList<>();
        multiRoutes.add(lucidRoute1);
        multiRoutes.add(lucidRoute2);

        MultiRoute paradoxicalSleep = new MultiRoute(multiRoutes);

        List<Route> multisRoutes = new ArrayList<>();
        multisRoutes.add(paradoxicalSleep);

        MultiRoute inceptionRoute = new MultiRoute(multisRoutes);

        assertEquals(0,inceptionRoute.indexOfSegmentAt(0));
        assertEquals(3,inceptionRoute.indexOfSegmentAt(30795));
        assertEquals(3,inceptionRoute.indexOfSegmentAt(inceptionRoute.length()));
        assertEquals(0,inceptionRoute.indexOfSegmentAt(5000));
        assertEquals(0,inceptionRoute.indexOfSegmentAt(-4));
        assertEquals(3,inceptionRoute.indexOfSegmentAt(inceptionRoute.length()+42));
        assertEquals(2,inceptionRoute.indexOfSegmentAt(20902));
    }
    @Test
    public void indexOfSegmentAtMixAndTwist(){
        edges1.add(edge1);
        edges1.add(edge2);
        SingleRoute route1 = new SingleRoute(edges1);

        edges2.add(edge3);
        edges2.add(edge4);
        SingleRoute route2 = new SingleRoute(edges2);

        edges3.add(edgeReverse4);
        edges3.add(edgeReverse3);
        SingleRoute route3 = new SingleRoute(edges3);

        edges4.add(edgeReverse2);
        edges4.add(edgeReverse1);
        SingleRoute route4 = new SingleRoute(edges4);

        List<Route> routes1 = new ArrayList<>();

        routes1.add(route1);
        routes1.add(route2);

        MultiRoute mixRoute = new MultiRoute(routes1);

        List<Route> routes2 = new ArrayList<>();
        routes2.add(mixRoute);
        routes2.add(route3);
        routes2.add(route4);

        MultiRoute twistRoute = new MultiRoute(routes2);

        assertEquals(0,twistRoute.indexOfSegmentAt(0));
        assertEquals(3,twistRoute.indexOfSegmentAt(30795));
        assertEquals(3,twistRoute.indexOfSegmentAt(twistRoute.length()));
        assertEquals(0,twistRoute.indexOfSegmentAt(5000));
        assertEquals(0,twistRoute.indexOfSegmentAt(-4));
        assertEquals(3,twistRoute.indexOfSegmentAt(twistRoute.length()+42));
        assertEquals(2,twistRoute.indexOfSegmentAt(20902));
    }
    @Test
    public void lengthTest(){
        edges1.add(edge1);
        edges1.add(edge2);
        SingleRoute route1 = new SingleRoute(edges1);

        edges2.add(edge3);
        edges2.add(edge4);
        SingleRoute route2 = new SingleRoute(edges2);

        edges3.add(edgeReverse4);
        edges3.add(edgeReverse3);
        SingleRoute route3 = new SingleRoute(edges3);

        edges4.add(edgeReverse2);
        edges4.add(edgeReverse1);
        SingleRoute route4 = new SingleRoute(edges4);


        List<Route> routes1 = new ArrayList<>();
        routes1.add(route1);
        routes1.add(route2);
        List<Route> routes2 = new ArrayList<>();
        routes2.add(route3);
        routes2.add(route4);

        MultiRoute multiRoute1 = new MultiRoute(routes1);
        MultiRoute multiRoute2 = new MultiRoute(routes2);

        List<Route> multiRoutes = new ArrayList<>();
        multiRoutes.add(multiRoute1);
        multiRoutes.add(multiRoute2);

        MultiRoute multiRouteSupreme = new MultiRoute(multiRoutes);

        assertEquals(route1.length()+route2.length()+route3.length()+route4.length(),multiRouteSupreme.length());
    }
    @Test
    public void edgesTest(){
        edges1.add(edge1);
        edges1.add(edge2);
        SingleRoute route1 = new SingleRoute(edges1);

        edges2.add(edge3);
        edges2.add(edge4);
        SingleRoute route2 = new SingleRoute(edges2);

        edges3.add(edgeReverse4);
        edges3.add(edgeReverse3);
        SingleRoute route3 = new SingleRoute(edges3);

        edges4.add(edgeReverse2);
        edges4.add(edgeReverse1);
        SingleRoute route4 = new SingleRoute(edges4);


        List<Route> routes1 = new ArrayList<>();
        routes1.add(route1);
        routes1.add(route2);
        List<Route> routes2 = new ArrayList<>();
        routes2.add(route3);
        routes2.add(route4);

        MultiRoute multiRoute1 = new MultiRoute(routes1);
        MultiRoute multiRoute2 = new MultiRoute(routes2);

        List<Route> multiRoutes = new ArrayList<>();
        multiRoutes.add(multiRoute1);
        multiRoutes.add(multiRoute2);

        MultiRoute multiRouteSupreme = new MultiRoute(multiRoutes);
        List<Edge> edges = List.of(edge1,edge2,edge3,edge4,edgeReverse4,edgeReverse3,edgeReverse2,edgeReverse1);
        assertEquals(edges,multiRouteSupreme.edges());
    }
    @Test
    public void pointsTest(){
        edges1.add(edge1);
        edges1.add(edge2);
        SingleRoute route1 = new SingleRoute(edges1);

        edges2.add(edge3);
        edges2.add(edge4);
        SingleRoute route2 = new SingleRoute(edges2);

        edges3.add(edgeReverse4);
        edges3.add(edgeReverse3);
        SingleRoute route3 = new SingleRoute(edges3);

        edges4.add(edgeReverse2);
        edges4.add(edgeReverse1);
        SingleRoute route4 = new SingleRoute(edges4);


        List<Route> routes1 = new ArrayList<>();
        routes1.add(route1);
        routes1.add(route2);
        List<Route> routes2 = new ArrayList<>();
        routes2.add(route3);
        routes2.add(route4);

        MultiRoute multiRoute1 = new MultiRoute(routes1);
        MultiRoute multiRoute2 = new MultiRoute(routes2);

        List<Route> multiRoutes = new ArrayList<>();
        multiRoutes.add(multiRoute1);
        multiRoutes.add(multiRoute2);

        MultiRoute multiRouteSupreme = new MultiRoute(multiRoutes);

        List<PointCh> points = List.of(point1,point2,point3,point4,point5,point4,point3,point2,point1);

        assertEquals(points,multiRouteSupreme.points());
    }
    @Test
    public void elevationAtTest(){
        edges1.add(edge1);
        edges1.add(edge2);
        SingleRoute route1 = new SingleRoute(edges1);

        edges2.add(edge3);
        edges2.add(edge4);
        SingleRoute route2 = new SingleRoute(edges2);

        edges3.add(edgeReverse4);
        edges3.add(edgeReverse3);
        SingleRoute route3 = new SingleRoute(edges3);

        edges4.add(edgeReverse2);
        edges4.add(edgeReverse1);
        SingleRoute route4 = new SingleRoute(edges4);


        List<Route> routes1 = new ArrayList<>();
        routes1.add(route1);
        routes1.add(route2);
        List<Route> routes2 = new ArrayList<>();
        routes2.add(route3);
        routes2.add(route4);

        MultiRoute multiRoute1 = new MultiRoute(routes1);
        MultiRoute multiRoute2 = new MultiRoute(routes2);

        List<Route> multiRoutes = new ArrayList<>();
        multiRoutes.add(multiRoute1);
        multiRoutes.add(multiRoute2);

        MultiRoute multiRouteSupreme = new MultiRoute(multiRoutes);

        assertEquals(route3.elevationAt(18302-route1.length()-route2.length()), multiRouteSupreme.elevationAt(18302),1e-6);
        assertEquals(route1.elevationAt(0), multiRouteSupreme.elevationAt(0));
        assertEquals(route4.elevationAt(route4.length()), multiRouteSupreme.elevationAt(multiRouteSupreme.length()));
        assertEquals(route3.elevationAt(route3.length()), multiRouteSupreme.elevationAt(route1.length()+route2.length()+route3.length()));
        assertEquals(route1.elevationAt(route1.length()), multiRouteSupreme.elevationAt(route1.length()));
    }
    @Test
    public void pointAtTest(){
        edges1.add(edge1);
        edges1.add(edge2);
        SingleRoute route1 = new SingleRoute(edges1);

        edges2.add(edge3);
        edges2.add(edge4);
        SingleRoute route2 = new SingleRoute(edges2);

        edges3.add(edgeReverse4);
        edges3.add(edgeReverse3);
        SingleRoute route3 = new SingleRoute(edges3);

        edges4.add(edgeReverse2);
        edges4.add(edgeReverse1);
        SingleRoute route4 = new SingleRoute(edges4);


        List<Route> routes1 = new ArrayList<>();
        routes1.add(route1);
        routes1.add(route2);
        List<Route> routes2 = new ArrayList<>();
        routes2.add(route3);
        routes2.add(route4);

        MultiRoute multiRoute1 = new MultiRoute(routes1);
        MultiRoute multiRoute2 = new MultiRoute(routes2);

        List<Route> multiRoutes = new ArrayList<>();
        multiRoutes.add(multiRoute1);
        multiRoutes.add(multiRoute2);

        MultiRoute multiRouteSupreme = new MultiRoute(multiRoutes);

        assertEquals(route3.pointAt(18302-route1.length()-route2.length()), multiRouteSupreme.pointAt(18302));
        assertEquals(route1.pointAt(0), multiRouteSupreme.pointAt(0));
        assertEquals(route4.pointAt(route4.length()), multiRouteSupreme.pointAt(multiRouteSupreme.length()));
    }
    @Test
    public void nodeClosestToTest(){
        edges1.add(edge1);
        edges1.add(edge2);
        SingleRoute route1 = new SingleRoute(edges1);

        edges2.add(edge3);
        edges2.add(edge4);
        SingleRoute route2 = new SingleRoute(edges2);

        edges3.add(edgeReverse4);
        edges3.add(edgeReverse3);
        SingleRoute route3 = new SingleRoute(edges3);

        edges4.add(edgeReverse2);
        edges4.add(edgeReverse1);
        SingleRoute route4 = new SingleRoute(edges4);


        List<Route> routes1 = new ArrayList<>();
        routes1.add(route1);
        routes1.add(route2);
        List<Route> routes2 = new ArrayList<>();
        routes2.add(route3);
        routes2.add(route4);

        MultiRoute multiRoute1 = new MultiRoute(routes1);
        MultiRoute multiRoute2 = new MultiRoute(routes2);

        List<Route> multiRoutes = new ArrayList<>();
        multiRoutes.add(multiRoute1);
        multiRoutes.add(multiRoute2);

        MultiRoute multiRouteSupreme = new MultiRoute(multiRoutes);

        assertEquals(18, multiRouteSupreme.nodeClosestTo(18302));
        assertEquals(8, multiRouteSupreme.nodeClosestTo(25000));
        assertEquals(0, multiRouteSupreme.nodeClosestTo(0));
        assertEquals(0, multiRouteSupreme.nodeClosestTo(multiRouteSupreme.length()));
    }
    @Test
    public void pointClosestToTest(){

        edges1.add(edge1);
        edges1.add(edge2);
        SingleRoute route1 = new SingleRoute(edges1);

        edges2.add(edge3);
        edges2.add(edge4);
        SingleRoute route2 = new SingleRoute(edges2);

        edges3.add(edgeReverse4);
        edges3.add(edgeReverse3);
        SingleRoute route3 = new SingleRoute(edges3);

        edges4.add(edgeReverse2);
        edges4.add(edgeReverse1);
        SingleRoute route4 = new SingleRoute(edges4);


        List<Route> routes1 = new ArrayList<>();
        routes1.add(route1);
        routes1.add(route2);
        List<Route> routes2 = new ArrayList<>();
        routes2.add(route3);
        routes2.add(route4);

        MultiRoute multiRoute1 = new MultiRoute(routes1);
        MultiRoute multiRoute2 = new MultiRoute(routes2);

        List<Route> multiRoutes = new ArrayList<>();
        multiRoutes.add(multiRoute1);
        multiRoutes.add(multiRoute2);

        MultiRoute multiRouteSupreme = new MultiRoute(multiRoutes);


        RoutePoint routePoint1 = new RoutePoint(point5,route1.length()+route2.length(),0);
        assertEquals(routePoint1, multiRouteSupreme.pointClosestTo(point5));

        PointCh expectedPoint = new PointCh(2609500,1199500);
        RoutePoint routePoint2 = new RoutePoint(point5,route1.length()+route2.length(),expectedPoint.distanceTo(point5));
        assertEquals(routePoint2,multiRouteSupreme.pointClosestTo(expectedPoint));

        //c'est pas toi c'est moi
        //envoyez moi des colis c'est le PointCh de ma maison (Clement sans accent parce que c'est William qui ecrit)
        PointCh exPoint = new PointCh(2534471, 1154885);
        RoutePoint routePoint3 = new RoutePoint(point1, 0,exPoint.distanceTo(point1));
        assertEquals(routePoint3, multiRouteSupreme.pointClosestTo(exPoint));



        List<Route> singleRoutes = new ArrayList<>();
        SingleRoute singleRoute1 = new SingleRoute(List.of(edge1));
        SingleRoute singleRoute2 = new SingleRoute(List.of(edge2));
        SingleRoute singleRoute3 = new SingleRoute(List.of(edge3));
        SingleRoute singleRoute4 = new SingleRoute(List.of(edge4));
        singleRoutes.add(singleRoute1);
        singleRoutes.add(singleRoute2);
        singleRoutes.add(singleRoute3);
        singleRoutes.add(singleRoute4);
        MultiRoute cRoute = new MultiRoute(singleRoutes);

        //Si celui là marche vous êtes chauds (Oui il y a des accents donc c'est l'autre loustic)
        PointCh middlePoint = new PointCh(2604723, 1193147);
        RoutePoint routePoint4 = new RoutePoint(point5, route1.length()+route2.length(),middlePoint.distanceTo(point5));
        //Si le test ne fonctionne pas pour vous, décommenter ces prints pourrait vous aider.
        //System.out.println(middlePoint.distanceTo(point1));
        //System.out.println(middlePoint.distanceTo(point2));
        //System.out.println(middlePoint.distanceTo(point3));
        //System.out.println(middlePoint.distanceTo(point4));
        //System.out.println(middlePoint.distanceTo(point5));
        assertEquals(routePoint4, cRoute.pointClosestTo(middlePoint));
    }
}

class MultiRouteTestTim {
    public static final List<PointCh> ALL_POINTS = allPoints();
    public static final List<Edge> ALL_EDGES = allEdges();
    public static final MultiRoute MULTI_ROUTE = multiRoute();

    public static List<PointCh> allPoints() {
        PointCh point0 = new PointCh(2_550_000, 1_152_300);
        PointCh point1 = new PointCh(2_550_500, 1_152_300);
        PointCh point2 = new PointCh(2_551_000, 1_152_300);
        PointCh point3 = new PointCh(2_551_500, 1_152_300);
        PointCh point4 = new PointCh(2_552_000, 1_152_300);
        PointCh point5 = new PointCh(2_552_500, 1_152_300);
        PointCh point6 = new PointCh(2_553_000, 1_152_300);
        PointCh point7 = new PointCh(2_553_500, 1_152_300);
        PointCh point8 = new PointCh(2_554_000, 1_152_300);
        PointCh point9 = new PointCh(2_554_500, 1_152_300);
        PointCh point10 = new PointCh(2_555_000, 1_152_300);
        PointCh point11 = new PointCh(2_555_500, 1_152_300);
        PointCh point12 = new PointCh(2_556_000, 1_152_300);

        List<PointCh> allPoints = new ArrayList<>(List.of(point0, point1, point2, point3, point4, point5, point6,
                point7, point8, point9, point10, point11, point12));

        return allPoints;
    }

    public static List<Edge> allEdges() {

        Edge edge0 = new Edge(0, 1, ALL_POINTS.get(0), ALL_POINTS.get(1), 500, DoubleUnaryOperator.identity());
        Edge edge1 = new Edge(1, 2, ALL_POINTS.get(1), ALL_POINTS.get(2), 500, DoubleUnaryOperator.identity());
        Edge edge2 = new Edge(2, 3, ALL_POINTS.get(2), ALL_POINTS.get(3), 500, DoubleUnaryOperator.identity());
        Edge edge3 = new Edge(3, 4, ALL_POINTS.get(3), ALL_POINTS.get(4), 500, DoubleUnaryOperator.identity());
        Edge edge4 = new Edge(4, 5, ALL_POINTS.get(4), ALL_POINTS.get(5), 500, DoubleUnaryOperator.identity());
        Edge edge5 = new Edge(5, 6, ALL_POINTS.get(5), ALL_POINTS.get(6), 500, DoubleUnaryOperator.identity());
        Edge edge6 = new Edge(6, 7, ALL_POINTS.get(6), ALL_POINTS.get(7), 500, DoubleUnaryOperator.identity());
        Edge edge7 = new Edge(7, 8, ALL_POINTS.get(7), ALL_POINTS.get(8), 500, DoubleUnaryOperator.identity());
        Edge edge8 = new Edge(8, 9, ALL_POINTS.get(8), ALL_POINTS.get(9), 500, DoubleUnaryOperator.identity());
        Edge edge9 = new Edge(9, 10, ALL_POINTS.get(9), ALL_POINTS.get(10), 500, DoubleUnaryOperator.identity());
        Edge edge10 = new Edge(10, 11, ALL_POINTS.get(10), ALL_POINTS.get(11), 500, DoubleUnaryOperator.identity());
        Edge edge11 = new Edge(11, 12, ALL_POINTS.get(11), ALL_POINTS.get(12), 500, DoubleUnaryOperator.identity());

        List<Edge> edges0 = new ArrayList<>(List.of(edge0, edge1));
        List<Edge> edges1 = new ArrayList<>(List.of(edge2, edge3));
        List<Edge> edges2 = new ArrayList<>(List.of(edge4, edge5));
        List<Edge> edges3 = new ArrayList<>(List.of(edge6, edge7));
        List<Edge> edges4 = new ArrayList<>(List.of(edge8, edge9));
        List<Edge> edges5 = new ArrayList<>(List.of(edge10, edge11));

        List<Edge> allEdges = new ArrayList<>();
        Stream.of(edges0, edges1, edges2, edges3, edges4, edges5).forEach(allEdges::addAll);

        return allEdges;
    }

    public static MultiRoute multiRoute() {

        Route singleRoute0 = new SingleRoute(ALL_EDGES.subList(0,2));
        Route singleRoute1 = new SingleRoute(ALL_EDGES.subList(2,4));
        Route singleRoute2 = new SingleRoute(ALL_EDGES.subList(4,6));
        Route singleRoute3 = new SingleRoute(ALL_EDGES.subList(6,8));
        Route singleRoute4 = new SingleRoute(ALL_EDGES.subList(8,10));
        Route singleRoute5 = new SingleRoute(ALL_EDGES.subList(10,12));

        List<Route> segment0 = new ArrayList<>(List.of(singleRoute0, singleRoute1, singleRoute2));
        List<Route> segment1 = new ArrayList<>(List.of(singleRoute3, singleRoute4, singleRoute5));

        MultiRoute multiRoute0 = new MultiRoute(segment0);
        MultiRoute multiRoute1 = new MultiRoute(segment1);

        List<Route> segments = List.of(multiRoute0, multiRoute1);

        //Stream.of(segment0, segment1).forEach(segments::addAll);

        return new MultiRoute(segments);
    }

    @Test
    void indexOfSegmentAtWorksOnKnownValues() {
        assertEquals(0, MULTI_ROUTE.indexOfSegmentAt(-400));
        assertEquals(0, MULTI_ROUTE.indexOfSegmentAt(100));
        assertEquals(3, MULTI_ROUTE.indexOfSegmentAt(4000));
        assertEquals(4, MULTI_ROUTE.indexOfSegmentAt(4100));
        assertEquals(5, MULTI_ROUTE.indexOfSegmentAt(5500));
        assertEquals(5, MULTI_ROUTE.indexOfSegmentAt(6000));
        assertEquals(5, MULTI_ROUTE.indexOfSegmentAt(12000));
    }

    @Test
    void edgesWorksForAllEdges() {
        assertEquals(ALL_EDGES, MULTI_ROUTE.edges());
    }

    @Test
    void pointsWorksForAllPoints() {
        assertEquals(ALL_POINTS, MULTI_ROUTE.points());
    }

    @Test
    void pointAtWorksOnKnownValues() {
        PointCh pointOnRoute5 = new PointCh(2_555_500, 1_152_300);
        PointCh pointOnRoute0 = new PointCh(2_550_000, 1_152_300);
        PointCh pointOnRoute5end = new PointCh(2_556_000, 1_152_300);
        assertEquals(pointOnRoute5, MULTI_ROUTE.pointAt(5500));
        assertEquals(pointOnRoute5end, MULTI_ROUTE.pointAt(6000));
        assertEquals(pointOnRoute5end, MULTI_ROUTE.pointAt(7000));
        assertEquals(pointOnRoute0, MULTI_ROUTE.pointAt(0));
        assertEquals(pointOnRoute0, MULTI_ROUTE.pointAt(-2));
    }

    @Test
    void elevationAtWorksOnKnownValues() {
        assertEquals(0, MULTI_ROUTE.elevationAt(5500));
        assertEquals(500, MULTI_ROUTE.elevationAt(6000));
        assertEquals(0, MULTI_ROUTE.elevationAt(0));
    }

    @Test
    void nodeClosestToWorksOnKnownValues() {
    }

    @Test
    void pointClosestTo() {

        PointCh point6 = new PointCh(2_553_000 + 10, 1_152_300);
        PointCh point6Reference = new PointCh(2_553_000 + 10, 1_152_300 + 50);
        RoutePoint routePoint6 = new RoutePoint(point6 , 3000 + 10, 50);

        PointCh point11 = new PointCh(2_555_500, 1_152_300);
        PointCh point11Reference = new PointCh(2_555_500, 1_152_300 + 50);
        RoutePoint routePoint11 = new RoutePoint(point11, 5500, 50);

        assertEquals(routePoint6, MULTI_ROUTE.pointClosestTo(point6Reference));
        assertEquals(routePoint11, MULTI_ROUTE.pointClosestTo(point11Reference));
    }
}

class MultiRouteTestMomo {

    PointCh point1 = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
    PointCh point2 = new PointCh(SwissBounds.MIN_E + 1000, SwissBounds.MIN_N);
    PointCh point3 = new PointCh(SwissBounds.MIN_E + 1000, SwissBounds.MIN_N + 1000);
    PointCh point4 = new PointCh(SwissBounds.MIN_E + 2000, SwissBounds.MIN_N + 1000);
    PointCh point5 = new PointCh(SwissBounds.MIN_E + 2000, SwissBounds.MIN_N + 2000);
    PointCh point6 = new PointCh(SwissBounds.MIN_E + 3000, SwissBounds.MIN_N + 2000);
    PointCh point7 = new PointCh(SwissBounds.MIN_E + 3000, SwissBounds.MIN_N + 3000);
    PointCh point8 = new PointCh(SwissBounds.MIN_E + 4000, SwissBounds.MIN_N + 3000);
    PointCh point9 = new PointCh(SwissBounds.MIN_E + 4000, SwissBounds.MIN_N + 4000);
    PointCh point10 = new PointCh(SwissBounds.MIN_E + 5000, SwissBounds.MIN_N + 4000);
    PointCh point11 = new PointCh(SwissBounds.MIN_E + 5000, SwissBounds.MIN_N + 5000);
    PointCh point12 = new PointCh(SwissBounds.MIN_E + 6000, SwissBounds.MIN_N + 5000);
    PointCh point13 = new PointCh(SwissBounds.MIN_E + 6000, SwissBounds.MIN_N + 6000);


    Edge edge1 = new Edge(0, 1, point1, point2, point1.distanceTo(point2), DoubleUnaryOperator.identity());
    Edge edge2 = new Edge(1, 2, point2, point3, point2.distanceTo(point3), DoubleUnaryOperator.identity());
    Edge edge3 = new Edge(2, 3, point3, point4, point3.distanceTo(point4), DoubleUnaryOperator.identity());
    Edge edge4 = new Edge(3, 4, point4, point5, point4.distanceTo(point5), DoubleUnaryOperator.identity());
    Edge edge5 = new Edge(4, 5, point5, point6, point5.distanceTo(point6), DoubleUnaryOperator.identity());
    Edge edge6 = new Edge(5, 6, point6, point7, point6.distanceTo(point7), DoubleUnaryOperator.identity());
    Edge edge7 = new Edge(6, 7, point7, point8, point7.distanceTo(point8), DoubleUnaryOperator.identity());
    Edge edge8 = new Edge(7, 8, point8, point9, point8.distanceTo(point9), DoubleUnaryOperator.identity());
    Edge edge9 = new Edge(8, 9, point9, point10, point9.distanceTo(point10), DoubleUnaryOperator.identity());
    Edge edge10 = new Edge(9, 10, point10, point11, point10.distanceTo(point11), DoubleUnaryOperator.identity());
    Edge edge11 = new Edge(10, 11, point11, point12, point11.distanceTo(point12), DoubleUnaryOperator.identity());
    Edge edge12 = new Edge(11, 12, point12, point13, point12.distanceTo(point13), DoubleUnaryOperator.identity());

    private List<Edge> edgesSet1(){
        List<Edge> edgesSet1Array = new ArrayList<>();
        edgesSet1Array.add(edge1);
        edgesSet1Array.add(edge2);
        return edgesSet1Array;
    }

    private List<Edge> edgesSet2(){
        List<Edge> edgesSet2Array = new ArrayList<>();
        edgesSet2Array.add(edge3);
        edgesSet2Array.add(edge4);
        edgesSet2Array.add(edge5);
        return edgesSet2Array;
    }

    private List<Edge> edgesSet3(){
        List<Edge> edgesSet3Array = new ArrayList<>();
        edgesSet3Array.add(edge6);
        return edgesSet3Array;
    }

    private List<Edge> edgesSet4(){
        List<Edge> edgesSet4Array = new ArrayList<>();
        edgesSet4Array.add(edge7);
        edgesSet4Array.add(edge8);
        edgesSet4Array.add(edge9);
        return edgesSet4Array;
    }

    private List<Edge> edgesSet5(){
        List<Edge> edgesSet5Array = new ArrayList<>();
        edgesSet5Array.add(edge10);
        return edgesSet5Array;
    }

    private List<Edge> edgesSet6(){
        List<Edge> edgesSet6Array = new ArrayList<>();
        edgesSet6Array.add(edge11);
        edgesSet6Array.add(edge12);
        return edgesSet6Array;
    }

    private List<Route> segmentsV2Array(Route route1, Route route2){
        List<Route> segments = new ArrayList<>();
        segments.add(route1);
        segments.add(route2);
        return segments;
    }

    private List<Route> segmentsV3Array(Route route1, Route route2, Route route3){
        List<Route> segments = new ArrayList<>();
        segments.add(route1);
        segments.add(route2);
        segments.add(route3);
        return segments;
    }

    private SingleRoute singleRoute(List<Edge> edges){
        return new SingleRoute(edges);
    }

    private MultiRoute multiRoute(List<Route> segments){
        return new MultiRoute(segments);
    }

    SingleRoute single1 = singleRoute(edgesSet1());
    SingleRoute single2 = singleRoute(edgesSet2());
    SingleRoute single3 = singleRoute(edgesSet3());
    SingleRoute single4 = singleRoute(edgesSet4());
    SingleRoute single5 = singleRoute(edgesSet5());
    SingleRoute single6 = singleRoute(edgesSet6());
    MultiRoute multi1 = multiRoute(segmentsV2Array(single1, single2));
    MultiRoute multi2 = multiRoute(segmentsV3Array(single4, single5, single6));
    MultiRoute multiGlobal = multiRoute(segmentsV3Array(multi1, single3, multi2));

    @Test
    void checkExceptionEmptySegmentsArray(){
        List<Route> segments = new ArrayList<>();
        assertThrows(IllegalArgumentException.class, () ->{new MultiRoute(segments);});
    }

    @Test
    void checkAllPoints(){
        List<PointCh> expectedPointsArrayMulti1 =
                List.of(point1, point2, point3, point4, point5, point6);
        List<PointCh> expectedPointsArrayMulti2 =
                List.of(point7, point8, point9, point10, point11, point12, point13);
        List<PointCh> expectedPointsArrayGlobalMulti =
                List.of(point1, point2, point3, point4, point5, point6, point7, point8, point9, point10, point11, point12, point13);


        assertEquals(expectedPointsArrayMulti1, multi1.points());
        assertEquals(expectedPointsArrayMulti2, multi2.points());
        assertEquals(expectedPointsArrayGlobalMulti, multiGlobal.points());
    }

    @Test
    void checkAllEdges(){
        List<Edge> edgeList1 =
                List.of(edge1, edge2, edge3, edge4, edge5);
        List<Edge> edgeList2 =
                List.of(edge7, edge8, edge9, edge10, edge11, edge12);
        List<Edge> edgeListGlobal =
                List.of(edge1, edge2, edge3, edge4, edge5, edge6, edge7, edge8, edge9, edge10, edge11, edge12);

        assertEquals(edgeList1, multi1.edges());
        assertEquals(edgeList2, multi2.edges());
        assertEquals(edgeListGlobal, multiGlobal.edges());
    }

    @Test
    void checkLength(){
        double totalLengthMulti1 = point1.distanceTo(point2) + point2.distanceTo(point3)
                + point3.distanceTo(point4) + point4.distanceTo(point5) + point5.distanceTo(point6);
        double totalLengthMulti2 = point7.distanceTo(point8) + point8.distanceTo(point9)
                + point9.distanceTo(point10) + point10.distanceTo(point11) + point11.distanceTo(point12) + point12.distanceTo(point13);
        double totalLengthMultiGlobal = totalLengthMulti1 + totalLengthMulti2 + point6.distanceTo(point7);

        assertEquals(totalLengthMulti1, multi1.length());
        assertEquals(totalLengthMulti2, multi2.length());
        assertEquals(totalLengthMultiGlobal, multiGlobal.length());
    }

    @Test
    void checkIndexOfSegmentAt(){
        //Multi1
        assertEquals(0, multi1.indexOfSegmentAt(0));
        assertEquals(0, multi1.indexOfSegmentAt(1300.2));
        assertEquals(1, multi1.indexOfSegmentAt(3400.5));
        assertEquals(1, multi1.indexOfSegmentAt(5000));
        assertTrue(multi1.indexOfSegmentAt(2000) == 0 || multi1.indexOfSegmentAt(2000) == 1);
        //Multi2
        assertEquals(0, multi2.indexOfSegmentAt(2645.876));
        assertEquals(1, multi2.indexOfSegmentAt(3280.476));
        assertEquals(2, multi2.indexOfSegmentAt(4768.98));
        assertEquals(2, multi2.indexOfSegmentAt(6000));
        assertTrue(multi2.indexOfSegmentAt(4000) == 1 || multi2.indexOfSegmentAt(4000) == 2);
        //MultiGlobal
        assertEquals(0, multiGlobal.indexOfSegmentAt(1999.999));
        assertEquals(1, multiGlobal.indexOfSegmentAt(2000.00001));
        assertEquals(2, multiGlobal.indexOfSegmentAt(5789.765));
        assertEquals(3, multiGlobal.indexOfSegmentAt(6666.666));
        assertEquals(4, multiGlobal.indexOfSegmentAt(9999.999));
        assertEquals(5, multiGlobal.indexOfSegmentAt(12000));
    }

    @Test
    void checkPointAt(){
        //Multi1
        assertEquals(point1, multi1.pointAt(0));
        assertEquals(new PointCh(2486000, 1075000), multi1.pointAt(1000));
        assertEquals(new PointCh(2486598.6, 1076000), multi1.pointAt(2598.6));
        assertEquals(point6, multi1.pointAt(5000));
        //Multi2
        assertEquals(point7, multi2.pointAt(0));
        assertEquals(new PointCh(2490000, 1079867.578), multi2.pointAt(3867.578));
        assertEquals(new PointCh(2491000, 1080999.999), multi2.pointAt(5999.999));
        //MultiGlobal
        assertEquals(new PointCh(2490800, 1080000), multiGlobal.pointAt(10800));
        assertEquals(new PointCh(2488000, 1077678.901), multiGlobal.pointAt(5678.901));
        assertEquals(point9, multiGlobal.pointAt(8000));
        assertEquals(new PointCh(2486000, 1075476.4857483), multiGlobal.pointAt(1476.4857483));
    }

    @Test
    void checkElevationAt(){
        //Multi1
        assertEquals(0, multi1.elevationAt(0));
        assertEquals(432, multi1.elevationAt(1432));
        assertEquals(999.9989999999998, multi1.elevationAt(4999.999));
        //Multi2
        assertEquals(0.01, multi2.elevationAt(0.01));
        assertEquals(546.7800000000002, multi2.elevationAt(2546.78));
        assertEquals(1000, multi2.elevationAt(6000));
        //MultiGlobal
        assertEquals(467.78000000000065, multiGlobal.elevationAt(9467.78));
    }

    @Test
    void checkNodeClosestTo(){
        //Multi1
        assertEquals(0, multi1.nodeClosestTo(450));
        assertEquals(2, multi1.nodeClosestTo(1999.999));
        //Multi2
        assertEquals(12, multi2.nodeClosestTo(5500.0001));
        assertEquals(7, multi2.nodeClosestTo(1000));
        //MultiGlobal
        assertEquals(0, multiGlobal.nodeClosestTo(444.444));
        assertEquals(10, multiGlobal.nodeClosestTo(10499.999));
        assertEquals(5, multiGlobal.nodeClosestTo(5000));
    }

    @Test
    void checkPointClosestTo(){
        //Multi1
        assertEquals(new RoutePoint(point1, 0, 542), multi1.pointClosestTo(new PointCh(2485000, 1075542)));
        assertEquals(new RoutePoint(new PointCh(2486000, 1075000), 1000, 1000),
                multi1.pointClosestTo(new PointCh(2487000, 1075000)));
        //Multi2
        assertEquals(new RoutePoint(new PointCh(2489000, 1078000), 1000, 1118.033988749895),
                multi2.pointClosestTo(new PointCh(2489500, 1077000)));
        assertEquals(new RoutePoint(point7, 0, point1.distanceTo(point7)), multi2.pointClosestTo(point1));
        //MultiGlobal
        assertEquals(new RoutePoint(point13, 12000, 9055.938659244552),
                multiGlobal.pointClosestTo(new PointCh(2500000, 1082005)));
    }
}

class MultiRouteTestMamoun {

    public static <E> void compareList(List<E> list1, List<E> list2) {
        assertTrue(list1.size() == list2.size());
        for (int i = 0; i < list1.size(); i++) {
            assertEquals(list1.get(i), list2.get(i));
        }
    }

    @Test
    public void indexOfSegmentAtWorksOnSimpleValues() {
        List<Edge> edges = new ArrayList<>();
        List<Edge> edges1 = new ArrayList<>();

        PointCh pt1 = new PointCh(2600000, 1200000);
        PointCh pt2 = new PointCh(2600100, 1200000);
        PointCh pt3 = new PointCh(2600000, 1200100);
        PointCh pt4 = new PointCh(2601234, 1201234);
        PointCh pt5 = new PointCh(2485001, 1075001);
        PointCh pt6 = new PointCh(2833999, 1295999);


        edges.add(new Edge(1, 1, pt1, pt1, 10, Functions.constant(1)));
        edges.add(new Edge(1, 1, pt2, pt2, 10, Functions.constant(1)));
        edges.add(new Edge(1, 1, pt3, pt3, 10, Functions.constant(1)));
        edges.add(new Edge(1, 1, pt4, pt4, 10, Functions.constant(1)));
        edges.add(new Edge(1, 1, pt5, pt5, 10, Functions.constant(1)));
        edges.add(new Edge(1, 1, pt6, pt6, 10, Functions.constant(1)));

        edges1.add(new Edge(1, 1, pt1, pt2, 10, Functions.constant(1)));
        edges1.add(new Edge(1, 1, pt2, pt5, 10, Functions.constant(1)));
        edges1.add(new Edge(1, 1, pt5, pt4, 10, Functions.constant(1)));
        edges1.add(new Edge(1, 1, pt4, pt6, 10, Functions.constant(1)));
        edges1.add(new Edge(1, 1, pt6, pt3, 10, Functions.constant(1)));
        edges1.add(new Edge(1, 1, pt3, pt3, 10, Functions.constant(1)));

        SingleRoute route = new SingleRoute(edges);
        SingleRoute route1 = new SingleRoute(edges1);
        SingleRoute route2 = new SingleRoute(edges);
        SingleRoute route3 = new SingleRoute(edges1);
        List<Route> listOfRoutes = new ArrayList<>();
        listOfRoutes.add(route);
        listOfRoutes.add(route1);
        MultiRoute multiRoute1 = new MultiRoute(listOfRoutes);
        listOfRoutes.add(route2);
        MultiRoute multiRoute2 = new MultiRoute(listOfRoutes);
        List<Route> listOfRoutesFinal = new ArrayList<>();
        listOfRoutesFinal.add(multiRoute1);
        listOfRoutesFinal.add(route3);
        listOfRoutesFinal.add(multiRoute2);

        MultiRoute multiRouteFinal = new MultiRoute(listOfRoutesFinal);

        assertEquals(0, multiRouteFinal.indexOfSegmentAt(0));
        assertEquals(0, multiRouteFinal.indexOfSegmentAt(-1));
        assertEquals(0, multiRouteFinal.indexOfSegmentAt(-500));
        assertEquals(1, multiRouteFinal.indexOfSegmentAt(90));
        assertEquals(2, multiRouteFinal.indexOfSegmentAt(120));
        assertEquals(3, multiRouteFinal.indexOfSegmentAt(180));
        assertEquals(4, multiRouteFinal.indexOfSegmentAt(240));
        assertEquals(4, multiRouteFinal.indexOfSegmentAt(299));
        assertEquals(5, multiRouteFinal.indexOfSegmentAt(300));
        assertEquals(5, multiRouteFinal.indexOfSegmentAt(376));


    }

    @Test
    public void indexOfSegmentAtWorksOnDifferentLengths() {
        List<Edge> edges = new ArrayList<>();
        List<Edge> edges1 = new ArrayList<>();

        PointCh pt1 = new PointCh(2600000, 1200000);
        PointCh pt2 = new PointCh(2600100, 1200000);
        PointCh pt3 = new PointCh(2600000, 1200100);
        PointCh pt4 = new PointCh(2601234, 1201234);
        PointCh pt5 = new PointCh(2485001, 1075001);
        PointCh pt6 = new PointCh(2833999, 1295999);


        edges.add(new Edge(1, 1, pt1, pt1, 8, Functions.constant(1)));
        edges.add(new Edge(1, 1, pt2, pt2, 12, Functions.constant(1)));
        edges.add(new Edge(1, 1, pt3, pt3, 24, Functions.constant(1)));
        edges.add(new Edge(1, 1, pt4, pt4, 7, Functions.constant(1)));
        edges.add(new Edge(1, 1, pt5, pt5, 14, Functions.constant(1)));
        edges.add(new Edge(1, 1, pt6, pt6, 5, Functions.constant(1))); // 70

        edges1.add(new Edge(1, 1, pt1, pt2, 1, Functions.constant(1)));
        edges1.add(new Edge(1, 1, pt2, pt5, 20, Functions.constant(1)));
        edges1.add(new Edge(1, 1, pt5, pt4, 11, Functions.constant(1)));
        edges1.add(new Edge(1, 1, pt4, pt6, 82, Functions.constant(1)));
        edges1.add(new Edge(1, 1, pt6, pt3, 10, Functions.constant(1)));
        edges1.add(new Edge(1, 1, pt3, pt3, 6, Functions.constant(1))); // 130

        SingleRoute route = new SingleRoute(edges);
        SingleRoute route1 = new SingleRoute(edges1);
        SingleRoute route2 = new SingleRoute(edges);
        SingleRoute route3 = new SingleRoute(edges1);
        List<Route> listOfRoutes = new ArrayList<>();
        listOfRoutes.add(route);
        listOfRoutes.add(route1);
        MultiRoute multiRoute1 = new MultiRoute(listOfRoutes);
        listOfRoutes.add(route2);
        MultiRoute multiRoute2 = new MultiRoute(listOfRoutes);
        List<Route> listOfRoutesFinal = new ArrayList<>();
        listOfRoutesFinal.add(multiRoute1);
        listOfRoutesFinal.add(route3);
        listOfRoutesFinal.add(multiRoute2);

        MultiRoute multiRouteFinal = new MultiRoute(listOfRoutesFinal);

        assertEquals(0, multiRouteFinal.indexOfSegmentAt(0));
        assertEquals(0, multiRouteFinal.indexOfSegmentAt(-10));
        assertEquals(0, multiRouteFinal.indexOfSegmentAt(69));
        assertEquals(1, multiRouteFinal.indexOfSegmentAt(199));
        assertEquals(2, multiRouteFinal.indexOfSegmentAt(329));
        assertEquals(3, multiRouteFinal.indexOfSegmentAt(330));
        assertEquals(4, multiRouteFinal.indexOfSegmentAt(400));
        assertEquals(4, multiRouteFinal.indexOfSegmentAt(529));
        assertEquals(5, multiRouteFinal.indexOfSegmentAt(530));
        assertEquals(5, multiRouteFinal.indexOfSegmentAt(700));


    }

    @Test
    public void indexOfSegmentAtWorksOnMultiRoutesInMultiRoutes() {
        List<Edge> edges = new ArrayList<>();
        List<Edge> edges1 = new ArrayList<>();

        PointCh pt1 = new PointCh(2600000, 1200000);
        PointCh pt2 = new PointCh(2600100, 1200000);
        PointCh pt3 = new PointCh(2600000, 1200100);
        PointCh pt4 = new PointCh(2601234, 1201234);
        PointCh pt5 = new PointCh(2485001, 1075001);
        PointCh pt6 = new PointCh(2833999, 1295999);


        edges.add(new Edge(1, 1, pt1, pt2, 10, Functions.constant(1)));
        edges.add(new Edge(1, 1, pt2, pt3, 10, Functions.constant(1)));
        edges.add(new Edge(1, 1, pt3, pt4, 10, Functions.constant(1)));
        edges.add(new Edge(1, 1, pt4, pt5, 10, Functions.constant(1)));
        edges.add(new Edge(1, 1, pt5, pt6, 10, Functions.constant(1)));
        edges.add(new Edge(1, 1, pt6, pt6, 10, Functions.constant(1))); // 60

        edges1.add(new Edge(1, 1, pt1, pt2, 10, Functions.constant(1)));
        edges1.add(new Edge(1, 1, pt2, pt5, 10, Functions.constant(1)));
        edges1.add(new Edge(1, 1, pt5, pt4, 10, Functions.constant(1)));
        edges1.add(new Edge(1, 1, pt4, pt6, 10, Functions.constant(1)));
        edges1.add(new Edge(1, 1, pt6, pt3, 10, Functions.constant(1)));
        edges1.add(new Edge(1, 1, pt3, pt3, 10, Functions.constant(1))); // 60

        SingleRoute route = new SingleRoute(edges);
        SingleRoute route1 = new SingleRoute(edges1);
        List<Route> listOfRoutes = new ArrayList<>();
        List<Route> listOfRoutesFinal = new ArrayList<>();

        listOfRoutes.add(route);
        listOfRoutes.add(route1);
        MultiRoute multiRoute1 = new MultiRoute(listOfRoutes);

        listOfRoutes.add(multiRoute1);
        MultiRoute multiRoute2 = new MultiRoute(listOfRoutes);

        List<Route> multiRouteInMultiRoute = new ArrayList<>();
        multiRouteInMultiRoute.add(multiRoute2);
        multiRouteInMultiRoute.add(multiRoute1);

        MultiRoute multiRoute3 = new MultiRoute(multiRouteInMultiRoute);
        listOfRoutesFinal.add(multiRoute1);
        listOfRoutesFinal.add(multiRoute2);
        listOfRoutesFinal.add(multiRoute3);

        MultiRoute multiRouteFinal = new MultiRoute(listOfRoutesFinal);

        assertEquals(0, multiRouteFinal.indexOfSegmentAt(0));
        assertEquals(0, multiRouteFinal.indexOfSegmentAt(-1));
        assertEquals(0, multiRouteFinal.indexOfSegmentAt(50));
        assertEquals(1, multiRouteFinal.indexOfSegmentAt(60));
        assertEquals(2, multiRouteFinal.indexOfSegmentAt(120));
        assertEquals(3, multiRouteFinal.indexOfSegmentAt(180));
        assertEquals(4, multiRouteFinal.indexOfSegmentAt(265));
        assertEquals(4, multiRouteFinal.indexOfSegmentAt(299));
        assertEquals(5, multiRouteFinal.indexOfSegmentAt(300));
        assertEquals(6, multiRouteFinal.indexOfSegmentAt(376));
        assertEquals(7, multiRouteFinal.indexOfSegmentAt(420));
        assertEquals(10, multiRouteFinal.indexOfSegmentAt(600));
        assertEquals(11, multiRouteFinal.indexOfSegmentAt(720));
        assertEquals(11, multiRouteFinal.indexOfSegmentAt(1000));


    }

    @Test
    public void lengthWorksOnMultiRoutesInMultiRoutes() {
        List<Edge> edges = new ArrayList<>();
        List<Edge> edges1 = new ArrayList<>();

        PointCh pt1 = new PointCh(2600000, 1200000);
        PointCh pt2 = new PointCh(2600100, 1200000);
        PointCh pt3 = new PointCh(2600000, 1200100);
        PointCh pt4 = new PointCh(2601234, 1201234);
        PointCh pt5 = new PointCh(2485001, 1075001);
        PointCh pt6 = new PointCh(2833999, 1295999);


        edges.add(new Edge(1, 1, pt1, pt2, 10, Functions.constant(1)));
        edges.add(new Edge(1, 1, pt2, pt3, 10, Functions.constant(1)));
        edges.add(new Edge(1, 1, pt3, pt4, 10, Functions.constant(1)));
        edges.add(new Edge(1, 1, pt4, pt5, 10, Functions.constant(1)));
        edges.add(new Edge(1, 1, pt5, pt6, 10, Functions.constant(1)));
        edges.add(new Edge(1, 1, pt6, pt6, 10, Functions.constant(1))); // 60

        edges1.add(new Edge(1, 1, pt1, pt2, 10, Functions.constant(1)));
        edges1.add(new Edge(1, 1, pt2, pt5, 10, Functions.constant(1)));
        edges1.add(new Edge(1, 1, pt5, pt4, 10, Functions.constant(1)));
        edges1.add(new Edge(1, 1, pt4, pt6, 10, Functions.constant(1)));
        edges1.add(new Edge(1, 1, pt6, pt3, 10, Functions.constant(1)));
        edges1.add(new Edge(1, 1, pt3, pt3, 10, Functions.constant(1))); // 60

        SingleRoute route = new SingleRoute(edges);
        SingleRoute route1 = new SingleRoute(edges1);
        List<Route> listOfRoutes = new ArrayList<>();
        List<Route> listOfRoutesFinal = new ArrayList<>();

        listOfRoutes.add(route);
        listOfRoutes.add(route1);
        MultiRoute multiRoute1 = new MultiRoute(listOfRoutes);

        listOfRoutes.add(multiRoute1);
        MultiRoute multiRoute2 = new MultiRoute(listOfRoutes);

        List<Route> multiRouteInMultiRoute = new ArrayList<>();
        multiRouteInMultiRoute.add(multiRoute2);
        multiRouteInMultiRoute.add(multiRoute1);

        MultiRoute multiRoute3 = new MultiRoute(multiRouteInMultiRoute);
        listOfRoutesFinal.add(multiRoute1);
        listOfRoutesFinal.add(multiRoute2);
        listOfRoutesFinal.add(multiRoute3);

        MultiRoute multiRouteFinal = new MultiRoute(listOfRoutesFinal);

        assertEquals(720, multiRouteFinal.length());


    }

    @Test
    public void edgesWorksOnMultiRoutes() {
        List<Edge> edges = new ArrayList<>();
        List<Edge> edges1 = new ArrayList<>();

        PointCh pt1 = new PointCh(2600000, 1200000);
        PointCh pt2 = new PointCh(2600100, 1200000);
        PointCh pt3 = new PointCh(2600000, 1200100);
        PointCh pt4 = new PointCh(2601234, 1201234);
        PointCh pt5 = new PointCh(2485001, 1075001);
        PointCh pt6 = new PointCh(2833999, 1295999);


        edges.add(new Edge(1, 1, pt1, pt2, 10, Functions.constant(1)));
        edges.add(new Edge(1, 1, pt2, pt3, 10, Functions.constant(1)));
        edges.add(new Edge(1, 1, pt3, pt4, 10, Functions.constant(1)));
        edges.add(new Edge(1, 1, pt4, pt5, 10, Functions.constant(1)));
        edges.add(new Edge(1, 1, pt5, pt6, 10, Functions.constant(1)));
        edges.add(new Edge(1, 1, pt6, pt6, 10, Functions.constant(1))); // 60

        edges1.add(new Edge(1, 1, pt1, pt2, 10, Functions.constant(1)));
        edges1.add(new Edge(1, 1, pt2, pt5, 10, Functions.constant(1)));
        edges1.add(new Edge(1, 1, pt5, pt4, 10, Functions.constant(1)));
        edges1.add(new Edge(1, 1, pt4, pt6, 10, Functions.constant(1)));
        edges1.add(new Edge(1, 1, pt6, pt3, 10, Functions.constant(1)));
        edges1.add(new Edge(1, 1, pt3, pt3, 10, Functions.constant(1))); // 60

        SingleRoute route = new SingleRoute(edges);
        SingleRoute route1 = new SingleRoute(edges1);
        List<Route> listOfRoutes = new ArrayList<>();
        List<Route> listOfRoutesFinal = new ArrayList<>();

        listOfRoutes.add(route);
        listOfRoutes.add(route1);
        MultiRoute multiRoute1 = new MultiRoute(listOfRoutes);

        listOfRoutes.add(multiRoute1);
        MultiRoute multiRoute2 = new MultiRoute(listOfRoutes);

        List<Route> multiRouteInMultiRoute = new ArrayList<>();
        multiRouteInMultiRoute.add(multiRoute2);
        multiRouteInMultiRoute.add(multiRoute1);

        MultiRoute multiRoute3 = new MultiRoute(multiRouteInMultiRoute);
        listOfRoutesFinal.add(multiRoute1);
        listOfRoutesFinal.add(multiRoute2);
        listOfRoutesFinal.add(multiRoute3);

        MultiRoute multiRouteFinal = new MultiRoute(listOfRoutesFinal);

        List<Edge> finalEdges = new ArrayList<>();
        for (Route element : listOfRoutesFinal) {
            finalEdges.addAll(element.edges());
        }
        compareList(finalEdges, multiRouteFinal.edges());
        assertEquals(finalEdges, multiRouteFinal.edges());

    }

    @Test
    public void pointsWorksWithMultiRoutes() {
        List<Edge> edges = new ArrayList<>();
        List<Edge> edges1 = new ArrayList<>();

        PointCh pt1 = new PointCh(2600000, 1200000);
        PointCh pt2 = new PointCh(2600100, 1200000);
        PointCh pt3 = new PointCh(2600000, 1200100);
        PointCh pt4 = new PointCh(2601234, 1201234);
        PointCh pt5 = new PointCh(2485001, 1075001);
        PointCh pt6 = new PointCh(2833999, 1295999);


        edges.add(new Edge(1, 1, pt1, pt2, 10, Functions.constant(1)));
        edges.add(new Edge(1, 1, pt2, pt3, 10, Functions.constant(1)));
        edges.add(new Edge(1, 1, pt3, pt4, 10, Functions.constant(1)));
        edges.add(new Edge(1, 1, pt4, pt5, 10, Functions.constant(1)));
        edges.add(new Edge(1, 1, pt5, pt6, 10, Functions.constant(1)));
        edges.add(new Edge(1, 1, pt6, pt6, 10, Functions.constant(1))); // 60

        edges1.add(new Edge(1, 1, pt1, pt2, 10, Functions.constant(1)));
        edges1.add(new Edge(1, 1, pt2, pt5, 10, Functions.constant(1)));
        edges1.add(new Edge(1, 1, pt5, pt4, 10, Functions.constant(1)));
        edges1.add(new Edge(1, 1, pt4, pt6, 10, Functions.constant(1)));
        edges1.add(new Edge(1, 1, pt6, pt3, 10, Functions.constant(1)));
        edges1.add(new Edge(1, 1, pt3, pt3, 10, Functions.constant(1))); // 60

        SingleRoute route = new SingleRoute(edges);
        SingleRoute route1 = new SingleRoute(edges1);
        List<Route> listOfRoutes = new ArrayList<>();
        List<Route> listOfRoutesFinal = new ArrayList<>();

        listOfRoutes.add(route);
        listOfRoutes.add(route1);
        MultiRoute multiRoute1 = new MultiRoute(listOfRoutes);

        listOfRoutes.add(multiRoute1);
        MultiRoute multiRoute2 = new MultiRoute(listOfRoutes);

        List<Route> multiRouteInMultiRoute = new ArrayList<>();
        multiRouteInMultiRoute.add(multiRoute2);
        multiRouteInMultiRoute.add(multiRoute1);

        MultiRoute multiRoute3 = new MultiRoute(multiRouteInMultiRoute);
        listOfRoutesFinal.add(multiRoute1);
        listOfRoutesFinal.add(multiRoute2);
        listOfRoutesFinal.add(multiRoute3);

        MultiRoute multiRouteFinal = new MultiRoute(listOfRoutesFinal);

        List<PointCh> finalPoints = new ArrayList<>();
        for (Route element : listOfRoutesFinal) {
            finalPoints.addAll(element.points());
            finalPoints.remove(finalPoints.size() - 1);
        }
        finalPoints.add(multiRoute3.pointAt(Double.POSITIVE_INFINITY));
        compareList(finalPoints, multiRouteFinal.points());
        assertEquals(finalPoints, multiRouteFinal.points());

    }


    @Test
    public void pointAtWorksOnMultiRoutesInMultiRoutes() {
        List<Edge> edges = new ArrayList<>();
        List<Edge> edges1 = new ArrayList<>();

        PointCh pt1 = new PointCh(2600000, 1200000);
        PointCh pt2 = new PointCh(2600100, 1200000);
        PointCh pt3 = new PointCh(2600000, 1200100);
        PointCh pt4 = new PointCh(2601234, 1201234);
        PointCh pt5 = new PointCh(2485001, 1075001);
        PointCh pt6 = new PointCh(2833999, 1295999);


        edges.add(new Edge(1, 1, pt1, pt2, 10, Math::cos));
        edges.add(new Edge(1, 1, pt2, pt3, 10, Math::sin));
        edges.add(new Edge(1, 1, pt3, pt4, 10, Functions.constant(NaN)));
        edges.add(new Edge(1, 1, pt4, pt5, 10, Functions.constant(1)));
        edges.add(new Edge(1, 1, pt5, pt6, 10, Functions.constant(1)));
        edges.add(new Edge(1, 1, pt6, pt6, 10, Functions.constant(1))); // 60

        edges1.add(new Edge(1, 1, pt2, pt1, 10,  Math::sin));
        edges1.add(new Edge(1, 1, pt1, pt5, 10,  Math::cos));
        edges1.add(new Edge(1, 1, pt5, pt4, 10, Functions.constant(1)));
        edges1.add(new Edge(1, 1, pt4, pt6, 10, Functions.constant(NaN)));
        edges1.add(new Edge(1, 1, pt6, pt3, 10, Functions.constant(1)));
        edges1.add(new Edge(1, 1, pt3, pt3, 10, Functions.constant(1))); // 60

        SingleRoute route = new SingleRoute(edges);
        SingleRoute route1 = new SingleRoute(edges1);
        List<Route> listOfRoutes = new ArrayList<>();
        List<Route> listOfRoutesFinal = new ArrayList<>();

        listOfRoutes.add(route);
        listOfRoutes.add(route1);
        MultiRoute multiRoute1 = new MultiRoute(listOfRoutes);

        listOfRoutes.add(multiRoute1);
        MultiRoute multiRoute2 = new MultiRoute(listOfRoutes);

        List<Route> multiRouteInMultiRoute = new ArrayList<>();
        multiRouteInMultiRoute.add(multiRoute2);
        multiRouteInMultiRoute.add(multiRoute1);

        MultiRoute multiRoute3 = new MultiRoute(multiRouteInMultiRoute);
        listOfRoutesFinal.add(multiRoute1);
        listOfRoutesFinal.add(multiRoute2);
        listOfRoutesFinal.add(multiRoute3);

        MultiRoute multiRouteFinal = new MultiRoute(listOfRoutesFinal);

        assertEquals(route1.pointAt(40), multiRouteFinal.pointAt(700));
        assertEquals(route.pointAt(0), multiRouteFinal.pointAt(0));
        assertEquals(route.pointAt(0), multiRouteFinal.pointAt(-700));
        assertEquals(route1.pointAt(0), multiRouteFinal.pointAt(180));
        assertEquals(route1.pointAt(59), multiRouteFinal.pointAt(239));
        assertEquals(route1.pointAt(60), multiRouteFinal.pointAt(1000));



    }

    @Test
    public void elevationAtWorksOnMultiRoutesInMultiRoutes() {
        List<Edge> edges = new ArrayList<>();
        List<Edge> edges1 = new ArrayList<>();

        PointCh pt1 = new PointCh(2600000, 1200000);
        PointCh pt2 = new PointCh(2600100, 1200000);
        PointCh pt3 = new PointCh(2600000, 1200100);
        PointCh pt4 = new PointCh(2601234, 1201234);
        PointCh pt5 = new PointCh(2485001, 1075001);
        PointCh pt6 = new PointCh(2833999, 1295999);


        edges.add(new Edge(1, 1, pt1, pt2, 10, Math::cos));
        edges.add(new Edge(1, 1, pt2, pt3, 10, Math::sin));
        edges.add(new Edge(1, 1, pt3, pt4, 10, Functions.constant(NaN)));
        edges.add(new Edge(1, 1, pt4, pt5, 10, Functions.constant(1)));
        edges.add(new Edge(1, 1, pt5, pt6, 10, Functions.constant(1)));
        edges.add(new Edge(1, 1, pt6, pt6, 10, Functions.constant(1))); // 60

        edges1.add(new Edge(1, 1, pt2, pt1, 10,  Math::sin));
        edges1.add(new Edge(1, 1, pt1, pt5, 10,  Math::cos));
        edges1.add(new Edge(1, 1, pt5, pt4, 10, Functions.constant(1)));
        edges1.add(new Edge(1, 1, pt4, pt6, 10, Functions.constant(NaN)));
        edges1.add(new Edge(1, 1, pt6, pt3, 10, Functions.constant(1)));
        edges1.add(new Edge(1, 1, pt3, pt3, 10, Functions.constant(1))); // 60

        SingleRoute route = new SingleRoute(edges);
        SingleRoute route1 = new SingleRoute(edges1);
        List<Route> listOfRoutes = new ArrayList<>();
        List<Route> listOfRoutesFinal = new ArrayList<>();

        listOfRoutes.add(route);
        listOfRoutes.add(route1);
        MultiRoute multiRoute1 = new MultiRoute(listOfRoutes);

        listOfRoutes.add(multiRoute1);
        MultiRoute multiRoute2 = new MultiRoute(listOfRoutes);

        List<Route> multiRouteInMultiRoute = new ArrayList<>();
        multiRouteInMultiRoute.add(multiRoute2);
        multiRouteInMultiRoute.add(multiRoute1);

        MultiRoute multiRoute3 = new MultiRoute(multiRouteInMultiRoute);
        listOfRoutesFinal.add(multiRoute1);
        listOfRoutesFinal.add(multiRoute2);
        listOfRoutesFinal.add(multiRoute3);

        MultiRoute multiRouteFinal = new MultiRoute(listOfRoutesFinal);

        assertEquals(route1.elevationAt(40), multiRouteFinal.elevationAt(700));
        assertEquals(route.elevationAt(0), multiRouteFinal.elevationAt(0));
        assertEquals(route.elevationAt(0), multiRouteFinal.elevationAt(-700));
        assertEquals(route1.elevationAt(0), multiRouteFinal.elevationAt(180));
        assertEquals(route1.elevationAt(59), multiRouteFinal.elevationAt(239));
        assertEquals(route1.elevationAt(60), multiRouteFinal.elevationAt(1000));

    }

    @Test
    public void nodeClosestToWorksOnMultiRoutesInMultiRoutes() {
        List<Edge> edges = new ArrayList<>();
        List<Edge> edges1 = new ArrayList<>();

        PointCh pt1 = new PointCh(2600000, 1200000);
        PointCh pt2 = new PointCh(2600100, 1200000);
        PointCh pt3 = new PointCh(2600000, 1200100);
        PointCh pt4 = new PointCh(2601234, 1201234);
        PointCh pt5 = new PointCh(2485001, 1075001);
        PointCh pt6 = new PointCh(2833999, 1295999);


        edges.add(new Edge(1, 1, pt1, pt2, 10, Math::cos));
        edges.add(new Edge(1, 1, pt2, pt3, 10, Math::sin));
        edges.add(new Edge(1, 1, pt3, pt4, 10, Functions.constant(NaN)));
        edges.add(new Edge(1, 1, pt4, pt5, 10, Functions.constant(1)));
        edges.add(new Edge(1, 1, pt5, pt6, 10, Functions.constant(1)));
        edges.add(new Edge(1, 1, pt6, pt6, 10, Functions.constant(1))); // 60

        edges1.add(new Edge(1, 1, pt2, pt1, 10,  Math::sin));
        edges1.add(new Edge(1, 1, pt1, pt5, 10,  Math::cos));
        edges1.add(new Edge(1, 1, pt5, pt4, 10, Functions.constant(1)));
        edges1.add(new Edge(1, 1, pt4, pt6, 10, Functions.constant(NaN)));
        edges1.add(new Edge(1, 1, pt6, pt3, 10, Functions.constant(1)));
        edges1.add(new Edge(1, 1, pt3, pt3, 10, Functions.constant(1))); // 60

        SingleRoute route = new SingleRoute(edges);
        SingleRoute route1 = new SingleRoute(edges1);
        List<Route> listOfRoutes = new ArrayList<>();
        List<Route> listOfRoutesFinal = new ArrayList<>();

        listOfRoutes.add(route);
        listOfRoutes.add(route1);
        MultiRoute multiRoute1 = new MultiRoute(listOfRoutes);

        listOfRoutes.add(multiRoute1);
        MultiRoute multiRoute2 = new MultiRoute(listOfRoutes);

        List<Route> multiRouteInMultiRoute = new ArrayList<>();
        multiRouteInMultiRoute.add(multiRoute2);
        multiRouteInMultiRoute.add(multiRoute1);

        MultiRoute multiRoute3 = new MultiRoute(multiRouteInMultiRoute);
        listOfRoutesFinal.add(multiRoute1);
        listOfRoutesFinal.add(multiRoute2);
        listOfRoutesFinal.add(multiRoute3);

        MultiRoute multiRouteFinal = new MultiRoute(listOfRoutesFinal);

        assertEquals(route1.nodeClosestTo(40), multiRouteFinal.nodeClosestTo(700));
        assertEquals(route.nodeClosestTo(0), multiRouteFinal.nodeClosestTo(0));
        assertEquals(route.nodeClosestTo(0), multiRouteFinal.nodeClosestTo(-700));
        assertEquals(route1.nodeClosestTo(0), multiRouteFinal.nodeClosestTo(180));
        assertEquals(route1.nodeClosestTo(59), multiRouteFinal.nodeClosestTo(239));
        assertEquals(route1.nodeClosestTo(60), multiRouteFinal.nodeClosestTo(1000));

    }

    @Test
    public void pointClosestToWorksOnMultiRoutesInMultiRoutes() {
        List<Edge> edges = new ArrayList<>();
        List<Edge> edges1 = new ArrayList<>();

        PointCh pt1 = new PointCh(2600000, 1200000);
        PointCh pt2 = new PointCh(2600100, 1200000);
        PointCh pt3 = new PointCh(2600110, 1200000);
        PointCh pt4 = new PointCh(2600234, 1200000);
        PointCh pt5 = new PointCh(2601000, 1200000);

        edges1.add(new Edge(1, 1, pt1, pt2, 10, Math::cos));
        edges1.add(new Edge(1, 1, pt2, pt3, 10, Math::sin));
        edges1.add(new Edge(1, 1, pt3, pt4, 10, Functions.constant(NaN)));
        edges1.add(new Edge(1, 1, pt4, pt5, 10, Functions.constant(1)));

        edges.add(new Edge(0, 1, pt1, pt2, 100, Functions.constant(1)));
        edges.add(new Edge(1, 2, pt2, pt3, 10, Functions.constant(1)));
        edges.add(new Edge(2, 3, pt3, pt4, 124, Functions.constant(1)));
        edges.add(new Edge(3, 4, pt4, pt5, 766, Functions.constant(1)));

        SingleRoute route = new SingleRoute(edges);
        SingleRoute route1 = new SingleRoute(edges1);
        List<Route> listOfRoutes = new ArrayList<>();
        List<Route> listOfRoutesFinal = new ArrayList<>();

        listOfRoutes.add(route);
        listOfRoutes.add(route1);
        MultiRoute multiRoute1 = new MultiRoute(listOfRoutes);

        listOfRoutes.add(multiRoute1);
        MultiRoute multiRoute2 = new MultiRoute(listOfRoutes);

        List<Route> multiRouteInMultiRoute = new ArrayList<>();
        multiRouteInMultiRoute.add(multiRoute2);
        multiRouteInMultiRoute.add(multiRoute1);

        MultiRoute multiRoute3 = new MultiRoute(multiRouteInMultiRoute);
        listOfRoutesFinal.add(multiRoute1);
        listOfRoutesFinal.add(multiRoute2);
        listOfRoutesFinal.add(multiRoute3);

        MultiRoute multiRouteFinal = new MultiRoute(listOfRoutesFinal);


        PointCh ptClosest1 = new PointCh(2600000,1200010);
        PointCh ptClosest2 = new PointCh(2600234, 1200004);
        PointCh ptClosest3 = new PointCh(2600112, 1200002);
        PointCh ptClosest4 = new PointCh(2600238, 1200067);

        RoutePoint routePt = new RoutePoint(pt1,0.0,0.0);
        RoutePoint routePtBis = new RoutePoint(pt5,route.length(),0);
        RoutePoint routePt1 = new RoutePoint(pt1,0.0,10.0);
        RoutePoint routePt2 = new RoutePoint(pt4,234,4.0);
        RoutePoint routePt3 = new RoutePoint(new PointCh(2600112,1200000),112 ,2.0);
        RoutePoint routePt4 = new RoutePoint(new PointCh(2600238,1200000),238,67.0);


        assertEquals(routePt, multiRouteFinal.pointClosestTo(pt1));
        assertEquals(routePt1, multiRouteFinal.pointClosestTo(ptClosest1));
        assertEquals(routePt3, multiRouteFinal.pointClosestTo(ptClosest3));
        assertEquals(routePtBis, multiRouteFinal.pointClosestTo(pt5));
        assertEquals(routePt2, multiRouteFinal.pointClosestTo(ptClosest2));
        assertEquals(routePt4, multiRouteFinal.pointClosestTo(ptClosest4));

    }


}

class MultiRouteTestLoricAdrian {

    @Test
    public void MultiRouteConstructionWorking(){


        List<Edge> edges = new ArrayList<Edge>();
        List<PointCh> points = new ArrayList<PointCh>();

        float[] typicalYasuoPlayer = {0.0f,-15f,8f,178f};

        PointCh pt1 = new PointCh(SwissBounds.MAX_E,SwissBounds.MIN_N);
        PointCh pt2 = new PointCh(SwissBounds.MAX_E-5800,SwissBounds.MIN_N);
        PointCh pt3 = new PointCh(SwissBounds.MAX_E-5800,SwissBounds.MIN_N);
        PointCh pt4 = new PointCh(SwissBounds.MAX_E-8100,SwissBounds.MIN_N);
        PointCh pt5 = new PointCh(SwissBounds.MAX_E-8100,SwissBounds.MIN_N);
        PointCh pt6 = new PointCh(SwissBounds.MAX_E-9200,SwissBounds.MIN_N);
        PointCh pt7 = new PointCh(SwissBounds.MAX_E-9200,SwissBounds.MIN_N);
        PointCh pt8 = new PointCh(SwissBounds.MAX_E-11400,SwissBounds.MIN_N);
        PointCh pt9 = new PointCh(SwissBounds.MAX_E-11400,SwissBounds.MIN_N);
        PointCh pt10 = new PointCh(SwissBounds.MAX_E-13100,SwissBounds.MIN_N);

        Edge edge1 = new Edge(0,1, pt1,pt2, 5800, Functions.sampled(typicalYasuoPlayer,5800));
        Edge edge2 = new Edge(1,2, pt3, pt4, 2300, Functions.sampled(typicalYasuoPlayer,2300));
        Edge edge3 = new Edge(2,3, pt5, pt6, 1100, Functions.sampled(typicalYasuoPlayer,1100));
        Edge edge4 = new Edge(3,4, pt7, pt8, 2200, Functions.sampled(typicalYasuoPlayer,2200));
        Edge edge5 = new Edge(4,5, pt9, pt10, 1700, Functions.sampled(typicalYasuoPlayer,1700));

        points.add(pt1);
        points.add(pt2);
        points.add(pt4);
        points.add(pt6);
        points.add(pt8);
        points.add(pt10);

        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);
        edges.add(edge4);
        edges.add(edge5);

        List<Route> segments = new ArrayList<>();

        SingleRoute sr1 = new SingleRoute(edges.subList(0,1));
        SingleRoute sr2 = new SingleRoute(edges.subList(1,2));

        List<Route> componentsOfMulti = new ArrayList<>();

        SingleRoute sr3 = new SingleRoute(edges.subList(2,3));
        SingleRoute sr4 = new SingleRoute(edges.subList(3,4));

        componentsOfMulti.add(sr3);
        componentsOfMulti.add(sr4);

        segments.add(sr1);
        segments.add(sr2);

        MultiRoute comp = new MultiRoute(componentsOfMulti);

        segments.add(comp);

        MultiRoute mt = new MultiRoute(segments);

        assertThrows(IllegalArgumentException.class, ()-> {

            List<Route> emptyOne = new ArrayList<>();

            MultiRoute mtBug = new MultiRoute(emptyOne);

        });

    }

    @Test
    public void MultiRouteImmuabilityWorking(){


        List<Edge> edges = new ArrayList<Edge>();
        List<PointCh> points = new ArrayList<PointCh>();

        float[] typicalYasuoPlayer = {0.0f,-15f,8f,178f};

        PointCh pt1 = new PointCh(SwissBounds.MAX_E,SwissBounds.MIN_N);
        PointCh pt2 = new PointCh(SwissBounds.MAX_E-5800,SwissBounds.MIN_N);
        PointCh pt3 = new PointCh(SwissBounds.MAX_E-5800,SwissBounds.MIN_N);
        PointCh pt4 = new PointCh(SwissBounds.MAX_E-8100,SwissBounds.MIN_N);
        PointCh pt5 = new PointCh(SwissBounds.MAX_E-8100,SwissBounds.MIN_N);
        PointCh pt6 = new PointCh(SwissBounds.MAX_E-9200,SwissBounds.MIN_N);
        PointCh pt7 = new PointCh(SwissBounds.MAX_E-9200,SwissBounds.MIN_N);
        PointCh pt8 = new PointCh(SwissBounds.MAX_E-11400,SwissBounds.MIN_N);
        PointCh pt9 = new PointCh(SwissBounds.MAX_E-11400,SwissBounds.MIN_N);
        PointCh pt10 = new PointCh(SwissBounds.MAX_E-13100,SwissBounds.MIN_N);

        Edge edge1 = new Edge(0,1, pt1,pt2, 5800, Functions.sampled(typicalYasuoPlayer,5800));
        Edge edge2 = new Edge(1,2, pt3, pt4, 2300, Functions.sampled(typicalYasuoPlayer,2300));
        Edge edge3 = new Edge(2,3, pt5, pt6, 1100, Functions.sampled(typicalYasuoPlayer,1100));
        Edge edge4 = new Edge(3,4, pt7, pt8, 2200, Functions.sampled(typicalYasuoPlayer,2200));
        Edge edge5 = new Edge(4,5, pt9, pt10, 1700, Functions.sampled(typicalYasuoPlayer,1700));

        points.add(pt1);
        points.add(pt2);
        points.add(pt4);
        points.add(pt6);
        points.add(pt8);
        points.add(pt10);

        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);
        edges.add(edge4);
        edges.add(edge5);

        List<Route> segments = new ArrayList<>();

        SingleRoute sr1 = new SingleRoute(edges.subList(0,2));
        SingleRoute sr2 = new SingleRoute(edges.subList(2,4));

        segments.add(sr1);
        segments.add(sr2);

        MultiRoute comp = new MultiRoute(segments);

        segments.add(comp);

        MultiRoute mt = new MultiRoute(segments);

        Edge attToModify = mt.edges().get(0);

        attToModify = new Edge(0,1, pt1,pt2, 848489, Functions.sampled(typicalYasuoPlayer,848489));


        assertEquals(mt.edges().get(0),edge1);

    }

    @Test
    public void MultiRouteIndexOfSegmentAtWorking(){


        List<Edge> edges = new ArrayList<Edge>();
        List<PointCh> points = new ArrayList<PointCh>();

        float[] typicalYasuoPlayer = {0.0f,-15f,8f,178f};

        PointCh pt1 = new PointCh(SwissBounds.MAX_E,SwissBounds.MIN_N);
        PointCh pt2 = new PointCh(SwissBounds.MAX_E-5800,SwissBounds.MIN_N);
        PointCh pt3 = new PointCh(SwissBounds.MAX_E-5800,SwissBounds.MIN_N);
        PointCh pt4 = new PointCh(SwissBounds.MAX_E-8100,SwissBounds.MIN_N);
        PointCh pt5 = new PointCh(SwissBounds.MAX_E-8100,SwissBounds.MIN_N);
        PointCh pt6 = new PointCh(SwissBounds.MAX_E-9200,SwissBounds.MIN_N);
        PointCh pt7 = new PointCh(SwissBounds.MAX_E-9200,SwissBounds.MIN_N);
        PointCh pt8 = new PointCh(SwissBounds.MAX_E-11400,SwissBounds.MIN_N);
        PointCh pt9 = new PointCh(SwissBounds.MAX_E-11400,SwissBounds.MIN_N);
        PointCh pt10 = new PointCh(SwissBounds.MAX_E-13100,SwissBounds.MIN_N);

        Edge edge1 = new Edge(0,1, pt1,pt2, 5800, Functions.sampled(typicalYasuoPlayer,5800));
        Edge edge2 = new Edge(1,2, pt3, pt4, 2300, Functions.sampled(typicalYasuoPlayer,2300));
        Edge edge3 = new Edge(2,3, pt5, pt6, 1100, Functions.sampled(typicalYasuoPlayer,1100));
        Edge edge4 = new Edge(3,4, pt7, pt8, 2200, Functions.sampled(typicalYasuoPlayer,2200));
        Edge edge5 = new Edge(4,5, pt9, pt10, 1700, Functions.sampled(typicalYasuoPlayer,1700));

        Edge edge6 = new Edge(3,4, pt7, pt8, 1000, Functions.sampled(typicalYasuoPlayer,1000));
        Edge edge7 = new Edge(4,5, pt9, pt10, 1000, Functions.sampled(typicalYasuoPlayer,1000));

        points.add(pt1);
        points.add(pt2);
        points.add(pt4);
        points.add(pt6);
        points.add(pt8);
        points.add(pt10);

        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);
        edges.add(edge4);
        edges.add(edge5);

        edges.add(edge6);
        edges.add(edge7);

        List<Route> segments = new ArrayList<>();

        SingleRoute sr1 = new SingleRoute(edges.subList(0,1));
        SingleRoute sr2 = new SingleRoute(edges.subList(1,2));

        List<Route> componentsOfMulti = new ArrayList<>();

        SingleRoute sr3 = new SingleRoute(edges.subList(2,3));
        SingleRoute sr4 = new SingleRoute(edges.subList(3,5));

        componentsOfMulti.add(sr3);
        componentsOfMulti.add(sr4);

        segments.add(sr1);
        segments.add(sr2);

        MultiRoute comp = new MultiRoute(componentsOfMulti);

        segments.add(comp);

        MultiRoute mt = new MultiRoute(segments);

        assertEquals(0,mt.indexOfSegmentAt(5400));
        assertEquals(0,mt.indexOfSegmentAt(5800));
        assertEquals(0,mt.indexOfSegmentAt(-12000));
        assertEquals(1,mt.indexOfSegmentAt(7800));
        assertEquals(2,mt.indexOfSegmentAt(8105));
        assertEquals(2,mt.indexOfSegmentAt(8405));
        assertEquals(3,mt.indexOfSegmentAt(11300));
        assertEquals(3,mt.indexOfSegmentAt(13500));

        List<Route> inceptionRoute = new ArrayList<>();

        inceptionRoute.add(sr1);
        inceptionRoute.add(sr2);

        List<Route> segmentCouche2 = new ArrayList<>();

        segmentCouche2.add(sr2);
        segmentCouche2.add(sr3);

        List<Route> segmentCouche3 = new ArrayList<>();

        SingleRoute sr5 = new SingleRoute(edges.subList(5,6));
        SingleRoute sr6 = new SingleRoute(edges.subList(6,7));

        segmentCouche3.add(sr5);
        segmentCouche3.add(sr6);

        MultiRoute couche3 = new MultiRoute(segmentCouche3);

        segmentCouche2.add(couche3);

        MultiRoute couche2 = new MultiRoute(segmentCouche2);

        inceptionRoute.add(couche2);
        inceptionRoute.add(sr5);

        MultiRoute couche1 = new MultiRoute(inceptionRoute);

        assertEquals(2,couche1.indexOfSegmentAt(10300));
        assertEquals(3,couche1.indexOfSegmentAt(11400));

        assertEquals(4,couche1.indexOfSegmentAt(11600));
        assertEquals(5,couche1.indexOfSegmentAt(12700));
        assertEquals(6,couche1.indexOfSegmentAt(13600));
        assertEquals(6,couche1.indexOfSegmentAt(15000));

    }

    @Test
    public void MultiRouteLengthWorking(){


        List<Edge> edges = new ArrayList<Edge>();
        List<PointCh> points = new ArrayList<PointCh>();

        float[] typicalYasuoPlayer = {0.0f,-15f,8f,178f};

        PointCh pt1 = new PointCh(SwissBounds.MAX_E,SwissBounds.MIN_N);
        PointCh pt2 = new PointCh(SwissBounds.MAX_E-5800,SwissBounds.MIN_N);
        PointCh pt3 = new PointCh(SwissBounds.MAX_E-5800,SwissBounds.MIN_N);
        PointCh pt4 = new PointCh(SwissBounds.MAX_E-8100,SwissBounds.MIN_N);
        PointCh pt5 = new PointCh(SwissBounds.MAX_E-8100,SwissBounds.MIN_N);
        PointCh pt6 = new PointCh(SwissBounds.MAX_E-9200,SwissBounds.MIN_N);
        PointCh pt7 = new PointCh(SwissBounds.MAX_E-9200,SwissBounds.MIN_N);
        PointCh pt8 = new PointCh(SwissBounds.MAX_E-11400,SwissBounds.MIN_N);
        PointCh pt9 = new PointCh(SwissBounds.MAX_E-11400,SwissBounds.MIN_N);
        PointCh pt10 = new PointCh(SwissBounds.MAX_E-13100,SwissBounds.MIN_N);

        Edge edge1 = new Edge(0,1, pt1,pt2, 5800, Functions.sampled(typicalYasuoPlayer,5800));
        Edge edge2 = new Edge(1,2, pt3, pt4, 2300, Functions.sampled(typicalYasuoPlayer,2300));
        Edge edge3 = new Edge(2,3, pt5, pt6, 1100, Functions.sampled(typicalYasuoPlayer,1100));
        Edge edge4 = new Edge(3,4, pt7, pt8, 2200, Functions.sampled(typicalYasuoPlayer,2200));
        Edge edge5 = new Edge(4,5, pt9, pt10, 1700, Functions.sampled(typicalYasuoPlayer,1700));

        Edge edge6 = new Edge(3,4, pt7, pt8, 1000, Functions.sampled(typicalYasuoPlayer,1000));
        Edge edge7 = new Edge(4,5, pt9, pt10, 1000, Functions.sampled(typicalYasuoPlayer,1000));

        points.add(pt1);
        points.add(pt2);
        points.add(pt4);
        points.add(pt6);
        points.add(pt8);
        points.add(pt10);

        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);
        edges.add(edge4);
        edges.add(edge5);

        edges.add(edge6);
        edges.add(edge7);

        List<Route> segments = new ArrayList<>();

        SingleRoute sr1 = new SingleRoute(edges.subList(0,1));
        SingleRoute sr2 = new SingleRoute(edges.subList(1,2));

        List<Route> componentsOfMulti = new ArrayList<>();

        SingleRoute sr3 = new SingleRoute(edges.subList(2,3));
        SingleRoute sr4 = new SingleRoute(edges.subList(3,5));

        componentsOfMulti.add(sr3);
        componentsOfMulti.add(sr4);

        segments.add(sr1);
        segments.add(sr2);

        MultiRoute comp = new MultiRoute(componentsOfMulti);

        segments.add(comp);

        MultiRoute mt = new MultiRoute(segments);

        assertEquals(13100,mt.length());

        List<Route> inceptionRoute = new ArrayList<>();

        inceptionRoute.add(sr1);
        inceptionRoute.add(sr2);

        List<Route> segmentCouche2 = new ArrayList<>();

        segmentCouche2.add(sr2);
        segmentCouche2.add(sr3);

        List<Route> segmentCouche3 = new ArrayList<>();

        SingleRoute sr5 = new SingleRoute(edges.subList(5,6));
        SingleRoute sr6 = new SingleRoute(edges.subList(6,7));

        segmentCouche3.add(sr5);
        segmentCouche3.add(sr6);

        MultiRoute couche3 = new MultiRoute(segmentCouche3);

        segmentCouche2.add(couche3);

        MultiRoute couche2 = new MultiRoute(segmentCouche2);

        inceptionRoute.add(couche2);
        inceptionRoute.add(sr5);

        MultiRoute couche1 = new MultiRoute(inceptionRoute);

        assertEquals(14500,couche1.length());
        assertEquals(5400,couche2.length());
        assertEquals(2000,couche3.length());

    }

    @Test
    public void MultiRoutePointsWorking(){


        List<Edge> edges = new ArrayList<Edge>();
        List<PointCh> points = new ArrayList<PointCh>();

        float[] typicalYasuoPlayer = {0.0f,-15f,8f,178f};

        PointCh pt1 = new PointCh(SwissBounds.MAX_E,SwissBounds.MIN_N);
        PointCh pt2 = new PointCh(SwissBounds.MAX_E-5800,SwissBounds.MIN_N);
        PointCh pt3 = new PointCh(SwissBounds.MAX_E-5800,SwissBounds.MIN_N);
        PointCh pt4 = new PointCh(SwissBounds.MAX_E-8100,SwissBounds.MIN_N);
        PointCh pt5 = new PointCh(SwissBounds.MAX_E-8100,SwissBounds.MIN_N);
        PointCh pt6 = new PointCh(SwissBounds.MAX_E-9200,SwissBounds.MIN_N);
        PointCh pt7 = new PointCh(SwissBounds.MAX_E-9200,SwissBounds.MIN_N);
        PointCh pt8 = new PointCh(SwissBounds.MAX_E-11400,SwissBounds.MIN_N);
        PointCh pt9 = new PointCh(SwissBounds.MAX_E-11400,SwissBounds.MIN_N);
        PointCh pt10 = new PointCh(SwissBounds.MAX_E-13100,SwissBounds.MIN_N);

        Edge edge1 = new Edge(0,1, pt1,pt2, 5800, Functions.sampled(typicalYasuoPlayer,5800));
        Edge edge2 = new Edge(1,2, pt3, pt4, 2300, Functions.sampled(typicalYasuoPlayer,2300));
        Edge edge3 = new Edge(2,3, pt5, pt6, 1100, Functions.sampled(typicalYasuoPlayer,1100));
        Edge edge4 = new Edge(3,4, pt7, pt8, 2200, Functions.sampled(typicalYasuoPlayer,2200));
        Edge edge5 = new Edge(4,5, pt9, pt10, 1700, Functions.sampled(typicalYasuoPlayer,1700));

        Edge edge6 = new Edge(3,4, pt7, pt8, 1000, Functions.sampled(typicalYasuoPlayer,1000));
        Edge edge7 = new Edge(4,5, pt9, pt10, 1000, Functions.sampled(typicalYasuoPlayer,1000));

        points.add(pt1);
        points.add(pt2);
        points.add(pt4);
        points.add(pt6);
        points.add(pt8);
        points.add(pt10);

        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);
        edges.add(edge4);
        edges.add(edge5);

        edges.add(edge6);
        edges.add(edge7);

        List<Route> segments = new ArrayList<>();

        SingleRoute sr1 = new SingleRoute(edges.subList(0,1));
        SingleRoute sr2 = new SingleRoute(edges.subList(1,2));

        segments.add(sr1);
        segments.add(sr2);

        MultiRoute mt = new MultiRoute(segments);

        List<PointCh> expectedPoints = new ArrayList<>();

        expectedPoints.add(pt1);
        expectedPoints.add(pt3);
        expectedPoints.add(pt4);

        assertEquals(expectedPoints,mt.points());

    }

    @Test
    public void MultiElevationAtWorking(){


        List<Edge> edges = new ArrayList<Edge>();
        List<PointCh> points = new ArrayList<PointCh>();

        float[] typicalYasuoPlayer = {0.0f,-15f,8f,178f};

        PointCh pt1 = new PointCh(SwissBounds.MAX_E,SwissBounds.MIN_N);
        PointCh pt2 = new PointCh(SwissBounds.MAX_E-5800,SwissBounds.MIN_N);
        PointCh pt3 = new PointCh(SwissBounds.MAX_E-5800,SwissBounds.MIN_N);
        PointCh pt4 = new PointCh(SwissBounds.MAX_E-8100,SwissBounds.MIN_N);
        PointCh pt5 = new PointCh(SwissBounds.MAX_E-8100,SwissBounds.MIN_N);
        PointCh pt6 = new PointCh(SwissBounds.MAX_E-9200,SwissBounds.MIN_N);
        PointCh pt7 = new PointCh(SwissBounds.MAX_E-9200,SwissBounds.MIN_N);
        PointCh pt8 = new PointCh(SwissBounds.MAX_E-11400,SwissBounds.MIN_N);
        PointCh pt9 = new PointCh(SwissBounds.MAX_E-11400,SwissBounds.MIN_N);
        PointCh pt10 = new PointCh(SwissBounds.MAX_E-13100,SwissBounds.MIN_N);

        Edge edge1 = new Edge(0,1, pt1,pt2, 5800, Functions.sampled(typicalYasuoPlayer,5800));
        Edge edge2 = new Edge(1,2, pt3, pt4, 2300, Functions.sampled(typicalYasuoPlayer,2300));
        Edge edge3 = new Edge(2,3, pt5, pt6, 1100, Functions.sampled(typicalYasuoPlayer,1100));
        Edge edge4 = new Edge(3,4, pt7, pt8, 2200, Functions.sampled(typicalYasuoPlayer,2200));
        Edge edge5 = new Edge(4,5, pt9, pt10, 1700, Functions.sampled(typicalYasuoPlayer,1700));

        Edge edge6 = new Edge(3,4, pt7, pt8, 1000, Functions.sampled(typicalYasuoPlayer,1000));
        Edge edge7 = new Edge(4,5, pt9, pt10, 1000, Functions.sampled(typicalYasuoPlayer,1000));

        points.add(pt1);
        points.add(pt2);
        points.add(pt4);
        points.add(pt6);
        points.add(pt8);
        points.add(pt10);

        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);
        edges.add(edge4);
        edges.add(edge5);

        edges.add(edge6);
        edges.add(edge7);

        List<Route> segments = new ArrayList<>();

        SingleRoute sr1 = new SingleRoute(edges.subList(0,1));
        SingleRoute sr2 = new SingleRoute(edges.subList(1,2));

        List<Route> componentsOfMulti = new ArrayList<>();

        SingleRoute sr3 = new SingleRoute(edges.subList(2,3));
        SingleRoute sr4 = new SingleRoute(edges.subList(3,5));

        componentsOfMulti.add(sr3);
        componentsOfMulti.add(sr4);

        segments.add(sr1);
        segments.add(sr2);

        MultiRoute comp = new MultiRoute(componentsOfMulti);

        assertEquals(sr3.elevationAt(1000),comp.elevationAt(1000));
        assertEquals(sr3.elevationAt(0),comp.elevationAt(-100));
        assertEquals(sr4.elevationAt(100),comp.elevationAt(1200));
        assertEquals(sr4.elevationAt(3900),comp.elevationAt(5500));

        segments.add(comp);

        MultiRoute mt = new MultiRoute(segments);

        List<Route> inceptionRoute = new ArrayList<>();

        inceptionRoute.add(sr1);
        inceptionRoute.add(sr2);

        List<Route> segmentCouche2 = new ArrayList<>();

        segmentCouche2.add(sr2);
        segmentCouche2.add(sr3);

        List<Route> segmentCouche3 = new ArrayList<>();

        SingleRoute sr5 = new SingleRoute(edges.subList(5,6));
        SingleRoute sr6 = new SingleRoute(edges.subList(6,7));

        segmentCouche3.add(sr5);
        segmentCouche3.add(sr6);

        MultiRoute couche3 = new MultiRoute(segmentCouche3);

        segmentCouche2.add(couche3);

        MultiRoute couche2 = new MultiRoute(segmentCouche2);

        inceptionRoute.add(couche2);
        inceptionRoute.add(sr5);

        MultiRoute couche1 = new MultiRoute(inceptionRoute);

        assertEquals(sr2.elevationAt(100),couche1.elevationAt(5900));
        assertEquals(couche2.elevationAt(100),couche1.elevationAt(8200));
        assertEquals(couche2.elevationAt(2200),couche1.elevationAt(10300));
        assertEquals(couche3.elevationAt(100),couche1.elevationAt(11600));
        assertEquals(couche3.elevationAt(1000),couche1.elevationAt(12500));
        assertEquals(couche2.elevationAt(2400),couche1.elevationAt(10500));
        assertEquals(sr5.elevationAt(100),couche1.elevationAt(13600));
        assertEquals(sr5.elevationAt(900),couche1.elevationAt(14400));
        assertEquals(sr5.elevationAt(1000),couche1.elevationAt(14500));
        assertEquals(sr5.elevationAt(1000),couche1.elevationAt(15600));


    }

    @Test
    public void MultiNodeClosestToWorking(){


        List<Edge> edges = new ArrayList<Edge>();
        List<PointCh> points = new ArrayList<PointCh>();

        float[] typicalYasuoPlayer = {0.0f,-15f,8f,178f};

        PointCh pt1 = new PointCh(SwissBounds.MAX_E,SwissBounds.MIN_N);
        PointCh pt2 = new PointCh(SwissBounds.MAX_E-5800,SwissBounds.MIN_N);
        PointCh pt3 = new PointCh(SwissBounds.MAX_E-5800,SwissBounds.MIN_N);
        PointCh pt4 = new PointCh(SwissBounds.MAX_E-8100,SwissBounds.MIN_N);
        PointCh pt5 = new PointCh(SwissBounds.MAX_E-8100,SwissBounds.MIN_N);
        PointCh pt6 = new PointCh(SwissBounds.MAX_E-9200,SwissBounds.MIN_N);
        PointCh pt7 = new PointCh(SwissBounds.MAX_E-9200,SwissBounds.MIN_N);
        PointCh pt8 = new PointCh(SwissBounds.MAX_E-11400,SwissBounds.MIN_N);
        PointCh pt9 = new PointCh(SwissBounds.MAX_E-11400,SwissBounds.MIN_N);
        PointCh pt10 = new PointCh(SwissBounds.MAX_E-13100,SwissBounds.MIN_N);

        Edge edge1 = new Edge(0,1, pt1,pt2, 5800, Functions.sampled(typicalYasuoPlayer,5800));
        Edge edge2 = new Edge(1,2, pt3, pt4, 2300, Functions.sampled(typicalYasuoPlayer,2300));
        Edge edge3 = new Edge(2,3, pt5, pt6, 1100, Functions.sampled(typicalYasuoPlayer,1100));
        Edge edge4 = new Edge(3,4, pt7, pt8, 2200, Functions.sampled(typicalYasuoPlayer,2200));
        Edge edge5 = new Edge(4,5, pt9, pt10, 1700, Functions.sampled(typicalYasuoPlayer,1700));

        Edge edge6 = new Edge(3,4, pt7, pt8, 1000, Functions.sampled(typicalYasuoPlayer,1000));
        Edge edge7 = new Edge(4,5, pt9, pt10, 1000, Functions.sampled(typicalYasuoPlayer,1000));

        points.add(pt1);
        points.add(pt2);
        points.add(pt4);
        points.add(pt6);
        points.add(pt8);
        points.add(pt10);

        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);
        edges.add(edge4);
        edges.add(edge5);

        edges.add(edge6);
        edges.add(edge7);

        List<Route> segments = new ArrayList<>();

        SingleRoute sr1 = new SingleRoute(edges.subList(0,1));
        SingleRoute sr2 = new SingleRoute(edges.subList(1,2));

        List<Route> componentsOfMulti = new ArrayList<>();

        SingleRoute sr3 = new SingleRoute(edges.subList(2,3));
        SingleRoute sr4 = new SingleRoute(edges.subList(3,5));

        componentsOfMulti.add(sr3);
        componentsOfMulti.add(sr4);

        segments.add(sr1);
        segments.add(sr2);

        MultiRoute comp = new MultiRoute(componentsOfMulti);

        assertEquals(0,comp.nodeClosestTo(550));
        assertEquals(1,comp.nodeClosestTo(1000));
        assertEquals(0,comp.nodeClosestTo(-100));
//        assertEquals(1,comp.nodeClosestTo(1200));
        segments.add(comp);

        MultiRoute mt = new MultiRoute(segments);

        assertEquals(13100,mt.length());

        List<Route> inceptionRoute = new ArrayList<>();

        inceptionRoute.add(sr1);
        inceptionRoute.add(sr2);

        List<Route> segmentCouche2 = new ArrayList<>();

        segmentCouche2.add(sr2);
        segmentCouche2.add(sr3);

        List<Route> segmentCouche3 = new ArrayList<>();

        SingleRoute sr5 = new SingleRoute(edges.subList(5,6));
        SingleRoute sr6 = new SingleRoute(edges.subList(6,7));

        segmentCouche3.add(sr5);
        segmentCouche3.add(sr6);

        MultiRoute couche3 = new MultiRoute(segmentCouche3);

        segmentCouche2.add(couche3);

        MultiRoute couche2 = new MultiRoute(segmentCouche2);

        inceptionRoute.add(couche2);
        inceptionRoute.add(sr5);

        MultiRoute couche1 = new MultiRoute(inceptionRoute);

        assertEquals(0,couche1.nodeClosestTo(550));
        assertEquals(0,couche1.nodeClosestTo(2400));
        assertEquals(1,couche1.nodeClosestTo(3000));
        assertEquals(2,couche1.nodeClosestTo(8000));
        assertEquals(2,couche1.nodeClosestTo(8400));
        assertEquals(4,couche1.nodeClosestTo(11400));
        assertEquals(5,couche1.nodeClosestTo(12600));

        assertEquals(4,couche1.nodeClosestTo(11900));
        assertEquals(5,couche1.nodeClosestTo(12900));
        assertEquals(6,couche1.nodeClosestTo(13700));
        assertEquals(6,couche1.nodeClosestTo(14000));
        assertEquals(7,couche1.nodeClosestTo(18000));

    }

    @Test
    public void MultiNodeClosestToWorking2(){


        List<Edge> edges = new ArrayList<Edge>();
        List<PointCh> points = new ArrayList<PointCh>();

        float[] typicalYasuoPlayer = {0.0f,-15f,8f,178f};

        PointCh pt1 = new PointCh(SwissBounds.MAX_E,SwissBounds.MIN_N);
        PointCh pt2 = new PointCh(SwissBounds.MAX_E-5800,SwissBounds.MIN_N);
        PointCh pt3 = new PointCh(SwissBounds.MAX_E-5800,SwissBounds.MIN_N);
        PointCh pt4 = new PointCh(SwissBounds.MAX_E-8100,SwissBounds.MIN_N);
        PointCh pt5 = new PointCh(SwissBounds.MAX_E-8100,SwissBounds.MIN_N);
        PointCh pt6 = new PointCh(SwissBounds.MAX_E-9200,SwissBounds.MIN_N);
        PointCh pt7 = new PointCh(SwissBounds.MAX_E-9200,SwissBounds.MIN_N);
        PointCh pt8 = new PointCh(SwissBounds.MAX_E-11400,SwissBounds.MIN_N);
        PointCh pt9 = new PointCh(SwissBounds.MAX_E-11400,SwissBounds.MIN_N);
        PointCh pt10 = new PointCh(SwissBounds.MAX_E-13100,SwissBounds.MIN_N);

        Edge edge1 = new Edge(0,1, pt1,pt2, 5800, Functions.sampled(typicalYasuoPlayer,5800));
        Edge edge2 = new Edge(1,2, pt3, pt4, 2300, Functions.sampled(typicalYasuoPlayer,2300));
        Edge edge3 = new Edge(2,3, pt5, pt6, 1100, Functions.sampled(typicalYasuoPlayer,1100));
        Edge edge4 = new Edge(3,4, pt7, pt8, 2200, Functions.sampled(typicalYasuoPlayer,2200));
        Edge edge5 = new Edge(4,5, pt9, pt10, 1700, Functions.sampled(typicalYasuoPlayer,1700));

        Edge edge6 = new Edge(3,4, pt7, pt8, 1000, Functions.sampled(typicalYasuoPlayer,1000));
        Edge edge7 = new Edge(4,5, pt9, pt10, 1000, Functions.sampled(typicalYasuoPlayer,1000));

        Edge bigFamily1 = new Edge(0,1, pt1,pt2, 500, Functions.sampled(typicalYasuoPlayer,500));
        Edge bigFamily2 = new Edge(0,1, pt1,pt2, 500, Functions.sampled(typicalYasuoPlayer,500));
        Edge bigFamily3 = new Edge(0,1, pt1,pt2, 1000, Functions.sampled(typicalYasuoPlayer,1000));
        Edge bigFamily4 = new Edge(0,1, pt1,pt2, 1000, Functions.sampled(typicalYasuoPlayer,1000));
        Edge bigFamily5 = new Edge(0,1, pt1,pt2, 1000, Functions.sampled(typicalYasuoPlayer,1000));
        Edge bigFamily6 = new Edge(0,1, pt1,pt2, 500, Functions.sampled(typicalYasuoPlayer,500));
        Edge bigFamily7 = new Edge(0,1, pt1,pt2, 500, Functions.sampled(typicalYasuoPlayer,500));
        Edge bigFamily8 = new Edge(0,1, pt1,pt2, 300, Functions.sampled(typicalYasuoPlayer,300));

        List<Edge> bigFamilyCompleteVersion = new ArrayList<>();

        bigFamilyCompleteVersion.add(bigFamily1);
        bigFamilyCompleteVersion.add(bigFamily2);
        bigFamilyCompleteVersion.add(bigFamily3);
        bigFamilyCompleteVersion.add(bigFamily4);
        bigFamilyCompleteVersion.add(bigFamily5);
        bigFamilyCompleteVersion.add(bigFamily6);
        bigFamilyCompleteVersion.add(bigFamily7);
        bigFamilyCompleteVersion.add(bigFamily8);

        points.add(pt1);
        points.add(pt2);
        points.add(pt4);
        points.add(pt6);
        points.add(pt8);
        points.add(pt10);

        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);
        edges.add(edge4);
        edges.add(edge5);

        edges.add(edge6);
        edges.add(edge7);

        List<Route> segments = new ArrayList<>();

        SingleRoute sr1 = new SingleRoute(bigFamilyCompleteVersion);
        SingleRoute sr2 = new SingleRoute(edges.subList(1,2));

        List<Route> componentsOfMulti = new ArrayList<>();

        SingleRoute sr3 = new SingleRoute(edges.subList(2,3));
        SingleRoute sr4 = new SingleRoute(edges.subList(3,5));

        componentsOfMulti.add(sr3);
        componentsOfMulti.add(sr4);

        segments.add(sr1);
        segments.add(sr2);

        MultiRoute comp = new MultiRoute(componentsOfMulti);

        assertEquals(0,comp.nodeClosestTo(550));
        assertEquals(1,comp.nodeClosestTo(1000));
        assertEquals(0,comp.nodeClosestTo(-100));
//        assertEquals(1,comp.nodeClosestTo(1200));
        segments.add(comp);

        MultiRoute mt = new MultiRoute(segments);


        List<Route> inceptionRoute = new ArrayList<>();

        inceptionRoute.add(sr1);
        inceptionRoute.add(sr2);

        List<Route> segmentCouche2 = new ArrayList<>();

        segmentCouche2.add(sr2);
        segmentCouche2.add(sr3);

        List<Route> segmentCouche3 = new ArrayList<>();

        SingleRoute sr5 = new SingleRoute(edges.subList(5,6));
        SingleRoute sr6 = new SingleRoute(edges.subList(6,7));

        segmentCouche3.add(sr5);
        segmentCouche3.add(sr6);

        MultiRoute couche3 = new MultiRoute(segmentCouche3);

        segmentCouche2.add(couche3);

        MultiRoute couche2 = new MultiRoute(segmentCouche2);

        inceptionRoute.add(couche2);
        inceptionRoute.add(sr5);

        MultiRoute couche1 = new MultiRoute(inceptionRoute);

        assertEquals(1,couche1.nodeClosestTo(550));
        assertEquals(8,couche1.nodeClosestTo(5900));


    }

    @Test
    public void MultiPointAtWorking(){


        List<Edge> edges = new ArrayList<Edge>();
        List<PointCh> points = new ArrayList<PointCh>();

        float[] typicalYasuoPlayer = {0.0f,-15f,8f,178f};

        PointCh pt1 = new PointCh(SwissBounds.MAX_E,SwissBounds.MIN_N);
        PointCh pt2 = new PointCh(SwissBounds.MAX_E-5800,SwissBounds.MIN_N);
        PointCh pt3 = new PointCh(SwissBounds.MAX_E-5800,SwissBounds.MIN_N);
        PointCh pt4 = new PointCh(SwissBounds.MAX_E-8100,SwissBounds.MIN_N);
        PointCh pt5 = new PointCh(SwissBounds.MAX_E-8100,SwissBounds.MIN_N);
        PointCh pt6 = new PointCh(SwissBounds.MAX_E-9200,SwissBounds.MIN_N);
        PointCh pt7 = new PointCh(SwissBounds.MAX_E-9200,SwissBounds.MIN_N);
        PointCh pt8 = new PointCh(SwissBounds.MAX_E-11400,SwissBounds.MIN_N);
        PointCh pt9 = new PointCh(SwissBounds.MAX_E-11400,SwissBounds.MIN_N);
        PointCh pt10 = new PointCh(SwissBounds.MAX_E-13100,SwissBounds.MIN_N);

        Edge edge1 = new Edge(0,1, pt1,pt2, 5800, Functions.sampled(typicalYasuoPlayer,5800));
        Edge edge2 = new Edge(1,2, pt3, pt4, 2300, Functions.sampled(typicalYasuoPlayer,2300));
        Edge edge3 = new Edge(2,3, pt5, pt6, 1100, Functions.sampled(typicalYasuoPlayer,1100));
        Edge edge4 = new Edge(3,4, pt7, pt8, 2200, Functions.sampled(typicalYasuoPlayer,2200));
        Edge edge5 = new Edge(4,5, pt9, pt10, 1700, Functions.sampled(typicalYasuoPlayer,1700));

        Edge edge6 = new Edge(3,4, pt7, pt8, 1000, Functions.sampled(typicalYasuoPlayer,1000));
        Edge edge7 = new Edge(4,5, pt9, pt10, 1000, Functions.sampled(typicalYasuoPlayer,1000));

        points.add(pt1);
        points.add(pt2);
        points.add(pt4);
        points.add(pt6);
        points.add(pt8);
        points.add(pt10);

        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);
        edges.add(edge4);
        edges.add(edge5);

        edges.add(edge6);
        edges.add(edge7);

        List<Route> segments = new ArrayList<>();

        SingleRoute sr1 = new SingleRoute(edges.subList(0,1));
        SingleRoute sr2 = new SingleRoute(edges.subList(1,2));

        List<Route> componentsOfMulti = new ArrayList<>();

        SingleRoute sr3 = new SingleRoute(edges.subList(2,3));
        SingleRoute sr4 = new SingleRoute(edges.subList(3,5));

        componentsOfMulti.add(sr3);
        componentsOfMulti.add(sr4);

        segments.add(sr1);
        segments.add(sr2);

        MultiRoute comp = new MultiRoute(componentsOfMulti);

        segments.add(comp);

        MultiRoute mt = new MultiRoute(segments);

        assertEquals(13100,mt.length());

        List<Route> inceptionRoute = new ArrayList<>();

        inceptionRoute.add(sr1);
        inceptionRoute.add(sr2);

        List<Route> segmentCouche2 = new ArrayList<>();

        segmentCouche2.add(sr2);
        segmentCouche2.add(sr3);

        List<Route> segmentCouche3 = new ArrayList<>();

        SingleRoute sr5 = new SingleRoute(edges.subList(5,6));
        SingleRoute sr6 = new SingleRoute(edges.subList(6,7));

        segmentCouche3.add(sr5);
        segmentCouche3.add(sr6);

        MultiRoute couche3 = new MultiRoute(segmentCouche3);

        segmentCouche2.add(couche3);

        MultiRoute couche2 = new MultiRoute(segmentCouche2);

        inceptionRoute.add(couche2);
        inceptionRoute.add(sr5);

        MultiRoute couche1 = new MultiRoute(inceptionRoute);

        assertEquals(sr1.pointAt(-500),couche1.pointAt(-550));
        assertEquals(sr1.pointAt(550),couche1.pointAt(550));
        assertEquals(sr2.pointAt(200),couche1.pointAt(6000));
        assertEquals(couche2.pointAt(100),couche1.pointAt(8200));
        assertEquals(couche2.pointAt(2400),couche1.pointAt(10500));
        assertEquals(couche3.pointAt(100),couche1.pointAt(11600));
        assertEquals(sr5.pointAt(15000),couche1.pointAt(15000));


    }

}

class MultiRouteTestAya {
    @Test
    public MultiRoute multiRouteConstructorForTest(){
        List<Edge> l0 = new ArrayList<Edge>();
        List<Edge> l1 = new ArrayList<Edge>();
        List<Route> listeRoutes = new ArrayList<>();

        PointCh fromPoint0 = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh toPoint0 = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        float[] profileTab0 = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile0 = Functions.sampled(profileTab0, 5);
        Edge edge0 = new Edge(0, 1, fromPoint0, toPoint0, 5, profile0);

        PointCh fromPoint1 = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh toPoint1 = new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N);
        float[] profileTab1 = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile1 = Functions.sampled(profileTab1, 5);
        Edge edge1 = new Edge(1, 2, fromPoint1, toPoint1, 5, profile1);

        PointCh fromPoint2 = new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N);
        PointCh toPoint2 = new PointCh(SwissBounds.MIN_E + 20, SwissBounds.MIN_N);
        float[] profileTab2 = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile2 = Functions.sampled(profileTab2, 5);
        Edge edge2 = new Edge(2, 3, fromPoint2, toPoint2, 10, profile2);

        PointCh fromPoint3 = new PointCh(SwissBounds.MIN_E + 20, SwissBounds.MIN_N);
        PointCh toPoint3 = new PointCh(SwissBounds.MIN_E + 28, SwissBounds.MIN_N);
        float[] profileTab3 = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile3 = Functions.sampled(profileTab3, 5);
        Edge edge3 = new Edge(3, 4, fromPoint3, toPoint3, 15, profile3);

        l0.add(edge0);
        l0.add(edge1);
        listeRoutes.add(new SingleRoute(l0));
        l1.add(edge2);
        l1.add(edge3);
        listeRoutes.add(new SingleRoute(l1));
        return new MultiRoute(listeRoutes);
    }

    @Test
    public void indexOfSegmentAtTest(){
        MultiRoute m = multiRouteConstructorForTest();
        //Cas sup à la length
        int expected0 = 1;
        int actual0 = m.indexOfSegmentAt(SwissBounds.MIN_E + 25);
        assertEquals(expected0, actual0);
        //Cas position négative
        int expected1 = 0;
        int actual1 = m.indexOfSegmentAt(-25);
        assertEquals(expected1, actual1);
        //Cas position nulle
        int expected2 = expected1;
        int actual2 = m.indexOfSegmentAt(0);
        assertEquals(expected2, actual2);
        //Cas position = length
        int expected3 = expected0;
        int actual3 = m.indexOfSegmentAt(25);
        assertEquals(expected3, actual3);
        //Cas normal
        int expected4 = 1;
        int actual4 = m.indexOfSegmentAt(25);
        assertEquals(expected4, actual4);
    }

    @Test
    public void lengthTest(){
        MultiRoute m = multiRouteConstructorForTest();
        double expected0 = 35.0;
        double actual0 = m.length();
        assertEquals(expected0, actual0);
    }

    @Test
    public void edgesTest(){
        List<Edge> l0 = new ArrayList<Edge>();
        List<Edge> l1 = new ArrayList<Edge>();
        List<Edge> allEdges = new ArrayList<>();
        List<Route> listeRoutes = new ArrayList<>();

        PointCh fromPoint0 = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh toPoint0 = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        float[] profileTab0 = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile0 = Functions.sampled(profileTab0, 5);
        Edge edge0 = new Edge(0, 1, fromPoint0, toPoint0, 5, profile0);

        PointCh fromPoint1 = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh toPoint1 = new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N);
        float[] profileTab1 = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile1 = Functions.sampled(profileTab1, 5);
        Edge edge1 = new Edge(1, 2, fromPoint1, toPoint1, 5, profile1);

        PointCh fromPoint2 = new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N);
        PointCh toPoint2 = new PointCh(SwissBounds.MIN_E + 20, SwissBounds.MIN_N);
        float[] profileTab2 = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile2 = Functions.sampled(profileTab2, 5);
        Edge edge2 = new Edge(2, 3, fromPoint2, toPoint2, 10, profile2);

        PointCh fromPoint3 = new PointCh(SwissBounds.MIN_E + 20, SwissBounds.MIN_N);
        PointCh toPoint3 = new PointCh(SwissBounds.MIN_E + 28, SwissBounds.MIN_N);
        float[] profileTab3 = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile3 = Functions.sampled(profileTab3, 5);
        Edge edge3 = new Edge(3, 4, fromPoint3, toPoint3, 15, profile3);

        l0.add(edge0);
        l0.add(edge1);
        listeRoutes.add(new SingleRoute(l0));
        l1.add(edge2);
        l1.add(edge3);
        listeRoutes.add(new SingleRoute(l1));
        MultiRoute m = new MultiRoute(listeRoutes);

        allEdges.add(edge0);
        allEdges.add(edge1);
        allEdges.add(edge2);
        allEdges.add(edge3);
        List<Edge> expected0 = allEdges;
        List<Edge> actual0 = m.edges();
        assertEquals(expected0, actual0);
    }

    @Test
    public void pointsTest(){
        MultiRoute m = multiRouteConstructorForTest();
        List<PointCh> allPoints = new ArrayList<>();

        PointCh fromPoint0 = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh fromPoint1 = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh fromPoint2 = new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N);
        PointCh fromPoint3 = new PointCh(SwissBounds.MIN_E + 20, SwissBounds.MIN_N);
        PointCh toPoint3 = new PointCh(SwissBounds.MIN_E + 28, SwissBounds.MIN_N);

        allPoints.add(fromPoint0);
        allPoints.add(fromPoint1);
        allPoints.add(fromPoint2);
        allPoints.add(fromPoint3);
        allPoints.add(toPoint3);

        List<PointCh> expected0 = allPoints;
        List<PointCh> actual0 = m.points();
        assertEquals(expected0, actual0);
    }

    @Test
    public void pointAtTest(){
        MultiRoute m = multiRouteConstructorForTest();
        //Cas normal
        PointCh expected0 = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh actual0 = m.pointAt(5);
        assertEquals(expected0, actual0);
        //Cas sup à la length
        PointCh expected1 = new PointCh(SwissBounds.MIN_E + 28, SwissBounds.MIN_N);
        PointCh actual1 = m.pointAt(SwissBounds.MIN_E + 5);
        assertEquals(expected1, actual1);
        //Cas position négative
        PointCh expected2 = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh actual2 = m.pointAt(-30);
        assertEquals(expected2, actual2);
        //Cas position nulle
        PointCh expected3 = expected2;
        PointCh actual3 = m.pointAt(0);
        assertEquals(expected3, actual3);
        //Cas position = length
        PointCh expected4 = expected1;
        PointCh actual4 = m.pointAt(35.0);
        assertEquals(expected4, actual4);
        //Cas normal 2.0
        PointCh expected5 = new PointCh(SwissBounds.MIN_E + 20, SwissBounds.MIN_N);
        PointCh actual5 = m.pointAt(20.0);
        assertEquals(expected5, actual5);
    }

    @Test
    public void elevationAtTest(){
        List<Edge> l0 = new ArrayList<Edge>();
        List<Edge> l1 = new ArrayList<Edge>();
        List<Route> listeRoutes = new ArrayList<>();

        PointCh fromPoint0 = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh toPoint0 = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        float[] profileTab0 = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile0 = Functions.sampled(profileTab0, 5);
        Edge edge0 = new Edge(0, 1, fromPoint0, toPoint0, 5, profile0);

        PointCh fromPoint1 = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh toPoint1 = new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N);
        float[] profileTab1 = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile1 = Functions.sampled(profileTab1, 5);
        Edge edge1 = new Edge(1, 2, fromPoint1, toPoint1, 5, profile1);

        PointCh fromPoint2 = new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N);
        PointCh toPoint2 = new PointCh(SwissBounds.MIN_E + 20, SwissBounds.MIN_N);
        float[] profileTab2 = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile2 = Functions.sampled(profileTab2, 5);
        Edge edge2 = new Edge(2, 3, fromPoint2, toPoint2, 10, profile2);

        PointCh fromPoint3 = new PointCh(SwissBounds.MIN_E + 20, SwissBounds.MIN_N);
        PointCh toPoint3 = new PointCh(SwissBounds.MIN_E + 28, SwissBounds.MIN_N);
        float[] profileTab3 = {500f, 505f, 510f, 505f, 510f};
        DoubleUnaryOperator profile3 = Functions.sampled(profileTab3, 5);
        Edge edge3 = new Edge(3, 4, fromPoint3, toPoint3, 15, profile3);

        l0.add(edge0);
        l0.add(edge1);
        listeRoutes.add(new SingleRoute(l0));
        l1.add(edge2);
        l1.add(edge3);
        listeRoutes.add(new SingleRoute(l1));
        MultiRoute m = new MultiRoute(listeRoutes);

        //Cas normal
        double expected0 = edge1.elevationAt(3);
        double actual0 = m.elevationAt(8);
        assertEquals(expected0, actual0);
        //Cas sup à la length
        double expected1 = edge3.elevationAt(15);
        double actual1 = m.elevationAt(SwissBounds.MIN_E + 5);
        assertEquals(expected1, actual1);
        //Cas position négative
        double expected2 = edge0.elevationAt(0);
        double actual2 = m.elevationAt(-30);
        assertEquals(expected2, actual2);
        //Cas position nulle
        double expected3 = expected2;
        double actual3 = m.elevationAt(0);
        assertEquals(expected3, actual3);
        //Cas position = length
        double expected4 = expected1;
        double actual4 = m.elevationAt(35.0);
        assertEquals(expected4, actual4);
        //Cas normal 2.0
        double expected5 = edge2.elevationAt(7);
        double actual5 = m.elevationAt(17);
        assertEquals(expected5, actual5);
    }

    @Test
    public void nodeClosestToTest(){
        MultiRoute m = multiRouteConstructorForTest();
        //Cas normal
        int expected0 = 3;
        int actual0 = m.nodeClosestTo(25);
        assertEquals(expected0, actual0);
        //Cas sup à la length
        int expected1 = 4;
        int actual1 = m.nodeClosestTo(SwissBounds.MIN_E + 5);
        assertEquals(expected1, actual1);
        //Cas position négative
        int expected2 = 0;
        int actual2 = m.nodeClosestTo(-30);
        assertEquals(expected2, actual2);
        //Cas position nulle
        int expected3 = expected2;
        int actual3 = m.nodeClosestTo(0);
        assertEquals(expected3, actual3);
        //Cas position = length
        int expected4 = expected1;
        int actual4 = m.nodeClosestTo(35.0);
        assertEquals(expected4, actual4);
        //Cas normal 2.0
        int expected5 = 2;
        int actual5 = m.nodeClosestTo(12);
        assertEquals(expected5, actual5);
    }

    @Test
    public void pointClosestToTest(){
        MultiRoute m = multiRouteConstructorForTest();
        //itinéraire horizontal
        //Cas normal
        PointCh point0 = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N + 1);
        PointCh expected0 = new PointCh(SwissBounds.MIN_E + 5, SwissBounds.MIN_N);
        PointCh actual0 = m.pointClosestTo(point0).point();
        assertEquals(expected0, actual0);
        //Cas point.e() sup à length
        PointCh point1 = new PointCh(SwissBounds.MIN_E + 40, SwissBounds.MIN_N + 40);
        PointCh expected1 = new PointCh(SwissBounds.MIN_E + 28, SwissBounds.MIN_N);
        PointCh actual1 = m.pointClosestTo(point1).point();
        assertEquals(expected1, actual1);
        //Cas point en dessus du premier de l'itinéraire
        PointCh point2 = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 40);
        PointCh expected2 = new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N);
        PointCh actual2 = m.pointClosestTo(point2).point();
        assertEquals(expected2, actual2);
        //Cas point en dessus du dernier de l'itinéraire
        PointCh point3 = new PointCh(SwissBounds.MIN_E + 28, SwissBounds.MIN_N + 40);
        PointCh expected3 = new PointCh(SwissBounds.MIN_E + 28, SwissBounds.MIN_N);
        PointCh actual3 = m.pointClosestTo(point3).point();
        //Ne devrait pas passer: GOOD
        //assertEquals(expected3, actual3);
        //Cas normal 2.0
        PointCh point4 = new PointCh(SwissBounds.MIN_E + 22, SwissBounds.MIN_N + 40);
        PointCh expected4 = new PointCh(SwissBounds.MIN_E + 20, SwissBounds.MIN_N);
        PointCh actual4 = m.pointClosestTo(point4).point();
        //Ne devrait pas passer: GOOD
        //assertEquals(expected4, actual4);
        //Cas point sur l'itinéraire
        PointCh point5 = new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N);
        PointCh expected5 = new PointCh(SwissBounds.MIN_E + 10, SwissBounds.MIN_N);
        PointCh actual5 = m.pointClosestTo(point5).point();
        assertEquals(expected5, actual5);
    }

    @Test
    public void pointClosestToTest2()
    {
        MultiRoute mr1 = new MultiRoute(new ArrayList<Route>(Arrays.asList(
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(3, 4, pointCreator(0, 0), pointCreator(10, 0), 10, null),
                                new Edge(4, 5, pointCreator(10, 0), pointCreator(20, 0), 10, null))
                )
                ),
                new SingleRoute(new ArrayList<>(
                        Arrays.asList(
                                new Edge(5, 6, pointCreator(20, 0), pointCreator(30, 0), 10, null),
                                new Edge(6, 7, pointCreator(30, 0), pointCreator(40, 0), 10, null))
                )
                )
        )));
        for (int i = 0; i < 10; i++) {
            assertEquals(i + SwissBounds.MIN_E, mr1.pointClosestTo(pointCreator(i, 1)).point().e());
        }
    }

    private PointCh pointCreator(int e, int n)
    {
        return new PointCh(SwissBounds.MIN_E + e, SwissBounds.MIN_N + n);
    }
}

class MultiRouteTestMaiLinh {

    private static final int ORIGIN_N = 1_200_000;
    private static final int ORIGIN_E = 2_600_000;
    private static final double EDGE_LENGTH = 100.25;
    // Sides of triangle used for "sawtooth" edges (shape: /\/\/\…)
    private static final double TOOTH_EW = 1023;
    private static final double TOOTH_NS = 64;
    private static final double TOOTH_LENGTH = 1025;
    private static final double TOOTH_ELEVATION_GAIN = 100d;
    private static final double TOOTH_SLOPE = TOOTH_ELEVATION_GAIN / TOOTH_LENGTH;
    private static List<Edge> verticalEdges(int edgesCount, int start) {
        var edges = new ArrayList<Edge>(edgesCount);
        for (int i = start; i < start + edgesCount; i += 1) {
            var p1 = new PointCh(ORIGIN_E, ORIGIN_N + i * EDGE_LENGTH);
            var p2 = new PointCh(ORIGIN_E, ORIGIN_N + (i + 1) * EDGE_LENGTH);
            edges.add(new Edge(i, i + 1, p1, p2, EDGE_LENGTH, x -> Double.NaN));
        }
        return Collections.unmodifiableList(edges);
    }
    private static List<Edge> verticalEdgesConstantProfile(int edgesCount, int start) {
        var edges = new ArrayList<Edge>(edgesCount);
        for (int i = start; i < start + edgesCount; i += 1) {
            var p1 = new PointCh(ORIGIN_E, ORIGIN_N + i * EDGE_LENGTH);
            var p2 = new PointCh(ORIGIN_E, ORIGIN_N + (i + 1) * EDGE_LENGTH);
            edges.add(new Edge(i, i + 1, p1, p2, EDGE_LENGTH, x -> 10d));
        }
        return Collections.unmodifiableList(edges);
    }
    private static List<Edge> verticalEdgesGivenConstantProfile(int edgesCount, int start, double profile) {
        var edges = new ArrayList<Edge>(edgesCount);
        for (int i = start; i < start + edgesCount; i += 1) {
            var p1 = new PointCh(ORIGIN_E, ORIGIN_N + i * EDGE_LENGTH);
            var p2 = new PointCh(ORIGIN_E, ORIGIN_N + (i + 1) * EDGE_LENGTH);
            edges.add(new Edge(i, i + 1, p1, p2, EDGE_LENGTH, x -> profile));
        }
        return Collections.unmodifiableList(edges);
    }
    private static List<Edge> sawToothEdges(int edgesCount) {
        var edges = new ArrayList<Edge>(edgesCount);
        for (int i = 0; i < edgesCount; i += 1) {
            var p1 = sawToothPoint(i);
            var p2 = sawToothPoint(i + 1);
            var startingElevation = i * TOOTH_ELEVATION_GAIN;
            edges.add(new Edge(i, i + 1, p1, p2, TOOTH_LENGTH, x -> startingElevation + x * TOOTH_SLOPE));
        }
        return Collections.unmodifiableList(edges);
    }
    private static PointCh sawToothPoint(int i) {
        return new PointCh(
                ORIGIN_E + TOOTH_EW * i,
                ORIGIN_N + ((i & 1) == 0 ? 0 : TOOTH_NS));
    }
    @Test
    void singleRouteConstructorThrowsOnEmptyEdgeList() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SingleRoute(List.of());
        });
    }
    @Test
    void indexOfSegmentAt1() {
        var route1 = new SingleRoute(verticalEdges(3,0));
        var route2 = new SingleRoute(verticalEdges(2,3));
        List routes = new ArrayList();
        routes.add(route1);
        routes.add(route2);
        var multiroute1 = new MultiRoute(routes);
        var actual1 = multiroute1.indexOfSegmentAt(250);
        assertEquals(0, actual1);
    }
    @Test
    void indexOfSegmentAt2() {
        var route1 = new SingleRoute(verticalEdges(3,0));
        var route2 = new SingleRoute(verticalEdges(2,3));
        List routes = new ArrayList();
        routes.add(route1);
        routes.add(route2);
        var multiroute1 = new MultiRoute(routes);
        List routes2 = new ArrayList();
        routes2.add(new SingleRoute(verticalEdges(3,5)));
        routes2.add(new SingleRoute(verticalEdges(4,8)));
        var multiroute2 = new MultiRoute(routes2);
        List multiroutes = new ArrayList();
        multiroutes.add(multiroute1);
        multiroutes.add(multiroute2);

        var multiroute = new MultiRoute(multiroutes);

        assertEquals(0, multiroute.indexOfSegmentAt(150));
        assertEquals(1, multiroute.indexOfSegmentAt(301));
        assertEquals(2, multiroute.indexOfSegmentAt(510));
        assertEquals(2, multiroute.indexOfSegmentAt(800));
        assertEquals(3, multiroute.indexOfSegmentAt(1000));
    }
    @Test
    void length() {
        var route1 = new SingleRoute(verticalEdges(3,0));
        var route2 = new SingleRoute(verticalEdges(2,3));

        List routes = new ArrayList();
        routes.add(route1);
        routes.add(route2);
        var multiroute1 = new MultiRoute(routes);

        List routes2 = new ArrayList();
        routes2.add(new SingleRoute(verticalEdges(3,5)));
        routes2.add(new SingleRoute(verticalEdges(4,8)));
        var multiroute2 = new MultiRoute(routes2);

        List multiroutes = new ArrayList();
        multiroutes.add(multiroute1);
        multiroutes.add(multiroute2);
        var multiroute = new MultiRoute(multiroutes);

        assertEquals(1203, multiroute.length());
    }
    @Test
    void points() {
        var route1 = new SingleRoute(verticalEdges(3,0));
        var route2 = new SingleRoute(verticalEdges(2,3));

        List routes = new ArrayList();
        routes.add(route1);
        routes.add(route2);
        var multiroute1 = new MultiRoute(routes);

        List routes2 = new ArrayList();
        routes2.add(new SingleRoute(verticalEdges(3, 5)));
        routes2.add(new SingleRoute(verticalEdges(4,8)));
        var multiroute2 = new MultiRoute(routes2);

        List multiroutes = new ArrayList();
        multiroutes.add(multiroute1);
        multiroutes.add(multiroute2);
        var multiroute = new MultiRoute(multiroutes);
    }
    @Test
    void pointAt() {
        var route1 = new SingleRoute(verticalEdges(3,0));
        var route2 = new SingleRoute(verticalEdges(2,3));

        List routes = new ArrayList();
        routes.add(route1);
        routes.add(route2);
        var multiroute1 = new MultiRoute(routes);

        List routes2 = new ArrayList();
        routes2.add(new SingleRoute(verticalEdges(3,5)));
        routes2.add(new SingleRoute(verticalEdges(4,8)));
        var multiroute2 = new MultiRoute(routes2);

        List multiroutes = new ArrayList();
        multiroutes.add(multiroute1);
        multiroutes.add(multiroute2);
        var multiroute = new MultiRoute(multiroutes);

        PointCh expctd1 = new PointCh(2_600_000, 1_200_150);
        assertEquals(expctd1, multiroute.pointAt(150));
        PointCh expctd2 = new PointCh(2_600_000, 1_200_301);
        assertEquals(expctd2, multiroute.pointAt(301));
        PointCh expctd3 = new PointCh(2_600_000, 1_200_510);
        assertEquals(expctd3, multiroute.pointAt(510));
        PointCh expctd4 = new PointCh(2_600_000, 1_200_800);
        assertEquals(expctd4, multiroute.pointAt(800));
        PointCh expctd5 = new PointCh(2_600_000, 1_201000);
        assertEquals(expctd5, multiroute.pointAt(1000));
        PointCh expctd6 = multiroute.edges().get(11).toPoint();
        assertEquals(expctd6, multiroute.pointAt(1300));
    }
    @Test
    void elevationAtNaN() {
        var route1 = new SingleRoute(verticalEdges(3,0));
        var route2 = new SingleRoute(verticalEdges(2,3));
        List routes = new ArrayList();
        routes.add(route1);
        routes.add(route2);
        var multiroute1 = new MultiRoute(routes);

        List routes2 = new ArrayList();
        routes2.add(new SingleRoute(verticalEdges(3,5)));
        routes2.add(new SingleRoute(verticalEdges(4,8)));
        var multiroute2 = new MultiRoute(routes2);

        List multiroutes = new ArrayList();
        multiroutes.add(multiroute1);
        multiroutes.add(multiroute2);
        var multiroute = new MultiRoute(multiroutes);

        assertEquals(Double.NaN, multiroute.elevationAt(0));
        assertEquals(Double.NaN, multiroute.elevationAt(150));
        assertEquals(Double.NaN, multiroute.elevationAt(301));
        assertEquals(Double.NaN, multiroute.elevationAt(510));
        assertEquals(Double.NaN, multiroute.elevationAt(800));
        assertEquals(Double.NaN, multiroute.elevationAt(-10));
        assertEquals(Double.NaN, multiroute.elevationAt(1000));
        assertEquals(Double.NaN, multiroute.elevationAt(10000));

    }

    /*
    @Test
    void elevationAtConstant()
    {

        var route1 = new SingleRoute(verticalEdgesCo
                n
                stantProfile(3,0));
        var route2 = new SingleRoute(verticalEd
                g
                esConstantProfile(2,3));
        List routes = n
        e
        w ArrayList();
        routes.add(route1);


        routes.add(route2);
        var multiroute1 = ne
        w
        MultiRoute(routes);
        List routes2 = new ArrayList();
        routes2.add(new SingleRoute(verticalEdgesConstantProfile(3,5)));
        r
                o
        utes2.add(new SingleRoute(verticalEdgesConstantProfile(4,8)));
        v
                a
        r multiroute2 = new MultiRoute(routes2);
        Lis
                t
        multiroutes = new ArrayList();
        multiroutes.add(multi
                r
                oute1);
        multiroutes.add(multiroute2);
        var multiroute =
                n
        ew MultiRoute(multiroutes);
        assertEquals(10d, multiroute.elevationAt(
                0
        ));
        assertEquals(10d, multiroute.elevationAt(150));
        assertEquals
                (
                        10d, mult
                        i
                        route.elevationAt(301));
        assertEquals(10d,
                m
                ultir
                o
                ute.elevationAt(510));
        assertEquals(10d, multiroute.elevationAt(800));

        a
        ssertEquals(10d, multiroute.elevationAt(-10));


        assertEquals(10d, multiroute.elevationAt(1000));
        as
                s
        ertEquals(10d, multiroute.elevationAt(10000));
    }
    @Test
    void

    elevationAtConstantGiving() {
        var route1 = new SingleRoute(verticalEd
                g
                esGivenConstantProfile(3,0, 10));
        var route2 = new SingleRoute(ve
                r
                ticalEdge
                s
                GivenConstantProfile(2,3, 5));
        List routes
                =
                new
                        A
        rrayList();
        routes.add(route1);
        routes.add(route2);
        var multiroute1 = new MultiRoute
                (
                        routes);
        List routes2 = new ArrayList();


        routes2.add(new SingleRoute(verticalEdgesGivenConstantPro
                f
                ile(3,5, 20)));
        routes2.add(new SingleRoute(verticalEdgesGivenC
                o
                nstantProfile(4,8, 15)));
        var multiroute2 = new MultiRoute(routes2);

        List multiroutes = new ArrayList();
        multiroutes.add(multiroute
                1
        );


        multiroutes.add(multiroute2);
        var multirou
        t
                e = n
        e
        w MultiRoute(multiroutes);
        assertEquals(10, multirout
                e
                        .elevationAt(0));
        assertEquals(10, multirout
                e
                        .elevationAt(150));
        assertEquals(5, multi
                r
                oute.elevationAt(301));
        assert
                E
        quals(20, multiroute.elevationAt(510));


        assertEquals(20, multiroute.elevationAt(800));
        a
                s
        sertEquals(10d, multiroute.elevationAt(-10));
        assertEquals(15, multiroute.elevationAt(1000));


        assert
                E
        quals(15d, multiroute.elevationAt(10000));
    }


    @Tes
    t

    void elevationAt() {
        var edgesCount1

                = 4;
        var route = ne
        w
        SingleRoute(sawToothEdges(edgesCount1))
        ;
        for (int i = 0; i < edgesCount1; i += 1) {
            for (double p = 0; p < 1; p += 0.125) {
                var pos = (i + p) * TOOTH_LENGTH;
                var expectedElevation = (i + p) * TOOTH_ELEVATION_GAIN;
                assertEquals(expectedElevation, route.elevationAt(pos));
            }
        }
        var edgesCount2 = 5;
        var route2 = new SingleRoute(sawToothEdges(edgesCount2));
        for (int i = 0; i < edgesCount2; i += 1) {
            for (double p = 0; p < 1; p += 0.125) {
                var pos = (i + p) * TOOTH_LENGTH;
                var expectedElevation = (i + p) * TOOTH_ELEVATION_GAIN;
                assertEquals(expectedElevation, route.elevationAt(pos));
            }
        }
        assertEquals(0, route.elevationAt(-1e6));
        assertEquals(edgesCount1 * TOOTH_ELEVATION_GAIN, route.elevationAt(+1e6));
        Route multiroute = new MultiRoute(List.of(route, route2));
    }
    */
    @Test
    void nodeClosestTo() {
    }
    @Test
    void pointClosestTo() {
    }

}
