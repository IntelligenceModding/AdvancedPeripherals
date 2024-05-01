package de.srendi.advancedperipherals.common.addons.powah;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.BlockEntityIntegrationPeripheral;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import owmii.powah.block.ender.EnderCellTile;

public class EnderCellIntegration extends BlockEntityIntegrationPeripheral<EnderCellTile> {
    protected EnderCellIntegration(BlockEntity entity) {
        super(entity);
    }

    @NotNull
    @Override
    public String getType() {
        return "ender_cell";
    }

    @LuaFunction(mainThread = true)
    public final String getName() {
        return "Ender Cell";
    }

    @LuaFunction(mainThread = true)
    public final double getEnergy() {
        return blockEntity.getEnergy().getEnergyStored();
    }

    @LuaFunction(mainThread = true)
    public final double getMaxEnergy() {
        return blockEntity.getEnergy().getMaxEnergyStored();
    }

    @LuaFunction(mainThread = true)
    public final int getChannel() {
        // Lua, and generally slots in MC, seem to be 1 based, make the conversion here
        int channel = blockEntity.getChannel().get();
        return channel + 1;
    }

    @LuaFunction(mainThread = true)
    public final void setChannel(int channel) {
        // Lua, and generally slots in MC, seem to be 1 based, make the conversion here
        channel = channel - 1;
        blockEntity.getChannel().set(channel);
    }

    @LuaFunction(mainThread = true)
    public final int getMaxChannels() {
        return blockEntity.getMaxChannels();
    }
}
