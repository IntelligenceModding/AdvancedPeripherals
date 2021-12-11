package de.srendi.advancedperipherals.common.addons.mekanism;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.TileEntityIntegrationPeripheral;
import mekanism.common.content.network.transmitter.UniversalCable;
import mekanism.common.tile.transmitter.TileEntityUniversalCable;
import net.minecraft.tileentity.TileEntity;
import org.jetbrains.annotations.NotNull;

public class UniversalCableIntegration extends TileEntityIntegrationPeripheral<TileEntityUniversalCable> {

    public UniversalCableIntegration(TileEntity entity) {
        super(entity);
    }

    @NotNull
    @Override
    public String getType() {
        return "universalCable";
    }

    @LuaFunction(mainThread = true)
    public long getEnergy() {
        return tileEntity.getTransmitter().buffer.getEnergy().getValue();
    }

    @LuaFunction(mainThread = true)
    public long getCapacity() {
        return tileEntity.getTransmitter().getCapacity();
    }

    @LuaFunction(mainThread = true)
    public String getTier() {
        return tileEntity.getTransmitter().tier.toString();
    }

}
