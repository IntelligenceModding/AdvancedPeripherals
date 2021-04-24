package de.srendi.advancedperipherals.common.items;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.items.base.BaseItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.UUID;

public class MemoryCardItem extends BaseItem {

    public MemoryCardItem() {
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
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if(!worldIn.isRemote) {
            ItemStack stack = playerIn.getHeldItem(handIn);
            if (stack.getOrCreateTag().contains("owner")) {
                playerIn.sendMessage(new TranslationTextComponent("text.advancedperipherals.removed_player"), UUID.randomUUID());
                stack.getOrCreateTag().remove("owner");
            } else {
                playerIn.sendMessage(new TranslationTextComponent("text.advancedperipherals.added_player"), UUID.randomUUID());
                stack.getOrCreateTag().putString("owner", playerIn.getName().getString());
            }
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
}
