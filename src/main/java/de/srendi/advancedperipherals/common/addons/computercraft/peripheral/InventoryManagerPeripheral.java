package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.PlayerArmorInvWrapper;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;
import net.minecraftforge.items.wrapper.PlayerOffhandInvWrapper;
import top.theillusivec4.curios.api.CuriosApi;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class InventoryManagerPeripheral extends BasePeripheral<BlockEntityPeripheralOwner<InventoryManagerEntity>> {

    public static final String PERIPHERAL_TYPE = "inventoryManager";

    public InventoryManagerPeripheral(InventoryManagerEntity tileEntity) {
        super(PERIPHERAL_TYPE, new BlockEntityPeripheralOwner<>(tileEntity));
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.enableInventoryManager.get();
    }

    @LuaFunction
    public final String getOwner() throws LuaException {
        if (owner.getOwner() == null) return null;
        return getOwnerPlayer().getName().getString();
    }

    /**
     * @deprecated Switch to the function with the item filter object
     */
    @Deprecated
    @LuaFunction(mainThread = true, value = {"pullItems", "addItemToPlayer"})
    public final MethodResult addItemToPlayer(String invDirection, int count, Optional<Integer> slot, Optional<String> item) throws LuaException {
        Pair<ItemFilter, String> filter;
        Map<Object, Object> filterMap = new HashMap<>();

        // Deprecated! Will be removed in the future. This exists to maintain compatibility within the same mc version
        item.ifPresent(s -> filterMap.put("name", s));
        slot.ifPresent(integer -> filterMap.put("toSlot", integer));
        filterMap.put("count", count);

        filter = ItemFilter.parse(filterMap);
        if (filter.rightPresent())
            return MethodResult.of(0, filter.getRight());
        return MethodResult.of(addItemCommon(invDirection, filter.getLeft()));

    }

    //Add the specified item to the player
    //The item is specified the same as with the RS/ME bridge:
    //{name="minecraft:enchanted_book", count=1, nbt="ae70053c97f877de546b0248b9ddf525"}
    //If a count is specified in the item it is silently IGNORED
    @LuaFunction(mainThread = true)
    public final MethodResult addItemToPlayerNBT(String invDirection, int count, Optional<Integer> slot, Optional<Map<?, ?>> item) throws LuaException {
        Pair<ItemFilter, String> filter;
        Map<Object, Object> filterMap = new HashMap<>();

        // Deprecated! Will be removed in the future. This exists to maintain compatibility within the same mc version
        slot.ifPresent(integer -> filterMap.put("toSlot", integer));
        filterMap.put("count", count);

        item.ifPresent(filterMap::putAll);

        filter = ItemFilter.parse(filterMap);
        if (filter.rightPresent())
            return MethodResult.of(0, filter.getRight());

        return MethodResult.of(addItemCommon(invDirection, filter.getLeft()), null);
    }

    private int addItemCommon(String invDirection, ItemFilter filter) throws LuaException {
        Direction direction = validateSide(invDirection);

        BlockEntity targetEntity = owner.getLevel().getBlockEntity(owner.getPos().relative(direction));
        IItemHandler inventoryFrom = targetEntity != null ? targetEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction).resolve().orElse(null) : null;
        Pair<IItemHandler, Integer> inventoryTo = getHandlerFromSlot(filter.getToSlot());

        inventoryTo.ifRightPresent(slot -> filter.toSlot = slot);

        //if (invSlot >= inventoryTo.getSlots() || invSlot < 0)
        //  throw new LuaException("Inventory out of bounds " + invSlot + " (max: " + (inventoryTo.getSlots() - 1) + ")");

        return InventoryUtil.moveItem(inventoryFrom, inventoryTo.getLeft(), filter);
    }

    @LuaFunction(mainThread = true, value = {"pushItems", "removeItemFromPlayer"})
    public final MethodResult removeItemFromPlayer(String invDirection, int count, Optional<Integer> slot, Optional<String> item) throws LuaException {
        Pair<ItemFilter, String> filter;
        Map<Object, Object> filterMap = new HashMap<>();

        // Deprecated! Will be removed in the future. This exists to maintain compatibility within the same mc version
        item.ifPresent(itemName -> filterMap.put("name", itemName));
        slot.ifPresent(toSlot -> filterMap.put("toSlot", toSlot));
        filterMap.put("count", count);

        filter = ItemFilter.parse(filterMap);
        if (filter.rightPresent())
            return MethodResult.of(0, filter.getRight());
        return removeItemCommon(invDirection, filter.getLeft());
    }

    @LuaFunction(mainThread = true)
    public final MethodResult removeItemFromPlayerNBT(String invDirection, int count, Optional<Integer> slot, Optional<Map<?, ?>> item) throws LuaException {
        Pair<ItemFilter, String> filter;
        Map<Object, Object> filterMap = new HashMap<>();

        // Deprecated! Will be removed in the future. This exists to maintain compatibility within the same mc version
        slot.ifPresent(toSlot -> filterMap.put("toSlot", toSlot));
        filterMap.put("count", count);

        item.ifPresent(filterMap::putAll);

        filter = ItemFilter.parse(filterMap);
        if (filter.rightPresent())
            return MethodResult.of(0, filter.getRight());

        return removeItemCommon(invDirection, filter.getLeft());
    }

    private MethodResult removeItemCommon(String invDirection, ItemFilter filter) throws LuaException {
        Direction direction = validateSide(invDirection);

        BlockEntity targetEntity = owner.getLevel().getBlockEntity(owner.getPos().relative(direction));
        Pair<IItemHandler, Integer> inventoryFrom = getHandlerFromSlot(filter.getFromSlot());
        IItemHandler inventoryTo = targetEntity != null ? targetEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction).resolve().orElse(null) : null;

        if(inventoryTo == null)
            return MethodResult.of(0, "INVENTORY_TO_INVALID");

        inventoryFrom.ifRightPresent(slot -> filter.fromSlot = slot);

        return MethodResult.of(InventoryUtil.moveItem(inventoryFrom.getLeft(), inventoryTo, filter));
    }

    @LuaFunction(value = {"list", "getItems"}, mainThread = true)
    public final List<Object> getItems() throws LuaException {
        List<Object> items = new ArrayList<>();
        int i = 0; //Used to let users easily sort the items by the slots. Also, a better way for the user to see where an item actually is
        for (ItemStack stack : getOwnerPlayer().getInventory().items) {
            if (!stack.isEmpty()) {
                items.add(LuaConverter.stackToObjectWithSlot(stack, i));
            }
            i++;
        }
        return items;
    }

    @LuaFunction(value = { "listCurios", "getItemsCurios" }, mainThread = true)
    public final List<Object> getCuriosItems() throws LuaException {
        List<Object> items = new ArrayList<>();
        // not sure how to handle slots, since Curios slots can change depending on config, advancements, etc.
        CuriosApi.getCuriosHelper().getEquippedCurios(getOwnerPlayer()).ifPresent(handler -> {
            for (int slot = 0; slot < handler.getSlots(); slot++) {
                ItemStack stack = handler.getStackInSlot(slot);
                if (!stack.isEmpty()) {
                    items.add(LuaConverter.stackToObjectWithSlot(stack, slot));
                }
            }
        });
        return items;
    }

    private int addCuriosToPlayerCommon(String invDirection, ItemFilter filter) throws LuaException {
        Direction direction = validateSide(invDirection);

        BlockEntity targetEntity = owner.getLevel().getBlockEntity(owner.getPos().relative(direction));
        IItemHandler inventoryFrom = targetEntity != null
                ? targetEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction).resolve()
                        .orElse(null)
                : null;

        Optional<IItemHandler> maybeInventoryTo = getCuriosHandler();
        if (maybeInventoryTo.isEmpty())
            return 0;

        IItemHandler inventoryTo = maybeInventoryTo.get();

        return InventoryUtil.moveItem(inventoryFrom, inventoryTo, filter);

    }

    private MethodResult removeCuriosFromPlayerCommon(String invDirection, ItemFilter filter) throws LuaException {
        Direction direction = validateSide(invDirection);

        BlockEntity targetEntity = owner.getLevel().getBlockEntity(owner.getPos().relative(direction));
        IItemHandler inventoryTo = targetEntity != null
                ? targetEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction).resolve()
                        .orElse(null)
                : null;

        Optional<IItemHandler> maybeInventoryFrom = getCuriosHandler();
        if (maybeInventoryFrom.isEmpty())
            return MethodResult.of(0, "INVENTORY_FROM_INVALID");

        IItemHandler inventoryFrom = maybeInventoryFrom.get();

        return MethodResult.of(InventoryUtil.moveItem(inventoryFrom, inventoryTo, filter));
    }

    @LuaFunction(mainThread = true, value = { "addCuriosToPlayer", "addCurios" })
    public final MethodResult addCuriosToPlayer(String invDirection, int count, Optional<Integer> slot,
            Optional<String> item)
            throws LuaException {
        Pair<ItemFilter, String> filter;
        Map<Object, Object> filterMap = new HashMap<>();

        // Deprecated! Will be removed in the future. This exists to maintain
        // compatibility within the same mc version
        item.ifPresent(itemName -> filterMap.put("name", itemName));
        slot.ifPresent(toSlot -> filterMap.put("toSlot", toSlot));
        filterMap.put("count", count);

        filter = ItemFilter.parse(filterMap);
        if (filter.rightPresent()) // right contains the error message
            return MethodResult.of(0, filter.getRight());

        // filter is good, try to add the item
        return MethodResult.of(addCuriosToPlayerCommon(invDirection, filter.getLeft()));
    }

    @LuaFunction(mainThread = true, value = {"removeCuriosFromPlayer", "removeCurios"})
    public final MethodResult removeCuriosFromPlayer(String invDirection, int count, Optional<Integer> slot, Optional<String> item) throws LuaException {
        Pair<ItemFilter, String> filter;
        Map<Object, Object> filterMap = new HashMap<>();

        // Deprecated! Will be removed in the future. This exists to maintain compatibility within the same mc version
        item.ifPresent(itemName -> filterMap.put("name", itemName));
        slot.ifPresent(toSlot -> filterMap.put("toSlot", toSlot));
        filterMap.put("count", count);

        filter = ItemFilter.parse(filterMap);
        if (filter.rightPresent())
            return MethodResult.of(0, filter.getRight());

        return MethodResult.of(removeCuriosFromPlayerCommon(invDirection, filter.getLeft()));
    }

    @LuaFunction(value = {"listChest", "getItemsChest"}, mainThread = true)
    public final MethodResult getItemsChest(String target) throws LuaException {
        Direction direction = validateSide(target);

        BlockEntity targetEntity = owner.getLevel().getBlockEntity(owner.getPos().relative(direction));
        IItemHandler inventoryTo = targetEntity != null ? targetEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction).resolve().orElse(null) : null;

        if(inventoryTo == null)
            return MethodResult.of(null, "INVENTORY_TO_INVALID");

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
        for (ItemStack stack : getOwnerPlayer().getInventory().armor) {
            if (!stack.isEmpty()) {
                items.add(LuaConverter.stackToObjectWithSlot(stack, ArmorSlot.getSlotForItem(stack)));
            }
        }
        return items;
    }

    @LuaFunction(mainThread = true)
    public final boolean isPlayerEquipped() throws LuaException {
        for (ItemStack stack : getOwnerPlayer().getInventory().armor) {
            if (!stack.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @LuaFunction(mainThread = true)
    public final boolean isWearing(int index) throws LuaException {
        int i = 0;
        for (ItemStack stack : getOwnerPlayer().getInventory().armor) {
            if (!stack.isEmpty()) {
                if (index == i - 100) return true;
                i++;
            }
        }
        return false;
    }

    @LuaFunction(mainThread = true)
    public final int getEmptySpace() throws LuaException {
        int i = 0;
        for (ItemStack stack : getOwnerPlayer().getInventory().items) {
            if (stack.isEmpty()) i++;
        }
        return i;
    }

    @LuaFunction(mainThread = true)
    public final boolean isSpaceAvailable() throws LuaException {
        return getEmptySpace() > 0;
    }

    @LuaFunction(mainThread = true)
    public final int getFreeSlot() throws LuaException {
        return getOwnerPlayer().getInventory().getFreeSlot();
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Object> getItemInHand() throws LuaException {
        return LuaConverter.stackToObject(getOwnerPlayer().getMainHandItem());
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Object> getItemInOffHand() throws LuaException {
        return LuaConverter.stackToObject(getOwnerPlayer().getOffhandItem());
    }

    private Player getOwnerPlayer() throws LuaException {
        if (owner.getOwner() == null)
            throw new LuaException("The Inventory Manager doesn't have a memory card or it isn't bound to a player.");
        return owner.getOwner();
    }

    @NotNull
    private Pair<IItemHandler, Integer> getHandlerFromSlot(int slot) throws LuaException {
        IItemHandler handler;
        if (slot >= 100 && slot <= 103) {
            handler = new PlayerArmorInvWrapper(getOwnerPlayer().getInventory());
            // If the slot is between 100 and 103, change the index to a normal index between 0 and 3.
            // This is necessary since the PlayerArmorInvWrapper does not work with these higher indexes
            slot = slot - 100;
        } else if (slot == 36) {
            handler = new PlayerOffhandInvWrapper(getOwnerPlayer().getInventory());
            // Set the "from slot" to zero so the offhand wrapper can work with that
            slot = 0;
        } else {
            handler = new PlayerInvWrapper(getOwnerPlayer().getInventory());
        }
        return Pair.of(handler, slot);
    }

    private Optional<IItemHandler> getCuriosHandler() throws LuaException {
        LazyOptional<IItemHandlerModifiable> handler = CuriosApi.getCuriosHelper().getEquippedCurios(getOwnerPlayer());
        if (handler.isPresent()) {
            return Optional.of(handler.orElse(null));
        } else {
            return Optional.empty();
        }
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
                    if (armorItem.getSlot() == slot.slotType) {
                        return slot.slot;
                    }
                }
            }
            AdvancedPeripherals.LOGGER.warn("Tried to get armor item slot for non armor item " + stack + ". Returning 0");
            return 0;
        }

        public int getSlot() {
            return slot;
        }

    }
}
