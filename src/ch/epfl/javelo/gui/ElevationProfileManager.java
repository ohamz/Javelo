package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.*;

import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;

import static java.lang.Double.NaN;

/**
 * @author Ursula El-Khoury (340994)
 * @author Omar Hammoud (341700)
 */

/**
 * Public final class that manages the graphics of the Elevation Profile of the route
 */
public final class ElevationProfileManager {
    private final ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty;
    private final ReadOnlyDoubleProperty highlightedPosition;

    private final ObjectProperty<Rectangle2D> rectangle;
    private final ObjectProperty<Transform> screenToWorld;
    private final ObjectProperty<Transform> worldToScreen;
    private final DoubleProperty mousePosition;

    private Pane pane;
    private BorderPane borderPane;

    private Path grid;
    private Group group;
    private Polygon profile;
    private Line line;
    private Text stats;

    private final double TOP;
    private final double RIGHT;
    private final double BOTTOM;
    private final double LEFT;
    private final int[] POS_STEPS =
            { 1000, 2000, 5000, 10_000, 25_000, 50_000, 100_000 };
    private final int[] ELE_STEPS =
            { 5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000 };

    private static final String HORIZONTAL_TEXT = "horizontal";
    private static final String VERTICAL_TEXT = "vertical";
    private static final int POSITION_PIXEL_STEP_VALUE = 50;
    private static final int ELEVATION_PIXEL_STEP_VALUE = 25;
    private static final int MAGIC_MULTIPLIER = 1000;
    private static final int MAGIC_ZERO = 0;

