package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerDetectorPeripheral implements IPeripheral {

    private final List<IComputerAccess> connectedComputers = new ArrayList<>();
    private String type;
    private TileEntity tileEntity;

    public PlayerDetectorPeripheral(String type, TileEntity tileEntity) {
        this.type = type;
        this.tileEntity = tileEntity;
    }

    @NotNull
    @Override
    public String getType() {
        return type;
    }

    @Nullable
    @Override
    public Object getTarget() {
        return tileEntity;
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
        if (AdvancedPeripheralsConfig.enablePlayerDetector) {
            double rangeNegative = -(double) range;
            List<PlayerEntity> players = tileEntity.getWorld().getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB(tileEntity.getPos().add(rangeNegative, rangeNegative, rangeNegative), tileEntity.getPos().add((double) range + 1, (double) range + 1, (double) range + 1)));
            List<String> playersName = new ArrayList<>();
            for (PlayerEntity name : players) {
                playersName.add(name.getName().getString());
            }
            return playersName;
        }
        return Collections.emptyList();
    }

    @LuaFunction(mainThread = true)
    public final boolean isPlayersInRange(int range) throws LuaException {
        if (AdvancedPeripheralsConfig.enablePlayerDetector) {
            double rangeNegative = -(double) range;
            List<PlayerEntity> players = tileEntity.getWorld().getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB(tileEntity.getPos().add(rangeNegative, rangeNegative, rangeNegative), tileEntity.getPos().add((double) range + 1, (double) range + 1, (double) range + 1)));
            return !players.isEmpty();
        }
        return false;
    }

    @LuaFunction(mainThread = true)
    public final boolean isPlayerInRange(int range, String username) throws LuaException {
        if (AdvancedPeripheralsConfig.enablePlayerDetector) {
            double rangeNegative = -(double) range;
            List<PlayerEntity> players = tileEntity.getWorld().getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB(tileEntity.getPos().add(rangeNegative, rangeNegative, rangeNegative), tileEntity.getPos().add((double) range + 1, (double) range + 1, (double) range + 1)));
            List<String> playersName = new ArrayList<>();
            for (PlayerEntity name : players) {
                playersName.add(name.getName().getString());
            }
            return playersName.contains(username);
        }
        return false;
    }
}
