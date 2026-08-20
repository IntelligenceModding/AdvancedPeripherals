package de.srendi.advancedperipherals.common.addons.computercraft.pocket;

import dan200.computercraft.api.pocket.IPocketAccess;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.GeoScannerPeripheral;
import de.srendi.advancedperipherals.common.setup.CCRegistration;
import de.srendi.advancedperipherals.lib.pocket.BasePocketUpgrade;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PocketGeoScannerUpgrade extends BasePocketUpgrade<GeoScannerPeripheral> {

    public PocketGeoScannerUpgrade(ItemStack stack) {
        super(CCRegistration.ID.Pocket.GEOSCANNER, stack);
    }

    @Override
    @NotNull
    protected GeoScannerPeripheral buildPeripheral(@NotNull IPocketAccess iPocketAccess) {
        return new GeoScannerPeripheral(iPocketAccess);
    }
}
