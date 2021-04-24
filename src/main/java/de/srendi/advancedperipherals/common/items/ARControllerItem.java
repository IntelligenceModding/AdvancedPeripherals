package de.srendi.advancedperipherals.common.items;

import java.util.Optional;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.items.base.BaseBlockItem;
import de.srendi.advancedperipherals.common.setup.Blocks;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ARControllerItem extends BaseBlockItem {

	public ARControllerItem() {
		super(Blocks.AR_CONTROLLER.get());
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
		return new TranslationTextComponent("item.advancedperipherals.tooltip.ar_controller");
	}
}
