package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.*;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.util.*;

/**
 * @author Ursula El-Khoury (340994)
 * @author Omar Hammoud (341700)
 */

/**
 * Public final class that represents a JavaFX bean
 * grouping properties related to waypoints and the corresponding route.
 */
public final class RouteBean {
    private final ObservableList<Waypoint> waypoints;
    private final DoubleProperty highlightedPosition;
    private final ObjectProperty<Route> route;
    private final ObjectProperty<ElevationProfile> elevationProfile;

    private static final double MAX_DISTANCE = 5;

    private final HashMap<Pair<Integer, Integer>, Route> cacheMemory;

    /**
     * Public constructor of the RouteBean
     * @param rc : the RouteComputer that helps in creating the MultiRoute
     */
    public RouteBean(RouteComputer rc) {
        cacheMemory = new HashMap<>();
        waypoints = FXCollections.observableArrayList();

        route = new SimpleObjectProperty<>();
        elevationProfile = new SimpleObjectProperty<>();
        highlightedPosition = new SimpleDoubleProperty(Double.NaN);

        waypoints.addListener((ListChangeListener<? super Waypoint>) e -> {
            if (waypoints.size() < 2 ) {
                route.set(null);
                elevationProfile.set(null);
            } else {
                route.set(modifyListAndCache(rc));
            }
        });
    }

    /**
     * Public method that calculates the index of the non-empty segment
     * @param position : given position on the route
     * @return the index of the non-empty segment containing the given position
     */
    public int indexOfNonEmptySegmentAt(double position) {
        int index = route().indexOfSegmentAt(position);
        for (int i = 0; i <= index; i += 1) {
            int n1 = waypoints.get(i).nodeId();
            int n2 = waypoints.get(i + 1).nodeId();
            if (n1 == n2) index += 1;
        }
        return index;
    }

    /**
     * Public method (getter) that returns the property of the waypoints' list
     * @return an ObservableList of Waypoint
     */
    public ObservableList<Waypoint> waypointsProperty() {
        return waypoints;
    }

    /**
     * Public method (getter) that returns the list of waypoints
     * @return a List of Waypoint
     */
    public List<Waypoint> waypoints() {
        return waypoints;
    }

    /**
     * Public method (getter) that returns the highlighted position's property
     * @return a DoubleProperty
     */
    public DoubleProperty highlightedPositionProperty() {
        return highlightedPosition;
    }

    /**
     * Public method (getter) that return the route's property
     * @return a ReadOnlyObjectProperty of Route
     */
    public ReadOnlyObjectProperty<Route> routeProperty() {
        return route;
    }

    /**
     * Public method (getter) that return the route
     * @return the route of type Route
     */
    public Route route() {
        return route.get();
    }

    /**
     * Public method (getter) that returns the elevation profile's property
     * @return a ReadOnlyObjectProperty of ElevationProfile
     */
    public ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty() {return elevationProfile;}

    /**
     * Public method (getter) that returns the elevation profile
     * @return the elevation profile pf type ElevationProfile
     */
    public ElevationProfile elevationProfile() {
        return elevationProfile.get();
    }


    /**
     * Private methods that creat a MultiRoute using a cacheMemory system and depending on the list of waypoints
     * @param rc : RouteComputer given in the constructor
     * @return the Route after modified list
     */
    private Route modifyListAndCache(RouteComputer rc) {
        HashMap<Pair<Integer, Integer>, Route> stashCache = new HashMap<>(cacheMemory);
        cacheMemory.clear();
        List<Route> singleRoutes = new ArrayList<>();

        for (int i = 1; i < waypoints.size(); i++) {
            Pair<Integer, Integer> pair =
                    new Pair<>(waypoints.get(i - 1).nodeId(), waypoints.get(i).nodeId());
            if (pair.getKey().equals(pair.getValue())) continue;

            Route bestRoute = stashCache.containsKey(pair) ? stashCache.get(pair)
                    : rc.bestRouteBetween(pair.getKey(), pair.getValue());

            if (bestRoute == null) {
                elevationProfile.set(null);
                return null;
            }
            cacheMemory.put(pair, bestRoute);
            singleRoutes.add(bestRoute);
        }
        elevationProfile.set(ElevationProfileComputer.elevationProfile(new MultiRoute(singleRoutes), MAX_DISTANCE));
        return new MultiRoute(singleRoutes);
    }

}
