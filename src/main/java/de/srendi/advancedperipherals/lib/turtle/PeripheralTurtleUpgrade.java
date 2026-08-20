package de.srendi.advancedperipherals.lib.turtle;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.AbstractTurtleUpgrade;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import de.srendi.advancedperipherals.common.setup.APDataComponents;
import de.srendi.advancedperipherals.common.util.TranslationUtil;
import de.srendi.advancedperipherals.lib.peripherals.DisabledPeripheral;
import de.srendi.advancedperipherals.lib.peripherals.IBasePeripheral;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class PeripheralTurtleUpgrade<T extends IBasePeripheral<?>> extends AbstractTurtleUpgrade {
    protected int tick = 0;

    protected PeripheralTurtleUpgrade(ResourceLocation id, ItemStack item) {
        super(id, TurtleUpgradeType.PERIPHERAL, TranslationUtil.turtle(id.getPath()), item);
    }

    public abstract ModelResourceLocation getLeftModel();

    public abstract ModelResourceLocation getRightModel();

    @NotNull
    protected abstract T buildPeripheral(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side);

    @Override
    public ItemStack getUpgradeItem(CompoundTag upgradeData) {
        ItemStack stack = this.getCraftingItem().copy();
        CompoundTag data = upgradeData.getCompound(APDataComponents.STORED_DATA);
        stack.getOrCreateTag().put("BlockEntityTag", data);
        String name = data.getString("CustomName");
        if (!name.isEmpty()) {
            stack.setHoverName(Component.literal(name));
        }
        return stack;
    }

    @Override
    public CompoundTag getUpgradeData(ItemStack stack) {
        CompoundTag data = stack.getTagElement("BlockEntityTag");
        if (data == null) {
            data = new CompoundTag();
        }
        if (stack.hasCustomHoverName()) {
            data.putString("CustomName", stack.getHoverName().getString());
        }
        CompoundTag wrapped = new CompoundTag();
        wrapped.put(APDataComponents.STORED_DATA, data);
        return wrapped;
    }

    @Override
    public boolean isItemSuitable(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && (tag.contains("display") || tag.contains("BlockEntityTag"))) {
            stack = stack.copy();
            stack.removeTagKey("display");
            stack.removeTagKey("BlockEntityTag");
        }
        return super.isItemSuitable(stack);
    }

    @Override
    @Nullable
    public IPeripheral createPeripheral(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
        T peripheral = buildPeripheral(turtle, side);
        return peripheral.isEnabled() ? peripheral : new DisabledPeripheral(peripheral);
    }

    @Override
    public void update(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
        super.update(turtle, side);
        this.tick++;
        if (!turtle.getLevel().isClientSide() && turtle.getPeripheral(side) instanceof IBasePeripheral<?> basePeripheral) {
            basePeripheral.update();
        }
    }
}
