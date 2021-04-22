package de.srendi.advancedperipherals.common.blocks.tileentity;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.ARControllerPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
import net.minecraft.tileentity.TileEntityType;

public class ARControllerTileEntity extends PeripheralTileEntity<ARControllerPeripheral> {
	public ARControllerTileEntity() {
        this(TileEntityTypes.AR_CONTROLLER.get());
    }

    public ARControllerTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Override
    protected ARControllerPeripheral createPeripheral() {
        return new ARControllerPeripheral("arController", this);
    }
}
