package de.srendi.advancedperipherals.common.blocks.tileentity;

import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.EnvironmentDetectorPeripheral;
import de.srendi.advancedperipherals.common.setup.ModTileEntityTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static dan200.computercraft.shared.Capabilities.CAPABILITY_PERIPHERAL;

public class EnvironmentDetectorTileEntiy extends TileEntity {

    private EnvironmentDetectorPeripheral peripheral;
    private LazyOptional<IPeripheral> peripheralCap;

    public EnvironmentDetectorTileEntiy() {
        this(ModTileEntityTypes.ENVIRONMENT_DETECTOR.get());
        peripheral = new EnvironmentDetectorPeripheral("environmentDetector", this);
    }

    public EnvironmentDetectorTileEntiy(TileEntityType<?> tileEntityTypeIn) {
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
    public void validate() {
        super.validate();
    }

    @Override
    protected void invalidateCaps() {
        super.invalidateCaps();
        if(peripheralCap != null)
            peripheralCap.invalidate();
    }

}
