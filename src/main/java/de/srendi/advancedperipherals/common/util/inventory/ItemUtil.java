package de.srendi.advancedperipherals.common.util.inventory;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.peripheral.generic.GenericPeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class ItemUtil {

    private ItemUtil() {
    }

    @Nullable
    public static IItemHandler extractHandler(@Nullable IPeripheral peripheral) {
        if (peripheral == null) {
            return null;
        }
        Object target = peripheral.getTarget();
        if (target instanceof IItemHandler handler) {
            return handler;
        }
        if (target instanceof Container container) {
            return new InvWrapper(container);
        }
        if (target instanceof BlockEntity be) {
            Direction side = peripheral instanceof GenericPeripheral sided ? sided.side() : null;
            return be.getCapability(ForgeCapabilities.ITEM_HANDLER, side).orElse(null);
        }
        return null;
    }

    @Nullable
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
            BlockEntity be = level.getBlockEntity(pos);
            if (be != null) {
                return be.getCapability(ForgeCapabilities.ITEM_HANDLER, direction).orElse(null);
            }
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
            ? storageTo::insertItem
            : toSlot < 0
                ? (stack, simulate) -> ItemHandlerHelper.insertItem(inventoryTo, stack, simulate)
                : (stack, simulate) -> inventoryTo.insertItem(toSlot, stack, simulate);

        // The logic changes with storage systems since these systems do not have slots
        if (inventoryFrom instanceof IStorageSystemItemHandler storageFrom) {
            return storageFrom.extractItems(filter, (extracted) -> extracted.getCount() - inserter.insertItem(extracted, false).getCount(), false);
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
            ItemStack extracted, remaining;
            int inserted;

            extracted = inventoryFrom.extractItem(i, needs, true);
            if (extracted.isEmpty()) {
                continue;
            }
            remaining = inserter.insertItem(extracted, true);
            inserted = extracted.getCount() - remaining.getCount();
            if (inserted == 0) {
                continue;
            }

            extracted = inventoryFrom.extractItem(i, inserted, false);
            if (extracted.isEmpty()) {
                continue;
            }
            remaining = inserter.insertItem(extracted, false);
            inserted = extracted.getCount() - remaining.getCount();
            needs -= inserted;
            if (needs <= 0) {
                break;
            }
        }
        return filter.getCount() - needs;
    }

    @Nullable
    public static IItemHandler getHandlerFromDirection(@NotNull BlockEntityPeripheralOwner<?> owner, @NotNull Direction direction) {
        Level level = Objects.requireNonNull(owner.getLevel());
        BlockEntity target = level.getBlockEntity(owner.getPos().relative(direction));
        if (target == null) {
            return null;
        }
        return extractHandler(target, level, target.getBlockPos(), direction.getOpposite());
    }

    @FunctionalInterface
    private interface ItemInserter {
        ItemStack insertItem(ItemStack stack, boolean simulate);
    }

    //Gathers all items in handler and returns them
    public static List<ItemStack> getItemsFromItemHandler(IItemHandler handler) {
        List<ItemStack> items = new ArrayList<>(handler.getSlots());
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            items.add(handler.getStackInSlot(slot).copy());
        }

        return items;
    }

    public static ResourceLocation getRegistryKey(Item item) {
        return ForgeRegistries.ITEMS.getKey(item);
    }

    public static ResourceLocation getRegistryKey(ItemStack item) {
        return getRegistryKey(item.getItem());
    }
}
