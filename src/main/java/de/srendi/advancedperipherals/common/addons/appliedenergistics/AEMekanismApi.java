package de.srendi.advancedperipherals.common.addons.appliedenergistics;

import appeng.api.storage.MEStorage;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import de.srendi.advancedperipherals.common.addons.computercraft.peripheral.MEBridgePeripheral;
import de.srendi.advancedperipherals.common.blocks.blockentities.MEBridgeEntity;
import de.srendi.advancedperipherals.common.util.Pair;
import de.srendi.advancedperipherals.common.util.StatusConstants;
import de.srendi.advancedperipherals.common.util.inventory.ChemicalFilter;
import de.srendi.advancedperipherals.common.util.inventory.ChemicalUtil;
import mekanism.api.chemical.IChemicalHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Offloading mekanism related ME Bridge functions to prevent class loading errors at runtime
 */
public class AEMekanismApi {

    /**
     * imports a fluid to the system from a valid tank
     *
     * @param arguments  the arguments given by the computer
     * @param computer   the computer connected to the peripheral - used for peripheral attached inventories
     * @param peripheral the ME Bridge peripheral
     * @return the imported amount or null with a string if something went wrong
     */
    public static MethodResult importToME(@NotNull IArguments arguments, IComputerAccess computer, MEBridgePeripheral peripheral) throws LuaException {
        MEBridgeEntity bridge = peripheral.getBridge();
        MEStorage monitor = AEApi.getMonitor(bridge.getActionableNode());
        Pair<ChemicalFilter, String> filter = ChemicalFilter.parse(arguments.getTable(0));

        if (filter.rightPresent())
            return MethodResult.of(0, filter.getRight());

        String side = arguments.getString(1);
        IChemicalHandler targetTank = ChemicalUtil.getHandlerFromDirection(side, peripheral.getPeripheralOwner());

        if (targetTank == null) {
            targetTank = ChemicalUtil.getHandlerFromName(computer, side);
        }

        if (targetTank == null)
            return MethodResult.of(0, StatusConstants.INVENTORY_NOT_FOUND.name());

        MEChemicalHandler chemicalHandler = new MEChemicalHandler(monitor, bridge);

        return MethodResult.of(ChemicalUtil.moveChemical(targetTank, chemicalHandler, filter.getLeft()));
    }

    /**
     * imports a fluid to the system from a valid tank
     *
     * @param arguments  the arguments given by the computer
     * @param computer   the computer connected to the peripheral - used for peripheral attached inventories
     * @param peripheral the ME Bridge peripheral
     * @return the exportable amount or null with a string if something went wrong
     */
    public static MethodResult exportToTank(@NotNull IArguments arguments, IComputerAccess computer, MEBridgePeripheral peripheral) throws LuaException {
        MEBridgeEntity bridge = peripheral.getBridge();
        MEStorage monitor = AEApi.getMonitor(bridge.getActionableNode());
        Pair<ChemicalFilter, String> filter = ChemicalFilter.parse(arguments.getTable(0));

        if (filter.rightPresent())
            return MethodResult.of(0, filter.getRight());

        String side = arguments.getString(1);
        IChemicalHandler targetTank = ChemicalUtil.getHandlerFromDirection(side, peripheral.getPeripheralOwner());

        if (targetTank == null) {
            targetTank = ChemicalUtil.getHandlerFromName(computer, side);
        }

        if (targetTank == null)
            return MethodResult.of(0, StatusConstants.INVENTORY_NOT_FOUND.name());

        MEChemicalHandler chemicalHandler = new MEChemicalHandler(monitor, bridge);

        return MethodResult.of(ChemicalUtil.moveChemical(chemicalHandler, targetTank, filter.getLeft()));
    }

}
