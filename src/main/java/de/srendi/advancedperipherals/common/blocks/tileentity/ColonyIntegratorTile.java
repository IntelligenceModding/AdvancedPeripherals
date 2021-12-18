package de.srendi.advancedperipherals.common.blocks.tileentity;

import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ColonyIntegratorTile extends PeripheralTileEntity<ColonyPeripheral> {

    public ColonyIntegratorTile(BlockPos pos, BlockState state) {
        super(TileEntityTypes.COLONY_INTEGRATOR.get(), pos, state);
    }

    @NotNull
    @Override
    protected ColonyPeripheral createPeripheral() {
        return new ColonyPeripheral(this);
    }
}