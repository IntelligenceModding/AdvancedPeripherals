package de.srendi.advancedperipherals.common.util;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public class InventoryUtil {

    private static final List<Pair<Predicate<Object>, Function<Object, IItemHandler>>> EXTRACTORS = new ArrayList<>();

    public static void registerExtractor(Predicate<Object> predicate, Function<Object, IItemHandler> handlerGenerator) {
        EXTRACTORS.add(Pair.of(predicate, handlerGenerator));
    }

    public static IItemHandler extractHandler(@Nullable Object object) {

        for (Pair<Predicate<Object>, Function<Object, IItemHandler>> extractor : EXTRACTORS) {
            if (extractor.getLeft().test(object))
                return extractor.getRight().apply(object);
        }

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

    public static int moveItem(IItemHandler from, int fromSlot, IItemHandler to, int toSlot, final int limit) {
        ItemStack extracted = from.extractItem(fromSlot, limit, true);
        if (extracted.isEmpty())
            return 0;
        int extractCount = Math.min(extracted.getCount(), limit);
        extracted.setCount(extractCount);

        ItemStack remainder = toSlot < 0 ? ItemHandlerHelper.insertItem(to, extracted, false) : to.insertItem(toSlot, extracted, false);
        int inserted = remainder.isEmpty() ? extractCount : extractCount - remainder.getCount();
        if (inserted <= 0)
            return 0;
        from.extractItem(fromSlot, inserted, false);
        return inserted;
    }

    public static MethodResult pushItems(@NotNull IArguments arguments, @NotNull IComputerAccess access, @NotNull IItemHandler source) throws LuaException {
        String toName = arguments.getString(0);
        int fromSlot = arguments.getInt(1);
        int limit = arguments.optInt(2, Integer.MAX_VALUE);
        int toSlot = arguments.optInt(3, -1);
        if (fromSlot < 1 || fromSlot > source.getSlots())
            return MethodResult.of(null, "From slot is incorrect");
        // Find location to transfer to
        IItemHandler to = getHandlerFromName(access, toName);
        if (toSlot != -1 && (toSlot < 1 || toSlot > to.getSlots()))
            return MethodResult.of(null, "To slot is incorrect");

        if (limit <= 0)
            return MethodResult.of(0);
        return MethodResult.of(InventoryUtil.moveItem(source, fromSlot - 1, to, toSlot - 1, limit));
    }

    public static MethodResult pullItems(@NotNull IArguments arguments, @NotNull IComputerAccess access, @NotNull IItemHandler source) throws LuaException {
        // Parsing arguments
        String fromName = arguments.getString(0);
        int fromSlot = arguments.getInt(1);
        int limit = arguments.optInt(2, Integer.MAX_VALUE);
        int toSlot = arguments.optInt(3, -1);
        if (toSlot != -1 && (toSlot < 1 || toSlot > source.getSlots()))
            return MethodResult.of(null, "To slot is incorrect");
        // Find location to transfer to

        IItemHandler from = getHandlerFromName(access, fromName);
        if (fromSlot < 1 || fromSlot > from.getSlots())
            return MethodResult.of(null, "From slot is incorrect");
        if (limit <= 0)
            return MethodResult.of(0);
        return MethodResult.of(InventoryUtil.moveItem(from, fromSlot - 1, source, toSlot - 1, limit));
    }

    public static @NotNull IItemHandler getHandlerFromName(@NotNull IComputerAccess access, String name) throws LuaException {
        IPeripheral location = access.getAvailablePeripheral(name);
        if (location == null)
            throw new LuaException("Target '" + name + "' does not exist");

        IItemHandler handler = extractHandler(location.getTarget());
        if (handler == null)
            throw new LuaException("Target '" + name + "' is not an inventory");
        return handler;
    }

    public static @NotNull IItemHandler getHandlerFromDirection(@NotNull String direction, @NotNull IPeripheralOwner owner) throws LuaException {
        Level level = owner.getLevel();
        Objects.requireNonNull(level);
        Direction relativeDirection = CoordUtil.getDirection(owner.getOrientation(), direction);
        BlockEntity target = level.getBlockEntity(owner.getPos().relative(relativeDirection));
        if (target == null)
            throw new LuaException("Target '" + direction + "' is empty or not an inventory");

        IItemHandler handler = extractHandler(target);
        if (handler == null)
            throw new LuaException("Target '" + direction + "' is not an inventory");
        return handler;
    }
}
