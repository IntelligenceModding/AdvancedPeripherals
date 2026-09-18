package de.srendi.advancedperipherals.common.items.base;

import de.srendi.advancedperipherals.client.KeyBindings;
import de.srendi.advancedperipherals.common.setup.APTranslations;
import de.srendi.advancedperipherals.common.util.EnumColor;
import de.srendi.advancedperipherals.common.util.KeybindUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public interface IAPTooltipItem {
    Component TOOLTIP_DISABLED = EnumColor.buildTextComponent(Component.translatable(APTranslations.TOOLTIP_DISABLED));

    Component getTooltipComponent();

    boolean isEnabled();

    static void appendTooltip(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        IAPTooltipItem item = (IAPTooltipItem) stack.getItem();
        if (!KeybindUtil.isKeyPressed(KeyBindings.DESCRIPTION_KEYBINDING)) {
            tooltip.add(EnumColor.buildTextComponent(Component.translatable(APTranslations.TOOLTIP_SHOW_DESC, KeyBindings.DESCRIPTION_KEYBINDING.getTranslatedKeyMessage())));
        } else {
            tooltip.add(item.getTooltipComponent());
        }
        if (!item.isEnabled()) {
            tooltip.add(TOOLTIP_DISABLED);
        }
    }
}
