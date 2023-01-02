package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Preconditions;

import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.function.DoubleUnaryOperator;

/**
 * @author Ursula El-Khoury (340994)
 * @author Omar Hammoud (341700)
 */

/**
 * Public class that handles the Elevation Profile of the route
 */
public final class ElevationProfile {
    private final double length, MIN_ALTITUDE, MAX_ALTITUDE, TOTAL_ASCENT, TOTAL_DESCENT ;
    private final DoubleUnaryOperator profile;

    /**
     * Public constructor of the object ElevationProfile
     *
     * @param length           : given length
     * @param elevationSamples : given table of float values
     */
    public ElevationProfile(double length, float[] elevationSamples) {
        Preconditions.checkArgument(length > 0 && elevationSamples.length >= 2);
        this.length = length;

        DoubleSummaryStatistics s = new DoubleSummaryStatistics();
        double sumA = 0;
        double sumD = 0;
        s.accept(elevationSamples[0]);
        for (int i = 1; i < elevationSamples.length; ++i) {
            s.accept(elevationSamples[i]);
            float currentDif = elevationSamples[i] - elevationSamples[i - 1];
            if (currentDif > 0)
                sumA += currentDif;
            if (currentDif < 0)
                sumD += currentDif;
        }
        TOTAL_ASCENT = sumA;
        TOTAL_DESCENT = Math.abs(sumD);
        MIN_ALTITUDE = s.getMin();
        MAX_ALTITUDE = s.getMax();
        profile = Functions.sampled(elevationSamples, length);

    }

    /**
     * Public method that gives the length of the profile
     *
     * @return the length of the profile in meters
     */
    public double length() {
        return length;
    }

    /**
     * Public method that gives the minimum altitude of the profile
     *
     * @return the minimum altitude of the profile in meters
     */
    public double minElevation() {
        return MIN_ALTITUDE;
    }

    /**
     * Public method that gives the maximum altitude of the profile
     *
     * @return the maximum altitude of the profile in meters
     */
    public double maxElevation() {
        return MAX_ALTITUDE;
    }

    /**
     * Public method that gives the total positive elevation of the profile
     *
     * @return the total positive height difference of the profile in meters
     */
    public double totalAscent() {
        return TOTAL_ASCENT;
    }

    /**
     * Public method that gives the total negative elevation of the profile
     *
     * @return the total negative height difference of the profile in meters
     */
    public double totalDescent() {
        return TOTAL_DESCENT;
    }

    /**
     * Public method that gives the elevation of the profile at a given position
     *
     * @param position : given position
     * @return the altitude of the profile at the given position
     */
    public double elevationAt(double position) {
        return profile.applyAsDouble(position);
    }
}
