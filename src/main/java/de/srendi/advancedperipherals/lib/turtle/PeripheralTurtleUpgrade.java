package de.srendi.advancedperipherals.lib.turtle;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.AbstractTurtleUpgrade;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import de.srendi.advancedperipherals.common.configuration.UnsafeConfig;
import de.srendi.advancedperipherals.common.util.TranslationUtil;
import de.srendi.advancedperipherals.lib.peripherals.DisabledPeripheral;
import de.srendi.advancedperipherals.lib.peripherals.IBasePeripheral;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class PeripheralTurtleUpgrade<T extends IBasePeripheral<?>> extends AbstractTurtleUpgrade {
    protected int tick;

    protected PeripheralTurtleUpgrade(ResourceLocation id, ItemStack item) {
        super(id, TurtleUpgradeType.PERIPHERAL, TranslationUtil.turtle(id.getPath()), item);
    }

    // TODO: Do we still need this with the new modeller system?
    public abstract ModelResourceLocation getLeftModel();

    public abstract ModelResourceLocation getRightModel();

    protected abstract T buildPeripheral(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side);

    @Nullable
    @Override
    public IPeripheral createPeripheral(@NotNull ITurtleAccess turtle, @NotNull TurtleSide side) {
        T peripheral = buildPeripheral(turtle, side);
        if (!peripheral.isEnabled()) {
            return DisabledPeripheral.INSTANCE;
        }
        return peripheral;
    }

    @Override
    public boolean isItemSuitable(@NotNull ItemStack stack) {
        if (UnsafeConfig.getIgnoreTurtlePeripheralItemNBT()) {
            // always accept NBTed items
            return true;
        }
        return super.isItemSuitable(stack);
    }
}
