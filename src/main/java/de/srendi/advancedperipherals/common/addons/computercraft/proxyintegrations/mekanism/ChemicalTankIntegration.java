package de.srendi.advancedperipherals.common.addons.computercraft.proxyintegrations.mekanism;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.shared.peripheral.generic.data.DataHelpers;
import de.srendi.advancedperipherals.common.addons.computercraft.base.ProxyIntegration;
import mekanism.common.tile.TileEntityChemicalTank;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.chemical.merged.MergedChemicalTank;

public class ChemicalTankIntegration extends ProxyIntegration<TileEntityChemicalTank> {
    @Override
    protected Class<TileEntityChemicalTank> getTargetClass() {
        return TileEntityChemicalTank.class;
    }

    @Override
    public ChemicalTankIntegration getNewInstance() {
        return new ChemicalTankIntegration();
    }

    @Override
    protected String getName() {
        return "chemicalTank";
    }

    @LuaFunction
    public final long getTankCapacity() {
        return getTank().getCapacity();
    }

    @LuaFunction
    public final long getFillLevel() {
        return getTank().getStored();
    }

    @LuaFunction
    public final long getFreeSpace() {
        return getTank().getNeeded();
    }

    @LuaFunction
    public final String getChemicalType() {
        return getMergedTank().getCurrent().name();
    }

    @LuaFunction
    public final String getChemical() {
        return DataHelpers.getId(getTank().getStack().getType());
    }

    private IChemicalTank<?, ?> getTank() {
        MergedChemicalTank mergedTank = getMergedTank();
        if (mergedTank.getCurrent() != MergedChemicalTank.Current.EMPTY) {
            return mergedTank.getTankFromCurrent(mergedTank.getCurrent());
        }
        return mergedTank.getGasTank();
    }

    private MergedChemicalTank getMergedTank() {
        return getTileEntity().getChemicalTank();
    }
}