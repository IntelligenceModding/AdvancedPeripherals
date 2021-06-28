package de.srendi.advancedperipherals.common.blocks.tileentity;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.EnvironmentDetectorPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
import net.minecraft.tileentity.TileEntityType;
import org.jetbrains.annotations.NotNull;

public class EnvironmentDetectorTile extends PeripheralTileEntity<EnvironmentDetectorPeripheral> {

    public EnvironmentDetectorTile() {
        this(TileEntityTypes.ENVIRONMENT_DETECTOR.get());
    }

    public EnvironmentDetectorTile(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @NotNull
    @Override
    protected EnvironmentDetectorPeripheral createPeripheral() {
        return new EnvironmentDetectorPeripheral("environmentDetector", this);
    }


}
