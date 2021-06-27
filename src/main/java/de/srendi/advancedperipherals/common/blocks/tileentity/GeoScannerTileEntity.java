package de.srendi.advancedperipherals.common.blocks.tileentity;

import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.GeoScannerPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.setup.TileEntityTypes;
import net.minecraft.tileentity.TileEntityType;

public class GeoScannerTileEntity extends PeripheralTileEntity<GeoScannerPeripheral> {

    public GeoScannerTileEntity() {
        this(TileEntityTypes.GEO_SCANNER.get());
    }

    public GeoScannerTileEntity(final TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    @Override
    protected GeoScannerPeripheral createPeripheral() {
        return new GeoScannerPeripheral("geoScanner", this);
    }
}