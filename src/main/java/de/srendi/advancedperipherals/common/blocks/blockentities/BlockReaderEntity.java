package de.srendi.advancedperipherals.common.blocks.blockentities;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.BlockReaderPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralBlockEntity;
import de.srendi.advancedperipherals.common.setup.APBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockReaderEntity extends PeripheralBlockEntity<BlockReaderPeripheral> {

    public BlockReaderEntity(BlockPos pos, BlockState state) {
        super(APBlockEntityTypes.BLOCK_READER.get(), pos, state);
    }

    @Override
    @NotNull
    protected BlockReaderPeripheral buildPeripheral() {
        return new BlockReaderPeripheral(this);
    }
}
