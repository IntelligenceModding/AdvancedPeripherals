package de.srendi.advancedperipherals.common.blocks.base;

import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.BasePeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.ChatBoxPeripheral;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static dan200.computercraft.shared.Capabilities.CAPABILITY_PERIPHERAL;

public abstract class PeripheralTileEntity<T extends BasePeripheral> extends TileEntity {

    protected T peripheral = createPeripheral();
    private LazyOptional<IPeripheral> peripheralCap;

    public PeripheralTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction direction) {
        if (cap == CAPABILITY_PERIPHERAL) {
            if (peripheralCap == null) {
                peripheralCap = LazyOptional.of(()->peripheral);
            }
            return peripheralCap.cast();
        }
        return super.getCapability(cap, direction);
    }

    @Override
    protected void invalidateCaps() {
        super.invalidateCaps();
        if(peripheralCap != null)
            peripheralCap.invalidate();
    }

    protected abstract T createPeripheral();

    public List<IComputerAccess> getConnectedComputers() {
        return peripheral.getConnectedComputers();
    }

}

