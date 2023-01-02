package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import java.io.IOException;

/**
 * @author Ursula El-Khoury (340994)
 * @author Omar Hammoud (341700)
 */

/**
 * Public final class that manages the display and interaction with the base map
 */
public final class BaseMapManager {
    private final Pane pane;
    private final Canvas canvas;
    private final TileManager tileManager;
    private final WaypointsManager waypointsManager;
    private final ObjectProperty<MapViewParameters> mapProperty;
    private MapViewParameters map;

    private boolean redrawNeeded;

    private Point2D clickPoint, releasePoint, difPoint;
    private int newZoom;
    private double zoomX, zoomY;

    private static final int TILE_SIZE = 256;
    private static final int MIN_CLAMP_VALUE = 8;
    private static final int MAX_CLAMP_VALUE = 19;

    /**
     * Public constructor of BaseMapManager
     * @param tileManager : the TileManager that draws the map's images
     * @param waypointsManager : the WaypointsManager that adds the waypoints on the map
     * @param mapProperty : the ObjectProperty that contains the map
     */
    public BaseMapManager(TileManager tileManager, WaypointsManager waypointsManager, ObjectProperty<MapViewParameters> mapProperty) {
        this.tileManager = tileManager;
        this.waypointsManager = waypointsManager;
        this.mapProperty = mapProperty;
        map = mapProperty.get();
        canvas = new Canvas();
        pane = new Pane();

        installBindings();
        installListeners();
        installHandlers();
    }

    /**
     * Public method (getter) that returns the pane containing the map
     * @return a JavaFX Pane
     */
    public Pane pane() {
        return pane;
    }

    /**
     * Private method that checks if the map needs to get redrawn, if it does then it draws it
     */
    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;

        draw();
    }

    /**
     * Private method that requires the map to be redrawn on the next pulse
     */
    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }

    /**
     * Private method that draws the map's tiles
     */
    private void draw() {
        GraphicsContext g = canvas.getGraphicsContext2D();
        double mapX = map.x();
        double mapY = map.y();
        Image image;

        int leftBound = (int) Math.floor(mapX / TILE_SIZE);
        int rightBound = (int) Math.floor((mapX + canvas.getWidth()) / TILE_SIZE);
        int upperBound = (int) Math.floor(mapY / TILE_SIZE);
        int lowerBound = (int) Math.floor((mapY + canvas.getHeight()) / TILE_SIZE);

        double imageX = -(mapX % TILE_SIZE);
        double imageY;

        for (int x = leftBound; x <= rightBound; x++) {
            imageY = -(mapY % TILE_SIZE);

            for (int y = upperBound; y <= lowerBound; y++) {
                if (!TileManager.TileId.isValid(map.zoomLevel(), x, y)) continue;
                TileManager.TileId id = new TileManager.TileId(map.zoomLevel(), x, y);
                try {
                    image = tileManager.imageForTileAt(id);
                } catch (IOException e) {
                    image = null;
                }
                g.drawImage(image, imageX,  imageY);

                imageY += TILE_SIZE;
            }
            imageX += TILE_SIZE;
        }
    }

    /**
     * Private method that installs the binding of the canvas to the pane
     */
    private void installBindings() {
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());
        pane.getChildren().add(canvas);
    }

    /**
     * Private method that installs all the needed listener (to the canvas and the mapProperty)
     */
    private void installListeners() {
        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

        canvas.widthProperty().addListener((p, o, n) -> redrawOnNextPulse());
        canvas.heightProperty().addListener((p, o, n) -> redrawOnNextPulse());
        mapProperty.addListener((p, o, n) -> redrawOnNextPulse());
    }

    /**
     * Private method that installs all the event handlers (adding waypoints,
     * sliding through the map and changing zoom levels)
     */
    private void installHandlers() {
        // Adding waypoints
        pane.setOnMouseClicked(e -> {
            if (e.isStillSincePress())
                waypointsManager.addWaypoint(e.getX(), e.getY());
        });

        // Moving the map
        pane.setOnMousePressed(e ->
            clickPoint = new Point2D(e.getX(), e.getY()));

        pane.setOnMouseDragged(e -> {
            releasePoint = new Point2D(e.getX(), e.getY());
            difPoint = releasePoint.subtract(clickPoint);

            map = new MapViewParameters(map.zoomLevel(),
                    map.x() - difPoint.getX(), map.y() - difPoint.getY());
            mapProperty.setValue(map);

            clickPoint = new Point2D(e.getX(), e.getY());
        });

        pane.setOnMouseReleased(e -> {
            releasePoint = new Point2D(e.getX(), e.getY());
            difPoint = releasePoint.subtract(clickPoint);
            if (!e.isStillSincePress()) {
                map = map.withMinXY(map.x() - difPoint.getX(), map.y() - difPoint.getY());
                mapProperty.set(map);
            }
        });

        // Zoom manipulation
        SimpleLongProperty minScrollTime = new SimpleLongProperty();
        pane.setOnScroll(e -> {
            //Zoom addition of the professor
            if (e.getDeltaY() == 0d) return;
            long currentTime = System.currentTimeMillis();
            if (currentTime < minScrollTime.get()) return;
            minScrollTime.set(currentTime + 200);
            int scaleZoom = (int) Math.signum(e.getDeltaY());

            newZoom = Math2.clamp(MIN_CLAMP_VALUE, Math.round(map.zoomLevel() + scaleZoom) , MAX_CLAMP_VALUE);
            PointWebMercator point = mapProperty.get().pointAt(e.getX(), e.getY());
            if (newZoom != map.zoomLevel()) {
                zoomX = point.xAtZoomLevel(newZoom) - e.getX();
                zoomY = point.yAtZoomLevel(newZoom) - e.getY();
                map = new MapViewParameters(newZoom, zoomX, zoomY);
                mapProperty.setValue(map);
            }
        });
    }
}
