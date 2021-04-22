package de.srendi.advancedperipherals.common.items;

import java.util.Optional;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.items.base.BaseItem;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ARGogglesItem extends BaseItem {
	public ARGogglesItem() {
        super(new Properties().group(AdvancedPeripherals.TAB).maxStackSize(64));
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
        return new TranslationTextComponent("item.advancedperipherals.tooltip.ar_goggles");
    }
}
