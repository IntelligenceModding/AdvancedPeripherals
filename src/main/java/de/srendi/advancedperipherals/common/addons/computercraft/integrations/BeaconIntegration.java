package de.srendi.advancedperipherals.common.addons.computercraft.integrations;

import dan200.computercraft.api.lua.GenericSource;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import net.minecraft.tileentity.BeaconTileEntity;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class BeaconIntegration implements GenericSource {

    @NotNull
    @Override
    public ResourceLocation id() {
        return new ResourceLocation(AdvancedPeripherals.MOD_ID, "beacon");
    }

    @LuaFunction
    public static int getLevel(BeaconTileEntity tileEntity) {
        return tileEntity.getLevels();
    }

    @LuaFunction
    public static String getPrimaryEffect(BeaconTileEntity tileEntity) {
        return tileEntity.primaryPower == null ? "none" : tileEntity.primaryPower.getDescriptionId();
    }

    @LuaFunction
    public static String getSecondaryEffect(BeaconTileEntity tileEntity) {
        return tileEntity.secondaryPower == null ? "none" : tileEntity.secondaryPower.getDescriptionId();
    }

}
