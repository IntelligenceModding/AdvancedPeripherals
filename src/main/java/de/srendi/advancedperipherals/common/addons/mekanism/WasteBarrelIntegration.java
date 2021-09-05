package de.srendi.advancedperipherals.common.addons.mekanism;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.TileEntityIntegrationPeripheral;
import mekanism.common.tile.TileEntityRadioactiveWasteBarrel;
import net.minecraft.tileentity.TileEntity;
import org.jetbrains.annotations.NotNull;

public class WasteBarrelIntegration extends TileEntityIntegrationPeripheral<TileEntityRadioactiveWasteBarrel> {

    public WasteBarrelIntegration(TileEntity entity) {
        super(entity);
    }

    @NotNull
    @Override
    public String getType() {
        return "wasteBarrel";
    }

    @LuaFunction(mainThread = true)
    public final float getStored() {
        return tileEntity.getGasTank().getStored();
    }

    @LuaFunction(mainThread = true)
    public final float getCapacity() {
        return tileEntity.getGasTank().getCapacity();
    }

    @LuaFunction(mainThread = true)
    public final float getFilledPercentage() {
        return tileEntity.getGasScale();
    }
}
