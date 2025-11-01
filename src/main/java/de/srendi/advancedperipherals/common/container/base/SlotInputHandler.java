package de.srendi.advancedperipherals.common.container.base;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class SlotInputHandler extends SlotItemHandler {

    SlotCondition condition;

    public SlotInputHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition, SlotCondition condition) {
        super(itemHandler, index, xPosition, yPosition);
        this.condition = condition;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return condition.isValid(stack);
    }
}
