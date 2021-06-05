package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.core.computer.ComputerSide;
import de.srendi.advancedperipherals.common.addons.computercraft.base.BasePeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.blocks.tileentity.RedstoneIntegratorTileEntity;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import net.minecraft.util.Direction;

import java.util.Locale;

public class RedstoneIntegratorPeripheral extends BasePeripheral {

    public RedstoneIntegratorPeripheral(String type, PeripheralTileEntity<?> tileEntity) {
        super(type, tileEntity);
    }

    @Override
    public boolean isEnabled() {
        return AdvancedPeripheralsConfig.enableRedstoneIntegrator;
    }

    @LuaFunction(mainThread = true)
    public final boolean getInput(String direction) throws LuaException {
        Direction dir = validateSide(direction);
        return getTileEntity().getRedstoneInput(dir) > 0;
    }

    @LuaFunction(mainThread = true)
    public final boolean getOutput(String direction) throws LuaException {
        return getTileEntity().power[validateSide(direction).get3DDataValue()] > 0;
    }

    @LuaFunction(value = {"getAnalogueInput", "getAnalogInput"}, mainThread = true)
    public final int getAnalogInput(String direction) throws LuaException {
        Direction dir = validateSide(direction);
        return getTileEntity().getRedstoneInput(dir);
    }

    @LuaFunction(value = {"getAnalogueOutput", "getAnalogOutput"}, mainThread = true)
    public final int getAnalogOutput(String direction) throws LuaException {
        Direction dir = validateSide(direction);
        return getTileEntity().power[validateSide(direction).get3DDataValue()];
    }

    @LuaFunction(mainThread = true)
    public final void setOutput(String direction, boolean power) throws LuaException {
        Direction dir = validateSide(direction);
        getTileEntity().setRedstoneOutput(dir, power ? 15 : 0);
    }

    @LuaFunction(value = {"setAnalogueOutput", "setAnalogOutput"}, mainThread = true)
    public final void setAnalogOutput(String direction, int power) throws LuaException {
        Direction dir = validateSide(direction);
        getTileEntity().setRedstoneOutput(dir, power);
    }

    private RedstoneIntegratorTileEntity getTileEntity() {
        return (RedstoneIntegratorTileEntity) tileEntity;
    }

    private Direction validateSide(String direction) throws LuaException {
        ComputerSide dir;
        try {
            dir = ComputerSide.valueOf(direction.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new LuaException(direction + " is not a valid side.");
        }
        return getTileEntity().getDirecton(dir);
    }
}
