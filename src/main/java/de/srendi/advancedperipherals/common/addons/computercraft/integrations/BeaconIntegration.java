package de.srendi.advancedperipherals.common.addons.computercraft.integrations;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.lib.peripherals.TileEntityIntegrationPeripheral;
import net.minecraft.tileentity.BeaconTileEntity;
import net.minecraft.tileentity.TileEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BeaconIntegration extends TileEntityIntegrationPeripheral<BeaconTileEntity> {

    public BeaconIntegration(TileEntity entity) {
        super(entity);
    }

    @Override
    public @NotNull String getType() {
        return "beacon";
    }

    @Override
    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return false;
    }

    @LuaFunction(mainThread = true)
    public final int getLevel() {
        return tileEntity.getLevels();
    }

    @LuaFunction(mainThread = true)
    public final String getPrimaryEffect() {
        return tileEntity.primaryPower == null ? "none" : tileEntity.primaryPower.getDescriptionId();
    }

    @LuaFunction(mainThread = true)
    public final String getSecondaryEffect() {
        return tileEntity.secondaryPower == null ? "none" : tileEntity.secondaryPower.getDescriptionId();
    }

}
