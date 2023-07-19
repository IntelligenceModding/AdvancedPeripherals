package de.srendi.advancedperipherals.common.items.base;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.util.TranslationUtil;
import de.srendi.advancedperipherals.common.util.inventory.ItemUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class BaseBlockItem extends BlockItem {
    private Component description;

    public BaseBlockItem(Block blockIn, Properties properties) {
        super(blockIn, properties.tab(AdvancedPeripherals.TAB));
    }

    public BaseBlockItem(Block blockIn) {
        super(blockIn, new Properties().tab(AdvancedPeripherals.TAB));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level levelIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        super.appendHoverText(stack, levelIn, tooltip, flagIn);
        ItemUtil.buildItemTooltip(getDescription(), isEnabled(), tooltip);
    }

    public @NotNull Component getDescription() {
        if (description == null) description = TranslationUtil.itemTooltip(getDescriptionId());
        return description;
    }

    public abstract boolean isEnabled();
}
