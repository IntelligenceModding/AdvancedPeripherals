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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ChemicalUtil {

    public static long moveChemical(IChemicalHandler inventoryFrom, IChemicalHandler inventoryTo, ChemicalFilter filter) {
        if (inventoryFrom == null) return 0;

        int fromSlot = filter.getFromSlot();
        int toSlot = filter.getToSlot();

        long amount = filter.getCount();
        long transferred = 0;

        // The logic changes when exporting from storage systems since these systems do not have slots
        if (inventoryFrom instanceof IStorageSystemChemicalHandler storageSystemHandler) {
            for (int i = toSlot == -1 ? 0 : toSlot; i < (toSlot == -1 ? inventoryTo.getChemicalTanks() : toSlot + 1); i++) {
                ChemicalStack existing = inventoryTo.getChemicalInTank(i);
                ChemicalStack extracted;
                if (existing.isEmpty()) {
                    extracted = storageSystemHandler.extractChemical(filter, filter.getCount() - transferred, Action.SIMULATE);
                }
                else { // If chemical already exists in tank, try to export same type of chemical
                    extracted = storageSystemHandler.extractChemical(ChemicalFilter.fromStack(existing), filter.getCount() - transferred, Action.SIMULATE);
                    if (!filter.test(extracted))
                        extracted = ChemicalStack.EMPTY;
                }
                if (extracted.isEmpty())
                    continue;
                ChemicalStack remaining;
                if (toSlot == -1) { // Try to use this chemical handler's distribution
                    remaining = inventoryTo.insertChemical(extracted, Action.EXECUTE);
                }
                else {
                    remaining = inventoryTo.insertChemical(i, extracted, Action.EXECUTE);
                }
                transferred += storageSystemHandler.extractChemical(ChemicalFilter.fromStack(extracted), extracted.getAmount() - remaining.getAmount(), Action.EXECUTE).getAmount();
                if (transferred >= filter.getCount())
                    break;
            }
            return transferred;
        }

        for (int i = fromSlot == -1 ? 0 : fromSlot; i < (fromSlot == -1 ? inventoryFrom.getChemicalTanks() : fromSlot + 1); i++) {
            if (filter.test(inventoryFrom.getChemicalInTank(i))) {
                ChemicalStack extracted;
                if (fromSlot == -1) { // Try to use this chemical handler's distribution
                    ChemicalStack toExtract = inventoryFrom.getChemicalInTank(i).copyWithAmount(filter.getCount() - transferred);
                    extracted = inventoryFrom.extractChemical(toExtract, Action.SIMULATE);
                }
                else {
                    extracted = inventoryFrom.extractChemical(i, filter.getCount() - transferred, Action.SIMULATE);
                }
                if (extracted.isEmpty())
                    continue;

                ChemicalStack remaining;
                if (toSlot == -1 && !(inventoryTo instanceof IStorageSystemItemHandler)) {  // Try to use this chemical handler's distribution
                    remaining = inventoryTo.insertChemical(extracted, Action.EXECUTE);
                } else {
                    remaining = inventoryTo.insertChemical(toSlot, extracted, Action.EXECUTE); // toSlot is ignored for storage systems
                }

                if (fromSlot == -1) { // Try to use this chemical handler's distribution
                    extracted.setAmount(extracted.getAmount() - remaining.getAmount());
                    transferred += inventoryFrom.extractChemical(extracted, Action.EXECUTE).getAmount();
                }
                else {
                    transferred += inventoryFrom.extractChemical(i, filter.getCount() - remaining.getAmount(), Action.EXECUTE).getAmount();
                }
                if (transferred >= filter.getCount())
                    break;
            }
        }
        return transferred;

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
