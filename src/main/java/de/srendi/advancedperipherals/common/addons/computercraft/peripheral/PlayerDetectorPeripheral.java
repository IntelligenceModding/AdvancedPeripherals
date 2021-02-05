package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PlayerDetectorPeripheral implements IPeripheral {

    private final List<IComputerAccess> connectedComputers = new ArrayList<>();
    private String type;
    private TileEntity entity;

    public PlayerDetectorPeripheral(String type, TileEntity entity) {
        this.type = type;
        this.entity = entity;
    }

    @NotNull
    @Override
    public String getType() {
        return type;
    }

    public List<IComputerAccess> getConnectedComputers() {
        return connectedComputers;
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

    @LuaFunction(mainThread = true)
    public final List<String> getPlayersInRange(int range) throws LuaException {
        double rangeNegative = -(double) range;
        List<PlayerEntity> players = entity.getWorld().getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB(entity.getPos().add(rangeNegative, rangeNegative, rangeNegative), entity.getPos().add((double) range + 1, (double) range + 1, (double) range + 1)));
        List<String> playersName = new ArrayList<>();
        for (PlayerEntity name : players) {
            playersName.add(name.getName().getString());
        }
        return playersName;
    }

    @LuaFunction(mainThread = true)
    public final boolean isPlayersInRange(int range) throws LuaException {
        double rangeNegative = -(double) range;
        List<PlayerEntity> players = entity.getWorld().getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB(entity.getPos().add(rangeNegative, rangeNegative, rangeNegative), entity.getPos().add((double) range + 1, (double) range + 1, (double) range + 1)));
        return !players.isEmpty();
    }

    @LuaFunction(mainThread = true)
    public final boolean isPlayerInRange(int range, String username) throws LuaException {
        double rangeNegative = -(double) range;
        List<PlayerEntity> players = entity.getWorld().getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB(entity.getPos().add(rangeNegative, rangeNegative, rangeNegative), entity.getPos().add((double) range + 1, (double) range + 1, (double) range + 1)));
        List<String> playersName = new ArrayList<>();
        for (PlayerEntity name : players) {
            playersName.add(name.getName().getString());
        }
        return playersName.contains(username);
    }
}
