package de.srendi.advancedperipherals.common.blocks.tileentity;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.EnvironmentDetectorPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
import net.minecraft.tileentity.TileEntityType;

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
