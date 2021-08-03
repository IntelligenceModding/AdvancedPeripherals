package de.srendi.advancedperipherals.common.items;

import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.items.base.BaseItem;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
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
    public Component getDescription() {
        return new TranslatableComponent("item.advancedperipherals.tooltip.memory_card");
    }

    @Override
    public boolean isEnabled() {
        return AdvancedPeripheralsConfig.enableInventoryManager;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip,
                                TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if (stack.getOrCreateTag().contains("owner")) {
            tooltip.add(new TranslatableComponent("item.advancedperipherals.tooltip.memory_card.bound",
                    stack.getOrCreateTag().getString("owner")));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        if (!worldIn.isClientSide) {
            ItemStack stack = playerIn.getItemInHand(handIn);
            if (stack.getOrCreateTag().contains("owner")) {
                playerIn.displayClientMessage(new TranslatableComponent("text.advancedperipherals.removed_player"),
                        true);
                stack.getOrCreateTag().remove("owner");
            } else {
                playerIn.displayClientMessage(new TranslatableComponent("text.advancedperipherals.added_player"),
                        true);
                stack.getOrCreateTag().putString("owner", playerIn.getName().getString());
            }
        }
        return super.use(worldIn, playerIn, handIn);
    }
}
