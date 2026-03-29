package de.srendi.advancedperipherals.lib.turtle;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.AbstractTurtleUpgrade;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import de.srendi.advancedperipherals.common.setup.APDataComponents;
import de.srendi.advancedperipherals.common.util.DataComponentUtil;
import de.srendi.advancedperipherals.common.util.TranslationUtil;
import de.srendi.advancedperipherals.lib.peripherals.DisabledPeripheral;
import de.srendi.advancedperipherals.lib.peripherals.IBasePeripheral;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class PeripheralTurtleUpgrade<T extends IBasePeripheral<?>> extends AbstractTurtleUpgrade {
    protected int tick = 0;

    protected PeripheralTurtleUpgrade(ResourceLocation id, ItemStack item) {
        super(TurtleUpgradeType.PERIPHERAL, TranslationUtil.turtle(id.getPath()), item);
    }

    public abstract ModelResourceLocation getLeftModel();

    public abstract ModelResourceLocation getRightModel();

    @NotNull
    protected abstract T buildPeripheral(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side);

    @Override
    public ItemStack getUpgradeItem(DataComponentPatch upgradeData) {
        return DataComponentUtil.patchStoredDataToItem(this.getCraftingItem(), upgradeData);
    }

    @Override
    public DataComponentPatch getUpgradeData(ItemStack stack) {
        return DataComponentUtil.getStoredDataFromItem(stack);
    }

    @Override
    public boolean isItemSuitable(@NotNull ItemStack stack) {
        if (stack.has(DataComponents.CUSTOM_NAME) || stack.has(APDataComponents.STORED_DATA)) {
            stack = stack.copy();
            stack.remove(DataComponents.CUSTOM_NAME);
            stack.remove(APDataComponents.STORED_DATA);
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
