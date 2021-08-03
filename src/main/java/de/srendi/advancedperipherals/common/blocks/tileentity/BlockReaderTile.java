package de.srendi.advancedperipherals.common.blocks.tileentity;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.BlockReaderPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockReaderTile extends PeripheralTileEntity<BlockReaderPeripheral> {

    public BlockReaderTile(BlockPos pos, BlockState state) {
        super(TileEntityTypes.BLOCK_READER.get(), pos, state);
    }

    @NotNull
    @Override
    protected BlockReaderPeripheral createPeripheral() {
        return new BlockReaderPeripheral("blockReader", this);
    }
}
