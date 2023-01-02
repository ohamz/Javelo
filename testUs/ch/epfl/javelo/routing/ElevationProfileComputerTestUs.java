package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;
import test.TestRandomizer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.DoubleUnaryOperator;

import static ch.epfl.javelo.routing.ElevationProfileComputer.elevationProfile;
import static test.TestRandomizer.newRandom;
import static java.lang.Float.NaN;
import static org.junit.jupiter.api.Assertions.*;

class ElevationProfileComputerTestUs {

    @Test
    void elevationProfileThrowsError() {
        Route routeRandom = null;

        assertThrows(IllegalArgumentException.class, ()-> ElevationProfileComputer.elevationProfile(routeRandom, -275));
        assertThrows(IllegalArgumentException.class, ()-> ElevationProfileComputer.elevationProfile(routeRandom, 0));
    }

    @Test
    void elevationProfileComputerWorks() {
        PointCh point1 = new PointCh(2535123, 1152123);
        PointCh point2 = new PointCh(2535234, 1152234);
        PointCh point3 = new PointCh(2535345, 1152345);
        PointCh point4 = new PointCh(2535456, 1152456);
        PointCh point5 = new PointCh(2535567, 1152567);
        PointCh point6 = new PointCh(2535678, 1152678);

        double xMax = 156.97770542341354;
        DoubleUnaryOperator profile1 = Functions.sampled(new float[]
                {NaN, NaN, -0.125f}, xMax);
        DoubleUnaryOperator profile2 = Functions.sampled(new float[]
                {-0.0625f, NaN, -0.125f, -0.0625f, -0.03125f}, xMax);
        DoubleUnaryOperator profile3 = Functions.sampled(new float[]
                {NaN, NaN, NaN, NaN}, xMax);
        DoubleUnaryOperator profile4 = Functions.sampled(new float[]
                {-0.125f, -0.0625f}, xMax);
        DoubleUnaryOperator profile5 = Functions.sampled(new float[]
                {-0.0625f, NaN, NaN}, xMax);

        Edge edge1 = new Edge(0, 1, point1, point2, point1.distanceTo(point2), profile1);
        Edge edge2 = new Edge(1, 2, point2, point3, point2.distanceTo(point3), profile2);
        Edge edge3 = new Edge(2, 3, point3, point4, point3.distanceTo(point4), profile3);
        Edge edge4 = new Edge(3, 4, point4, point5, point4.distanceTo(point5), profile4);
        Edge edge5 = new Edge(4, 5, point5, point6, point5.distanceTo(point6), profile5);

        List<Edge> edges = new ArrayList<>();
        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);
        edges.add(edge4);
        edges.add(edge5);

        SingleRoute singleRoute = new SingleRoute(edges);

        double expectedLength = singleRoute.length();
        float[] expectedElevationSamples = new float[]
                {       -0.125f, -0.125f, -0.125f, -0.0625f,
                        -0.09375f, -0.125f, -0.0625f, -0.03125f,
                        -0.05f, -0.06875f, -0.0875f, -0.10625f
                        -0.125f, -0.0625f, -0.0625f, -0.0625f, -0.0625f};

        ElevationProfile expectedElevationProfile = new ElevationProfile(expectedLength, expectedElevationSamples);
        ElevationProfile actualElevationProfile = ElevationProfileComputer.elevationProfile(singleRoute, xMax);

        int index = 0;
        for (double d = 0; d < singleRoute.length(); d+=5) {
            if (expectedElevationProfile.elevationAt(d) != actualElevationProfile.elevationAt(d))
                ++index;
        }
        boolean equal = index == 0;
        //assertTrue(equal);
    }
}

class ElevationProfileComputerMMOalex {
    PointCh A = new PointCh(2600123, 1200456);
    PointCh B = new PointCh(2600456, 1200789);
    PointCh C = new PointCh(2600789, 1200123);
    PointCh D = new PointCh(2601000, 1201000);
    PointCh E = new PointCh(2601283, 1201110);
    PointCh F = new PointCh(2602000, 1201999);
    PointCh G = new PointCh(2602500, 1201010);
    PointCh H = new PointCh(2602877, 1200829);
    PointCh I = new PointCh(2603000, 1201086);
    PointCh J = new PointCh(2603124, 1198878);

