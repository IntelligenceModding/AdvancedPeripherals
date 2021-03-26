package de.srendi.advancedperipherals.common.items;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.items.base.BaseItem;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ChunkControllerItem extends BaseItem {

    public ChunkControllerItem() {
        super(new Properties().group(AdvancedPeripherals.TAB).maxStackSize(64));
    }

    @Override
    protected ITextComponent getDescription() {
        return new TranslationTextComponent("item.advancedperipherals.tooltip.chunk_controller");
    }
}
