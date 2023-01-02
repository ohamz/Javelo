package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.Route;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.List;

/**
 * @author Ursula El-Khoury (340994)
 * @author Omar Hammoud (341700)
 */

/**
 * Public final class that manages the display of the route and part of the interaction with it
 */
public final class RouteManager {

    private final RouteBean routeBean;
    private final ReadOnlyObjectProperty<MapViewParameters> mapProperty;

    private final Pane pane;
    private Polyline itinerary;
    private Circle circle;
    private final DoubleProperty highlightedPositionProperty;

    private Route route;
    private static final int CIRCLE_RADIUS = 5;

    /**
     * Public constructor of RouteManager
     * @param routeBean : the RouteBean that gives us access to the route that needs to be drawn
     * @param mapProperty : the ObjectProperty of nature MapViewParameter that gives us information about the map
     */
    public RouteManager(RouteBean routeBean, ReadOnlyObjectProperty<MapViewParameters> mapProperty) {
        this.routeBean = routeBean;
        this.mapProperty = mapProperty;

        pane = new Pane();
        highlightedPositionProperty = routeBean.highlightedPositionProperty();
        pane.setPickOnBounds(false);

        route = routeBean.route();

        createRouteManager();
        installBindings();
        installListeners();
        installEventHandler();
    }

    /**
     * Public method that gives us the pane where the route is drawn
     * @return a JavaFX Pane
     */
    public Pane pane() {
        return pane;
    }

    /**
     * Private method that installs the listeners of the class
     */
    private void installListeners() {
        routeBean.routeProperty().addListener((p, o, n) -> {
            if (routeBean.route() != null) {
                route = routeBean.route();
                initializeRoute(n);
            }
        });

        mapProperty.addListener((p, o, n) -> {
            route = routeBean.route();
            if (route != null) {
                setItineraryCoordinates(n);
                setCircleCoordinates(highlightedPositionProperty.doubleValue(), route);
                if (o.zoomLevel() != n.zoomLevel()) initializeRoute(route);
            }
        });

        highlightedPositionProperty.addListener((p, o, n) -> {
            route = routeBean.route();
            if (route != null) setCircleCoordinates(n.doubleValue(), route);
        });
    }

    /**
     * Private method that creates the RouteManager (which consists of the route and the circle used to add waypoints)
     */
    private void createRouteManager() {
        itinerary = new Polyline();
        circle = new Circle(CIRCLE_RADIUS);
        itinerary.setId("route");
        circle.setId("highlight");

        pane.getChildren().setAll(itinerary, circle);
    }

    /**
     * Private method that handles all the events of the class
     */
    private void installEventHandler() {
        circle.setOnMouseClicked(e -> {
            Point2D point = circle.localToParent(e.getX(), e.getY());
            PointCh pointCh = mapProperty.get().pointAt(point.getX(), point.getY()).toPointCh();
            int nodeId = routeBean.route().nodeClosestTo(highlightedPositionProperty.doubleValue());
            int index = routeBean.indexOfNonEmptySegmentAt(highlightedPositionProperty.doubleValue());
            Waypoint waypoint = new Waypoint(pointCh, nodeId);

            routeBean.waypoints().add(index + 1, waypoint);
        });
    }

    /**
     * Private method that creates the route (gives it the points in order to form it and situate it on the map
     * @param route : that is going to be drawn
     */
    private void initializeRoute(Route route) {
        if (route != null) {
            itinerary.getPoints().clear();
            int zoom = mapProperty.get().zoomLevel();
            List<PointCh> points = route.points();
            for (PointCh point : points) {
                PointWebMercator pointWebMercator = PointWebMercator.ofPointCh(point);
                itinerary.getPoints().add(pointWebMercator.xAtZoomLevel(zoom));
                itinerary.getPoints().add(pointWebMercator.yAtZoomLevel(zoom));
            }
            setCircleCoordinates(highlightedPositionProperty.doubleValue(), route);
            setItineraryCoordinates(mapProperty.get());
        }
    }

    /**
     * Private method that sets the coordinates of the circle
     * @param highlightedPosition : the highlighted position stored in the RouteBean
     * @param route : the route that is going to be drawn
     */
    private void setCircleCoordinates(double highlightedPosition, Route route) {
        PointWebMercator center = PointWebMercator.ofPointCh(route.pointAt(highlightedPosition));
        circle.setCenterX(mapProperty.get().viewX(center));
        circle.setCenterY(mapProperty.get().viewY(center));
    }

    /**
     * Private method that sets the coordinates of the itinerary (the route that has to be drawn)
     * @param map : the map that is stored in the RouteBean
     */
    private void setItineraryCoordinates(MapViewParameters map) {
        itinerary.setLayoutX(-map.x());
        itinerary.setLayoutY(-map.y());
    }

    /**
     * Private method that takes care of the visibility of the circle and the itinerary
     * through bindings
     */
    private void installBindings() {
        circle.visibleProperty().bind(routeBean.highlightedPositionProperty().greaterThanOrEqualTo(0));
        itinerary.visibleProperty().bind(routeBean.elevationProfileProperty().isNotNull());
    }

}
