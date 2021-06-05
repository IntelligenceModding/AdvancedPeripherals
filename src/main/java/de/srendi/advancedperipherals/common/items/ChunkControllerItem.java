package de.srendi.advancedperipherals.common.items;

import de.srendi.advancedperipherals.common.items.base.BaseItem;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Optional;

public class ChunkControllerItem extends BaseItem {

    public ChunkControllerItem() {
        super(new Properties().stacksTo(16));
    }

    @Override
    public Optional<String> getPocketID() {
        return Optional.empty();
    }

    @Override
    public Optional<String> getTurtleID() {
        return Optional.of("chunky_turtle");
    }

    @Override
    public ITextComponent getDescription() {
        return new TranslationTextComponent("item.advancedperipherals.tooltip.chunk_controller");
    }

}
