package de.srendi.advancedperipherals.common.addons.computercraft.integrations.mekanism;

import dan200.computercraft.api.lua.GenericSource;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.shared.peripheral.generic.data.DataHelpers;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.base.ProxyIntegration;
import mekanism.common.tile.TileEntityChemicalTank;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.chemical.merged.MergedChemicalTank;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ChemicalTankIntegration implements GenericSource {

    @NotNull
    @Override
    public ResourceLocation id() {
        return new ResourceLocation(AdvancedPeripherals.MOD_ID, "chemicalTank");
    }

    @LuaFunction
    public static Map<String, Object> getStored(TileEntityChemicalTank tileEntity) {
        Map<String, Object> result = new HashMap<>(3);
        result.put("name", DataHelpers.getId(getTank(tileEntity).getStack().getType()));
        result.put("amount", getTank(tileEntity).getStored());
        result.put("type", getMergedTank(tileEntity).getCurrent().name());
        return result;
    }

    @LuaFunction
    public static long getCapacity(TileEntityChemicalTank tileEntity) {
        return getTank(tileEntity).getCapacity();
    }

    @LuaFunction
    public static double getFilledPercentage(TileEntityChemicalTank tileEntity) {
        return getTank(tileEntity).getStored() / (double) getCapacity(tileEntity);
    }

    @LuaFunction
    public static long getNeeded(TileEntityChemicalTank tileEntity) {
        return getTank(tileEntity).getNeeded();
    }

    private static IChemicalTank<?, ?> getTank(TileEntityChemicalTank tileEntity) {
        MergedChemicalTank mergedTank = getMergedTank(tileEntity);
        if (mergedTank.getCurrent() != MergedChemicalTank.Current.EMPTY) {
            return mergedTank.getTankFromCurrent(mergedTank.getCurrent());
        }
        return mergedTank.getGasTank();
    }

    private static MergedChemicalTank getMergedTank(TileEntityChemicalTank tileEntity) {
        return tileEntity.getChemicalTank();
    }

}
