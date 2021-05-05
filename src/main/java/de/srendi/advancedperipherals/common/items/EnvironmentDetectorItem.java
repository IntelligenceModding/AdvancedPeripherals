package de.srendi.advancedperipherals.common.items;

import de.srendi.advancedperipherals.common.items.base.BaseBlockItem;
import de.srendi.advancedperipherals.common.setup.Blocks;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Optional;

public class EnvironmentDetectorItem extends BaseBlockItem {

    public EnvironmentDetectorItem() {
        super(Blocks.ENVIRONMENT_DETECTOR.get());
    }

    @Override
    public Optional<String> getPocketID() {
        return Optional.of("environment_pocket");
    }

    @Override
    public Optional<String> getTurtleID() {
        return Optional.of("environment_detector_turtle");
    }

    @Override
    public ITextComponent getDescription() {
        return new TranslationTextComponent("item.advancedperipherals.tooltip.environment_detector");
    }
}
