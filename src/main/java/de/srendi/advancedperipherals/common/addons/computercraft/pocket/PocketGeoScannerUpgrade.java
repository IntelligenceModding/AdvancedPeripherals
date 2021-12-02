package de.srendi.advancedperipherals.common.addons.computercraft.pocket;

import dan200.computercraft.api.pocket.IPocketAccess;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.lib.pocket.BasePocketUpgrade;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.GeoScannerPeripheral;
import de.srendi.advancedperipherals.common.setup.Blocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PocketGeoScannerUpgrade extends BasePocketUpgrade<GeoScannerPeripheral> {
    public static final ResourceLocation ID = new ResourceLocation(AdvancedPeripherals.MOD_ID, "geoscanner_pocket");

    public PocketGeoScannerUpgrade(ResourceLocation id, ItemStack stack) {
        super(id, stack);
    }

    @Nullable
    @Override
    public GeoScannerPeripheral getPeripheral(@NotNull IPocketAccess iPocketAccess) {
        return new GeoScannerPeripheral(iPocketAccess);
    }

}
