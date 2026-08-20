package de.srendi.advancedperipherals.common.addons.create;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.lib.peripherals.APGenericPeripheral;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BasinIntegration implements APGenericPeripheral {
    @Override
    @NotNull
    public String getPeripheralType() {
        return "basin";
    }

    @LuaFunction(mainThread = true)
    public final List<Object> inputTanks(BasinBlockEntity blockEntity) {
        IFluidHandler handler = blockEntity.getTanks().getFirst().getCapability();
        int size = handler.getTanks();
        List<Object> tanks = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            tanks.add(LuaConverter.fluidStackToLua(handler.getFluidInTank(i)));
        }
        return tanks;
    }

    @LuaFunction(mainThread = true)
    public final List<Object> outputTanks(BasinBlockEntity blockEntity) {
        IFluidHandler handler = blockEntity.getTanks().getSecond().getCapability();
        int size = handler.getTanks();
        List<Object> tanks = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            tanks.add(LuaConverter.fluidStackToLua(handler.getFluidInTank(i)));
        }
        return tanks;
    }
}
