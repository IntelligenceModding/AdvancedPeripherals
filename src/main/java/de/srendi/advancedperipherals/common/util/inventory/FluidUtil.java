package de.srendi.advancedperipherals.common.util.inventory;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.IPeripheralOwner;
import de.srendi.advancedperipherals.common.util.CoordUtil;
import de.srendi.advancedperipherals.common.util.FingerprintUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class FluidUtil {

    private FluidUtil() {
    }

    public static int moveFluid(IFluidHandler inventoryFrom, IFluidHandler inventoryTo, FluidFilter filter) {
        if (inventoryFrom == null) return 0;

        int amount = filter.getCount();
        int transferableAmount = 0;

        // The logic changes with storage systems since these systems do not have slots
        if (inventoryFrom instanceof IStorageSystemFluidHandler storageSystemHandler) {
            FluidStack extracted = storageSystemHandler.drain(filter, IFluidHandler.FluidAction.SIMULATE);
            int inserted = inventoryTo.fill(extracted, IFluidHandler.FluidAction.EXECUTE);

            transferableAmount += storageSystemHandler.drain(filter.setCount(inserted), IFluidHandler.FluidAction.EXECUTE).getAmount();

            return transferableAmount;
        }

        if (inventoryTo instanceof IStorageSystemFluidHandler storageSystemHandler) {
            if (filter.test(inventoryFrom.getFluidInTank(0))) {
                FluidStack toExtract = inventoryFrom.getFluidInTank(0).copy();
                toExtract.setAmount(amount);
                FluidStack extracted = inventoryFrom.drain(toExtract, IFluidHandler.FluidAction.SIMULATE);
                if (extracted.isEmpty())
                    return 0;
                int inserted = storageSystemHandler.fill(extracted, IFluidHandler.FluidAction.EXECUTE);

                extracted.setAmount(inserted);
                transferableAmount += inventoryFrom.drain(extracted, IFluidHandler.FluidAction.EXECUTE).getAmount();
            }

            return transferableAmount;
        }

        return transferableAmount;
    }

    public static IFluidHandler extractHandler(@Nullable Object object, @Nullable Level level, @Nullable BlockPos pos, @Nullable Direction direction) {
        if (object instanceof IFluidHandler itemHandler)
            return itemHandler;
        if (object instanceof BlockEntity blockEntity && level == null && pos == null) {
            pos = blockEntity.getBlockPos();
            level = blockEntity.getLevel();
        }
        if (level != null && pos != null) {
            return level.getCapability(Capabilities.FluidHandler.BLOCK, pos, direction != null ? direction : Direction.NORTH);
        }
        return null;
    }

    @Nullable
    public static IFluidHandler getHandlerFromDirection(@NotNull String direction, @NotNull IPeripheralOwner owner) throws LuaException {
        Level level = owner.getLevel();
        Objects.requireNonNull(level);
        Direction relativeDirection = CoordUtil.getDirection(owner.getOrientation(), direction);
        if (relativeDirection == null)
            return null;
        BlockEntity target = level.getBlockEntity(owner.getPos().relative(relativeDirection));
        if (target == null)
            return null;

        return extractHandler(target, level, owner.getPos().relative(relativeDirection), relativeDirection);
    }

    @Nullable
    public static IFluidHandler getHandlerFromName(@NotNull IComputerAccess access, String name) throws LuaException {
        IPeripheral location = access.getAvailablePeripheral(name);

        // Tanks/Block Entities can't be accessed if the bridge is not exposed to the same network as the target tank/block entity
        // This can occur when the bridge was wrapped via a side and not via modems
        if (location == null)
            return null;

        IFluidHandler handler = extractHandler(location.getTarget(), null, null, null);
        if (handler == null)
            throw new LuaException("Target '" + name + "' is not a fluid handler");
        return handler;
    }

    @NotNull
    public static String getFingerprint(@NotNull FluidStack stack) {
        FingerprintUtil.FingerprintKey fingerprintKey = new FingerprintUtil.FingerprintKey(getRegistryKey(stack), stack.getComponentsPatch().hashCode(), stack.getDisplayName().getString());

        return FingerprintUtil.hash(fingerprintKey);
    }

    public static ResourceLocation getRegistryKey(Fluid fluid) {
        return BuiltInRegistries.FLUID.getKey(fluid);
    }

    public static ResourceLocation getRegistryKey(FluidStack fluid) {
        return BuiltInRegistries.FLUID.getKey(fluid.copy().getFluid());
    }
}
