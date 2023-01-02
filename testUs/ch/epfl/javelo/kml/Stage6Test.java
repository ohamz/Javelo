package ch.epfl.javelo.kml;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.CostFunction;
import ch.epfl.javelo.routing.Route;
import ch.epfl.javelo.routing.RouteComputer;

import java.io.IOException;
import java.nio.file.Path;

public final class Stage6Test {
    public static void main(String[] args) throws IOException {
        //lausanne();
        ch_west();

    }

    private static void lausanne() throws IOException {
        Graph g = Graph.loadFrom(Path.of("lausanne"));
        CostFunction cf = new CityBikeCF(g);
        RouteComputer rc = new RouteComputer(g, cf);
        Route r = rc.bestRouteBetween(159049, 117669);
        KmlPrinter.write("javeloOG.kml", r);
    }

    private static void ch_west() throws IOException {
        Graph g = Graph.loadFrom(Path.of("ch_west"));
        CostFunction cf = new CityBikeCF(g);
        RouteComputer rc = new RouteComputer(g, cf);
        long t0 = System.nanoTime();
        Route r = rc.bestRouteBetween(2046055, 2694240);
        System.out.printf("Itinéraire calculé en %d ms\n",
                (System.nanoTime() - t0) / 1_000_000);
    }
}