    Edge edge1 = new Edge(1, 2, A, B, A.distanceTo(B), x -> 4);
    Edge edge2 = new Edge(3, 4, B, C, B.distanceTo(C), x -> Double.NaN);
    Edge edge3 = new Edge(4, 5, C, D, C.distanceTo(D), x -> 6.);
    Edge edge4 = new Edge(5, 6, D, E, D.distanceTo(E), x -> 9.5);
    Edge edge5 = new Edge(7, 8, E, F, E.distanceTo(F), x -> Double.NaN);
    Edge edge6 = new Edge(9, 10, F, G, F.distanceTo(G), x -> Double.NaN);
    Edge edge7 = new Edge(10, 11, G, H, G.distanceTo(H), x -> 15);
    Edge edge8 = new Edge(11, 12, H, I, H.distanceTo(I), x -> 16);
    Edge edge9 = new Edge(12, 13, I, J, I.distanceTo(J), x -> Double.NaN);

    public SingleRoute ourRoute() {

        List<Edge> edges = new ArrayList<>();
        edges.add(edge1);edges.add(edge2);edges.add(edge3);edges.add(edge4);
        edges.add(edge5);edges.add(edge6);edges.add(edge7);edges.add(edge8);edges.add(edge9);

        return new SingleRoute(edges);
    }

    @Test
    void test1(){
        float[] tab = { 4.0f, 5.0f, 6.0f, 8.25f, 10.5f, 12.75f, 15.0f, 15.0f, 15.0f, 15.0f };
        ElevationProfile expected = new ElevationProfile(ourRoute().length(), tab );
        assertEquals(expected, ElevationProfileComputer.elevationProfile(ourRoute(), ourRoute().length()/tab.length));
    }

    // Test Louis

