package de.srendi.advancedperipherals.common.addons.create;

import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.lua.ObjectLuaTable;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.lib.peripherals.APGenericPeripheral;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @LuaFunction(mainThread = true)
    public final Map<String, ?> getFilter(BasinBlockEntity blockEntity) {
        return CreateFilter.filterToLua(FilterItemStack.of(blockEntity.getFilter().getFilter()), blockEntity.getLevel().registryAccess());
    }

    @LuaFunction(mainThread = true)
    public final MethodResult setFilter(BasinBlockEntity blockEntity, Optional<Map<?, ?>> filter) throws LuaException {
        ItemStack filterStack = blockEntity.getFilter().getFilter();
        Pair<ItemStack, String> result = CreateFilter.updateFilter(filterStack, filter.map(ObjectLuaTable::new).orElse(null), blockEntity.getLevel().registryAccess());
        if (result.rightPresent()) {
            return MethodResult.of(false, result.right());
        }
        if (!blockEntity.getFilter().setFilter(result.left())) {
            return MethodResult.of(false, "invalid filter");
        }
        return MethodResult.of(true);
    }
}
