package de.srendi.advancedperipherals.common.addons.create;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.lib.peripherals.BlockEntityIntegrationPeripheral;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BasinIntegration extends BlockEntityIntegrationPeripheral<BasinBlockEntity> {

    public BasinIntegration(BlockEntity entity) {
        super(entity);
    }

    @NotNull
    @Override
    public String getType() {
        return "basin";
    }

    @LuaFunction(mainThread = true)
    public final List<Object> getInputFluids() {
        IFluidHandler handler = blockEntity.getTanks().getFirst().getCapability().orElse(null);
        if (handler == null) {
            return null;
        }
        int size = handler.getTanks();
        List<Object> tanks = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            FluidStack fluid = handler.getFluidInTank(i);
            Map<String, Object> data = new HashMap<>();
            data.put("amount", fluid.getAmount());
            data.put("fluid", ForgeRegistries.FLUIDS.getKey(fluid.getFluid()).toString());
            tanks.add(data);
        }
        return tanks;
    }

    @LuaFunction(mainThread = true)
    public final List<Object> getOutputFluids() {
        IFluidHandler handler = blockEntity.getTanks().getSecond().getCapability().orElse(null);
        if (handler == null) {
            return null;
        }
        int size = handler.getTanks();
        List<Object> tanks = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            FluidStack fluid = handler.getFluidInTank(i);
            Map<String, Object> data = new HashMap<>();
            data.put("amount", fluid.getAmount());
            data.put("fluid", ForgeRegistries.FLUIDS.getKey(fluid.getFluid()).toString());
            tanks.add(data);
        }
        return tanks;
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Object> getFilter() {
        return LuaConverter.itemStackToObject(blockEntity.getFilter().getFilter());
    }

    @LuaFunction(mainThread = true)
    public final List<Object> getInventory() {
        Optional<IItemHandler> handlerOptional = blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve();
        if (handlerOptional.isEmpty()) return null;
        IItemHandler handler = handlerOptional.get();
        List<Object> items = new ArrayList<>();
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            items.add(LuaConverter.itemStackToObject(handler.getStackInSlot(slot)));
        }
        return items;
    }
}
