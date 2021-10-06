package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.shared.util.NBTUtil;
import de.srendi.advancedperipherals.common.blocks.tileentity.InventoryManagerTile;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import de.srendi.advancedperipherals.common.util.ItemUtil;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import de.srendi.advancedperipherals.lib.peripherals.owner.TileEntityPeripheralOwner;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;

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
        return AdvancedPeripheralsConfig.enableInventoryManager;
    }

    @LuaFunction
    public final String getOwner() {
        if (getOwnerPlayer() == null)
            return null;
        return getOwnerPlayer().getName().getString();
    }

    @LuaFunction(mainThread = true, value = {"pullItems", "addItemToPlayer"})
    public final int addItemToPlayer(String invDirection, int count, Optional<Integer> slot, Optional<String> item) throws LuaException {
        ensurePlayerIsLinked();
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
        IItemHandler inventoryFrom = targetEntity != null ? targetEntity
                .getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, direction).resolve().orElse(null) : null;
        PlayerInventory inventoryTo = getOwnerPlayer().inventory;

        //inventoryTo is checked via ensurePlayerIsLinked()
        if (inventoryFrom == null)
            return 0;

        int amount = count;
        int transferableAmount = 0;

        if (invSlot == -1)
            for (int i = 0; i < inventoryFrom.getSlots(); i++) {
                if (!stack.isEmpty())
                    if (inventoryFrom.getStackInSlot(i).sameItem(stack)) {
                        if (inventoryFrom.getStackInSlot(i).getCount() >= amount) {
                            if (inventoryTo.add(inventoryFrom.extractItem(i, amount, true))) {
                                inventoryFrom.extractItem(i, amount, false);
                                transferableAmount += amount;
                            }
                            break;
                        } else {
                            int subcount = inventoryFrom.getStackInSlot(i).getCount();
                            if (inventoryTo.add(inventoryFrom.extractItem(i, subcount, true))) {
                                inventoryFrom.extractItem(i, subcount, false);
                                amount = count - subcount;
                                transferableAmount += subcount;
                            }
                        }
                    }
                if (stack.isEmpty())
                    if (inventoryFrom.getStackInSlot(i).getCount() >= amount) {
                        if (inventoryTo.add(inventoryFrom.extractItem(i, amount, true))) {
                            inventoryFrom.extractItem(i, amount, false);
                            transferableAmount += amount;
                        }
                        break;
                    } else {
                        int subcount = inventoryFrom.getStackInSlot(i).getCount();
                        if (inventoryTo.add(inventoryFrom.extractItem(i, subcount, true))) {
                            inventoryFrom.extractItem(i, subcount, false);
                            amount = count - subcount;
                            transferableAmount += subcount;
                        }
                    }
            }
        if (invSlot != -1) {
            if (stack.isEmpty())
                if (inventoryFrom.getStackInSlot(slot.get()).getCount() >= amount) {
                    if (inventoryTo.add(inventoryFrom.extractItem(slot.get(), amount, true))) {
                        inventoryFrom.extractItem(slot.get(), amount, false);
                        transferableAmount += amount;
                    }
                } else {
                    int subcount = inventoryFrom.getStackInSlot(slot.get()).getCount();
                    if (inventoryTo.add(inventoryFrom.extractItem(slot.get(), subcount, true))) {
                        inventoryFrom.extractItem(slot.get(), subcount, false);
                        transferableAmount += subcount;
                    }
                }
            if (!stack.isEmpty())
                if (inventoryFrom.getStackInSlot(slot.get()).sameItem(stack)) {
                    if (inventoryFrom.getStackInSlot(slot.get()).getCount() >= amount) {
                        if (inventoryTo.add(inventoryFrom.extractItem(slot.get(), amount, true))) {
                            inventoryFrom.extractItem(slot.get(), amount, false);
                            transferableAmount += amount;
                        }
                    } else {
                        int subcount = inventoryFrom.getStackInSlot(slot.get()).getCount();
                        if (inventoryTo.add(inventoryFrom.extractItem(slot.get(), subcount, true))) {
                            inventoryFrom.extractItem(slot.get(), subcount, false);
                            transferableAmount += subcount;
                        }
                    }
                }
        }
        return transferableAmount;
    }

    @LuaFunction(mainThread = true, value = {"pushItems", "removeItemFromPlayer"})
    public final int removeItemFromPlayer(String invDirection, int count, Optional<Integer> slot, Optional<String> item) throws LuaException {
        ensurePlayerIsLinked();
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

    @LuaFunction(value = {"list", "getItems"}, mainThread = true)
    public final Map<Integer, Object> getItems() throws LuaException {
        ensurePlayerIsLinked();
        Map<Integer, Object> items = new HashMap<>();
        int i = 0;
        for (ItemStack stack : getOwnerPlayer().inventory.items) {
            if (!stack.isEmpty()) {
                Map<String, Object> map = new HashMap<>();
                String displayName = stack.getDisplayName().getString();
                CompoundNBT nbt = stack.getTag(); //use getTag instead of getOrCreateTag to fix https://github.com/Seniorendi/AdvancedPeripherals/issues/177
                map.put("name", stack.getItem().getRegistryName().toString());
                map.put("amount", stack.getCount());
                map.put("displayName", displayName);
                if(nbt == null) {
                    nbt = new CompoundNBT();//ensure compatibility with lua programs relying on a non-nil value
                }
                map.put("nbt", NBTUtil.toLua(nbt));
                map.put("tags", LuaConverter.tagsToList(stack.getItem().getTags()));
                items.put(i, map);
                i++;
            }
        }
        return items;
    }

    @LuaFunction(mainThread = true)
    public final Map<Integer, Object> getArmor() throws LuaException {
        ensurePlayerIsLinked();
        Map<Integer, Object> items = new HashMap<>();
        int i = 0;
        for (ItemStack stack : getOwnerPlayer().inventory.armor) {
            if (!stack.isEmpty()) {
                Map<String, Object> map = new HashMap<>();
                String displayName = stack.getDisplayName().getString();
                CompoundNBT nbt = stack.getTag(); //use getTag instead of getOrCreateTag to fix https://github.com/Seniorendi/AdvancedPeripherals/issues/177
                map.put("name", stack.getItem().getRegistryName().toString());
                map.put("amount", stack.getCount());
                map.put("displayName", displayName);
                if(nbt == null) {
                    nbt = new CompoundNBT();//ensure compatibility with lua programs relying on a non-nil value
                }
                map.put("nbt", NBTUtil.toLua(nbt));
                map.put("tags", LuaConverter.tagsToList(stack.getItem().getTags()));
                items.put(i, map);
                i++;
            }
        }
        return items;
    }

    @LuaFunction(mainThread = true)
    public final boolean isPlayerEquipped() throws LuaException {
        ensurePlayerIsLinked();
        for (ItemStack stack : getOwnerPlayer().inventory.armor) {
            if (!stack.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @LuaFunction(mainThread = true)
    public final boolean isWearing(int index) throws LuaException {
        ensurePlayerIsLinked();
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

    private PlayerEntity getOwnerPlayer() {
        return owner.getOwner();
    }

    private void ensurePlayerIsLinked() throws LuaException {
        if (getOwnerPlayer() == null)
            throw new LuaException("The Inventory Manager doesn't have a memory card or it isn't bound to a player.");
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
}
