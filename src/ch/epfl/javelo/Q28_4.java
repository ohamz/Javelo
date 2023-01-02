package ch.epfl.javelo;

/**
 * @author Ursula El-Khoury (340994)
 * @author Omar Hammoud (341700)
 */

public final class Q28_4 {
    private static int MASK_SHIFT = 4;

    private Q28_4(){}

    /**
     * Public method that gives the value Q28.4 of a given int
     *
     * @param i : given int
     * @return the value Q28.4 corresponding to the given int
     */
    public static int ofInt(int i) {
        return i << MASK_SHIFT;
    }

    /**
     * Public method that gives double value of a Q28.4 value
     *
     * @param q28_4 : given Q28.4 value
     * @return the double equal to the given Q28.4 value
     */
    public static double asDouble(int q28_4) {
        return Math.scalb((double) q28_4, -MASK_SHIFT);
    }

    /**
     * Public method that gives float value of a Q28.4 value
     *
     * @param q28_4 : given Q28.4 value
     * @return the value of type float of the given Q28.4 value
     */
    public static float asFloat(int q28_4) {
        return Math.scalb(q28_4, -MASK_SHIFT);
    }

}
