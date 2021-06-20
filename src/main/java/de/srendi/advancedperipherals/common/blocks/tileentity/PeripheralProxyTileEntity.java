package de.srendi.advancedperipherals.common.blocks.tileentity;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.BlockReaderPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;

public class PeripheralProxyTileEntity extends PeripheralTileEntity<BlockReaderPeripheral> {

    public PeripheralProxyTileEntity() {
        super(TileEntityTypes.BLOCK_READER.get());
    }

    @Override
    protected BlockReaderPeripheral createPeripheral() {
        return new BlockReaderPeripheral("blockReader", this);
    }
}
