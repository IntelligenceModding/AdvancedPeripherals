package de.srendi.advancedperipherals.common.addons.powah;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.BlockEntityIntegrationPeripheral;
import net.minecraft.world.level.block.entity.BlockEntity;
import owmii.powah.block.reactor.ReactorTile;

public class ReactorIntegration extends BlockEntityIntegrationPeripheral<ReactorTile> {
    protected ReactorIntegration(BlockEntity entity) {
        super(entity);
    }

    @Override
    public String getType() {
        return "uraniniteReactor";
    }

    @LuaFunction(mainThread = true)
    public final String getName() {
        return "Uraninite Reactor";
    }

    @LuaFunction(mainThread = true)
    public final boolean isRunning() {
        return blockEntity.isRunning();
    }
}
