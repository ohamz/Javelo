package ch.epfl.javelo;

/**
 * @author Ursula El-Khoury (340994)
 * @author Omar Hammoud (341700)
 */

/**
 * Public final class with only one method used to simplify
 * the process of checking the preconditions of a method
 */
public final class Preconditions {

    private Preconditions() {
    }

    /**
     * Public method that makes sure that conditions are respected
     *
     * @param shouldBeTrue : the condition entered should be True
     *                     The method throws an exception if the condition is false
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) {
            throw new IllegalArgumentException();
        }
    }
}
