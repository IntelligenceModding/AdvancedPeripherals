package de.srendi.advancedperipherals.common.items;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.items.base.BaseBlockItem;
import de.srendi.advancedperipherals.common.setup.Blocks;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Optional;

public class EnvironmentDetectorItem extends BaseBlockItem {

    public EnvironmentDetectorItem() {
        super(Blocks.ENVIRONMENT_DETECTOR.get(), new Properties().group(AdvancedPeripherals.TAB));
    }

    @Override
    protected Optional<String> getTurtleID() {
        return Optional.of("environment_detector_turtle");
    }

    @Override
    protected ITextComponent getDescription() {
        return new TranslationTextComponent("item.advancedperipherals.tooltip.environment_detector");
    }
}