    @Test
    void elevationProfileThrowsWhenIllegalStep() throws IOException {
        Graph routeGraph = Graph.loadFrom(Path.of("lausanne"));
        Edge edge1 = Edge.of(routeGraph, 0, 0, routeGraph.edgeTargetNodeId(0));
        List<Edge> edges = new ArrayList<>();
        edges.add(edge1);
        Route coucou = new SingleRoute(edges);
        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfileComputer.elevationProfile(new SingleRoute(new ArrayList<Edge>()), 5);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfileComputer.elevationProfile(coucou, 0.0);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfileComputer.elevationProfile(coucou, -1.0);
        });

    }
    @Test
    void elevationProfile() throws IOException {
        Graph routeGraph = Graph.loadFrom(Path.of("lausanne"));
        Edge edge1 = Edge.of(routeGraph, 0, 0, routeGraph.edgeTargetNodeId(0));
        Edge edge2 = Edge.of(routeGraph, 1, 1, routeGraph.edgeTargetNodeId(1));
        List<Edge> edges = new ArrayList<>();
        edges.add(edge1);
        edges.add(edge2);
        Route coucou = new SingleRoute(edges);
        ElevationProfile wesh = ElevationProfileComputer.elevationProfile(coucou, 1.0);
        int nbOfProfiles = (int)Math.ceil(coucou.length()) + 1;
        double stepLengthOfCoucou = nbOfProfiles/coucou.length();
        System.out.println("Statistiques de la route coucou, qui va du noeud 0 à 2, (edgeID 0 et 1)");
        System.out.println("Gain d'élévation de la route 'coucou' :" + routeGraph.edgeElevationGain(0));
        System.out.println("Descente totale de la route 'coucou' :" + wesh.totalDescent());
        System.out.println("Montée totale de la route 'coucou' :" + wesh.totalAscent());
        System.out.println("Altitude MAX de la route 'coucou' : " + wesh.maxElevation());
        System.out.println("Altitude MIN de la route 'coucou': " + wesh.minElevation());
        System.out.println("échantillons de la route 'coucou' (MaxPas = 1.0):");
        for (int i = 0; i <= nbOfProfiles; i++) {
            System.out.println(i + " : " + wesh.elevationAt(i*stepLengthOfCoucou));
        }
        double routeLength = 20; // 2meters
      /*  Edge edge = new Edge(0, 10, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 20), 20, Functions.sampled(
                new float[]{1f, 1f, 1f, 3f, Float.NaN, 7f, Float.NaN, Float.NaN, Float.NaN, 2f, Float.NaN},
                routeLength));
        List<Edge> salut = new ArrayList<>();
        salut.add(edge);
        float[] elevationProfileComputer1Excepcted = new float[]{1f, 1f, 1f, 3f, 5f, 7f, 5.75f, 4.5f, 3.25f, 2f, 2f};
        Route MDRR = new SingleRoute(salut);

       */
        List<float[]> lesTableaux = new ArrayList<>();
        float[][] lesEdges = new float[5][];
        double[] longueurs = new double[5];
        lesEdges[0] = new float[]{2f, 3F, 3f};
        longueurs[0] = 4;
        lesTableaux.add(lesEdges[0]);
        lesEdges[1] = new float[]{Float.NaN, Float.NaN, Float.NaN, Float.NaN};
        longueurs[1] = 6;
        lesTableaux.add(lesEdges[1]);
        lesEdges[2] = new float[]{3f, 4f, 2f};
        longueurs[2] = 4;
        lesTableaux.add(lesEdges[2]);
        lesEdges[3] = new float[]{Float.NaN, Float.NaN, Float.NaN};
        longueurs[3] =4;
        lesTableaux.add(lesEdges[3]);
        lesEdges[4] = new float[]{0f, 1f, 2f, 3f, 4f};
        longueurs[4] = 8;
        lesTableaux.add(lesEdges[4]);
        int nbOfProfilesOfLaRoute = (int) Math.ceil(26.0/2) + 1;
        double stepLengthOfLaRoute = 26.0 / nbOfProfiles;
        Route laRoute = routeCreator(lesTableaux, longueurs);
        ElevationProfile elevationRoute = ElevationProfileComputer.elevationProfile(laRoute, 1.0);
        System.out.println("============");
        System.out.println("Statistiques de la Route laRoute tkt ça marche");
        System.out.println("Descente totale de la route 'laRoute' :" + elevationRoute.totalDescent());
        System.out.println("Montée totale de la route 'laRoute' :" + elevationRoute.totalAscent());
        System.out.println("Altitude MAX de la route 'laRoute' : " + elevationRoute.maxElevation());
        System.out.println("Altitude MIN de la route 'laRoute': " + elevationRoute.minElevation());
        System.out.println("échantillons de la route 'laRoute' (MaxPas = 1.0):" );
        for (int i = 0; i < nbOfProfilesOfLaRoute; i++) {
            System.out.println(i + " : " + elevationRoute.elevationAt(i * stepLengthOfLaRoute));
        }

        //    ElevationProfile expected = new ElevationProfile(20, elevationProfileComputer1Excepcted);
        //  ElevationProfile actual = ElevationProfileComputer.elevationProfile(MDRR, 0.01);
        //elevationTests(expected, actual);
        ElevationProfile weakActual = ElevationProfileComputer.elevationProfile(routeCreator(lesTableaux, longueurs), 0.001);
        ElevationProfile bigBossExpected = new ElevationProfile(26.0, new float[]{2f, 3f, 3f, 3f, 3f, 3f, 4f, 2f, 1f, 0f, 1f, 2f, 3f, 4f});
        elevationTests(bigBossExpected, weakActual);
    }

    void elevationTests (ElevationProfile uno, ElevationProfile dos){
        System.out.println("-> Tests avec les valeurs attendues (delta = 1e-2)");
        System.out.print("maxElevation...");
        assertEquals(uno.maxElevation(), dos.maxElevation(), 0.01);
        System.out.println("passed!");
        System.out.print("minElevation...");
        assertEquals(uno.minElevation(), dos.minElevation(), 0.01);
        System.out.println("passed!");
        System.out.print("totalAscent...");
        assertEquals(uno.totalAscent(), dos.totalAscent(), 0.01);
        System.out.println("passed!");
        System.out.print("totalDescent...");
        assertEquals(uno.totalDescent(), dos.totalDescent(), 0.01);
        System.out.println("passed!");
        System.out.print("ElevationAt...");
        Random coucou = new Random(2002);
        for (int i = 0; i < 100; i++) {
            double salut = coucou.nextDouble(uno.length());
            System.out.println("position : " + salut);
            assertEquals(uno.elevationAt(salut), dos.elevationAt(salut), 0.1);
        }
        System.out.print("passed!");
    }
    private Route routeCreator(List<float[]> slt, double[] longueurs) {
        double sum =0;
        List<Edge> listePourLesRoutes = new ArrayList<>();
        for (double longueur : longueurs) {
            sum += longueur;
        }
        for (int i = 0; i < slt.size(); i++) {
            listePourLesRoutes.add(new Edge(0, 10, new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N),
                    new PointCh(SwissBounds.MIN_E, SwissBounds.MIN_N + 20), longueurs[i], Functions.sampled(slt.get(i), longueurs[i])));
        }
        return new SingleRoute(listePourLesRoutes);
    }
}

