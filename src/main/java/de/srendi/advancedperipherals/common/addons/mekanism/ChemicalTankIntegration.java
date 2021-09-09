package de.srendi.advancedperipherals.common.addons.mekanism;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.shared.peripheral.generic.data.DataHelpers;
import de.srendi.advancedperipherals.lib.peripherals.TileEntityIntegrationPeripheral;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.chemical.merged.MergedChemicalTank;
import mekanism.common.tile.TileEntityChemicalTank;
import net.minecraft.tileentity.TileEntity;

import java.util.HashMap;
import java.util.Map;

public class ChemicalTankIntegration extends TileEntityIntegrationPeripheral<TileEntityChemicalTank> {

    public ChemicalTankIntegration(TileEntity entity) {
        super(entity);
    }

    @Override
    public String getType() {
        return "chemicalTank";
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Object> getStored() {
        Map<String, Object> result = new HashMap<>(3);
        result.put("name", DataHelpers.getId(getTank().getStack().getType()));
        result.put("amount", getTank().getStored());
        result.put("type", getMergedTank().getCurrent().name());
        return result;
    }

    @LuaFunction(mainThread = true)
    public final long getCapacity() {
        return getTank().getCapacity();
    }

    @LuaFunction
    public final String getTier() {
        return tileEntity.getTier().name();
    }

    @LuaFunction(mainThread = true)
    public final double getFilledPercentage() {
        return getTank().getStored() / (double) getCapacity();
    }

    @LuaFunction(mainThread = true)
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
        return tileEntity.getChemicalTank();
    }
}
