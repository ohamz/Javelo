package ch.epfl.javelo.gui;

import javafx.geometry.Point2D;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;


public class TestTransform {

    private static Point2D point;
    private static double paneWidth = 600;
    private static double paneHeight = 300;
    private static double recWidth = 550;
    private static double recHeight = 270;

    public static void main(String[] args) {

        Transform screenToWorld = screenToWorld();
        System.out.println(screenToWorld.transform(0, 663));

    }

    public static Transform screenToWorld() {

        Affine screenToWorld = new Affine();

        screenToWorld.prependTranslation(40, 20);
        screenToWorld.prependScale(2, -0.3);
        screenToWorld.prependTranslation(-40, -10);

        return screenToWorld;
    }
}
