package de.srendi.advancedperipherals.common.blocks.blockentities;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.GeoScannerPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PoweredPeripheralBlockEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.setup.APBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class GeoScannerEntity extends PoweredPeripheralBlockEntity<GeoScannerPeripheral> {

    public GeoScannerEntity(BlockPos pos, BlockState state) {
        super(APBlockEntityTypes.GEO_SCANNER.get(), pos, state);
    }

    @Override
    protected int getMaxEnergyStored() {
        return APConfig.PERIPHERALS_CONFIG.poweredPeripheralMaxEnergyStorage.get();
    }

    @Override
    protected @NotNull GeoScannerPeripheral createPeripheral() {
        return new GeoScannerPeripheral(this);
    }

}
