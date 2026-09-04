package de.srendi.advancedperipherals.common.items.base;

import de.srendi.advancedperipherals.client.KeyBindings;
import de.srendi.advancedperipherals.common.setup.APTranslations;
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
    private Component tooltipComponent;

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
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, level, tooltip, flagIn);

        if (!KeybindUtil.isKeyPressed(KeyBindings.DESCRIPTION_KEYBINDING)) {
            tooltip.add(EnumColor.buildTextComponent(Component.translatable(APTranslations.TOOLTIP_SHOW_DESC, KeyBindings.DESCRIPTION_KEYBINDING.getTranslatedKeyMessage())));
        } else {
            if (this.tooltipComponent == null) {
                this.tooltipComponent = Component.translatable(TranslationUtil.tooltip(getDescriptionId()));
            }
            tooltip.add(EnumColor.buildTextComponent(this.tooltipComponent));
        }
        if (!isEnabled()) {
            tooltip.add(EnumColor.buildTextComponent(Component.translatable(APTranslations.TOOLTIP_DISABLED)));
        }
    }

    public abstract boolean isEnabled();
}
