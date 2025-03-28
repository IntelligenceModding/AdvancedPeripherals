package de.srendi.advancedperipherals.common.util.inventory;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.IPeripheralOwner;
import de.srendi.advancedperipherals.common.util.CoordUtil;
import de.srendi.advancedperipherals.common.util.StringUtil;
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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class ChemicalUtil {

    public static long moveChemical(IChemicalHandler inventoryFrom, IChemicalHandler inventoryTo, ChemicalFilter filter) {
        if (inventoryFrom == null) return 0;

        long amount = filter.getCount();
        long transferableAmount = 0;

        // The logic changes with storage systems since these systems do not have slots
        if (inventoryFrom instanceof IStorageSystemChemicalHandler storageSystemHandler) {
            ChemicalStack extracted = storageSystemHandler.extractChemical(filter, amount, Action.SIMULATE);
            ChemicalStack remaining = inventoryTo.insertChemical(extracted, Action.EXECUTE);

            transferableAmount += storageSystemHandler.extractChemical(filter, amount - remaining.getAmount(), Action.EXECUTE).getAmount();

            return transferableAmount;
        }

        if (inventoryTo instanceof IStorageSystemChemicalHandler storageSystemHandler) {
            if (filter.test(inventoryFrom.getChemicalInTank(0))) {
                ChemicalStack toExtract = inventoryFrom.getChemicalInTank(0).copy();
                toExtract.setAmount(amount);
                ChemicalStack extracted = inventoryFrom.extractChemical(toExtract, Action.SIMULATE);
                if (extracted.isEmpty())
                    return 0;
                long remaining = storageSystemHandler.insertChemical(extracted, Action.EXECUTE).getAmount();

                extracted.setAmount(extracted.getAmount() + remaining);
                transferableAmount += inventoryFrom.extractChemical(extracted, Action.EXECUTE).getAmount();
            }

            return transferableAmount;
        }

        return transferableAmount;
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
            throw new LuaException("Target '" + name + "' is not a fluid handler");
        return handler;
    }

    public static ChemicalStack toChemicalStack(Chemical chemical, long amount) {
        return new ChemicalStack(MekanismAPI.CHEMICAL_REGISTRY.wrapAsHolder(chemical), amount);
    }

    @NotNull
    public static String getFingerprint(@NotNull ChemicalStack stack) {
        // A pretty lame fingerprint, a chemical stack does not have any components or other stuff
        String fingerprint = getRegistryKey(stack).toString();
        try {
            byte[] bytesOfHash = fingerprint.getBytes(StandardCharsets.UTF_8);
            MessageDigest md = MessageDigest.getInstance("MD5");
            return StringUtil.toHexString(md.digest(bytesOfHash));
        } catch (NoSuchAlgorithmException ex) {
            AdvancedPeripherals.debug("Could not parse fingerprint", ex);
        }
        return "";
    }

    public static ResourceLocation getRegistryKey(Chemical fluid) {
        return MekanismAPI.CHEMICAL_REGISTRY.getKey(fluid);
    }

    public static ResourceLocation getRegistryKey(ChemicalStack fluid) {
        return MekanismAPI.CHEMICAL_REGISTRY.getKey(fluid.getChemical());
    }
}
