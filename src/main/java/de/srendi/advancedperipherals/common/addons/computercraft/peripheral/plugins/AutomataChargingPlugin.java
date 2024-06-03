package de.srendi.advancedperipherals.common.addons.computercraft.peripheral.plugins;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.FuelAbility;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.PeripheralOwnerAbility;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.TurtlePeripheralOwner;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.lib.peripherals.AutomataCorePeripheral;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import org.jetbrains.annotations.NotNull;
import java.util.Objects;

public class AutomataChargingPlugin extends AutomataCorePlugin {

    public AutomataChargingPlugin(AutomataCorePeripheral automataCore) {
        super(automataCore);
    }

    @LuaFunction(mainThread = true)
    public final MethodResult chargeTurtle(@NotNull IArguments arguments) throws LuaException {
        TurtlePeripheralOwner owner = automataCore.getPeripheralOwner();
        FuelAbility<?> fuelAbility = owner.getAbility(PeripheralOwnerAbility.FUEL);
        Objects.requireNonNull(fuelAbility);
        if (fuelAbility.isFuelConsumptionDisable())
            return MethodResult.of(null, "Fuel consumption is disabled, why do you even need this?");

        ItemStack stack = owner.getToolInMainHand();
        int fuel = arguments.optInt(0, -1);
        return stack.getCapability(ForgeCapabilities.ENERGY).map(storage -> {
            int availableFuelSpace = fuelAbility.getFuelMaxCount() - fuelAbility.getFuelCount();
            int requestedRF;
            if (fuel != -1) {
                requestedRF = fuel * APConfig.METAPHYSICS_CONFIG.energyToFuelRate.get();
            } else {
                requestedRF = storage.getEnergyStored();
            }
            int realConsumedRF = storage.extractEnergy(Math.min(requestedRF, availableFuelSpace * APConfig.METAPHYSICS_CONFIG.energyToFuelRate.get()), false);
            int receivedFuel = realConsumedRF / APConfig.METAPHYSICS_CONFIG.energyToFuelRate.get();
            fuelAbility.addFuel(receivedFuel);
            automataCore.addRotationCycle();
            return MethodResult.of(true, receivedFuel);
        }).orElse(MethodResult.of(null, "Item should provide energy ..."));
    }
}
