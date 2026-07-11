package de.srendi.advancedperipherals.common.addons.create;

import com.simibubi.create.content.fluids.tank.BoilerData;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.lib.peripherals.APGenericPeripheral;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class FluidTankIntegration implements APGenericPeripheral {
    @Override
    @NotNull
    public String getPeripheralType() {
        return "fluid_tank";
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Object> info(FluidTankBlockEntity blockEntity) {
        FluidTankBlockEntity controller = blockEntity.getControllerBE();
        BoilerData boiler = controller.boiler;
        Map<String, Object> data = new HashMap<>();
        data.put("size", controller.getTotalTankSize());
        data.put("capacity", controller.getTankInventory().getCapacity());
        data.put("fluid", LuaConverter.fluidStackToLua(controller.getTankInventory().getFluid()));
        data.put("boiler", Map.of(
            "active", boiler.isActive(),
            "supply", boiler.waterSupply,
            "activeHeat", boiler.activeHeat,
            "passiveHeat", boiler.passiveHeat,
            "engines", boiler.attachedEngines
        ));
        return data;
    }
}
