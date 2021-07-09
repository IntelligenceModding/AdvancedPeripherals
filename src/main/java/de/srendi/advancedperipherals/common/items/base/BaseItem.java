package de.srendi.advancedperipherals.common.items.base;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.util.EnumColor;
import de.srendi.advancedperipherals.common.util.ItemUtil;
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
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Optional;

public abstract class BaseItem extends Item {

    public BaseItem(Properties properties) {
        super(properties.tab(AdvancedPeripherals.TAB));
    }

    public BaseItem() {
        super(new Properties().tab(AdvancedPeripherals.TAB));
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (worldIn.isClientSide)
            return new ActionResult<>(ActionResultType.PASS, playerIn.getItemInHand(handIn));
        if (this instanceof IInventoryItem) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) playerIn;
            ItemStack stack = playerIn.getItemInHand(handIn);
            NetworkHooks.openGui(serverPlayerEntity, ((IInventoryItem) this).createContainer(playerIn, stack), buf -> {
                buf.writeItem(stack);
            });
        }
        return super.use(worldIn, playerIn, handIn);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if (!InputMappings.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_CONTROL)) {
            tooltip.add(EnumColor.buildTextComponent(new TranslationTextComponent("item.advancedperipherals.tooltip.hold_ctrl")));
        } else {
            tooltip.add(EnumColor.buildTextComponent(getDescription()));
        }
    }

    public abstract Optional<String> getTurtleID();

    public abstract Optional<String> getPocketID();

    public abstract ITextComponent getDescription();

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
