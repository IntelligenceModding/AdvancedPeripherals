package de.srendi.advancedperipherals.common.util.inventory;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.IPeripheralOwner;
import de.srendi.advancedperipherals.common.util.CoordUtil;
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
        long needs = filter.getAmount();
        if (needs <= 0) {
            return 0;
        }

        // The logic changes with storage systems since these systems do not have slots
        if (inventoryFrom instanceof IStorageSystemChemicalHandler storageFrom) {
            ChemicalStack extracted = storageFrom.extractChemical(filter, Action.SIMULATE);
            ChemicalStack remaining = toSlot <= 0
                ? inventoryTo.insertChemical(extracted, Action.EXECUTE)
                : inventoryTo.insertChemical(toSlot, extracted, Action.EXECUTE);
            long inserted = extracted.getAmount() - remaining.getAmount();
            if (inserted == 0) {
                return 0;
            }
            needs -= inserted;
            extracted.setAmount(inserted);
            storageFrom.extractChemical(ChemicalFilter.fromStack(extracted), Action.EXECUTE);
            return filter.getAmount() - needs;
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
            ChemicalStack remaining = toSlot <= 0
                ? inventoryTo.insertChemical(extracted, Action.EXECUTE)
                : inventoryTo.insertChemical(toSlot, extracted, Action.EXECUTE);
            long inserted = extracted.getAmount() - remaining.getAmount();
            needs -= inserted;
            inventoryFrom.extractChemical(i, inserted, Action.SIMULATE);
        }
        return filter.getAmount() - needs;
    }

    public static IChemicalHandler extractHandler(@Nullable Object object, @Nullable Level level, @Nullable BlockPos pos, @Nullable Direction direction) {
        if (object instanceof IChemicalHandler itemHandler)
            return itemHandler;
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
    public static IChemicalHandler getHandlerFromDirection(@NotNull String direction, @NotNull IPeripheralOwner owner) throws LuaException {
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
    public static IChemicalHandler getHandlerFromName(@NotNull IComputerAccess access, String name) throws LuaException {
        IPeripheral location = access.getAvailablePeripheral(name);

        // Tanks/Block Entities can't be accessed if the bridge is not exposed to the same network as the target tank/block entity
        // This can occur when the bridge was wrapped via a side and not via modems
        if (location == null)
            return null;

        IChemicalHandler handler = extractHandler(location.getTarget(), null, null, null);
        if (handler == null)
            throw new LuaException("Target '" + name + "' is not a chemical handler");
        return handler;
    }

    public static ChemicalStack toChemicalStack(Chemical chemical, long amount) {
        return new ChemicalStack(MekanismAPI.CHEMICAL_REGISTRY.wrapAsHolder(chemical), amount);
    }

    public static String getFingerprint(@NotNull ChemicalStack stack) {
        // A pretty lame fingerprint, a chemical stack does not have any components or other stuff
        FingerprintUtil.FingerprintKey fingerprintKey = new FingerprintUtil.FingerprintKey(getRegistryKey(stack), null, null);

        return FingerprintUtil.hash(fingerprintKey);
    }

    public static ResourceLocation getRegistryKey(Chemical chemical) {
        return MekanismAPI.CHEMICAL_REGISTRY.getKey(chemical);
    }

    public static ResourceLocation getRegistryKey(ChemicalStack chemicalStack) {
        return MekanismAPI.CHEMICAL_REGISTRY.getKey(chemicalStack.getChemical());
    }
}
