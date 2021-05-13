package de.srendi.advancedperipherals.common.addons.computercraft.proxyintegrations;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.addons.computercraft.base.ProxyIntegration;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.math.FloatingLong;
import mekanism.common.integration.energy.EnergyCompatUtils;
import mekanism.common.tile.base.TileEntityMekanism;

import java.util.List;
import java.util.function.Function;

public class GenericMekanismIntegration extends ProxyIntegration<TileEntityMekanism> {

    //Most of the functions are adapted from mekanism

    @Override
    protected Class<TileEntityMekanism> getTargetClass() {
        return TileEntityMekanism.class;
    }

    @Override
    protected GenericMekanismIntegration getNewInstance() {
        return new GenericMekanismIntegration();
    }

    @Override
    protected String getName() {
        return "mekanismMachine";
    }

    private FloatingLong getTotalEnergy(Function<IEnergyContainer, FloatingLong> getter) {
        FloatingLong total = FloatingLong.ZERO;
        List<IEnergyContainer> energyContainers = getTileEntity().getEnergyContainers(null);
        for (IEnergyContainer energyContainer : energyContainers) {
            total = total.plusEqual(getter.apply(energyContainer));
        }
        return total;
    }

    @LuaFunction
    public final double getTotalEnergyFilledPercentage() {
        FloatingLong stored = FloatingLong.ZERO;
        FloatingLong max = FloatingLong.ZERO;
        List<IEnergyContainer> energyContainers = getTileEntity().getEnergyContainers(null);
        for (IEnergyContainer energyContainer : energyContainers) {
            stored = stored.plusEqual(energyContainer.getEnergy());
            max = max.plusEqual(energyContainer.getMaxEnergy());
        }
        return stored.divideToLevel(max);
    }

    @LuaFunction
    public final long getTotalEnergy() {
        return EnergyCompatUtils.EnergyType.FORGE.convertToAsLong(getTotalEnergy(IEnergyContainer::getEnergy));
    }

    @LuaFunction
    public final long getTotalMaxEnergy() {
        return EnergyCompatUtils.EnergyType.FORGE.convertToAsLong(getTotalEnergy(IEnergyContainer::getMaxEnergy));
    }

    @LuaFunction
    public final long getTotalEnergyNeeded() {
        return EnergyCompatUtils.EnergyType.FORGE.convertToAsLong(getTotalEnergy(IEnergyContainer::getNeeded));
    }

}
