package de.srendi.advancedperipherals.common.util.inventory;

import dan200.computercraft.api.lua.LuaException;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.HashMap;
import java.util.Map;

public class InventoryUtil {
    private InventoryUtil() {
    }

    public static Map<Integer, Object> list(IItemHandler handler) throws LuaException {
        int size = handler.getSlots();
        Map<Integer, Object> items = new HashMap<>();
        for (int slot = 0; slot < size; slot++) {
            ItemStack stack = handler.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                items.put(slot + 1, LuaConverter.itemStackToLua(stack));
            }
        }
        return items;
    }
}
