package de.srendi.advancedperipherals.common.items;

import de.srendi.advancedperipherals.common.items.base.BaseItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MemoryCardItem extends BaseItem {

    public MemoryCardItem() {
        super(new Properties().maxStackSize(1));
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
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip,
                               ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (stack.getOrCreateTag().contains("owner")) {
            tooltip.add(new TranslationTextComponent("item.advancedperipherals.tooltip.memory_card.bound",
                    stack.getOrCreateTag().getString("owner")));
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (!worldIn.isRemote) {
            ItemStack stack = playerIn.getHeldItem(handIn);
            if (stack.getOrCreateTag().contains("owner")) {
                playerIn.sendMessage(new TranslationTextComponent("text.advancedperipherals.removed_player"),
                        UUID.randomUUID());
                stack.getOrCreateTag().remove("owner");
            } else {
                playerIn.sendMessage(new TranslationTextComponent("text.advancedperipherals.added_player"),
                        UUID.randomUUID());
                stack.getOrCreateTag().putString("owner", playerIn.getName().getString());
            }
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
}