class ElevationProfileComputerLorenzo {
    Route route = new Route() {
        @Override
        public int indexOfSegmentAt(double position) {
            return 0;
        }

        @Override
        public double length() {
            return 55;
        }

        @Override
        public List<Edge> edges() {
            return null;
        }

        @Override
        public List<PointCh> points() {
            return null;
        }

        @Override
        public PointCh pointAt(double position) {
            return null;
        }

        @Override
        public double elevationAt(double position) {
            DoubleUnaryOperator function = new DoubleUnaryOperator() {
                @Override
                public double applyAsDouble(double operand) {
                    if(operand<30){
                        return NaN;
                    }

                    return 33+4*(operand-30);
                }
            };
            return function.applyAsDouble(position);

        }

        @Override
        public int nodeClosestTo(double position) {
            return 0;
        }

        @Override
        public RoutePoint pointClosestTo(PointCh point) {
            return null;
        }
    };




    Route route2 = new Route() {
        @Override
        public int indexOfSegmentAt(double position) {
            return 0;
        }

        @Override
        public double length() {
            return 10;
        }

        @Override
        public List<Edge> edges() {
            return null;
        }

        @Override
        public List<PointCh> points() {
            return null;
        }

        @Override
        public PointCh pointAt(double position) {
            return null;
        }

        @Override
        public double elevationAt(double position) {
            DoubleUnaryOperator function = new DoubleUnaryOperator() {
                @Override
                public double applyAsDouble(double operand) {
                    return NaN;
                }
            };
            return function.applyAsDouble(position);

        }

        @Override
        public int nodeClosestTo(double position) {
            return 0;
        }

        @Override
        public RoutePoint pointClosestTo(PointCh point) {
            return null;
        }
    };

    Route route3 = new Route() {
        @Override
        public int indexOfSegmentAt(double position) {
            return 0;
        }

        @Override
        public double length() {
            return 30;
        }

        @Override
        public List<Edge> edges() {
            return null;
        }

        @Override
        public List<PointCh> points() {
            return null;
        }

        @Override
        public PointCh pointAt(double position) {
            return null;
        }

        @Override
        public double elevationAt(double position) {
            DoubleUnaryOperator function = new DoubleUnaryOperator() {
                @Override
                public double applyAsDouble(double operand) {
                    if(operand < 10){
                        return 10+operand;
                    }
                    if(operand>=10 && operand <20){
                        return NaN;
                    }
                    return 5*operand;
                }
            };
            return function.applyAsDouble(position);

        }

        @Override
        public int nodeClosestTo(double position) {
            return 0;
        }

        @Override
        public RoutePoint pointClosestTo(PointCh point) {
            return null;
        }
    };


    Route route4 = new Route() {
        @Override
        public int indexOfSegmentAt(double position) {
            return 0;
        }

        @Override
        public double length() {
            return 50;
        }

        @Override
        public List<Edge> edges() {
            return null;
        }

        @Override
        public List<PointCh> points() {
            return null;
        }

        @Override
        public PointCh pointAt(double position) {
            return null;
        }

        @Override
        public double elevationAt(double position) {
            DoubleUnaryOperator function = new DoubleUnaryOperator() {
                @Override
                public double applyAsDouble(double operand) {
                    if(operand < 20){
                        return operand*10;
                    }
                    if(operand>=20 && operand <40){
                        return NaN;
                    }
                    return operand+49;
                }
            };
            return function.applyAsDouble(position);

        }

        @Override
        public int nodeClosestTo(double position) {
            return 0;
        }

        @Override
        public RoutePoint pointClosestTo(PointCh point) {
            return null;
        }
    };

    Route route5 = new Route() {
        @Override
        public int indexOfSegmentAt(double position) {
            return 0;
        }

        @Override
        public double length() {
            return 200;
        }

        @Override
        public List<Edge> edges() {
            return null;
        }

        @Override
        public List<PointCh> points() {
            return null;
        }

        @Override
        public PointCh pointAt(double position) {
            return null;
        }

        @Override
        public double elevationAt(double position) {
            DoubleUnaryOperator function = new DoubleUnaryOperator() {
                @Override
                public double applyAsDouble(double operand) {
                    if(operand < 20){
                        return NaN;
                    }
                    else if(operand>=20 && operand <40){
                        return 10*(operand-20);
                    }
                    else if(operand >=40 && operand<60){
                        return NaN;
                    }
                    else if(operand>=60 && operand <120){
                        return 300 + 5*(operand-60);
                    }
                    else if(operand == 120){
                        return NaN;
                    }
                    else if (operand >=130 && operand <170){
                        return 40+(operand-130);
                    }
                    return NaN;
                }

            };
            return function.applyAsDouble(position);

        }

        @Override
        public int nodeClosestTo(double position) {
            return 0;
        }

        @Override
        public RoutePoint pointClosestTo(PointCh point) {
            return null;
        }
    };





