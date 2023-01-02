package ch.epfl.javelo.kml;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.*;

import java.io.IOException;
import java.nio.file.Path;

public final class KmlDoc {

    public static void writesKMLFile(int startNodeId, int endNodeId, String name) throws IOException {
        Graph g = Graph.loadFrom(Path.of("lausanne"));
        CostFunction cf = new CityBikeCF(g);
        RouteComputer rc = new RouteComputer(g, cf);
        Route r = rc.bestRouteBetween(startNodeId, endNodeId);
        long t0 = System.nanoTime();
        KmlPrinter.write(name, r);
        System.out.printf("Itinéraire calculé en %d ms\n",
                (System.nanoTime() - t0) / 1_000_000);
    }

    public static void main(String[] args) throws IOException {
        writesKMLFile(159049, 117669, "kmlFiles.javelo.kml");
        writesKMLFile(175000, 143235, "kmlFiles.javelo2.kml");
        writesKMLFile(124587, 185320, "kmlFiles.javelo3.kml");
        writesKMLFile(127654, 183194, "kmlFiles.javelo4.kml");
        writesKMLFile(158149, 173706, "kmlFiles.javelo5.kml");
        writesKMLFile(157404, 114881, "kmlFiles.javelo6.kml");
        writesKMLFile(119948, 157182, "kmlFiles.javelo7.kml");
        writesKMLFile(143505, 115460, "kmlFiles.javelo8.kml");
        writesKMLFile(139011, 165274, "kmlFiles.javelo9.kml");
        writesKMLFile(121806, 135247, "kmlFiles.javelo10.kml");
    }

}
