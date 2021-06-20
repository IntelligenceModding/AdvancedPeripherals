package de.srendi.advancedperipherals.common.addons.computercraft.integrations.mekanism;

import dan200.computercraft.api.lua.GenericSource;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.math.FloatingLong;
import mekanism.common.integration.energy.EnergyCompatUtils;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

public class GenericMekanismIntegration implements GenericSource {

    @NotNull
    @Override
    public ResourceLocation id() {
        return new ResourceLocation(AdvancedPeripherals.MOD_ID, "mekanismMachine");
    }

    private static FloatingLong getTotalEnergy(TileEntityMekanism tileEntity, Function<IEnergyContainer, FloatingLong> getter) {
        FloatingLong total = FloatingLong.ZERO;
        List<IEnergyContainer> energyContainers = tileEntity.getEnergyContainers(null);
        for (IEnergyContainer energyContainer : energyContainers) {
            total = total.plusEqual(getter.apply(energyContainer));
        }
        return total;
    }

    @LuaFunction
    public static double getTotalEnergyFilledPercentage(TileEntityMekanism tileEntity) {
        FloatingLong stored = FloatingLong.ZERO;
        FloatingLong max = FloatingLong.ZERO;
        List<IEnergyContainer> energyContainers = tileEntity.getEnergyContainers(null);
        for (IEnergyContainer energyContainer : energyContainers) {
            stored = stored.plusEqual(energyContainer.getEnergy());
            max = max.plusEqual(energyContainer.getMaxEnergy());
        }
        return stored.divideToLevel(max);
    }

    @LuaFunction
    public static long getTotalEnergy(TileEntityMekanism tileEntity) {
        return EnergyCompatUtils.EnergyType.FORGE.convertToAsLong(getTotalEnergy(tileEntity, IEnergyContainer::getEnergy));
    }

    @LuaFunction
    public static long getTotalMaxEnergy(TileEntityMekanism tileEntity) {
        return EnergyCompatUtils.EnergyType.FORGE.convertToAsLong(getTotalEnergy(tileEntity, IEnergyContainer::getMaxEnergy));
    }

    @LuaFunction
    public static long getTotalEnergyNeeded(TileEntityMekanism tileEntity) {
        return EnergyCompatUtils.EnergyType.FORGE.convertToAsLong(getTotalEnergy(tileEntity, IEnergyContainer::getNeeded));
    }

}
