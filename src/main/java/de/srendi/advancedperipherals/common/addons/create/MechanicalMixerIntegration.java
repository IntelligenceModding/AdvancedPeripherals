package de.srendi.advancedperipherals.common.addons.create;

import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.APGenericPeripheral;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

public class MechanicalMixerIntegration implements APGenericPeripheral {
    @NotNull
    @Override
    public String getPeripheralType() {
        return "mechanicalMixer";
    }

    @LuaFunction(mainThread = true)
    public final boolean isRunning(MechanicalMixerBlockEntity blockEntity) {
        return blockEntity.running;
    }

    @LuaFunction(mainThread = true)
    public final boolean hasBasin(MechanicalMixerBlockEntity blockEntity) {
        if (blockEntity.getLevel() == null) return false;
        BlockEntity basinTE = blockEntity.getLevel().getBlockEntity(blockEntity.getBlockPos().below(2));
        return basinTE instanceof BasinBlockEntity;
    }
}
