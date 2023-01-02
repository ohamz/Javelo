package ch.epfl.javelo.projection;

/**
 * @author Ursula El-Khoury (340994)
 * @author Omar Hammoud (341700)
 */

public final class SwissBounds {
    /**
     * Public static attribute that gives the minimum east coordinate
     */
    public static final double MIN_E = 2485000;

    /**
     * Public static attribute that gives the maximum east coordinate
     */
    public static final double MAX_E = 2834000;

    /**
     * Public static attribute that gives the minimum north coordinate
     */
    public static final double MIN_N = 1075000;

    /**
     * Public static attribute that gives the maximum north coordinate
     */
    public static final double MAX_N = 1296000;

    /**
     * Public static attribute that gives the width of the swiss bounds
     */
    public static final double WIDTH = MAX_E - MIN_E;

    /**
     * Public static attribute that gives the height of the swiss bounds
     */
    public static final double HEIGHT = MAX_N - MIN_N;


    private SwissBounds() {}

    /**
     * Public method that makes sure that the coordinates are in the SwissBounds
     * @param e : given e coordinate
     * @param n : given n coordinate
     * @return true if and only if the coordinates e and n are in the swiss territory
     */
    public static boolean containsEN(double e, double n) {
        return (MIN_E <= e && e <= MAX_E && MIN_N <= n && n <= MAX_N);
    }
}