package de.srendi.advancedperipherals.common.addons.computercraft.integrations.mekanism;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.shared.peripheral.generic.data.DataHelpers;
import de.srendi.advancedperipherals.common.addons.computercraft.base.Integration;
import mekanism.common.tile.TileEntityChemicalTank;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.chemical.merged.MergedChemicalTank;

import java.util.HashMap;
import java.util.Map;

public class ChemicalTankIntegration extends Integration<TileEntityChemicalTank> {
    @Override
    protected Class<TileEntityChemicalTank> getTargetClass() {
        return TileEntityChemicalTank.class;
    }

    @Override
    public ChemicalTankIntegration getNewInstance() {
        return new ChemicalTankIntegration();
    }

    @Override
    public String getType() {
        return "chemicalTank";
    }

    @LuaFunction
    public final Map<String, Object> getStored() {
        Map<String, Object> result = new HashMap<>(3);
        result.put("name", DataHelpers.getId(getTank().getStack().getType()));
        result.put("amount", getTank().getStored());
        result.put("type", getMergedTank().getCurrent().name());
        return result;
    }

    @LuaFunction
    public final long getCapacity() {
        return getTank().getCapacity();
    }

    @LuaFunction
    public final double getFilledPercentage() {
        return getTank().getStored() / (double) getCapacity();
    }

    @LuaFunction
    public final long getNeeded() {
        return getTank().getNeeded();
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
