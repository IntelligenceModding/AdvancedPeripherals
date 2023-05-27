package de.srendi.advancedperipherals.common.addons.powah;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.BlockEntityIntegrationPeripheral;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import owmii.powah.block.furnator.FurnatorTile;

public class FurnatorIntegration extends BlockEntityIntegrationPeripheral<FurnatorTile> {
    protected FurnatorIntegration(BlockEntity entity) {
        super(entity);
    }

    @NotNull
    @Override
    public String getType() {
        return "furnator";
    }

    @LuaFunction(mainThread = true)
    public final String getName() {
        return "Furnator";
    }

    @LuaFunction(mainThread = true)
    public final boolean isBurning() {
        return blockEntity.isBurning();
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
    public final double getCarbon() {
        return blockEntity.getCarbon().perCent();
    }

    @LuaFunction(mainThread = true)
    public final ItemStack getInventory() {
        return blockEntity.getInventory().getStackInSlot(1);
    }
}
