package de.srendi.advancedperipherals.common.blocks.blockentities;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.ColonyPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralBlockEntity;
import de.srendi.advancedperipherals.common.setup.BlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ColonyIntegratorEntity extends PeripheralBlockEntity<ColonyPeripheral> {

    public ColonyIntegratorEntity(BlockPos pos, BlockState state) {
        super(BlockEntityTypes.COLONY_INTEGRATOR.get(), pos, state);
    }

    @NotNull
    @Override
    protected ColonyPeripheral createPeripheral() {
        return new ColonyPeripheral(this);
    }
}
