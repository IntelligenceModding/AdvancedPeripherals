package de.srendi.advancedperipherals.common.addons.create;

import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.BlockEntityIntegrationPeripheral;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public class MechanicalMixerIntegration extends BlockEntityIntegrationPeripheral<MechanicalMixerBlockEntity> {

    public MechanicalMixerIntegration(BlockEntity entity) {
        super(entity);
    }

    @NotNull
    @Override
    public String getType() {
        return "mechanical_mixer";
    }

    @LuaFunction(mainThread = true)
    public final boolean isRunning() {
        return blockEntity.running;
    }

    @LuaFunction(mainThread = true)
    public final boolean hasBasin() {
        if (blockEntity.getLevel() == null) return false;
        BlockEntity basinTE = blockEntity.getLevel().getBlockEntity(blockEntity.getBlockPos().below(2));
        return basinTE instanceof BasinBlockEntity;
    }
}
