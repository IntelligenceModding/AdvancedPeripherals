package de.srendi.advancedperipherals.common.addons.powah;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.BlockEntityIntegrationPeripheral;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import owmii.powah.block.energycell.EnergyCellTile;

public class EnergyCellIntegration extends BlockEntityIntegrationPeripheral<EnergyCellTile> {
    protected EnergyCellIntegration(BlockEntity entity) {
        super(entity);
    }

    @NotNull
    @Override
    public String getType() {
        return "energy_cell";
    }

    @LuaFunction(mainThread = true)
    public final String getName() {
        return "Energy Cell";
    }

    @LuaFunction(mainThread = true)
    public final double getEnergy() {
        return blockEntity.getEnergy().getEnergyStored();
    }

    @LuaFunction(mainThread = true)
    public final double getMaxEnergy() {
        return blockEntity.getEnergy().getMaxEnergyStored();
    }
}
