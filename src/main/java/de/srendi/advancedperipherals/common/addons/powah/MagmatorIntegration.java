package de.srendi.advancedperipherals.common.addons.powah;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.BlockEntityIntegrationPeripheral;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import owmii.powah.block.magmator.MagmatorTile;

public class MagmatorIntegration extends BlockEntityIntegrationPeripheral<MagmatorTile> {
    protected MagmatorIntegration(BlockEntity entity) {
        super(entity);
    }

    @NotNull
    @Override
    public String getType() {
        return "magmator";
    }

    @LuaFunction(mainThread = true)
    public final String getName() {
        return "Magmator";
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
    public final boolean isBurning() {
        return blockEntity.isBurning();
    }

    @LuaFunction(mainThread = true)
    public final long getTankCapacity() {
        return blockEntity.getTank().getCapacity();
    }

    @LuaFunction(mainThread = true)
    public final long getFluidInTank() {
        return blockEntity.getTank().getFluidAmount();
    }
}
