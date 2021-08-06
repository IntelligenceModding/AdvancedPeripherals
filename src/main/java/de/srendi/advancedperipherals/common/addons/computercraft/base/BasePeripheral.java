package de.srendi.advancedperipherals.common.addons.computercraft.base;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import de.srendi.advancedperipherals.common.util.LuaConverter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class BasePeripheral implements IBasePeripheral {

    protected final List<IComputerAccess> connectedComputers = new ArrayList<>();
    protected String type;
    protected IPeripheralOwner owner;

    public <T extends BlockEntity & IPeripheralTileEntity> BasePeripheral(String type, T tileEntity) {
        this.type = type;
        this.owner = new TileEntityPeripheralOwner<>(tileEntity);
    }

    public BasePeripheral(String type, ITurtleAccess turtle, TurtleSide side) {
        this.type = type;
        this.owner = new TurtlePeripheralOwner(turtle, side);
    }

    public BasePeripheral(String type, IPocketAccess access) {
        this.type = type;
        this.owner = new PocketPeripheralOwner(access);
    }

    public List<IComputerAccess> getConnectedComputers() {
        return connectedComputers;
    }

    @Nullable
    @Override
    public Object getTarget() {
        return owner;
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

    @LuaFunction
    public final String getName() {
        return owner.getCustomName();
    }

    protected BlockPos getPos() {
        return owner.getPos();
    }

    protected Level getWorld() {
        return owner.getWorld();
    }

    protected Direction validateSide(String direction) throws LuaException {
        String dir = direction.toUpperCase(Locale.ROOT);

        return LuaConverter.getDirection(owner.getFacing(), dir);
    }
}
