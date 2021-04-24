package de.srendi.advancedperipherals.common.items;

import java.util.Optional;

import de.srendi.advancedperipherals.common.items.base.BaseBlockItem;
import de.srendi.advancedperipherals.common.setup.Blocks;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class EnvironmentDetectorItem extends BaseBlockItem {

    public EnvironmentDetectorItem() {
        super(Blocks.ENVIRONMENT_DETECTOR.get());
    }

    @Override
    protected Optional<String> getPocketID() {
        return Optional.of("environment_pocket");
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
