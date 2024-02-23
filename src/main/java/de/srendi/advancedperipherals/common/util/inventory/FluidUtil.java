package de.srendi.advancedperipherals.common.util.inventory;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.IPeripheralOwner;
import de.srendi.advancedperipherals.common.util.CoordUtil;
import de.srendi.advancedperipherals.common.util.StringUtil;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class FluidUtil {

    private FluidUtil() {
    }

    @Nullable
    public static IFluidHandler extractHandler(@Nullable Object object) {
        if (object instanceof IFluidHandler fluidHandler)
            return fluidHandler;

        if (object instanceof ICapabilityProvider capabilityProvider) {
            LazyOptional<IFluidHandler> cap = capabilityProvider.getCapability(ForgeCapabilities.FLUID_HANDLER);
            if (cap.isPresent())
                return cap.orElseThrow(NullPointerException::new);
        }
        return null;
    }

    @NotNull
    public static IFluidHandler getHandlerFromDirection(@NotNull String direction, @NotNull IPeripheralOwner owner) throws LuaException {
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

    @Nullable
    public static IFluidHandler getHandlerFromName(@NotNull IComputerAccess access, String name) throws LuaException {
        IPeripheral location = access.getAvailablePeripheral(name);

        // Tanks/Block Entities can't be accessed if the bridge is not exposed to the same network as the target tank/block entity
        // This can occur when the bridge was wrapped via a side and not via modems
        if (location == null)
            return null;

        IFluidHandler handler = extractHandler(location.getTarget());
        if (handler == null)
            throw new LuaException("Target '" + name + "' is not a fluid handler");
        return handler;
    }

    @NotNull
    public static String getFingerprint(@NotNull FluidStack stack) {
        String fingerprint = stack.getOrCreateTag() + getRegistryKey(stack).toString() + stack.getDisplayName().getString();
        try {
            byte[] bytesOfHash = fingerprint.getBytes(StandardCharsets.UTF_8);
            MessageDigest md = MessageDigest.getInstance("MD5");
            return StringUtil.toHexString(md.digest(bytesOfHash));
        } catch (NoSuchAlgorithmException ex) {
            AdvancedPeripherals.debug("Could not parse fingerprint.", org.apache.logging.log4j.Level.ERROR);
            ex.printStackTrace();
        }
        return "";
    }

    public static ResourceLocation getRegistryKey(Fluid fluid) {
        return ForgeRegistries.FLUIDS.getKey(fluid);
    }

    public static ResourceLocation getRegistryKey(FluidStack fluid) {
        return ForgeRegistries.FLUIDS.getKey(fluid.copy().getFluid());
    }
}
