package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class BasePeripheral implements IPeripheral {

    protected final List<IComputerAccess> connectedComputers = new ArrayList<>();
    public Entity entity;
    protected String type;
    protected TileEntity tileEntity;

    public BasePeripheral(String type, PeripheralTileEntity<?> tileEntity) {
        this.type = type;
        this.tileEntity = tileEntity;
    }

    public BasePeripheral(String type, TileEntity tileEntity) {
        this.type = type;
        this.tileEntity = tileEntity;
    }

    public BasePeripheral(String type, Entity entity) {
        this.type = type;
        this.entity = entity;
    }

    public void setTileEntity(TileEntity tileEntity) {
        this.tileEntity = tileEntity;
    }

    public List<IComputerAccess> getConnectedComputers() {
        return connectedComputers;
    }

    @Nullable
    @Override
    public Object getTarget() {
        return tileEntity;
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

    @Override
    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return iPeripheral == this;
    }

    public abstract boolean isEnabled();

    @LuaFunction
    public final String getName() {
        return tileEntity.getTileData().getString("CustomName");
    }

    protected BlockPos getPos() {
        return tileEntity != null ? tileEntity.getPos() : entity.getPosition();
    }

    protected World getWorld() {
        return tileEntity != null ? tileEntity.getWorld() : entity.getEntityWorld();
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}
