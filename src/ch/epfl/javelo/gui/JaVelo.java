package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.GpxGenerator;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * @author Ursula El-Khoury (340994)
 * @author Omar Hammoud (341700)
 */

/**
 * Public final class that handles the main program of the project
 * and the initialization of the map
 */
public final class JaVelo extends Application {

    public static void main(String[] args) { launch(args); }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Graph graph = Graph.loadFrom(Path.of("javelo-data"));
        TileManager tileManager = new TileManager(
                Path.of("osm-cache"), "tile.openstreetmap.org");

        RouteComputer routeComputer = new RouteComputer(graph, new CityBikeCF(graph));
        RouteBean routeBean = new RouteBean(routeComputer);

        ErrorManager errorManager = new ErrorManager();
        Consumer<String> errorConsumer = errorManager::displayError;

        AnnotatedMapManager annotatedMapManager = new AnnotatedMapManager(
                graph, tileManager, routeBean, errorConsumer);

        ElevationProfileManager elevationProfileManager = new ElevationProfileManager(
                routeBean.elevationProfileProperty(), routeBean.highlightedPositionProperty());

        SplitPane splitPane = new SplitPane(annotatedMapManager.pane());
        splitPane.setOrientation(Orientation.VERTICAL);
        SplitPane.setResizableWithParent(elevationProfileManager.pane(), false);

        MenuItem menuItem = new MenuItem("Exporter GPX");
        menuItem.setDisable(true);
        MenuBar menuBar = new MenuBar(new Menu("Fichier", null, menuItem));

        // Listeners
        routeBean.routeProperty().addListener((p, o, n) -> {
            if (n != null && o == null) splitPane.getItems().add(elevationProfileManager.pane());
            else if (n == null)
                splitPane.getItems().remove(elevationProfileManager.pane());
        });

        // Bindings
        routeBean.highlightedPositionProperty().bind(Bindings
                .when(annotatedMapManager.mousePositionOnRouteProperty().greaterThanOrEqualTo(0))
                .then(annotatedMapManager.mousePositionOnRouteProperty())
                .otherwise(elevationProfileManager.mousePositionOnProfileProperty()));

        menuItem.disableProperty().bind(routeBean.routeProperty().isNull());

        // Event handlers
        menuItem.setOnAction(e -> {
            try {
                GpxGenerator.writeGpx("javelo.gpx", routeBean.route(), routeBean.elevationProfile());
            } catch (IOException ex) {throw new UncheckedIOException(ex);}
        });

        StackPane stackPane = new StackPane(splitPane, errorManager.pane());
        BorderPane mainPane = new BorderPane(stackPane, menuBar, null, null, null);
        mainPane.getStylesheets().add("map.css");

        primaryStage.setTitle("JaVelo");
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.show();
    }
}
