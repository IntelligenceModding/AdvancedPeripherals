package de.srendi.advancedperipherals.common.blocks.tileentity;

import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.EnvironmentDetectorPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static dan200.computercraft.shared.Capabilities.CAPABILITY_PERIPHERAL;

public class EnvironmentDetectorTileEntiy extends PeripheralTileEntity<EnvironmentDetectorPeripheral> {

    public EnvironmentDetectorTileEntiy() {
        this(TileEntityTypes.ENVIRONMENT_DETECTOR.get());
    }

    public EnvironmentDetectorTileEntiy(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Override
    protected EnvironmentDetectorPeripheral createPeripheral() {
        return new EnvironmentDetectorPeripheral("environmentDetector", this);
    }


}
