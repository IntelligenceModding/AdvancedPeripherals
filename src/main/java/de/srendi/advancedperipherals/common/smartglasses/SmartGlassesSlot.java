package de.srendi.advancedperipherals.common.smartglasses;

import dan200.computercraft.core.computer.ComputerSide;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.SlotItemHandler;

public class SmartGlassesSlot extends SlotItemHandler {
    public static final int PERIPHERAL_SLOTS = 5;
    public static final int MODULE_SLOT_OFFSET = PERIPHERAL_SLOTS;
    public static final int MODULE_SLOTS = 6;
    public static final int SLOTS = PERIPHERAL_SLOTS + MODULE_SLOTS;

    public static final ComputerSide[] UPGRADE_SIDES = new ComputerSide[]{
        ComputerSide.TOP, ComputerSide.LEFT, ComputerSide.FRONT, ComputerSide.RIGHT, ComputerSide.BOTTOM,
    };

    public final SlotType slotType;
    private SlotType activeSlotType;
    private ItemStack lastCopy = ItemStack.EMPTY;

    public SmartGlassesSlot(IItemHandlerModifiable itemHandler, int index, int xPosition, int yPosition, SlotType slotType) {
        super(itemHandler, index, xPosition, yPosition);
        this.slotType = slotType;
        this.activeSlotType = SlotType.defaultType();
    }

    @Override
    public boolean isActive() {
        return this.slotType == this.activeSlotType;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    public void setActiveSlotType(SlotType activeSlotType) {
        this.activeSlotType = activeSlotType;
    }

    public static int sideToIndex(ComputerSide side) {
        return switch (side) {
            case TOP -> 0;
            case LEFT -> 1;
            case FRONT -> 2;
            case RIGHT -> 3;
            case BOTTOM -> 4;
            default -> throw new IllegalArgumentException("side cannot be BACK");
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