    @Test
    void nbOfSamplesWorks(){
        //int nbOfSamples= (int)Math.ceil(route.length()/maxStepLength) + 1;
        double length1 = 23;
        double maxStepLength1 = 32;
        double nbOfSamples1= (int)Math.ceil(length1/maxStepLength1) + 1;
        assertEquals(2,nbOfSamples1);
        double length2 = 435;
        double maxStepLength2 = 98;
        double nbOfSamples2= (int)Math.ceil(length2/maxStepLength2) + 1;
        assertEquals(6,nbOfSamples2);
        double length3 = 10987123;
        double maxStepLength3 = 12656;
        double nbOfSamples3= (int)Math.ceil(length3/maxStepLength3) + 1;
        assertEquals(870,nbOfSamples3);

    }



    @Test
    void worksOnKnownValues(){
        float[] expected3 = new float[]{
                10,12,14,16,18,31.666666666f,45.33333333f,59,72.666666666f,86.333336f,100,110,120,130,140,150
        };
        assertArrayEquals(expected3, elevationProfile(route3,2));
        float[] expected1 = new float[]{
                33,33,33,33,33,33,33,53,73,93,113,133
        };
        assertArrayEquals(expected1, elevationProfile(route,5));
        float[] expected2 = new float[]{
                0,0,0,0,0,0,0,0,0,0,0
        };
        assertArrayEquals(expected2, elevationProfile(route2,1));
        float[] expected4 = new float[]{
                0, 50, 100, 150, 137.8f, 125.6f, 113.4f, 101.2f,89,94,99
        };
        assertArrayEquals(expected4,elevationProfile(route4,5));
        float[] expected5 = new float[]{
                0,0,0,100,166.666666667f,233.333333333f,300,350,400,450,500,550,295,40,50,60,70,70,70,70,70
        };
        assertArrayEquals(expected5,elevationProfile(route5,10));



    }

    private float[] elevationProfile(Route route, double maxStepLength) {
        Preconditions.checkArgument(maxStepLength > 0);

        int numberOfSamples =(int) Math.ceil(route.length() / maxStepLength) + 1;
        double step = route.length() / (numberOfSamples - 1) ;
        float[] samplesElevation = new float[numberOfSamples];

        for (int i = 0; i < numberOfSamples; i++) {
            double position = i * step;
            samplesElevation[i] = (float) route.elevationAt(position);
        }

        //test debut
        int index = 0;
        int start = -1;
        while (index != samplesElevation.length && Float.isNaN(samplesElevation[index])) {
            start = 0;
            ++index;
        }
        if (start != -1) {
            if (index == samplesElevation.length) {
                Arrays.fill(samplesElevation, 0, samplesElevation.length, 0);
            } else {
                Arrays.fill(samplesElevation, start, index, samplesElevation[index]);
            }
        }

        //test end
        index = samplesElevation.length - 1;
        start = -1;
        while (Float.isNaN(samplesElevation[index])) {
            start = samplesElevation.length - 1;
            --index;
        }
        if (start != -1) {
            Arrays.fill(samplesElevation, index + 1, start + 1, samplesElevation[index]);
        }

        //test intermediaire
        int ind0;
        int ind1;
        for (int i = 0; i < samplesElevation.length; ++i) {
            if (Float.isNaN(samplesElevation[i])) {
                ind0 = i;
                ind1 = i;
                ++i;
                while (Float.isNaN(samplesElevation[i])) {
                    ind1 = i;
                    ++i;
                }
                double x = ind1 - ind0 + 2;
                DoubleUnaryOperator function = Functions.sampled(new float[] {samplesElevation[ind0 - 1], samplesElevation[ind1 + 1]}, x);
                for (int j = 1; j < x; ++j) {
                    samplesElevation[ind0 - 1 + j] = (float) function.applyAsDouble(j);
                }
            }
        }
        return samplesElevation;
    }

}

class ElevationProfileComputerLina {

    public class RouteTest1 implements Route {

        private List<Edge> edges;
        private PointCh point1 = new PointCh(2485010, 1075010); //NotARealAttribute

        public RouteTest1(List<Edge> edges) {
            if (edges.isEmpty()) {
                throw new IllegalArgumentException();
            } else {
                this.edges = edges;
            }
        }

