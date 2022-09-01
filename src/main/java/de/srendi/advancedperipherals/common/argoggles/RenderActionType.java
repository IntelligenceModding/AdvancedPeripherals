package de.srendi.advancedperipherals.common.argoggles;

import de.srendi.advancedperipherals.AdvancedPeripherals;

public enum RenderActionType {

    DRAW_CENTERED_STRING(3),
    DRAW_STRING(3),
    FILL(5),
    HORIZONTAL_LINE(4),
    VERTICAL_LINE(4),
    FILL_GRADIENT(6),
    DRAW_RIGHTBOUND_STRING(3),
    DRAW_CIRCLE(4),
    FILL_CIRCLE(4),
    DRAW_ITEM_ICON(2);

    private final int intArgCount;

    RenderActionType(int intArgCount) {
        this.intArgCount = intArgCount;
    }

    public boolean ensureArgs(int[] args) {
        boolean correct = args.length >= intArgCount;
        if (!correct)
            AdvancedPeripherals.LOGGER.warn("Got invalid number of arguments for AR render action {}: expected {}, got {}", this.toString(), intArgCount, args.length);
        return correct;
    }
}
