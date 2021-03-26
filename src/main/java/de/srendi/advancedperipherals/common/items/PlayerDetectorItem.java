package de.srendi.advancedperipherals.common.items;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.items.base.BaseBlockItem;
import de.srendi.advancedperipherals.common.setup.Blocks;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class PlayerDetectorItem extends BaseBlockItem {

    public PlayerDetectorItem() {
        super(Blocks.PLAYER_DETECTOR.get(), new Properties().group(AdvancedPeripherals.TAB));
    }

    @Override
    protected ITextComponent getDescription() {
        return new TranslationTextComponent("item.advancedperipherals.tooltip.player_detector");
    }
}
