package de.srendi.advancedperipherals.common.blocks.base;

import static dan200.computercraft.shared.Capabilities.CAPABILITY_PERIPHERAL;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.base.BasePeripheral;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public abstract class PeripheralTileEntity<T extends BasePeripheral> extends TileEntity {

    protected T peripheral = createPeripheral();
    private LazyOptional<IPeripheral> peripheralCap;

    public PeripheralTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Override
    public <T0> LazyOptional<T0> getCapability(@NotNull Capability<T0> cap, @Nullable Direction direction) {
        if (peripheral.isEnabled()) {
            if (cap == CAPABILITY_PERIPHERAL) {
                if (peripheralCap == null) {
                    peripheralCap = LazyOptional.of(()->peripheral);
                }
                return peripheralCap.cast();
            }
        } else {
            AdvancedPeripherals.Debug(peripheral.getType() + " is disabled, you can enable it in the Configuration.");
        }
        return super.getCapability(cap, direction);
    }

    @Override
    protected void invalidateCaps() {
        super.invalidateCaps();
        if (peripheralCap != null)
            peripheralCap.invalidate();
    }

    protected abstract T createPeripheral();

    public List<IComputerAccess> getConnectedComputers() {
        return peripheral.getConnectedComputers();
    }

}

