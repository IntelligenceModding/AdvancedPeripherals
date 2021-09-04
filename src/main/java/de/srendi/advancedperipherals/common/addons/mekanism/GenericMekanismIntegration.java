package de.srendi.advancedperipherals.common.addons.mekanism;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.api.peripherals.IPeripheralPlugin;
import de.srendi.advancedperipherals.lib.peripherals.TileEntityIntegrationPeripheral;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.math.FloatingLong;
import mekanism.common.integration.energy.EnergyCompatUtils;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.tileentity.TileEntity;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

public class GenericMekanismIntegration extends TileEntityIntegrationPeripheral<TileEntityMekanism> implements IPeripheralPlugin {

    public GenericMekanismIntegration(TileEntity entity) {
        super(entity);
    }

    //Most of the functions are adapted from mekanism

    private FloatingLong getTotalEnergy(Function<IEnergyContainer, FloatingLong> getter) {
        FloatingLong total = FloatingLong.ZERO;
        List<IEnergyContainer> energyContainers = tileEntity.getEnergyContainers(null);
        for (IEnergyContainer energyContainer : energyContainers) {
            total = total.plusEqual(getter.apply(energyContainer));
        }
        return total;
    }

    @LuaFunction(mainThread = true)
    public final double getTotalEnergyFilledPercentage() {
        FloatingLong stored = FloatingLong.ZERO;
        FloatingLong max = FloatingLong.ZERO;
        List<IEnergyContainer> energyContainers = tileEntity.getEnergyContainers(null);
        for (IEnergyContainer energyContainer : energyContainers) {
            stored = stored.plusEqual(energyContainer.getEnergy());
            max = max.plusEqual(energyContainer.getMaxEnergy());
        }
        return stored.divideToLevel(max);
    }

    @LuaFunction(mainThread = true)
    public final long getTotalEnergy() {
        return EnergyCompatUtils.EnergyType.FORGE.convertToAsLong(getTotalEnergy(IEnergyContainer::getEnergy));
    }

    @LuaFunction(mainThread = true)
    public final long getTotalMaxEnergy() {
        return EnergyCompatUtils.EnergyType.FORGE.convertToAsLong(getTotalEnergy(IEnergyContainer::getMaxEnergy));
    }

    @LuaFunction(mainThread = true)
    public final long getTotalEnergyNeeded() {
        return EnergyCompatUtils.EnergyType.FORGE.convertToAsLong(getTotalEnergy(IEnergyContainer::getNeeded));
    }

    @NotNull
    @Override
    public String getType() {
        return "mekanismMachine";
    }
}
