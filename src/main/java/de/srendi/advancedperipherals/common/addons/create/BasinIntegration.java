package de.srendi.advancedperipherals.common.addons.create;

import com.simibubi.create.content.contraptions.processing.BasinTileEntity;
import com.simibubi.create.foundation.tileEntity.behaviour.fluid.SmartFluidTankBehaviour;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.lib.peripherals.BlockEntityIntegrationPeripheral;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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
    public final Map<String, Object> getInputFluids() {
        return new HashMap<>() {{
                for(SmartFluidTankBehaviour.TankSegment tank : blockEntity.getTanks().getFirst().getTanks()) {
                    put("amount", tank.getRenderedFluid().getAmount());
                    put("fluid", tank.getRenderedFluid().getFluid().getRegistryName().toString());
                }
        }};
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Object> getOutputFluids() {
        return new HashMap<>() {{
            for(SmartFluidTankBehaviour.TankSegment tank : blockEntity.getTanks().getSecond().getTanks()) {
                put("amount", tank.getRenderedFluid().getAmount());
                put("fluid", tank.getRenderedFluid().getFluid().getRegistryName().toString());
            }
        }};
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Object> getFilter() {
        return LuaConverter.stackToObject(blockEntity.getFilter().getFilter());
    }

    @LuaFunction(mainThread = true)
    public final Object[] getInventory() {
        Optional<IItemHandler> handlerOptional = blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).resolve();
        if(handlerOptional.isEmpty())
            return null;
        IItemHandler handler = handlerOptional.get();
        Object[] items = new Object[handler.getSlots()];
        for(int slot = 0; slot < handler.getSlots(); slot++)
        {
            items[slot] = LuaConverter.stackToObject(handler.getStackInSlot(slot));
        }
        return items;
    }
}
