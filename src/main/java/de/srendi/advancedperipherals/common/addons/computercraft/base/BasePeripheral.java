package de.srendi.advancedperipherals.common.addons.computercraft.base;

import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
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
    protected ITurtleAccess turtle;

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

    public BasePeripheral(String type, ITurtleAccess turtle) {
        this.type = type;
        this.turtle = turtle;
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
        if (tileEntity != null)
            return tileEntity.getBlockPos();
        if (turtle != null)
            return turtle.getPosition();
        if (entity != null)
            return entity.blockPosition();
        return null;
    }

    protected World getWorld() {
        if (tileEntity != null)
            return tileEntity.getLevel();
        if (turtle != null)
            return turtle.getWorld();
        if (entity != null)
            return entity.getCommandSenderWorld();
        return null;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public void setTurtle(ITurtleAccess turtle) {
        this.turtle = turtle;
    }
}
