package ch.epfl.javelo.data;

import org.junit.jupiter.api.Test;

import static ch.epfl.javelo.data.Attribute.*;
import static org.junit.jupiter.api.Assertions.*;

class AttributeSetTestUs {

    @Test
    void contains() {
        AttributeSet test = AttributeSet.of(TRACKTYPE_GRADE1, HIGHWAY_TRACK);
        assertEquals(true, test.contains(HIGHWAY_TRACK));
        assertEquals(false, test.contains(TRACKTYPE_GRADE3));
    }

    @Test
    void intersects() {
        AttributeSet set1 = AttributeSet.of(TRACKTYPE_GRADE1, HIGHWAY_SERVICE);
        AttributeSet set2 = AttributeSet.of(TRACKTYPE_GRADE1, HIGHWAY_LIVING_STREET, HIGHWAY_TRUNK);
        AttributeSet set3 = AttributeSet.of(TRACKTYPE_GRADE5);
        assertEquals(true, set1.intersects(set2));
        assertEquals(false, set1.intersects(set3));
    }

    @Test
    void testToString() {
        AttributeSet set = AttributeSet.of(TRACKTYPE_GRADE1, HIGHWAY_TRACK);
        assertEquals("{highway=track,tracktype=grade1}", set.toString());
    }

    @Test
    void bits() {
        assertThrows(IllegalArgumentException.class, () -> {
            AttributeSet test = new AttributeSet(0b0111111111111111111111111111111111000000000000000000000000000000L);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            AttributeSet test = new AttributeSet(0b1111111111111111111111111111111111000000000000000000000000000000L);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            AttributeSet test = new AttributeSet(0b1011111111111111111111111111111111000000000000000000000000000000L);
        });
    }
}