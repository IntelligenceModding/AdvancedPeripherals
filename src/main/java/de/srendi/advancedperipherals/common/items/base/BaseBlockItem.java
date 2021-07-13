package de.srendi.advancedperipherals.common.items.base;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.util.EnumColor;
import de.srendi.advancedperipherals.common.util.ItemUtil;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

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
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if (!InputMappings.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_CONTROL)) {
            tooltip.add(EnumColor.buildTextComponent(new TranslationTextComponent("item.advancedperipherals.tooltip.hold_ctrl")));
        } else {
            tooltip.add(EnumColor.buildTextComponent(getDescription()));
        }
        if (!isEnabled())
            tooltip.add(EnumColor.buildTextComponent(new TranslationTextComponent("item.advancedperipherals.tooltip.disabled")));
    }

    public abstract Optional<String> getTurtleID();

    public abstract Optional<String> getPocketID();

    public abstract ITextComponent getDescription();

    public abstract boolean isEnabled();

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
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
