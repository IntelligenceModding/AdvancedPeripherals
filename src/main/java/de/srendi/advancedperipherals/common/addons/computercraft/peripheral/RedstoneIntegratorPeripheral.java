package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.blocks.blockentities.RedstoneIntegratorEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import net.minecraft.core.Direction;

public class RedstoneIntegratorPeripheral extends BasePeripheral<BlockEntityPeripheralOwner<RedstoneIntegratorEntity>> {

    public static final String PERIPHERAL_TYPE = "redstoneIntegrator";

    public RedstoneIntegratorPeripheral(RedstoneIntegratorEntity tileEntity) {
        super(PERIPHERAL_TYPE, new BlockEntityPeripheralOwner<>(tileEntity));
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.enableRedstoneIntegrator.get();
    }

    @LuaFunction(mainThread = true)
    public final boolean getInput(String direction) throws LuaException {
        Direction dir = validateSide(direction);
        return owner.tileEntity.getRedstoneInput(dir) > 0;
    }

    @LuaFunction(mainThread = true)
    public final boolean getOutput(String direction) throws LuaException {
        return owner.tileEntity.power[validateSide(direction).get3DDataValue()] > 0;
    }

    @LuaFunction(value = {"getAnalogueInput", "getAnalogInput"}, mainThread = true)
    public final int getAnalogInput(String direction) throws LuaException {
        Direction dir = validateSide(direction);
        return owner.tileEntity.getRedstoneInput(dir);
    }

    @LuaFunction(value = {"getAnalogueOutput", "getAnalogOutput"}, mainThread = true)
    public final int getAnalogOutput(String direction) throws LuaException {
        Direction dir = validateSide(direction);
        return owner.tileEntity.power[dir.get3DDataValue()];
    }

    @LuaFunction(mainThread = true)
    public final void setOutput(String direction, boolean power) throws LuaException {
        Direction dir = validateSide(direction);
        owner.tileEntity.setRedstoneOutput(dir, power ? 15 : 0);
    }

    @LuaFunction(value = {"setAnalogueOutput", "setAnalogOutput"}, mainThread = true)
    public final void setAnalogOutput(String direction, int power) throws LuaException {
        Direction dir = validateSide(direction);
        owner.tileEntity.setRedstoneOutput(dir, power);
    }
}
