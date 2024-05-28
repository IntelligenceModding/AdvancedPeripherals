package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.addons.computercraft.owner.BlockEntityPeripheralOwner;
import de.srendi.advancedperipherals.common.blocks.blockentities.RedstoneIntegratorEntity;
import de.srendi.advancedperipherals.common.configuration.APConfig;
import de.srendi.advancedperipherals.lib.peripherals.BasePeripheral;
import net.minecraft.core.Direction;

public class RedstoneIntegratorPeripheral extends BasePeripheral<BlockEntityPeripheralOwner<RedstoneIntegratorEntity>> {

    public static final String PERIPHERAL_TYPE = "redstone_integrator";

    public RedstoneIntegratorPeripheral(RedstoneIntegratorEntity tileEntity) {
        super(PERIPHERAL_TYPE, new BlockEntityPeripheralOwner<>(tileEntity));
    }

    @Override
    public boolean isEnabled() {
        return APConfig.PERIPHERALS_CONFIG.enableRedstoneIntegrator.get();
    }

    @LuaFunction
    public final boolean getInput(String direction) throws LuaException {
        Direction dir = validateSide(direction);
        return owner.tileEntity.getInput(dir) > 0;
    }

    @LuaFunction
    public final boolean getOutput(String direction) throws LuaException {
        Direction dir = validateSide(direction);
        return owner.tileEntity.getOutput(dir) > 0;
    }

    @LuaFunction(value = {"getAnalogueInput", "getAnalogInput"})
    public final int getAnalogInput(String direction) throws LuaException {
        Direction dir = validateSide(direction);
        return owner.tileEntity.getInput(dir);
    }

    @LuaFunction(value = {"getAnalogueOutput", "getAnalogOutput"})
    public final int getAnalogOutput(String direction) throws LuaException {
        Direction dir = validateSide(direction);
        return owner.tileEntity.getOutput(dir);
    }

    @LuaFunction
    public final void setOutput(String direction, boolean power) throws LuaException {
        Direction dir = validateSide(direction);
        owner.tileEntity.setOutput(dir, power ? 15 : 0);
    }

    @LuaFunction(value = {"setAnalogueOutput", "setAnalogOutput"})
    public final void setAnalogOutput(String direction, int power) throws LuaException {
        Direction dir = validateSide(direction);
        if (power > 15 || power < 0) {
            throw new LuaException("redstone power exceeds the range [0,15]");
        }
        owner.tileEntity.setOutput(dir, power);
    }
}
