package de.srendi.advancedperipherals.common.util.inventory;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.peripheral.generic.GenericPeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
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
        if (inventoryFrom == null) {
            return 0;
        }

        int needs = filter.getAmount();
        if (needs <= 0) {
            return 0;
        }

        // The logic changes with storage systems since these systems do not have slots
        if (inventoryFrom instanceof IStorageSystemFluidHandler storageFrom) {
            return storageFrom.extractFluids(
                filter,
                (extracted) -> inventoryTo.fill(extracted, IFluidHandler.FluidAction.EXECUTE),
                IFluidHandler.FluidAction.EXECUTE
            );
        }

        for (int i = 0; i < inventoryFrom.getTanks() && needs >= 0; i++) {
            FluidStack stack = inventoryFrom.getFluidInTank(i);
            if (!filter.test(stack)) {
                continue;
            }
            FluidStack extracted = inventoryFrom.drain(stack.copyWithAmount(needs), IFluidHandler.FluidAction.SIMULATE);
            if (extracted.isEmpty()) {
                continue;
            }
            int inserted = inventoryTo.fill(extracted, IFluidHandler.FluidAction.EXECUTE);
            if (inserted == 0) {
                continue;
            }
            needs -= inserted;
            extracted.setAmount(inserted);
            inventoryFrom.drain(extracted, IFluidHandler.FluidAction.EXECUTE);
        }

        return filter.getAmount() - needs;
    }

    @Nullable
    public static IFluidHandler extractHandler(@Nullable IPeripheral peripheral) {
        if (peripheral == null) {
            return null;
        }
        Object target = peripheral.getTarget();
        if (target instanceof IFluidHandler handler) {
            return handler;
        }
        if (target instanceof BlockEntity be) {
            Direction side = peripheral instanceof GenericPeripheral sided ? sided.side() : null;
            return be.getLevel().getCapability(Capabilities.FluidHandler.BLOCK, be.getBlockPos(), side);
        }
        return null;
    }

    public static IFluidHandler extractHandler(@Nullable Object object, @Nullable Level level, @Nullable BlockPos pos, @Nullable Direction direction) {
        if (object instanceof IFluidHandler handler) {
            return handler;
        }
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
    public static IFluidHandler getHandlerFromDirection(@NotNull BlockEntityPeripheralOwner<?> owner, @NotNull Direction direction) {
        Level level = Objects.requireNonNull(owner.getLevel());
        BlockEntity target = level.getBlockEntity(owner.getPos().relative(direction));
        if (target == null) {
            return null;
        }
        return extractHandler(target, level, target.getBlockPos(), direction.getOpposite());
    }

    @NotNull
    public static String getFingerprint(@NotNull FluidStack stack) {
        FingerprintUtil.FingerprintKey fingerprintKey = new FingerprintUtil.FingerprintKey(getRegistryKey(stack), stack.getComponentsPatch().hashCode());

        return FingerprintUtil.hash(fingerprintKey);
    }

    public static ResourceLocation getRegistryKey(Fluid fluid) {
        return BuiltInRegistries.FLUID.getKey(fluid);
    }

    public static ResourceLocation getRegistryKey(FluidStack fluid) {
        return BuiltInRegistries.FLUID.getKey(fluid.getFluid());
    }
}
