package de.srendi.advancedperipherals.common.addons.computercraft.integrations;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.BlockEntityIntegrationPeripheral;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public class BeaconIntegration extends BlockEntityIntegrationPeripheral<BeaconBlockEntity> {

    public BeaconIntegration(BlockEntity entity) {
        super(entity);
    }

    @Override
    public @NotNull String getType() {
        return "beacon";
    }

    @LuaFunction(mainThread = true)
    public final int getLevel() {
        return blockEntity.levels;
    }

    @LuaFunction(mainThread = true)
    public final String getPrimaryEffect() {
        return blockEntity.primaryPower == null ? "none" : blockEntity.primaryPower.getDescriptionId();
    }

    @LuaFunction(mainThread = true)
    public final String getSecondaryEffect() {
        return blockEntity.secondaryPower == null ? "none" : blockEntity.secondaryPower.getDescriptionId();
    }

}
