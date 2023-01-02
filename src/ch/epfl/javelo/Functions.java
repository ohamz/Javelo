package ch.epfl.javelo;

import java.util.function.DoubleUnaryOperator;

/**
 * @author Ursula El-Khoury (340994)
 * @author Omar Hammoud (341700)
 */

public final class Functions {

    /**
     * Public method that gives the constant function of value y
     *
     * @param y : given y coordinate
     * @return a constant function with y as value
     */
    public static DoubleUnaryOperator constant(double y) {
        return new Constant(y);
    }

    /*
        Private record that gives the constant function of value y
     */
    private record Constant(double y) implements DoubleUnaryOperator {

        /**
         * Public method that gives the value of the function at a given x
         *
         * @param operand : coordinate
         * @return the value of the function at the value operand
         */
        @Override
        public double applyAsDouble(double operand) {
            return y;
        }
    }

    /**
     * Public method that gives the function formed via linear interpolation
     *
     * @param samples : given table of floats
     * @param xMax    : maximal value of coordinate x
     * @return a function found by linear interpolation between the samples
     */
    public static DoubleUnaryOperator sampled(float[] samples, double xMax) {
        Preconditions.checkArgument(xMax > 0 && samples.length >= 2);
        return new Sampled(samples.clone(), xMax);
    }

    /*
        Private record that creates the sampled function
     */
    private record Sampled(float[] samples, double xMax) implements DoubleUnaryOperator {

        /**
         * Public method that gives the value of the function at a given x
         *
         * @param operand : coordinate
         * @return the value of the function at the value operand
         */
        @Override
        public double applyAsDouble(double operand) {
            if (operand <= 0) {  //the value of y at x=0
                return samples[0];
            }
            if (operand >= xMax) {  //the value of y at x=xMax
                return samples[samples.length - 1];
            }
            double interval = xMax / (samples.length - 1);
            int initialIndex = (int) Math.floor((samples.length - 1) * (operand / xMax));
            double initialY = samples[initialIndex];
            double nextY = samples[initialIndex + 1];
            return Math2.interpolate(initialY, nextY, (operand / interval) - initialIndex);
        }
    }

}
