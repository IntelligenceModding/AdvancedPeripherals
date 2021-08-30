package de.srendi.advancedperipherals.common.addons.computercraft.integrations.mekanism;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.addons.computercraft.base.Integration;
import mekanism.common.tile.TileEntityRadioactiveWasteBarrel;
import org.jetbrains.annotations.NotNull;

public class WasteBarrelIntegration extends Integration<TileEntityRadioactiveWasteBarrel> {

    @Override
    protected Class<TileEntityRadioactiveWasteBarrel> getTargetClass() {
        return TileEntityRadioactiveWasteBarrel.class;
    }

    @Override
    public Integration<?> getNewInstance() {
        return new WasteBarrelIntegration();
    }

    @NotNull
    @Override
    public String getType() {
        return "wasteBarrel";
    }

    @LuaFunction
    public final float getStored() {
        return getTileEntity().getGasTank().getStored();
    }

    @LuaFunction
    public final float getCapacity() {
        return getTileEntity().getGasTank().getCapacity();
    }

    @LuaFunction
    public final float getFilledPercentage() {
        return getTileEntity().getGasScale();
    }
}
