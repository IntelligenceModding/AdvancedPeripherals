package de.srendi.advancedperipherals.common.blocks.tileentity;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.GeoScannerPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PoweredPeripheralTileEntity;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
import net.minecraft.tileentity.TileEntityType;
import org.jetbrains.annotations.NotNull;

public class GeoScannerTile extends PoweredPeripheralTileEntity<GeoScannerPeripheral> {

    public GeoScannerTile() {
        this(TileEntityTypes.GEO_SCANNER.get());
    }

    public GeoScannerTile(final TileEntityType<?> tileEntityType) {
        super(tileEntityType);
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