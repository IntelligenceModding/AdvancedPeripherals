package de.srendi.advancedperipherals.common.items.base;

import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.client.KeyBindings;
import de.srendi.advancedperipherals.common.util.EnumColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
            NetworkHooks.openGui(serverPlayerEntity, ((IInventoryItem) this).createContainer(playerIn, stack), buf -> buf.writeItem(stack));
        }
        return super.use(worldIn, playerIn, handIn);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        if (!KeyBindings.DESCRIPTION_KEYBINDING.isDown()) {
            tooltip.add(EnumColor.buildTextComponent(new TranslationTextComponent("item.advancedperipherals.tooltip.show_desc", KeyBindings.DESCRIPTION_KEYBINDING.getTranslatedKeyMessage())));
        } else {
            tooltip.add(EnumColor.buildTextComponent(getDescription()));
        }
        if (!isEnabled())
            tooltip.add(EnumColor.buildTextComponent(new TranslationTextComponent("item.advancedperipherals.tooltip.disabled")));
    }

    public abstract ITextComponent getDescription();

    public abstract boolean isEnabled();


}
