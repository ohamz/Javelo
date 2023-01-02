package ch.epfl.javelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Q28_4TestUs {

    @Test
    void ofInt() {
        assertEquals(64, Q28_4.ofInt(4));
        assertEquals(-2147483648, Q28_4.ofInt(402653184));
        assertEquals(0, Q28_4.ofInt(0));
        assertEquals(0, Q28_4.ofInt(805306368));
    }

    @Test
    void asDouble() {
        assertEquals(0.0625, Q28_4.asDouble(1));
        assertEquals(-134217728.0, Q28_4.asDouble(-2147483648));
        assertEquals(0.5, Q28_4.asDouble(8));
        assertEquals(0.0, Q28_4.asDouble(0));
    }

    @Test
    void asFloat() {
        assertEquals(0.0625, Q28_4.asFloat(1));
        assertEquals(-134217728.0, Q28_4.asFloat(-2147483648));
        assertEquals(0.5, Q28_4.asFloat(8));
        assertEquals(0.0, Q28_4.asFloat(0));
    }
}