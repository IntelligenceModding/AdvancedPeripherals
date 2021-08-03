package de.srendi.advancedperipherals.common.items.base;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.KeyBindings;
import de.srendi.advancedperipherals.common.util.EnumColor;
import de.srendi.advancedperipherals.common.util.ItemUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public abstract class BaseBlockItem extends BlockItem {

    public BaseBlockItem(Block blockIn, Properties properties) {
        super(blockIn, properties.tab(AdvancedPeripherals.TAB));
    }

    public BaseBlockItem(Block blockIn) {
        super(blockIn, new Properties().tab(AdvancedPeripherals.TAB));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if (!KeyBindings.DESCRIPTION_KEYBINDING.isDown()) {
            tooltip.add(EnumColor.buildTextComponent(new TranslatableComponent("item.advancedperipherals.tooltip.show_desc", KeyBindings.DESCRIPTION_KEYBINDING.getTranslatedKeyMessage())));
        } else {
            tooltip.add(EnumColor.buildTextComponent(getDescription()));
        }
        if (!isEnabled())
            tooltip.add(EnumColor.buildTextComponent(new TranslatableComponent("item.advancedperipherals.tooltip.disabled")));
    }

    public abstract Optional<String> getTurtleID();

    public abstract Optional<String> getPocketID();

    public abstract Component getDescription();

    public abstract boolean isEnabled();

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        super.fillItemCategory(group, items);
        if (!allowdedIn(group))
            return;
        if (getTurtleID().isPresent()) {
            items.add(ItemUtil.makeTurtle(ItemUtil.TURTLE_ADVANCED, AdvancedPeripherals.MOD_ID + ":" + getTurtleID().get()));
            items.add(ItemUtil.makeTurtle(ItemUtil.TURTLE_NORMAL, AdvancedPeripherals.MOD_ID + ":" + getTurtleID().get()));
        }
        if (getPocketID().isPresent()) {
            items.add(ItemUtil.makePocket(ItemUtil.POCKET_ADVANCED, AdvancedPeripherals.MOD_ID + ":" + getPocketID().get()));
            items.add(ItemUtil.makePocket(ItemUtil.POCKET_NORMAL, AdvancedPeripherals.MOD_ID + ":" + getPocketID().get()));
        }
    }

}
