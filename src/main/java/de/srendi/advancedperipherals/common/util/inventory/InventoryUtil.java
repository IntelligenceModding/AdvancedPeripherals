package de.srendi.advancedperipherals.common.util.inventory;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.IPeripheralOwner;
import de.srendi.advancedperipherals.common.util.CoordUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class InventoryUtil {

    private InventoryUtil() {
    }

    public static IItemHandler extractHandler(@Nullable Object object, @Nullable Level level, @Nullable BlockPos pos, @Nullable Direction direction) {
        if (object instanceof IItemHandler itemHandler)
            return itemHandler;
        if (object instanceof Container container)
            return new InvWrapper(container);
        if (object instanceof BlockEntity blockEntity && level == null && pos == null) {
            pos = blockEntity.getBlockPos();
            level = blockEntity.getLevel();
        }
        if (level != null && pos != null) {
            return level.getCapability(Capabilities.ItemHandler.BLOCK, pos, direction != null ? direction : Direction.NORTH);
        }
        return null;
    }

    public static int moveItem(IItemHandler inventoryFrom, IItemHandler inventoryTo, ItemFilter filter) {
        if (inventoryFrom == null) return 0;

        int fromSlot = filter.getFromSlot();
        int toSlot = filter.getToSlot();

        int transferred = 0;

        // The logic changes when exporting from storage systems since these systems do not have slots
        if (inventoryFrom instanceof IStorageSystemItemHandler storageSystemHandler) {
            for (int i = toSlot == -1 ? 0 : toSlot; i < (toSlot == -1 ? inventoryTo.getSlots() : toSlot + 1); i++) {
                ItemStack existing = inventoryTo.getStackInSlot(i);
                ItemStack extracted;
                if (existing.isEmpty()) {
                    extracted = storageSystemHandler.extractItem(filter, filter.getCount() - transferred, true);
                }
                else { // If item already exists in slot, try to export same type of item
                    extracted = storageSystemHandler.extractItem(ItemFilter.fromStack(existing), filter.getCount() - transferred, true);
                    if (!filter.test(extracted))
                        extracted = ItemStack.EMPTY;
                }
                if (extracted.isEmpty())
                    continue;
                ItemStack remaining = inventoryTo.insertItem(i, extracted, false);
                transferred += storageSystemHandler.extractItem(filter, extracted.getCount() - remaining.getCount(), false).getCount();
                if (transferred >= filter.getCount())
                    break;
            }
            return transferred;
        }

        for (int i = fromSlot == -1 ? 0 : fromSlot; i < (fromSlot == -1 ? inventoryFrom.getSlots() : fromSlot + 1); i++) {
            if (filter.test(inventoryFrom.getStackInSlot(i))) {
                ItemStack extracted = inventoryFrom.extractItem(i, filter.getCount() - transferred, true);
                if (extracted.isEmpty())
                    continue;
                ItemStack remaining;
                if (toSlot == -1 && !(inventoryTo instanceof IStorageSystemItemHandler)) {
                    remaining = ItemHandlerHelper.insertItem(inventoryTo, extracted, false);
                } else {
                    remaining = inventoryTo.insertItem(toSlot, extracted, false); // toSlot is ignored for storage systems
                }
                transferred += inventoryFrom.extractItem(i, extracted.getCount() - remaining.getCount(), false).getCount();
                if (transferred >= filter.getCount())
                    break;
            }
        }
        return transferred;
    }

    @Nullable
    public static IItemHandler getHandlerFromName(@NotNull IComputerAccess access, String name) throws LuaException {
        IPeripheral location = access.getAvailablePeripheral(name);
        if (location == null)
            return null;

        return extractHandler(location.getTarget(), null, null, null);
    }

    @Nullable
    public static IItemHandler getHandlerFromDirection(@NotNull String direction, @NotNull IPeripheralOwner owner) throws LuaException {
        Level level = owner.getLevel();
        Objects.requireNonNull(level);
        Direction relativeDirection = CoordUtil.getDirection(owner.getOrientation(), direction);
        if (relativeDirection == null)
            return null;
        BlockEntity target = level.getBlockEntity(owner.getPos().relative(relativeDirection));
        if (target == null)
            return null;

        return extractHandler(target, level, target.getBlockPos(), relativeDirection.getOpposite());
    }
}
