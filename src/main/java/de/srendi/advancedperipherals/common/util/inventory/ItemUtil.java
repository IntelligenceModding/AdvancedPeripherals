package de.srendi.advancedperipherals.common.util.inventory;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.peripheral.generic.GenericPeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.util.FingerprintUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
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
            return be.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, be.getBlockPos(), side);
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
            return level.getCapability(Capabilities.ItemHandler.BLOCK, pos, direction);
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
        ItemStack insertItem(ItemStack stack);
    }

    /**
     * Fingerprints are XXHash64 hashes generated out of the nbt tag, the registry name and the display name from item stacks
     * Used to filter inventory specific operations. See {@link ItemFilter}
     *
     * @return A generated XXHash64 hash from the item stack
     */
    public static String getFingerprint(ItemStack stack) {
        FingerprintUtil.FingerprintKey fingerprintKey = new FingerprintUtil.FingerprintKey(getRegistryKey(stack), stack.getComponentsPatch().hashCode());

        return FingerprintUtil.hash(fingerprintKey);
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
        return BuiltInRegistries.ITEM.getKey(item);
    }

    public static ResourceLocation getRegistryKey(ItemStack item) {
        return BuiltInRegistries.ITEM.getKey(item.copy().getItem());
    }
}
