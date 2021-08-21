package de.srendi.advancedperipherals.common.blocks.tileentity;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.BlockReaderPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
import org.jetbrains.annotations.NotNull;

public class BlockReaderTile extends PeripheralTileEntity<BlockReaderPeripheral> {

    public BlockReaderTile() {
        super(TileEntityTypes.BLOCK_READER.get());
    }

    @NotNull
    @Override
    protected BlockReaderPeripheral createPeripheral() {
        return new BlockReaderPeripheral(this);
    }
}
