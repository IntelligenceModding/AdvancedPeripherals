package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.lua.ObjectLuaTable;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.InventoryManagerOwner;
import de.srendi.advancedperipherals.common.blocks.blockentities.InventoryManagerEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.inventory.InventoryUtil;
import de.srendi.advancedperipherals.common.util.inventory.ItemFilter;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.PlayerArmorInvWrapper;
import net.neoforged.neoforge.items.wrapper.PlayerInvWrapper;
import net.neoforged.neoforge.items.wrapper.PlayerOffhandInvWrapper;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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


    //Add the specified item to the player
    //The item is specified the same as with the RS/ME bridge:
    //{name="minecraft:enchanted_book", count=1, nbt="ae70053c97f877de546b0248b9ddf525"}
    @LuaFunction(mainThread = true)
    public final MethodResult addItemToPlayer(String invDirection, Map<?, ?> item) throws LuaException {
        Pair<ItemFilter, String> filter = ItemFilter.parse(new ObjectLuaTable(item));
        if (filter.rightPresent()) {
            return MethodResult.of(0, filter.getRight());
        }
        return addItemCommon(invDirection, filter.getLeft());
    }

    private MethodResult addItemCommon(String invDirection, ItemFilter filter) throws LuaException {
        Direction direction = validateSide(invDirection);

        Pair<IItemHandler, Integer> inventoryTo = getHandlerFromSlot(filter.getToSlot());
        IItemHandler inventoryFrom = getLevel().getCapability(Capabilities.ItemHandler.BLOCK, owner.getPos().relative(direction), direction.getOpposite());
        if (inventoryFrom == null) {
            return MethodResult.of(0, "INVENTORY_FROM_INVALID");
        }

        inventoryTo.ifRightPresent(slot -> filter.toSlot = slot);

        //if (invSlot >= inventoryTo.getSlots() || invSlot < 0)
        //  throw new LuaException("Inventory out of bounds " + invSlot + " (max: " + (inventoryTo.getSlots() - 1) + ")");

        return MethodResult.of(InventoryUtil.moveItem(inventoryFrom, inventoryTo.getLeft(), filter));
    }

    @LuaFunction(mainThread = true)
    public final MethodResult removeItemFromPlayer(String invDirection, Map<?, ?> item) throws LuaException {
        Pair<ItemFilter, String> filter = ItemFilter.parse(new ObjectLuaTable(item));
        if (filter.rightPresent()) {
            return MethodResult.of(0, filter.getRight());
        }
        return removeItemCommon(invDirection, filter.getLeft());
    }

    private MethodResult removeItemCommon(String invDirection, ItemFilter filter) throws LuaException {
        Direction direction = validateSide(invDirection);

        Pair<IItemHandler, Integer> inventoryFrom = getHandlerFromSlot(filter.getFromSlot());
        IItemHandler inventoryTo = getLevel().getCapability(Capabilities.ItemHandler.BLOCK, owner.getPos().relative(direction), direction.getOpposite());

        if (inventoryTo == null) {
            return MethodResult.of(0, "INVENTORY_TO_INVALID");
        }

        inventoryFrom.ifRightPresent(slot -> filter.fromSlot = slot);

        return MethodResult.of(InventoryUtil.moveItem(inventoryFrom.getLeft(), inventoryTo, filter));
    }

    @LuaFunction(mainThread = true)
    public final List<Object> list() throws LuaException {
        List<Object> items = new ArrayList<>();
        List<ItemStack> stacks = getOwnerPlayerOrError().getInventory().items;
        // Used to let users easily sort the items by the slots. Also, a better way for the user to see where an item actually is
        for (int slot = 0; slot < stacks.size(); slot++) {
            ItemStack stack = stacks.get(slot);
            if (!stack.isEmpty()) {
                items.add(LuaConverter.stackToObjectWithSlot(stack, slot));
            }
        }
        return items;
    }

    @LuaFunction(mainThread = true)
    public final MethodResult listChest(String target) throws LuaException {
        Direction direction = validateSide(target);

        IItemHandler inventoryTo = getLevel().getCapability(Capabilities.ItemHandler.BLOCK, owner.getPos().relative(direction), direction.getOpposite());

        if (inventoryTo == null) {
            return MethodResult.of(null, "INVENTORY_TO_INVALID");
        }

        List<Object> items = new ArrayList<>();
        for (int slot = 0; slot < inventoryTo.getSlots(); slot++) {
            if (!inventoryTo.getStackInSlot(slot).isEmpty()) {
                items.add(LuaConverter.stackToObjectWithSlot(inventoryTo.getStackInSlot(slot), slot));
            }
        }
        return MethodResult.of(items);
    }

    @LuaFunction(mainThread = true)
    public final List<Object> getArmor() throws LuaException {
        List<Object> items = new ArrayList<>();
        for (ItemStack stack : getOwnerPlayerOrError().getInventory().armor) {
            if (!stack.isEmpty()) {
                items.add(LuaConverter.stackToObjectWithSlot(stack, ArmorSlot.getSlotForItem(stack)));
            }
        }
        return items;
    }

    @LuaFunction(mainThread = true)
    public final boolean isPlayerEquipped() throws LuaException {
        for (ItemStack stack : getOwnerPlayerOrError().getInventory().armor) {
            if (!stack.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @LuaFunction(mainThread = true)
    public final boolean isWearing(int index) throws LuaException {
        index--;
        List<ItemStack> armor = getOwnerPlayerOrError().getInventory().armor;
        return 0 <= index && index < armor.size() && !armor.get(index).isEmpty();
    }

    @LuaFunction(mainThread = true)
    public final int getEmptySpace() throws LuaException {
        int count = 0;
        for (ItemStack stack : getOwnerPlayerOrError().getInventory().items) {
            if (stack.isEmpty()) {
                count++;
            }
        }
        return count;
    }

    @LuaFunction(mainThread = true)
    public final boolean isSpaceAvailable() throws LuaException {
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
        return LuaConverter.stackToObjectWithSlot(player.getMainHandItem(), player.getInventory().selected);
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Object> getItemInOffHand() throws LuaException {
        return LuaConverter.itemStackToObject(getOwnerPlayerOrError().getOffhandItem());
    }

    private Player getOwnerPlayerOrError() throws LuaException {
        Player player = owner.getOwner();
        if (player == null) {
            throw new LuaException("The Inventory Manager doesn't have a memory card or it isn't bound to a player.");
        }
        return player;
    }

    @NotNull
    private Pair<IItemHandler, Integer> getHandlerFromSlot(int slot) throws LuaException {
        Player player = getOwnerPlayerOrError();
        IItemHandler handler;
        if (slot >= 100 && slot <= 103) {
            handler = new PlayerArmorInvWrapper(player.getInventory());
            // If the slot is between 100 and 103, change the index to a normal index between 0 and 3.
            // This is necessary since the PlayerArmorInvWrapper does not work with these higher indexes
            slot = slot - 100;
        } else if (slot == 36) {
            handler = new PlayerOffhandInvWrapper(player.getInventory());
            // Set the "from slot" to zero so the offhand wrapper can work with that
            slot = 0;
        } else {
            handler = new PlayerInvWrapper(player.getInventory());
        }
        return Pair.of(handler, slot);
    }

    /**
     * Used to get the proper slot number for armor.
     *
     * @see InventoryManagerPeripheral#getArmor()
     */
    private enum ArmorSlot {

        HELMET_SLOT(103, EquipmentSlot.HEAD),
        CHEST_SLOT(102, EquipmentSlot.CHEST),
        LEGGINGS_SLOT(101, EquipmentSlot.LEGS),
        BOOTS_SLOT(100, EquipmentSlot.FEET);

        private final int slot;
        private final EquipmentSlot slotType;

        ArmorSlot(int slot, EquipmentSlot slotType) {
            this.slot = slot;
            this.slotType = slotType;
        }

        public static int getSlotForItem(ItemStack stack) {
            if (stack.getItem() instanceof ArmorItem armorItem) {
                for (ArmorSlot slot : values()) {
                    if (armorItem.getEquipmentSlot() == slot.slotType) {
                        return slot.slot;
                    }
                }
            }
            AdvancedPeripherals.debug("Tried to get armor item slot for non armor item " + stack + ". Returning 0", Level.WARN);
            return -1;
        }

        public int getSlot() {
            return slot;
        }

    }
}
