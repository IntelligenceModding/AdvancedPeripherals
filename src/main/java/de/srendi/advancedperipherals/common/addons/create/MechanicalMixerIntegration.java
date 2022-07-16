package de.srendi.advancedperipherals.common.addons.create;

import com.simibubi.create.content.contraptions.components.mixer.MechanicalMixerTileEntity;
import com.simibubi.create.content.contraptions.processing.BasinTileEntity;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.BlockEntityIntegrationPeripheral;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class MechanicalMixerIntegration extends BlockEntityIntegrationPeripheral<MechanicalMixerTileEntity> {

    public MechanicalMixerIntegration(BlockEntity entity) {
        super(entity);
    }

    @NotNull
    @Override
    public String getType() {
        return "mechanicalMixer";
    }

    @LuaFunction(mainThread = true)
    public final boolean isRunning() {
        return blockEntity.running;
    }

    @LuaFunction(mainThread = true)
    public final boolean hasBasin() {
        if (blockEntity.getLevel() == null)
            return false;
        BlockEntity basinTE = blockEntity.getLevel().getBlockEntity(blockEntity.getBlockPos().below(2));
        return basinTE instanceof BasinTileEntity;
    }
}
