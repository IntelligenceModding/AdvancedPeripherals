package de.srendi.advancedperipherals.common.items;

import de.srendi.advancedperipherals.client.ClientUUIDCache;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.items.base.BaseItem;
import de.srendi.advancedperipherals.common.util.EnumColor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static de.srendi.advancedperipherals.common.setup.APDataComponents.OWNER;

public class MemoryCardItem extends APItem {
    public MemoryCardItem() {
        super(new Properties().stacksTo(1), APConfig.PERIPHERALS_CONFIG.enableInventoryManager);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        Minecraft minecraft = Minecraft.getInstance();
        if (stack.has(OWNER)) {
            String username = ClientUUIDCache.getUsername(stack.get(OWNER));
            if (username == null) {
                username = stack.get(OWNER).toString();
            }
            tooltip.add(EnumColor.buildTextComponent(Component.translatable("item.advancedperipherals.tooltip.memory_card.bound", username)));
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level worldIn, @NotNull Player playerIn, @NotNull InteractionHand handIn) {
        if (!worldIn.isClientSide) {
            ItemStack stack = playerIn.getItemInHand(handIn);
            if (stack.has(OWNER)) {
                playerIn.displayClientMessage(Component.translatable("text.advancedperipherals.removed_player"), true);
                stack.remove(OWNER);
            } else {
                playerIn.displayClientMessage(Component.translatable("text.advancedperipherals.added_player"), true);
                stack.set(OWNER, playerIn.getUUID());
            }
        }
        return super.use(worldIn, playerIn, handIn);
    }
}