        @Override
        public double length() {
            double length = 0;
            for (int i = 0; i < edges.size(); ++i) {
                length += edges.get(i).length();
            }
            return length;
        }

        @Override
        public int indexOfSegmentAt(double position) {
            return 0;
        }

        @Override
        public List<Edge> edges() {
            return edges;
        }

        @Override
        public List<PointCh> points() {
            List<PointCh> points = new ArrayList<>();
            for (int i = 0; i < edges.size(); ++i) {
                points.add(edges.get(i).fromPoint());
            }
            points.add(edges.get(edges.size() - 1).toPoint());
            return points;
        }

        public RoutePoint pointClosestTo(PointCh point) {
            RoutePoint route1 = new RoutePoint(point1, 10, 20);
            return route1;
        }

        @Override
        public PointCh pointAt(double position) {
            return point1;
        }


        //J'AI INVENTE UNE SORTE D'ALGO BUT NOT SURE.
        @Override
        public double elevationAt(double position) {
            double oldPosition = position;
            double newPosition = position;
            for (int i = 0; i < edges.size(); ++i) {
                newPosition = oldPosition - edges.get(i).length();
                if (newPosition <= 0) {
                    return edges.get(i).elevationAt(oldPosition);
                } else {
                    oldPosition = newPosition;
                }
            }
            return 444;
        }

        @Override
        public int nodeClosestTo(double position) {
            return 444;
        }
    }

