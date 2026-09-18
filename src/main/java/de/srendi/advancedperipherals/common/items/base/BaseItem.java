package de.srendi.advancedperipherals.common.items.base;

import de.srendi.advancedperipherals.common.util.EnumColor;
import de.srendi.advancedperipherals.common.util.TranslationUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public abstract class BaseItem extends Item implements IAPTooltipItem {
    private Component tooltipComponent;

    public BaseItem(Properties properties) {
        super(properties);
    }

    public BaseItem() {
        super(new Properties());
    }

    @Override
    public Component getTooltipComponent() {
        if (this.tooltipComponent == null) {
            this.tooltipComponent = EnumColor.buildTextComponent(Component.translatable(TranslationUtil.tooltip(getDescriptionId())));
        }
        return this.tooltipComponent;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        IAPTooltipItem.appendTooltip(stack, context, tooltip, flag);
    }
}
