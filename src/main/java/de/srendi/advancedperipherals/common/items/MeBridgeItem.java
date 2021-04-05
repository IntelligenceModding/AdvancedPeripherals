package de.srendi.advancedperipherals.common.items;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.items.base.BaseBlockItem;
import de.srendi.advancedperipherals.common.setup.Blocks;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Optional;

public class MeBridgeItem extends BaseBlockItem {

    public MeBridgeItem() {
        super(Blocks.ME_BRIDGE.get(), new Properties().group(AdvancedPeripherals.TAB));
    }

    @Override
    protected Optional<String> getPocketID() {
        return Optional.empty();
    }

    @Override
    protected Optional<String> getTurtleID() {
        return Optional.empty();
    }

    @Override
    protected ITextComponent getDescription() {
        return new TranslationTextComponent("item.advancedperipherals.tooltip.me_bridge");
    }
}