    @Test
    void elevationProfileWorksForEmptyProfiles() {
        PointCh fromPoint = new PointCh(2485010, 1075010);
        PointCh toPoint = new PointCh(2485020, 1075020);
        DoubleUnaryOperator nan = Functions.constant(Double.NaN);
        Edge edge0 = new Edge(1, 2, fromPoint, toPoint, 10, nan);
        Edge edge1 = new Edge(2, 3, fromPoint, toPoint, 10, nan);
        Edge edge2 = new Edge(4, 5, fromPoint, toPoint, 10, nan);
        Edge edge3 = new Edge(5, 6, fromPoint, toPoint, 10, nan);
        Edge edge4 = new Edge(6, 7, fromPoint, toPoint, 10, nan);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge0);
        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);
        edges.add(edge4);
        RouteTest1 routeTest1 = new RouteTest1(edges);
        ElevationProfile elevationProfileTesting = elevationProfile(routeTest1, 10);
        var rng = newRandom();
        for (var i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i += 1) {
            var position = rng.nextDouble(0, 50);
            assertEquals(0, elevationProfileTesting.elevationAt(position));
        }
    }

    @Test
    void elevationProfileOneEdgeNotNaN(){
        PointCh fromPoint = new PointCh(2485010, 1075010);
        PointCh toPoint = new PointCh(2485020, 1075020);
        DoubleUnaryOperator nan = Functions.constant(Double.NaN);
        DoubleUnaryOperator cst = Functions.constant(5.5);
        Edge edge0 = new Edge(1, 2, fromPoint, toPoint, 10, nan);
        Edge edge1 = new Edge(2, 3, fromPoint, toPoint, 10, nan);
        Edge edge2 = new Edge(4, 5, fromPoint, toPoint, 10, cst);
        Edge edge3 = new Edge(5, 6, fromPoint, toPoint, 10, nan);
        Edge edge4 = new Edge(6, 7, fromPoint, toPoint, 10, nan);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge0);
        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);
        edges.add(edge4);
        RouteTest1 routeTest1 = new RouteTest1(edges);
        ElevationProfile elevationProfileTesting = elevationProfile(routeTest1, 10);
        assertEquals(5.5, elevationProfileTesting.elevationAt(25));
        assertEquals(5.5, elevationProfileTesting.elevationAt(35));
        assertEquals(5.5, elevationProfileTesting.elevationAt(45));
        var rng = newRandom();
        for (var i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i += 1) {
            var position = rng.nextDouble(0, 50);
            assertEquals(5.5, elevationProfileTesting.elevationAt(position));
        }
    }

    @Test
    void elevationProfileWith1NaNInTheMiddleSimple(){
        PointCh fromPoint = new PointCh(2485010, 1075010);
        PointCh toPoint = new PointCh(2485020, 1075020);
        DoubleUnaryOperator nan = Functions.constant(Double.NaN);
        DoubleUnaryOperator cst = Functions.constant(5.5);
        Edge edge0 = new Edge(1, 2, fromPoint, toPoint, 10, cst);
        Edge edge1 = new Edge(2, 3, fromPoint, toPoint, 10, cst);
        Edge edge2 = new Edge(4, 5, fromPoint, toPoint, 10, nan);
        Edge edge3 = new Edge(5, 6, fromPoint, toPoint, 10, cst);
        Edge edge4 = new Edge(6, 7, fromPoint, toPoint, 10, cst);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge0);
        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);
        edges.add(edge4);
        RouteTest1 routeTest1 = new RouteTest1(edges);
        ElevationProfile elevationProfileTesting = elevationProfile(routeTest1, 10);
        var rng = newRandom();
        for (var i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i += 1) {
            var position = rng.nextDouble(0, 50);
            assertEquals(5.5, elevationProfileTesting.elevationAt(position));
        }
    }

    @Test
    void elevationProfileWith1NaNInTheMiddleDouble(){
        PointCh fromPoint = new PointCh(2485010, 1075010);
        PointCh toPoint = new PointCh(2485020, 1075020);
        DoubleUnaryOperator nan = Functions.constant(Double.NaN);
        DoubleUnaryOperator cst = Functions.constant(5);
        DoubleUnaryOperator cst2 = Functions.constant(6);
        Edge edge0 = new Edge(1, 2, fromPoint, toPoint, 1, cst);
        Edge edge1 = new Edge(2, 3, fromPoint, toPoint, 1, cst);
        Edge edge2 = new Edge(4, 5, fromPoint, toPoint, 1, nan);
        Edge edge3 = new Edge(5, 6, fromPoint, toPoint, 1, cst2);
        Edge edge4 = new Edge(6, 7, fromPoint, toPoint, 1, cst2);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge0);
        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);
        edges.add(edge4);
        RouteTest1 routeTest1 = new RouteTest1(edges);
        ElevationProfile elevationProfileTesting = elevationProfile(routeTest1, 1);
        assertEquals(5, elevationProfileTesting.elevationAt(0));
        assertEquals(5, elevationProfileTesting.elevationAt(2));
        assertEquals(6,elevationProfileTesting.elevationAt(4));
    }

    @Test
    void elevationProfileCompleteTest(){
        PointCh fromPoint = new PointCh(2485010, 1075010);
        PointCh toPoint = new PointCh(2485020, 1075020);
        DoubleUnaryOperator nan = Functions.constant(Double.NaN);
        DoubleUnaryOperator cst = Functions.constant(5);
        float[] squaredSamples = {0,4,16,36,64,100};
        DoubleUnaryOperator squared = Functions.sampled(squaredSamples, 10);
        Edge edge0 = new Edge(1, 2, fromPoint, toPoint, 10, nan);
        Edge edge1 = new Edge(2, 3, fromPoint, toPoint, 10, cst);
        Edge edge2 = new Edge(4, 5, fromPoint, toPoint, 10, nan);
        Edge edge3 = new Edge(5, 6, fromPoint, toPoint, 10,squared );
        Edge edge4 = new Edge(6, 7, fromPoint, toPoint, 10, nan);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge0);
        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);
        edges.add(edge4);
        RouteTest1 routeTest1 = new RouteTest1(edges);
        ElevationProfile elevationProfileTesting = elevationProfile(routeTest1, 2.5);
        float[] expected = {5,5,5,5,5,5,5,5,5,5.4f,5.8f,6.2f,6.6f,7,26,57,100,100,100,100,100};
        assertEquals(5,elevationProfileTesting.elevationAt(5));
        assertEquals(5,elevationProfileTesting.elevationAt(15));
        assertEquals(5.4f,elevationProfileTesting.elevationAt(22.5));
        assertEquals(5.8f,elevationProfileTesting.elevationAt(25));
        assertEquals(6.6f,elevationProfileTesting.elevationAt(30));
        assertEquals(7,elevationProfileTesting.elevationAt(32.5));
        assertEquals(100,elevationProfileTesting.elevationAt(50));
    }
}

class ElevationProfileComputerTestWilliam {
    private List<Edge> edges = new ArrayList<>();
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
    Edge edge5 = new Edge(18, 5,point5 ,point6, point5.distanceTo(point6),x ->Float.NaN);


