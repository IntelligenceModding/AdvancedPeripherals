package de.srendi.advancedperipherals.common.items.base;

import de.srendi.advancedperipherals.common.util.EnumColor;
import de.srendi.advancedperipherals.common.util.TranslationUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.List;

public abstract class BaseBlockItem extends BlockItem implements IAPTooltipItem {
    private Component tooltipComponent;

    public BaseBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    public BaseBlockItem(Block block) {
        super(block, new Properties());
    }

    @Override
    public Component getTooltipComponent() {
        if (this.tooltipComponent == null) {
            this.tooltipComponent = EnumColor.buildTextComponent(Component.translatable(TranslationUtil.tooltip(getDescriptionId())));
        }
        return this.tooltipComponent;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        IAPTooltipItem.appendTooltip(stack, level, tooltip, flag);
    }
}
