package de.srendi.advancedperipherals.common.items.base;

import de.srendi.advancedperipherals.common.util.EnumColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.registries.ObjectHolder;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Optional;

public abstract class BaseItem extends Item {

    @ObjectHolder("computercraft:turtle_normal")
    public static Item TURTLE_NORMAL;
    @ObjectHolder("computercraft:turtle_advanced")
    public static Item TURTLE_ADVANCED;

    @ObjectHolder("computercraft:pocket_computer_normal")
    public static Item POCKET_NORMAL;
    @ObjectHolder("computercraft:pocket_computer_advanced")
    public static Item POCKET_ADVANCED;

    public BaseItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if(worldIn.isRemote)
            return new ActionResult<>(ActionResultType.PASS, playerIn.getHeldItem(handIn));
        if(this instanceof IInventoryItem) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) playerIn;
            NetworkHooks.openGui(serverPlayerEntity, ((IInventoryItem) this).createContainer(playerIn, playerIn.getHeldItem(handIn)));
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (!InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), GLFW.GLFW_KEY_LEFT_CONTROL)) {
            tooltip.add(EnumColor.buildTextComponent(new TranslationTextComponent("item.advancedperipherals.tooltip.hold_ctrl")));
        } else {
            tooltip.add(EnumColor.buildTextComponent(getDescription()));
        }
    }

    protected abstract Optional<String> getTurtleID();

    protected abstract Optional<String> getPocketID();

    protected abstract ITextComponent getDescription();

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        super.fillItemGroup(group, items);
        if (!isInGroup(group))
            return;
        if (getTurtleID().isPresent()) {
            items.add(makeTurtle(TURTLE_ADVANCED, "advancedperipherals:" + getTurtleID().get()));
            items.add(makeTurtle(TURTLE_NORMAL, "advancedperipherals:" + getTurtleID().get()));
        }
        if (getPocketID().isPresent()) {
            items.add(makePocket(POCKET_ADVANCED, "advancedperipherals:" + getPocketID().get()));
            items.add(makePocket(POCKET_NORMAL, "advancedperipherals:" + getPocketID().get()));
        }
    }

    private ItemStack makeTurtle(Item turtle, String upgrade) {
        ItemStack stack = new ItemStack(turtle);
        stack.getOrCreateTag().putString("RightUpgrade", upgrade);
        return stack;
    }

    private ItemStack makePocket(Item turtle, String upgrade) {
        ItemStack stack = new ItemStack(turtle);
        stack.getOrCreateTag().putString("Upgrade", upgrade);
        return stack;
    }

}
