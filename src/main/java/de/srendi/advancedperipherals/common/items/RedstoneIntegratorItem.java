package de.srendi.advancedperipherals.common.items;

import de.srendi.advancedperipherals.common.items.base.BaseBlockItem;
import de.srendi.advancedperipherals.common.setup.Blocks;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Optional;

public class RedstoneIntegratorItem extends BaseBlockItem {

    public RedstoneIntegratorItem() {
        super(Blocks.REDSTONE_INTEGRATOR.get());
    }

    @Override
    public Optional<String> getTurtleID() {
        return Optional.empty();
    }

    @Override
    public Optional<String> getPocketID() {
        return Optional.empty();
    }

    @Override
    public ITextComponent getDescription() {
        return new TranslationTextComponent("item.advancedperipherals.tooltip.redstone_integrator");
    }

}
