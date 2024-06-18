package de.srendi.advancedperipherals.common.items;

import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.items.base.BaseItem;
import de.srendi.advancedperipherals.common.util.EnumColor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MemoryCardItem extends BaseItem {

    public MemoryCardItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.enableInventoryManager.get();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level levelIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, levelIn, tooltip, flagIn);
        CompoundTag data = stack.getOrCreateTag();
        // TODO <0.8>: remove the owner name field
        if (data.contains("ownerId") && data.contains("owner")) {
            tooltip.add(EnumColor.buildTextComponent(Component.translatable("item.advancedperipherals.tooltip.memory_card.bound", data.getString("owner"))));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        if (!worldIn.isClientSide) {
            ItemStack stack = playerIn.getItemInHand(handIn);
            CompoundTag data = stack.getOrCreateTag();
            // TODO <0.8>: remove the owner name field
            if (data.contains("ownerId") || data.contains("owner")) {
                playerIn.displayClientMessage(Component.translatable("text.advancedperipherals.removed_player"), true);
                data.remove("ownerId");
                data.remove("owner");
            } else {
                playerIn.displayClientMessage(Component.translatable("text.advancedperipherals.added_player"), true);
                data.putUUID("ownerId", playerIn.getUUID());
                data.putString("owner", playerIn.getName().getString());
            }
        }
        return super.use(worldIn, playerIn, handIn);
    }
}
