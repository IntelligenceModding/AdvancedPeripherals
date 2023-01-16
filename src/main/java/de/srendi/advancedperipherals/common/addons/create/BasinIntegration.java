package de.srendi.advancedperipherals.common.addons.create;

import com.simibubi.create.content.contraptions.processing.BasinTileEntity;
import com.simibubi.create.foundation.tileEntity.behaviour.fluid.SmartFluidTankBehaviour;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.lib.peripherals.BlockEntityIntegrationPeripheral;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BasinIntegration extends BlockEntityIntegrationPeripheral<BasinTileEntity> {

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
        List<Object> tanks = new ArrayList<>();
        for (SmartFluidTankBehaviour.TankSegment tank : blockEntity.getTanks().getFirst().getTanks()) {
            Map<String, Object> data = new HashMap<>();
            data.put("amount", tank.getRenderedFluid().getAmount());
            data.put("fluid", tank.getRenderedFluid().getFluid().getRegistryName().toString());
            tanks.add(data);
        }
        return tanks;
    }

    @LuaFunction(mainThread = true)
    public final List<Object> getOutputFluids() {
        List<Object> tanks = new ArrayList<>();
        for (SmartFluidTankBehaviour.TankSegment tank : blockEntity.getTanks().getSecond().getTanks()) {
            Map<String, Object> data = new HashMap<>();
            data.put("amount", tank.getRenderedFluid().getAmount());
            data.put("fluid", tank.getRenderedFluid().getFluid().getRegistryName().toString());
            tanks.add(data);
        }
        return tanks;
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Object> getFilter() {
        return LuaConverter.stackToObject(blockEntity.getFilter().getFilter());
    }

    @LuaFunction(mainThread = true)
    public final List<Object> getInventory() {
        Optional<IItemHandler> handlerOptional = blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).resolve();
        if (handlerOptional.isEmpty()) return null;
        IItemHandler handler = handlerOptional.get();
        List<Object> items = new ArrayList<>();
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            items.add(LuaConverter.stackToObject(handler.getStackInSlot(slot)));
        }
        return items;
    }
}
