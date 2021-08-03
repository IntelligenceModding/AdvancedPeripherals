package de.srendi.advancedperipherals.common.addons.computercraft.pocket;

import dan200.computercraft.api.pocket.IPocketAccess;
import de.srendi.advancedperipherals.common.addons.computercraft.base.BasePocket;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.GeoScannerPeripheral;
import de.srendi.advancedperipherals.common.setup.Blocks;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PocketGeoScanner extends BasePocket<GeoScannerPeripheral> {

    public PocketGeoScanner() {
        super(new ResourceLocation("advancedperipherals", "geoscanner_pocket"), "pocket.advancedperipherals.geoscanner_pocket", Blocks.GEO_SCANNER);
    }

    @Nullable
    @Override
    public GeoScannerPeripheral getPeripheral(@NotNull IPocketAccess iPocketAccess) {
        return new GeoScannerPeripheral("geoScanner", iPocketAccess);
    }

}
