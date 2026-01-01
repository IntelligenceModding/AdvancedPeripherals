package de.srendi.advancedperipherals.common.addons.dimstorage;

import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.APGenericPeripheral;
import edivad.dimstorage.api.Frequency;
import edivad.dimstorage.blockentities.BlockEntityDimChest;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DimChestIntegration implements APGenericPeripheral {

    protected DimChestIntegration(BlockEntity entity) {
        super(entity);
    }

    @NotNull
    @Override
    public String getPeripheralType() {
        return "dim_chest";
    }

    @LuaFunction(mainThread = true)
    public final String getOwnerUUID(BlockEntityDimChest blockEntity) {
        UUID uuid = blockEntity.getFrequency().getOwnerUUID();
        if (uuid == null) {
            return null;
        }
        return uuid.toString();
    }

    @LuaFunction(mainThread = true)
    public final String getOwner(BlockEntityDimChest blockEntity) {
        return blockEntity.getFrequency().getOwner();
    }

    @LuaFunction(mainThread = true)
    public final boolean hasOwner(BlockEntityDimChest blockEntity) {
        return blockEntity.getFrequency().hasOwner();
    }

    @LuaFunction(mainThread = true)
    public final int getChannel(BlockEntityDimChest blockEntity) {
        return blockEntity.getFrequency().getChannel();
    }

    @LuaFunction(mainThread = true)
    public final boolean setChannel(BlockEntityDimChest blockEntity, int channel) {
        Frequency fre = blockEntity.getFrequency();
        if (fre.hasOwner()) {
            return false;
        }
        fre.setChannel(channel);
        blockEntity.setFrequency(fre);
        return true;
    }
}
