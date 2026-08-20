package de.srendi.advancedperipherals.common.items;

import de.srendi.advancedperipherals.client.ClientUUIDCache;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.setup.APDataComponents;
import de.srendi.advancedperipherals.common.util.EnumColor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class MemoryCardItem extends APItem {
    public MemoryCardItem() {
        super(new Properties().stacksTo(1), APConfig.PERIPHERALS_CONFIG.enableInventoryManager);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, level, tooltip, flagIn);
        if (!stack.hasTag() || !stack.getTag().contains(APDataComponents.OWNER)) {
            return;
        }
        UUID uuid = stack.getTag().getUUID(APDataComponents.OWNER);
        String username = ClientUUIDCache.getUsername(uuid);
        if (username == null) {
            username = uuid.toString();
        }
        tooltip.add(EnumColor.buildTextComponent(Component.translatable("item.advancedperipherals.tooltip.memory_card.bound", username)));
    }

    @Override
    @NotNull
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        if (!worldIn.isClientSide) {
            ItemStack stack = playerIn.getItemInHand(handIn);
            if (stack.hasTag() && stack.getTag().contains(APDataComponents.OWNER)) {
                playerIn.displayClientMessage(Component.translatable("text.advancedperipherals.removed_player"), true);
                stack.removeTagKey(APDataComponents.OWNER);
            } else {
                playerIn.displayClientMessage(Component.translatable("text.advancedperipherals.added_player"), true);
                stack.getOrCreateTag().putUUID(APDataComponents.OWNER, playerIn.getUUID());
            }
        }
        return super.use(worldIn, playerIn, handIn);
    }
}
