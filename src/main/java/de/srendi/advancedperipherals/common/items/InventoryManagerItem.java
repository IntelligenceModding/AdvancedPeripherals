package de.srendi.advancedperipherals.common.items;

import de.srendi.advancedperipherals.common.items.base.BaseBlockItem;
import de.srendi.advancedperipherals.common.setup.Blocks;
import net.minecraft.block.Block;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Optional;

public class InventoryManagerItem extends BaseBlockItem {

    public InventoryManagerItem() {
        super(Blocks.INVENTORY_MANAGER.get());
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
        return new TranslationTextComponent("item.advancedperipherals.tooltip.inventory_manager");
    }
}
