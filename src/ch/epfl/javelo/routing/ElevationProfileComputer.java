package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Preconditions;

import java.util.Arrays;
import java.util.function.DoubleUnaryOperator;

/**
 * @author Ursula El-Khoury (340994)
 * @author Omar Hammoud (341700)
 */

public final class ElevationProfileComputer {
    private ElevationProfileComputer() {
    }

    /**
     * Public method that gives the profile of a Route, with the profile samples being seperated by maxStepLength intervals
     *
     * @param route         : given route
     * @param maxStepLength : given length between each step
     * @return the profile of the given route, ensuring that the spacing between profile samples is at most maxStepLength meters
     */
    public static ElevationProfile elevationProfile(Route route, double maxStepLength) {
        Preconditions.checkArgument(maxStepLength > 0);

        double routeLength = route.length();
        int numberOfSamples = (int) Math.ceil(routeLength / maxStepLength) + 1;
        double step = routeLength / (numberOfSamples - 1);
        float[] samplesElevation = new float[numberOfSamples];

        for (int i = 0; i < numberOfSamples; i++) {
            double position = i * step;
            samplesElevation[i] = (float) route.elevationAt(position);
        }
        int samplesElevationSize = samplesElevation.length;

        //check if the beginning of the samplesElevation tab is valid
        int index = 0;
        while (index != samplesElevationSize && Float.isNaN(samplesElevation[index])) {
            ++index;  //index of the first value that is not a NaN in the tab (index - 1 is the index of the last NaN in the tab)
        }
        if (index != 0) {
            if (index == samplesElevationSize) {  //all the values in the tab are NaN
                Arrays.fill(samplesElevation, 0, index, 0);
            } else {
                Arrays.fill(samplesElevation, 0, index, samplesElevation[index]);
            }
        }

        //check if the end of the samplesElevation tab is valid
        index = samplesElevationSize - 1;
        while (Float.isNaN(samplesElevation[index])) {
            --index;  //index of the last value that is not a NaN in the tab starting from the end of the tab
        }
        if (index != samplesElevationSize - 1) {
            Arrays.fill(samplesElevation, index + 1, samplesElevationSize, samplesElevation[index]);
        }

        //check if the middle of the samplesElevation tab is valid
        int index0;
        int index1;
        for (int i = 0; i < samplesElevationSize; ++i) {
            if (Float.isNaN(samplesElevation[i])) {
                index0 = i; //initializing the two indexes
                index1 = i;
                ++i;
                while (Float.isNaN(samplesElevation[i])) {
                    index1 = i;  //takes the index of the last found NaN
                    ++i;  //checks for the next NaN and jumps loops of the for loop
                }
                double xMax = index1 - index0 + 2;
                DoubleUnaryOperator function = Functions.sampled(new float[]{samplesElevation[index0 - 1],
                        samplesElevation[index1 + 1]}, xMax);
                for (int j = 1; j < xMax; ++j) {
                    samplesElevation[index0 - 1 + j] = (float) function.applyAsDouble(j);
                }
            }
        }
        return new ElevationProfile(routeLength, samplesElevation);
    }

}