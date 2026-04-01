package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.lua.ObjectLuaTable;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.InventoryManagerOwner;
import de.srendi.advancedperipherals.common.blocks.blockentities.InventoryManagerEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.inventory.InventoryUtil;
import de.srendi.advancedperipherals.common.util.inventory.ItemFilter;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.PlayerInvWrapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryManagerPeripheral extends BasePeripheral<InventoryManagerOwner> {

    public static final String PERIPHERAL_TYPE = "inventory_manager";

    public InventoryManagerPeripheral(InventoryManagerEntity tileEntity) {
        super(PERIPHERAL_TYPE, new InventoryManagerOwner(tileEntity));
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.enableInventoryManager.get();
    }

    @LuaFunction
    public final MethodResult getOwner() throws LuaException {
        Player player = owner.getOwner();
        if (player == null) {
            return MethodResult.of();
        }
        return MethodResult.of(player.getName().getString(), player.getUUID().toString());
    }


    // Add the specified item to the player
    // The item is specified the same as with the RS/ME bridge:
    // {name="minecraft:enchanted_book", count=1, nbt="ae70053c97f877de546b0248b9ddf525"}
    @LuaFunction(mainThread = true)
    public final MethodResult addItemToPlayer(String invDirection, Map<?, ?> item) throws LuaException {
        Pair<ItemFilter, String> filter = ItemFilter.parse(new ObjectLuaTable(item));
        if (filter.rightPresent()) {
            return MethodResult.of(null, filter.right());
        }
        return addItemCommon(invDirection, filter.left());
    }

    private MethodResult addItemCommon(String invDirection, ItemFilter filter) throws LuaException {
        Direction direction = validateSide(invDirection);

        IItemHandler inventoryTo = new PlayerInvWrapper(getOwnerPlayerOrError().getInventory());
        IItemHandler inventoryFrom = getLevel().getCapability(Capabilities.ItemHandler.BLOCK, owner.getPos().relative(direction), direction.getOpposite());
        if (inventoryFrom == null) {
            return MethodResult.of(null, "INVENTORY_FROM_INVALID");
        }

        // if (invSlot >= inventoryTo.getSlots() || invSlot < 0)
        //  throw new LuaException("Inventory out of bounds " + invSlot + " (max: " + (inventoryTo.getSlots() - 1) + ")");

        return MethodResult.of(InventoryUtil.moveItem(inventoryFrom, inventoryTo, filter));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult removeItemFromPlayer(String invDirection, Map<?, ?> item) throws LuaException {
        Pair<ItemFilter, String> filter = ItemFilter.parse(new ObjectLuaTable(item));
        if (filter.rightPresent()) {
            return MethodResult.of(null, filter.right());
        }
        return removeItemCommon(invDirection, filter.left());
    }

    private MethodResult removeItemCommon(String invDirection, ItemFilter filter) throws LuaException {
        Direction direction = validateSide(invDirection);

        IItemHandler inventoryFrom = new PlayerInvWrapper(getOwnerPlayerOrError().getInventory());
        IItemHandler inventoryTo = getLevel().getCapability(Capabilities.ItemHandler.BLOCK, owner.getPos().relative(direction), direction.getOpposite());
        if (inventoryTo == null) {
            return MethodResult.of(null, "INVENTORY_TO_INVALID");
        }

        return MethodResult.of(InventoryUtil.moveItem(inventoryFrom, inventoryTo, filter));
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
    public final MethodResult listChest(String target) throws LuaException {
        Direction direction = validateSide(target);

        IItemHandler inventory = getLevel().getCapability(Capabilities.ItemHandler.BLOCK, owner.getPos().relative(direction), direction.getOpposite());
        if (inventory == null) {
            return MethodResult.of(null, "INVENTORY_TO_INVALID");
        }

        int size = inventory.getSlots();
        Map<Integer, Object> items = new HashMap<>(size * 4 / 3 + 1);
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                items.put(slot + 1, LuaConverter.itemStackToLua(stack));
            }
        }
        return MethodResult.of(items);
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

    private Player getOwnerPlayerOrError() throws LuaException {
        Player player = owner.getOwner();
        if (player == null) {
            throw new LuaException("The Inventory Manager doesn't have a memory card or it isn't bound to a player.");
        }
        return player;
    }
}
