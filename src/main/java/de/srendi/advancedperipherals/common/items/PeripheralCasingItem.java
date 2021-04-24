package de.srendi.advancedperipherals.common.items;

import java.util.Optional;

import de.srendi.advancedperipherals.common.items.base.BaseBlockItem;
import de.srendi.advancedperipherals.common.setup.Blocks;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class PeripheralCasingItem extends BaseBlockItem {

    public PeripheralCasingItem() {
        super(Blocks.PERIPHERAL_CASING.get());
    }

    @Override
    protected Optional<String> getTurtleID() {
        return Optional.empty();
    }

    @Override
    protected Optional<String> getPocketID() {
        return Optional.empty();
    }

    @Override
    protected ITextComponent getDescription() {
        return new TranslationTextComponent("item.advancedperipherals.tooltip.peripheral_casing");
    }
}
