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
import java.util.stream.IntStream;

public class InventoryUtil {

    private InventoryUtil() {
    }

    public static IItemHandler extractHandler(@Nullable Object object, @Nullable Level level, @Nullable BlockPos pos, @Nullable Direction direction) {
        if (object instanceof IItemHandler itemHandler) {
            return itemHandler;
        }
        if (object instanceof Container container) {
            return new InvWrapper(container);
        }
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
        if (inventoryFrom == null) {
            return 0;
        }

        int fromSlot = filter.getFromSlot();
        int toSlot = filter.getToSlot();

        if (!(inventoryFrom instanceof IStorageSystemItemHandler) && fromSlot >= inventoryFrom.getSlots()) {
            return 0;
        }
        if (!(inventoryTo instanceof IStorageSystemItemHandler) && toSlot >= inventoryTo.getSlots()) {
            return 0;
        }

        int needs = filter.getCount();
        if (needs <= 0) {
            return 0;
        }

        ItemInserter inserter = inventoryTo instanceof IStorageSystemItemHandler storageTo
            ? (stack) -> storageTo.insertItem(stack, false)
            : toSlot < 0
                ? (stack) -> ItemHandlerHelper.insertItem(inventoryTo, stack, false)
                : (stack) -> inventoryTo.insertItem(toSlot, stack, false);

        // The logic changes with storage systems since these systems do not have slots
        if (inventoryFrom instanceof IStorageSystemItemHandler storageFrom) {
            return storageFrom.extractItems(filter, (extracted) -> extracted.getCount() - inserter.insertItem(extracted).getCount(), false);
        }

        int[] fromSlots = (
            fromSlot >= 0
                ? IntStream.of(fromSlot)
                : IntStream.range(0, inventoryFrom.getSlots())
        )
            .filter((i) -> filter.test(inventoryFrom.getStackInSlot(i)))
            .toArray();
        if (fromSlots.length == 0) {
            return 0;
        }

        for (int i : fromSlots) {
            ItemStack extracted = inventoryFrom.extractItem(i, needs, true);
            if (extracted.isEmpty()) {
                continue;
            }
            ItemStack remaining = inserter.insertItem(extracted);
            int inserted = extracted.getCount() - remaining.getCount();
            if (inserted == 0) {
                continue;
            }
            needs -= inserted;
            inventoryFrom.extractItem(i, inserted, false);
            if (needs <= 0) {
                break;
            }
        }
        return filter.getCount() - needs;
    }

    @Nullable
    public static IItemHandler getHandlerFromName(@NotNull IComputerAccess access, String name) throws LuaException {
        IPeripheral location = access.getAvailablePeripheral(name);
        if (location == null) {
            return null;
        }
        return extractHandler(location.getTarget(), null, null, null);
    }

    @Nullable
    public static IItemHandler getHandlerFromDirection(@NotNull String direction, @NotNull IPeripheralOwner owner) throws LuaException {
        Level level = Objects.requireNonNull(owner.getLevel());
        Direction relativeDirection = CoordUtil.getDirection(owner.getOrientation(), direction);
        if (relativeDirection == null) {
            return null;
        }
        BlockEntity target = level.getBlockEntity(owner.getPos().relative(relativeDirection));
        if (target == null) {
            return null;
        }
        return extractHandler(target, level, target.getBlockPos(), relativeDirection.getOpposite());
    }

    @FunctionalInterface
    private interface ItemInserter {
        ItemStack insertItem(ItemStack stack);
    }
}
