package de.srendi.advancedperipherals.common.util;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
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
import java.util.function.Function;
import java.util.function.Predicate;

public class InventoryUtil {

    private static final List<Pair<Predicate<Object>, Function<Object, IItemHandler>>> EXTRACTORS = new ArrayList<>();

    public static void registerExtractor(Predicate<Object> predicate, Function<Object, IItemHandler> handlerGenerator) {
        EXTRACTORS.add(Pair.of(predicate, handlerGenerator));
    }

    public static IItemHandler extractHandler(@Nullable Object object) {

        for (Pair<Predicate<Object>, Function<Object, IItemHandler>> extractor: EXTRACTORS) {
            if (extractor.getLeft().test(object))
                return extractor.getRight().apply(object);
        }

        if (object instanceof ICapabilityProvider) {
            LazyOptional<IItemHandler> cap = ((ICapabilityProvider) object).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
            if (cap.isPresent()) return cap.orElseThrow(NullPointerException::new);
        }
        if (object instanceof IItemHandler)
            return (IItemHandler) object;
        if (object instanceof Container)
            return new InvWrapper((Container) object);
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
        IPeripheral location = access.getAvailablePeripheral(toName);
        if (location == null) throw new LuaException("Target '" + toName + "' does not exist");

        IItemHandler to = extractHandler(location.getTarget());
        if (to == null) throw new LuaException("Target '" + toName + "' is not an inventory");
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
        IPeripheral location = access.getAvailablePeripheral(fromName);
        if (location == null) throw new LuaException("Target '" + fromName + "' does not exist");

        IItemHandler from = extractHandler(location.getTarget());
        if (from == null) throw new LuaException("Target '" + fromName + "' is not an inventory");
        if (fromSlot < 1 || fromSlot > from.getSlots())
            return MethodResult.of(null, "From slot is incorrect");
        if (limit <= 0)
            return MethodResult.of(0);
        return MethodResult.of(InventoryUtil.moveItem(from, fromSlot - 1, source, toSlot - 1, limit));
    }
}
