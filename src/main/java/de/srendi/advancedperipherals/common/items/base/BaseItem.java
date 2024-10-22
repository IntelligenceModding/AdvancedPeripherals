package de.srendi.advancedperipherals.common.items.base;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.util.TranslationUtil;
import de.srendi.advancedperipherals.common.util.inventory.ItemUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class BaseItem extends Item {
    private Component description;

    public BaseItem(Properties properties) {
        super(properties.tab(AdvancedPeripherals.TAB));
    }

    public BaseItem() {
        super(new Properties().tab(AdvancedPeripherals.TAB));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        if (worldIn.isClientSide)
            return new InteractionResultHolder<>(InteractionResult.PASS, playerIn.getItemInHand(handIn));
        if (this instanceof IInventoryItem inventoryItem) {
            ServerPlayer serverPlayerEntity = (ServerPlayer) playerIn;
            ItemStack stack = playerIn.getItemInHand(handIn);
            NetworkHooks.openScreen(serverPlayerEntity, inventoryItem.createContainer(playerIn, stack), buf -> buf.writeItem(stack));
        }
        return super.use(worldIn, playerIn, handIn);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        ItemUtil.buildItemTooltip(getDescription(), isEnabled(), tooltip);
    }

    public @NotNull Component getDescription() {
        if (description == null) description = TranslationUtil.itemTooltip(getDescriptionId());
        return description;
    }

    public abstract boolean isEnabled();

}
