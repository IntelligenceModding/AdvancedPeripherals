package de.srendi.advancedperipherals.common.util;

import appeng.api.storage.MEStorage;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.core.apis.TableHelper;
import de.srendi.advancedperipherals.common.addons.appliedenergistics.AppEngApi;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.IPeripheralOwner;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;

public class FluidUtil {
    public static IFluidHandler extractHandler(@Nullable Object object) {
        if (object instanceof IFluidHandler fluidHandler)
            return fluidHandler;

        if (object instanceof ICapabilityProvider capabilityProvider) {
            LazyOptional<IFluidHandler> cap = capabilityProvider.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
            if (cap.isPresent())
                return cap.orElseThrow(NullPointerException::new);
        }
        return null;
    }

    public static @NotNull IFluidHandler getHandlerFromDirection(@NotNull String direction, @NotNull IPeripheralOwner owner) throws LuaException {
        Level level = owner.getLevel();
        Objects.requireNonNull(level);
        Direction relativeDirection = CoordUtil.getDirection(owner.getOrientation(), direction);
        BlockEntity target = level.getBlockEntity(owner.getPos().relative(relativeDirection));
        if (target == null)
            throw new LuaException("Target '" + direction + "' is empty or not a fluid handler");

        IFluidHandler handler = extractHandler(target);
        if (handler == null)
            throw new LuaException("Target '" + direction + "' is not a fluid handler");
        return handler;
    }

    public static @NotNull IFluidHandler getHandlerFromName(@NotNull IComputerAccess access, String name) throws LuaException {
        IPeripheral location = access.getAvailablePeripheral(name);
        if (location == null)
            throw new LuaException("Target '" + name + "' does not exist");

        IFluidHandler handler = extractHandler(location.getTarget());
        if (handler == null)
            throw new LuaException("Target '" + name + "' is not a fluid handler");
        return handler;
    }

    public static FluidStack getFluidStack(Map<?, ?> table, MEStorage monitor) throws LuaException {
        if (table == null || table.isEmpty()) return FluidStack.EMPTY;

        if (table.containsKey("fingerprint")) {
            FluidStack fingerprint = AppEngApi.findMatchingFluidFingerprint(TableHelper.getStringField(table, "fingerprint"), monitor);
            if (table.containsKey("amount")) fingerprint.setAmount(TableHelper.getIntField(table, "amount"));
            return fingerprint;
        }

        if (!table.containsKey("name")) return FluidStack.EMPTY;

        String name = TableHelper.getStringField(table, "name");

        Fluid fluid = getRegistryEntry(name, ForgeRegistries.FLUIDS);

        FluidStack stack = new FluidStack(fluid, 1);

        if (table.containsKey("amount")) stack.setAmount(TableHelper.getIntField(table, "amount"));

        if (table.containsKey("nbt") || table.containsKey("json") || table.containsKey("tag"))
            stack.setTag(getTag(stack, table, monitor));

        return stack;
    }

    public static <T extends ForgeRegistryEntry<T>> T getRegistryEntry(String name, IForgeRegistry<T> forgeRegistry) {
        ResourceLocation location;
        try {
            location = new ResourceLocation(name);
        } catch (ResourceLocationException ex) {
            location = null;
        }

        T value;
        if (location != null && forgeRegistry.containsKey(location) && (value = forgeRegistry.getValue(location)) != null) {
            return value;
        } else {
            return null;
        }
    }

    private static CompoundTag getTag(FluidStack stack, Map<?, ?> table, MEStorage monitor) throws LuaException {
        CompoundTag nbt = NBTUtil.fromText(TableHelper.optStringField(table, "json", null));
        if (nbt == null) {
            nbt = NBTUtil.fromBinary(TableHelper.optStringField(table, "tag", null));
            if (nbt == null) {
                nbt = parseNBTHash(stack, table, monitor);
            }
        }
        return nbt;
    }

    private static CompoundTag parseNBTHash(FluidStack stack, Map<?, ?> table, MEStorage monitor) throws LuaException {
        String nbt = TableHelper.optStringField(table, "nbt", null);
        if (nbt == null || nbt.isEmpty()) return null;
        CompoundTag tag = AppEngApi.findMatchingTag(stack, nbt, monitor);
        if (tag != null) return tag;

        tag = new CompoundTag();
        tag.put("_apPlaceholder_", IntTag.valueOf(1));
        return tag;
    }
}
