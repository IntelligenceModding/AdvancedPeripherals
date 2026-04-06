package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.peripheral.generic.GenericPeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.InventoryManagerOwner;
import de.srendi.advancedperipherals.common.blocks.blockentities.InventoryManagerEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.util.EmptyLuaTable;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.inventory.ItemUtil;
import de.srendi.advancedperipherals.common.util.inventory.ItemFilter;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.PlayerInvWrapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InventoryManagerPeripheral extends BasePeripheral<InventoryManagerOwner> {

    public static final String PERIPHERAL_TYPE = "inventory_manager";

    public InventoryManagerPeripheral(InventoryManagerEntity tileEntity) {
        super(PERIPHERAL_TYPE, new InventoryManagerOwner(tileEntity));
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.enableInventoryManager.get();
    }

    @Override
    protected Map<String, Object> getPeripheralConfiguration() {
        Map<String, Object> configs = super.getPeripheralConfiguration();
        configs.put("itemsTransferEnabled", APConfig.PERIPHERALS_CONFIG.enableItemsTransfer.get());
        return configs;
    }

    private Player getOwnerPlayerOrError() throws LuaException {
        Player player = owner.getOwner();
        if (player == null) {
            throw new LuaException("The Inventory Manager doesn't have a memory card or it isn't bound to a player.");
        }
        return player;
    }

    @LuaFunction
    public final MethodResult getOwner() throws LuaException {
        Player player = owner.getOwner();
        if (player == null) {
            return MethodResult.of();
        }
        return MethodResult.of(player.getUUID().toString(), player.getGameProfile().getName());
    }

    @LuaFunction(mainThread = true)
    public final int size() throws LuaException {
        return getOwnerPlayerOrError().getInventory().getContainerSize();
    }

    @LuaFunction(mainThread = true)
    public final Map<Integer, Object> list() throws LuaException {
        Inventory inventory = getOwnerPlayerOrError().getInventory();

        int size = inventory.getContainerSize();
        Map<Integer, Object> items = new HashMap<>(size * 4 / 3 + 1);
        for (int slot = 0; slot < size; slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (!stack.isEmpty()) {
                items.put(slot + 1, LuaConverter.itemStackToLua(stack));
            }
        }
        return items;
    }

    @LuaFunction(mainThread = true)
    public final MethodResult pushItems(IComputerAccess computer, String toName, Optional<Map<?, ?>> filterTable) throws LuaException {
        checkAllowItemTransfers();

        IPeripheral toPeripheral = computer.getAvailablePeripheral(toName);
        if (toPeripheral == null) {
            throw new LuaException("Target '" + toName + "' does not exist");
        }
        IItemHandler inventoryTo = extractItemHandler(toPeripheral);
        if (inventoryTo == null) {
            throw new LuaException("Target '" + toName + "' is not an inventory");
        }

        Pair<ItemFilter, String> filter = ItemFilter.parse(EmptyLuaTable.orEmpty(filterTable.orElse(null)));
        if (filter.rightPresent()) {
            return MethodResult.of(null, filter.right());
        }

        IItemHandler inventoryFrom = new PlayerInvWrapper(getOwnerPlayerOrError().getInventory());
        return MethodResult.of(ItemUtil.moveItem(inventoryFrom, inventoryTo, filter.left()));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult pullItems(IComputerAccess computer, String fromName, Optional<Map<?, ?>> filterTable) throws LuaException {
        checkAllowItemTransfers();

        IPeripheral toPeripheral = computer.getAvailablePeripheral(fromName);
        if (toPeripheral == null) {
            throw new LuaException("Target '" + fromName + "' does not exist");
        }
        IItemHandler inventoryFrom = extractItemHandler(toPeripheral);
        if (inventoryFrom == null) {
            throw new LuaException("Target '" + fromName + "' is not an inventory");
        }

        Pair<ItemFilter, String> filter = ItemFilter.parse(EmptyLuaTable.orEmpty(filterTable.orElse(null)));
        if (filter.rightPresent()) {
            return MethodResult.of(null, filter.right());
        }

        IItemHandler inventoryTo = new PlayerInvWrapper(getOwnerPlayerOrError().getInventory());
        return MethodResult.of(ItemUtil.moveItem(inventoryFrom, inventoryTo, filter.left()));
    }

    @LuaFunction(mainThread = true)
    public final boolean isWearing(int index) throws LuaException {
        index--;
        List<ItemStack> armor = getOwnerPlayerOrError().getInventory().armor;
        return 0 <= index && index < armor.size() && !armor.get(index).isEmpty();
    }

    @LuaFunction(mainThread = true)
    public final int getEmptySlots() throws LuaException {
        int count = 0;
        for (ItemStack stack : getOwnerPlayerOrError().getInventory().items) {
            if (stack.isEmpty()) {
                count++;
            }
        }
        return count;
    }

    @LuaFunction(mainThread = true)
    public final boolean hasAvailableSpace() throws LuaException {
        return getOwnerPlayerOrError().getInventory().getFreeSlot() >= 0;
    }

    @LuaFunction(mainThread = true)
    public final int getFreeSlot() throws LuaException {
        return getOwnerPlayerOrError().getInventory().getFreeSlot() + 1;
    }

    @LuaFunction(mainThread = true)
    public final int getHandSlot() throws LuaException {
        return getOwnerPlayerOrError().getInventory().selected + 1;
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Object> getItemInHand() throws LuaException {
        Player player = getOwnerPlayerOrError();
        return LuaConverter.itemStackToLuaWithSlot(player.getMainHandItem(), player.getInventory().selected);
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Object> getItemInOffHand() throws LuaException {
        return LuaConverter.itemStackToLua(getOwnerPlayerOrError().getOffhandItem());
    }

    private void checkAllowItemTransfers() throws LuaException {
        if (!APConfig.PERIPHERALS_CONFIG.enableItemsTransfer.get()) {
            throw new LuaException("This function is disabled in the config [Inventory_Manager.enableItemsTransfer]. Activate it or ask admins if they can activate it.");
        }
    }

    private static IItemHandler extractItemHandler(IPeripheral peripheral) {
        Object target = peripheral.getTarget();
        if (target instanceof IItemHandler handler) {
            return handler;
        }
        if (target instanceof BlockEntity be) {
            Direction side = peripheral instanceof GenericPeripheral sided ? sided.side() : null;
            return be.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, be.getBlockPos(), side);
        }
        return null;
    }
}
