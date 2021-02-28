package de.srendi.advancedperipherals.common.addons.computercraft.peripheral;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import de.srendi.advancedperipherals.common.blocks.base.PeripheralTileEntity;
import de.srendi.advancedperipherals.common.configuration.AdvancedPeripheralsConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.ArrayList;
import java.util.List;

public class PlayerDetectorPeripheral extends BasePeripheral {

    public PlayerDetectorPeripheral(String type, PeripheralTileEntity<?> tileEntity) {
        super(type, tileEntity);
    }

    @Override
    public boolean isEnabled() {
        return AdvancedPeripheralsConfig.enablePlayerDetector;
    }

    @LuaFunction(mainThread = true)
    public final List<String> getPlayersInRange(int range) throws LuaException {
        double rangeNegative = -(double) range;
        List<PlayerEntity> players = tileEntity.getWorld().getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB(tileEntity.getPos().add(rangeNegative, rangeNegative, rangeNegative), tileEntity.getPos().add((double) range + 1, (double) range + 1, (double) range + 1)));
        List<String> playersName = new ArrayList<>();
        for (PlayerEntity name : players) {
            playersName.add(name.getName().getString());
        }
        return playersName;
    }

    @LuaFunction(mainThread = true)
    public final boolean isPlayersInRange(int range) throws LuaException {
        double rangeNegative = -(double) range;
        List<PlayerEntity> players = tileEntity.getWorld().getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB(tileEntity.getPos().add(rangeNegative, rangeNegative, rangeNegative), tileEntity.getPos().add((double) range + 1, (double) range + 1, (double) range + 1)));
        return !players.isEmpty();
    }

    @LuaFunction(mainThread = true)
    public final boolean isPlayerInRange(int range, String username) throws LuaException {
        double rangeNegative = -(double) range;
        List<PlayerEntity> players = tileEntity.getWorld().getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB(tileEntity.getPos().add(rangeNegative, rangeNegative, rangeNegative), tileEntity.getPos().add((double) range + 1, (double) range + 1, (double) range + 1)));
        List<String> playersName = new ArrayList<>();
        for (PlayerEntity name : players) {
            playersName.add(name.getName().getString());
        }
        return playersName.contains(username);
    }
}
