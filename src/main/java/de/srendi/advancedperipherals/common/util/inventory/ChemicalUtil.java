package de.srendi.advancedperipherals.common.util.inventory;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.peripheral.generic.GenericPeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.IPeripheralOwner;
import de.srendi.advancedperipherals.common.util.FingerprintUtil;
import mekanism.api.Action;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.stream.IntStream;

public class ChemicalUtil {

    public static long moveChemical(IChemicalHandler inventoryFrom, IChemicalHandler inventoryTo, ChemicalFilter filter) {
        if (inventoryFrom == null) {
            return 0;
        }

        int fromSlot = filter.getFromSlot();
        int toSlot = filter.getToSlot();

        if (!(inventoryFrom instanceof IStorageSystemChemicalHandler) && fromSlot >= inventoryFrom.getChemicalTanks()) {
            return 0;
        }
        if (!(inventoryTo instanceof IStorageSystemChemicalHandler) && toSlot >= inventoryTo.getChemicalTanks()) {
            return 0;
        }

        long needs = filter.getAmount();
        if (needs <= 0) {
            return 0;
        }

        ChemicalInserter inserter = inventoryTo instanceof IStorageSystemChemicalHandler storageTo
            ? (stack) -> storageTo.insertChemical(stack, Action.EXECUTE)
            : toSlot < 0
                ? (stack) -> inventoryTo.insertChemical(stack, Action.EXECUTE)
                : (stack) -> inventoryTo.insertChemical(toSlot, stack, Action.EXECUTE);

        // The logic changes with storage systems since these systems do not have slots
        if (inventoryFrom instanceof IStorageSystemChemicalHandler storageFrom) {
            return storageFrom.extractChemicals(
                filter,
                (extracted) -> extracted.getAmount() - inserter.insertChemical(extracted).getAmount(),
                Action.EXECUTE
            );
        }

        int[] fromSlots = (
            fromSlot >= 0
                ? IntStream.of(fromSlot)
                : IntStream.range(0, inventoryFrom.getChemicalTanks())
        )
            .filter((i) -> filter.test(inventoryFrom.getChemicalInTank(i)))
            .toArray();
        if (fromSlots.length == 0) {
            return 0;
        }

        for (int i : fromSlots) {
            ChemicalStack extracted = inventoryFrom.extractChemical(i, needs, Action.SIMULATE);
            if (extracted.isEmpty()) {
                continue;
            }
            ChemicalStack remaining = inserter.insertChemical(extracted);
            long inserted = extracted.getAmount() - remaining.getAmount();
            if (inserted == 0) {
                continue;
            }
            needs -= inserted;
            inventoryFrom.extractChemical(i, inserted, Action.EXECUTE);
            if (needs <= 0) {
                break;
            }
        }
        return filter.getAmount() - needs;
    }

    @Nullable
    public static IChemicalHandler extractHandler(@Nullable IPeripheral peripheral) {
        if (peripheral == null) {
            return null;
        }
        Object target = peripheral.getTarget();
        if (target instanceof IChemicalHandler handler) {
            return handler;
        }
        if (target instanceof BlockEntity be) {
            Direction side = peripheral instanceof GenericPeripheral sided ? sided.side() : null;
            return be.getLevel().getCapability(Capabilities.CHEMICAL.block(), be.getBlockPos(), side);
        }
        return null;
    }

    public static IChemicalHandler extractHandler(@Nullable Object object, @Nullable Level level, @Nullable BlockPos pos, @Nullable Direction direction) {
        if (object instanceof IChemicalHandler handler) {
            return handler;
        }
        if (object instanceof BlockEntity blockEntity && level == null && pos == null) {
            pos = blockEntity.getBlockPos();
            level = blockEntity.getLevel();
        }
        if (level != null && pos != null) {
            return level.getCapability(Capabilities.CHEMICAL.block(), pos, direction != null ? direction : Direction.NORTH);
        }
        return null;
    }

    @Nullable
    public static IChemicalHandler getHandlerFromDirection(@NotNull IPeripheralOwner owner, @NotNull Direction direction) {
        Level level = Objects.requireNonNull(owner.getLevel());
        BlockEntity target = level.getBlockEntity(owner.getPos().relative(direction));
        if (target == null) {
            return null;
        }
        return extractHandler(target, level, target.getBlockPos(), direction.getOpposite());
    }

    public static ChemicalStack toChemicalStack(Chemical chemical, long amount) {
        return new ChemicalStack(MekanismAPI.CHEMICAL_REGISTRY.wrapAsHolder(chemical), amount);
    }

    public static String getFingerprint(@NotNull ChemicalStack stack) {
        // A pretty lame fingerprint, a chemical stack does not have any components or other stuff
        FingerprintUtil.FingerprintKey fingerprintKey = new FingerprintUtil.FingerprintKey(getRegistryKey(stack), 0);

        return FingerprintUtil.hash(fingerprintKey);
    }

    public static ResourceLocation getRegistryKey(Chemical chemical) {
        return MekanismAPI.CHEMICAL_REGISTRY.getKey(chemical);
    }

    public static ResourceLocation getRegistryKey(ChemicalStack chemicalStack) {
        return MekanismAPI.CHEMICAL_REGISTRY.getKey(chemicalStack.getChemical());
    }

    @FunctionalInterface
    private interface ChemicalInserter {
        ChemicalStack insertChemical(ChemicalStack stack);
    }
}
