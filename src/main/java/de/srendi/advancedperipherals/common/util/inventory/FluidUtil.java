package de.srendi.advancedperipherals.common.util.inventory;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.peripheral.generic.GenericPeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;
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
            FluidStack needsStack = stack.copy();
            needsStack.setAmount(needs);
            FluidStack extracted = inventoryFrom.drain(needsStack, IFluidHandler.FluidAction.SIMULATE);
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
            return be.getCapability(ForgeCapabilities.FLUID_HANDLER, side).orElse(null);
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
            BlockEntity be = level.getBlockEntity(pos);
            if (be != null) {
                return be.getCapability(ForgeCapabilities.FLUID_HANDLER, direction != null ? direction : Direction.NORTH).orElse(null);
            }
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

    public static ResourceLocation getRegistryKey(Fluid fluid) {
        return ForgeRegistries.FLUIDS.getKey(fluid);
    }

    public static ResourceLocation getRegistryKey(FluidStack fluid) {
        return getRegistryKey(fluid.getFluid());
    }
}
