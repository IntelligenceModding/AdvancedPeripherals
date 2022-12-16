package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.blocks.blockentities.InventoryManagerEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.util.InventoryUtil;
import de.srendi.advancedperipherals.common.util.ItemUtil;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InventoryManagerPeripheral extends BasePeripheral<BlockEntityPeripheralOwner<InventoryManagerEntity>> {

    public static final String PERIPHERAL_TYPE = "inventoryManager";

    public InventoryManagerPeripheral(InventoryManagerEntity tileEntity) {
        super(PERIPHERAL_TYPE, new BlockEntityPeripheralOwner<>(tileEntity));
    }

    private static int getArmorSlot(int index) {
        return switch (index) {
            case 103 -> 3;
            case 102 -> 2;
            case 101 -> 1;
            default -> 0;
        };
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

    @LuaFunction(mainThread = true, value = {"pullItems", "addItemToPlayer"})
    public final int addItemToPlayer(String invDirection, int count, Optional<Integer> slot, Optional<String> item) throws LuaException {
        ItemStack stack = ItemStack.EMPTY;
        if (item.isPresent()) {
            Item item1 = ItemUtil.getRegistryEntry(item.get(), ForgeRegistries.ITEMS);
            stack = new ItemStack(item1, count);
        }

        return addItemCommon(invDirection, count, slot, stack);

    }

    //Add the specified item to the player
    //The item is specified the same as with the RS/ME bridge:
    //{name="minecraft:enchanted_book", count=1, nbt="ae70053c97f877de546b0248b9ddf525"}
    //If a count is specified in the item it is silently IGNORED
    @LuaFunction(mainThread = true)
    public final int addItemToPlayerNBT(String invDirection, int count, Optional<Integer> slot, Optional<Map<?, ?>> item) throws LuaException {
        ItemStack stack = ItemStack.EMPTY;
        if (item.isPresent()) {
            Direction direction = validateSide(invDirection);

            BlockEntity targetEntity = owner.getLevel().getBlockEntity(owner.getPos().relative(direction));
            IItemHandler inventoryFrom = targetEntity != null ? targetEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction).resolve().orElse(null) : null;

            //We can use getItemStackRS, as it works with List<ItemStack>
            //And doesn't use anything RS specific
            stack = ItemUtil.getItemStackRS(item.get(), ItemUtil.getItemsFromItemHandler(inventoryFrom));
        }

        return addItemCommon(invDirection, count, slot, stack);
    }

    private int addItemCommon(String invDirection, int count, Optional<Integer> slot, ItemStack stack) throws LuaException {
        Direction direction = validateSide(invDirection);

        BlockEntity targetEntity = owner.getLevel().getBlockEntity(owner.getPos().relative(direction));
        IItemHandler inventoryFrom = targetEntity != null ? targetEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction).resolve().orElse(null) : null;
        IItemHandler inventoryTo = new PlayerInvWrapper(getOwnerPlayer().getInventory());

        int invSlot = slot.orElse(0);

        //if (invSlot >= inventoryTo.getSlots() || invSlot < 0)
        //  throw new LuaException("Inventory out of bounds " + invSlot + " (max: " + (inventoryTo.getSlots() - 1) + ")");

        if (inventoryFrom == null) return 0;

        int amount = count;
        int transferableAmount = 0;

        for (int i = 0; i < inventoryFrom.getSlots() && amount > 0; i++) {
            if (stack.isEmpty()) {
                stack = inventoryFrom.getStackInSlot(i).copy();
                if (stack.isEmpty()) continue;
            }

            if (ItemHandlerHelper.canItemStacksStack(stack, inventoryFrom.getStackInSlot(i))) {
                int inserted;
                if (invSlot >= 100 && invSlot < 104) {
                    if (!(stack.getItem() instanceof ArmorItem))
                        throw new LuaException(stack + "is not an armor item. Can't put it into the slot " + invSlot);
                    //When there is already an item in the slot, just continue
                    if (!getOwnerPlayer().getInventory().armor.get(getArmorSlot(invSlot)).isEmpty()) continue;
                    getOwnerPlayer().getInventory().armor.set(getArmorSlot(invSlot), stack);
                    inventoryFrom.extractItem(i, 1, false);
                    //Armor can't be stacked, so we set this just to one
                    transferableAmount = 1;
                    //Continue as we don't want to run the normal code for non armor items
                    continue;
                }
                inserted = InventoryUtil.moveItem(inventoryFrom, i, inventoryTo, invSlot, amount);
                transferableAmount += inserted;
                amount -= inserted;

                inserted = InventoryUtil.moveItem(inventoryFrom, i, inventoryTo, -1, amount);
                transferableAmount += inserted;
                amount -= inserted;
            }
        }

        return transferableAmount;
    }

    @LuaFunction(mainThread = true, value = {"pushItems", "removeItemFromPlayer"})
    public final int removeItemFromPlayer(String invDirection, int count, Optional<Integer> slot, Optional<String> item) throws LuaException {
        ItemStack stack = ItemStack.EMPTY;
        if (item.isPresent()) {
            Item item1 = ItemUtil.getRegistryEntry(item.get(), ForgeRegistries.ITEMS);
            stack = new ItemStack(item1, count);
        }

        return removeItemCommon(invDirection, count, slot, stack);
    }

    @LuaFunction(mainThread = true)
    public final int removeItemFromPlayerNBT(String invDirection, int count, Optional<Integer> slot, Optional<Map<?, ?>> item) throws LuaException {
        ItemStack stack = ItemStack.EMPTY;
        if (item.isPresent()) {
            Direction direction = validateSide(invDirection);

            BlockEntity targetEntity = owner.getLevel().getBlockEntity(owner.getPos().relative(direction));
            IItemHandler inventoryFrom = targetEntity != null ? targetEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction).resolve().orElse(null) : null;

            //We can use getItemStackRS, as it works with List<ItemStack>
            //And doesn't use anything RS specific
            stack = ItemUtil.getItemStackRS(item.get(), ItemUtil.getItemsFromItemHandler(inventoryFrom));
        }

        return removeItemCommon(invDirection, count, slot, stack);
    }

    private int removeItemCommon(String invDirection, int count, Optional<Integer> slot, ItemStack stack) throws LuaException {
        //With this, we can use the item parameter without need to use the slot parameter. If we don't want to use
        //the slot parameter, we can use -1
        int invSlot = -1;
        if (slot.isPresent() && slot.get() > 0) invSlot = slot.get();

        Direction direction = validateSide(invDirection);

        BlockEntity targetEntity = owner.getLevel().getBlockEntity(owner.getPos().relative(direction));
        Inventory inventoryFrom = getOwnerPlayer().getInventory();
        IItemHandler inventoryTo = targetEntity != null ? targetEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction).resolve().orElse(null) : null;

        //invetoryFrom is checked via ensurePlayerIsLinked()
        if (inventoryTo == null) return 0;

        int amount = count;
        int transferableAmount = 0;

        ItemStack rest = ItemStack.EMPTY;
        if (invSlot == -1) {
            for (int i = 0; i < inventoryFrom.getContainerSize(); i++) {
                if (!stack.isEmpty() && inventoryFrom.getItem(i).sameItem(stack)) {
                    if (inventoryFrom.getItem(i).getCount() >= amount) {
                        rest = insertItem(inventoryTo, inventoryFrom.removeItem(i, amount));
                        transferableAmount += amount - rest.getCount();
                        break;
                    } else {
                        int subCount = inventoryFrom.getItem(i).getCount();
                        rest = insertItem(inventoryTo, inventoryFrom.removeItem(i, subCount));
                        amount -= subCount;
                        transferableAmount += subCount - rest.getCount();
                        if (!rest.isEmpty()) break;
                    }
                }
                if (stack.isEmpty() && inventoryFrom.getItem(i).getCount() >= amount) {
                    rest = insertItem(inventoryTo, inventoryFrom.removeItem(i, amount));
                    transferableAmount += amount - rest.getCount();
                    break;
                } else {
                    int subCount = inventoryFrom.getItem(i).getCount();
                    rest = insertItem(inventoryTo, inventoryFrom.removeItem(i, subCount));
                    amount -= subCount;
                    transferableAmount += subCount - rest.getCount();
                    if (!rest.isEmpty()) break;
                }
            }
        }
        if (invSlot != -1) {
            if (!stack.isEmpty() && inventoryFrom.getItem(slot.get()).sameItem(stack)) {
                if (inventoryFrom.getItem(slot.get()).getCount() >= amount) {
                    rest = insertItem(inventoryTo, inventoryFrom.removeItem(slot.get(), amount));
                    transferableAmount += amount - rest.getCount();
                } else {
                    int subCount = inventoryFrom.getItem(slot.get()).getCount();
                    rest = insertItem(inventoryTo, inventoryFrom.removeItem(slot.get(), subCount));
                    transferableAmount += subCount - rest.getCount();
                }
            }
            if (stack.isEmpty() && inventoryFrom.getItem(slot.get()).getCount() >= amount) {
                rest = insertItem(inventoryTo, inventoryFrom.removeItem(slot.get(), amount));
                transferableAmount += amount - rest.getCount();
            } else {
                int subCount = inventoryFrom.getItem(slot.get()).getCount();
                rest = insertItem(inventoryTo, inventoryFrom.removeItem(slot.get(), subCount));
                transferableAmount += subCount - rest.getCount();
            }
        }
        if (!rest.isEmpty()) inventoryFrom.add(rest);

        return transferableAmount;
    }

    @Nonnull
    @LuaFunction(value = {"list", "getItems"}, mainThread = true)
    public final List<Object> getItems() throws LuaException {
        List<Object> items = new ArrayList<>();
        int i = 0; //Used to let users easily sort the items by the slots. Also a better way for the user to see where a item actually is
        for (ItemStack stack : getOwnerPlayer().getInventory().items) {
            if (!stack.isEmpty()) {
                items.add(LuaConverter.stackToObjectWithSlot(stack, i));
            }
            i++;
        }
        return items;
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
                if (index == getArmorSlot(i)) return true;
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

    private ItemStack insertItem(IItemHandler inventoryTo, ItemStack stack) {
        for (int i = 0; i < inventoryTo.getSlots(); i++) {
            if (stack.isEmpty()) break;
            //Fixes https://github.com/Seniorendi/AdvancedPeripherals/issues/93
            if (!stack.hasTag()) stack.setTag(null);
            stack = inventoryTo.insertItem(i, stack, false);
        }
        return stack;
    }

    private Player getOwnerPlayer() throws LuaException {
        if (owner.getOwner() == null)
            throw new LuaException("The Inventory Manager doesn't have a memory card or it isn't bound to a player.");
        return owner.getOwner();
    }

    /**
     * Used to get the proper slot number for armor. See https://docs.srendi.de/ for the slot numbers
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
