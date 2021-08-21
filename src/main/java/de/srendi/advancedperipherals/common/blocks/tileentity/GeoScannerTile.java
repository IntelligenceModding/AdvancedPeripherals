package de.srendi.advancedperipherals.common.blocks.tileentity;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.GeoScannerPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PoweredPeripheralTileEntity;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class GeoScannerTile extends PoweredPeripheralTileEntity<GeoScannerPeripheral> {

    public GeoScannerTile(BlockPos pos, BlockState state) {
        super(TileEntityTypes.GEO_SCANNER.get(), pos, state);
    }

    @Override
    protected int getMaxEnergyStored() {
        return AdvancedPeripheralsConfig.poweredPeripheralMaxEnergyStored;
    }

    @Override
    protected @NotNull GeoScannerPeripheral createPeripheral() {
        return new GeoScannerPeripheral(this);
    }

}