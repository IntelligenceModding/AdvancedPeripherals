package de.srendi.advancedperipherals.common.smartglasses;

import dan200.computercraft.core.computer.ComputerSide;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class SmartGlassesSlot extends SlotItemHandler {

    public final SlotType slotType;
    private boolean isEnabled;

    public SmartGlassesSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, SlotType slotType) {
        super(itemHandler, index, xPosition, yPosition);
        this.slotType = slotType;
        this.isEnabled = slotType == SlotType.defaultType();
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    @Override
    public boolean isActive() {
        return isEnabled;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    public static int sideToIndex(ComputerSide side) {
        return switch (side) {
            case TOP -> 0;
            case LEFT -> 1;
            case FRONT -> 2;
            case RIGHT -> 3;
            case BOTTOM -> 4;
            default -> throw new IllegalArgumentException("side cannot be back");
        };
    }

    public static ComputerSide indexToSide(int slot) {
        return switch (slot) {
            case 0 -> ComputerSide.TOP;
            case 1 -> ComputerSide.LEFT;
            case 2 -> ComputerSide.FRONT;
            case 3 -> ComputerSide.RIGHT;
            case 4 -> ComputerSide.BOTTOM;
            default -> throw new IllegalArgumentException("slot must in range [0,4]");
        };
    }

}
