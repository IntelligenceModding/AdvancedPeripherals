package de.srendi.advancedperipherals.common.items;

import de.srendi.advancedperipherals.client.ClientUUIDCache;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.util.EnumColor;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

import static de.srendi.advancedperipherals.common.setup.APDataComponents.OWNER;

public class MemoryCardItem extends APItem {
    public MemoryCardItem() {
        super(new Properties().stacksTo(1), APConfig.PERIPHERALS_CONFIG.enableInventoryManager);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        UUID uuid = stack.get(OWNER);
        if (uuid == null) {
            return;
        }
        String username = ClientUUIDCache.getUsername(uuid);
        if (username == null) {
            username = uuid.toString();
        }
        tooltip.add(EnumColor.buildTextComponent(Component.translatable("item.advancedperipherals.tooltip.memory_card.bound", username)));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            if (player.getClass() != ServerPlayer.class) {
                return InteractionResultHolder.fail(stack);
            }
            if (stack.has(OWNER)) {
                player.displayClientMessage(Component.translatable("text.advancedperipherals.removed_player"), true);
                stack.remove(OWNER);
            } else {
                player.displayClientMessage(Component.translatable("text.advancedperipherals.added_player"), true);
                stack.set(OWNER, player.getUUID());
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
