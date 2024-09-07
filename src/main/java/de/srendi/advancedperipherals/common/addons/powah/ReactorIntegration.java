package de.srendi.advancedperipherals.common.addons.powah;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.BlockEntityIntegrationPeripheral;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import owmii.powah.block.reactor.ReactorPartTile;

public class ReactorIntegration extends BlockEntityIntegrationPeripheral<ReactorPartTile> {
    protected ReactorIntegration(BlockEntity entity) {
        super(entity);
    }

    @Override
    @NotNull
    public String getType() {
        return "uraninite_reactor";
    }

    @LuaFunction(mainThread = true)
    public final String getName() {
        return "Uraninite Reactor";
    }

    @LuaFunction(mainThread = true)
    public final boolean isRunning() {
        if (blockEntity.core().isEmpty())
            return false;
        return blockEntity.core().get().isRunning();
    }

    @LuaFunction(mainThread = true)
    public final double getFuel() {
        if (blockEntity.core().isEmpty())
            return 0.0d;
        return blockEntity.core().get().fuel.perCent();
    }

    @LuaFunction(mainThread = true)
    public final double getCarbon() {
        if (blockEntity.core().isEmpty())
            return 0.0d;
        return blockEntity.core().get().carbon.perCent();
    }

    @LuaFunction(mainThread = true)
    public final double getRedstone() {
        if (blockEntity.core().isEmpty())
            return 0.0d;
        return blockEntity.core().get().redstone.perCent();
    }

    @LuaFunction(mainThread = true)
    public final double getEnergy() {
        if (blockEntity.core().isEmpty())
            return 0.0d;
        return blockEntity.core().get().getEnergy().getEnergyStored();
    }

    @LuaFunction(mainThread = true)
    public final double getMaxEnergy() {
        if (blockEntity.core().isEmpty())
            return 0.0d;
        return blockEntity.core().get().getEnergy().getMaxEnergyStored();
    }

    @LuaFunction(mainThread = true)
    public final double getTemperature() {
        if (blockEntity.core().isEmpty())
            return 0.0d;
        return blockEntity.core().get().temp.perCent();
    }

    @LuaFunction(mainThread = true)
    public final Object getInventoryUraninite() {
        if (blockEntity.core().isEmpty()) {
            return null;
        }
        ItemStack stack = blockEntity.core().get().getStack(1);
        if (stack.isEmpty()) {
            return null;
        }
        return LuaConverter.itemStackToObject(stack);
    }

    @LuaFunction(mainThread = true)
    public final Object getInventoryRedstone() {
        if (blockEntity.core().isEmpty()) {
            return null;
        }
        ItemStack stack = blockEntity.core().get().getStack(3);
        if (stack.isEmpty()) {
            return null;
        }
        return LuaConverter.itemStackToObject(stack);
    }

    @LuaFunction(mainThread = true)
    public final Object getInventoryCarbon() {
        if (blockEntity.core().isEmpty()) {
            return null;
        }
        ItemStack stack = blockEntity.core().get().getStack(2);
        if (stack.isEmpty()) {
            return null;
        }
        return LuaConverter.itemStackToObject(stack);
    }
}
