package de.srendi.advancedperipherals.common.items.base;

import de.srendi.advancedperipherals.client.KeyBindings;
import de.srendi.advancedperipherals.common.setup.APTranslations;
import de.srendi.advancedperipherals.common.util.EnumColor;
import de.srendi.advancedperipherals.common.util.KeybindUtil;
import de.srendi.advancedperipherals.common.util.TranslationUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.List;

public abstract class BaseBlockItem extends BlockItem {
    private Component tooltipComponent;

    public BaseBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    public BaseBlockItem(Block block) {
        super(block, new Properties());
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
