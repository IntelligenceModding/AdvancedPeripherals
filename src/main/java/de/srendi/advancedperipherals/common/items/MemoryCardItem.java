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

public class MemoryCardItem extends BaseItem {

    public MemoryCardItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public Optional<String> getTurtleID() {
        return Optional.empty();
    }

    @Override
    public Optional<String> getPocketID() {
        return Optional.empty();
    }

    @Override
    public ITextComponent getDescription() {
        return new TranslationTextComponent("item.advancedperipherals.tooltip.memory_card");
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip,
                                ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if (stack.getOrCreateTag().contains("owner")) {
            tooltip.add(new TranslationTextComponent("item.advancedperipherals.tooltip.memory_card.bound",
                    stack.getOrCreateTag().getString("owner")));
        }
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (!worldIn.isClientSide) {
            ItemStack stack = playerIn.getItemInHand(handIn);
            if (stack.getOrCreateTag().contains("owner")) {
                playerIn.displayClientMessage(new TranslationTextComponent("text.advancedperipherals.removed_player"),
                        true);
                stack.getOrCreateTag().remove("owner");
            } else {
                playerIn.displayClientMessage(new TranslationTextComponent("text.advancedperipherals.added_player"),
                        true);
                stack.getOrCreateTag().putString("owner", playerIn.getName().getString());
            }
        }
        return super.use(worldIn, playerIn, handIn);
    }
}
