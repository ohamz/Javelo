package ch.epfl.javelo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BitsTestUs {

    @Test
    void extractSigned() {
        int actual1 = Bits.extractSigned(190, 3, 3);
        int actual2 = Bits.extractSigned(510, 0, 5);
        int actual3 = Bits.extractSigned(-5, 0, 3);
        int actual4 = Bits.extractSigned(0, 30, 2);
        int expected1 = -1;
        int expected2 = -2;
        int expected3 = 3;
        int expected4 = 0;
        assertEquals(expected1, actual1);
        assertEquals(expected2, actual2);
        assertEquals(expected3, actual3);
        assertEquals(expected4, actual4);
    }

    @Test
    void ThrowsErrorExtractSigned() {
        boolean thrown = false;
        try {
            Bits.extractSigned(266, 7, 0);
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    void extractUnsigned() {
        int actual1 = Bits.extractUnsigned(190, 3, 3);
        int actual2 = Bits.extractUnsigned(510, 0, 5);
        int actual3 = Bits.extractUnsigned(-5, 0, 3);
        int actual4 = Bits.extractUnsigned(0, 30, 1);
        int expected1 = 7;
        int expected2 = 30;
        int expected3 = 3;
        int expected4 = 0;
        assertEquals(expected1, actual1);
        assertEquals(expected2, actual2);
        assertEquals(expected3, actual3);
        assertEquals(expected4, actual4);
    }

    @Test
    void ThrowsErrorExtractUnsigned() {
        boolean thrown = false;
        try {
            Bits.extractUnsigned(266, 7, 0);
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }
}