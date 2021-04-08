package de.srendi.advancedperipherals.common.util;

public class MathUtil {

    /**
     * @param a integer to add
     * @param b integer to add
     * @return returns a and b added together. If the result is not in the range of an integer, it will return a.
     */
    public static int safeAdd(int a, int b) {
        if (b > 0 ? a > Integer.MAX_VALUE - b
                : a < Integer.MIN_VALUE - b) {
            return b;
        }
        return a + b;
    }

    /**
     * @param a integer to subtract
     * @param b integer to subtract
     * @return returns a and b subtracted together. If the result is not in the range of an integer, it will return a.
     */
    public static int safeSubtract(int a, int b) {
        if (b > 0 ? a < Integer.MIN_VALUE + b
                : a > Integer.MAX_VALUE + b) {
            return a;
        }
        return a - b;
    }

}
