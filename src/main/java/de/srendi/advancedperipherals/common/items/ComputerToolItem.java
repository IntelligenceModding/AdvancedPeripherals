package de.srendi.advancedperipherals.common.items;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.items.base.BaseItem;
import net.minecraft.util.text.ITextComponent;

import java.util.Optional;

public class ComputerToolItem extends BaseItem {

    public ComputerToolItem() {
        super(new Properties().maxStackSize(1).group(AdvancedPeripherals.TAB));
    }

    @Override
    protected Optional<String> getTurtleID() {
        return Optional.empty();
    }

    @Override
    protected ITextComponent getDescription() {
        return null;
    }
}
