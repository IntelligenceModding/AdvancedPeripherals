package de.srendi.advancedperipherals.common.blocks.tileentity;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.GeoScannerPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PoweredPeripheralTileEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class GeoScannerTile extends PoweredPeripheralTileEntity<GeoScannerPeripheral> {

    public GeoScannerTile(BlockPos pos, BlockState state) {
        super(TileEntityTypes.GEO_SCANNER.get(), pos, state);
    }

    @Override
    protected int getMaxEnergyStored() {
        return APConfig.PERIPHERALS_CONFIG.POWERED_PERIPHERAL_MAX_ENERGY_STORAGE.get();
    }

    @Override
    protected @NotNull GeoScannerPeripheral createPeripheral() {
        return new GeoScannerPeripheral(this);
    }

}