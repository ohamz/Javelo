package ch.epfl.javelo.data;

import ch.epfl.javelo.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * @author Ursula El-Khoury (340994)
 * @author Omar Hammoud (341700)
 */

public record AttributeSet(long bits) {

    /**
     * Public constructor of the object AttributeSet
     *
     * @param bits : that gives the attributes of the AttributeSet
     */
    public AttributeSet {
        Preconditions.checkArgument((bits & (1L << (Attribute.COUNT))) == 0 && (bits & (1L << Attribute.COUNT + 1)) == 0);
    }

    /**
     * Public method that creates an AttributeSet from an ellipse of attributes
     *
     * @param attributes : given attributes
     * @return the set of attributes given in argument
     */
    public static AttributeSet of(Attribute... attributes) {
        long list = 0;
        List<Attribute> attributeList = new ArrayList<>();
        for (Attribute attribute : attributes) {
            if (!attributeList.contains(attribute)) {
                attributeList.add(attribute);
                list += (1L << attribute.ordinal());
            }

        }
        return new AttributeSet(list);
    }

    /**
     * Public method that checks if the attribute is contained in the AttributeSet
     *
     * @param attribute : given attribute
     * @return true if and only if the current set (this) contains the attribute given in argument
     */
    public boolean contains(Attribute attribute) {
        long mask = 1L << attribute.ordinal();
        return (bits & mask) == mask;
    }

    /**
     * Public method that checks if the contents of two AttributeSets intersects
     *
     * @param that : given attribute
     * @return true if and only if the intersection between the current set
     * and the given attribute isn't empty
     */
    public boolean intersects(AttributeSet that) {
        return (this.bits & that.bits) != 0;
    }

    /**
     * Public method that returns a String version of the AttributeSet
     *
     * @return a new textual representation of elements of the set surrounded by accolades and seperated by commas
     */
    @Override
    public String toString() {
        StringJoiner j = new StringJoiner(",", "{", "}");
        for (Attribute a : Attribute.ALL) {
            if (this.contains(a)) {
                j.add(a.keyValue());
            }
        }
        return j.toString();
    }

}
