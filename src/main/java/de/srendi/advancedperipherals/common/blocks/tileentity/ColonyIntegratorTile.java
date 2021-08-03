package de.srendi.advancedperipherals.common.blocks.tileentity;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.ColonyPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ColonyIntegratorTile extends PeripheralTileEntity<ColonyPeripheral> {

    public ColonyIntegratorTile(BlockPos pos, BlockState state) {
        super(TileEntityTypes.COLONY_INTEGRATOR.get(), pos, state);
    }

    @NotNull
    @Override
    protected ColonyPeripheral createPeripheral() {
        return new ColonyPeripheral("colonyIntegrator", this);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return null;
    }
}