    /**
     * Public constructor of the ElevationProfileManager
     * @param elevationProfileProperty : a ReadOnlyObjectProperty representing the property ElevationProfile
     * @param position : a ReadOnlyObjectProperty representing the highlighted position
     */
    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> elevationProfileProperty,
                                   ReadOnlyDoubleProperty position) {
        this.elevationProfileProperty = elevationProfileProperty;
        highlightedPosition = position;

        Insets insets = new Insets(10, 10, 20, 40);
        TOP = insets.getTop();
        RIGHT = insets.getRight();
        BOTTOM = insets.getBottom();
        LEFT = insets.getLeft();


        createHierarchy();

        rectangle = new SimpleObjectProperty<>(Rectangle2D.EMPTY);

        screenToWorld = new SimpleObjectProperty<>(new Affine());
        worldToScreen = new SimpleObjectProperty<>(new Affine());

        mousePosition = new SimpleDoubleProperty(NaN);

        setBindings();
        setListeners();
        eventHandler();
    }

    /**
     * Public method that returns the borderPane of the ElevationProfileManager
     * @return the attribute borderPane of the object
     */
    public Pane pane() {
        return borderPane;
    }

    /**
     * Public method that returns a ReadOnlyDoubleProperty which contains the position of the mouse on the Elevation Profile
     * @return the attribute mousePosition of the object
     */
    public ReadOnlyDoubleProperty mousePositionOnProfileProperty() {
        return mousePosition;
    }

    /**
     * Private method that initializes all the graphic attributes of the class, namely the panes and nodes
     */
    private void createHierarchy() {
        pane = new Pane();
        VBox vBox = new VBox();
        borderPane = new BorderPane(pane, null, null, vBox, null);

        grid = new Path();
        group = new Group();
        profile = new Polygon();
        line = new Line();
        stats = new Text();

        borderPane.getStylesheets().add("elevation_profile.css");

        grid.setId("grid");
        profile.setId("profile");
        vBox.setId("profile_data");

        pane.getChildren().addAll(grid, group, profile, line);
        vBox.getChildren().add(stats);
    }

    /**
     * Private method that creates a new rectangle with its width and height
     * that are dependent of the width and height of the pane
     * @return a new instance of Rectangle2D
     */
    private Rectangle2D renewRectangle() {
        return new Rectangle2D(
                LEFT, TOP, clampWithZero(pane.getWidth() -RIGHT-LEFT),
                clampWithZero(pane.getHeight() -TOP-BOTTOM));
    }

    /**
     * Private method that draws the polygon of the profile
     */
    private void drawPolygon() {
        profile.getPoints().clear();
        Rectangle2D rect = rectangle.get();


        for (double screenX = rect.getMinX(); screenX < rect.getMaxX(); screenX++) {
            double worldX = screenToWorld.get().transform(screenX, MAGIC_ZERO).getX();
            double screenY = worldToScreen.get().transform(MAGIC_ZERO,
                    elevationProfileProperty.get().elevationAt(worldX)).getY();
            profile.getPoints().add(screenX);
            profile.getPoints().add(screenY);
        }

        profile.getPoints().add(rect.getMaxX());
        profile.getPoints().add(rect.getMaxY());
        profile.getPoints().add(rect.getMinX());
        profile.getPoints().add(rect.getMaxY());
    }

    /**
     * Private method that creates the VBox which contains the statistics
     */
    private void createVBox() {
        ElevationProfile eProf = elevationProfileProperty.get();

        stats.setText(String.format(
                "Longueur : %.1f km" +
                "     Montée : %.0f m" +
                "     Descente : %.0f m" +
                "     Altitude : de %.0f m à %.0f m",
                eProf.length() / MAGIC_MULTIPLIER,
                eProf.totalAscent(), eProf.totalDescent(),
                eProf.minElevation(), eProf.maxElevation()));
    }

    /**
     * Private method that finds the scale in which the texts have to be seperated
     * @param tab : the tab that contains the scales
     * @param value : the value that determines whether it's the scale for the position or the elevation
     * @return : the index of the scale that has to be used
     */
    private int scaleSearch(int[] tab, double value) {
        int step = 0;
        double pixels;
        for (int i : tab) {
            if (value == POSITION_PIXEL_STEP_VALUE) pixels = worldToScreen.get().deltaTransform(i, MAGIC_ZERO).getX();
            else pixels = worldToScreen.get().deltaTransform(MAGIC_ZERO, -i).getY();
            step = i;
            if (pixels >= value) {
                return step;
            }
        }
        return step;
    }

    /**
     * Private method that creates the grid and positions the text inside the rectangle
     */
    private void createGridAndText() {
        Rectangle2D rect = rectangle.get();
        double width = rect.getWidth();
        double height = rect.getHeight();

        int positionPixels = scaleSearch(POS_STEPS, POSITION_PIXEL_STEP_VALUE);
        int elevationPixels = scaleSearch(ELE_STEPS, ELEVATION_PIXEL_STEP_VALUE);

        double positionScale = worldToScreen.get().deltaTransform(positionPixels, MAGIC_ZERO).getX();

        double xStart;
        double lineX;

        int counter = 0;
        int positionStep = positionPixels / MAGIC_MULTIPLIER;

        for (int i = 0; i < width; i += positionScale) {
            xStart = LEFT + i;
            lineX = TOP + height;

            completeGrid(xStart, TOP, xStart, lineX, HORIZONTAL_TEXT,
                    xStart, lineX, counter++ * positionStep);
        }

        for (int i = 0; i < elevationProfileProperty.get().maxElevation(); i += elevationPixels) {
            double yStart = worldToScreen.get().transform(MAGIC_ZERO, i).getY();
            if (rect.contains(rect.getMinX(), yStart)) {
                xStart = rect.getMinX();
                lineX = LEFT + width;

                completeGrid(xStart, yStart, lineX, yStart, VERTICAL_TEXT,
                        xStart, lineX, i);
            }
        }
    }

    /**
     * Private method that sraws the lines of the grid, positions them and assigns the text that comes with it
     * @param moveX  : the initial x coordinate of the line
     * @param moveY  : the initial y coordinate of the line
     * @param lineX  : the end x coordinate of the line
     * @param lineY  : the end y coordinate of the line
     * @param str    : the string that determines whether the text is a horizontal text or a vertical text
     * @param xStart : the x coordinate of the text
     * @param yStart : the y coordinate of the text
     * @param value  : the value of the text
     */
    private void completeGrid(double moveX, double moveY, double lineX, double lineY, String str,
                              double xStart, double yStart, int value) {
        PathElement moveTo = new MoveTo(moveX, moveY);
        PathElement lineTo = new LineTo(lineX, lineY);
        grid.getElements().add(moveTo);
        grid.getElements().add(lineTo);

        Text text = str.equals(HORIZONTAL_TEXT)
                ? createText(String.valueOf(value), HORIZONTAL_TEXT, xStart, yStart)
                : createText(String.valueOf(value), VERTICAL_TEXT, xStart, lineY);
        if (str.equals(HORIZONTAL_TEXT)) {
            text.setX(xStart);
            text.setY(yStart);
        }
        group.getChildren().add(text);
    }


    /**
     * Private method that creates and returns the text that will be added to the rectangle along the gird
     * @param content : the content of the text
     * @param nature : determines whether it's a vertical or horizontal text
     * @param xStart : the x position of the text
     * @param yStart : the y position of the text
     * @return a Text
     */
    private Text createText(String content, String nature, double xStart, double yStart) {
        Text text = new Text(content);
        text.getStyleClass().add("grid_label");
        text.setFont(Font.font("Avenir", 10));
        if (nature.equals(HORIZONTAL_TEXT)) {
            text.setTextOrigin(VPos.TOP);
            double prefWidth = text.prefWidth(0)/2;
            text.setTranslateX(-prefWidth);
            text.getStyleClass().add(HORIZONTAL_TEXT);
        } else {
            text.setTextOrigin(VPos.CENTER);
            double prefHeight = text.prefWidth(0)  + 2;
            text.setX(xStart - prefHeight);
            text.setY(yStart);
            text.getStyleClass().add(VERTICAL_TEXT);
        }
        return text;
    }

    /**
     * Private method that creates the transformation screenToWorld
     * @return a Transform transformation
     */
    private Transform screenToWorld() {
        Affine screenToWorld = new Affine();
        ElevationProfile eProfile = elevationProfileProperty.get();

        Rectangle2D rect = rectangle.get();

        double xFactor = eProfile.length() / rect.getWidth();
        double yFactor = (eProfile.maxElevation() - eProfile.minElevation()) / rect.getHeight();

        screenToWorld.prependTranslation(-rect.getMinX(), -rect.getMaxY());
        screenToWorld.prependScale(xFactor, -yFactor);
        screenToWorld.prependTranslation(0, eProfile.minElevation());

        return screenToWorld;
    }

    /**
     * Private method that creates the worldToScreen transformation
     * @return a Transform transformation
     */
    private Transform worldToScreen() {
        try {
            return screenToWorld.get().createInverse();
        } catch (NonInvertibleTransformException ignored) {}
        return null;
    }

    /**
     * Private method that handles the bindings of the rectangle and the line of the highlighted position
     */
    private void setBindings() {
        line.layoutXProperty().bind(Bindings.createDoubleBinding(() -> worldToScreen.get().transform(
                        highlightedPosition.get(), 0).getX(), highlightedPosition, worldToScreen));
        line.startYProperty().bind(Bindings.select(rectangle, "minY"));
        line.endYProperty().bind(Bindings.select(rectangle, "maxY"));
        line.visibleProperty().bind(highlightedPosition.greaterThanOrEqualTo(0));

        rectangle.bind(Bindings.createObjectBinding(this::renewRectangle,
                pane.widthProperty(), pane.heightProperty()));

    }

    /**
     * Private method that handles the listeners of the properties of the class
     */
    private void setListeners() {
        elevationProfileProperty.addListener((p, o, n) -> renewContents());
        rectangle.addListener((p, o, n) -> renewContents());
    }

    /**
     * Private method that handles the recreation of the rectangle, transformations, grid and texts
     */
    private void renewContents() {
        if (elevationProfileProperty.get() == null) return;

        grid.getElements().clear();
        group.getChildren().clear();

        screenToWorld.set(screenToWorld());
        worldToScreen.set(worldToScreen());

        renewRectangle();
        drawPolygon();
        createGridAndText();
        createVBox();
    }

    /**
     * Private method that handles the movements of the mouse along the pane
     */
    private void eventHandler() {
        pane.setOnMouseMoved(e -> {
            if (rectangle.get().contains(e.getX(), e.getY())) {
                Point2D position = screenToWorld.get().transform(e.getX(), 0);
                mousePosition.setValue(Math.round(position.getX()));
            } else mousePosition.setValue(NaN);
        });

        pane.setOnMouseExited(e -> mousePosition.setValue(NaN));
    }

    /**
     * Private method that returns the value v if it's positive and 0 otherwise
     * @param v : the number that we want to clamp
     * @return positive double value or 0
     */
    private double clampWithZero(double v) {
        return Math.max(0, v);
    }

}
