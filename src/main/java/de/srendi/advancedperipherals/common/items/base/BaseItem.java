package de.srendi.advancedperipherals.common.items.base;

import de.srendi.advancedperipherals.client.KeyBindings;
import de.srendi.advancedperipherals.common.util.EnumColor;
import de.srendi.advancedperipherals.common.util.KeybindUtil;
import de.srendi.advancedperipherals.common.util.TranslationUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class BaseItem extends Item {
    private Component description;

    public BaseItem(Properties properties) {
        super(properties);
    }

    public BaseItem() {
        super(new Properties());
    }

    @Override
    @NotNull
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        if (worldIn.isClientSide) {
            return InteractionResultHolder.pass(playerIn.getItemInHand(handIn));
        }
        return super.use(worldIn, playerIn, handIn);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        if (!KeybindUtil.isKeyPressed(KeyBindings.DESCRIPTION_KEYBINDING)) {
            tooltip.add(EnumColor.buildTextComponent(Component.translatable("item.advancedperipherals.tooltip.show_desc", KeyBindings.DESCRIPTION_KEYBINDING.getTranslatedKeyMessage())));
        } else {
            tooltip.add(EnumColor.buildTextComponent(getDescription()));
        }
        if (!isEnabled())
            tooltip.add(EnumColor.buildTextComponent(Component.translatable("item.advancedperipherals.tooltip.disabled")));
    }

    @Override
    @NotNull
    public Component getDescription() {
        if (description == null) {
            description = TranslationUtil.itemTooltip(getDescriptionId());
        }
        return description;
    }

    public abstract boolean isEnabled();

}
