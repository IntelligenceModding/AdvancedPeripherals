package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.AdvancedPeripherals;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.LightType;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EnvironmentDetectorPeripheral implements IPeripheral {

    private String type;
    private TileEntity entity;

    List<IComputerAccess> connectedComputers = new ArrayList<>();

    public EnvironmentDetectorPeripheral(String type, TileEntity entity) {
        this.type = type;
        this.entity = entity;
    }

    @NotNull
    @Override
    public String getType() {
        return type;
    }

    @Override
    public void attach(@NotNull IComputerAccess computer) {
        connectedComputers.add(computer);
    }

    @Override
    public void detach(@NotNull IComputerAccess computer) {
        connectedComputers.remove(computer);
    }

    @Nullable
    @Override
    public Object getTarget() {
        return null;
    }

    @Override
    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return iPeripheral == this;
    }

    @LuaFunction(mainThread = true)
    public String getBiome() throws LuaException {
        String biomeName = entity.getWorld().getBiome(entity.getPos()).getRegistryName().toString();
        String[] biome = biomeName.split(":");
        return biome[1];
    }

    @LuaFunction(mainThread = true)
    public int getLightLevel() throws LuaException {
        return entity.getWorld().getLightFor(LightType.SKY, entity.getPos().add(0, 1, 0));
    }

    @LuaFunction(mainThread = true)
    public long getTime() throws LuaException {
        return entity.getWorld().getDayTime();
    }
}
