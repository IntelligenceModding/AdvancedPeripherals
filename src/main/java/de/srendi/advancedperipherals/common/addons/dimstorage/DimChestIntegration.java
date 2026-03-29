package de.srendi.advancedperipherals.common.addons.dimstorage;

import com.mojang.authlib.GameProfile;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.lib.peripherals.APGenericPeripheral;
import edivad.dimstorage.api.Frequency;
import edivad.dimstorage.blockentities.BlockEntityDimChest;
import org.jetbrains.annotations.NotNull;

public class DimChestIntegration implements APGenericPeripheral {
    @Override
    @NotNull
    public String getPeripheralType() {
        return "dim_chest";
    }

    @LuaFunction(mainThread = true)
    public final String getOwnerUUID(BlockEntityDimChest blockEntity) {
        GameProfile profile = blockEntity.getFrequency().gameProfile().orElse(null);
        if (profile == null) {
            return null;
        }
        return profile.getId().toString();
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
        return blockEntity.getFrequency().channel();
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
