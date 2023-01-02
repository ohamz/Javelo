package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FunctionsTestUs {

    @Test
    void constant() {
        double expected1  = 5.75;
        double actual1 = Functions.constant(5.75).applyAsDouble(15.55);
        double expected2 = 3.123456789123456789123456789;
        double actual2 = Functions.constant(3.123456789123456789123456789).applyAsDouble(1.427248721);
        assertEquals(expected1, actual1);
        assertEquals(expected2, actual2);
    }

    @Test
    void sampled() {
        double expected1 = 2.0;
        double actual1 = Functions.sampled(new float[]{0, 1, 2, 3, 4}, 4).applyAsDouble(2.0);
        double expected2 = -1;
        double actual2 = Functions.sampled(new float[]{-1, -2, 0, 0}, 3).applyAsDouble(-5);
        double expected3 = -1.25;
        double actual3 = Functions.sampled(new float[]{-1, -2, 0, 0}, 3).applyAsDouble(0.25);
        double expected4 = 1.5;
        double actual4 = Functions.sampled(new float[]{3, -2, 5, -6}, 6).applyAsDouble(3);
        double expected5 = -15;
        double actual5 = Functions.sampled(new float[]{0.2735468f, -2.97f, 7.25f, -15}, 6.5).applyAsDouble(8);
        assertEquals(expected1, actual1);
        assertEquals(expected2, actual2);
        assertEquals(expected3, actual3);
        assertEquals(expected4, actual4);
        assertEquals(expected5, actual5);
    }

    @Test
    void sampledError() throws IllegalArgumentException {
        boolean thrown = false;
        try {
            Functions.sampled(new float[]{0, 3.5f, -50.43f, 73}, -6.5).applyAsDouble(3.856);
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }
}