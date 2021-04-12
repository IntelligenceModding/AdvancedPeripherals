package de.srendi.advancedperipherals.common.items;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.container.MemoryCardContainer;
import de.srendi.advancedperipherals.common.container.base.NamedContainerProvider;
import de.srendi.advancedperipherals.common.items.base.BaseItem;
import de.srendi.advancedperipherals.common.items.base.IInventoryItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Optional;

public class MemoryCardItem extends BaseItem implements IInventoryItem {

    public MemoryCardItem() {
        super(new Properties().group(AdvancedPeripherals.TAB));
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
        return new TranslationTextComponent("item.advancedperipherals.tooltip.memory_card");
    }

    @Override
    public INamedContainerProvider createContainer(PlayerEntity playerEntity, ItemStack itemStack) {
        return new NamedContainerProvider(new StringTextComponent("Yep"), (id, playerInventory, player) -> new MemoryCardContainer(id, playerInventory, itemStack));
    }
}
