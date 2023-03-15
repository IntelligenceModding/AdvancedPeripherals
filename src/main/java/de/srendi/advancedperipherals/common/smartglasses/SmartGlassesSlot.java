package de.srendi.advancedperipherals.common.smartglasses;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class SmartGlassesSlot extends SlotItemHandler {

    public final SlotType slotType;
    private boolean isEnabled = false;

    public SmartGlassesSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, SlotType slotType) {
        super(itemHandler, index, xPosition, yPosition);
        this.slotType = slotType;
        this.isEnabled = slotType == SlotType.PERIPHERALS;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    @Override
    public boolean isActive() {
        return isEnabled;
    }

}
