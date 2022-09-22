package de.srendi.advancedperipherals.common.blocks.blockentities;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.SmartGlassesPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralBlockEntity;
import de.srendi.advancedperipherals.common.setup.APBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class SmartGlassesControllerEntity extends PeripheralBlockEntity<SmartGlassesPeripheral> {

    public SmartGlassesControllerEntity(BlockPos pos, BlockState state) {
        super(APBlockEntityTypes.AR_CONTROLLER.get(), pos, state);
    }


    @NotNull
    @Override
    protected SmartGlassesPeripheral createPeripheral() {
        return null;
    }
}