    @Test
    public void elevationProfileComputerThrowsIAEOnInvalidStepLength() {
        edges.add(edge1);
        Route route = new SingleRoute(edges);
        assertThrows(IllegalArgumentException.class, () -> {
            ElevationProfile elevationProfileComputer = ElevationProfileComputer.elevationProfile(route,-4);
        });
    }
    @Test
    public void elevationProfileComputerWorksWithoutProfile0(){
        double maxStepLength = 200;
        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);
        edges.add(edge4);
        SingleRoute route = new SingleRoute(edges);
        ElevationProfile elevationProfileComputer = ElevationProfileComputer.elevationProfile(route,maxStepLength);

        double length=route.length();
        int numberOfSamples = (int)Math.ceil(length/maxStepLength) +1;
        float [] expectedSamples = new float[numberOfSamples];
        double spacing = (length/(numberOfSamples -1));
        for (int i = 0; i <numberOfSamples; i++) {
            expectedSamples[i] = (float) route.elevationAt(i*spacing);
        }


        //Renvoie la bonne longueur
        assertEquals(length,elevationProfileComputer.length());
        //Les echantillons sont identiques
        for (int i =0; i < expectedSamples.length; i++) {
            //assertEquals(expectedSamples[i], elevationProfileComputer.elevationSamples[i],"Inegalite a l'indice " + i);
        }
    }

    @Test
    public void elevationProfileComputerWorksWhenOnlyNaN() {
        Edge edge = new Edge(0, 3, point1, point2, point1.distanceTo(point2), x -> Float.NaN);
        edges.add(edge);
        Route route = new SingleRoute(edges);
        int numberOfSamples = (int)Math.ceil(edge.length()/200) +1;
        ElevationProfile elevationProfileComputer = ElevationProfileComputer.elevationProfile(route,200);
        //assertEquals(numberOfSamples, elevationProfileComputer.elevationSamples.length);
        for (int i = 0; i < numberOfSamples; i++) {
            //assertEquals(0, elevationProfileComputer.elevationSamples[i]);
        }

    }

    @Test
    public void elevationProfileComputerWorksForAllProfiles(){
        double maxStepLength = 200;
        edge1 = new Edge(0, 3, point1, point2, point1.distanceTo(point2), x->Float.NaN);
        edge3 = new Edge(8,10,point3, point4,point3.distanceTo(point4), x -> Float.NaN);
        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);
        edges.add(edge4);
        edges.add(edge5);
        SingleRoute route = new SingleRoute(edges);
        ElevationProfile elevationProfileComputer = ElevationProfileComputer.elevationProfile(route,maxStepLength);
        double length=route.length();
        int numberOfSamples = (int)Math.ceil(length/maxStepLength) +1;
        float [] expectedSamples = new float[numberOfSamples];
        double spacing = (length/(numberOfSamples -1));
        expectedSamples = new float[]{376.40225f, 376.40225f, 376.40225f, 376.40225f, 376.40225f, 376.40225f, 376.40225f, 376.40225f, 376.40225f, 376.40225f, 376.40225f, 376.40225f, 376.40225f, 376.40225f, 376.40225f, 376.40225f, 376.40225f, 376.40225f, 376.40225f, 376.40225f, 376.40225f, 376.40225f, 376.40225f, 376.40225f, 376.40225f, 376.40225f, 376.40225f, 376.40225f, 376.40225f, 376.40225f, 376.40225f, 376.40225f, 376.40225f, 376.40225f, 370.91168f, 365.42114f, 359.93057f, 354.44f, 348.94946f, 343.4589f, 341.01584f, 343.7611f, 346.5064f, 349.25168f, 344.00912f, 335.7733f, 327.53745f, 320.6984f, 328.93423f, 337.17007f, 345.4059f, 337.56253f, 329.71915f, 321.87576f, 314.03238f, 306.189f, 298.3456f, 290.50223f, 282.65884f, 274.81546f, 266.97208f, 259.1287f, 251.2853f, 260.03418f, 268.78308f, 277.53198f, 286.28088f, 295.0298f, 302.2672f, 307.51654f, 312.76587f, 318.01523f, 323.26456f, 328.5139f, 333.76324f, 339.01257f, 344.2619f, 349.51126f, 354.7606f, 360.00662f, 363.50616f, 367.00574f, 370.50528f, 374.00485f, 377.50443f, 381.00397f, 384.50354f, 388.00308f, 391.50266f, 395.0022f, 398.50177f, 398.50177f, 398.50177f, 398.50177f, 398.50177f} ;

        //assertEquals(expectedSamples.length, elevationProfileComputer.elevationSamples.length);
        for (int i = 0; i <expectedSamples.length ; i++) {
            //assertEquals(expectedSamples[i],elevationProfileComputer.elevationSamples[i]);
        }

    }
}



