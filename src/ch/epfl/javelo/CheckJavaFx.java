package ch.epfl.javelo;

import javafx.scene.paint.Color;

public final class CheckJavaFx {
    public static void main(String[] args) {
        double grade = grade(18 * 3 + 16 + 15 + 58, 19 + 94, 31 + 111, 1d);
        System.out.println(grade);

    }

    public static double grade(int p1, int p2, int pE, double b) {
        double p2b = Math.ceil(130d * Math.pow(p2 / 130d, 1d / b));
        double rawGrade = 0.875 + 5.25 * ((p1 + p2b + pE) / 500d);
        System.out.println(rawGrade);
        return Math.rint(rawGrade * 4) / 4;
    }
}
