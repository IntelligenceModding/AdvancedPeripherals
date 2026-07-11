package de.srendi.advancedperipherals.common.addons.create;

import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.LuaTable;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.lua.ObjectLuaTable;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.lib.peripherals.APGenericPeripheral;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FilteringBehaviourIntegration implements APGenericPeripheral {
    @Override
    @NotNull
    public String getPeripheralType() {
        return "filtering_behaviour";
    }

    @LuaFunction(mainThread = true)
    public final Map<String, ?> getFilter(Wrapper wrapper) {
        Map<String, Object> data = new HashMap<>(
            CreateFilter.filterToLua(FilterItemStack.of(wrapper.behaviour().getFilter()), wrapper.blockEntity().getLevel().registryAccess())
        );
        data.put("count", wrapper.behaviour().count);
        data.put("upTo", wrapper.behaviour().upTo);
        return data;
    }

    @LuaFunction(mainThread = true)
    public final MethodResult setFilter(Wrapper wrapper, Optional<Map<?, ?>> filter) throws LuaException {
        FilteringBehaviour behaviour = wrapper.behaviour();
        ItemStack filterStack = behaviour.getFilter();
        LuaTable<?, ?> filterTable = filter.map(ObjectLuaTable::new).orElse(null);

        Pair<ItemStack, String> result = CreateFilter.updateFilter(filterStack, filterTable, wrapper.blockEntity().getLevel().registryAccess());
        if (result.rightPresent()) {
            return MethodResult.of(false, result.right());
        }
        if (!behaviour.setFilter(result.left())) {
            return MethodResult.of(false, "invalid filter");
        }
        behaviour.count = Math.min(Math.max(filterTable.optInt("count").orElse(behaviour.getMaxStackSize()), 1), behaviour.getMaxStackSize());
        behaviour.upTo = filterTable.optBoolean("upTo").orElse(true);
        return MethodResult.of(true);
    }

    public record Wrapper(FilteringBehaviour behaviour, SmartBlockEntity blockEntity) {
    }
}
