package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.addons.computercraft.base.BasePeripheral;
import de.srendi.advancedperipherals.common.blocks.tileentity.RedstoneIntegratorTile;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import net.minecraft.util.Direction;

public class RedstoneIntegratorPeripheral extends BasePeripheral {

    public static final String TYPE = "redstoneIntegrator";

    private final RedstoneIntegratorTile tile;

    public RedstoneIntegratorPeripheral(RedstoneIntegratorTile tileEntity) {
        super(TYPE, tileEntity);
        this.tile = tileEntity;
    }

    @Override
    public boolean isEnabled() {
        return AdvancedPeripheralsConfig.enableRedstoneIntegrator;
    }

    @LuaFunction(mainThread = true)
    public final boolean getInput(String direction) throws LuaException {
        Direction dir = validateSide(direction);
        return tile.getRedstoneInput(dir) > 0;
    }

    @LuaFunction(mainThread = true)
    public final boolean getOutput(String direction) throws LuaException {
        return tile.power[validateSide(direction).get3DDataValue()] > 0;
    }

    @LuaFunction(value = {"getAnalogueInput", "getAnalogInput"}, mainThread = true)
    public final int getAnalogInput(String direction) throws LuaException {
        Direction dir = validateSide(direction);
        return tile.getRedstoneInput(dir);
    }

    @LuaFunction(value = {"getAnalogueOutput", "getAnalogOutput"}, mainThread = true)
    public final int getAnalogOutput(String direction) throws LuaException {
        Direction dir = validateSide(direction);
        return tile.power[validateSide(direction).get3DDataValue()];
    }

    @LuaFunction(mainThread = true)
    public final void setOutput(String direction, boolean power) throws LuaException {
        Direction dir = validateSide(direction);
        tile.setRedstoneOutput(dir, power ? 15 : 0);
    }

    @LuaFunction(value = {"setAnalogueOutput", "setAnalogOutput"}, mainThread = true)
    public final void setAnalogOutput(String direction, int power) throws LuaException {
        Direction dir = validateSide(direction);
        tile.setRedstoneOutput(dir, power);
    }
}
