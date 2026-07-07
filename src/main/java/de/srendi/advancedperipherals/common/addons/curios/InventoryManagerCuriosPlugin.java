package de.srendi.advancedperipherals.common.addons.curios;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.InventoryManagerPeripheral;
import de.srendi.advancedperipherals.common.util.EmptyLuaTable;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.inventory.InventoryUtil;
import de.srendi.advancedperipherals.common.util.inventory.ItemFilter;
import de.srendi.advancedperipherals.common.util.inventory.ItemUtil;
import de.srendi.advancedperipherals.common.util.inventory.PlayerStorageItemWrapper;
import de.srendi.advancedperipherals.lib.peripherals.IPeripheralPlugin;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InventoryManagerCuriosPlugin implements IPeripheralPlugin {
    private final InventoryManagerPeripheral peripheral;

    public InventoryManagerCuriosPlugin(InventoryManagerPeripheral peripheral) {
        this.peripheral = peripheral;
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Integer> getCuriosSizes() throws LuaException {
        final ICuriosItemHandler curiosInv = CuriosApi.getCuriosInventory(this.peripheral.getOwnerPlayerOrError()).orElse(null);
        if (curiosInv == null) {
            return Map.of();
        }
        Map<String, Integer> idMap = new HashMap<>();
        for (Map.Entry<String, ICurioStacksHandler> entry : curiosInv.getCurios().entrySet()) {
            idMap.put(entry.getKey(), entry.getValue().getSlots());
        }
        return idMap;
    }

    @LuaFunction(mainThread = true)
    public final Map<String, ?> listCurios() throws LuaException {
        final ICuriosItemHandler curiosInv = CuriosApi.getCuriosInventory(this.peripheral.getOwnerPlayerOrError()).orElse(null);
        if (curiosInv == null) {
            return Map.of();
        }
        Map<String, Map<Integer, ?>> idMap = new HashMap<>();
        for (Map.Entry<String, ICurioStacksHandler> entry : curiosInv.getCurios().entrySet()) {
            ICurioStacksHandler handler = entry.getValue();
            Map<Integer, Map<String, Object>> list = InventoryUtil.list(handler.getStacks());
            NonNullList<Boolean> activeStates = handler.getActiveStates();
            for (Map.Entry<Integer, Map<String, Object>> itemEntry : list.entrySet()) {
                itemEntry.getValue().put("isActive", activeStates.get(itemEntry.getKey() - 1));
            }
            idMap.put(entry.getKey(), list);
        }
        return idMap;
    }

    @NotNull
    private IDynamicStackHandler getCuriosHandler(String curiosId) throws LuaException {
        final ICuriosItemHandler curiosInv = CuriosApi.getCuriosInventory(this.peripheral.getOwnerPlayerOrError()).orElse(null);
        if (curiosInv == null) {
            throw new LuaException("Curios slot '" + curiosId + "' does not exist");
        }
        return curiosInv.getStacksHandler(curiosId)
            .orElseThrow(() -> new LuaException("Curios slot '" + curiosId + "' does not exist"))
            .getStacks();
    }

    @LuaFunction(mainThread = true)
    public final MethodResult importCuriosItems(IComputerAccess computer, String toCurios, String fromName, Optional<Map<?, ?>> filterTable) throws LuaException {
        this.peripheral.assertAllowItemTransfers();

        Pair<ItemFilter, String> filter = ItemFilter.parse(EmptyLuaTable.orEmpty(filterTable.orElse(null)));
        if (filter.rightPresent()) {
            return MethodResult.of(null, filter.right());
        }

        IItemHandler inventoryTo = this.getCuriosHandler(toCurios);
        IItemHandler inventoryFrom = this.peripheral.getItemHandler(computer, fromName);
        return MethodResult.of(ItemUtil.moveItem(inventoryFrom, inventoryTo, filter.left()));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult exportCuriosItems(IComputerAccess computer, String fromCurios, String toName, Optional<Map<?, ?>> filterTable) throws LuaException {
        this.peripheral.assertAllowItemTransfers();

        Pair<ItemFilter, String> filter = ItemFilter.parse(EmptyLuaTable.orEmpty(filterTable.orElse(null)));
        if (filter.rightPresent()) {
            return MethodResult.of(null, filter.right());
        }

        IItemHandler inventoryFrom = this.getCuriosHandler(fromCurios);
        IItemHandler inventoryTo = this.peripheral.getItemHandler(computer, toName);
        return MethodResult.of(ItemUtil.moveItem(inventoryFrom, inventoryTo, filter.left()));
    }

    @LuaFunction(mainThread = true)
    public final PlayerStorageItemWrapper wrapCuriosStorageItem(IComputerAccess computer, String curiosId, int slot) throws LuaException {
        int islot = slot - 1;
        return PlayerStorageItemWrapper.create(computer, this.peripheral, this.peripheral.getOwnerPlayerOrError(), (player) -> {
            final ICuriosItemHandler curiosInv = CuriosApi.getCuriosInventory(player).orElse(null);
            if (curiosInv == null) {
                return ItemStack.EMPTY;
            }
            final ICurioStacksHandler handler = curiosInv.getStacksHandler(curiosId).orElse(null);
            if (handler == null) {
                return ItemStack.EMPTY;
            }
            if (islot < 0 || islot > handler.getSlots()) {
                return ItemStack.EMPTY;
            }
            return handler.getStacks().getStackInSlot(islot);
        }, (player, stack) -> {
            final ICurioStacksHandler handler = CuriosApi.getCuriosInventory(player).get().getStacksHandler(curiosId).get();
            handler.getStacks().setStackInSlot(islot, stack);
        });
    }
}
