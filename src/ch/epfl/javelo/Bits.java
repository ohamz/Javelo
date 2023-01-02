package ch.epfl.javelo;

/**
 * @author Ursula El-Khoury (340994)
 * @author Omar Hammoud (341700)
 */

public final class Bits {
    private Bits() {}

    /**
     * Public method that gives the signed value of a bitString of size 32
     *
     * @param value  : the int that we'll extract from
     * @param start  : index of the first chosen bit
     * @param length : length of the chosen bitString
     * @return the signed value of the bitString of size 32 of given value starting from the index
     * start and of length the given length, will throw an exception if the given length is not valid
     */
    public static int extractSigned(int value, int start, int length) {
        Preconditions.checkArgument(length > 0 && length <= Integer.SIZE
                && start >= 0 && start < 31 && (start + length) <= Integer.SIZE);
        int smashG = value << (Integer.SIZE - start - length);
        return smashG >> (Integer.SIZE - length);
    }

    /**
     * Public method that gives the unsigned value of a bitString of size 32
     *
     * @param value  : the int that we'll extract from
     * @param start  : index of the first chosen bit
     * @param length : length of the chosen bitString
     * @return the unsigned value of the bitString of size 32 of given value starting from the index
     * start and of length the given length, will throw an exception if the given length is not valid
     */
    public static int extractUnsigned(int value, int start, int length) {
        Preconditions.checkArgument(length > 0 && length < Integer.SIZE
                && start >= 0 && start < 31 && (start + length) <= Integer.SIZE);
        int smashG = value << (Integer.SIZE - start - length);
        return smashG >>> (Integer.SIZE - length);
    }
}
