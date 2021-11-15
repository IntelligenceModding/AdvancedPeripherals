package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.blocks.tileentity.InventoryManagerTile;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.common.configuration.GeneralConfig;
import de.srendi.advancedperipherals.common.util.InventoryUtil;
import de.srendi.advancedperipherals.common.util.ItemUtil;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import de.srendi.advancedperipherals.lib.peripherals.owner.TileEntityPeripheralOwner;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InventoryManagerPeripheral extends BasePeripheral<TileEntityPeripheralOwner<InventoryManagerTile>> {

    public static final String TYPE = "inventoryManager";

    public InventoryManagerPeripheral(InventoryManagerTile tileEntity) {
        super(TYPE, new TileEntityPeripheralOwner<>(tileEntity));
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.ENABLE_INVENTORY_MANAGER.get();
    }

    @LuaFunction
    public final String getOwner() throws LuaException {
        if (getOwnerPlayer() == null)
            return null;
        return getOwnerPlayer().getName().getString();
    }

    @LuaFunction(mainThread = true, value = {"pullItems", "addItemToPlayer"})
    public final int addItemToPlayer(String invDirection, int count, Optional<Integer> slot, Optional<String> item) throws LuaException {
        ItemStack stack = ItemStack.EMPTY;
        if (item.isPresent()) {
            Item item1 = ItemUtil.getRegistryEntry(item.get(), ForgeRegistries.ITEMS);
            stack = new ItemStack(item1, count);
        }

        Direction direction = validateSide(invDirection);

        TileEntity targetEntity = owner.getWorld().getBlockEntity(owner.getPos().relative(direction));
        IItemHandler inventoryFrom = targetEntity != null ? targetEntity
                .getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction).resolve().orElse(null) : null;
        IItemHandler inventoryTo = new PlayerInvWrapper(getOwnerPlayer().inventory);

        int invSlot = slot.orElse(0);

        //if (invSlot >= inventoryTo.getSlots() || invSlot < 0)
          //  throw new LuaException("Inventory out of bounds " + invSlot + " (max: " + (inventoryTo.getSlots() - 1) + ")");

        if (inventoryFrom == null)
            return 0;

        int amount = count;
        int transferableAmount = 0;

        for (int i = 0; i < inventoryFrom.getSlots() && amount > 0; i++) {
            if (stack.isEmpty()) {
                stack = inventoryFrom.getStackInSlot(i).copy();
                if (stack.isEmpty())
                    continue;
            }

            if (ItemHandlerHelper.canItemStacksStack(stack, inventoryFrom.getStackInSlot(i))) {
                int inserted;
                if(invSlot >= 100 && invSlot < 104) {
                    if(!(stack.getItem() instanceof ArmorItem))
                        throw new LuaException(stack + "is not an armor item. Can't put it into the slot " + invSlot);
                    //When there is already an item in the slot, just continue
                    if(!getOwnerPlayer().inventory.armor.get(getArmorSlot(invSlot)).isEmpty())
                        continue;
                    getOwnerPlayer().inventory.armor.set(getArmorSlot(invSlot), stack);
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
        //With this, we can use the item parameter without need to use the slot parameter. If we don't want to use
        //the slot parameter, we can use -1
        int invSlot = -1;
        if (slot.isPresent() && slot.get() > 0)
            invSlot = slot.get();

        Direction direction = validateSide(invDirection);

        TileEntity targetEntity = owner.getWorld().getBlockEntity(owner.getPos().relative(direction));
        PlayerInventory inventoryFrom = getOwnerPlayer().inventory;
        IItemHandler inventoryTo = targetEntity != null ? targetEntity
                .getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction).resolve().orElse(null) : null;

        //invetoryFrom is checked via ensurePlayerIsLinked()
        if (inventoryTo == null)
            return 0;

        int amount = count;
        int transferableAmount = 0;

        ItemStack rest = ItemStack.EMPTY;
        if (invSlot == -1)
            for (int i = 0; i < inventoryFrom.getContainerSize(); i++) {
                if (!stack.isEmpty())
                    if (inventoryFrom.getItem(i).sameItem(stack)) {
                        if (inventoryFrom.getItem(i).getCount() >= amount) {
                            rest = insertItem(inventoryTo, inventoryFrom.removeItem(i, amount));
                            transferableAmount += amount - rest.getCount();
                            break;
                        } else {
                            int subcount = inventoryFrom.getItem(i).getCount();
                            rest = insertItem(inventoryTo, inventoryFrom.removeItem(i, subcount));
                            amount = count - subcount;
                            transferableAmount += subcount - rest.getCount();
                            if (!rest.isEmpty())
                                break;
                        }
                    }
                if (stack.isEmpty())
                    if (inventoryFrom.getItem(i).getCount() >= amount) {
                        rest = insertItem(inventoryTo, inventoryFrom.removeItem(i, amount));
                        transferableAmount += amount - rest.getCount();
                        break;
                    } else {
                        int subcount = inventoryFrom.getItem(i).getCount();
                        rest = insertItem(inventoryTo, inventoryFrom.removeItem(i, subcount));
                        amount = count - subcount;
                        transferableAmount += subcount - rest.getCount();
                        if (!rest.isEmpty())
                            break;
                    }
            }
        if (invSlot != -1) {
            if (!stack.isEmpty())
                if (inventoryFrom.getItem(slot.get()).sameItem(stack)) {
                    if (inventoryFrom.getItem(slot.get()).getCount() >= amount) {
                        rest = insertItem(inventoryTo, inventoryFrom.removeItem(slot.get(), amount));
                        transferableAmount += amount - rest.getCount();
                    } else {
                        int subcount = inventoryFrom.getItem(slot.get()).getCount();
                        rest = insertItem(inventoryTo, inventoryFrom.removeItem(slot.get(), subcount));
                        transferableAmount += subcount - rest.getCount();
                    }
                }
            if (stack.isEmpty())
                if (inventoryFrom.getItem(slot.get()).getCount() >= amount) {
                    rest = insertItem(inventoryTo, inventoryFrom.removeItem(slot.get(), amount));
                    transferableAmount += amount - rest.getCount();
                } else {
                    int subcount = inventoryFrom.getItem(slot.get()).getCount();
                    rest = insertItem(inventoryTo, inventoryFrom.removeItem(slot.get(), subcount));
                    transferableAmount += subcount - rest.getCount();
                }
        }
        if (!rest.isEmpty())
            inventoryFrom.add(rest);

        return transferableAmount;
    }

    @Nonnull
    @LuaFunction(value = {"list", "getItems"}, mainThread = true)
    public final Map<Integer, Object> getItems() throws LuaException {
        Map<Integer, Object> items = new HashMap<>();
        int i = 0; //Used to let users easily sort the items by the slots. Also a better way for the user to see where a item actually is
        for (ItemStack stack : getOwnerPlayer().inventory.items) {
            if (!stack.isEmpty()) {
                items.put(i, LuaConverter.stackToObject(stack));
            }
            i++;
        }
        return items;
    }

    @LuaFunction(mainThread = true)
    public final Map<Integer, Object> getArmor() throws LuaException {
        Map<Integer, Object> items = new HashMap<>();
        int i = 0;
        for (ItemStack stack : getOwnerPlayer().inventory.armor) {
            AdvancedPeripherals.debug("DEBUG1 " + i);
            if (!stack.isEmpty()) {
                items.put(ArmorSlot.getSlotForItem(stack), LuaConverter.stackToObject(stack));
            }
            i++;
        }
        return items;
    }

    @LuaFunction(mainThread = true)
    public final boolean isPlayerEquipped() throws LuaException {
        for (ItemStack stack : getOwnerPlayer().inventory.armor) {
            if (!stack.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @LuaFunction(mainThread = true)
    public final boolean isWearing(int index) throws LuaException {
        int i = 0;
        for (ItemStack stack : getOwnerPlayer().inventory.armor) {
            if (!stack.isEmpty()) {
                if (index == i)
                    return true;
                i++;
            }
        }
        return false;
    }


    @LuaFunction(mainThread = true)
    public final int getEmptySpace() throws LuaException {
        int i = 0;
        for (ItemStack stack : getOwnerPlayer().inventory.items) {
            if (stack.isEmpty())
                i++;

        }
        return i;
    }

    @LuaFunction(mainThread = true)
    public final boolean isSpaceAvailable() throws LuaException {
        return getEmptySpace() > 0;
    }

    @LuaFunction(mainThread = true)
    public final int getFreeSlot() throws LuaException {
        return getOwnerPlayer().inventory.getFreeSlot();
    }

    @LuaFunction(mainThread = true)
    public final Map<String, Object> getItemInHand() throws LuaException {
        return LuaConverter.stackToObject(getOwnerPlayer().inventory.getSelected());
    }

    private PlayerEntity getOwnerPlayer() throws LuaException {
        if (owner.getOwner() == null)
            throw new LuaException("The Inventory Manager doesn't have a memory card or it isn't bound to a player.");
        return owner.getOwner();
    }

    private ItemStack insertItem(IItemHandler inventoryTo, ItemStack stack) {
        for (int i = 0; i < inventoryTo.getSlots(); i++) {
            if (stack.isEmpty())
                break;
            //Fixes https://github.com/Seniorendi/AdvancedPeripherals/issues/93
            if (!stack.hasTag())
                stack.setTag(null);
            stack = inventoryTo.insertItem(i, stack, false);
        }
        return stack;
    }

    private static int getArmorSlot(int index) {
        switch (index) {
            case 103:
                return 3;
            case 102:
                return 2;
            case 101:
                return 1;
            case 100:
                return 0;
        }
        return 0;
    }

    /**
     * Used to get the proper slot number for armor. See https://docs.srendi.de/ for the slot numbers
     *
     * @see InventoryManagerPeripheral#getArmor()
     */
    private enum ArmorSlot {

        HELMET_SLOT(103, EquipmentSlotType.HEAD),
        CHEST_SLOT(102, EquipmentSlotType.CHEST),
        LEGGINGS_SLOT(101, EquipmentSlotType.LEGS),
        BOOTS_SLOT(100, EquipmentSlotType.FEET);

        private final int slot;
        private final EquipmentSlotType slotType;

        ArmorSlot(int slot, EquipmentSlotType slotType) {
            this.slot = slot;
            this.slotType = slotType;
        }

        public int getSlot() {
            return slot;
        }

        public static int getSlotForItem(ItemStack stack) {
            if (stack.getItem() instanceof ArmorItem) {
                for (ArmorSlot slot : values()) {
                    if (((ArmorItem) stack.getItem()).getSlot() == slot.slotType) {
                        return slot.slot;
                    }
                }
            }
            AdvancedPeripherals.LOGGER.warn("Tried to get armor item slot for non armor item " + stack + ". Returning 0");
            return 0;
        }

    }
}
