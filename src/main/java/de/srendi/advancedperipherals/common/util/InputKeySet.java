package de.srendi.advancedperipherals.common.util;

public record InputKeySet(
    boolean forward,
    boolean back,
    boolean left,
    boolean right,
    boolean up,
    boolean down
) {
    public static final InputKeySet NONE = new InputKeySet(false, false, false, false, false, false);

    private static final byte FORWARD_BIT = 1 << 0;
    private static final byte BACK_BIT = 1 << 1;
    private static final byte LEFT_BIT = 1 << 2;
    private static final byte RIGHT_BIT = 1 << 3;
    private static final byte UP_BIT = 1 << 4;
    private static final byte DOWN_BIT = 1 << 5;

    public byte toByte() {
        byte b = 0;
        if (this.forward) {
            b |= FORWARD_BIT;
        }
        if (this.back) {
            b |= BACK_BIT;
        }
        if (this.left) {
            b |= LEFT_BIT;
        }
        if (this.right) {
            b |= RIGHT_BIT;
        }
        if (this.up) {
            b |= UP_BIT;
        }
        if (this.down) {
            b |= DOWN_BIT;
        }
        return b;
    }

    public static InputKeySet fromByte(final byte bits) {
        if (bits == 0) {
            return InputKeySet.NONE;
        }
        return new InputKeySet(
            (bits & FORWARD_BIT) != 0,
            (bits & BACK_BIT) != 0,
            (bits & LEFT_BIT) != 0,
            (bits & RIGHT_BIT) != 0,
            (bits & UP_BIT) != 0,
            (bits & DOWN_BIT) != 0
        );
    }
}
