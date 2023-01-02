package ch.epfl.javelo;

/**
 * @author Ursula El-Khoury (340994)
 * @author Omar Hammoud (341700)
 */

/**
 * Public final class that provides static methods, not present in the class Math,
 * used for mathematical operations
 */
public final class Math2 {

    private Math2() {}

    /**
     * Public method that gives the excess division of two numbers x and y
     *
     * @param x : is not a negative number
     * @param y : is a positive number
     * @return the rounded non-decimal number in excess
     */
    public static int ceilDiv(int x, int y) {
        Preconditions.checkArgument(x >= 0 && y > 0);
        return (x + y - 1) / y;
    }

    /**
     * Public method that gives the value of y interpolated
     *
     * @param y0 : coordinate y of point 0
     * @param y1 : coordinate y of point 1
     * @param x  : given x coordinate
     * @return the coordinate y of the coordinate x that belongs to the line passing by (0, y0) and (1, y1)
     */
    public static double interpolate(double y0, double y1, double x) {
        return Math.fma(y1 - y0, x, y0);
    }

    /**
     * Public method that gives the value of v bounded between min and max
     *
     * @param min : minimal value (int)
     * @param v   : the value used (int)
     * @param max : maximal value (int)
     * @return the minimal value if v is lower or the maximal value if v is higher
     */
    public static int clamp(int min, int v, int max) {
        Preconditions.checkArgument(min <= max);

        return (v < min) ? min : ((v > max) ? max : v);
    }

    /**
     * Public method that gives the value of v bounded between min and max
     *
     * @param min : minimal value (double)
     * @param v   : the value used (double)
     * @param max : maximal value (double)
     * @return the minimal value if v is lower or the maximal value if v is higher
     */
    public static double clamp(double min, double v, double max) {
        Preconditions.checkArgument(min <= max);

        return (v < min) ? min : ((v > max) ? max : v);
    }

    /**
     * Public method that gives the hyperbolic sinus value of x
     *
     * @param x : given x coordinate
     * @return the hyperbolic sinus of x
     */
    public static double asinh(double x) {
        return Math.log(x + Math.sqrt(1 + Math.pow(x, 2)));
    }

    /**
     * Public method that gives the scalar product of 2 vectors
     *
     * @param uX : x coordinate of the vector u
     * @param uY : y coordinate of the vector u
     * @param vX : x coordinate of the vector v
     * @param vY : y coordinate of the vector v
     * @return the scalar product of the vectors u and v
     */
    public static double dotProduct(double uX, double uY, double vX, double vY) {
        return Math.fma(uX, vX, uY * vY);
    }

    /**
     * Public method that gives the squared norm of a vector
     *
     * @param uX : x coordinate of the vector u
     * @param uY : y coordinate of the vector u
     * @return the squared norm of the vector u
     */
    public static double squaredNorm(double uX, double uY) {
        return dotProduct(uX, uY, uX, uY);
    }

    /**
     * Public method that gives the norm of a vector
     *
     * @param uX : x coordinate of the vector u
     * @param uY : y coordinate of the vector u
     * @return the norm of the vector u
     */
    public static double norm(double uX, double uY) {
        return Math.sqrt(squaredNorm(uX, uY));
    }


    /**
     * Public method that gives the projection of a vector on another
     *
     * @param aX : x coordinate of the vector a
     * @param aY : y coordinate of the vector a
     * @param bX : x coordinate of the vector b
     * @param bY : y coordinate of the vector b
     * @param pX : x coordinate of the vector p
     * @param pY : y coordinate of the vector p
     * @return the norm of the projection of the vector AP on the vector AB
     */
    public static double projectionLength(double aX, double aY, double bX, double bY, double pX, double pY) {
        double xBA = bX - aX;
        double yBA = bY - aY;
        return dotProduct(pX - aX, pY - aY, xBA, yBA) / norm(xBA,  yBA);
    }

}