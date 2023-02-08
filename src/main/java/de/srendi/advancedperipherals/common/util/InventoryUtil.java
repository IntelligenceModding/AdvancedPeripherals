package de.srendi.advancedperipherals.common.util;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.IPeripheralOwner;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class InventoryUtil {

    public static IItemHandler extractHandler(@Nullable Object object) {
        if (object instanceof ICapabilityProvider capabilityProvider) {
            LazyOptional<IItemHandler> cap = capabilityProvider.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
            if (cap.isPresent())
                return cap.orElseThrow(NullPointerException::new);
        }
        if (object instanceof IItemHandler itemHandler)
            return itemHandler;
        if (object instanceof Container container)
            return new InvWrapper(container);
        return null;
    }

    public static int moveItem(IItemHandler inventoryFrom, IItemHandler inventoryTo, ItemFilter filter) {
        if (inventoryFrom == null) return 0;

        int fromSlot = filter.getFromSlot();
        int toSlot = filter.getToSlot();

        int amount = filter.getCount();
        int transferableAmount = 0;

        // The logic changes with storage systems since these systems do not have slots
        if (inventoryFrom instanceof IStorageSystemItemHandler storageSystemHandler) {
            for (int i = toSlot == -1 ? 0 : toSlot; i < (toSlot == -1 ? inventoryTo.getSlots() : toSlot + 1); i++) {
                ItemStack extracted = storageSystemHandler.extractItem(filter, true);
                ItemStack inserted;
                if (toSlot == -1) {
                    inserted = ItemHandlerHelper.insertItem(inventoryTo, extracted, false);
                } else {
                    inserted = inventoryTo.insertItem(toSlot, extracted, false);
                }
                amount -= inserted.getCount();
                transferableAmount += storageSystemHandler.extractItem(filter, false).getCount();
                if (transferableAmount >= filter.getCount())
                    break;
            }
            return transferableAmount;
        }

        if (inventoryTo instanceof IStorageSystemItemHandler storageSystemHandler) {
            for (int i = fromSlot == -1 ? 0 : fromSlot; i < (fromSlot == -1 ? inventoryFrom.getSlots() : fromSlot + 1); i++) {
                if (filter.test(inventoryFrom.getStackInSlot(i))) {
                    ItemStack extracted = inventoryFrom.extractItem(i, amount - transferableAmount, true);
                    ItemStack inserted = storageSystemHandler.insertItem(toSlot, extracted, false);

                    amount -= inserted.getCount();
                    transferableAmount += inventoryFrom.extractItem(i, extracted.getCount() - inserted.getCount(), false).getCount();
                    if (transferableAmount >= filter.getCount())
                        break;
                }
            }
            return transferableAmount;
        }

        for (int i = fromSlot == -1 ? 0 : fromSlot; i < (fromSlot == -1 ? inventoryFrom.getSlots() : fromSlot + 1); i++) {
            if (filter.test(inventoryFrom.getStackInSlot(i))) {
                ItemStack extracted = inventoryFrom.extractItem(i, amount - transferableAmount, true);
                ItemStack inserted;
                if (toSlot == -1) {
                    inserted = ItemHandlerHelper.insertItem(inventoryTo, extracted, false);
                } else {
                    inserted = inventoryTo.insertItem(toSlot, extracted, false);
                }
                amount -= inserted.getCount();
                transferableAmount += inventoryFrom.extractItem(i, extracted.getCount() - inserted.getCount(), false).getCount();
                if (transferableAmount >= filter.getCount())
                    break;
            }
        }
        return transferableAmount;
    }

    @Nullable
    public static IItemHandler getHandlerFromName(@NotNull IComputerAccess access, String name) throws LuaException {
        IPeripheral location = access.getAvailablePeripheral(name);
        if (location == null)
            return null;

        return extractHandler(location.getTarget());
    }

    @Nullable
    public static IItemHandler getHandlerFromDirection(@NotNull String direction, @NotNull IPeripheralOwner owner) throws LuaException {
        Level level = owner.getLevel();
        Objects.requireNonNull(level);
        Direction relativeDirection = CoordUtil.getDirection(owner.getOrientation(), direction);
        BlockEntity target = level.getBlockEntity(owner.getPos().relative(relativeDirection));
        if (target == null)
            return null;

        return extractHandler(target);
    }
}
