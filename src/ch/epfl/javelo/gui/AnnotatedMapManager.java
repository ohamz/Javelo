package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.RoutePoint;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.function.Consumer;

import static java.lang.Double.NaN;

/**
 * @author Ursula El-Khoury (340994)
 * @author Omar Hammoud (341700)
 */

/**
 * Public final class that manages the display of the base map
 * above which the route and waypoints are superimposed.
 */
public final class AnnotatedMapManager {
    private final Pane stackPane;
    private final RouteBean routeBean;

    private final ObjectProperty<MapViewParameters> mapProperty;
    private final DoubleProperty mousePositionOnRouteProperty;
    private final ObjectProperty<Point2D> currentMousePositionProperty;
    private Point2D nextPoint;

    private static final int PIXELS_LIMIT = 15;

    /**
     * Public constructor of AnnotatedMapManager
     * @param graph : the graph that contains the information about the map
     * @param tileManager : the TileManager that draws the map's images
     * @param routeBean : the RouteBean that gives us access to the multiple used objects
     * @param errorConsumer : the ErrorConsumer that deals with the errors
     */
    public AnnotatedMapManager(Graph graph, TileManager tileManager,
                               RouteBean routeBean, Consumer<String> errorConsumer) {
        this.routeBean = routeBean;

        mousePositionOnRouteProperty = new SimpleDoubleProperty(NaN);
        currentMousePositionProperty = new SimpleObjectProperty<>();

        MapViewParameters mapViewParameters =
                new MapViewParameters(12, 543200, 370650);
        mapProperty = new SimpleObjectProperty<>(mapViewParameters);

        WaypointsManager waypointsManager = new WaypointsManager(
                graph, mapProperty, routeBean.waypointsProperty(), errorConsumer);
        BaseMapManager baseMapManager = new BaseMapManager(tileManager, waypointsManager, mapProperty);
        RouteManager routeManager = new RouteManager(routeBean, mapProperty);

        stackPane = new StackPane(baseMapManager.pane(), routeManager.pane(), waypointsManager.pane());
        stackPane.getStylesheets().add("map.css");

        installBindings();
        installHandlers();
    }

    /**
     * Public method that returns the pane of the AnnotatedMapManager
     * @return a stackPane which contains the BaseMapManager,the RouteManager and the WaypoinsManager
     */
    public Pane pane() {
        return stackPane;
    }

    /**
     * Public method that retruns the position of the mouse on the Route
     * @return a DoubleProperty which contains the position
     */
    public DoubleProperty mousePositionOnRouteProperty() {
        return mousePositionOnRouteProperty;
    }

    /**
     * Private method that handles the interactions of the mouse with the pane of the AnnotatedMapManager
     */
    private void installHandlers() {
        stackPane.setOnMouseMoved(e -> {
            nextPoint = new Point2D(e.getX(), e.getY());
            currentMousePositionProperty.set(nextPoint);
        });

        stackPane.setOnMouseExited(e -> currentMousePositionProperty.set(null));
    }

    /**
     * Private method that installs the listeners of the AnnotatedMapManager
     */
    private void installBindings() {
        mousePositionOnRouteProperty.bind(Bindings.createDoubleBinding(this::getMouseDistanceFromRoute,
                currentMousePositionProperty, routeBean.routeProperty(), mapProperty));
    }

    /**
     * Private method that returns the closest point to the mouse on the road
     * @return the double value of the position on the road, if it exists
     */
    private double getMouseDistanceFromRoute() {
        if (routeBean.route() == null || currentMousePositionProperty.get() == null)
            return NaN;

        MapViewParameters map = mapProperty.get();
        Point2D mousePos = currentMousePositionProperty.get();

        PointCh pointCh = map.pointAt(mousePos.getX(), mousePos.getY()).toPointCh();

        if (pointCh == null) return NaN;

        RoutePoint routePoint = routeBean.route().pointClosestTo(pointCh);
        PointWebMercator pointOnRoute = PointWebMercator.ofPointCh(routePoint.point());

        double distance = Math2.norm(
                mousePos.getX() - map.viewX(pointOnRoute),
                mousePos.getY() - map.viewY(pointOnRoute));

        if (distance > PIXELS_LIMIT) return NaN;
        return routePoint.position();
    }
}