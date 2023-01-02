package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Ursula El-Khoury (340994)
 * @author Omar Hammoud (341700)
 */

/**
 * Public final class that manages the display and interaction with the waypoints
 */
public final class WaypointsManager {
    private final Graph graph;
    private final ObjectProperty<MapViewParameters> mapProperty;
    private final ObservableList<Waypoint> observableWaypoints;
    private final Consumer<String> errorConsumer;
    private final Pane pane;

    private final static int FIRST_WAYPOINT = 1;
    private final static int MIDDLE_WAYPOINT = 2;
    private final static int LAST_WAYPOINT = 3;
    private final static int SEARCH_DISTANCE = 500;

    private final static String ERROR_MESSAGE = "Aucune route a proximité !";

    /**
     * The public constructor of WaypointsManager
     * @param graph : the graph that contains the information about the map
     * @param mapProperty : the ObjectProperty that contains the map
     * @param observableWaypoints : the ObservableList that contains all the Waypoints of the route
     * @param errorConsumer : the ErrorConsumer that deals with the errors
     */
    public WaypointsManager(Graph graph, ObjectProperty<MapViewParameters> mapProperty,
                            ObservableList<Waypoint> observableWaypoints, Consumer<String> errorConsumer) {
        this.graph = graph;
        this.mapProperty = mapProperty;
        this.observableWaypoints = observableWaypoints;
        this.errorConsumer = errorConsumer;
        this.pane = new Pane();
        this.pane.getChildren().setAll(getNodeChildren());

        pane.setPickOnBounds(false);

        installListeners();
    }

    /**
     * Public method that gives us the pane where the route is drawn
     * @return a JavaFX Pane
     */
    public Pane pane() {
        return pane;
    }

    /**
     * Public method that adds Waypoints using the coordinates of the canvas
     *
     * @param x : the coordinate x of the point
     * @param y : the coordinate y of the point
     */
    public void addWaypoint(double x, double y) {
        PointCh pointCh = mapProperty.get().pointAt(x, y).toPointCh();
        int nodeId = pointCh == null ? -1
                : graph.nodeClosestTo(pointCh, SEARCH_DISTANCE);

        if (nodeId == -1) {
            errorConsumer.accept(ERROR_MESSAGE);
        } else {
            Waypoint waypoint = new Waypoint(pointCh, nodeId);
            observableWaypoints.add(waypoint);
        }
    }

    /**
     * Private method that installs the listeners of the class
     */
    private void installListeners() {
        mapProperty.addListener((p, o, n) -> {
            for (Node child : pane.getChildren()) {
                PointWebMercator point = o.pointAt(child.getLayoutX(), child.getLayoutY());
                updateNode(child, n.viewX(point), n.viewY(point));
            }
        });
        observableWaypoints.addListener((Observable e) -> {
            pane.getChildren().clear();
            List<Node> newChildren = getNodeChildren();
            pane.getChildren().setAll(newChildren);
        });
    }

    /**
     * Private method that gets the children of the pane based on the list of Waypoints
     * @return : the list of children to add to the pane
     */
    private List<Node> getNodeChildren() {
        List<Node> children = new ArrayList<>();
        if (observableWaypoints.size() == 0) return children;
        Node firstNode = giveNode(observableWaypoints.get(0), FIRST_WAYPOINT);
        children.add(firstNode);
        for (int i = 1; i < observableWaypoints.size() - 1; i++) {
            Waypoint waypoint = observableWaypoints.get(i);
            Node node = giveNode(waypoint, MIDDLE_WAYPOINT);
            children.add(node);
        }
        if (observableWaypoints.size() > 1) {
            Node lastNode = giveNode(observableWaypoints.get(observableWaypoints.size() - 1), LAST_WAYPOINT);
            children.add(lastNode);
        }
        return children;
    }

    /**
     * Private method that creates the node based on the Waypoint
     * @param waypoint : the waypoint of the node
     * @param rank : determines whether the node is the first, middle or last node
     * @return : the node created
     */
    private Node setNode(Waypoint waypoint, int rank) {
        SVGPath outline = new SVGPath();
        outline.setContent("M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20");
        SVGPath inline = new SVGPath();
        inline.setContent("M0-23A1 1 0 000-29 1 1 0 000-23");

        Node node = new Group(outline, inline);
        PointWebMercator pointWebMercator = PointWebMercator.ofPointCh(waypoint.point());
        updateNode(node, mapProperty.get().viewX(pointWebMercator), mapProperty.get().viewY(pointWebMercator));

        node.getStyleClass().add("pin");
        outline.getStyleClass().add("pin_outside");
        inline.getStyleClass().add("pin_inside");

        if (rank == FIRST_WAYPOINT) {
            node.getStyleClass().add("first");
        } else if (rank == MIDDLE_WAYPOINT) {
            node.getStyleClass().add("middle");
        } else if (rank == LAST_WAYPOINT) {
            node.getStyleClass().add("last");
        }

        return node;
    }

    /**
     * Private method that makes sure the node is created and is handled by the event handler
     * @param waypoint : the waypoint that is given
     * @param rank : the rank of the node
     * @return the node created
     */
    private Node giveNode(Waypoint waypoint, int rank) {
        Node node = setNode(waypoint, rank);
        waypointEventManager(node, waypoint);
        return node;
    }

    /**
     * Private method that manages the events of the node
     * @param node : the node that is managed
     * @param waypoint : the waypoint given
     */
    private void waypointEventManager(Node node, Waypoint waypoint) {
        ObjectProperty<Point2D> vector = new SimpleObjectProperty<>();
        ObjectProperty<Point2D> initialCoordinates = new SimpleObjectProperty<>();

        node.setOnMouseClicked(e -> {
            if (e.isStillSincePress()) {
                observableWaypoints.remove(waypoint);
                pane.getChildren().remove(node);
            }
        });

        node.setOnMousePressed(e -> {
            vector.set(new Point2D(e.getX(), e.getY()));  //offset between the mouse and the pin
            initialCoordinates.set(new Point2D(node.getLayoutX(), node.getLayoutY()));
        });

        node.setOnMouseDragged(e -> {
            Point2D point2D = node.localToParent(e.getX() - vector.get().getX(), e.getY() - vector.get().getY());
            updateNode(node, point2D.getX(), point2D.getY());
        });

        node.setOnMouseReleased(e -> {
            if (!e.isStillSincePress()) {
                PointCh newPoint = mapProperty.get().pointAt(node.getLayoutX(), node.getLayoutY()).toPointCh();
                if (newPoint != null) {
                    int nodeId = graph.nodeClosestTo(newPoint, SEARCH_DISTANCE);
                    if (nodeId == -1) {
                        updateNode(node, initialCoordinates.get().getX(), initialCoordinates.get().getY());
                        errorConsumer.accept(ERROR_MESSAGE);
                    } else observableWaypoints.set(
                            observableWaypoints.indexOf(waypoint), new Waypoint(newPoint, nodeId));
                } else {
                    updateNode(node, initialCoordinates.get().getX(), initialCoordinates.get().getY());
                    errorConsumer.accept(ERROR_MESSAGE);
                }
            }
        });
    }

    /**
     * Private method that sets the coordinates of the node
     * @param node : given node
     * @param x : x coordinate of the node
     * @param y : y coordinate of the node
     */
    private void updateNode(Node node, double x, double y) {
        node.setLayoutX(x);
        node.setLayoutY(y);
    }
}